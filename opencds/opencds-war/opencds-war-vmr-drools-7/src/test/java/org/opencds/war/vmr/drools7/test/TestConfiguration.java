package org.opencds.war.vmr.drools7.test;

import org.opencds.webapp.config.dss.DssConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(DssConfiguration.class)
@PropertySource(value = "classpath:/config/opencds.properties")
public class TestConfiguration {
}
