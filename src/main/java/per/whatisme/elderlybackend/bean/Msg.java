package per.whatisme.elderlybackend.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "message")
public class Msg extends BeanBase{
    @Id
    Long id;
    String username;
    String text;
    Date time;
    Boolean isComplain;
    @ApiModelProperty("本留言回复的留言id,0或null表示没有")
    Long fa;
}
