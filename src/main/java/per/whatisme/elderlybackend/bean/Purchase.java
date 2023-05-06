package per.whatisme.elderlybackend.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "purchase")
public class Purchase extends BeanBase {
    @Id
    String id;
    Long elderlyId;
    String address;
    Long merchantId;
    Long goodId;
    String goodName;
    String tag;
    Long num;
    Double price;
    String comment;
    @Schema(defaultValue = "是否交付,0未交付,1已交付,-1为正常交付")
    Integer statue;
}
