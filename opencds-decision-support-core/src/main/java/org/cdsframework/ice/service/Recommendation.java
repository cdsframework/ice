/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.cdsframework.ice.util.TimePeriod;
import org.cdsframework.ice.util.TimePeriod.DurationType;
import org.kie.api.definition.type.ClassReactive;
import org.springframework.util.ObjectUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Interim recommendation class to store information that is used to determine a final recommendation. All information stored in instantiated Recommendation objects are utilized to determine
 * the recommended status, recommended vaccine, recommendation (i.e.- forecast) dates, and recommendation reasons. Note, however, that this class only stores one forecast date. If additional dates are
 * to be considered for the forecast, it must be added to a separate instantiated object. Various forecast dates are taken into consideration by different rules (e.g. - age and interval rules) and by
 * the type of forecast (e.g. - earliest, recommended, and latest recommended).
 */
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ClassReactive
public class Recommendation
{
    public enum RecommendationDateType
    {
        EARLIEST,
        EARLIEST_RECOMMENDED,
        LATEST_RECOMMENDED
    }

    /**
     * Return a subset of the supplied Recommendation List with a List of those Recommendations that have the same RecommendationStatuses as the ones
     * supplied. If the supplied recommendation list is null or is empty, return an empty list. If there are no recommendations in the list with the
     * supplied RecommendsationStatuses,
     * return an empty list.
     */
    public static List<Recommendation> getRecommendationListSubsetWithSpecifiedStatuses(final List<Recommendation> recList,
            final List<RecommendationStatus> recStatusListOfInterest)
    {
        if (ObjectUtils.isEmpty(recList) || ObjectUtils.isEmpty(recStatusListOfInterest))
            return List.of();

        return recList.stream()
                .filter(lRec -> Optional.ofNullable(lRec.getRecommendationStatus())
                        .map(recStatusListOfInterest::contains)
                        .orElse(Boolean.FALSE))
                .toList();
    }

    public static Date obtainMostRecentEarliestDateFromRecommendationsList(final List<Recommendation> recommendationsList)
    {
        if (recommendationsList == null)
            return null;

        return recommendationsList.stream().map(Recommendation::getEarliestDate).max(Date::compareTo).orElse(null);
    }

    public static Date obtainMostRecentRecommendationDateFromRecommendationsList(final List<Recommendation> recommendationsList)
    {
        if (recommendationsList == null)
            return null;

        return recommendationsList.stream().map(Recommendation::getRecommendationDate).max(Date::compareTo).orElse(null);
    }

    public static Date obtainMostRecentLatestRecommendationDateFromRecommendationsList(
            final List<Recommendation> recommendationsList)
    {
        if (recommendationsList == null)
            return null;

        return recommendationsList.stream().map(Recommendation::getLatestRecommendationDate).max(Date::compareTo).orElse(null);
    }

    @EqualsAndHashCode.Include
    private final String recommendationIdentifier;
    private final String targetSeriesIdentifier;
    private String targetDoseIdentifier;
    private RecommendationStatus recommendationStatus;
    private Vaccine recommendedVaccine;
    private Date earliestDate;
    private Date recommendationDate;
    private Date latestRecommendationDate;
    private String recommendationReason;

    /**
     * Initializes a Recommendation object; recommendationReason is set to empty (it is never null), TargetSeriesIdentifier to the supplied
     * TargetSeries tSeriesIdentifier, and all other attributes to null
     *
     * @throws IllegalArgumentException if supplied identifier is null
     */
    public Recommendation(final TargetSeries pTS) throws IllegalArgumentException
    {
        final String _METHODNAME = "Recommendation(): ";
        if (pTS == null)
        {
            final String errStr = "Supplied target series identifier is null";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        recommendationIdentifier = ICELogicHelper.generateUniqueString();
        targetSeriesIdentifier = pTS.getTargetSeriesIdentifier();
        recommendedVaccine = null;
        earliestDate = null;
        recommendationDate = null;
        latestRecommendationDate = null;
        recommendationStatus = RecommendationStatus.NOT_FORECASTED;
        recommendationReason = null;
    }

    public Recommendation(final TargetSeries pTS, final TargetDose pTD) throws IllegalArgumentException
    {
        this(pTS);

        final String _METHODNAME = "Recommendation(): ";
        if (pTD == null)
        {
            final String errStr = "Supplied target dose identifier is null";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        targetDoseIdentifier = pTD.getUniqueId();
    }

    /**
     * Set the RecommendationStatus for this recommended. If the supplied parameter is null, RecommendationStatus is not changed
     */
    public void setRecommendationStatus(final RecommendationStatus recommendationStatus)
    {
        if (recommendationStatus == null)
            return;

        this.recommendationStatus = recommendationStatus;
    }

    /**
     * Sets the latest recommendation date to the overdue date - 1 days
     */
    public void setOverdueDate(final Date overdueDate)
    {
        if (overdueDate == null)
            this.latestRecommendationDate = null;
        else
            setLatestRecommendationDate(TimePeriod.addTimePeriod(overdueDate, new TimePeriod(-1, DurationType.DAYS)));
    }

    @Override
    public String toString()
    {
        return "Recommendation [recommendationIdentifier=%s, targetSeriesIdentifier=%s, recommendationStatus=%s, recommendedVaccine=%s, recommendationDate=%s, recommendationReason=%s]".formatted(
                recommendationIdentifier, targetSeriesIdentifier, recommendationStatus, recommendedVaccine, recommendationDate,
                recommendationReason);
    }
}