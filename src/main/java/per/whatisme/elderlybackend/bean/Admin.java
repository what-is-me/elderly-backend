package per.whatisme.elderlybackend.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user")
public class Admin extends User {
    public Admin() {
        super("admin");
    }
}
