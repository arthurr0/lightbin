package dev.shitzuu.lightbin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi getDocket() {
        return GroupedOpenApi.builder()
                .group("dev.shitzuu")
                .pathsToMatch("/api/**")
                .pathsToExclude("/resources/**")
                .build();
    }

    @Bean
    public OpenAPI getOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("lightbin documentation")
                        .version("1.0.1")
                        .contact(new Contact().email("contact@shitzuu.dev")));
    }
}
