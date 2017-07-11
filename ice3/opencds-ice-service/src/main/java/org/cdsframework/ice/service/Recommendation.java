/**
 * Copyright (C) 2017 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Recommendation {
	
	public enum RecommendationType { EARLIEST, EARLIEST_RECOMMENDED, LATEST_RECOMMENDED }
	
	private String recommendationIdentifier;
	private String targetSeriesIdentifier;
	private RecommendationStatus recommendationStatus;
	private Vaccine recommendedVaccine;
	private Date recommendationDate;
	private String recommendationReason;

	private static Log logger = LogFactory.getLog(Recommendation.class);
	
	/**
	 * Initializes a Recommendation object; recommendationReason is set to empty (it is never null), TargetSeriesIdentifier to the supplied 
	 * TargetSeries tSeriesIdentifier, and all other attributes to null
	 * @param pTargetSeriesIdentifier unique identifier of series that is populated in this objects TargetSeriesIdentifier
	 * @throws IllegalArgumentException if supplied identifier is null
	 */
	public Recommendation(TargetSeries pTS) 
		throws IllegalArgumentException {
		
		String _METHODNAME = "Recommendation(): ";
		if (pTS == null) {
			String errStr = "Supplied target series identifier is null";
			logger.error(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}

		recommendationIdentifier = ICELogicHelper.generateUniqueString();
		targetSeriesIdentifier = pTS.getTargetSeriesIdentifier();
		recommendedVaccine = null;
		recommendationDate = null;
		recommendationStatus = RecommendationStatus.NOT_FORECASTED;
		recommendationReason = null;
	}
	
	public String getRecommendationIdentifier() {
		return recommendationIdentifier;
	}

	public String getTargetSeriesIdentifier() {
		return targetSeriesIdentifier;
	}

	public RecommendationStatus getRecommendationStatus() {
		return recommendationStatus;
	}

	/**
	 * Set the RecommendationStatus for this recommended. If the supplied parameter is null, RecommendationStatus is not changed
	 * @param recommendationStatus
	 */
	public void setRecommendationStatus(RecommendationStatus recommendationStatus) {
		if (recommendationStatus == null) {
			return;
		}
		this.recommendationStatus = recommendationStatus;
	}
	
	public Vaccine getRecommendedVaccine() {
		return recommendedVaccine;
	}

	public void setRecommendedVaccine(Vaccine recommendedVaccine) {
		this.recommendedVaccine = recommendedVaccine;
	}

	public Date getRecommendationDate() {
		return recommendationDate;
	}

	public void setRecommendationDate(Date recommendationDate) {
		this.recommendationDate = recommendationDate;
	}
	
	public String getRecommendationReason() {
		return recommendationReason;
	}

	public void setRecommendationReason(String recommendationReason) {
		if (recommendationReason == null) {
			this.recommendationReason = null;
		}
		else {
			this.recommendationReason = recommendationReason;
		}
	}
	
	/**
	 * Return a subset of the supplied Recommendation List with a List of those Recommendations that have the same RecommendationStatuses as the ones 
	 * supplied. If the supplied recommendation list is null or is empty, return an empty list. If there are no recommendations in the list with the 
	 * supplied RecommendsationStatuses, 
	 * return an empty list.
	 * @param recList
	 * @param recStatusOfInterest
	 */
	public static List<Recommendation> getRecommendationListSubsetWithSpecifiedStatuses(List<Recommendation> recList, List<RecommendationStatus> recStatusListOfInterest) {
		
		if (recList == null || recList.size() == 0 || recStatusListOfInterest == null || recStatusListOfInterest.size() == 0) {
			return new ArrayList<Recommendation>();
		}
		
		List<Recommendation> lSubset = new ArrayList<Recommendation>();
		for (Recommendation lRec : recList) {
			RecommendationStatus lRecStatus = lRec.getRecommendationStatus();
			if (lRecStatus != null) {
				if (recStatusListOfInterest.contains(lRecStatus)) {
					lSubset.add(lRec);
				}
			}
		}
		
		return lSubset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recommendationIdentifier == null) ? 0 : recommendationIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Recommendation))
			return false;
		Recommendation other = (Recommendation) obj;
		if (recommendationIdentifier == null) {
			if (other.recommendationIdentifier != null)
				return false;
		} else if (!recommendationIdentifier.equals(other.recommendationIdentifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Recommendation [recommendationIdentifier="
				+ recommendationIdentifier + ", targetSeriesIdentifier="
				+ targetSeriesIdentifier + ", recommendationStatus="
				+ recommendationStatus + ", recommendedVaccine="
				+ recommendedVaccine + ", recommendationDate="
				+ recommendationDate + ", recommendationReason="
				+ recommendationReason + "]";
	}
	
}