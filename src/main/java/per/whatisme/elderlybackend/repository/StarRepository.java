package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Star;

public interface StarRepository extends ReactiveMongoRepository<Star, Long> {
}
