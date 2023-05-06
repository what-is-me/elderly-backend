package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Purchase;
import per.whatisme.elderlybackend.bean.Star;
import per.whatisme.elderlybackend.bean.StarBody;
import per.whatisme.elderlybackend.repository.ElderlyRepository;
import per.whatisme.elderlybackend.repository.GoodRepository;
import per.whatisme.elderlybackend.repository.PurchaseRepository;
import per.whatisme.elderlybackend.repository.StarRepository;
import per.whatisme.elderlybackend.utils.UUIDGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@Api(tags = "购物相关")
@RequestMapping("/api")
public class PurchaseController {
    @Autowired
    ElderlyRepository elderlyRepository;
    @Autowired
    StarRepository starRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    GoodRepository goodRepository;

    @GetMapping("/elderly/stared/{id}")
    @Operation(summary = "老人所收藏的商品")
    public Mono<Star> findStars(@PathVariable Long id) {
        return starRepository.findById(id);
    }

    @PostMapping("/elderly/star/{id}")
    @Operation(summary = "老人收藏商品")
    public Mono<Star> addStar(@PathVariable Long id, @RequestBody StarBody s) {
        return starRepository.findById(id)
                .defaultIfEmpty(new Star(id))
                .map(star -> {
                    star.getStared().put(s.getGoodId(), s);
                    return star;
                }).flatMap(star -> starRepository.save(star))
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }

    @DeleteMapping("/elderly/unstar/{id}")
    @Operation(summary = "老人取消收藏")
    public Mono<Star> deleteStar(@PathVariable Long id, @RequestParam Long goodId) {
        return starRepository.findById(id)
                .map(star -> {
                    star.getStared().remove(goodId);
                    return star;
                }).flatMap(star -> starRepository.save(star));
    }

    @PostMapping("/elderly/buy")
    @Operation(summary = "老人购买")
    public Mono<ResponseEntity<Purchase>> buy(@RequestBody Purchase purchase) {
        return elderlyRepository.findById(purchase.getElderlyId()).flatMap(
                        e -> {
                            if (purchase.getAddress() == null || purchase.getAddress().equals(""))
                                purchase.setAddress(e.getAddress());
                            return goodRepository.findById(purchase.getGoodId());
                        }
                )
                .map(good -> {
                    purchase.setComment("");
                    purchase.setId(UUIDGenerator.generate());
                    purchase.setStatue(0);
                    purchase.setMerchantId(good.getMerchantId());
                    purchase.setGoodName(good.getName());
                    purchase.setPrice(good.getTagPrice().get(purchase.getTag()) * good.getDiscount() * purchase.getNum());
                    return purchase;
                })
                .flatMap(p -> purchaseRepository.save(p))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping("/elderly/comment")
    @ApiOperation(value = "老人评论", notes = "只要传id和comment")
    public Mono<ResponseEntity<Purchase>> comment(@RequestBody Purchase purchase) {
        return purchaseRepository.findById(purchase.getId()).map(p -> {
                    p.setComment(purchase.getComment());
                    return p;
                }).flatMap(p -> purchaseRepository.save(p))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/elderly/get-buy/{id}")
    @Operation(summary = "老人查看自己的订单")
    public Flux<Purchase> findElderlyPurchase(@PathVariable Long id) {
        return purchaseRepository.findAllByElderlyId(id);
    }

    @GetMapping("/merchant/get-buy/{id}")
    @Operation(summary = "商家查看自己收到的订单")
    public Flux<Purchase> findMerchantPurchase(@PathVariable Long id) {
        return purchaseRepository.findAllByMerchantId(id);
    }

    @GetMapping("/common/good-comment/{goodId}")
    @Operation(summary = "某件商品的所有评论")
    public Flux<String> comments(@PathVariable Long goodId) {
        return purchaseRepository.findAllByGoodId(goodId)
                .map(p -> p.getComment() == null || p.getComment().equals("") ? "未评论" : p.getComment());
    }

    @GetMapping("/common/purchase/{purchaseId}")
    public Mono<Purchase> findById(@PathVariable String purchaseId) {
        return purchaseRepository.findById(purchaseId);
    }

    @PostMapping("/merchant/alter-purchase-statue/{purchaseId}")
    @Operation(summary = "改变某订单交付状态，1交付，0未交付，-1未正常交付(退货),statue直接放路径上")
    public Mono<ResponseEntity<Purchase>> alterPurchaseStatue(@PathVariable String purchaseId, @RequestParam Integer statue) {
        return purchaseRepository.findById(purchaseId).map(p -> {
                    p.setStatue(statue);
                    return p;
                }).flatMap(p -> purchaseRepository.save(p))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
