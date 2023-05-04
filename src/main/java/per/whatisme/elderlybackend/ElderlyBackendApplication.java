package per.whatisme.elderlybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class ElderlyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElderlyBackendApplication.class, args);
    }

}
