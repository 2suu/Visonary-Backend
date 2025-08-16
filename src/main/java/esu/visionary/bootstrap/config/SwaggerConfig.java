package esu.visionary.bootstrap.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(
            @Value("${app.api.title:Visionary API}") String title,
            @Value("${app.api.description:Visionary Backend}") String description,
            @Value("${app.api.version:v1}") String version,
            @Value("${app.api.server-url:http://localhost:8080}") String serverUrl
    ) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact().name("Visionary Team")))
                .addServersItem(new Server().url(serverUrl));
    }

    /** ErrorResponse / ApiResponse 전역 스키마 등록 */
    @Bean
    public GlobalOpenApiCustomizer globalSchemas() {
        return openApi -> {
            Components components = openApi.getComponents();

            Schema<?> errorResponse = new ObjectSchema()
                    .name("ErrorResponse")
                    .addProperties("code", new IntegerSchema().example(400))
                    .addProperties("status", new StringSchema().example("BAD_REQUEST"))
                    .addProperties("message", new StringSchema().example("올바른 이메일 형식이 아닙니다."));
            components.addSchemas("ErrorResponse", errorResponse);

            Schema<?> apiResponse = new ObjectSchema()
                    .name("ApiResponse")
                    .addProperties("code", new IntegerSchema().example(200))
                    .addProperties("status", new StringSchema().example("OK"))
                    .addProperties("message", new StringSchema().example("SUCCESS"))
                    .addProperties("data", new ObjectSchema());
            components.addSchemas("ApiResponse", apiResponse);
        };
    }

    /** 모든 API 허용 (보안 제거) */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApi -> openApi.setSecurity(List.of())) // 전역 보안 제거
                .build();
    }
}
