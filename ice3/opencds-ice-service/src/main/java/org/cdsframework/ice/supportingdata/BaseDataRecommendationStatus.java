package org.cdsframework.ice.supportingdata;

public enum BaseDataRecommendationStatus implements BaseData {
	NOT_FORECASTED, 
	EVALUATION_OF_HISTORY_REQUIRED, 
	FORECASTING_IN_PROGRESS, 
	FORECASTING_COMPLETE, 
	RECOMMENDED("RECOMMENDATION_STATUS_CONCEPT.RECOMMENDED"), 
	CONDITIONALLY_RECOMMENDED("RECOMMENDATION_STATUS_CONCEPT.CONDITIONAL"), 
	NOT_RECOMMENDED("RECOMMENDATION_STATUS_CONCEPT.NOT_RECOMMENDED"), 
	RECOMMENDED_IN_FUTURE("RECOMMENDATION_STATUS_CONCEPT.FUTURE_RECOMMENDED"); 
	
	private String cdsListItemName;
	
	private BaseDataRecommendationStatus() {
		this.cdsListItemName = null;
	}
	
	private BaseDataRecommendationStatus(String pRecommendationStatusCdsListItem) {
		this.cdsListItemName = pRecommendationStatusCdsListItem;
	}
	
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}

	/*
	public static String getRecommendationStatusCdsListItem(RecommendationStatus pRS) {
		
		if (pRS == null) {
			return null;
		}
		else {
			return pRS.getCdsListItemName();
		}
	}
	*/
}
