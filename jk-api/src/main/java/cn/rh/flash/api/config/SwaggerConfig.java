package cn.rh.flash.api.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.models.auth.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

/**
 * swagger在线文档配置<br>
 * 项目启动后可通过地址：http://host:ip/swagger-ui.html 查看在线文档
 */

@EnableOpenApi
@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {


    @Bean
    public Docket frontApi() {
        ApiKey apiKey = new ApiKey("Authorization", "tk", In.HEADER.toValue() );
        List<SecurityScheme> headers =  Collections.singletonList(apiKey);

        return new Docket(DocumentationType.SWAGGER_2)
                //是否开启，根据环境配置
                .enable(true)
//                .groupName("api")
                .apiInfo(frontApiInfo())
                .select()

                //指定扫描的包  前端api
                .apis(RequestHandlerSelectors.basePackage("cn.rh.flash.api.controller.frontapi"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(headers).securityContexts(securityContexts());
    }
    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                        .build()
        );
    }
    /**
     * 前台API信息
     */
    private ApiInfo frontApiInfo() {
        return new ApiInfoBuilder()
                .title("rh-api")
                .description("前台API")
                .version("v1.0")
                .license("MIT 1.0")
                .contact(    //添加开发者的一些信息
                        new Contact("jk", "",
                                "5439795xx@qq.com"))
                .build();
    }
}
