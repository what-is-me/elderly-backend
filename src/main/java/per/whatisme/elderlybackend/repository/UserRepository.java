package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.User;

public interface UserRepository extends ReactiveMongoRepository<User,Long> {
}
