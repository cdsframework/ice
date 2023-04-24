/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */
 
package org.cdsframework.ice.supportingdata;

public enum ICEConceptType {

	DISEASE("SUPPORTED_DISEASE_CONCEPT"),
	EVALUATION_STATUS("EVALUATION_STATUS_CONCEPT"),
	EVALUATION_REASON("EVALUATION_REASON_CONCEPT"),
	FACT("SUPPORTED_FACT_CONCEPT"),
	IMMUNITY("SUPPORTED_IMMUNITY_CONCEPT"),
	OPENCDS("OPENCDS_CDM"),
	PERSON("SUPPORTED_PERSON_CONCEPT"),
	RECOMMENDATION_STATUS("RECOMMENDATION_STATUS_CONCEPT"),
	RECOMMENDATION_REASON("RECOMMENDATION_REASON_CONCEPT"),
	VACCINE("SUPPORTED_VACCINES"),
	VACCINE_GROUP("VACCINE_GROUP_CONCEPT");
	
	private String iceConceptTypeValue;
	
	private ICEConceptType(String pIceConceptType) {
		
		this.iceConceptTypeValue = pIceConceptType;
	}
	
	/**
	 * Return SupportedEvaluationConcept for the specified concept code; null if no associated SupportedEvaluationConcept exists
	 */
	public static ICEConceptType getSupportedIceConceptType(String pIceConceptType) {

		if (pIceConceptType == null) {
			return null;
		}
		for (ICEConceptType vc : ICEConceptType.values()) {
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

