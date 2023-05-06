package per.whatisme.elderlybackend.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Admin;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.AdminRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@Api(tags = "管理人员的增删查改")
public class AdminAdminController {
    @Autowired
    AdminRepository adminRepository;

    @GetMapping("/admin")
    public Mono<ResponseEntity<List<Admin>>> findAllAdmin(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return adminRepository
                .findAll(Example.of(new Admin()))
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/admin")
    @ApiOperation("不用传id")
    public Mono<ResponseEntity<Admin>> addAdmin(
            @RequestParam String token,
            @RequestBody Admin admin) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        admin.setUserId(UidGenerator.generate());
        admin.setPassword(new PasswordEncoder().encode(admin.getPassword()));
        admin.setUserType("admin");
        return adminRepository.insert(admin)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/admin")
    @ApiOperation(value = "更新管理员用户信息", notes = "设置密码长度小于等于20，那么当传回来原始密码时，会对密码加密，否则就什么也不干。<br>user内容必须传全，否则该项内容会被清空。")
    public Mono<ResponseEntity<Admin>> updateAdmin(
            @RequestParam String token,
            @RequestBody Admin admin) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        if (admin.getPassword().length() <= 20) {
            admin.setPassword(new PasswordEncoder().encode(admin.getPassword()));
        }
        admin.setUserType("admin");
        return adminRepository.save(admin)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/admin")
    public Mono<ResponseEntity<Void>> deleteAdmin(
            @RequestParam String token,
            @RequestParam("user_id") Long userId) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return adminRepository.deleteById(userId)
                .then(Mono.fromCallable(() -> new ResponseEntity<Void>(HttpStatus.OK)))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
