package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Good;
import per.whatisme.elderlybackend.repository.GoodRepository;
import per.whatisme.elderlybackend.repository.MerchantRepository;
import per.whatisme.elderlybackend.utils.UidGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    Mono<ResponseEntity<Good>> saveGood(@RequestBody Good good) {
        if (good.getId() == null || good.getId() == 0) good.setId(UidGenerator.generate());
        return merchantRepository.findById(good.getMerchantId()).flatMap(merchant -> {
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
    Mono<Void> deleteGoodById(@PathVariable Long id) {
        return goodRepository.deleteById(id);
    }

    @GetMapping("/common/good/{id}")
    @Operation(summary = "根据id获取商品")
    Mono<Good> find(@PathVariable Long id) {
        return goodRepository.findById(id);
    }

    @GetMapping("/common/good/")
    @Operation(summary = "所有商品")
    Flux<Good> findAll() {
        return goodRepository.findAll();
    }

    @GetMapping("/common/good/by_merchant")
    @Operation(summary = "某家商店的商品，传id或者名字的一部分")
    Flux<Good> findAllByMerchant(@RequestParam(value = "merchant_id") Long merchantId) {
        return goodRepository.findGoodsByMerchantId(merchantId);
    }

    @GetMapping("/common/good/discount")
    @Operation(summary = "按折扣搜索，单开一个页面展示就行")
    Flux<Good> findAllByDiscount(@RequestParam(required = false) Float discount1,
                                 @RequestParam(required = false) Float discount2) {
        float left = 0.01F, right = 1F;
        if (discount1 != null) left = Math.max(left, discount1);
        if (discount2 != null) right = Math.min(right, discount2);
        return goodRepository.findGoodsByDiscountBetween(left, right);
    }

    @GetMapping("/common/good/search")
    @Operation(summary = "搜索")
    Flux<Good> find(@RequestParam String value) {
        return goodRepository.findGoodsByMerchantNameContainsOrNameContainsOrDescriptionContains(value, value, value);
    }
}
