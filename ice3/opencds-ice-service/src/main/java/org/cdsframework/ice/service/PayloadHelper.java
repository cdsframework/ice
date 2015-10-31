package org.cdsframework.ice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.Recommendation.RecommendationStatus;
import org.cdsframework.ice.supportingdata.tmp.SupportedEvaluationConcept;
import org.cdsframework.ice.supportingdata.tmp.SupportedRecommendationConcept;
import org.cdsframework.ice.supportingdata.tmp.SupportedVaccineConcept;
import org.cdsframework.ice.supportingdata.tmp.SupportedVaccineGroupConcept;
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

	// TODO: CDSOutput XML codes... Make configurable
	// public static final String substanceAdministrationProposalTemplateId = "2.16.840.1.113883.3.795.11.9.3.1";
	// public static final String substanceAdministrationGeneralPurposeCodeSystem = "2.16.840.1.113883.6.5";
	// public static final String substanceAdministrationGeneralPurposeCodeSystemName = "SNOMED CT";
	// etc... others in output functions below... 
	// End CDSOuput XML codes

	private static Log logger = LogFactory.getLog(PayloadHelper.class);


	
	public static void OutputNestedImmEvaluationResult(KnowledgeHelper k, java.util.HashMap pNamedObjects, EvalTime evalTime, String focalPersonId, 
			SubstanceAdministrationEvent sae, SupportedVaccineGroupConcept vg, TargetDose d) {

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
		SupportedVaccineConcept lSVC = SupportedVaccineConcept.getSupportedVaccineConceptByConceptCode(d.getVaccineComponent().getVaccineConcept().getOpenCdsConceptCode());
		asSubstanceCode.setCodeSystem(lSVC.getLocalCodeSystem());
		asSubstanceCode.setCode(lSVC.getLocalCodeValue());
		asSubstanceCode.setDisplayName(lSVC.getLocalCodeDisplayName());
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
	public static void OutputRootImmRecommendationSubstanceAdministrationProposal(KnowledgeHelper drools, java.util.HashMap pNamedObjects, String focalPersonId, TargetSeries ts) 
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
		RecommendationStatus rs = ts.getRecommendationStatus();
		List<Recommendation> recs = ts.getFinalRecommendations();
		List<CD> interpretations = new ArrayList<CD>();
		if (recs != null) {
			for (Recommendation rec : recs) {
				CD openCDSReasonCode = rec.getRecommendationReason();
				if (openCDSReasonCode != null) {
					String openCDSReasonCodeValue = openCDSReasonCode.getCode();
					CD localCDInterp = getLocalCodeForRecommendationReason(openCDSReasonCodeValue);
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
	private static CD getLocalCodeForEvaluationConcept(SupportedVaccineGroupConcept pVG) {

		String _METHODNAME = "getLocalCodeForEvaluationConcept(): ";
		if (pVG == null) {
			logger.warn(_METHODNAME + "VaccineGroup parameter supplied is null");
			return null;
		}

		return pVG.getLocalCodeConcept();
	}


	/**
	 * Return local ICE3 Observation Recommendation Focus code for Vaccine or Vaccine Group
	 * @param pVG
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	private static CD getLocalCodeConceptForRecommendationConcept(TargetSeries pTS, boolean atVaccineConceptLevelIfSpecificVaccineRecommended) {

		String _METHODNAME = "getLocalCodeConceptForRecommendationConcept(): ";

		if (pTS == null) {
			logger.error(_METHODNAME + "TargetSeries parameter supplied is null; cannot supply a recommendation focus");
			return null;
		}

		Vaccine lRecommendedVaccine = pTS.getRecommendationVaccine();
		if (lRecommendedVaccine != null && atVaccineConceptLevelIfSpecificVaccineRecommended == true) {
			SupportedVaccineConcept sv = SupportedVaccineConcept.getSupportedVaccineConceptByConceptCode(lRecommendedVaccine.getVaccineConcept().getOpenCdsConceptCode());
			if (sv != null) {
				// A specific vaccine was recommended; indicate the vaccine recommended instead of othe vaccine group
				return sv.getLocalCodeConcept();
			}
			else {
				logger.warn(_METHODNAME + "A vaccine was recommended but no corresponding SupportedVaccineConcept exists; cannot recommend by vaccine");
			}
		}

		// No recommended vaccine specifically; focus will be vaccine group
		SupportedVaccineGroupConcept lVG = pTS.getVaccineGroup();
		if (lVG == null) {
			logger.warn(_METHODNAME + "VaccineGroup parameter supplied is null");
			return null;
		}

		return lVG.getLocalCodeConcept();
	}

	/**
	 * Return local ICE3 recommendation code for the OpenCDS reason code value. 
	 * @param openCDSReasonCode
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	public static CD getLocalCodeForRecommendationReason(String openCDSReasonCode) {

		String _METHODNAME = "getLocalCodeForRecommendationReason(): ";

		if (openCDSReasonCode == null) {
			logger.warn(_METHODNAME + "no concept code supplied; returning null");
			return null;
		}

		SupportedRecommendationConcept src = SupportedRecommendationConcept.getSupportedRecommendationConceptByConceptCode(openCDSReasonCode);
		if (src != null) {
			return src.getLocalCodeConcept();
		}
		else {
			logger.warn(_METHODNAME + "no associated SupportedRecommendationConcept was found for the supplied concept code; returning null");
			return null;
		}

	}

	/**
	 * Return local ICE3 code for the OpenCDS reason code value. 
	 * @param openCDSReasonCode
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	public static CD getLocalCodeForEvaluationReason(String openCDSReasonCode) {

		String _METHODNAME = "getLocalCodeForEvaluationReason(): ";
		if (openCDSReasonCode == null) {
			logger.warn(_METHODNAME + "no concept code supplied; returning null");
			return null;
		}

		SupportedEvaluationConcept src = SupportedEvaluationConcept.getSupportedEvaluationConceptByConceptCode(openCDSReasonCode);
		if (src != null) {
			return src.getLocalCodeConcept();
		}
		else {
			logger.warn(_METHODNAME + "no associated SupportedEvaluationConcept was found for the supplied concept code; returning null");
			return null;
		}

	}
	*/
	
	/**
	 * Return local ICE3 code for the OpenCDS reason code value. 
	 * @param openCDSReasonCode
	 * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
	 */
	public static CD getLocalCodeForEvaluationReason(String openCDSReasonCode) {

		String _METHODNAME = "getLocalCodeForEvaluationReason(): ";
		if (openCDSReasonCode == null) {
			logger.warn(_METHODNAME + "no concept code supplied; returning null");
			return null;
		}

		SupportedEvaluationConcept src = SupportedEvaluationConcept.getSupportedEvaluationConceptByConceptCode(openCDSReasonCode);
		if (src != null && SupportedEvaluationConcept._VACCINE_NOT_ALLOWED_FOR_THIS_DOSE.getConceptCodeValue().equals(src.getConceptCodeValue())) {
			logger.warn(_METHODNAME + "Vaccine not permitted for this dose but we don't return this code, currently");
			return null;
		}
		else if (src != null) {
			return src.getLocalCodeConcept();
		}
		else {
			logger.warn(_METHODNAME + "no associated SupportedEvaluationConcept was found for the supplied concept code; returning null");
			return null;
		}

	}

	

	/**
	 * Return local ICE3 code value for the DoseStatus. 
	 * @param pDS
	 * @return local ICE3 code value, null if DoseStatus is null, "" if local code value for DoseStatus is not found
	 */
	public static CD getLocalCodeForEvaluationStatus(DoseStatus pDS) {

		if (pDS == null) {
			return null;
		}

		if (pDS == DoseStatus.ACCEPTED) {
			return SupportedEvaluationConcept._ACCEPTED_EVALUATION_STATUS.getLocalCodeConcept();
		}
		else if (pDS == DoseStatus.VALID) {
			return SupportedEvaluationConcept._VALID_EVALUATION_STATUS.getLocalCodeConcept();
		}
		else if (pDS == DoseStatus.INVALID) {
			return SupportedEvaluationConcept._INVALID_EVALUATION_STATUS.getLocalCodeConcept();
		}
		else {
			CD retCD = new CD();
			retCD.setCode(SupportedEvaluationConcept._ACCEPTED_EVALUATION_STATUS.getLocalCodeValue());
			retCD.setCodeSystem(SupportedEvaluationConcept._ACCEPTED_EVALUATION_STATUS.getLocalCodeSystem());
			retCD.setCodeSystemName(SupportedEvaluationConcept._ACCEPTED_EVALUATION_STATUS.getLocalCodeSystemName());
			return retCD;
		}
	}


	/**
	 * Return local ICE3 code value for Recommendation
	 * @param pRecommendation
	 * @return null if provided value is null; local ICE3 CD code value; CD with no code value set if provided recommendation status is not either
	 * RecommendationStatus.RECOMMENDED, RecommendationStatus.RECOMMENDED_IN_FUTURE, RecommendationStatus.CONDITIONALLY_RECOMMENDED, 
	 * or RecommendationStatus.NOT_RECOMMENDED
	 */
	public static CD getLocalCodeForRecommendationStatus(RecommendationStatus recStatus) {

		if (recStatus == null) {
			return null;
		}

		if (recStatus == RecommendationStatus.RECOMMENDED) {
			return SupportedRecommendationConcept._RECOMMENDED_RECOMMENDATION_STATUS.getLocalCodeConcept();
		}
		else if (recStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED) {
			return SupportedRecommendationConcept._CONDITIONAL_RECOMMENDATION_STATUS.getLocalCodeConcept();
		}
		else if (recStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE) {
			return SupportedRecommendationConcept._FUTURE_RECOMMEDATION_STATUS.getLocalCodeConcept();
		}
		else if (recStatus == RecommendationStatus.NOT_RECOMMENDED) {
			return SupportedRecommendationConcept._NOT_RECOMMENDED_RECOMMENDATION_STATUS.getLocalCodeConcept();
		}
		else {
			CD retCD = new CD();
			retCD.setCode(SupportedRecommendationConcept._RECOMMENDED_RECOMMENDATION_STATUS.getLocalCodeValue());
			retCD.setCodeSystem(SupportedRecommendationConcept._RECOMMENDED_RECOMMENDATION_STATUS.getLocalCodeSystem());
			retCD.setCodeSystemName(SupportedRecommendationConcept._RECOMMENDED_RECOMMENDATION_STATUS.getLocalCodeSystemName());
			return retCD;
		}
	}


}
