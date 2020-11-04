package org.opencds.config.api.pool;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.service.JAXBContextService;

public class UnmarshallerFactory extends BaseKeyedPooledObjectFactory<SemanticSignifier, Unmarshaller> {
	private static final Logger log = LogManager.getLogger();
    private final JAXBContextService jaxbContextService;

    public UnmarshallerFactory(JAXBContextService jaxbContextService) {
        this.jaxbContextService = jaxbContextService;
    }
    
    @Override
    public Unmarshaller create(SemanticSignifier semanticSignifier) throws Exception {
        Unmarshaller unmarshaller = null;
        log.debug(semanticSignifier + ": creating pooled instance of unmarshaller: " + semanticSignifier.getEntryPoint());
        try {
            unmarshaller = jaxbContextService.getJAXBContext(semanticSignifier).createUnmarshaller();
        } catch (JAXBException e) {
            throw new OpenCDSRuntimeException("Request for Unmarshaller for SSID: " + semanticSignifier.getSSId()
                    + " (" + semanticSignifier.getEntryPoint() + ") created JAXBException: " + e.getMessage());
        }
        if (unmarshaller == null) {
            throw new OpenCDSRuntimeException("Could not resolve Unmarshaller for data model: "
                    + semanticSignifier.getSSId() + ", and class: " + semanticSignifier.getEntryPoint());
        }
        return unmarshaller;
    }

    @Override
    public PooledObject<Unmarshaller> wrap(Unmarshaller value) {
        return new DefaultPooledObject<>(value);
    }

}
