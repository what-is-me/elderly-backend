package per.whatisme.elderlybackend.controller.admin;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@Api(tags="用户的查询")
@RequestMapping("/api/admin/user")
public class AdminUserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> findUserById(@PathVariable("id") Long id) {
        return userRepository
                .findById(id)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/")
    public Flux<User> findAllUser() {
        return userRepository.findAll();
    }
}
