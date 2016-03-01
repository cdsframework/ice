package org.cdsframework.ice.supportingdata;

public enum BaseDataRecommendationReason implements BaseData {

	_NOT_RECOMMENDED_COMPLETE_REASON("RECOMMENDATION_REASON_CONCEPT.COMPLETE"),
	_NOT_RECOMMENDED_NOT_SPECIFIED_REASON("RECOMMENDATION_REASON_CONCEPT.NOT_SPECIFIED"),
	_RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON("RECOMMENDATION_REASON_CONCEPT.COMPLETE_HIGH_RISK"),
	_RECOMMENDED_IN_FUTURE_REASON("RECOMMENDATION_REASON_CONCEPT.DUE_IN_FUTURE"),
	_RECOMMENDED_DUE_NOW_REASON("RECOMMENDATION_REASON_CONCEPT.DUE_NOW");
	
	private String cdsListItemName;
	
	private BaseDataRecommendationReason() {
		this.cdsListItemName = null;
	}
	
	private BaseDataRecommendationReason(String pRecommendationReasonCdsListItem) {
		this.cdsListItemName = pRecommendationReasonCdsListItem;
	}
	
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}

}
