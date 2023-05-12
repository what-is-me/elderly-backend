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
import per.whatisme.elderlybackend.bean.User;
import per.whatisme.elderlybackend.repository.ElderlyRepository;
import per.whatisme.elderlybackend.repository.GoodRepository;
import per.whatisme.elderlybackend.repository.PurchaseRepository;
import per.whatisme.elderlybackend.repository.StarRepository;
import per.whatisme.elderlybackend.utils.TokenHandler;
import per.whatisme.elderlybackend.utils.UUIDGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @GetMapping("/elderly/stared")
    @Operation(summary = "老人所收藏的商品")
    public Mono<ResponseEntity<Star>> findStars(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return starRepository
                .findById(user.getUserId())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/elderly/star")
    @Operation(summary = "老人收藏商品")
    public Mono<ResponseEntity<Star>> addStar(
            @RequestParam String token,
            @RequestBody StarBody s) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return starRepository.findById(user.getUserId())
                .defaultIfEmpty(new Star(user.getUserId()))
                .map(star -> {
                    star.getStared().put(s.getGoodId(), s);
                    return star;
                }).flatMap(star -> starRepository.save(star))
                .map(ResponseEntity::ok)
                .doOnError(throwable -> log.error(throwable.getMessage()))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/elderly/unstar")
    @Operation(summary = "老人取消收藏")
    public Mono<ResponseEntity<Star>> deleteStar(
            @RequestParam String token,
            @RequestParam Long goodId) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return starRepository.findById(user.getUserId())
                .map(star -> {
                    star.getStared().remove(goodId);
                    return star;
                }).flatMap(star -> starRepository.save(star))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/elderly/buy")
    @Operation(summary = "老人购买")
    public Mono<ResponseEntity<Purchase>> buy(
            @RequestParam String token,
            @RequestBody Purchase purchase) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return elderlyRepository.findById(user.getUserId()).flatMap(
                        e -> {
                            if (purchase.getAddress() == null || purchase.getAddress().equals(""))
                                purchase.setAddress(e.getAddress());
                            purchase.setElderlyId(user.getUserId());
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
    @ApiOperation(value = "老人评论", notes = "只要comment和订单id")
    public Mono<ResponseEntity<Purchase>> comment(
            @RequestParam String token,
            @RequestBody Purchase purchase) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        return purchaseRepository.findById(purchase.getId())
                .<Purchase>handle((p, sink) -> {
                    if (p.getElderlyId().equals(user.getUserId())) {
                        p.setComment(purchase.getComment());
                        sink.next(p);
                        return;
                    }
                    sink.error(new RuntimeException("UNAUTHORIZED"));
                }).flatMap(p -> purchaseRepository.save(p))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorResume(e -> {
                    if (e.getMessage().equals("UNAUTHORIZED"))
                        return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
                    return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                });
    }

    @GetMapping("/elderly/get-buy")
    @Operation(summary = "老人查看自己的订单")
    public Flux<Purchase> findElderlyPurchase(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"elderly".equals(user.getUserType()))
            return Flux.empty();
        return purchaseRepository
                .findPurchasesByElderlyId(user.getUserId());
    }

    @GetMapping("/merchant/get-buy")
    @Operation(summary = "商家查看自己收到的订单")
    public Flux<Purchase> findMerchantPurchase(
            @RequestParam String token) {
        User user = TokenHandler.getUser(token);
        if (user == null || !"merchant".equals(user.getUserType()))
            return Flux.empty();
        return purchaseRepository
                .findAllByMerchantId(user.getUserId());
    }

    @GetMapping("/common/good-comment")
    @Operation(summary = "某件商品的所有评论")
    public Mono<List<String>> comments(
            @RequestParam Long goodId) {
        return purchaseRepository
                .findAllByGoodId(goodId)
                .map(p -> p.getComment() == null || p.getComment().equals("") ? "未评论" : p.getComment())
                .collectList();
    }

    @GetMapping("/common/purchase")
    public Mono<Purchase> findById(
            @RequestParam String purchaseId) {
        return purchaseRepository
                .findById(purchaseId);
    }

    @PostMapping("/merchant/alter-purchase-statue")
    @Operation(summary = "改变某订单交付状态，1交付，0未交付，-1未正常交付(退货),statue直接放路径上")
    public Mono<ResponseEntity<Purchase>> alterPurchaseStatue(
            @RequestParam String token,
            @RequestParam String purchaseId,
            @RequestParam Integer statue) {
        statue = statue == null ? 0 : (statue > 0 ? 1 : (statue < 0 ? -1 : 0));
        User user = TokenHandler.getUser(token);
        if (user == null || !"merchant".equals(user.getUserType()))
            return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        Integer finalStatue = statue;
        return purchaseRepository
                .findById(purchaseId)
                .<Purchase>handle((p, sink) -> {
                    if (!p.getMerchantId().equals(user.getUserId())) {
                        sink.error(new RuntimeException("UNAUTHORIZED"));
                        return;
                    }
                    p.setStatue(finalStatue);
                    sink.next(p);
                }).flatMap(p -> purchaseRepository.save(p))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .doOnError(e -> log.error(e.getMessage()))
                .onErrorResume(e -> {
                    if (e.getMessage().equals("UNAUTHORIZED"))
                        return Mono.just(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
                    return Mono.just(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
                });
    }
}
