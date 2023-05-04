package per.whatisme.elderlybackend.utils;

import java.util.Date;

public class UidGenerator {
    static long id = 0;
    static long time = 0;

    public static synchronized Long generate() {
        long cur = new Date().getTime() / 1000;
        if (time == cur) {
            id++;
        } else {
            id = 0;
            time = cur;
        }
        return time * 10000 + id;
    }
}
