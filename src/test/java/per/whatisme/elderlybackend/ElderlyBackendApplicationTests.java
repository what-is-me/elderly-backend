package per.whatisme.elderlybackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import per.whatisme.elderlybackend.bean.Elderly;
import per.whatisme.elderlybackend.repository.ElderlyRepository;

import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SpringBootTest
class ElderlyBackendApplicationTests {
    @Autowired
    ElderlyRepository elderlyRepository;


    @Test
    void findElderly() throws InterruptedException {
        elderlyRepository.findAll().subscribe(System.out::println);
    }
}
