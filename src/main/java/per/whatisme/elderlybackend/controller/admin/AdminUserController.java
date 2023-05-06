package per.whatisme.elderlybackend.controller.admin;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.UserRepository;
import per.whatisme.elderlybackend.utils.TokenHandler;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "用户的查询")
@RequestMapping("/api/admin/user")
public class AdminUserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findUserById(
            @RequestParam String token,
            @PathVariable("id") Long id) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return userRepository
                .findById(id)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/")
    public Mono<ResponseEntity<List<User>>> findAllUser(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return userRepository.findAll()
                .collectList()
                .map(ResponseEntity::ok);
    }
}
