package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Good;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GoodRepository extends ReactiveMongoRepository<Good, Long> {

    Flux<Good> findGoodsByDiscountBetween(Float discount, Float discount2);

    Flux<Good> findGoodsByMerchantId(Long merchantId);

    Flux<Good> findGoodsByMerchantNameContainsOrNameContainsOrDescriptionContains(String merchantName, String name, String description);

    Mono<Void> deleteByIdAndMerchantId(Long id, Long merchantId);
}
