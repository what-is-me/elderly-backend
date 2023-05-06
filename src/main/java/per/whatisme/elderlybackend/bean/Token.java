package per.whatisme.elderlybackend.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Token {
    String token;
    Date expire;
}
