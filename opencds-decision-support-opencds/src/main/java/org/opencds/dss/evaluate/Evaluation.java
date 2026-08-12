package org.opencds.dss.evaluate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPOutputStream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EntityIdentifier;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.FinalKMEvaluationResponse;
import org.omg.dss.InteractionIdentifier;
import org.omg.dss.ItemIdentifier;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.SemanticPayload;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.exceptions.UnrecognizedScopedEntityException;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.SSId;
import org.opencds.dss.evaluate.util.DssUtil;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.service.evaluate.CDSOutputExitPoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Evaluation
{
    private final EvaluationService evaluationService;
    private final ConfigurationService configurationService;
    private final EvaluationRequestPreProcessor evaluationRequestPreProcessor;

    public EvaluationResponse evaluate(final InteractionIdentifier interactionId, final EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        log.debug("II: {}EvaluationSoapService.evaluate started", interactionId.getInteractionId());
        final long preProcessingStartedAtNanos = System.nanoTime();
        final EvaluationRequestPreProcessor.Result preProcessingResult = evaluationRequestPreProcessor.process(evaluationRequest);
        final long preProcessingDurationMillis = (System.nanoTime() - preProcessingStartedAtNanos) / 1_000_000L;
        log.debug(
                "II: {} evaluation request preprocessing completed in {} ms; payloads={}, augmentedPayloads={}, addedScheduleFlags={}",
                interactionId.getInteractionId(), preProcessingDurationMillis, preProcessingResult.payloadCount(),
                preProcessingResult.augmentedPayloadCount(), preProcessingResult.addedScheduleFlagCount());

        final List<FinalKMEvaluationResponse> responses =
                evaluateInternal(configurationService.getKnowledgeRepository(), interactionId.getInteractionId(), evaluationRequest,
                        LocalDate.now());

        final EvaluationResponse evaluateResponse = new EvaluationResponse();

        for (final FinalKMEvaluationResponse response : responses)
            evaluateResponse.getFinalKMEvaluationResponse().add(response);

        log.info("II: {}  EvaluationSoapService.evaluate completed", interactionId.getInteractionId());

        return evaluateResponse;
    }

    private EvaluationRequestDataItem createEvaluationRequestDataItem(final EvaluationRequest evaluationRequest,
            final List<DataRequirementItemData> listDRIData, final String interactionId, final LocalDate evalTime)
    {
        return EvaluationRequestDataItem.builder()
                .interactionId(interactionId)
                .evalTime(evalTime)
                .clientLanguage(evaluationRequest.getClientLanguage())
                .clientTimeZoneOffset(evaluationRequest.getClientTimeZoneOffset())
                .inputItemName(listDRIData.getFirst().getDriId().getItemId())
                .inputContainingEntityId(DssUtil.makeEIString(listDRIData.getFirst().getDriId().getContainingEntityId()))
                .externalFactModelSSId(DssUtil.makeEIString(listDRIData.getFirst().getData().getInformationModelSSId()))
                .build();
    }

    public EvaluationResponse evaluateAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        final long startedAtNanos = System.nanoTime();
        log.debug("II: {} EvaluationSoapService.evaluateAtSpecifiedTime started", interactionId.getInteractionId());
        final long preProcessingStartedAtNanos = System.nanoTime();
        final EvaluationRequestPreProcessor.Result preProcessingResult = evaluationRequestPreProcessor.process(evaluationRequest);
        final long preProcessingDurationMillis = (System.nanoTime() - preProcessingStartedAtNanos) / 1_000_000L;
        log.debug(
                "II: {} evaluation request preprocessing completed in {} ms; payloads={}, augmentedPayloads={}, addedScheduleFlags={}",
                interactionId.getInteractionId(), preProcessingDurationMillis, preProcessingResult.payloadCount(),
                preProcessingResult.augmentedPayloadCount(), preProcessingResult.addedScheduleFlagCount());

        final List<FinalKMEvaluationResponse> responses =
                evaluateInternal(configurationService.getKnowledgeRepository(), interactionId.getInteractionId(), evaluationRequest,
                        XMLDateUtility.xmlGregorian2Date(specifiedTime));

        final EvaluationResponse evalAtSpecTimeResponse = new EvaluationResponse();

        for (final FinalKMEvaluationResponse response : responses)
            evalAtSpecTimeResponse.getFinalKMEvaluationResponse().add(response);

        final long durationMillis = (System.nanoTime() - startedAtNanos) / 1_000_000L;
        log.info("II: {}  EvaluationSoapService.evaluateAtSpecifiedTime completed in {} ms", interactionId.getInteractionId(),
                durationMillis);

        return evalAtSpecTimeResponse;
    }

    private List<FinalKMEvaluationResponse> evaluateInternal(final KnowledgeRepository kr, final String interactionId,
            final EvaluationRequest evaluationRequest, final LocalDate specifiedTime)
            throws EvaluationExceptionFault, UnrecognizedScopedEntityExceptionFault, DSSRuntimeExceptionFault
    {
        final List<DataRequirementItemData> listDRIData =
                new CopyOnWriteArrayList<>(evaluationRequest.getDataRequirementItemData());

        final EvaluationRequestDataItem evalRequestDataItem =
                createEvaluationRequestDataItem(evaluationRequest, listDRIData, interactionId, specifiedTime);

        final List<EvaluationResponseKMItem> responseKMItems;
        try
        {
            responseKMItems = evaluationService.evaluate(kr,
                    RequestProcessor.decodeInput(kr, evaluationRequest, evalRequestDataItem, listDRIData, specifiedTime));
        }
        catch (final EvaluationException e)
        {
            log.error(e.getMessage(), e);
            throw new EvaluationExceptionFault(e.getCause().getMessage(), new org.omg.dss.EvaluationException(), e.getCause());
        }
        catch (final UnrecognizedScopedEntityException e)
        {
            throw new UnrecognizedScopedEntityExceptionFault(e.getMessage(), new org.omg.dss.UnrecognizedScopedEntityException(),
                    e);
        }
        catch (final OpenCDSRuntimeException e)
        {
            throw new DSSRuntimeExceptionFault(e.getMessage(), e);
        }

        final List<FinalKMEvaluationResponse> responses = new ArrayList<>();
        try
        {
            for (final EvaluationResponseKMItem responseKMItem : responseKMItems)
            {
                log.debug("Building output");
                final EntityIdentifier ei = DssUtil.makeEI(
                        responseKMItem.evaluationRequestKMItem().evaluationRequestDataItem().externalFactModelSSId());
                final SSId ssId = new SSId(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
                final byte[] rawResult =
                        CDSOutputExitPoint.buildOutput(responseKMItem.resultFactLists(), responseKMItem.evaluationRequestKMItem());
                log.debug("Building output done.");
                final SemanticPayload semanticPayload = createSemanticPayload(encodePayload(rawResult, evalRequestDataItem),
                        DssUtil.makeEI(
                                responseKMItem.evaluationRequestKMItem().evaluationRequestDataItem().externalFactModelSSId()));
                final ItemIdentifier itemId = createItemIdentifier(evalRequestDataItem);
                final KMEvaluationResultData kmerData = createKMEvaluationResultData(semanticPayload, itemId);

                final FinalKMEvaluationResponse response = new FinalKMEvaluationResponse();
                response.setKmId(DssUtil.makeEI(responseKMItem.evaluationRequestKMItem().requestedKmId()));
                response.getKmEvaluationResultData().add(kmerData);

                log.debug("KMId: {} DSSEvaluation.evaluateAtSpecifiedTime completed one KM",
                        responseKMItem.evaluationRequestKMItem().requestedKmId());
                log.debug("Adding response for KM");
                responses.add(response);
                log.debug("Finished evaluation of KM");
            }
        }
        catch (final OpenCDSRuntimeException e)
        {
            throw new DSSRuntimeExceptionFault(e.getMessage(), e);
        }
        return responses;
    }

    private KMEvaluationResultData createKMEvaluationResultData(final SemanticPayload payload, final ItemIdentifier itemId)
    {
        final KMEvaluationResultData kmerData = new KMEvaluationResultData();
        kmerData.setData(payload);
        kmerData.setEvaluationResultId(itemId);
        return kmerData;
    }

    private ItemIdentifier createItemIdentifier(final EvaluationRequestDataItem evalRequestDataItem)
    {
        final ItemIdentifier itemIdentifier = new ItemIdentifier();
        itemIdentifier.setItemId(evalRequestDataItem.inputItemName() + ".EvaluationResult");
        itemIdentifier.setContainingEntityId(DssUtil.makeEI(evalRequestDataItem.inputContainingEntityId()));
        return itemIdentifier;
    }

    private SemanticPayload createSemanticPayload(final byte[] payload, final EntityIdentifier eid)
    {
        final SemanticPayload semanticPayload = new SemanticPayload();
        semanticPayload.setInformationModelSSId(eid);
        semanticPayload.getBase64EncodedPayload().add(payload);
        return semanticPayload;
    }

    public byte[] encodePayload(final byte[] payload, final EvaluationRequestDataItem evaluationRequestDataItem)
            throws DSSRuntimeExceptionFault
    {
        final byte[] result;
        if (DssUtil.isGZipDesignated(evaluationRequestDataItem))
        {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(payload.length);
            try (final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream))
            {
                gzipOutputStream.write(payload);
            }
            catch (final IOException e)
            {
                throw new DSSRuntimeExceptionFault("Error compressing payload: " + e.getMessage(), e);
            }
            result = byteArrayOutputStream.toByteArray();
        }
        else
            result = payload;
        return result;
    }
}
