package per.whatisme.elderlybackend.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "activity")
public class CommunityActivity extends BeanBase {
    @Id
    Long id;
    @ApiModelProperty("开始时间")
    Date begin;
    @ApiModelProperty("结束时间")
    Date end;
    @ApiModelProperty("标题")
    String title;
    @ApiModelProperty("活动地址")
    String address;
    @ApiModelProperty("如果可以，使用markdown")
    String description;
}
