package org.opencds.webapp.config.cdshooks.stu3;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.hooks.evaluation.service.HookEvaluation;
import org.opencds.hooks.evaluation.service.ResourceListBuilder;
import org.opencds.hooks.model.stu3.util.Stu3JsonUtil;
import org.opencds.hooks.services.CDSHooksService;
import org.opencds.hooks.services.cors.CorsFilter;
import org.opencds.webapp.config.cdshooks.common.CommonCdsHooksConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonCdsHooksConfig.class)
public class Stu3CdsHooksConfig {

    @Bean
    public Stu3JsonUtil stu3JsonUtil() {
        return new Stu3JsonUtil();
    }

    @Bean
    public HookEvaluation stu3HookEvaluation(ConfigurationService configurationService,
                                           EvaluationService evaluationService) {
        return new HookEvaluation(configurationService, evaluationService, new ResourceListBuilder(), FhirVersion.STU3);
    }

    @Bean
    public CDSHooksService stu3CdsHooksService(ConfigurationService configurationService,
                                               HookEvaluation stu3HookEvaluation,
                                               Stu3JsonUtil stu3JsonUtil) {
        return new CDSHooksService(configurationService, stu3HookEvaluation, stu3JsonUtil);
    }

    @Bean
    public Server r4CdsHooksServer(SpringBus springBus, CDSHooksService stu3CdsHooksService, CorsFilter cdsHooksCorsFilter) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(springBus);
        endpoint.setAddress("/stu3/hooks");
        endpoint.setServiceBean(stu3CdsHooksService);
        endpoint.setProvider(cdsHooksCorsFilter);
        return endpoint.create();
    }

}
