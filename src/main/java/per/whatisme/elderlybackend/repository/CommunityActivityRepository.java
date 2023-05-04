package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.CommunityActivity;

public interface CommunityActivityRepository extends ReactiveMongoRepository<CommunityActivity,Long> {
}
