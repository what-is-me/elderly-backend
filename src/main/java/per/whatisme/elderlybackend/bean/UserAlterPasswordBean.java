package per.whatisme.elderlybackend.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(defaultValue = "修改密码用的")
public class UserAlterPasswordBean {
    Long Id;
    String oldPassword;
    String newPassword;
}
