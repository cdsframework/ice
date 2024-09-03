package org.opencds.webapp.config.dss;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.opencds.config.api.ConfigurationService;
import org.opencds.dss.evaluate.EvaluationSoapService;
import org.opencds.dss.evaluate.impl.DSSEvaluation;
import org.opencds.dss.evaluate.impl.RequestProcessorService;
import org.opencds.dss.evaluation.service.util.DSSCallableUtil;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.evaluation.service.EvaluationServiceImpl;
import org.opencds.webapp.config.base.AppConfiguration;
import org.opencds.webapp.config.base.ConfigRestConfiguration;
import org.opencds.webapp.config.base.SecurityConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.xml.namespace.QName;

@Configuration
@Import({
        AppConfiguration.class,
        ConfigRestConfiguration.class,
        SecurityConfiguration.class
})
public class DssConfiguration {
    @Bean
    public RequestProcessorService requestProcessorService() {
        return new RequestProcessorService();
    }

    @Bean
    public DSSCallableUtil dssCallableUtil() {
        return new DSSCallableUtil();
    }

    @Bean
    public EvaluationService evaluationService() {
        return new EvaluationServiceImpl(dssCallableUtil());
    }

    @Bean
    public DSSEvaluation dssEvaluation(ConfigurationService configurationService) {
        return new DSSEvaluation(
                evaluationService(),
                configurationService,
                requestProcessorService());
    }

    @Bean
    public EvaluationSoapService evaluationSoapService(DSSEvaluation dssEvaluation) {
        return new EvaluationSoapService(dssEvaluation);
    }

    @Bean
    public Endpoint endpoint(SpringBus springBus, EvaluationSoapService evaluationSoapService) {
        EndpointImpl endpoint = new EndpointImpl(springBus, evaluationSoapService);
        endpoint.setServiceName(new QName(
                "http://www.omg.org/spec/CDSS/201105/dssWsdl",
                "DecisionSupportService"));
        endpoint.setEndpointName(new QName(
                "http://www.omg.org/spec/CDSS/201105/dssWsdl",
                "evaluate"));
        endpoint.setWsdlLocation("WEB-INF/wsdl/dss.wsdl");
        endpoint.publish("/evaluate");
        return endpoint;
    }


}
