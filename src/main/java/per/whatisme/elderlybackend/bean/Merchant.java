package per.whatisme.elderlybackend.bean;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user")
public class Merchant extends User {
    @ApiModelProperty("是否通过审核")
    boolean verified;
    @ApiModelProperty("商家广告图片链接")
    String advertisementUrl;
    @ApiModelProperty("资质文件的链接")
    String verifyDocumentFilesUrl;
    public Merchant(){
        super("merchant");
    }
}
