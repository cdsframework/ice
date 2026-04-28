package org.cdsframework.ice.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfiguration
{
    @Bean
    public OpenAPI immunizationForecastOpenApi()
    {
        return new OpenAPI().info(new Info().title("ICE Immunization Forecast API")
                .description("FHIR operation endpoint for immunization forecasting: POST /$immunization-forecast. "
                        + "The request body is a FHIR Parameters resource and the response is a FHIR Parameters resource.")
                .version("1.0.0")
                .contact(new Contact().name("ICE Support").email("ice@hln.com").url("http://www.hln.com/ice"))
                .license(new License().name("LGPL-3.0-or-later").url("https://www.gnu.org/licenses/lgpl-3.0.en.html")));
    }
}
