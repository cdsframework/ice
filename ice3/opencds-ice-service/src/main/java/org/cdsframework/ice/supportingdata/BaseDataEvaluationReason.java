package org.cdsframework.ice.supportingdata;


public enum BaseDataEvaluationReason implements BaseData {

	_EXTRA_DOSE_EVALUATION_REASON("EVALUATION_REASON_CONCEPT._EXTRA_DOSE_EVALUATION_REASON"),
	_INVALID_AGE_EVALUATION_REASON("EVALUATION_REASON_CONCEPT.INVALID_AGE"),
	_BELOW_MINIMUM_AGE_EVALUATION_REASON("EVALUATION_REASON_CONCEPT.BELOW_MINIMUM_AGE_SERIES"),
	_BELOW_MINIMUM_INTERVAL_EVALUATION_REASON("EVALUATION_REASON_CONCEPT.BELOW_MINIMUM_INTERVAL");
	
	private String cdsListItemName;
	
	private BaseDataEvaluationReason() {
		this.cdsListItemName = null;
	}
	
	private BaseDataEvaluationReason(String pEvaluationReasonCdsListItem) {
		this.cdsListItemName = pEvaluationReasonCdsListItem;
	}
	
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}
	
}
