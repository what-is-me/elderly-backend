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
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.MerchantRepository;
import per.whatisme.elderlybackend.utils.PasswordEncoder;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@Api(tags = "商家的增删查改")
@RequestMapping("/api")
public class AdminMerchantController {
    @Autowired
    MerchantRepository merchantRepository;

    @GetMapping({"/common/merchant"})
    public Mono<ResponseEntity<List<Merchant>>> findAllMerchant(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return merchantRepository
                .findAll(Example.of(new Merchant()))
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/admin/merchant")
    @ApiOperation("不用传id")
    public Mono<ResponseEntity<Merchant>> addMerchant(
            @RequestParam String token,
            @RequestBody Merchant merchant) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        merchant.setUserId(UidGenerator.generate());
        merchant.setUserType("merchant");
        merchant.setPassword(new PasswordEncoder().encode(merchant.getPassword()));
        return merchantRepository.insert(merchant)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping("/admin/merchant")
    @ApiOperation(value = "更新商家信息", notes = "设置密码长度小于等于20，那么当传回来原始密码时，会对密码加密，否则就什么也不干。<br>user内容必须传全，否则该项内容会被清空。")
    public Mono<ResponseEntity<Merchant>> updateMerchant(
            @RequestParam String token,
            @RequestBody Merchant merchant) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        if (merchant.getPassword().length() <= 20) {
            merchant.setPassword(new PasswordEncoder().encode(merchant.getPassword()));
        }
        merchant.setUserType("merchant");
        return merchantRepository.save(merchant)
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/admin/merchant")
    public Mono<ResponseEntity<Void>> deleteMerchant(
            @RequestParam String token,
            @RequestParam("user_id") Long userId) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"admin".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return merchantRepository.deleteById(userId)
                .then(Mono.fromCallable(() -> new ResponseEntity<Void>(HttpStatus.OK)))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
