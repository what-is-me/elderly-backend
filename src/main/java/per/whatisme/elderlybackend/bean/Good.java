package per.whatisme.elderlybackend.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document(collection = "good")
public class Good extends BeanBase {
    Long merchantId;
    String merchantName;
    @Id
    Long id;
    String name;
    String description;
    String type;
    Float discount;
    String pictureUrl;
    @Schema(defaultValue = "map<标签,价格>")
    Map<String, Double> tagPrice = new HashMap<>();
}
