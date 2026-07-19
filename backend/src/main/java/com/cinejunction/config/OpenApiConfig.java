package com.cinejunction.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI cineJunctionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CineJunction API")
                        .description("CineJunction movie platform backend API")
                        .version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)));
    }

    @Bean
    public GroupedOpenApi publicApis() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/auth/**", "/actuator/health", "/error", "/swagger-ui/**", "/v3/api-docs/**")
                .build();
    }

    @Bean
    public GroupedOpenApi protectedApis() {
        return GroupedOpenApi.builder()
                .group("protected")
                .pathsToExclude("/api/auth/**", "/actuator/health", "/error", "/swagger-ui/**", "/v3/api-docs/**")
                .build();
    }
}
