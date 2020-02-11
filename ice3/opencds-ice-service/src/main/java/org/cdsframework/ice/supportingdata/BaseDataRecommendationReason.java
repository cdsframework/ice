/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

public enum BaseDataRecommendationReason implements BaseData {

	_NOT_RECOMMENDED_COMPLETE_REASON("RECOMMENDATION_REASON_CONCEPT.COMPLETE"),
	_NOT_RECOMMENDED_NOT_SPECIFIED_REASON("RECOMMENDATION_REASON_CONCEPT.NOT_SPECIFIED"),
	_RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON("RECOMMENDATION_REASON_CONCEPT.HIGH_RISK"),
	_RECOMMENDED_IN_FUTURE_REASON("RECOMMENDATION_REASON_CONCEPT.DUE_IN_FUTURE"),
	_RECOMMENDED_DUE_NOW_REASON("RECOMMENDATION_REASON_CONCEPT.DUE_NOW"),
	_RECOMMENDATION_NOT_SUPPORTED("RECOMMENDATION_REASON_CONCEPT.NOT_SUPPORTED"),
	_SUPPLEMENTAL_TEXT("RECOMMENDATION_REASON_CONCEPT.SUPPLEMENTAL_TEXT");
	
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
