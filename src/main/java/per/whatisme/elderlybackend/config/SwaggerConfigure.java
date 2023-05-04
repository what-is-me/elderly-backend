package per.whatisme.elderlybackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigure {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("per.whatisme.elderlybackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket docketAdmin() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("per.whatisme.elderlybackend.controller"))
                .paths(PathSelectors.ant("/api/admin/**")
                        .or(PathSelectors.ant("/api/common/**")))
                .build()
                .groupName("管理员接口");
    }
    @Bean
    public Docket docketElderly() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("per.whatisme.elderlybackend.controller"))
                .paths(PathSelectors.ant("/api/elderly/**")
                        .or(PathSelectors.ant("/api/common/**")))
                .build()
                .groupName("老年用户接口");
    }
    @Bean
    public Docket docketMerchant() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("per.whatisme.elderlybackend.controller"))
                .paths(PathSelectors.ant("/api/merchant/**")
                        .or(PathSelectors.ant("/api/common/**")))
                .build()
                .groupName("商家接口");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("面向老年人的智能居家第三方平台")
                .description("后端api接口")
                .contact(new Contact("whatisme", "https://github.com/what-is-me", "whatisme@outlook.jp"))
                .version("v0.0.1")
                .license("GNU GPL3.0")
                .licenseUrl("https://www.gnu.org/licenses/gpl-3.0.md")
                .build();
    }
}
