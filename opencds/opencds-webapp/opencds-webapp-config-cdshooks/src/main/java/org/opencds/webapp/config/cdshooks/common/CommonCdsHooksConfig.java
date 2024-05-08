package org.opencds.webapp.config.cdshooks.common;

import org.opencds.hooks.services.cors.CorsFilter;
import org.opencds.webapp.config.base.AppConfiguration;
import org.opencds.webapp.config.base.ConfigRestConfiguration;
import org.opencds.webapp.config.base.SecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AppConfiguration.class,
        ConfigRestConfiguration.class,
        SecurityConfiguration.class
})
public class CommonCdsHooksConfig {
    @Bean
    public CorsFilter cdsHooksCorsFilter() {
        return new CorsFilter();
    }
}
