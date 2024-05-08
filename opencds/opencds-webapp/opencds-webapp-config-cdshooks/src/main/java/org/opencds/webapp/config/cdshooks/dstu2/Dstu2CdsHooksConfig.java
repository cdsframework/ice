package org.opencds.webapp.config.cdshooks.dstu2;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.hooks.evaluation.service.HookEvaluation;
import org.opencds.hooks.evaluation.service.ResourceListBuilder;
import org.opencds.hooks.model.dstu2.util.Dstu2JsonUtil;
import org.opencds.hooks.services.CDSHooksService;
import org.opencds.hooks.services.cors.CorsFilter;
import org.opencds.webapp.config.cdshooks.common.CommonCdsHooksConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonCdsHooksConfig.class)
public class Dstu2CdsHooksConfig {

    @Bean
    public Dstu2JsonUtil dstu2JsonUtil() {
        return new Dstu2JsonUtil();
    }

    @Bean
    public HookEvaluation dstu2HookEvaluation(ConfigurationService configurationService,
                                              EvaluationService evaluationService) {
        return new HookEvaluation(configurationService, evaluationService, new ResourceListBuilder(), FhirVersion.DSTU2);
    }

    @Bean
    public CDSHooksService dstu2CdsHooksService(ConfigurationService configurationService,
                                                HookEvaluation dstu2HookEvaluation,
                                                Dstu2JsonUtil dstu2JsonUtil) {
        return new CDSHooksService(configurationService, dstu2HookEvaluation, dstu2JsonUtil);
    }

    @Bean
    public Server dstu2CdsHooksServer(SpringBus springBus, CDSHooksService dstu2CdsHooksService, CorsFilter cdsHooksCorsFilter) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(springBus);
        endpoint.setAddress("/dstu2/hooks");
        endpoint.setServiceBean(dstu2CdsHooksService);
        endpoint.setProvider(cdsHooksCorsFilter);
        return endpoint.create();
    }

}
