package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Elderly;

public interface ElderlyRepository extends ReactiveMongoRepository<Elderly,Long> {
}
