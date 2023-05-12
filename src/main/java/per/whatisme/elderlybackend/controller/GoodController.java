package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Good;
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.GoodRepository;
import per.whatisme.elderlybackend.repository.MerchantRepository;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@RestController
@Api(tags = "商品相关")
@RequestMapping("/api")
public class GoodController {
    @Autowired
    GoodRepository goodRepository;
    @Autowired
    MerchantRepository merchantRepository;

    @PostMapping("/merchant/add-or-alter-good/")
    @Operation(summary = "修改或新增商品，没有id表示新增，merchant_name不用放，会自动生成")
    Mono<ResponseEntity<Good>> saveGood(
            @RequestParam String token,
            @RequestBody Good good) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"merchant".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        good.setMerchantId(user.getUserId());
        if (good.getId() == null || good.getId() == 0) good.setId(UidGenerator.generate());
        if (good.getDiscount() == null || good.getDiscount() > 1 || good.getDiscount() <= 0) good.setDiscount(1F);
        return merchantRepository
                .findById(good.getMerchantId())
                .flatMap(merchant -> {
                    good.setMerchantName(merchant.getUsername());
                    log.info(good.toString());
                    return goodRepository.save(good);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @DeleteMapping({"/merchant/delete-good/{id}", "/admin/delete-good/{id}"})
    @Operation(summary = "删除商品")
    Mono<ResponseEntity<Void>> deleteGoodById(
            @RequestParam String token,
            @PathVariable Long id) {
        User user = TokenHandler.getUser(token);
        if (user == null || !Set.of("merchant", "admin").contains(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        if ("admin".equals(user.getUserType()))
            return goodRepository
                    .deleteById(id)
                    .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.OK)));
        return goodRepository
                .deleteByIdAndMerchantId(id, user.getUserId())
                .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.OK)));
    }

    @GetMapping("/common/good/{id}")
    @Operation(summary = "根据id获取商品")
    Mono<Good> find(
            @PathVariable Long id) {
        return goodRepository
                .findById(id);
    }

    @GetMapping("/common/good/")
    @Operation(summary = "所有商品")
    Flux<Good> findAll() {
        return goodRepository
                .findAll();
    }

    @GetMapping("/common/good/by_merchant")
    @Operation(summary = "某家商店的商品，传id")
    Flux<Good> findAllByMerchant(
            @RequestParam(value = "merchant_id") Long merchantId) {
        return goodRepository
                .findGoodsByMerchantId(merchantId);
    }

    @GetMapping("/common/good/discount")
    @Operation(summary = "按折扣搜索，单开一个页面展示就行")
    Flux<Good> findAllByDiscount(
            @RequestParam(required = false) Float discount1,
            @RequestParam(required = false) Float discount2) {
        float left = 0.01F, right = 1F;
        if (discount1 != null) left = Math.max(left, discount1);
        if (discount2 != null) right = Math.min(right, discount2);
        return goodRepository
                .findGoodsByDiscountBetween(left, right);
    }

    @GetMapping("/common/good/search")
    @Operation(summary = "搜索")
    Flux<Good> find(
            @RequestParam String value) {
        return goodRepository
                .findGoodsByMerchantNameContainsOrNameContainsOrDescriptionContains(value, value, value);
    }
}
