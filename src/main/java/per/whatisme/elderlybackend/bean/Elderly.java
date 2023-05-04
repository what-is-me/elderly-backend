package per.whatisme.elderlybackend.bean;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user")
public class Elderly extends User {
    private String address;
    public Elderly(){
        super("elderly");
    }
}
