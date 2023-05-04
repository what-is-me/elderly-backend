package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Merchant;

public interface MerchantRepository extends ReactiveMongoRepository<Merchant,Long> {
}
