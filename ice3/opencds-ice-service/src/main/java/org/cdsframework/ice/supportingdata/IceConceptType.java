package org.cdsframework.ice.supportingdata;

public enum IceConceptType {

	DISEASE("SUPPORTED_DISEASE_CONCEPT"),
	EVALUATION_STATUS("EVALUATION_STATUS_CONCEPT"),
	EVALUATION_REASON("EVALUATION_REASON_CONCEPT"),
	FACT("SUPPORTED_FACT_CONCEPT"),
	IMMUNITY("SUPPORTED_IMMUNITY_CONCEPT"),
	PERSON("SUPPORTED_PERSON_CONCEPT"),
	RECOMMENDATION_STATUS("RECOMMENDATION_STATUS_CONCEPT"),
	RECOMMENDATION_REASON("RECOMMENDATION_REASON_CONCEPT"),
	VACCINE("SUPPORTED_VACCINES"),
	VACCINE_GROUP("VACCINE_GROUP_CONCEPT");
	
	private String iceConceptTypeValue;
	
	private IceConceptType(String pIceConceptType) {
		
		this.iceConceptTypeValue = pIceConceptType;
	}
	
	/**
	 * Return SupportedEvaluationConcept for the specified concept code; null if no associated SupportedEvaluationConcept exists
	 */
	public static IceConceptType getSupportedIceConceptType(String pIceConceptType) {

		if (pIceConceptType == null) {
			return null;
		}
		for (IceConceptType vc : IceConceptType.values()) {
			if (pIceConceptType.equals(vc.getIceConceptTypeValue())) {
				return vc;
			}
		}
		
		return null;
	}
	
	public String getIceConceptTypeValue() {
		
		return iceConceptTypeValue;
	}
}

