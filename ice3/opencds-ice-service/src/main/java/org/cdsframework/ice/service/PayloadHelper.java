/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationReason;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.drools.spi.KnowledgeHelper;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.EvalTime;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

public class PayloadHelper {

	private Schedule backingSchedule;
	
	private static Log logger = LogFactory.getLog(PayloadHelper.class);
	// TODO: CDSOutput Template codes... Make configurable

	
	/**
	 * Initialize the PayloadHelper with the backing schedule
	 * @param pS Schedule backing this patient's evaluation and forecast
	 * @IllegalArgumentException if supplied schedule is null or has not been initialized
	 */
	public PayloadHelper(Schedule pS) {
		
		if (pS == null || pS.isScheduleInitialized() == false) {
			String lExStr = "Schedule has not been provided or has not been initialized; cannot continue";
			logger.error("PayloadHelper(): " + lExStr);
			throw new IllegalArgumentException(lExStr);
		}
		
		this.backingSchedule = pS;
	}
	

	public void OutputNestedImmEvaluationResult(KnowledgeHelper k, java.util.HashMap pNamedObjects, EvalTime evalTime, String focalPersonId, String cdsSource, SubstanceAdministrationEvent sae, String vg, TargetDose d) {

		String _METHODNAME = "OutputNestedImmEvaluationResult: ";
		if (k == null || pNamedObjects == null || evalTime == null || sae == null || d == null) {
			String str = "Caller supplied either NULL KnowledgeHelper, NamedObject HashMap, evalTime, SubstanceAdministrationEvent or TargetDose parameter";
			logger.warn(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}

		if (logger.isDebugEnabled()) {
			String str = "focalPersonId " + focalPersonId + ", sae: " + sae.getId() + ", VG: " + vg + ", Dose unique ID: " + d.getUniqueId() + ", Dose ID " + d.getDoseId() +
					", Dose all: " + d.toString();
			logger.debug(str);
		}
		String conceptTargetId = sae.getId();
		
		// Embedded SubstanceAdministrationEvent
		SubstanceAdministrationEvent lSAE = new SubstanceAdministrationEvent();
		String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
		lSAE.setId(uniqueSarIdValue);
		String[] subsAdmEvtTemplateArr = { "2.16.840.1.113883.3.795.11.9.1.1" };
		lSAE.setTemplateId(subsAdmEvtTemplateArr);
		lSAE.setEvaluatedPersonId(focalPersonId); 
		lSAE.setSubjectIsFocalPerson(true);

		// Record Substance AdministrationEvent CDS System Data Source
		if (cdsSource != null &&  cdsSource.isEmpty() == false) {
			CD cdsDataSource = new CD();
			cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
			cdsDataSource.setCodeSystemName("org.cdsframework source");
			cdsDataSource.setCode(cdsSource);
			lSAE.setDataSourceType(cdsDataSource);
		}
		
		// Substance Proposal General Purpose
		CD subsAdmGeneralPurposeCD = new CD();
		subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
		subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
		subsAdmGeneralPurposeCD.setCode("384810002");
		subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
		lSAE.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);
		// Dose information
		INT lINTDoseNumber = new INT();
		lINTDoseNumber.setValue(d.getDoseNumberCount());
		lSAE.setDoseNumber(lINTDoseNumber);
		// Administration Time Interval
		lSAE.setAdministrationTimeInterval(sae.getAdministrationTimeInterval());
		// Validity flag
		BL lSAEValidity = new BL();
		lSAEValidity.setValue(d.getIsValid());
		lSAE.setIsValid(lSAEValidity);
		// AdministrableSubstance
		AdministrableSubstance lAS = new AdministrableSubstance();
		lAS.setId(ICELogicHelper.generateUniqueString());
		CD asSubstanceCode = new CD();
		// Get the associated vaccine concept associated with the TargetDose
		LocallyCodedCdsListItem lSVC = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsConcepts().getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS, d.getVaccineComponent().getCdsConcept());
		if (lSVC == null) {
			String lErrStr = "LocallyCodedCdsListItem Vaccine not found for specified TargetDose; this should not occur";
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		asSubstanceCode.setCodeSystem(lSVC.getCdsListCodeSystem());
		asSubstanceCode.setCode(lSVC.getCdsListItemKey());
		asSubstanceCode.setDisplayName(lSVC.getCdsListItemValue());
		lAS.setSubstanceCode(asSubstanceCode);
		lAS.setToBeReturned(true);
		lSAE.setSubstance(lAS);

		// This is a nested clinical statement
		lSAE.setClinicalStatementToBeRoot(false);
		lSAE.setToBeReturned(true);
		// k.insert(lSAE);
		k.insert(lSAE);
		pNamedObjects.put("lSAE" + uniqueSarIdValue, lSAE);

		// Therefore, create as a relatedClinicalStatement
		ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
		rel.setSourceId(conceptTargetId);
		rel.setTargetId(uniqueSarIdValue);
		CD relCodeSR = new CD();
		relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
		relCodeSR.setCode("PERT");
		relCodeSR.setDisplayName("has pertinent information");
		rel.setTargetRelationshipToSource(relCodeSR);
		// k.insert(rel);
		k.insert(rel);
		pNamedObjects.put("rel" + uniqueSarIdValue, rel);

		//
		// Observation
		//
		String nestedIdValue = ICELogicHelper.generateUniqueString();
		ObservationResult childObs = new ObservationResult();
		String[] obsTemplateArr = { "2.16.840.1.113883.3.795.11.6.1.1" };
		childObs.setTemplateId(obsTemplateArr);

		// Eval Time
		IVLDate obsTime = new IVLDate(); 
		obsTime.setLow(evalTime.getEvalTimeValue()); 
		obsTime.setHigh(evalTime.getEvalTimeValue()); 
		childObs.setId(nestedIdValue);
		childObs.setEvaluatedPersonId(focalPersonId);
		childObs.setObservationEventTime(obsTime);
		childObs.setSubjectIsFocalPerson(true); 

		// Observation Focus
		CD localCD = getLocalCodeForEvaluationConcept(vg);
		childObs.setObservationFocus(localCD);

		// Observation Value
		DoseStatus doseStatus = d.getStatus();
		CD localObsCD = getLocalCodeForEvaluationStatus(doseStatus, this.backingSchedule);
		ObservationValue childObsValue = new ObservationValue();
		childObsValue.setConcept(localObsCD);
		childObs.setObservationValue(childObsValue);

		// Observation interpretation
		List<CD> interpretations = new ArrayList<CD>();
		if (doseStatus == DoseStatus.INVALID) {
			for (String interp : d.getInvalidReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp, this.backingSchedule);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			if (interpretations.size() > 0)
				childObs.setInterpretation(interpretations);
		}
		else if (doseStatus == DoseStatus.NOT_EVALUATED) {
			for (String interp : d.getNotEvaluatedReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp, this.backingSchedule);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			if (interpretations.size() > 0)
				childObs.setInterpretation(interpretations);
		}
		else if (doseStatus == DoseStatus.ACCEPTED || doseStatus == DoseStatus.VALID) {
			for (String interp : d.getAcceptedReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp, this.backingSchedule);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			for (String interp : d.getValidReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp, this.backingSchedule);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			if (interpretations.size() > 0)
				childObs.setInterpretation(interpretations);
		}

		// This is a nested clinical statement
		childObs.setClinicalStatementToBeRoot(false);
		childObs.setToBeReturned(true);
		/////// k.insert(childObs);
		k.insert(childObs);
		pNamedObjects.put("childObs" + nestedIdValue, childObs);

		// Therefore, create as a relatedClinicalStatement
		ClinicalStatementRelationship relO = new ClinicalStatementRelationship();
		relO.setSourceId(uniqueSarIdValue);
		relO.setTargetId(nestedIdValue);
		CD relCodeSO = new CD();
		relCodeSO.setCodeSystem("2.16.840.1.113883.5.1002");
		relCodeSO.setCode("PERT");
		relCodeSO.setDisplayName("has pertinent information");
		rel.setTargetRelationshipToSource(relCodeSO);
		/////// k.insert(relO);
		k.insert(relO);
		pNamedObjects.put("rel" + nestedIdValue, relO);
	}

	public void OutputNestedImmEvaluationNotSupported(KnowledgeHelper k, java.util.HashMap pNamedObjects, EvalTime evalTime, String focalPersonId, String cdsSource, SubstanceAdministrationEvent sae, String vg) {

		String _METHODNAME = "OutputNestedImmEvaluationNotSupported: ";
		if (k == null || pNamedObjects == null || evalTime == null || sae == null || vg == null) {
			String str = "Caller supplied either NULL KnowledgeHelper, NamedObject HashMap, evalTime, or SubstanceAdministrationEvent parameter";
			logger.warn(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}

		if (logger.isDebugEnabled()) {
			String str = "focalPersonId " + focalPersonId + ", sae: " + sae.getId() + ", VG: " + vg;
			logger.debug(str);
		}
		String conceptTargetId = sae.getId();

		// SubstanceAdministrationEvent
		SubstanceAdministrationEvent lSAE = new SubstanceAdministrationEvent();
		String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
		lSAE.setId(uniqueSarIdValue);
		String[] subsAdmEvtTemplateArr = { "2.16.840.1.113883.3.795.11.9.1.1" };
		lSAE.setTemplateId(subsAdmEvtTemplateArr);
		lSAE.setEvaluatedPersonId(focalPersonId); 
		lSAE.setSubjectIsFocalPerson(true);

		// Record Substance AdministrationEvent CDS System Data Source
		if (cdsSource != null &&  cdsSource.isEmpty() == false) {
			CD cdsDataSource = new CD();
			cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
			cdsDataSource.setCodeSystemName("org.cdsframework source");
			cdsDataSource.setCode(cdsSource);
			lSAE.setDataSourceType(cdsDataSource);
		}

		// Substance Proposal General Purpose
		CD subsAdmGeneralPurposeCD = new CD();
		subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
		subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
		subsAdmGeneralPurposeCD.setCode("384810002");
		subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
		lSAE.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);
		// Administration Time Interval
		lSAE.setAdministrationTimeInterval(sae.getAdministrationTimeInterval());
		// AdministrableSubstance sae
		AdministrableSubstance lsaeAS = sae.getSubstance();
		// AdministrableSubstance (new)
		AdministrableSubstance lAS = lsaeAS;
		lAS.setToBeReturned(true);
		lSAE.setSubstance(lAS);

		// This is a nested clinical statement
		lSAE.setClinicalStatementToBeRoot(false);
		lSAE.setToBeReturned(true);
		/////// k.insert(lSAE);
		k.insert(lSAE);
		pNamedObjects.put("lSAE" + uniqueSarIdValue, lSAE);

		// Therefore, create as a relatedClinicalStatement
		ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
		rel.setSourceId(conceptTargetId);
		rel.setTargetId(uniqueSarIdValue);
		CD relCodeSR = new CD();
		relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
		relCodeSR.setCode("PERT");
		relCodeSR.setDisplayName("has pertinent information");
		rel.setTargetRelationshipToSource(relCodeSR);
		/////// k.insert(rel);
		k.insert(rel);
		pNamedObjects.put("rel" + uniqueSarIdValue, rel);

		//
		// Observation
		//
		String nestedIdValue = ICELogicHelper.generateUniqueString();
		ObservationResult childObs = new ObservationResult();
		String[] obsTemplateArr = { "2.16.840.1.113883.3.795.11.6.1.1" };
		childObs.setTemplateId(obsTemplateArr);

		// Eval Time
		IVLDate obsTime = new IVLDate(); 
		obsTime.setLow(evalTime.getEvalTimeValue()); 
		obsTime.setHigh(evalTime.getEvalTimeValue()); 
		childObs.setId(nestedIdValue);
		childObs.setEvaluatedPersonId(focalPersonId);
		childObs.setObservationEventTime(obsTime);
		childObs.setSubjectIsFocalPerson(true); 

		// Observation Focus
		CD localCD = getLocalCodeForEvaluationConcept(vg);
		childObs.setObservationFocus(localCD);

		// Observation Value
		DoseStatus doseStatus = DoseStatus.NOT_EVALUATED;
		CD localObsCD = getLocalCodeForEvaluationStatus(doseStatus, this.backingSchedule);
		ObservationValue childObsValue = new ObservationValue();
		childObsValue.setConcept(localObsCD);
		childObs.setObservationValue(childObsValue);

		// Observation interpretation
		List<CD> interpretations = new ArrayList<CD>();
		interpretations.add(getLocalCodeForEvaluationReason(BaseDataEvaluationReason._VACCINE_NOT_SUPPORTED_REASON.getCdsListItemName(), this.backingSchedule));  // "EVALUATION_REASON_CONCEPT.VACCINE_NOT_SUPPORTED"
		childObs.setInterpretation(interpretations);

		// This is a nested clinical statement
		childObs.setClinicalStatementToBeRoot(false);
		childObs.setToBeReturned(true);
		/////// k.insert(childObs);
		k.insert(childObs);
		pNamedObjects.put("childObs" + nestedIdValue, childObs);

		// Therefore, create as a relatedClinicalStatement
		ClinicalStatementRelationship relO = new ClinicalStatementRelationship();
		relO.setSourceId(uniqueSarIdValue);
		relO.setTargetId(nestedIdValue);
		CD relCodeSO = new CD();
		relCodeSO.setCodeSystem("2.16.840.1.113883.5.1002");
		relCodeSO.setCode("PERT");
		relCodeSO.setDisplayName("has pertinent information");
		rel.setTargetRelationshipToSource(relCodeSO);
		/////// k.insert(relO);
		k.insert(relO);
		pNamedObjects.put("rel" + nestedIdValue, relO);
	}

	
	/**
	 * This method creates a root SubstanceAdministrationProposal in the following format
		   <substanceAdministrationProposal>
		       <templateId root="2.16.840.1.113883.3.795.11.9.3.1"/>
		       <id root="47e4ad13-e11d-4ca5-ae63-93619baa3e92"/>
		       <substanceAdministrationGeneralPurpose displayName="Immunization/vaccination management (procedure)" codeSystemName="SNOMED CT" codeSystem="2.16.840.1.113883.6.5" code="384810002"/>
		       <substance>
		           <id root="051e605d-46a3-4f2b-8e4e-1901cf4eca9b"/>
		           <substanceCode displayName="TBD - vaccine group: 810 - substance code: 104" codeSystem="2.16.840.1.113883.12.292" code="810"/>
		       </substance>
		       <validAdministrationTimeInterval low="20101201"/>	<!-- earliest date a shot can be administered (only returned if option enabled) -->
		       <proposedAdministrationTimeInterval low="20111201" high="20121201"/>		<!-- recommended forecast date and latest recommended forecast date (latest only if option enabled; otherwise it is set to same value as forecast date) -->
		       <relatedClinicalStatement>
		           <targetRelationshipToSource codeSystem="2.16.840.1.113883.5.1002" code="RSON" displayName="has reason"/>
		           <observationResult>
		              <templateId root="2.16.840.1.113883.10.20.1.31"/>
		              <id root="1815afc5-3b70-4b7b-bc64-4af8cb424b5a"/>
		              <observationFocus code="PROPOSAL (HEP A)" codeSystem="2.16.840.1.113883.3.795.12.100.4"/>
		              <observationValue>
		                  <concept displayName="TBD" codeSystem="2.16.840.1.113883.3.795.12.100.5" code="RECOMMENDED"/>
		              </observationValue>
		              <interpretation displayName="TBD" codeSystem="2.16.840.1.113883.3.795.12.100.6" code="DUE_NOW"/>
		           </observationResult>
		       </relatedClinicalStatement>
		   </substanceAdministrationProposal>
	 */
	public void OutputRootImmRecommendationSubstanceAdministrationProposal(KnowledgeHelper drools, java.util.HashMap pNamedObjects, String focalPersonId, String cdsSource, TargetSeries ts, boolean outputEarliestOverdue, boolean outputSupplementalText) 
		throws ImproperUsageException, InconsistentConfigurationException {

		String _METHODNAME = "OutputRootImmRecommendationSubstanceAdministrationProposal: ";

		if (ts == null || pNamedObjects == null || drools == null || ts.getTargetSeriesIdentifier() == null) {
			String lErrStr = "Error outputting SubstanceAdministrationProposal: one or more method parameters not initialized";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		SubstanceAdministrationProposal sap = new SubstanceAdministrationProposal();
		// String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
		String uniqueSarIdValue = ts.getTargetSeriesIdentifier();
		sap.setId(uniqueSarIdValue);
		String[] subsAdmPropTemplateArr = { "2.16.840.1.113883.3.795.11.9.3.1" };
		sap.setTemplateId(subsAdmPropTemplateArr);
		sap.setEvaluatedPersonId(focalPersonId); 
		sap.setSubjectIsFocalPerson(true);
		
		// Substance Administration Proposal CDS System Data Source
		if (cdsSource != null && ! cdsSource.isEmpty()) {
			CD cdsDataSource = new CD();
			cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
			cdsDataSource.setCodeSystemName("org.cdsframework source");
			cdsDataSource.setCode(cdsSource);
			sap.setDataSourceType(cdsDataSource);
		}
		
		// Substance Proposal General Purpose
		CD subsAdmGeneralPurposeCD = new CD();
		subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
		subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
		subsAdmGeneralPurposeCD.setCode("384810002");
		subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
		sap.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);

		//////////////
		// Set Earliest valid date, recommendation date and/or latest recommendation date
		//////////////
		Date finalEarliestDate = ts.getFinalEarliestDate();
		Date finalRecommendationDate = ts.getFinalRecommendationDate();
		if (! outputEarliestOverdue) {
			// Only recommended forecast date should be set
			if (finalRecommendationDate != null) {
				IVLDate obsTime = new IVLDate();
				obsTime.setLow(finalRecommendationDate);
				obsTime.setHigh(finalRecommendationDate);
				sap.setProposedAdministrationTimeInterval(obsTime);
			}
		}
		else {		
			// Earliest, recommended and latest recommended should be set
			Date finalLatestRecommendationDate = ts.getFinalOverdueDate();
			Date finalLatestDate = null;		// We do not support returning "latest" possible date separately in payload, as of now
			if (finalRecommendationDate != null || finalLatestRecommendationDate != null) {
				IVLDate obsTime = new IVLDate();
				if (finalRecommendationDate != null) {
					obsTime.setLow(finalRecommendationDate);
				}
				else {
					// (This should not happen; however, since an earliest recommendation date, will just use the latest date. Log that this occurred.
					String lWarnStr = "No earliest recommendation date was calculated but a latest recommendation date was calculated! This should not happen";
					logger.warn(_METHODNAME + lWarnStr);
				}
				if (finalLatestRecommendationDate != null) {
					obsTime.setHigh(finalLatestRecommendationDate);
				}
				sap.setProposedAdministrationTimeInterval(obsTime);
			}
			if (finalEarliestDate != null || finalLatestDate != null) {
				IVLDate obsTime = new IVLDate();
				if (finalEarliestDate != null) {
					obsTime.setLow(finalEarliestDate);
				}
				if (finalLatestDate != null) {
					obsTime.setHigh(finalLatestDate);
				}
				sap.setValidAdministrationTimeInterval(obsTime);	
			}
		}
		// Set the AdministrableSubstance - may be a vaccine or a vaccine group
		CD vaccGroupCode = getLocalCodeConceptForRecommendationConcept(ts, true);
		AdministrableSubstance substance = new AdministrableSubstance();
		substance.setId(ICELogicHelper.generateUniqueString());
		substance.setSubstanceCode(vaccGroupCode);
		sap.setSubstance(substance);

		// Set as a root clinical statement
		sap.setClinicalStatementToBeRoot(true); 
		sap.setToBeReturned(true); 	
		drools.insert(sap);
		pNamedObjects.put("sap" + uniqueSarIdValue, sap);

		// Now create the nested observation result
		String nestedIdValue = ICELogicHelper.generateUniqueString();
		// Observation
		ObservationResult childObs = new ObservationResult();
		childObs.setId(nestedIdValue);
		String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
		sap.setTemplateId(observationResultTemplateArr);
		childObs.setEvaluatedPersonId(focalPersonId);
		childObs.setSubjectIsFocalPerson(true); 

		// Set the Observation Focus - always the vaccine group
		CD localCD = getLocalCodeConceptForRecommendationConcept(ts, false);
		childObs.setObservationFocus(localCD);

		// Observation Value
		CD localObsCD = getLocalCodeForRecommendationStatus(ts.getRecommendationStatus(), this.backingSchedule);
		if (localObsCD.getCode() == null) {
			String errStr = "Invalid Recommendation Supplied for output";
			logger.warn(_METHODNAME + errStr + "; CD: " + localObsCD);
			throw new ImproperUsageException(errStr);
		}
		ObservationValue childObsValue = new ObservationValue();
		childObsValue.setConcept(localObsCD);
		childObs.setObservationValue(childObsValue);

		// Observation interpretation
		RecommendationStatus rs = ts.getRecommendationStatus();
		List<Recommendation> recs = ts.getFinalRecommendations();
		List<CD> interpretations = new ArrayList<CD>();
		if (recs != null) {
			for (Recommendation rec : recs) {
				String recommendationReasonCode = rec.getRecommendationReason();
				if (recommendationReasonCode != null) {
					boolean lSupplementalTextFound = false;
					if (rec.getRecommendationSupplementalText() != null && recommendationReasonCode.equals(BaseDataRecommendationReason._SUPPLEMENTAL_TEXT.getCdsListItemName())) {
						lSupplementalTextFound = true;
					}
					if (outputSupplementalText == true || (outputSupplementalText == false && lSupplementalTextFound == false)) {
						CD localCDInterp = getLocalCodeForRecommendationReason(recommendationReasonCode, this.backingSchedule);
						if (localCDInterp != null && rec.getRecommendationStatus() == rs && ! interpretations.contains(localCDInterp)) {
							if (lSupplementalTextFound && outputSupplementalText) {
								localCDInterp.setOriginalText(rec.getRecommendationSupplementalText());
							}	
							interpretations.add(localCDInterp);
						}
					}
				}
			}
			if (interpretations.size() > 0)
				childObs.setInterpretation(interpretations);
		}
		childObs.setClinicalStatementToBeRoot(false);
		childObs.setToBeReturned(true);
		drools.insert(childObs);
		pNamedObjects.put("childObs" + nestedIdValue, childObs);

		// Therefore, create as a relatedClinicalStatement
		ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
		rel.setSourceId(uniqueSarIdValue);
		rel.setTargetId(nestedIdValue);
		CD relCodeSR = new CD();
		relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
		relCodeSR.setCode("RSON");
		relCodeSR.setDisplayName("has pertaining reason");
		rel.setTargetRelationshipToSource(relCodeSR);
		drools.insert(rel);
		pNamedObjects.put("rel" + nestedIdValue, rel);
	}

	
	public void outputOtherImmRecommendationSubstanceAdministrationProposal(KnowledgeHelper drools, java.util.HashMap pNamedObjects, String focalPersonId, String cdsSource) 
		throws ImproperUsageException, InconsistentConfigurationException {

		String _METHODNAME = "OutputRootImmRecommendationSubstanceAdministrationProposal: ";

		if (pNamedObjects == null || drools == null) {
			String lErrStr = "Error outputting SubstanceAdministrationProposal: one or more method parameters not initialized";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		SubstanceAdministrationProposal sap = new SubstanceAdministrationProposal();

		String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
		sap.setId(uniqueSarIdValue);
		String[] subsAdmPropTemplateArr = { "2.16.840.1.113883.3.795.11.9.3.1" };
		sap.setTemplateId(subsAdmPropTemplateArr);
		sap.setEvaluatedPersonId(focalPersonId); 
		sap.setSubjectIsFocalPerson(true);

		// Substance Administration Proposal CDS System Data Source
		if (cdsSource != null && ! cdsSource.isEmpty()) {
			CD cdsDataSource = new CD();
			cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
			cdsDataSource.setCodeSystemName("CAT CDS Source");
			cdsDataSource.setCode(cdsSource);
			cdsDataSource.setDisplayName("ICE Version");
			sap.setDataSourceType(cdsDataSource);
		}

		// Substance Proposal General Purpose
		CD subsAdmGeneralPurposeCD = new CD();
		subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
		subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
		subsAdmGeneralPurposeCD.setCode("384810002");
		subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
		sap.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);

		// Set the AdministrableSubstance - may be a vaccine or a vaccine group
		CD localObservationFocusCD = getLocalCodeForRecommendationReason("VACCINE_GROUP_CONCEPT.999", this.backingSchedule);
		AdministrableSubstance substance = new AdministrableSubstance();
		substance.setId(ICELogicHelper.generateUniqueString());
		substance.setSubstanceCode(localObservationFocusCD);
		sap.setSubstance(substance);

		// Set as a root clinical statement
		sap.setClinicalStatementToBeRoot(true); 
		sap.setToBeReturned(true); 	
		drools.insert(sap);
		pNamedObjects.put("sap" + uniqueSarIdValue, sap);

		// Now create the nested observation result
		String nestedIdValue = ICELogicHelper.generateUniqueString();
		// Observation
		ObservationResult childObs = new ObservationResult();
		childObs.setId(nestedIdValue);
		String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
		sap.setTemplateId(observationResultTemplateArr);
		childObs.setEvaluatedPersonId(focalPersonId);
		childObs.setSubjectIsFocalPerson(true); 

		// Set the Observation Focus - always the vaccine group
		childObs.setObservationFocus(localObservationFocusCD);

		// Observation Value
		ObservationValue childObsValue = new ObservationValue();
		CD recommendationCode = getLocalCodeForRecommendationStatus(RecommendationStatus.RECOMMENDATION_NOT_AVAILABLE, this.backingSchedule);
		childObsValue.setConcept(recommendationCode);
		childObs.setObservationValue(childObsValue);

		// Observation interpretation
		List<CD> interpretations = new ArrayList<CD>();
		interpretations.add(getLocalCodeForRecommendationReason(BaseDataRecommendationReason._RECOMMENDATION_NOT_SUPPORTED.getCdsListItemName(), this.backingSchedule));
		childObs.setInterpretation(interpretations);
		childObs.setClinicalStatementToBeRoot(false);
		childObs.setToBeReturned(true);
		drools.insert(childObs);
		pNamedObjects.put("childObs" + nestedIdValue, childObs);

		// Therefore, create as a relatedClinicalStatement
		ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
		rel.setSourceId(uniqueSarIdValue);
		rel.setTargetId(nestedIdValue);
		CD relCodeSR = new CD();
		relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
		relCodeSR.setCode("RSON");
		relCodeSR.setDisplayName("has pertaining reason");
		rel.setTargetRelationshipToSource(relCodeSR);
		drools.insert(rel);
		pNamedObjects.put("rel" + nestedIdValue, rel);
	}
	
	
	/**
	 * Return local ICE3 Observation Evaluation Focus code for the Vaccine Group
	 * @param pVG
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	private CD getLocalCodeForEvaluationConcept(String pVG) {

		String _METHODNAME = "getLocalCodeForEvaluationConcept(): ";
		if (pVG == null) {
			logger.warn(_METHODNAME + "VaccineGroup parameter supplied is null");
			return null;
		}
		
		LocallyCodedCdsListItem lccli = this.backingSchedule.getICESupportingDataConfiguration().getSupportedVaccineGroups().getCdsListItem(pVG);
		if (lccli == null) {
			String lErrStr = "No associated LocallyCodedCdsListItem for the supplied LocallyCodedVaccineGroupItem: " + pVG;
			logger.warn(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}

		return lccli.getCdsListItemCD();
	}


	/**
	 * Return local ICE3 Observation Recommendation Focus code for Vaccine or Vaccine Group
	 * @param pVG
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	private CD getLocalCodeConceptForRecommendationConcept(TargetSeries pTS, boolean atVaccineConceptLevelIfSpecificVaccineRecommended) {

		String _METHODNAME = "getLocalCodeConceptForRecommendationConcept(): ";

		if (pTS == null) {
			logger.error(_METHODNAME + "TargetSeries parameter supplied is null; cannot supply a recommendation focus");
			return null;
		}

		Vaccine lRecommendedVaccine = pTS.getRecommendationVaccine();
		if (lRecommendedVaccine != null && atVaccineConceptLevelIfSpecificVaccineRecommended == true) {
			LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsConcepts().getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS, lRecommendedVaccine.getCdsConcept());
			/////// LocallyCodedCdsListItem sv = this.backingSchedule.getSupportedCdsLists().getCdsListItem(lRecommendedVaccine.getCdsListItemName());
			if (sv != null) {
				// A specific vaccine was recommended; indicate the vaccine recommended instead of othe vaccine group
				return sv.getCdsListItemCD();
			}
			else {
				logger.warn(_METHODNAME + "A vaccine was recommended but no corresponding SupportedVaccineConcept exists; cannot recommend by vaccine; vaccine: " + lRecommendedVaccine);
			}
		}

		// No recommended vaccine specifically; focus will be vaccine group
		String lcvg = pTS.getVaccineGroup();
		if (lcvg == null) {
			logger.warn(_METHODNAME + "VaccineGroup parameter supplied is null");
			return null;
		}

		LocallyCodedCdsListItem lcvgi = this.backingSchedule.getICESupportingDataConfiguration().getSupportedVaccineGroups().getCdsListItem(lcvg);
		if (lcvgi == null) {
			String lErrStr = "LocallyCodedVaccineGroupItem not found for specified vaccine group in TargetSeries (this should not happen); vaccine group: " + lcvg;
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}

		return lcvgi.getCdsListItemCD();
	}

	
	/**
	 * Return local ICE3 recommendation code for the OpenCDS reason code value. 
	 * @param pReasonCode
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	public static CD getLocalCodeForRecommendationReason(String pReasonCode, Schedule s) {

		String _METHODNAME = "getLocalCodeForRecommendationReason(): ";

		if (pReasonCode == null || s == null) {
			logger.warn(_METHODNAME + "no reason code or schedule supplied; returning null");
			return null;
		}

		LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(pReasonCode);
		if (sv == null) {
			String lErrStr = "reason code supplied is not one that is defined in the supporting data; returning null";
			logger.warn(_METHODNAME + lErrStr);
			return null;
		}
		
		return sv.getCdsListItemCD();
	}

	
	/**
	 * Return local ICE3 code for the OpenCDS reason code value. 
	 * @param pReasonCode
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	public static CD getLocalCodeForEvaluationReason(String pReasonCode, Schedule s) {

		String _METHODNAME = "getLocalCodeForEvaluationReason(): ";
		if (pReasonCode == null || s == null) {
			logger.warn(_METHODNAME + "no concept code or schedule supplied; returning null");
			return null;
		}

		LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(pReasonCode);
		if (sv == null) {
			String lErrStr = "reason code supplied is not one that is defined in the supporting data; returning null";
			logger.warn(_METHODNAME + lErrStr);
			return null;
		}
		else if ("EVALUATION_REASON_CONCEPT.UNSPECIFIED_REASON".equals(sv.getCdsListItemName())) {
			logger.info(_METHODNAME + "Unspecified reason for this this shot; no reason for this evaluated shot will be returned");
			return null;
		}
		else {
			return sv.getCdsListItemCD();
		}
	}

	
	/**
	 * Return local ICE3 code value for the DoseStatus. 
	 * @param pDS
	 * @return local ICE3 code value, null if DoseStatus is null, "" if local code value for DoseStatus is not found
	 */
	public static CD getLocalCodeForEvaluationStatus(DoseStatus pDS, Schedule s) {

		String _METHODNAME = "getLocalCodeForEvaluationStatus(): ";
		if (pDS == null || s == null) {
			return null;
		}

		DoseStatus lDoseStatusToReturn = null;
		if (pDS == DoseStatus.VALID || pDS == DoseStatus.ACCEPTED || pDS == DoseStatus.INVALID || pDS == DoseStatus.NOT_EVALUATED) {
			lDoseStatusToReturn = pDS;
		}
		else {
			lDoseStatusToReturn = DoseStatus.ACCEPTED;
		}

		String lCdsListItemName = lDoseStatusToReturn.getCdsListItemName();
		LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(lCdsListItemName);
		if (sv == null) {
			String lErrStr = "status code supplied is not one that is defined in the supporting data; returning null";
			logger.warn(_METHODNAME + lErrStr);
			return null;
		}
		return sv.getCdsListItemCD();
	}


	/**
	 * Return local ICE3 code value for Recommendation
	 * @param pRecommendation
	 * @return null if provided value is null; local ICE3 CD code value; CD with no code value set if provided recommendation status is not either
	 * RecommendationStatus.RECOMMENDED, RecommendationStatus.RECOMMENDED_IN_FUTURE, RecommendationStatus.CONDITIONALLY_RECOMMENDED, 
	 * or RecommendationStatus.NOT_RECOMMENDED
	 */
	public static CD getLocalCodeForRecommendationStatus(RecommendationStatus recStatus, Schedule s) {

		String _METHODNAME = "getLocalCodeForRecommendationStatus(): ";
		if (recStatus == null || s == null) {
			return null;
		}

		RecommendationStatus lRecStatusToReturn = null;
		if (recStatus == RecommendationStatus.RECOMMENDED || recStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED || recStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE || 
			recStatus == RecommendationStatus.NOT_RECOMMENDED || recStatus == RecommendationStatus.RECOMMENDATION_NOT_AVAILABLE) {
			lRecStatusToReturn = recStatus;
		}
		else {
			lRecStatusToReturn = RecommendationStatus.RECOMMENDED;
		}
		
		String lCdsListItemName = lRecStatusToReturn.getCdsListItemName();
		LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(lCdsListItemName);
		if (sv == null) {
			String lErrStr = "status code supplied is not one that is defined in the supporting data; returning null";
			logger.warn(_METHODNAME + lErrStr);
			return null;
		}

		return sv.getCdsListItemCD();
	}

}
