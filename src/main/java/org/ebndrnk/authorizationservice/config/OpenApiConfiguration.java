package org.ebndrnk.authorizationservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfiguration
 * <p>
 * This configuration class sets up the OpenApi's beans
 * for OpenAPI configuration for API documentation.
 */
@Configuration
public class OpenApiConfiguration {


    /**
     * Creates an OpenAPI bean for API documentation.
     * The server URL is injected from the property 'site.domain.url'.
     *
     * @param api the base API URL from configuration
     * @return a configured OpenAPI instance with server details
     */
    @Bean
    OpenAPI prodOpenAPI(@Value("${site.domain.url}") String api) {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addServersItem(new Server().url(api))
                .info(new Info().title("Innowise intern project"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }


}





