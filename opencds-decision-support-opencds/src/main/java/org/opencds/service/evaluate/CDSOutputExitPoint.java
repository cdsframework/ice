package org.opencds.service.evaluate;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.pool.MarshallerFactory;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObjectFactory;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class CDSOutputExitPoint
{
    public static byte[] buildOutput(final Map<String, List<?>> results, final EvaluationRequestKMItem dssRequestKMItem)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        log.debug("building output for data model: {}", dssRequestKMItem.evaluationRequestDataItem().externalFactModelSSId());
        try
        {
            final JAXBElement<?> jaxbCDSOutput = createOutput(results, dssRequestKMItem);

            if (jaxbCDSOutput == null)
                return new byte[0];

            final StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(output);

            MarshallerFactory.create(CDSOutput.class).marshal(jaxbCDSOutput, streamResult);
        }
        catch (final JAXBException e)
        {
            log.error(e.getMessage(), e);
            throw new EvaluationException("JAXBException in mappingOutbound marshalling cdsOutput: " + e.getMessage(), e);
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new OpenCDSRuntimeException("OpenCDS encountered Exception when building output: " + e.getMessage(), e);
        }

        log.debug("Finished marshalling results to external VMR.");

        return output.toByteArray();
    }

    private static JAXBElement<CDSOutput> createOutput(final Map<String, List<?>> results,
            final EvaluationRequestKMItem dssRequestKMItem)
    {
        final String interactionId = dssRequestKMItem.evaluationRequestDataItem().interactionId();
        final String requestedKmId = dssRequestKMItem.requestedKmId();

        log.debug("II: {} KMId: {} begin buildVMRSchemaResultSet", interactionId, requestedKmId);

        final CDSOutput cdsXMLOutput = CdsOutputResultSetBuilder.buildResultSet(results);

        log.debug("II: {} KMId: {} finish buildVMRSchemaResultSet", interactionId, requestedKmId);

        if (((cdsXMLOutput.getSimpleOutput() == null) && (cdsXMLOutput.getVmrOutput() == null)))
            return null;

        log.debug("II: {} KMId: {} finished building results as external VMR: {}", interactionId, requestedKmId,
                cdsXMLOutput.getVmrOutput().getTemplateId().toString());

        final JAXBElement<CDSOutput> jaxbCDSOutput;

        try
        {
            log.debug("II: {} KMId: {} begin factory.createCdsOutput", interactionId, requestedKmId);

            final ObjectFactory factory = new ObjectFactory();
            jaxbCDSOutput = factory.createCdsOutput(cdsXMLOutput);

            log.debug("II: {} KMId: {} finish factory.createCdsOutput", interactionId, requestedKmId);
        }
        catch (final RuntimeException e)
        {
            log.error(e.getMessage(), e);
            throw new OpenCDSRuntimeException("RuntimeException in OutputFactoryWrapper: %s, vmrOutput=%s".formatted(e.getMessage(),
                    cdsXMLOutput.getVmrOutput().toString()), e);
        }

        return jaxbCDSOutput;
    }
}
