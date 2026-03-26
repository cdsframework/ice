package org.opencds.service.evaluate;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.xml.transform.stream.StreamSource;

import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.pool.UnmarshallerFactory;
import org.opencds.config.api.ss.EntryPoint;
import org.opencds.vmr.v1_0.schema.CDSInput;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CDSInputEntryPoint implements EntryPoint<CDSInput>
{
    private final UnmarshallerFactory unmarshallerFactory = new UnmarshallerFactory();

    @Override
    public CDSInput buildInput(final byte[] inputPayload)
    {
        log.debug("starting CDSInputEntryPoint");
        final CDSInput cdsInput;

        try
        {
            final StreamSource payloadStream = new StreamSource(new ByteArrayInputStream(inputPayload));
            final Unmarshaller unmarshaller = unmarshallerFactory.create(CDSInput.class);
            final JAXBElement<CDSInput> jaxbElement = unmarshaller.unmarshal(payloadStream, CDSInput.class);
            cdsInput = jaxbElement.getValue();
        }
        catch (final JAXBException e)
        {
            log.error(e.getMessage(), e);
            throw new InvalidDriDataFormatException(
                    e.getMessage() + ", therefore unable to unmarshal input Semantic Payload xml string: " + Arrays.toString(
                            inputPayload), e);
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new OpenCDSRuntimeException(e.getMessage(), e);
        }

        log.debug("finished CDSInputEntryPoint");

        return cdsInput;
    }
}
