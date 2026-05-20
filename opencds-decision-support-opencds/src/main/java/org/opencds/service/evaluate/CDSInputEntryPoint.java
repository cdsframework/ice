package org.opencds.service.evaluate;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.xml.transform.stream.StreamSource;

import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.pool.UnmarshallerFactory;
import org.opencds.vmr.v1_0.schema.CDSInput;

import jakarta.xml.bind.JAXBException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class CDSInputEntryPoint
{
    public static CDSInput buildInput(final byte[] inputPayload)
    {
        log.debug("starting CDSInputEntryPoint");
        final CDSInput cdsInput;

        try
        {
            cdsInput = UnmarshallerFactory.create(CDSInput.class)
                    .unmarshal(new StreamSource(new ByteArrayInputStream(inputPayload)), CDSInput.class)
                    .getValue();
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
