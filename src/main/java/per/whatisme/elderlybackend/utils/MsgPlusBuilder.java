package per.whatisme.elderlybackend.utils;

import per.whatisme.elderlybackend.bean.Msg;
import per.whatisme.elderlybackend.bean.MsgPlus;
import reactor.core.publisher.Flux;

import java.util.*;

public class MsgPlusBuilder {
    public static Flux<MsgPlus> build(List<Msg> msgs) {
        Map<Long, MsgPlus> map = new HashMap<>();
        List<MsgPlus> ret = new ArrayList<>();
        msgs
                .stream()
                .sorted(Comparator.comparingLong(msg -> msg.getTime().getTime()))
                .forEachOrdered(msg -> {
                    try {
                        MsgPlus msgp = new MsgPlus(msg);
                        map.put(msgp.getId(), msgp);
                        if (msgp.getFa() == null || msgp.getFa() == 0) {
                            ret.add(msgp);
                        }
                        if (map.containsKey(msgp.getFa())) {
                            map.get(msgp.getFa()).pushBack(msgp);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
        System.out.println(map);
        return Flux.fromIterable(ret);
    }
}
