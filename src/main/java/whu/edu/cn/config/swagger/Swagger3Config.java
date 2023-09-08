package whu.edu.cn.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger3 configuration.
 */
@Configuration
@EnableSwagger2
public class Swagger3Config {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
//        Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select() // 选择那些路径和api会生成document
                .apis(RequestHandlerSelectors.basePackage("whu.edu.cn.controller"))// 对指定包api进行监控
                //不显示错误的接口地址
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("GeoCube Web API Documentation")
                .contact(new Contact("Kaixuan Wang(WHU)", "", "wangkaixuan@whu.edu.cn"))
                .description("This is a GeoCube API documentation")
                .termsOfServiceUrl("No terms of service")
                /*.license("The Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")*/
                .version("v1.0")
                .build();
    }
}
