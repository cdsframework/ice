/**
 * Copyright 2011 - 2013 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.service.evaluate;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
import org.omg.dss.evaluation.requestresponse.DataRequirementItemData;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;
import org.omg.dss.evaluation.requestresponse.KMEvaluationRequest;
import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.InboundPayloadProcessor;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.Payload;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.dss.evaluate.KMEvalRequest;
import org.opencds.dss.util.DssUtil;

/**
 * RequestProcessorService.java
 * 
 * <p>
 * Adapter to process the evaluate operation of the DSS web service: 1. It
 * prepares lists of KMs requested 2. It prepares lists of Data submitted 3. It
 * checks that the submitted data is in the input format required by the
 * requested KM 4. It looks up the evaluation engine required for the requested
 * KM 5. It calls the evaluation engine, passes it the submitted data 6. It
 * feeds the results returned by the evaluation engine back to the requestor
 * 
 * Simply stated, input messages contain a list of rules (Knowledge Modules, or
 * KMs) to run, and structured data to run against those KMs. The submitted data
 * format is both specified by the submitted SemanticSignifierMeta, and
 * validated against metadata for the KMs (which are written for one specific
 * SemanticSignifierMeta).
 * 
 * The primary supported data structure at this time is the HL7 vMR as balloted
 * by HL7 in September, 2011.
 * 
 * Additional structures for the submitted data may be developed, and this code
 * is designed to be extended to additional data models, if needed.
 * 
 * This code is also written to support alternate inferencing engines, and more
 * than one can be active at runtime. The correct inferencing engine is
 * specified in metadata for a KM, and cached for rapid invocation.
 * 
 * <p/>
 * <p>
 * Copyright 2010 - 2013 OpenCDS.org
 * </p>
 * <p>
 * Company: OpenCDS
 * </p>
 * 
 * @author David Shields
 * @version 1.0
 * @date 09-24-2010
 * 
 */
public class RequestProcessorService implements RequestProcessor
{
    private static Log log = LogFactory.getLog(RequestProcessorService.class);

    private final InboundPayloadProcessor inboundPayloadProcessor;
    private Map<String, InboundPayloadProcessor> inboundPayloadProcessorsMap;
    
    public RequestProcessorService(InboundPayloadProcessor inboundPayloadProcessor) {
        this.inboundPayloadProcessor = inboundPayloadProcessor;
    }
    
    private static void validateDRIDataFormat(List<DataRequirementItemData> listDRID)
            throws InvalidDriDataFormatExceptionFault {
        boolean driDataFormatBad = false;
        String msg = "";

        // TODO implement validateDRIDataFormat

        if (driDataFormatBad) {
            throw new InvalidDriDataFormatExceptionFault(msg);
        }
        return;
    }

    private static void validateScopedEntityIDRecognized(List<DataRequirementItemData> listDRID)
            throws UnrecognizedScopedEntityExceptionFault {
        boolean driScopedEntityIDBad = false;
        String msg = "";

        // TODO implement validateScopedEntityIDRecognized

        if (driScopedEntityIDBad) {
            throw new UnrecognizedScopedEntityExceptionFault(msg);
        }
        return;
    }

    private static void validateRequiredDataProvided(List<ItemIdentifier> listFactItemTypes,
            KMEvaluationRequest kmeRequest)
            throws RequiredDataNotProvidedExceptionFault,
            UnrecognizedScopedEntityExceptionFault {
        boolean kmerRequiredDataProvided = true;

        // TODO implement validateRequiredDataProvided, which will fault with a
        // list of all KMDataRequirementItemId values for missing elements
        // EntityIdentifier kmId = kmeRequest.getKmId();

        String msg = "";

        if (kmerRequiredDataProvided) {
            return;
        } else {
            throw new RequiredDataNotProvidedExceptionFault(msg);
        }
    }

    /**
     * Note that there is currently a logical constraint that OpenCDS requires
     * all of the list of KMs (when there is more than one) to use the same
     * inference engine adapter and vmr version.
     * 
     * It is also a constraint that iterative and non-iterative KMs cannot be
     * used in the same request.
     * 
     * big picture pseudo code for this method:
     * 
     * unmarshal the input and save the data as JaxB structures for each
     * requestedKmId { load factLists and ConceptLists from data pass the
     * following to the DecisionEngine specified for the KM: requestedKmId,
     * externalFactModelSSId, inputPayloadString, evalTime, clientLanguage,
     * clientTimeZoneOffset } stack individual results as Base64 data for each
     * requested KmId } return dSSRequestList containing results from all
     * requestedKM
     * 
     * This means that we are considering the OMG-HL7-CDSS concept of
     * KnowledgeModule equivalent to the Drools concept of KnowledgeBase.
     * 
     * It also means that we are only allowing one SSID for a set of one or more
     * KMID.
     */
    @Override
    public List<EvaluationRequestKMItem> decodeInput(KnowledgeRepository knowledgeRepository, KMEvalRequest request,
            EvaluationRequestDataItem evaluationRequestDataItem)
            throws InvalidDriDataFormatExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            UnsupportedLanguageExceptionFault,
            DSSRuntimeExceptionFault {
        log.debug("II: " + evaluationRequestDataItem.getInteractionId() + " starting RequestProcessorService.decodeInput");

        List<EvaluationRequestKMItem> kmItems = new ArrayList<>();
        /*
         * get data from dss wrapper
         */
        List<DataRequirementItemData> listDRIData = new CopyOnWriteArrayList<DataRequirementItemData>(request
                .getEvaluationRequest().getDataRequirementItemData());

        // FIXME flesh out and implement the following
        validateDRIDataFormat(listDRIData);
        validateScopedEntityIDRecognized(listDRIData);

        /**
         * Note that OpenCDS only supports one payload at a time, even though
         * the DSS standard supports more than one.
         */

        // TODO change this code to stack input payloads, possibly including
        // mixing vMR with CCD and/or HL7v2.x data...
        if (listDRIData.size() != 1) {
            log.warn("RequestProcessorService.getInputPayloadString did not have exactly 1 payload.  It had " + listDRIData.size()
                    + " payloads, and only the first one can be used.");
        }

        String inputPayloadString = getInputPayloadString(listDRIData.get(0));

        updateDSSRequestDataItem(evaluationRequestDataItem, request.getEvaluationRequest(), listDRIData, inputPayloadString);
        
        // get SemanticSignifier
        EntityIdentifier ei = request.getEvaluationRequest().getDataRequirementItemData().get(0).getData()
                .getInformationModelSSId();
        SSId ssId = SSIdImpl.create(ei.getScopingEntityId(),ei.getBusinessId(), ei.getVersion());
        SemanticSignifier ss = knowledgeRepository.getSemanticSignifierService().find(ssId);
        if (ss == null) {
            throw new InvalidDriDataFormatException("Unknown/unsupported semantic signifier: " + ssId);
        }

        Object cdsInput = getCdsInput(ss, request, evaluationRequestDataItem, inputPayloadString);
        evaluationRequestDataItem.setCdsInput(cdsInput);

        log.debug("II: " + evaluationRequestDataItem.getInteractionId() + " unmarshalling completed");

        /**
         * get List of Rules to run from dss wrapper
         * 
         */
        validateFactItemTypes(listDRIData, request.getEvaluationRequest().getKmEvaluationRequest());

        log.debug("II: " + evaluationRequestDataItem.getInteractionId() + " input data validated");

        FactListsBuilder flb = knowledgeRepository.getSemanticSignifierService().getFactListsBuilder(ssId);
        
        for (KMEvaluationRequest kmeRequest : request.getEvaluationRequest().getKmEvaluationRequest()) {
            String kmidString = DssUtil.makeEIString(kmeRequest.getKmId());
            KnowledgeModule km = knowledgeRepository.getKnowledgeModuleService().find(kmidString);
            if (km == null) {
                throw new OpenCDSRuntimeException("Unknown KMId : " + kmidString);
            }
            Map<Class<?>, List<?>> allFactLists = flb.buildFactLists(knowledgeRepository, km, cdsInput, request.getEvalTime());
            kmItems.add(new EvaluationRequestKMItem(kmidString, evaluationRequestDataItem, cdsInput, allFactLists));
        }
        
        return kmItems;
    }

    private void updateDSSRequestDataItem(EvaluationRequestDataItem evaluationRequestDataItem, EvaluationRequest evaluationRequest,
            List<DataRequirementItemData> listDRIData, String inputPayloadString) {
        // dssRequestDataItem.setInteractionId(ii.getInteractionId());
        evaluationRequestDataItem.setClientLanguage(evaluationRequest.getClientLanguage());
        evaluationRequestDataItem.setClientTimeZoneOffset(evaluationRequest.getClientTimeZoneOffset());
        evaluationRequestDataItem.setInputItemName(listDRIData.get(0).getDriId().getItemId());
        evaluationRequestDataItem.setInputContainingEntityId(DssUtil.makeEIString(listDRIData.get(0).getDriId()
                .getContainingEntityId()));
        evaluationRequestDataItem.setExternalFactModelSSId(DssUtil.makeEIString(listDRIData.get(0).getData()
                .getInformationModelSSId()));
        evaluationRequestDataItem.setInputPayloadString(inputPayloadString);
    }

    /**
     * @param driData
     * @return
     * @throws DSSRuntimeExceptionFault
     * @throws UnsupportedEncodingException
     */
    private String getInputPayloadString(DataRequirementItemData driData) throws DSSRuntimeExceptionFault {

        // gunzip the data if it has been compressed
        List<byte[]> base64EncodedPayload = DssUtil.gUnzipData(driData);
        // input is automatically decoded from Base64 data by presenting it to
        // us...
        StringBuffer payloadStringBuffer = new StringBuffer();
        try {
            for (byte[] byteList : base64EncodedPayload) {
                // assembles from chunks of encoded data separated by newline,
                // which is fairly common
                payloadStringBuffer.append(new String(byteList, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new DSSRuntimeExceptionFault("RequestProcessorService.getInputPayloadString had UnsupportedEncoding Exception: "
                    + e.getMessage());
        }

        String payloadString = payloadStringBuffer.toString();
        log.trace(payloadString);
        return payloadString;
    }

    private Object getCdsInput(SemanticSignifier ss, KMEvalRequest request, EvaluationRequestDataItem evaluationRequestDataItem, String inputPayloadString)
            throws DSSRuntimeExceptionFault,
            InvalidDriDataFormatExceptionFault,
            RequiredDataNotProvidedExceptionFault {
        try {
        	String modelProcessor = ss.getName();
        	return inboundPayloadProcessorsMap.get( modelProcessor).buildInput(ss, new Payload(inputPayloadString, request.getEvalTime()));
            // return inboundPayloadProcessor.buildInput(ss, new Payload(inputPayloadString, request.getEvalTime()));
        } catch (InvalidDriDataFormatException e) {
            throw new InvalidDriDataFormatExceptionFault(e.getMessage(), e);
        } catch (OpenCDSRuntimeException e) {
            throw new DSSRuntimeExceptionFault(e.getMessage(), e);
        }
    }

    private List<KMEvaluationRequest> validateFactItemTypes(List<DataRequirementItemData> listDRIData,
            List<KMEvaluationRequest> kmEvaluationRequest)
            throws RequiredDataNotProvidedExceptionFault,
            UnrecognizedScopedEntityExceptionFault {
        for (KMEvaluationRequest oneKMEvaluationRequest : kmEvaluationRequest) {
            List<ItemIdentifier> listFactItemTypes = new CopyOnWriteArrayList<ItemIdentifier>();
            for (DataRequirementItemData oneDataRequirementItemData : listDRIData) {
                listFactItemTypes.add(oneDataRequirementItemData.getDriId());
            }
            validateRequiredDataProvided(listFactItemTypes, oneKMEvaluationRequest);
        }
        return kmEvaluationRequest;
    }

	public Map<String, InboundPayloadProcessor> getInboundPayloadProcessorsMap() {
		return inboundPayloadProcessorsMap;
	}

	public void setInboundPayloadProcessorsMap(Map<String, InboundPayloadProcessor> inboundPayloadProcessorsMap) {
		this.inboundPayloadProcessorsMap = inboundPayloadProcessorsMap;
	}

}
