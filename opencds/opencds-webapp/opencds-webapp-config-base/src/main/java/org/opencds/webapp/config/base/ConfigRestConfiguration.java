package org.opencds.webapp.config.base;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.xml.JAXBContextService;
import org.opencds.config.service.rest.ConceptDeterminationMethodRestService;
import org.opencds.config.service.rest.ConfigurationReloadRestService;
import org.opencds.config.service.rest.ExecutionEngineRestService;
import org.opencds.config.service.rest.KnowledgeModuleRestService;
import org.opencds.config.service.rest.LogRestService;
import org.opencds.config.service.rest.PluginPackageRestService;
import org.opencds.config.service.rest.SemanticSignifierRestService;
import org.opencds.config.service.rest.SupportingDataRestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ConfigRestConfiguration {

    @Bean
    public JAXBContextService jaxbContextService() {
        return JAXBContextService.get();
    }

    @Bean
    public ConceptDeterminationMethodRestService conceptDeterminationMethodRestService(ConfigurationService configurationService) {
        return new ConceptDeterminationMethodRestService(configurationService);
    }

    @Bean
    public ExecutionEngineRestService executionEngineRestService(ConfigurationService configurationService) {
        return new ExecutionEngineRestService(configurationService);
    }

    @Bean
    public KnowledgeModuleRestService knowledgeModuleRestService(ConfigurationService configurationService) {
        return new KnowledgeModuleRestService(configurationService);
    }

    @Bean
    public SupportingDataRestService supportingDataRestService(ConfigurationService configurationService) {
        return new SupportingDataRestService(configurationService);
    }

    @Bean
    public SemanticSignifierRestService semanticSignifierRestService(ConfigurationService configurationService) {
        return new SemanticSignifierRestService(configurationService);
    }

    @Bean
    public PluginPackageRestService pluginPackageRestService(ConfigurationService configurationService) {
        return new PluginPackageRestService(configurationService);
    }

    @Bean
    public ConfigurationReloadRestService configurationReloadRestService(ConfigurationService configurationService) {
        return new ConfigurationReloadRestService(configurationService);
    }

    @Bean
    public LogRestService logRestService() {
        return new LogRestService();
    }

    @Bean
    public Server rsServer(SpringBus springBus,
                           ConceptDeterminationMethodRestService conceptDeterminationMethodRestService,
                           ExecutionEngineRestService executionEngineRestService,
                           KnowledgeModuleRestService knowledgeModuleRestService,
                           SupportingDataRestService supportingDataRestService,
                           SemanticSignifierRestService semanticSignifierRestService,
                           PluginPackageRestService pluginPackageRestService,
                           ConfigurationReloadRestService configurationReloadRestService,
                           LogRestService logRestService) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(springBus);
        endpoint.setAddress("/config/v1");
        endpoint.setSchemaLocations(Arrays.asList(
                "classpath:schema/OpenCDSConfiguration.xsd",
                "classpath:schema/OpenCDSConfigRest.xsd"
        ));
        endpoint.setServiceBeans(Arrays.asList(
                conceptDeterminationMethodRestService,
                executionEngineRestService,
                knowledgeModuleRestService,
                supportingDataRestService,
                semanticSignifierRestService,
                pluginPackageRestService,
                configurationReloadRestService,
                logRestService
        ));
        return endpoint.create();
    }


}
