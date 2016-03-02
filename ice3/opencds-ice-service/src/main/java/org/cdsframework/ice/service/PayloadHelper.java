/**
 * Copyright (C) 2016 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationStatus;
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
	
	
	public void OutputNestedImmEvaluationResult(KnowledgeHelper k, java.util.HashMap pNamedObjects, EvalTime evalTime, String focalPersonId, 
			SubstanceAdministrationEvent sae, String vg, TargetDose d) {

		String _METHODNAME = "OutputNestedImmEvaluationResult: ";
		if (k == null || pNamedObjects == null || evalTime == null || sae == null || d == null) {
			String str = "Caller supplied either NULL KnowledgeHelper, NamedObject HashMap, evalTime, SubstanceAdministrationEvent, TargetDose or Schedule parameter";
			logger.warn(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}

		if (logger.isDebugEnabled()) {
			String str = "focalPersonId " + focalPersonId + ", sae: " + sae.getId() + ", VG: " + vg + ", Dose unique ID: " + d.getUniqueId() + ", Dose ID " + d.getDoseId() +
					", Dose all: " + d.toString();
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
		// Substance Proposal General Purpose
		CD subsAdmGeneralPurposeCD = new CD();
		subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
		subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
		subsAdmGeneralPurposeCD.setCode("384810002");
		subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
		lSAE.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);
		// Dose information
		INT lINTDoseNumber = new INT();
		lINTDoseNumber.setValue(d.getDoseNumberInSeries());
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
		// SupportedVaccineConcept lSVC = SupportedVaccineConcept.getSupportedVaccineConceptByConceptCode(d.getVaccineComponent().getVaccineConcept().getOpenCdsConceptCode());
		LocallyCodedCdsListItem lSVC = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsConcepts().getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE, d.getVaccineComponent().getVaccineConcept());
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
		CD localObsCD = getLocalCodeForEvaluationStatus(doseStatus);
		ObservationValue childObsValue = new ObservationValue();
		childObsValue.setConcept(localObsCD);
		childObs.setObservationValue(childObsValue);

		// Observation interpretation
		List<CD> interpretations = new ArrayList<CD>();
		if (doseStatus == DoseStatus.INVALID) {
			for (String interp : d.getInvalidReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			if (interpretations.size() > 0)
				childObs.setInterpretation(interpretations);
		}
		else if (doseStatus == DoseStatus.ACCEPTED || doseStatus == DoseStatus.VALID) {
			for (String interp : d.getAcceptedReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			for (String interp : d.getValidReasons()) {
				CD localCDInterp = getLocalCodeForEvaluationReason(interp);
				if (localCDInterp != null && ! interpretations.contains(localCDInterp))
					interpretations.add(localCDInterp);
			}
			if (interpretations.size() > 0)
				childObs.setInterpretation(interpretations);
		}

		// This is a nested clinical statement
		childObs.setClinicalStatementToBeRoot(false);
		childObs.setToBeReturned(true);
		// k.insert(childObs);
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
		// k.insert(relO);
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
		       <proposedAdministrationTimeInterval low="20111201" high="20111201"/>
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
	public void OutputRootImmRecommendationSubstanceAdministrationProposal(KnowledgeHelper drools, java.util.HashMap pNamedObjects, String focalPersonId, TargetSeries ts) 
			throws ImproperUsageException, InconsistentConfigurationException {

		String _METHODNAME = "OutputRootImmRecommendationSubstanceAdministrationProposal: ";

		SubstanceAdministrationProposal sap = new SubstanceAdministrationProposal();
		String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
		sap.setId(uniqueSarIdValue);
		String[] subsAdmPropTemplateArr = { "2.16.840.1.113883.3.795.11.9.3.1" };
		sap.setTemplateId(subsAdmPropTemplateArr);
		sap.setEvaluatedPersonId(focalPersonId); 
		sap.setSubjectIsFocalPerson(true); 
		// Substance Proposal General Purpose
		CD subsAdmGeneralPurposeCD = new CD();
		subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
		subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
		subsAdmGeneralPurposeCD.setCode("384810002");
		subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
		sap.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);
		// Proposed Time Interval
		Date finalRecommendationDate = ts.getFinalRecommendationDate();
		if (finalRecommendationDate != null) {
			IVLDate obsTime = new IVLDate(); 
			obsTime.setLow(finalRecommendationDate); 
			obsTime.setHigh(finalRecommendationDate);
			sap.setProposedAdministrationTimeInterval(obsTime);
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
		CD localObsCD = getLocalCodeForRecommendationStatus(ts.getRecommendationStatus());
		if (localObsCD.getCode() == null) {
			String errStr = "Invalid Recommendation Supplied for output";
			logger.warn(_METHODNAME + errStr + "; CD: " + localObsCD);
			throw new ImproperUsageException(errStr);
		}
		ObservationValue childObsValue = new ObservationValue();
		childObsValue.setConcept(localObsCD);
		childObs.setObservationValue(childObsValue);

		// Observation interpretation
		BaseDataRecommendationStatus rs = ts.getRecommendationStatus();
		List<Recommendation> recs = ts.getFinalRecommendations();
		List<CD> interpretations = new ArrayList<CD>();
		if (recs != null) {
			for (Recommendation rec : recs) {
				String recommendationReasonCode = rec.getRecommendationReason();
				if (recommendationReasonCode != null) {
					CD localCDInterp = getLocalCodeForRecommendationReason(recommendationReasonCode);
					if (localCDInterp != null && rec.getRecommendationStatus() == rs && ! interpretations.contains(localCDInterp))
						interpretations.add(localCDInterp);
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
			LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsConcepts().getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE, lRecommendedVaccine.getVaccineConcept());
			if (sv != null) {
				// A specific vaccine was recommended; indicate the vaccine recommended instead of othe vaccine group
				return sv.getCdsListItemCD();
			}
			else {
				logger.warn(_METHODNAME + "A vaccine was recommended but no corresponding SupportedVaccineConcept exists; cannot recommend by vaccine");
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
	public CD getLocalCodeForRecommendationReason(String pReasonCode) {

		String _METHODNAME = "getLocalCodeForRecommendationReason(): ";

		if (pReasonCode == null) {
			logger.warn(_METHODNAME + "no reason code supplied; returning null");
			return null;
		}

		LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(pReasonCode);
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
	public CD getLocalCodeForEvaluationReason(String pReasonCode) {

		String _METHODNAME = "getLocalCodeForEvaluationReason(): ";
		if (pReasonCode == null) {
			logger.warn(_METHODNAME + "no concept code supplied; returning null");
			return null;
		}

		LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(pReasonCode);
		if (sv == null) {
			String lErrStr = "reason code supplied is not one that is defined in the supporting data; returning null";
			logger.warn(_METHODNAME + lErrStr);
			return null;
		}
		else if ("EVALUATION_REASON_CONCEPT.VACCINE_NOT_ALLOWED_FOR_THIS_DOSE".equals(sv.getCdsListItemName())) {		// TODO: Deal with this hard-code issue
			logger.warn(_METHODNAME + "Vaccine not permitted for this dose but we don't return this code, currently");
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
	public CD getLocalCodeForEvaluationStatus(DoseStatus pDS) {

		String _METHODNAME = "getLocalCodeForEvaluationStatus(): ";
		if (pDS == null) {
			return null;
		}

		DoseStatus lDoseStatusToReturn = null;
		if (pDS == DoseStatus.VALID || pDS == DoseStatus.ACCEPTED || pDS == DoseStatus.INVALID) {
			lDoseStatusToReturn = pDS;
		}
		else {
			lDoseStatusToReturn = DoseStatus.ACCEPTED;
		}

		String lCdsListItemName = lDoseStatusToReturn.getCdsListItemName();
		LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(lCdsListItemName);
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
	public CD getLocalCodeForRecommendationStatus(BaseDataRecommendationStatus recStatus) {

		String _METHODNAME = "getLocalCodeForRecommendationStatus(): ";
		if (recStatus == null) {
			return null;
		}

		BaseDataRecommendationStatus lRecStatusToReturn = null;
		if (recStatus == BaseDataRecommendationStatus.RECOMMENDED || recStatus == BaseDataRecommendationStatus.CONDITIONALLY_RECOMMENDED || recStatus == BaseDataRecommendationStatus.RECOMMENDED_IN_FUTURE || 
			recStatus == BaseDataRecommendationStatus.NOT_RECOMMENDED) {
			lRecStatusToReturn = recStatus;
		}
		else {
			lRecStatusToReturn = BaseDataRecommendationStatus.RECOMMENDED;
		}
		
		// String lCdsListItemName = RecommendationStatus.getRecommendationStatusCdsListItem(lRecStatusToReturn);
		String lCdsListItemName = lRecStatusToReturn.getCdsListItemName();
		LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(lCdsListItemName);
		if (sv == null) {
			String lErrStr = "status code supplied is not one that is defined in the supporting data; returning null";
			logger.warn(_METHODNAME + lErrStr);
			return null;
		}

		return sv.getCdsListItemCD();
	}

}
