package per.whatisme.elderlybackend.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.whatisme.elderlybackend.bean.Star;
import per.whatisme.elderlybackend.bean.StarBody;
import per.whatisme.elderlybackend.repository.StarRepository;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@Api(tags = "购物相关")
@RequestMapping("/api")
public class PurchaseController {
    @Autowired
    StarRepository starRepository;

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
}
