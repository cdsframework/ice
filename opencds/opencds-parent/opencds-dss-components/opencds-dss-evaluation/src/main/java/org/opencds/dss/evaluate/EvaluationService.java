package org.opencds.dss.evaluate;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ItemIdentifier;
import org.omg.dss.common.SemanticPayload;
import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateIteratively;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateIterativelyResponse;
import org.omg.dss.evaluation.EvaluateResponse;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.omg.dss.evaluation.requestresponse.FinalKMEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.KMEvaluationResultData;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.OutboundPayloadProcessor;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.dss.util.DssUtil;
import org.opencds.service.evaluate.RequestProcessor;

/**
 * EvaluationService is the primary entry point for CDS Evaluation.
 * 
 * It gets a list of the required KMId to process from RequestProcessorService
 * and loops through the following: It gets a new instance of
 * requiredEvaluationEngine for each KMId from EvaluationFactory and executes it
 * It gets the evaluationResponse from DSSEvaluationAdapterShell, and stacks it
 * into the FinalEvaluationResponse Finally it returns the
 * FinalEvaluationResponse to the requestor.
 * 
 * @author phillip
 *
 */
public class EvaluationService implements Evaluation {

    private static Log log = LogFactory.getLog(EvaluationService.class);

    private final ConfigurationService configurationService;
    private final RequestProcessor requestProcessor;
    private final EvaluationFactory evaluationFactory;
    private final OutboundPayloadProcessor outboundPayloadProcessor;
    private Map<String, OutboundPayloadProcessor> outboundPayloadProcessorsMap;

    // TODO: Make this configurable
    private final ForkJoinPool evalPool = new ForkJoinPool(128, ForkJoinPool.defaultForkJoinWorkerThreadFactory, new EvaluationExceptionHandler(),
            true);

    public EvaluationService(ConfigurationService configurationService, RequestProcessor requestProcessor,
            EvaluationFactory evaluationFactory, OutboundPayloadProcessor outboundPayloadProcessor) {
        this.configurationService = configurationService;
        this.requestProcessor = requestProcessor;
        this.evaluationFactory = evaluationFactory;
        this.outboundPayloadProcessor = outboundPayloadProcessor;
    }
    
    private static class EvaluationExceptionHandler implements UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("UncaughtException in thread '" + t.getName() + "' message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param date
     *            as long
     * @return XMLGregorianCalendar
     */
    private static XMLGregorianCalendar long2Gregorian(long date) {
        DatatypeFactory dataTypeFactory;
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new OpenCDSRuntimeException(e);
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date);
        return dataTypeFactory.newXMLGregorianCalendar(gc);
    }

    /**
     * 
     * @param date
     * @return XMLGregorianCalendar
     */
    private XMLGregorianCalendar rightNow() {
        return long2Gregorian(new Date().getTime());
    }

    /*
     * (non-Javadoc)
     * 
     * FIXME: extract out the common functionality from evaluate and
     * evaluateAtSpecifiedTime
     * 
     * @see org.omg.dss.Evaluation#evaluate(org.omg.dss.evaluation.Evaluate)
     */
    @Override
    public EvaluateResponse evaluate(Evaluate parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault {
        log.debug("II: " + parameters.getInteractionId().getInteractionId().toString()
                + "EvaluationSoapService.evaluate started");

        List<FinalKMEvaluationResponse> responses = evaluateInternal(parameters.getInteractionId().getInteractionId(),
                parameters.getEvaluationRequest(), XMLDateUtility.xmlGregorian2Date(rightNow()));
        
        EvaluateResponse evaluateResponse = createEvaluateResponse();

        for (FinalKMEvaluationResponse response : responses) {
            evaluateResponse.getEvaluationResponse().getFinalKMEvaluationResponse().add(response);
        }

        log.info("II: " + parameters.getInteractionId().getInteractionId().toString() + " "
                + " EvaluationSoapService.evaluate completed");

        return evaluateResponse;
    }

    private EvaluateResponse createEvaluateResponse() {
        EvaluateResponse evaluateResponse = new EvaluateResponse();
        evaluateResponse.setEvaluationResponse(new EvaluationResponse());
        return evaluateResponse;
    }

    private EvaluateAtSpecifiedTimeResponse createEvaluateAtSpecifiedTimeResponse() {
        EvaluateAtSpecifiedTimeResponse evaluateResponse = new EvaluateAtSpecifiedTimeResponse();
        evaluateResponse.setEvaluationResponse(new EvaluationResponse());
        return evaluateResponse;
    }

    private EvaluationRequestDataItem createEvaluationRequestDataItem(String interactionId, Date evalTime) {
        EvaluationRequestDataItem evalRequestDataItem = new EvaluationRequestDataItem();
        evalRequestDataItem.setInteractionId(interactionId);
        evalRequestDataItem.setEvalTime(evalTime);
        return evalRequestDataItem;
    }

    @Override
    public EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTime(EvaluateAtSpecifiedTime parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault {
        log.debug("II: " + parameters.getInteractionId().getInteractionId()
                + " EvaluationSoapService.evaluateAtSpecifiedTime started");

        List<FinalKMEvaluationResponse> responses = evaluateInternal(parameters.getInteractionId().getInteractionId(),
                parameters.getEvaluationRequest(), XMLDateUtility.xmlGregorian2Date(parameters.getSpecifiedTime()));
        
        EvaluateAtSpecifiedTimeResponse evalAtSpecTimeResponse = createEvaluateAtSpecifiedTimeResponse();
        
        for (FinalKMEvaluationResponse response : responses) {
            evalAtSpecTimeResponse.getEvaluationResponse().getFinalKMEvaluationResponse().add(response);
        }

        log.info("II: " + parameters.getInteractionId().getInteractionId().toString() + " "
                + " EvaluationSoapService.evaluateAtSpecifiedTime completed");

        return evalAtSpecTimeResponse;
    }
    
    private List<FinalKMEvaluationResponse> evaluateInternal(String interactionId, EvaluationRequest evaluationRequest,
            Date specifiedTime)
            throws InvalidDriDataFormatExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            UnsupportedLanguageExceptionFault,
            DSSRuntimeExceptionFault {
        EvaluationRequestDataItem evalRequestDataItem = createEvaluationRequestDataItem(interactionId, specifiedTime);

        KMEvalRequest request = new KMEvalRequest(evaluationRequest, specifiedTime);

        KnowledgeRepository kr = configurationService.getKnowledgeRepository();
        KnowledgeModuleService knowledgeModuleService = kr.getKnowledgeModuleService();
        
        List<EvaluationRequestKMItem> kmEvaluationRequestList = requestProcessor.decodeInput(kr, request,
                evalRequestDataItem);
        
        List<ForkJoinTask<FinalKMEvaluationResponse>> tasks = new ArrayList<>();
        for (EvaluationRequestKMItem oneKMEvaluationRequest : kmEvaluationRequestList) {
            log.debug("Starting evaluation of KM");
           // EvalTask task = new EvalTask(oneKMEvaluationRequest, kr, knowledgeModuleService, evaluationFactory,
           //         outboundPayloadProcessor); // This is when it was just VMR model
           //Switch the outboundProcessor based on the model from the semantic signifier mapping
            EntityIdentifier ei =  evaluationRequest.getDataRequirementItemData().get(0).getData().getInformationModelSSId();
            SSId ssId = SSIdImpl.create(ei.getScopingEntityId(),ei.getBusinessId(), ei.getVersion());
            SemanticSignifier ss = kr.getSemanticSignifierService().find(ssId);
            EvalTask task = new EvalTask(oneKMEvaluationRequest, kr, knowledgeModuleService, evaluationFactory,
                    outboundPayloadProcessorsMap.get(ss.getName()));
            tasks.add(evalPool.submit(task));
        }

        List<FinalKMEvaluationResponse> responses = new ArrayList<>();

        boolean failing = false;
        Throwable t = null;
        for (ForkJoinTask<FinalKMEvaluationResponse> task : tasks) {
            if (!failing) {
                log.debug("Joining on task : " + task.toString());
                task.quietlyJoin();
                t = task.getException();
                if (t == null) {
                    responses.add(task.getRawResult());
                } else {
                    failing = true;
                }
            } else {
                task.cancel(true);
            }
        }
        if (t != null) {
            throw new EvaluationExceptionFault(t.getCause().getMessage(), t.getCause());
        }
        return responses;
    }

    private static class EvalTask implements Callable<FinalKMEvaluationResponse> {
        private final EvaluationRequestKMItem oneRequest;

        private final KnowledgeRepository kr;
        private final KnowledgeModuleService knowledgeModuleService;
        private final EvaluationFactory evaluationFactory;
        private final OutboundPayloadProcessor outboundPayloadProcessor;

        public EvalTask(EvaluationRequestKMItem oneRequest, KnowledgeRepository kr,
                KnowledgeModuleService knowledgeModuleService, EvaluationFactory evaluationFactory,
                OutboundPayloadProcessor outboundPayloadProcessor) {
            this.oneRequest = oneRequest;
            this.kr = kr;
            this.knowledgeModuleService = knowledgeModuleService;
            this.evaluationFactory = evaluationFactory;
            this.outboundPayloadProcessor = outboundPayloadProcessor;
        }

        @Override
        public FinalKMEvaluationResponse call() throws Exception {
            FinalKMEvaluationResponse response = new FinalKMEvaluationResponse();
            String result = "";
            log.debug("Creating evaluator");
            Map<String, List<?>> resultFactLists = evaluationFactory.createEvaluater(kr, oneRequest).getOneResponse(kr,
                    oneRequest);
            log.debug("Building output");
            result = outboundPayloadProcessor.buildOutput(kr, resultFactLists, oneRequest);
            log.debug("Building output done.");

            log.debug("KMId: " + oneRequest.getRequestedKmId()
                    + " EvaluationService.evaluateAtSpecifiedTime starting one KM");
            log.trace("" + result);

            ItemIdentifier itemId = createItemIdentifier(oneRequest.getEvaluationRequestDataItem());

            KnowledgeModule km = knowledgeModuleService.find(oneRequest.getRequestedKmId());

            SemanticPayload semanticPayload = createSemanticPayload(DssUtil.gZipData(result.getBytes(), oneRequest.getEvaluationRequestDataItem()), km.getSSId());

            KMEvaluationResultData kmerData = createKMEvaluationResultData(semanticPayload, itemId);

            response.setKmId(DssUtil.makeEI(oneRequest.getRequestedKmId()));
            response.getKmEvaluationResultData().add(kmerData);

            log.debug("KMId: " + oneRequest.getRequestedKmId()
                    + " EvaluationService.evaluateAtSpecifiedTime completed one KM");

            log.debug("Adding response for KM");
            log.debug("Finished evaluation of KM");
            return response;
        }

        private ItemIdentifier createItemIdentifier(EvaluationRequestDataItem evalRequestDataItem) {
            ItemIdentifier itemIdentifier = new ItemIdentifier();
            itemIdentifier.setItemId(evalRequestDataItem.getInputItemName() + ".EvaluationResult");
            itemIdentifier.setContainingEntityId(DssUtil.makeEI(evalRequestDataItem.getInputContainingEntityId()));
            return itemIdentifier;
        }

        private KMEvaluationResultData createKMEvaluationResultData(SemanticPayload payload, ItemIdentifier itemId) {
            KMEvaluationResultData kmerData = new KMEvaluationResultData();
            kmerData.setData(payload);
            kmerData.setEvaluationResultId(itemId);
            return kmerData;
        }

        private SemanticPayload createSemanticPayload(byte[] payload, SSId ssId) {
            SemanticPayload semanticPayload = new SemanticPayload();
            semanticPayload.setInformationModelSSId(DssUtil.makeEI(ssId.getScopingEntityId(), ssId.getBusinessId(), ssId.getVersion()));
            semanticPayload.getBase64EncodedPayload().add(payload);
            // TODO: Verify that this is indeed the data model
            // semanticPayload.setInformationModelSSId(DssUtil.makeEIFromCommon(entityIdentifier.getEntityId()));
            return semanticPayload;
        }

    }

    @Override
    public EvaluateIterativelyResponse evaluateIteratively(EvaluateIteratively parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault {
        log.debug("started EvaluationSoapService.evaluateIteratively");
        IterativeEvaluater evaluater = EvaluationFactory.createIterativeEvaluater(parameters
                .getIterativeEvaluationRequest());
        EvaluateIterativelyResponse er = new EvaluateIterativelyResponse();
        er.setIterativeEvaluationResponse(evaluater.getResponse(parameters.getInteractionId(), rightNow(),
                parameters.getIterativeEvaluationRequest()));
        log.debug("completed EvaluationSoapService.evaluateIteratively");
        return er;
    }

    @Override
    public EvaluateIterativelyAtSpecifiedTimeResponse evaluateIterativelyAtSpecifiedTime(
            EvaluateIterativelyAtSpecifiedTime parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault {
        log.debug("started EvaluationSoapService.evaluateIterativelyAtSpecifiedTime");
        IterativeEvaluater evaluater = EvaluationFactory.createIterativeEvaluater(parameters
                .getIterativeEvaluationRequest());
        EvaluateIterativelyAtSpecifiedTimeResponse er = new EvaluateIterativelyAtSpecifiedTimeResponse();
        er.setIterativeEvaluationResponse(evaluater.getResponse(parameters.getInteractionId(),
                parameters.getSpecifiedTime(), parameters.getIterativeEvaluationRequest()));
        log.debug("completed EvaluationSoapService.evaluateIterativelyAtSpecifiedTime");
        return er;
    }

	public Map<String, OutboundPayloadProcessor> getOutboundPayloadProcessorsMap() {
		return outboundPayloadProcessorsMap;
	}

	public void setOutboundPayloadProcessorsMap(Map<String, OutboundPayloadProcessor> outboundPayloadProcessorsMap) {
		this.outboundPayloadProcessorsMap = outboundPayloadProcessorsMap;
	}

}
