/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.common.structures;

import java.net.URI;
import java.util.Date;

/**
 * @author David Shields
 * 
 * @date
 *
 */
public class EvaluationRequestDataItem {
	protected String    focalPersonId;
	protected Date 		evalTime; 
	protected String 	clientLanguage;
	protected String 	clientTimeZoneOffset;
	protected String 	externalFactModelSSId;
	protected String    inputItemName;
	protected String    inputContainingEntityId;
	protected String    interactionId;
	protected URI       serverUri;
	
	public String getFocalPersonId() {
		return focalPersonId;
	}
	
	public void setFocalPersonId(String focalPersonId) {
		this.focalPersonId = focalPersonId;
	}

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
	
    public URI getServerBaseUri() {
        return serverUri;
    }

    public void setServerBaseUri(URI serverUri) {
        this.serverUri = serverUri;
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
				+ ", interactionId=" + interactionId 
				+ ", serverUri=" + serverUri
				+ "]";
	}

}
