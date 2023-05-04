package per.whatisme.elderlybackend.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MsgPlus extends Msg {
    private List<MsgPlus> replies = new ArrayList<>();

    public MsgPlus(Msg msg) throws IllegalAccessException {
        this.id=msg.id;
        this.username=msg.username;
        this.text= msg.text;
        this.time=msg.time;
        this.isComplain=msg.isComplain;
        this.fa=msg.fa;
    }

    public void pushBack(MsgPlus msg) {
        replies.add(msg);
    }
}
