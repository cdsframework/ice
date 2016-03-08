package org.cdsframework.ice.supportingdata;

public enum BaseDataPerson implements BaseData {

	_GENDER_FEMALE("SUPPORTED_PERSON_CONCEPT.F"),
	_GENDER_MALE("SUPPORTED_PERSON_CONCEPT.M");
	
	private String cdsListItemName;
	
	private BaseDataPerson() {
		this.cdsListItemName = null;
	}
	
	private BaseDataPerson(String pCdsListItem) {
		this.cdsListItemName = pCdsListItem;
	}
	
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}
	
}
