package org.opencds.config.api.pool;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.service.JAXBContextService;

public class MarshallerFactory extends BaseKeyedPooledObjectFactory<SemanticSignifier, Marshaller>{
    private static final Log log = LogFactory.getLog(MarshallerFactory.class);
    private final JAXBContextService jaxbContextService;
    
    public MarshallerFactory(JAXBContextService jaxbContextService) {
        this.jaxbContextService = jaxbContextService;
    }
    
    @Override
    public Marshaller create(SemanticSignifier semanticSignifier) throws Exception {
        Marshaller marshaller = null;
        log.debug(semanticSignifier + ": creating pooled instance of marshaller: " +  semanticSignifier.getEntryPoint());
        try {
            marshaller = jaxbContextService.getJAXBContext(semanticSignifier).createMarshaller();
        } catch (JAXBException e) {
            throw new OpenCDSRuntimeException("Request for Marshaller for SSID: " + semanticSignifier.getSSId()
                    + " (" + semanticSignifier.getEntryPoint() + ") created JAXBException: " + e.getMessage());
        }
        if (marshaller == null) {
            throw new OpenCDSRuntimeException("Could not resolve Marshaller for data model: "
                    + semanticSignifier.getSSId() + ", and class: " + semanticSignifier.getEntryPoint());
        }
        return marshaller;
    }

    @Override
    public PooledObject<Marshaller> wrap(Marshaller value) {
        return new DefaultPooledObject<>(value);
    }

}
