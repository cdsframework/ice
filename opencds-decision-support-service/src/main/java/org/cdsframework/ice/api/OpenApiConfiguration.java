package org.cdsframework.ice.api;

import org.cdsframework.ice.config.OpenApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class OpenApiConfiguration
{
    @Bean
    public OpenAPI immunizationForecastOpenApi(final OpenApiProperties openApiProperties)
    {
        return new OpenAPI().info(new Info().title(openApiProperties.getTitle())
                .description(openApiProperties.getDescription())
                .version(openApiProperties.getVersion())
                .contact(new Contact().name(openApiProperties.getContact().name())
                        .email(openApiProperties.getContact().email())
                        .url(openApiProperties.getContact().url()))
                .license(new License().name(openApiProperties.getLicense().name()).url(openApiProperties.getLicense().url())));
    }
}
