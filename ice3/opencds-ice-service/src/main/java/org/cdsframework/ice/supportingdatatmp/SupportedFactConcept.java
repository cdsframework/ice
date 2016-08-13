package org.cdsframework.ice.supportingdatatmp;

import org.opencds.vmr.v1_0.internal.datatypes.CD;


public enum SupportedFactConcept {

	_ADOLESCENT_TDAP_5_DOSE_SERIES_RECOMMENDATION_EXCEPTION("ICE_FACT_ADOLESCENT_TDAP_5_DOSE_SERIES_RECOMMENDATION_EXCEPTION", "Adolescent Tdap 5 Dose Series Recommendation"),
	_BELOW_MINIMUM_AGE("ICE_FACT-BELOW_MINIMUM_AGE", "Below Minimum Age"),
	_BELOW_MINIMUM_INTERVAL("ICE_FACT-BELOW_MINIMUM_INTERVAL", "Below Minimum Interval"),
	_DOSE_OF_PERTUSSIS("ICE_FACT-DOSE_OF_PERTUSSIS", "Dose of Pertussis"),
	_DOSE_OF_ADOLESCENT_TDAP_NEEDED("ICE_FACT-DOSE_OF_TDAP_NEEDED", "Dose of Tdap Needed"),
	_DTP_5_DOSE_SERIES_EXCEPTION1("ICE_FACT-DTP_5_DOSE_SERIES_EXCEPTION1", "DTP 5-Dose Series Exception 1"),	
	_DTP_5_DOSE_SERIES_EXCEPTION2("ICE_FACT-DTP_5_DOSE_SERIES_EXCEPTION2", "DTP 5-Dose Series Exception 2"),
	_DUPLICATE_SHOT_SAME_DAY("ICE_FACT-DUPLICATE_SHOT_SAME_DAY", "Duplicate shot/same day"),
	_INVALID_AGE("ICE_FACT-INVALID_AGE", "Invalid Age"),
	_INVALID_VACCINE("ICE_FACT-INVALID_VACCINE", "Invalid Vaccine"),
	_OUTPUT_NUMBER_OF_DOSES_REMAINING("ICE_FACT-OUTPUT_NUMBER_OF_DOSES_REMAINING", "Output number of doses remaining");


	private CD supportedFactConcept;


	private SupportedFactConcept(String pConceptCodeValue, String pConceptDisplayNameValue) {
		supportedFactConcept = new CD();
		supportedFactConcept.setCode(pConceptCodeValue);
		supportedFactConcept.setDisplayName(pConceptDisplayNameValue);
	}


	/**
	 * Return SupportedEvaluationConcept for the specified concept code; null if no associated SupportedEvaluationConcept exists
	 */
	public static SupportedFactConcept getSupportedFactConceptByConceptCode(String conceptCode) {

		if (conceptCode == null) {
			return null;
		}
		for (SupportedFactConcept vc : SupportedFactConcept.values()) {
			if (conceptCode.equals(vc.getConceptCodeValue())) {
				return vc;
			}
		}

		return null;
	}

	public CD getConcept() {
		return supportedFactConcept;
	}

	public String getConceptCodeValue() {
		return supportedFactConcept.getCode();
	}

	public String getConceptDisplayNameValue() {
		return supportedFactConcept.getDisplayName();
	}

}
