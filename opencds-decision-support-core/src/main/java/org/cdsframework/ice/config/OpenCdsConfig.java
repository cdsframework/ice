package org.cdsframework.ice.config;

import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.cdsframework.ice.service.VersionData;
import org.cdsframework.ice.service.configurations.ICEDecisionEngineDSSEvaluationAdapter;
import org.cdsframework.ice.service.configurations.ICESupportingDataLoaderPlugin;
import org.cdsframework.ice.service.configurations.IceExecutionEngineContext;
import org.cdsframework.ice.service.configurations.IceKnowledgeLoader;
import org.opencds.config.api.ConfigData;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.strategy.ConfigStrategy;
import org.opencds.dss.evaluate.Evaluation;
import org.opencds.dss.evaluate.EvaluationSoapService;
import org.opencds.evaluation.service.EvaluationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;

import jakarta.xml.ws.Endpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class OpenCdsConfig
{
    @Bean
    public EvaluationService evaluationService()
    {
        return new EvaluationService(new VirtualThreadTaskExecutor());
    }

    @Bean
    public Evaluation dssEvaluation(final EvaluationService evaluationService, final ConfigurationService configurationService)
    {
        return new Evaluation(evaluationService, configurationService);
    }

    @Bean
    public EvaluationSoapService evaluationSoapService(final Evaluation evaluation)
    {
        return new EvaluationSoapService(evaluation);
    }

    @Bean
    public Endpoint endpoint(final SpringBus springBus, final EvaluationSoapService evaluationSoapService)
    {
        final EndpointImpl endpoint = new EndpointImpl(springBus, evaluationSoapService);
        endpoint.setServiceName(new QName("http://www.omg.org/spec/CDSS/201105/dssWsdl", "DecisionSupportService"));
        endpoint.setEndpointName(new QName("http://www.omg.org/spec/CDSS/201105/dssWsdl", "evaluate"));
        endpoint.setWsdlLocation("wsdl/dss.wsdl");
        endpoint.publish("/");
        return endpoint;
    }

    @Bean
    public ConfigData configData(final IceProperties iceProperties) throws IOException
    {
        return new ConfigData(ConfigStrategy.supportedConfigType, iceProperties.getConfigPath().getURI().toString());
    }

    @Bean
    public ConfigStrategy configStrategy()
    {
        return new ConfigStrategy(new VirtualThreadTaskExecutor());
    }

    @Bean
    public ConfigurationService configurationService(final IceProperties iceProperties,
            final org.cdsframework.ice.service.SupportingDataService iceSupportingDataService, final VersionData versionData,
            final ConfigData configData, final ConfigStrategy configStrategy)
    {
        log.info("Setting fire limit to {}", iceProperties.getFireLimit());
        System.setProperty("org.jbpm.rule.task.firelimit", Integer.toString(iceProperties.getFireLimit()));

        ICEDecisionEngineDSSEvaluationAdapter.setIceProperties(iceProperties);

        IceKnowledgeLoader.setSupportingDataService(iceSupportingDataService);

        ICESupportingDataLoaderPlugin.setIceProperties(iceProperties);
        ICESupportingDataLoaderPlugin.setSupportingDataService(iceSupportingDataService);

        log.info("Context refreshed, preloading immunization schedules");
        ICESupportingDataLoaderPlugin.preloadSchedules();

        IceExecutionEngineContext.setIceVersion(versionData.iceVersion());

        return new ConfigurationService(Set.of(configStrategy), configData);
    }
}
