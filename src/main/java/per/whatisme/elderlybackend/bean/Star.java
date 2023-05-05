package per.whatisme.elderlybackend.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document(collection = "star")
public class Star {
    @Id
    Long elderlyId;
    Map<Long, StarBody> stared = new HashMap<>();

    public Star(Long elderlyId) {
        this.elderlyId = elderlyId;
    }
}
