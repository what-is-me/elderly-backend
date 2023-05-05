package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, Long> {
    Mono<User> findUserByUsername(String username);
}
