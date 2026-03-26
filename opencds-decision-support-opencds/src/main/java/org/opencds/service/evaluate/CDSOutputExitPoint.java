package org.opencds.service.evaluate;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.pool.MarshallerFactory;
import org.opencds.config.api.ss.ExitPoint;
import org.opencds.config.api.util.EntityIdentifierUtil;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.ObjectFactory;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CDSOutputExitPoint implements ExitPoint
{
    private static final String EMPTY_STRING = "";

    public final MarshallerFactory marshallerFactory = new MarshallerFactory();

    @Override
    public byte[] buildOutput(final ResultSetBuilder<?> resultSetBuilder, final Map<String, List<?>> results,
            final EvaluationRequestKMItem dssRequestKMItem)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final EntityIdentifier ei =
                EntityIdentifierUtil.makeEI(dssRequestKMItem.evaluationRequestDataItem().getExternalFactModelSSId());
        log.debug("building output for data model: {}", ei);
        try
        {
            final JAXBElement<?> jaxbCDSOutput = createOutput(resultSetBuilder, results, dssRequestKMItem);

            if (jaxbCDSOutput == null)
                return EMPTY_STRING.getBytes(StandardCharsets.UTF_8);

            final StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(output);

            final Marshaller marshaller = marshallerFactory.create(CDSOutput.class);
            marshaller.marshal(jaxbCDSOutput, streamResult);
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

    private JAXBElement<CDSOutput> createOutput(final ResultSetBuilder<?> resultSetBuilder, final Map<String, List<?>> results,
            final EvaluationRequestKMItem dssRequestKMItem)
    {
        final String interactionId = dssRequestKMItem.evaluationRequestDataItem().getInteractionId();
        final String requestedKmId = dssRequestKMItem.requestedKmId();

        log.debug("II: {} KMId: {} begin buildVMRSchemaResultSet", interactionId, requestedKmId);

        final CDSOutput cdsXMLOutput = (CDSOutput) resultSetBuilder.buildResultSet(results, dssRequestKMItem);

        log.debug("II: {} KMId: {} finish buildVMRSchemaResultSet", interactionId, requestedKmId);

        if ((null == cdsXMLOutput) || ((null == cdsXMLOutput.getSimpleOutput()) && (null == cdsXMLOutput.getVmrOutput())))
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
            throw new OpenCDSRuntimeException(
                    "RuntimeException in OutputFactoryWrapper: " + e.getMessage() + ", vmrOutput=" + cdsXMLOutput.getVmrOutput()
                            .toString(), e);
        }

        return jaxbCDSOutput;
    }
}
