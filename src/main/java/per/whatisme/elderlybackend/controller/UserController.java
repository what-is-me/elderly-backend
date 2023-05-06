package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.*;
import per.whatisme.elderlybackend.repository.ElderlyRepository;
import per.whatisme.elderlybackend.repository.MerchantRepository;
import per.whatisme.elderlybackend.repository.UserRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Api(tags = "登录、注册、修改个人信息等")
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
    public Mono<ResponseEntity<Map<String, Object>>> login(
            @RequestBody User user) {
        user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        return userRepository.findUserByUsername(user.getUsername()).mapNotNull(u -> {
                    if (u.getPassword().equals(user.getPassword())) return u;
                    return null;
                }).map(u -> Map.of("user", u, "token", TokenHandler.saveUser(u))
                ).map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "注册：只对商家开放")
    @PostMapping("/common/signup")
    public Mono<Merchant> signup(
            @RequestBody Merchant merchant) {
        merchant.setVerified(false);
        merchant.setUserId(UidGenerator.generate());
        merchant.setPassword(new PasswordEncoder().encode(merchant.getPassword()));
        return merchantRepository
                .insert(merchant);
    }

    @Operation(summary = "老人：修改收货地址，只传地址")
    @PostMapping("/elderly/alter-address")
    public Mono<ResponseEntity<Elderly>> alterAddress(
            @RequestParam String token,
            @RequestBody Elderly data) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return elderlyRepository
                .findById(user.getUserId())
                .flatMap(u -> {
                    u.setAddress(data.getAddress());
                    return elderlyRepository.save(u);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "修改密码，需要旧密码")
    @PostMapping({"/admin/repassword", "/elderly/repassword", "/merchant/repassword"})
    public Mono<ResponseEntity<User>> rePassword(
            @RequestBody UserAlterPasswordBean user) {
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

    @GetMapping("/common/profile")
    @Operation(summary = "个人信息")
    public Mono<ResponseEntity<User>> findUserById(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null)
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return userRepository
                .findById(user.getUserId())
                .map(e -> new ResponseEntity<>(e, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/common/fresh-token")
    @Operation(summary = "刷新token，token过期时间一天，基本用不上")
    public Mono<ResponseEntity<Token>> freshToken(
            @RequestParam String token) {
        Token tk = TokenHandler.updateToken(token);
        if (tk == null)
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return Mono.just(ResponseEntity.ok(tk));
    }
}
