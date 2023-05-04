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
import per.whatisme.elderlybackend.repository.AdminRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/admin/admin")
@Api(tags = "管理人员的增删查改")
public class AdminAdminController {
    @Autowired
    AdminRepository adminRepository;

    @GetMapping("/")
    public Flux<Admin> findAllAdmin() {
        return adminRepository.findAll(Example.of(new Admin()));
    }

    @PostMapping("/")
    @ApiOperation("不用传id")
    public Mono<ResponseEntity<Admin>> addAdmin(@RequestBody Admin user) {
        user.setUserId(UidGenerator.generate());
        user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        user.setUserType("admin");
        return adminRepository.insert(user)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/")
    @ApiOperation(value = "更新管理员用户信息", notes = "设置密码长度小于等于20，那么当传回来原始密码时，会对密码加密，否则就什么也不干。<br>user内容必须传全，否则该项内容会被清空。")
    public Mono<ResponseEntity<Admin>> updateAdmin(@RequestBody Admin user) {
        if (user.getPassword().length() <= 20) {
            user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        }
        user.setUserType("admin");
        return adminRepository.save(user)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/")
    public Mono<ResponseEntity<Void>> deleteAdmin(@RequestParam("user_id") Long userId) {
        return adminRepository.deleteById(userId)
                .then(Mono.fromCallable(() -> new ResponseEntity<Void>(HttpStatus.OK)))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
