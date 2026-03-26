package org.opencds.config.api.pool;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.xml.JAXBContextService;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarshallerFactory
{
    public Marshaller create(final Class<?> clazz)
    {
        final Marshaller marshaller;
        log.debug("Creating instance of marshaller: {}", clazz.getCanonicalName());
        try
        {
            marshaller = JAXBContextService.getInstance().getJAXBContext(clazz).createMarshaller();
        }
        catch (final JAXBException e)
        {
            throw new OpenCDSRuntimeException(
                    "Request for Marshaller for class: " + clazz.getCanonicalName() + " created JAXBException: " + e.getMessage(),
                    e);
        }

        if (marshaller == null)
            throw new OpenCDSRuntimeException("Could not resolve Marshaller for class: " + clazz.getCanonicalName());

        return marshaller;
    }
}
