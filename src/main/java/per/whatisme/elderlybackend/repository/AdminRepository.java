package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Admin;

public interface AdminRepository extends ReactiveMongoRepository<Admin,Long> {
}
