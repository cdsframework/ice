/*
 * Copyright 2015-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.omg.dss.evaluation.requestresponse.DataRequirementItemData;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.omg.dss.evaluation.requestresponse.FinalKMEvaluationResponse;
import org.omg.dss.evaluation.requestresponse.KMEvaluationResultData;
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

/**
 * DSSEvaluation is the primary entry point for CDS Evaluation.
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
public class DSSEvaluation implements Evaluation {

	static Log log = LogFactory.getLog(DSSEvaluation.class);

	private final EvaluationService evaluationService;
	private final ConfigurationService configurationService;
	private final RequestProcessor requestProcessor;

	public DSSEvaluation(EvaluationService evaluationService, ConfigurationService configurationService,
			RequestProcessor requestProcessor) {
		this.evaluationService = evaluationService;
		this.configurationService = configurationService;
		this.requestProcessor = requestProcessor;
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
	public EvaluateResponse evaluate(Evaluate parameters) throws InvalidDriDataFormatExceptionFault,
			UnrecognizedLanguageExceptionFault, RequiredDataNotProvidedExceptionFault,
			UnsupportedLanguageExceptionFault, UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault,
			InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault {
		log.debug("II: " + parameters.getInteractionId().getInteractionId().toString()
				+ "EvaluationSoapService.evaluate started");

		List<FinalKMEvaluationResponse> responses = evaluateInternal(configurationService.getKnowledgeRepository(),
				parameters.getInteractionId().getInteractionId(), parameters.getEvaluationRequest(),
				XMLDateUtility.xmlGregorian2Date(rightNow()));

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

	private EvaluationRequestDataItem createEvaluationRequestDataItem(EvaluationRequest evaluationRequest,
			List<DataRequirementItemData> listDRIData, String interactionId, Date evalTime) {
		EvaluationRequestDataItem evalRequestDataItem = new EvaluationRequestDataItem();
		evalRequestDataItem.setInteractionId(interactionId);
		evalRequestDataItem.setEvalTime(evalTime);
		// dssRequestDataItem.setInteractionId(ii.getInteractionId());
		evalRequestDataItem.setClientLanguage(evaluationRequest.getClientLanguage());
		evalRequestDataItem.setClientTimeZoneOffset(evaluationRequest.getClientTimeZoneOffset());
		evalRequestDataItem.setInputItemName(listDRIData.get(0).getDriId().getItemId());
		evalRequestDataItem.setInputContainingEntityId(
				DssUtil.makeEIString(listDRIData.get(0).getDriId().getContainingEntityId()));
		evalRequestDataItem
				.setExternalFactModelSSId(DssUtil.makeEIString(listDRIData.get(0).getData().getInformationModelSSId()));
		return evalRequestDataItem;
	}

	@Override
	public EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTime(EvaluateAtSpecifiedTime parameters)
			throws InvalidDriDataFormatExceptionFault, UnrecognizedLanguageExceptionFault,
			RequiredDataNotProvidedExceptionFault, UnsupportedLanguageExceptionFault,
			UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, InvalidTimeZoneOffsetExceptionFault,
			DSSRuntimeExceptionFault {
		log.debug("II: " + parameters.getInteractionId().getInteractionId()
				+ " EvaluationSoapService.evaluateAtSpecifiedTime started");

		List<FinalKMEvaluationResponse> responses = evaluateInternal(configurationService.getKnowledgeRepository(),
				parameters.getInteractionId().getInteractionId(), parameters.getEvaluationRequest(),
				XMLDateUtility.xmlGregorian2Date(parameters.getSpecifiedTime()));

		EvaluateAtSpecifiedTimeResponse evalAtSpecTimeResponse = createEvaluateAtSpecifiedTimeResponse();

		for (FinalKMEvaluationResponse response : responses) {
			evalAtSpecTimeResponse.getEvaluationResponse().getFinalKMEvaluationResponse().add(response);
		}

		log.info("II: " + parameters.getInteractionId().getInteractionId().toString() + " "
				+ " EvaluationSoapService.evaluateAtSpecifiedTime completed");

		return evalAtSpecTimeResponse;
	}

	private List<FinalKMEvaluationResponse> evaluateInternal(KnowledgeRepository kr, String interactionId,
			EvaluationRequest evaluationRequest, Date specifiedTime) throws InvalidDriDataFormatExceptionFault,
					RequiredDataNotProvidedExceptionFault, EvaluationExceptionFault,
					InvalidTimeZoneOffsetExceptionFault, UnrecognizedScopedEntityExceptionFault,
					UnrecognizedLanguageExceptionFault, UnsupportedLanguageExceptionFault, DSSRuntimeExceptionFault {
		List<DataRequirementItemData> listDRIData = new CopyOnWriteArrayList<DataRequirementItemData>(
				evaluationRequest.getDataRequirementItemData());

		EvaluationRequestDataItem evalRequestDataItem = createEvaluationRequestDataItem(evaluationRequest, listDRIData,
				interactionId, specifiedTime);

		List<EvaluationResponseKMItem> responseKMItems = new ArrayList<>();
		try {
			List<EvaluationRequestKMItem> evaluationRequestKMItems = requestProcessor.decodeInput(kr, evaluationRequest,
					evalRequestDataItem, listDRIData, specifiedTime);

			responseKMItems = evaluationService.evaluate(kr, evaluationRequestKMItems);
		} catch (EvaluationException e) {
			e.printStackTrace();
			org.omg.dss.exception.EvaluationException ee = new org.omg.dss.exception.EvaluationException();
			// TODO/FIXME will have to put the KMId into the message later...
			throw new EvaluationExceptionFault(e.getCause().getMessage(), ee, e.getCause());
		} catch (UnrecognizedScopedEntityException e) {
			org.omg.dss.exception.UnrecognizedScopedEntityException usee = new org.omg.dss.exception.UnrecognizedScopedEntityException();
			throw new UnrecognizedScopedEntityExceptionFault(e.getMessage(), usee, e);
		} catch (OpenCDSRuntimeException e) {
			throw new DSSRuntimeExceptionFault(e.getMessage(), e);
		}

		List<FinalKMEvaluationResponse> responses = new ArrayList<>();
		try {
			for (EvaluationResponseKMItem responseKMItem : responseKMItems) {
				log.debug("Building output");
				EntityIdentifier ei = DssUtil.makeEI(responseKMItem.getEvaluationRequestKMItem().getEvaluationRequestDataItem().getExternalFactModelSSId());
				SSId ssId = SSIdImpl.create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion());
				ExitPoint exitPoint = kr.getSemanticSignifierService().getExitPoint(ssId);
				ResultSetBuilder<?> resultSetBuilder = kr.getSemanticSignifierService().getResultSetBuilder(ssId);
				byte[] rawResult = exitPoint.buildOutput(resultSetBuilder, responseKMItem.getResultFactLists(),
						responseKMItem.getEvaluationRequestKMItem());
				log.debug("Building output done.");
				SemanticPayload semanticPayload = createSemanticPayload(encodePayload(rawResult, evalRequestDataItem),
						DssUtil.makeEI(responseKMItem.getEvaluationRequestKMItem().getEvaluationRequestDataItem()
								.getExternalFactModelSSId()));
				ItemIdentifier itemId = createItemIdentifier(evalRequestDataItem);
				KMEvaluationResultData kmerData = createKMEvaluationResultData(semanticPayload, itemId);

				FinalKMEvaluationResponse response = new FinalKMEvaluationResponse();
				response.setKmId(DssUtil.makeEI(responseKMItem.getEvaluationRequestKMItem().getRequestedKmId()));
				response.getKmEvaluationResultData().add(kmerData);

				log.debug("KMId: " + responseKMItem.getEvaluationRequestKMItem().getRequestedKmId()
						+ " DSSEvaluation.evaluateAtSpecifiedTime completed one KM");
				log.debug("Adding response for KM");
				responses.add(response);
				log.debug("Finished evaluation of KM");
			}
		} catch (OpenCDSRuntimeException e) {
			throw new DSSRuntimeExceptionFault(e.getMessage(), e);
		}
		return responses;
	}

	@Override
	public EvaluateIterativelyResponse evaluateIteratively(EvaluateIteratively parameters)
			throws InvalidDriDataFormatExceptionFault, UnrecognizedLanguageExceptionFault,
			RequiredDataNotProvidedExceptionFault, UnsupportedLanguageExceptionFault,
			UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, InvalidTimeZoneOffsetExceptionFault,
			DSSRuntimeExceptionFault {
		log.debug("started EvaluationSoapService.evaluateIteratively");
		// FIXME: rebuild support using EvaluationService
		IterativeEvaluater evaluater = EvaluationFactory
				.createIterativeEvaluater(parameters.getIterativeEvaluationRequest());
		EvaluateIterativelyResponse er = new EvaluateIterativelyResponse();
		er.setIterativeEvaluationResponse(evaluater.getResponse(parameters.getInteractionId(), rightNow(),
				parameters.getIterativeEvaluationRequest()));
		log.debug("completed EvaluationSoapService.evaluateIteratively");
		return er;
	}

	@Override
	public EvaluateIterativelyAtSpecifiedTimeResponse evaluateIterativelyAtSpecifiedTime(
			EvaluateIterativelyAtSpecifiedTime parameters) throws InvalidDriDataFormatExceptionFault,
					UnrecognizedLanguageExceptionFault, RequiredDataNotProvidedExceptionFault,
					UnsupportedLanguageExceptionFault, UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault,
					InvalidTimeZoneOffsetExceptionFault, DSSRuntimeExceptionFault {
		log.debug("started EvaluationSoapService.evaluateIterativelyAtSpecifiedTime");
		// FIXME: rebuild support using EvaluationService
		IterativeEvaluater evaluater = EvaluationFactory
				.createIterativeEvaluater(parameters.getIterativeEvaluationRequest());
		EvaluateIterativelyAtSpecifiedTimeResponse er = new EvaluateIterativelyAtSpecifiedTimeResponse();
		er.setIterativeEvaluationResponse(evaluater.getResponse(parameters.getInteractionId(),
				parameters.getSpecifiedTime(), parameters.getIterativeEvaluationRequest()));
		log.debug("completed EvaluationSoapService.evaluateIterativelyAtSpecifiedTime");
		return er;
	}

	private KMEvaluationResultData createKMEvaluationResultData(SemanticPayload payload, ItemIdentifier itemId) {
		KMEvaluationResultData kmerData = new KMEvaluationResultData();
		kmerData.setData(payload);
		kmerData.setEvaluationResultId(itemId);
		return kmerData;
	}

	private ItemIdentifier createItemIdentifier(EvaluationRequestDataItem evalRequestDataItem) {
		ItemIdentifier itemIdentifier = new ItemIdentifier();
		itemIdentifier.setItemId(evalRequestDataItem.getInputItemName() + ".EvaluationResult");
		itemIdentifier.setContainingEntityId(DssUtil.makeEI(evalRequestDataItem.getInputContainingEntityId()));
		return itemIdentifier;
	}

	private SemanticPayload createSemanticPayload(byte[] payload, EntityIdentifier eid) {
		SemanticPayload semanticPayload = new SemanticPayload();
		semanticPayload.setInformationModelSSId(eid);
		semanticPayload.getBase64EncodedPayload().add(payload);
		return semanticPayload;
	}

	/**
     * Gzip outgoing payload before base64 encoding if containing entity id has the gzip
     * designation.
	 */
	public byte[] encodePayload(byte[] payload, EvaluationRequestDataItem evaluationRequestDataItem) throws DSSRuntimeExceptionFault {
		byte[] result;
		if (DssUtil.isGZipDesignated(evaluationRequestDataItem)) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(payload.length);
			try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
				gzipOutputStream.write(payload);
			} catch (IOException e) {
				throw new DSSRuntimeExceptionFault("Error compressing payload: " + e.getMessage(), e);
			}
			result = byteArrayOutputStream.toByteArray();
		} else {
			result = payload;
		}
		return result;
	}

}
