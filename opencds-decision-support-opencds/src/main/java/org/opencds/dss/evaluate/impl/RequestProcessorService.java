package org.opencds.dss.evaluate.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.ItemIdentifier;
import org.omg.dss.KMEvaluationRequest;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.UnrecognizedScopedEntityException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.ss.EntryPoint;
import org.opencds.dss.evaluate.RequestProcessor;
import org.opencds.dss.evaluate.util.DssUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestProcessorService implements RequestProcessor
{
    private static void assertGZipped(final byte[] bytes)
    {
        if (!DssUtil.isGZipped(bytes))
            throw new InvalidDriDataFormatException("Payload designated as gzipped but the load wasn't gzipped.");
    }

    private static void validateDRIDataFormat(final List<DataRequirementItemData> listDRID)
    {
        final boolean driDataFormatBad = false;
        final String msg = "";

    }

    private static void validateScopedEntityIDRecognized(final List<DataRequirementItemData> listDRID)
    {
        final boolean driScopedEntityIDBad = false;
        final String msg = "";

    }

    private static void validateRequiredDataProvided(final List<ItemIdentifier> listFactItemTypes,
            final KMEvaluationRequest kmeRequest) throws RequiredDataNotProvidedExceptionFault
    {
        final boolean kmerRequiredDataProvided = true;

        final String msg = "";

        if (!kmerRequiredDataProvided)
            throw new RequiredDataNotProvidedExceptionFault(msg);
    }

    @Override
    public List<EvaluationRequestKMItem> decodeInput(final KnowledgeRepository knowledgeRepository, final EvaluationRequest request,
            final EvaluationRequestDataItem evaluationRequestDataItem, final List<DataRequirementItemData> listDRIData,
            final Date evalTime) throws DSSRuntimeExceptionFault
    {
        log.debug("II: {} starting RequestProcessorService.decodeInput", evaluationRequestDataItem.getInteractionId());

        final List<EvaluationRequestKMItem> kmItems = new ArrayList<>();

        validateDRIDataFormat(listDRIData);
        validateScopedEntityIDRecognized(listDRIData);

        if (listDRIData.size() != 1)
        {
            log.warn(
                    "RequestProcessorService.getInputPayloadString did not have exactly 1 payload.  It had {} payloads, and only the first one can be used.",
                    listDRIData.size());
        }

        final byte[] inputPayload = getInputPayload(listDRIData.getFirst());

        final EntityIdentifier ei = request.getDataRequirementItemData().getFirst().getData().getInformationModelSSId();
        final SSId ssId = SSIdImpl.create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
        final SemanticSignifier ss = knowledgeRepository.semanticSignifierService().find(ssId);
        if (ss == null)
            throw new InvalidDriDataFormatException("Unknown/unsupported semantic signifier: " + ssId);

        final Object cdsInput = getInput(knowledgeRepository.semanticSignifierService(), ss, evalTime, inputPayload);

        log.debug("II: {} unmarshalling completed", evaluationRequestDataItem.getInteractionId());

        log.debug("II: {} input data validated", evaluationRequestDataItem.getInteractionId());

        final FactListsBuilder flb = knowledgeRepository.semanticSignifierService().getFactListsBuilder(ssId);

        for (final KMEvaluationRequest kmeRequest : request.getKmEvaluationRequest())
        {
            final String kmidString = DssUtil.makeEIString(kmeRequest.getKmId());
            final KnowledgeModule km = knowledgeRepository.knowledgeModuleService().find(kmidString);
            if (km == null)
                throw new UnrecognizedScopedEntityException("Unknown KMId : " + kmidString);
            final Map<Class<?>, List<?>> allFactLists = flb.buildFactLists(knowledgeRepository, km, cdsInput, evalTime);
            kmItems.add(new EvaluationRequestKMItem(kmidString, evaluationRequestDataItem, allFactLists));
        }

        return kmItems;
    }

    private byte[] getInputPayload(final DataRequirementItemData driData)
    {
        byte[] payload;
        if (DssUtil.isGZipDesignated(driData))
        {
            final ByteArrayOutputStream uncompressed = new ByteArrayOutputStream(1024 * 4);
            for (final byte[] chunk : driData.getData().getBase64EncodedPayload())
            {
                assertGZipped(chunk);
                try (final var gzIS = new GZIPInputStream(new ByteArrayInputStream(chunk)))
                {
                    gzIS.transferTo(uncompressed);
                }
                catch (final IOException e)
                {
                    throw new InvalidDriDataFormatException("Unable to decode payload: " + e.getMessage(), e);
                }
            }
            payload = uncompressed.toByteArray();
        }
        else
        {
            payload = driData.getData().getBase64EncodedPayload().getFirst();
            for (int i = 1; i < driData.getData().getBase64EncodedPayload().size(); i++)
            {
                final byte[] chunk = driData.getData().getBase64EncodedPayload().get(i);
                payload = ArrayUtils.addAll(payload, chunk);
            }
        }
        if (log.isTraceEnabled())
            log.trace(new String(payload));
        return payload;
    }

    private Object getInput(final SemanticSignifierService semanticSignifierService, final SemanticSignifier ss,
            final Date evalTime, final byte[] inputPayload) throws DSSRuntimeExceptionFault
    {
        final EntryPoint<?> entryPoint = semanticSignifierService.getEntryPoint(ss.getSSId());
        if (entryPoint == null)
            throw new DSSRuntimeExceptionFault("EntryPoint not found for SemanticSignifier: " + ss.getSSId());
        return entryPoint.buildInput(inputPayload);
    }
}
