/**
 * Copyright (C) 2016 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

package org.cdsframework.ice.service;

public enum DoseStatus {
	ACCEPTED("EVALUATION_STATUS_CONCEPT.ACCEPTED"), 
	PRIMARY_SHOT_DETERMINATION_IN_PROCESS, 
	EVALUATION_IN_PROCESS, 
	EVALUATION_COMPLETE, 
	NOT_EVALUATED, 
	INVALID("EVALUATION_STATUS_CONCEPT.INVALID"), 
	VALID("EVALUATION_STATUS_CONCEPT.VALID"), 
	DECISION_RENDERED;
	
	private String doseStatusCdsListItem;
	
	private DoseStatus() {
		this.doseStatusCdsListItem = null;
	}
	
	private DoseStatus(String pDoseStatusConcept) {
		this.doseStatusCdsListItem = pDoseStatusConcept;
	}
	
	public String getDoseStatusCdsListItem() {
		return this.doseStatusCdsListItem;
	}
	
	public static String getDoseStatusCdsListItem(DoseStatus pDS) {
		
		if (pDS == null) {
			return null;
		}
		else {
			return pDS.getDoseStatusCdsListItem();
		}
	}
}
