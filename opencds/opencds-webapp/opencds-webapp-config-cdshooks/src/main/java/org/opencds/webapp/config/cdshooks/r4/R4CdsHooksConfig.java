package org.opencds.webapp.config.cdshooks.r4;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.hooks.evaluation.service.HookEvaluation;
import org.opencds.hooks.evaluation.service.ResourceListBuilder;
import org.opencds.hooks.model.r4.util.R4JsonUtil;
import org.opencds.hooks.services.CDSHooksService;
import org.opencds.hooks.services.cors.CorsFilter;
import org.opencds.webapp.config.cdshooks.common.CommonCdsHooksConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonCdsHooksConfig.class)
public class R4CdsHooksConfig {

    @Bean
    public R4JsonUtil r4JsonUtil() {
        return new R4JsonUtil();
    }

    @Bean
    public HookEvaluation r4HookEvaluation(ConfigurationService configurationService,
                                           EvaluationService evaluationService) {
        return new HookEvaluation(configurationService, evaluationService, new ResourceListBuilder(), FhirVersion.R4);
    }

    @Bean
    public CDSHooksService r4CdsHooksService(ConfigurationService configurationService,
                                             HookEvaluation r4HookEvaluation,
                                             R4JsonUtil r4JsonUtil) {
        return new CDSHooksService(configurationService, r4HookEvaluation, r4JsonUtil);
    }

    @Bean
    public Server r4CdsHooksServer(SpringBus springBus, CDSHooksService r4CdsHooksService, CorsFilter cdsHooksCorsFilter) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(springBus);
        endpoint.setAddress("/r4/hooks");
        endpoint.setServiceBean(r4CdsHooksService);
        endpoint.setProvider(cdsHooksCorsFilter);
        return endpoint.create();
    }

}
