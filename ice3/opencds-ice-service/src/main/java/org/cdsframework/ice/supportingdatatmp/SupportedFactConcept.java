package org.cdsframework.ice.supportingdatatmp;

import org.opencds.vmr.v1_0.internal.datatypes.CD;


public enum SupportedFactConcept {

	_ADOLESCENT_TDAP_5_DOSE_SERIES_RECOMMENDATION_EXCEPTION("ICE_FACT_ADOLESCENT_TDAP_5_DOSE_SERIES_RECOMMENDATION_EXCEPTION", "Adolescent Tdap 5 Dose Series Recommendation"),
	_ABOVE_MAXIMUM_AGE("ICE_FACT-ABOVE_MAXIMUM_AGE", "Above Minimum Age"),
	_BELOW_MINIMUM_AGE("ICE_FACT-BELOW_MINIMUM_AGE", "Below Minimum Age"),
	_BELOW_MINIMUM_INTERVAL("ICE_FACT-BELOW_MINIMUM_INTERVAL", "Below Minimum Interval"),
	_CANDIDATE_SERIES_IDENTIFICATION_DEFAULT_RULE_OVERRIDE("ICE_FACT-CANDIDATE_SERIES_IDENTIFICATION_DEFAULT_RULE_OVERRIDE", "Override series Identification Default Logic"),
	_DOSE_INTERVAL_OVERRIDE("ICE_FACT-DOSE_INTERVAL_OVERRIDE", "Override Dose Interval Check via IceFact"),
	_DOSE_OF_PERTUSSIS("ICE_FACT-DOSE_OF_PERTUSSIS", "Dose of Pertussis"),
	_DOSE_OF_ADOLESCENT_TDAP("ICE_FACT-DOSE_OF_ADOLESCENT_TDAP", "Dose of Adolescent Tdap"),
	_DOSE_OF_ADOLESCENT_TDAP_NEEDED("ICE_FACT-DOSE_OF_TDAP_NEEDED", "Dose of Tdap Needed"),
	_ADOLESCENT_TDAP_COMPLETED("ICE_FACT-ADOLESCENT_TDAP_COMPLETED", "Adolescent Tdap Completed"),
	_DTP_5_DOSE_SERIES_EXCEPTION1("ICE_FACT-DTP_5_DOSE_SERIES_EXCEPTION1", "DTP 5-Dose Series Exception 1"),
	_DTP_5_DOSE_SERIES_EXCEPTION2("ICE_FACT-DTP_5_DOSE_SERIES_EXCEPTION2", "DTP 5-Dose Series Exception 2"),
	_DUPLICATE_SHOT_SAME_DAY("ICE_FACT-DUPLICATE_SHOT_SAME_DAY", "Duplicate shot/same day"),
	_EXECUTION_DATE_PRIOR_TO_20220902("ICE_FACT-__DATE_PRIOR_TO_20220902", "Date prior to 9/2/2022"),
	_EXECUTION_DATE_ON_OR_AFTER_20220902("ICE_FACT-__DATE_ON_OF_AFTER_20220902", "Date on or after 9/2/2022"),
	_EXECUTION_DATE_ON_OR_AFTER_20221012("ICE_FACT-__DATE_ON_OF_AFTER_20221012", "Date on or after 10/12/2022"),
	_INVALID_AGE("ICE_FACT-INVALID_AGE", "Invalid Age"),
	_INVALID_VACCINE("ICE_FACT-INVALID_VACCINE", "Invalid Vaccine"),
	_OUTPUT_NUMBER_OF_DOSES_REMAINING("ICE_FACT-OUTPUT_NUMBER_OF_DOSES_REMAINING", "Output number of doses remaining"),
	_PNEUMOCOCCAL_CHILD_SERIES_COMPLETE("ICE_FACT-PNEUMOCOCCAL_CHILD_SERIES_COMPLETE", "Pneumococcal- Child Series Complete"),
	_PNEUMOCOCCAL_RECOMMENDED_CHILD_RECEIVE_PCV13_DOSE("ICE_FACT-_PNEUMOCOCCAL_RECOMMENDED_CHILD_RECEIVE_PCV13_DOSE", "Pneumococcal- Recommended Child Receive PCV13 Dose");

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
