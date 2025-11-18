package org.opencds.webapp.config.ice;

import javax.xml.namespace.QName;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.opencds.dss.evaluate.EvaluationSoapService;
import org.opencds.webapp.config.base.AppConfiguration;
import org.opencds.webapp.config.base.ConfigRestConfiguration;
import org.opencds.webapp.config.base.SecurityConfiguration;
import org.opencds.webapp.config.dss.DssConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import jakarta.xml.ws.Endpoint;

@Configuration
@Import({ AppConfiguration.class, ConfigRestConfiguration.class, SecurityConfiguration.class })
public class IceConfiguration extends DssConfiguration
{
    @Override
    @Bean
    public Endpoint endpoint(final SpringBus springBus, final EvaluationSoapService evaluationSoapService)
    {
        final EndpointImpl endpoint = new EndpointImpl(springBus, evaluationSoapService);
        endpoint.setServiceName(new QName("http://www.omg.org/spec/CDSS/201105/dssWsdl", "DecisionSupportService"));
        endpoint.setEndpointName(new QName("http://www.omg.org/spec/CDSS/201105/dssWsdl", "evaluate"));
        endpoint.setWsdlLocation("WEB-INF/wsdl/dss.wsdl");
        endpoint.publish("/evaluate");
        return endpoint;
    }
}
