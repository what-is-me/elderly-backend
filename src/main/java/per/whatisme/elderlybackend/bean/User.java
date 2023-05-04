package per.whatisme.elderlybackend.bean;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document(collection = "user")
public class User extends BeanBase {
    @Id
    private Long userId;
    private String password;
    @Indexed(unique=true)
    private String username;
    private String userType;
    public User() {
    }
    public User(String userType) {
        this.userType = userType;
    }
}
