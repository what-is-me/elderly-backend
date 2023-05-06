package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Msg;
import per.whatisme.elderlybackend.bean.MsgPlus;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.MsgRepository;
import per.whatisme.elderlybackend.utils.MsgPlusBuilder;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@RestController
@Api(tags = "留言相关")
@RequestMapping("/api/common/msg")
public class MsgController {
    @Autowired
    MsgRepository msgRepository;

    @PostMapping("/")
    @Operation(summary = "新增留言")
    public Mono<ResponseEntity<Msg>> addMsg(
            @RequestParam String token,
            @RequestBody Msg msg) {
        User user = TokenHandler.getUser(token);
        if (user == null)
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        msg.setUsername(user.getUsername());
        msg.setTime(new Date());
        msg.setId(UidGenerator.generate());
        return msgRepository.insert(msg)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/")
    public Flux<MsgPlus> findAll(
            @RequestParam(required = false) Date after) {
        Flux<Msg> flux;
        if (after == null) flux = msgRepository.findAll();
        else flux = msgRepository
                .findAllByTimeAfter(after);
        return flux
                .collectList()
                .flatMapMany(MsgPlusBuilder::build);
    }
}
