package com.yufei.shop.config;



import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j配置类（适配 knife4j-openapi2-spring-boot-starter 4.1.0）
 * 基于Swagger 2（OpenAPI 2.0）规范
 */
@Configuration
@EnableSwagger2WebMvc // OpenAPI2必须加这个注解，启用Swagger2核心功能
@EnableKnife4j // 开启Knife4j的增强功能（美化UI、文档导出等）
public class Knife4jConfig {

    // 从配置文件注入核心配置项，添加默认值避免配置缺失报错
    @Value("${knife4j.swagger2.title:接口文档}")
    private String title;

    @Value("${knife4j.swagger2.version:1.0.0}")
    private String version;

    @Value("${knife4j.swagger2.description:Shop项目接口文档}")
    private String description;

    // 联系人信息（均添加默认空值）
    @Value("${knife4j.swagger2.contact.name:}")
    private String contactName;

    @Value("${knife4j.swagger2.contact.email:}")
    private String contactEmail;

    @Value("${knife4j.swagger2.contact.url:}")
    private String contactUrl;

    /**
     * 配置Swagger 2核心Docket Bean（OpenAPI2的核心配置）
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2) // 指定为Swagger 2规范
                .apiInfo(apiInfo()) // 绑定文档基础信息
                .select()
                // 扫描你的Controller包路径（必须替换为实际的controller包）
                .apis(RequestHandlerSelectors.basePackage("com.yufei.shop.controller"))
                .paths(PathSelectors.any()) // 匹配所有路径
                .build()
                .enable(true); // 启用Swagger（生产环境可设为false）
    }

    /**
     * 构建文档基础信息（ApiInfo）
     */
    private springfox.documentation.service.ApiInfo apiInfo() {
        // 构建联系人信息（空值容错）
        Contact contact = new Contact(
                contactName.isBlank() ? "默认联系人" : contactName,
                contactUrl.isBlank() ? "" : contactUrl,
                contactEmail.isBlank() ? "" : contactEmail
        );

        // 组装ApiInfo
        return new ApiInfoBuilder()
                .title(title) // 文档标题
                .description(description) // 文档描述
                .version(version) // 版本号
                .contact(contact) // 联系人信息
                .license("Apache 2.0") // 许可证（可选）
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0") // 许可证URL（可选）
                .build();
    }
}
