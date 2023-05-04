package per.whatisme.elderlybackend.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import per.whatisme.elderlybackend.bean.Msg;
import reactor.core.publisher.Flux;

import java.util.Date;

public interface MsgRepository extends ReactiveMongoRepository<Msg, Long> {
    Flux<Msg>findAllByTimeAfter(Date time);
}
