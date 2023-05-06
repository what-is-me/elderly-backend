package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Purchase;
import reactor.core.publisher.Flux;

public interface PurchaseRepository extends ReactiveMongoRepository<Purchase, String> {
    Flux<Purchase> findAllByElderlyId(Long id);

    Flux<Purchase> findAllByMerchantId(Long id);

    Flux<Purchase> findAllByGoodId(Long id);
}
