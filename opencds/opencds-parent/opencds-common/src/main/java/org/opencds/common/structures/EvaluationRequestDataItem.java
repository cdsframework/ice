/**
 * 
 */
package org.opencds.common.structures;

import java.util.Date;

/**
 * @author David Shields
 * 
 * @date
 *
 */
public class EvaluationRequestDataItem {
	protected Date 		evalTime; 
	protected String 	clientLanguage;
	protected String 	clientTimeZoneOffset;
	protected String 	externalFactModelSSId;
	protected String	inputItemName;
	protected String	inputContainingEntityId;
	protected String 	inputPayloadString;
	protected String	interactionId;
	protected Object	cdsInput; 	//must be cast to a JAXB object when used...
	protected String	iceVersion;

	/**
	 * @return the evalTime
	 */
	public Date getEvalTime() {
		return evalTime;
	}

	/**
	 * @param evalTime the evalTime to set
	 */
	public void setEvalTime(Date evalTime) {
		this.evalTime = evalTime;
	}

	/**
	 * @return the clientLanguage
	 */
	public String getClientLanguage() {
		return clientLanguage;
	}

	/**
	 * @param clientLanguage the clientLanguage to set
	 */
	public void setClientLanguage(String clientLanguage) {
		this.clientLanguage = clientLanguage;
	}

	/**
	 * @return the clientTimeZoneOffset
	 */
	public String getClientTimeZoneOffset() {
		return clientTimeZoneOffset;
	}

	/**
	 * @param clientTimeZoneOffset the clientTimeZoneOffset to set
	 */
	public void setClientTimeZoneOffset(String clientTimeZoneOffset) {
		this.clientTimeZoneOffset = clientTimeZoneOffset;
	}

	/**

	 * @return the externalFactModelSSId
	 */
	public String getExternalFactModelSSId() {
		return externalFactModelSSId;
	}

	/**
	 * @param externalFactModelSSId the externalFactModelSSId to set
	 */
	public void setExternalFactModelSSId(String externalFactModelSSId) {
		this.externalFactModelSSId = externalFactModelSSId;
	}

	/**
	 * @return the inputItemName
	 */
	public String getInputItemName() {
		return inputItemName;
	}

	/**
	 * @param inputItemName the inputItemName to set
	 */
	public void setInputItemName(String inputItemName) {
		this.inputItemName = inputItemName;
	}

	/**
	 * @return the inputContainingEntityId
	 */
	public String getInputContainingEntityId() {
		return inputContainingEntityId;
	}

	/**
	 * @param inputContainingEntityId the inputContainingEntityId to set
	 */
	public void setInputContainingEntityId(String inputContainingEntityId) {
		this.inputContainingEntityId = inputContainingEntityId;
	}

	/**
	 * @return the inputPayloadString
	 */
	public String getInputPayloadString() {
		return inputPayloadString;
	}

	/**
	 * @param inputPayloadString the inputPayloadString to set
	 */
	public void setInputPayloadString(String inputPayloadString) {
		this.inputPayloadString = inputPayloadString;
	}

	/**
	 * @return the interactionId
	 */
	public String getInteractionId() {
		return interactionId;
	}

	/**
	 * @param interactionId the interactionId to set
	 */
	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}

	/**
	 * @return the cdsInput
	 */
	public Object getCdsInput() {
		return cdsInput;
	}

	/**
	 * @param cdsInput the cdsInput to set
	 */
	public void setCdsInput(Object cdsInput) {
		this.cdsInput = cdsInput;
	}

	public String getIceVersion()
	{
		return iceVersion;
	}

	public void setIceVersion(String iceVersion)
	{
		this.iceVersion = iceVersion;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DSSRequestItem [evalTime=" + evalTime 
				+ ", clientLanguage=" + clientLanguage 
				+ ", clientTimeZoneOffset=" + clientTimeZoneOffset 
				+ ", externalFactModelSSId=" + externalFactModelSSId
				+ ", inputItemName=" + inputItemName
				+ ", inputContainingEntityId=" + inputContainingEntityId
				+ ", inputPayloadString=" + inputPayloadString
				+ ", interactionId=" + interactionId 
				+ ", cdsInput=" + cdsInput
				+ ", iceVersion=" + iceVersion
				+ "]";
	}
}
