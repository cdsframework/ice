package org.opencds.dss.evaluate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.KMEvaluationRequest;
import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.UnrecognizedScopedEntityException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.dss.evaluate.util.DssUtil;
import org.opencds.service.evaluate.CDSInputEntryPoint;
import org.opencds.service.evaluate.CdsInputFactListsBuilder;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class RequestProcessor
{
    private static void assertGZipped(final byte[] bytes)
    {
        if (!DssUtil.isGZipped(bytes))
            throw new InvalidDriDataFormatException("Payload designated as gzipped but the load wasn't gzipped.");
    }

    public static List<EvaluationRequestKMItem> decodeInput(final KnowledgeRepository knowledgeRepository,
            final EvaluationRequest request, final EvaluationRequestDataItem evaluationRequestDataItem,
            final List<DataRequirementItemData> listDRIData, final LocalDate evalTime)
    {
        log.debug("II: {} starting RequestProcessorService.decodeInput", evaluationRequestDataItem.interactionId());

        final List<EvaluationRequestKMItem> kmItems = new ArrayList<>();

        if (listDRIData.size() != 1)
            log.warn(
                    "RequestProcessorService.getInputPayloadString did not have exactly 1 payload.  It had {} payloads, and only the first one can be used.",
                    listDRIData.size());

        final byte[] inputPayload = getInputPayload(listDRIData.getFirst());

        final EntityIdentifier ei = request.getDataRequirementItemData().getFirst().getData().getInformationModelSSId();
        final SSId ssId = new SSId(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
        final SemanticSignifier ss = knowledgeRepository.semanticSignifierService().find(ssId);
        if (ss == null)
            throw new InvalidDriDataFormatException("Unknown/unsupported semantic signifier: " + ssId);

        final Object cdsInput = getInput(inputPayload);

        log.debug("II: {} unmarshalling completed", evaluationRequestDataItem.interactionId());

        log.debug("II: {} input data validated", evaluationRequestDataItem.interactionId());

        for (final KMEvaluationRequest kmeRequest : request.getKmEvaluationRequest())
        {
            final String kmidString = DssUtil.makeEIString(kmeRequest.getKmId());
            final KnowledgeModule km = knowledgeRepository.knowledgeModuleService().find(kmidString);
            if (km == null)
                throw new UnrecognizedScopedEntityException("Unknown KMId : " + kmidString);

            kmItems.add(new EvaluationRequestKMItem(kmidString, evaluationRequestDataItem,
                    CdsInputFactListsBuilder.buildFactLists(knowledgeRepository, km, cdsInput, evalTime)));
        }

        return kmItems;
    }

    private static byte[] getInputPayload(final DataRequirementItemData driData)
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
                payload = ArrayUtils.addAll(payload, driData.getData().getBase64EncodedPayload().get(i));
        }

        if (log.isTraceEnabled())
            log.trace(new String(payload));

        return payload;
    }

    private static Object getInput(final byte[] inputPayload)
    {
        return CDSInputEntryPoint.buildInput(inputPayload);
    }
}
