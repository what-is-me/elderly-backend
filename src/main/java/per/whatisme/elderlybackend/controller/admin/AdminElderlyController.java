package per.whatisme.elderlybackend.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Elderly;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.ElderlyRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "老人的增删查改")
@RequestMapping("/api/admin")
public class AdminElderlyController {
    @Autowired
    ElderlyRepository elderlyRepository;

    @GetMapping("/elderly")
    public Mono<ResponseEntity<List<Elderly>>> findAllElderly(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return elderlyRepository
                .findAll(Example.of(new Elderly()))
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/elderly")
    @ApiOperation("不用传id")
    public Mono<ResponseEntity<Elderly>> addElderly(
            @RequestParam String token,
            @RequestBody Elderly elderly) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        elderly.setUserId(UidGenerator.generate());
        elderly.setUserType("elderly");
        elderly.setPassword(new PasswordEncoder().encode(elderly.getPassword()));
        return elderlyRepository.insert(elderly)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/elderly")
    @ApiOperation(value = "更新老年用户信息", notes = "设置密码长度小于等于20，那么当传回来原始密码时，会对密码加密，否则就什么也不干。<br>user内容必须传全，否则该项内容会被清空。")
    public Mono<ResponseEntity<Elderly>> updateElderly(
            @RequestParam String token,
            @RequestBody Elderly elderly) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        if (elderly.getPassword().length() <= 20) {
            elderly.setPassword(new PasswordEncoder().encode(elderly.getPassword()));
        }
        elderly.setUserType("elderly");
        return elderlyRepository.save(elderly)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/elderly")
    public Mono<ResponseEntity<Void>> deleteElderly(
            @RequestParam String token,
            @RequestParam("user_id") Long userId) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return elderlyRepository.deleteById(userId)
                .then(Mono.fromCallable(() -> new ResponseEntity<Void>(HttpStatus.OK)))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
