package per.whatisme.elderlybackend.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Merchant;
import per.whatisme.elderlybackend.repository.MerchantRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@Api(tags = "商家的增删查改")
@RequestMapping("/api")
public class AdminMerchantController {
    @Autowired
    MerchantRepository merchantRepository;

    @GetMapping({ "/common/merchant/"})
    public Flux<Merchant> findAllMerchant() {
        return merchantRepository.findAll(Example.of(new Merchant()));
    }

    @PostMapping("/admin/merchant/")
    @ApiOperation("不用传id")
    public Mono<ResponseEntity<Merchant>> addMerchant(@RequestBody Merchant user) {
        user.setUserId(UidGenerator.generate());
        user.setUserType("merchant");
        user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        return merchantRepository.insert(user)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/admin/merchant/")
    @ApiOperation(value = "更新商家信息", notes = "设置密码长度小于等于20，那么当传回来原始密码时，会对密码加密，否则就什么也不干。<br>user内容必须传全，否则该项内容会被清空。")
    public Mono<ResponseEntity<Merchant>> updateMerchant(@RequestBody Merchant user) {
        if (user.getPassword().length() <= 20) {
            user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        }
        user.setUserType("merchant");
        return merchantRepository.save(user)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/admin/merchant/")
    public Mono<ResponseEntity<Void>> deleteMerchant(@RequestParam("user_id") Long userId) {
        return merchantRepository.deleteById(userId)
                .then(Mono.fromCallable(() -> new ResponseEntity<Void>(HttpStatus.OK)))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
