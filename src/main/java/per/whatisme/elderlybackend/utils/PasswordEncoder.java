package per.whatisme.elderlybackend.utils;

import org.springframework.util.DigestUtils;

public class PasswordEncoder {
public String encode(String pwd){return DigestUtils.md5DigestAsHex((pwd).getBytes());}
}
