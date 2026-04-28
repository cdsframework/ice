package org.opencds.dss.evaluate.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPOutputStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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
import org.omg.dss.IterativeEvaluationRequest;
import org.omg.dss.IterativeEvaluationResponse;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.SemanticPayload;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.exceptions.UnrecognizedScopedEntityException;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.api.ss.ExitPoint;
import org.opencds.dss.evaluate.Evaluation;
import org.opencds.dss.evaluate.EvaluationFactory;
import org.opencds.dss.evaluate.IterativeEvaluater;
import org.opencds.dss.evaluate.RequestProcessor;
import org.opencds.dss.evaluate.util.DssUtil;
import org.opencds.evaluation.service.EvaluationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DSSEvaluation implements Evaluation
{
    private static XMLGregorianCalendar long2Gregorian(final long date)
    {
        final DatatypeFactory dataTypeFactory;
        try
        {
            dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (final DatatypeConfigurationException e)
        {
            throw new OpenCDSRuntimeException(e);
        }
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date);
        return dataTypeFactory.newXMLGregorianCalendar(gc);
    }

    private final EvaluationService evaluationService;
    private final ConfigurationService configurationService;
    private final RequestProcessor requestProcessor;

    public DSSEvaluation(final EvaluationService evaluationService, final ConfigurationService configurationService,
            final RequestProcessor requestProcessor)
    {
        this.evaluationService = evaluationService;
        this.configurationService = configurationService;
        this.requestProcessor = requestProcessor;
    }

    private XMLGregorianCalendar rightNow()
    {
        return long2Gregorian(new Date().getTime());
    }

    @Override
    public EvaluationResponse evaluate(final InteractionIdentifier interactionId, final EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        log.debug("II: {}EvaluationSoapService.evaluate started", interactionId.getInteractionId());

        final List<FinalKMEvaluationResponse> responses =
                evaluateInternal(configurationService.getKnowledgeRepository(), interactionId.getInteractionId(), evaluationRequest,
                        XMLDateUtility.xmlGregorian2Date(rightNow()));

        final EvaluationResponse evaluateResponse = new EvaluationResponse();

        for (final FinalKMEvaluationResponse response : responses)
            evaluateResponse.getFinalKMEvaluationResponse().add(response);

        log.info("II: {}  EvaluationSoapService.evaluate completed", interactionId.getInteractionId());

        return evaluateResponse;
    }

    private EvaluationRequestDataItem createEvaluationRequestDataItem(final EvaluationRequest evaluationRequest,
            final List<DataRequirementItemData> listDRIData, final String interactionId, final Date evalTime)
    {
        final EvaluationRequestDataItem evalRequestDataItem = new EvaluationRequestDataItem();
        evalRequestDataItem.setInteractionId(interactionId);
        evalRequestDataItem.setEvalTime(evalTime);
        evalRequestDataItem.setClientLanguage(evaluationRequest.getClientLanguage());
        evalRequestDataItem.setClientTimeZoneOffset(evaluationRequest.getClientTimeZoneOffset());
        evalRequestDataItem.setInputItemName(listDRIData.getFirst().getDriId().getItemId());
        evalRequestDataItem.setInputContainingEntityId(
                DssUtil.makeEIString(listDRIData.getFirst().getDriId().getContainingEntityId()));
        evalRequestDataItem.setExternalFactModelSSId(
                DssUtil.makeEIString(listDRIData.getFirst().getData().getInformationModelSSId()));
        return evalRequestDataItem;
    }

    @Override
    public EvaluationResponse evaluateAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final EvaluationRequest evaluationRequest)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        final long startedAtNanos = System.nanoTime();
        log.debug("II: {} EvaluationSoapService.evaluateAtSpecifiedTime started", interactionId.getInteractionId());

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
            final EvaluationRequest evaluationRequest, final Date specifiedTime)
            throws EvaluationExceptionFault, UnrecognizedScopedEntityExceptionFault, DSSRuntimeExceptionFault
    {
        final List<DataRequirementItemData> listDRIData =
                new CopyOnWriteArrayList<>(evaluationRequest.getDataRequirementItemData());

        final EvaluationRequestDataItem evalRequestDataItem =
                createEvaluationRequestDataItem(evaluationRequest, listDRIData, interactionId, specifiedTime);

        final List<EvaluationResponseKMItem> responseKMItems;
        try
        {
            final List<EvaluationRequestKMItem> evaluationRequestKMItems =
                    requestProcessor.decodeInput(kr, evaluationRequest, evalRequestDataItem, listDRIData, specifiedTime);

            responseKMItems = evaluationService.evaluate(kr, evaluationRequestKMItems);
        }
        catch (final EvaluationException e)
        {
            log.error(e.getMessage(), e);
            final org.omg.dss.EvaluationException ee = new org.omg.dss.EvaluationException();
            throw new EvaluationExceptionFault(e.getCause().getMessage(), ee, e.getCause());
        }
        catch (final UnrecognizedScopedEntityException e)
        {
            final org.omg.dss.UnrecognizedScopedEntityException usee = new org.omg.dss.UnrecognizedScopedEntityException();
            throw new UnrecognizedScopedEntityExceptionFault(e.getMessage(), usee, e);
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
                        responseKMItem.evaluationRequestKMItem().evaluationRequestDataItem().getExternalFactModelSSId());
                final SSId ssId = SSIdImpl.create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
                final ExitPoint exitPoint = kr.semanticSignifierService().getExitPoint(ssId);
                final ResultSetBuilder<?> resultSetBuilder = kr.semanticSignifierService().getResultSetBuilder(ssId);
                final byte[] rawResult = exitPoint.buildOutput(resultSetBuilder, responseKMItem.resultFactLists(),
                        responseKMItem.evaluationRequestKMItem());
                log.debug("Building output done.");
                final SemanticPayload semanticPayload = createSemanticPayload(encodePayload(rawResult, evalRequestDataItem),
                        DssUtil.makeEI(
                                responseKMItem.evaluationRequestKMItem().evaluationRequestDataItem().getExternalFactModelSSId()));
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

    @Override
    public IterativeEvaluationResponse evaluateIteratively(final InteractionIdentifier interactionId,
            final IterativeEvaluationRequest iterativeEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault, DSSRuntimeExceptionFault
    {
        log.debug("started EvaluationSoapService.evaluateIteratively");
        final IterativeEvaluater evaluater = EvaluationFactory.createIterativeEvaluater(iterativeEvaluationRequest);
        final IterativeEvaluationResponse er = evaluater.getResponse(interactionId, rightNow(), iterativeEvaluationRequest);
        log.debug("completed EvaluationSoapService.evaluateIteratively");
        return er;
    }

    @Override
    public IterativeEvaluationResponse evaluateIterativelyAtSpecifiedTime(final InteractionIdentifier interactionId,
            final XMLGregorianCalendar specifiedTime, final IterativeEvaluationRequest iterativeEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault, DSSRuntimeExceptionFault
    {
        log.debug("started EvaluationSoapService.evaluateIterativelyAtSpecifiedTime");
        final IterativeEvaluater evaluater = EvaluationFactory.createIterativeEvaluater(iterativeEvaluationRequest);
        final IterativeEvaluationResponse er = evaluater.getResponse(interactionId, specifiedTime, iterativeEvaluationRequest);
        log.debug("completed EvaluationSoapService.evaluateIterativelyAtSpecifiedTime");
        return er;
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
        itemIdentifier.setItemId(evalRequestDataItem.getInputItemName() + ".EvaluationResult");
        itemIdentifier.setContainingEntityId(DssUtil.makeEI(evalRequestDataItem.getInputContainingEntityId()));
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
