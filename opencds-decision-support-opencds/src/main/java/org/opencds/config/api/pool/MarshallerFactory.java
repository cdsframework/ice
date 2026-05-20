package org.opencds.config.api.pool;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.xml.JAXBContextService;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class MarshallerFactory
{
    public static Marshaller create(final Class<?> clazz)
    {
        final Marshaller marshaller;
        log.debug("Creating instance of marshaller: {}", clazz.getCanonicalName());
        try
        {
            marshaller = JAXBContextService.getJAXBContext(clazz).createMarshaller();
        }
        catch (final JAXBException e)
        {
            throw new OpenCDSRuntimeException(
                    "Request for Marshaller for class: %s created JAXBException: %s".formatted(clazz.getCanonicalName(),
                            e.getMessage()), e);
        }

        if (marshaller == null)
            throw new OpenCDSRuntimeException("Could not resolve Marshaller for class: " + clazz.getCanonicalName());

        return marshaller;
    }
}
