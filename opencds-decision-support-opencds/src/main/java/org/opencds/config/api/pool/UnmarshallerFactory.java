package org.opencds.config.api.pool;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.xml.JAXBContextService;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnmarshallerFactory
{
    public Unmarshaller create(final Class<?> clazz)
    {
        final Unmarshaller unmarshaller;
        log.debug("Creating instance of unmarshaller: {}", clazz.getCanonicalName());
        try
        {
            unmarshaller = JAXBContextService.getInstance().getJAXBContext(clazz).createUnmarshaller();
        }
        catch (final JAXBException e)
        {
            throw new OpenCDSRuntimeException(
                    "Request for Unmarshaller for class: " + clazz.getCanonicalName() + " created JAXBException: "
                            + e.getMessage());
        }

        if (unmarshaller == null)
            throw new OpenCDSRuntimeException("Could not resolve Unmarshaller for class: " + clazz.getCanonicalName());

        return unmarshaller;
    }
}
