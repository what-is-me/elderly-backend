package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Elderly;
import per.whatisme.elderlybackend.bean.Merchant;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.bean.UserAlterPasswordBean;
import per.whatisme.elderlybackend.repository.ElderlyRepository;
import per.whatisme.elderlybackend.repository.MerchantRepository;
import per.whatisme.elderlybackend.repository.UserRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "各种用户的一些操作")
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    ElderlyRepository elderlyRepository;

    @Operation(summary = "登录：只要传username和password就行")
    @PostMapping("/common/login")
    public Mono<User> login(@RequestBody User user) {
        user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        return userRepository.findOne(Example.of(user));
    }

    @Operation(summary = "注册：只对商家开放")
    @PostMapping("/merchant/signup")
    public Mono<Merchant> signup(@RequestBody Merchant merchant) {
        merchant.setVerified(false);
        merchant.setUserId(UidGenerator.generate());
        merchant.setPassword(new PasswordEncoder().encode(merchant.getPassword()));
        return merchantRepository.insert(merchant);
    }

    @Operation(summary = "老人：修改收货地址")
    @PostMapping("/elderly/alter-address")
    public Mono<ResponseEntity<Elderly>> alterAddress(@RequestBody Elderly user) {
        return elderlyRepository
                .findById(user.getUserId())
                .flatMap(u -> {
                    u.setAddress(user.getAddress());
                    return elderlyRepository.save(u);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "修改密码，需要旧密码")
    @PostMapping("/common/repassword")
    public Mono<ResponseEntity<User>> rePassword(@RequestBody UserAlterPasswordBean user) {
        user.setOldPassword(new PasswordEncoder().encode(user.getOldPassword()));
        user.setNewPassword(new PasswordEncoder().encode(user.getNewPassword()));
        return userRepository.findById(user.getId())
                .<User>handle((u, sink) -> {
                    if (u.getPassword().equals(user.getOldPassword())) u.setPassword(user.getNewPassword());
                    else {
                        sink.error(new RuntimeException("Wrong Password"));
                        return;
                    }
                    sink.next(u);
                })
                .flatMap(u -> userRepository.save(u))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorReturn(e -> e.getMessage().equals("Wrong Password"),
                        new ResponseEntity<>(HttpStatus.UNAUTHORIZED))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/common/user/{id}")
    public Mono<ResponseEntity<User>> findUserById(@PathVariable("id") Long id) {
        return userRepository
                .findById(id)
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
