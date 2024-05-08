package org.opencds.webapp.config.cdshooks.r5;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.hooks.evaluation.service.HookEvaluation;
import org.opencds.hooks.evaluation.service.ResourceListBuilder;
import org.opencds.hooks.model.r5.util.R5JsonUtil;
import org.opencds.hooks.services.CDSHooksService;
import org.opencds.hooks.services.cors.CorsFilter;
import org.opencds.webapp.config.cdshooks.common.CommonCdsHooksConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonCdsHooksConfig.class)
public class R5CdsHooksConfig {

    @Bean
    public R5JsonUtil r5JsonUtil() {
        return new R5JsonUtil();
    }

    @Bean
    public HookEvaluation r5HookEvaluation(ConfigurationService configurationService,
                                           EvaluationService evaluationService) {
        return new HookEvaluation(configurationService, evaluationService, new ResourceListBuilder(), FhirVersion.R5);
    }

    @Bean
    public CDSHooksService r5CdsHooksService(ConfigurationService configurationService,
                                             HookEvaluation r5HookEvaluation,
                                             R5JsonUtil r5JsonUtil) {
        return new CDSHooksService(configurationService, r5HookEvaluation, r5JsonUtil);
    }

    @Bean
    public Server r4CdsHooksServer(SpringBus springBus, CDSHooksService r5CdsHooksService, CorsFilter cdsHooksCorsFilter) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(springBus);
        endpoint.setAddress("/r5/hooks");
        endpoint.setServiceBean(r5CdsHooksService);
        endpoint.setProvider(cdsHooksCorsFilter);
        return endpoint.create();
    }

}
