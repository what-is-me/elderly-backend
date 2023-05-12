package per.whatisme.elderlybackend.utils;

import org.springframework.data.util.Pair;
import per.whatisme.elderlybackend.bean.Token;
import per.whatisme.elderlybackend.bean.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenHandler {
    private static final Map<String, Pair<User, Date>> tokenUser = new HashMap<>();
    private static final Map<Long, String> userToken = new HashMap<>();

    public static Token saveUser(User user) {
        if (user == null) return null;
        String oldToken = userToken.get(user.getUserId());
        if (oldToken != null) {
            tokenUser.remove(oldToken);
        }
        Token token = new Token(UUIDGenerator.generate(), new Date(new Date().getTime() + 1000L * 60 * 60 * 24));
        tokenUser.put(token.getToken(), Pair.of(user, token.getExpire()));
        userToken.put(user.getUserId(), token.getToken());
        return token;
    }

    public static User getUser(String token) {
        Pair<User, Date> pUser = tokenUser.get(token);
        if (pUser == null) return null;
        if (pUser.getSecond().compareTo(new Date()) < 0) {
            tokenUser.remove(token);
            return null;
        }
        return pUser.getFirst();
    }

    public static Token updateToken(String oldToken) {
        User user = getUser(oldToken);
        if (user == null) return null;
        return saveUser(user);
    }
}
