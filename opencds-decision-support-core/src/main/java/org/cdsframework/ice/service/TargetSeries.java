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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.service.Recommendation.RecommendationDateType;
import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationReason;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.util.TimePeriod;
import org.cdsframework.ice.util.TimePeriod.DurationType;
import org.cdsframework.ice.util.TimePeriodException;
import org.kie.api.definition.type.ClassReactive;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.springframework.util.ObjectUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ClassReactive
public class TargetSeries
{
    private enum EvaluationType
    {
        MINIMUM_AGE,
        MAXIMUM_AGE
    }

    /*
     * TargetSeriesComparator Note: this comparator imposes orderings that are inconsistent with equals.
     */
    private static class TargetSeriesComparator implements Comparator<TargetDose>
    {
        @Override
        public int compare(final TargetDose a, final TargetDose b)
        {
            if (a == null && b == null)
                return 0;

            if (a == null)
                return 1;

            if (b == null)
                return -1;

            final String uniqueIdA = a.getUniqueId();
            final String uniqueIdB = b.getUniqueId();

            if (uniqueIdA != null && uniqueIdA.equals(uniqueIdB))
                return 0;

            final LocalDate aDate = a.getAdministrationDate();
            final LocalDate bDate = b.getAdministrationDate();

            if (aDate == null && bDate == null)
                return 0;

            if (aDate == null)
                return 1;

            final int comparison = aDate.compareTo(bDate);
            if (comparison == 0)
                return 1;

            return comparison;
        }
    }

    @EqualsAndHashCode.Include
    private final String targetSeriesIdentifier;
    private final Schedule scheduleBackingSeries;
    private final LocalDate seriesStartAgeDate;
    private final LocalDate seriesEndAgeDate;
    private final Map<String, Integer> interimEvaluationValidityCountByDisease;
    // Disease -> evaluation validity count for disease
    private final Map<String, Map<Integer, Integer>> interimDosesToSkipByDisease;
    // Disease -> skip dose instructions for disease
    private final Map<String, LocalDate> diseaseImmunityDate;
    private final List<LocalDate> liveVirusDatesAccountedForInRecommendedFinalEarliestDate;
    private final List<LocalDate> liveVirusDatesAccountedForInRecommendedFinalDate;
    private final List<LocalDate> adjuvantDatesAccountedForInRecommendedFinalEarliestDate;
    private final List<LocalDate> adjuvantDatesAccountedForInRecommendedFinalDate;
    private final List<String> seriesRulesProcessed;
    private SeriesRules seriesRules;
    private NavigableSet<TargetDose> targetDoses;
    private Season targetSeason;
    private boolean seriesComplete;
    private boolean seriesCompleteFlagManuallySet;
    private int seriesCompleteAtDoseNumber;
    private boolean selectedSeries;
    private boolean immunityToAllDiseasesRecorded;
    private boolean historyEvaluationInitiated;
    private boolean postForecastCheckCompleted;
    private int manuallySetDoseNumberToRecommend;
    private List<Recommendation> interimRecommendationsScheduleEarliestAge;
    private List<Recommendation> interimRecommendationsScheduleEarliestInterval;
    private List<Recommendation> interimRecommendationsScheduleEarliestRecommendedAge;
    private List<Recommendation> interimRecommendationsScheduleEarliestRecommendedInterval;
    private List<Recommendation> interimRecommendationsScheduleLatestRecommendedAge;
    private List<Recommendation> interimRecommendationsScheduleLatestRecommendedInterval;
    private List<Recommendation> interimRecommendationsCustom;
    private List<Recommendation> interimRecommendationsCustomEarliest;
    private List<Recommendation> interimRecommendationsCustomLatest;
    // Disease -> disease immunity date
    private Boolean manuallySetAccountForLiveVirusIntervalsInRecommendation;
    private Vaccine recommendationVaccine;
    private RecommendationStatus recommendationStatus;
    private RecommendationStatus recommendationStatusPrior;
    private LocalDate finalEarliestDate;
    private LocalDate finalRecommendationDate;
    private LocalDate finalOverdueDate;
    private List<Recommendation> finalRecommendations;
    private boolean displayForecastDateForConditionalRecommendations;

    /**
     * TargetSeries constructor
     *
     * @param pSeriesRules           SeriesRules parameter, must be provided
     * @param pScheduleBackingSeries Schedule parameter, must be provided
     * @throws IllegalArgumentException If SeriesRules or Schedule parameter not populated or improperly populated
     */
    public TargetSeries(final SeriesRules pSeriesRules, final Schedule pScheduleBackingSeries, final EvaluatedPerson pP)
    {
        final String _METHODNAME = "TargetSeries(SeriesRules, Schedule): ";

        if (pSeriesRules == null || pScheduleBackingSeries == null)
        {
            final String errStr = "SeriesRules, EvalTime and/or Schedule parameter was not supplied";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        if (pP == null || pP.getDemographics() == null || pP.getDemographics().getBirthTime() == null)
        {
            final String errStr = "EvaluatedPerson parameter does not contain the birthdate of the patient being evaluated";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        targetSeriesIdentifier = ICELogicHelper.generateUniqueString();
        scheduleBackingSeries = pScheduleBackingSeries;
        seriesRules = pSeriesRules;
        targetDoses = new TreeSet<>(new TargetSeriesComparator()); // Ordered by administration date
        targetSeason = null;
        seriesComplete = false;
        selectedSeries = false;
        seriesCompleteAtDoseNumber = 0;
        postForecastCheckCompleted = false;
        historyEvaluationInitiated = false;
        manuallySetDoseNumberToRecommend = 0;
        immunityToAllDiseasesRecorded = false;
        seriesRulesProcessed = new ArrayList<>();
        seriesCompleteFlagManuallySet = false;
        interimRecommendationsScheduleEarliestAge = new ArrayList<>();
        interimRecommendationsScheduleEarliestInterval = new ArrayList<>();
        interimRecommendationsScheduleEarliestRecommendedAge = new ArrayList<>();
        interimRecommendationsScheduleEarliestRecommendedInterval = new ArrayList<>();
        interimRecommendationsScheduleLatestRecommendedAge = new ArrayList<>();
        interimRecommendationsScheduleLatestRecommendedInterval = new ArrayList<>();
        interimRecommendationsCustom = new ArrayList<>();
        interimRecommendationsCustomEarliest = new ArrayList<>();
        interimRecommendationsCustomLatest = new ArrayList<>();

        manuallySetAccountForLiveVirusIntervalsInRecommendation = null;
        liveVirusDatesAccountedForInRecommendedFinalEarliestDate = new ArrayList<>();
        liveVirusDatesAccountedForInRecommendedFinalDate = new ArrayList<>();
        adjuvantDatesAccountedForInRecommendedFinalEarliestDate = new ArrayList<>();
        adjuvantDatesAccountedForInRecommendedFinalDate = new ArrayList<>();
        recommendationVaccine = null;
        recommendationStatus = RecommendationStatus.NOT_FORECASTED;
        recommendationStatusPrior = null;
        finalRecommendations = new ArrayList<>();
        finalEarliestDate = null;
        finalRecommendationDate = null;
        finalOverdueDate = null;
        displayForecastDateForConditionalRecommendations = false;

        if (seriesRules.getSeriesStartAge() != null)
            seriesStartAgeDate = TimePeriod.addTimePeriod(pP.getDemographics().getBirthTime(), seriesRules.getSeriesStartAge());
        else
            seriesStartAgeDate = null;
        if (seriesRules.getSeriesEndAge() != null)
            seriesEndAgeDate = TimePeriod.addTimePeriod(pP.getDemographics().getBirthTime(), seriesRules.getSeriesEndAge());
        else
            seriesEndAgeDate = null;

        interimEvaluationValidityCountByDisease = new HashMap<>();
        interimDosesToSkipByDisease = new HashMap<>();
        final Collection<String> targetedDiseases =
                pScheduleBackingSeries.getDiseasesTargetedByVaccineGroup(pSeriesRules.getVaccineGroup());
        if (targetedDiseases != null)
        {
            for (final String disease : targetedDiseases)
            {
                interimEvaluationValidityCountByDisease.put(disease, 0);
                interimDosesToSkipByDisease.put(disease, new HashMap<>());
            }
        }

        diseaseImmunityDate = new HashMap<>();
    }

    /**
     * Constructs a TargetSeries for a single invocation. If the Season parameter is not valid (i.e. - is not supported by the underlying SeriesRules),
     * an IllegalArgumentException is thrown
     */
    // public TargetSeries(SeriesRules pSeriesRules, Schedule pScheduleBackingSeries, Season pTargetSeason, Date pEvalTime) {
    public TargetSeries(final SeriesRules pSeriesRules, final Schedule pScheduleBackingSeries, final Season pTargetSeason,
            final EvaluatedPerson pP)
    {
        this(pSeriesRules, pScheduleBackingSeries, pP);

        final String _METHODNAME = "TargetSeries(SeriesRules, Schedule, Season): ";
        if (pTargetSeason == null || pTargetSeason.isDefaultSeason())
            throw new IllegalArgumentException(_METHODNAME
                    + "Season parameter not specified or specified as a default season (default seasons not permitted)");

        boolean foundApplicableSeason = false;
        final List<Season> seriesRulesSeasons = this.seriesRules.getSeasons();
        if (seriesRulesSeasons != null)
        {
            for (final Season s : seriesRulesSeasons)
            {
                if (s.seasonsHaveEquivalentStartAndEndDates(pTargetSeason) || !s.seasonOverlapsWith(pTargetSeason))
                {
                    foundApplicableSeason = true;
                    break;
                }
            }

            if (foundApplicableSeason)
                this.targetSeason = pTargetSeason;
        }

        if (!foundApplicableSeason)
        {
            final String errStr = "Season parameter specified is inconsistent with Series supporting data for this TargetSeries";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }
    }

    /**
     * Switch the dose rules to follow in this TargetSeries to that of the specified series, starting from the specified dose number. May only
     * switch the doses to another series within the same vaccine group of this TargetSeries
     *
     * @throws IllegalArgumentException if the series does not exist, or the dose number from which to switch to does not exist in the specified series
     */
    public void convertToSpecifiedSeries(final String seriesToConvertTo, final int doseNumberFromWhichToBeginSwitch,
            final boolean useDoseIntervalOfPriorDoseFromSwitchToSeries) throws InconsistentConfigurationException
    {
        convertToSpecifiedSeries(seriesToConvertTo, doseNumberFromWhichToBeginSwitch, doseNumberFromWhichToBeginSwitch,
                useDoseIntervalOfPriorDoseFromSwitchToSeries);
    }

    private void convertToSpecifiedSeries(final String seriesToConvertTo, final int doseNumberFromWhichToBeginSwitch,
            final int doseNumberToSwitchTo, final boolean useDoseIntervalOfPriorDoseFromSwitchToSeries)
            throws InconsistentConfigurationException
    {
        final String _METHODNAME = "switchSeries(): ";
        if (seriesToConvertTo == null)
        {
            final String str = _METHODNAME + "series specified is null";
            log.error(str);
            throw new IllegalArgumentException(str);
        }

        if (doseNumberFromWhichToBeginSwitch != doseNumberToSwitchTo)
        {
            // For now, mechanisms are not in place permit/support moving from one dose number to a different dose number; additionally, it does not fit into
            // the definition of converting to a series. (E.g. - doesn't make sense to go from dose 3 of "from" series to dose 2 of "to" series)
            final String lErrStr =
                    "Dose number from series to switch from does not equal the dose number of the series being switched to; cannot continue.";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        // Get the specified SeriesRules
        final SeriesRules srOfSeriesToSwitchTo =
                scheduleBackingSeries.getScheduleSeriesByName(this.seriesRules.getVaccineGroup(), seriesToConvertTo);
        if (srOfSeriesToSwitchTo == null)
        {
            final String str = _METHODNAME + "specified series not found; cannot continue.";
            log.error(str);
            throw new IllegalArgumentException(str);
        }

        if (srOfSeriesToSwitchTo.getNumberOfDosesInSeries() < doseNumberToSwitchTo)
        {
            final String str = _METHODNAME
                    + "number of doses in specified series is less than the specified dose number from which to begin the switch; cannot continue.";
            log.error(str);
            throw new IllegalArgumentException(str);
        }

        final List<DoseRule> doseRulesOfSeriesToSwitchTo = srOfSeriesToSwitchTo.getSeriesDoseRules();
        if (doseRulesOfSeriesToSwitchTo == null)
        {
            final String str = _METHODNAME + "List of DoseRules in switch series is null";
            log.error(str);
            throw new InconsistentConfigurationException(str);
        }

        final int sizeOfSeriesToSwitchTo = srOfSeriesToSwitchTo.getNumberOfDosesInSeries();
        if (doseRulesOfSeriesToSwitchTo.size() != sizeOfSeriesToSwitchTo)
        {
            final String str =
                    _METHODNAME + "Length of DoseRulesList in switch series does not equal reported size in switch series";
            log.error(str);
            throw new InconsistentConfigurationException(str);
        }

        // SeriesRules srOfThisSeries = this.seriesRules;
        final List<DoseRule> thisSeriesDoseRules = this.seriesRules.getSeriesDoseRules();
        if (thisSeriesDoseRules == null)
        {
            final String str = _METHODNAME + "specified series does not have any dose rules; cannot continue.";
            log.error(str);
            throw new InconsistentConfigurationException(str);
        }

        // Remove existing DoseRules starting with the existing dose number.
        final int numberOfDosesDefinedInThisSeriesRules = thisSeriesDoseRules.size();
        thisSeriesDoseRules.subList(doseNumberFromWhichToBeginSwitch - 1, numberOfDosesDefinedInThisSeriesRules).clear();
        if (log.isDebugEnabled())
        {
            log.debug(
                    "{}Series to switch to: {}; doseNumberFromWhichToBeginSwitch: {}; doseNumberToWhichToSwitch: {}; useDoseIntervalOfPriorDoseFromSwitchToSeries: {}",
                    _METHODNAME, seriesToConvertTo, doseNumberFromWhichToBeginSwitch, doseNumberToSwitchTo,
                    useDoseIntervalOfPriorDoseFromSwitchToSeries);

            final StringBuilder debugStr = new StringBuilder(_METHODNAME
                    + "After removing doseNumber-forward existing DoseRules from this Series, the following DoseRules remain: ");
            for (final DoseRule dr : this.seriesRules.getSeriesDoseRules())
            {
                debugStr.append("(dose #: ")
                        .append(dr.getDoseNumber())
                        .append(") absoluteMinimumAge: ")
                        .append(dr.getAbsoluteMinimumAge())
                        .append(" minAge: ")
                        .append(dr.getMinimumAge())
                        .append(" absoluteMinimumInterval: ")
                        .append(dr.getAbsoluteMinimumInterval())
                        .append(" minInterval: ")
                        .append(dr.getMinimumInterval())
                        .append(" recommendedAge: ")
                        .append(dr.getEarliestRecommendedAge())
                        .append(" recommendedInterval ")
                        .append(dr.getEarliestRecommendedAge())
                        .append("; ");
            }
            log.debug(debugStr.toString());
        }

        // If the specified dose number is > 1, then modify the interval values from the series to switch to and change only those interval values leaving the remaining
        if (useDoseIntervalOfPriorDoseFromSwitchToSeries && doseNumberFromWhichToBeginSwitch > 1)
        {
            final DoseRule thisSeriesLastDoseRuleFromPriorSeries = thisSeriesDoseRules.get(doseNumberFromWhichToBeginSwitch - 2);
            final DoseRule newSeriesPriorDoseRuleForIntervalOnly =
                    srOfSeriesToSwitchTo.getSeriesDoseRuleByDoseNumber(doseNumberToSwitchTo - 1);
            thisSeriesLastDoseRuleFromPriorSeries.setAbsoluteMinimumInterval(
                    newSeriesPriorDoseRuleForIntervalOnly.getAbsoluteMinimumInterval());
            thisSeriesLastDoseRuleFromPriorSeries.setMinimumInterval(newSeriesPriorDoseRuleForIntervalOnly.getMinimumInterval());
            thisSeriesLastDoseRuleFromPriorSeries.setEarliestRecommendedInterval(
                    newSeriesPriorDoseRuleForIntervalOnly.getEarliestRecommendedInterval());
            thisSeriesLastDoseRuleFromPriorSeries.setLatestRecommendedInterval(
                    newSeriesPriorDoseRuleForIntervalOnly.getLatestRecommendedInterval());
        }

        // Add new DoseRules starting with the existing dose number - properly set the dose number to ensure they are sequential
        final List<DoseRule> ssDoseRulesToAdd =
                srOfSeriesToSwitchTo.getSeriesDoseRules().subList(doseNumberToSwitchTo - 1, sizeOfSeriesToSwitchTo);
        if (log.isDebugEnabled())
        {
            final StringBuilder debugStr = new StringBuilder(_METHODNAME + "Switch series doses to add: ");
            for (final DoseRule dr : ssDoseRulesToAdd)
            {
                debugStr.append("(dose #: ")
                        .append(dr.getDoseNumber())
                        .append(") absoluteMinimumAge: ")
                        .append(dr.getAbsoluteMinimumAge())
                        .append(" minAge: ")
                        .append(dr.getMinimumAge())
                        .append(" absoluteMinimumInterval: ")
                        .append(dr.getAbsoluteMinimumInterval())
                        .append(" minInterval: ")
                        .append(dr.getMinimumInterval())
                        .append(" recommendedAge: ")
                        .append(dr.getEarliestRecommendedAge())
                        .append(" recommendedInterval ")
                        .append(dr.getEarliestRecommendedAge())
                        .append("; ");
            }
            log.debug(debugStr.toString());
        }
        // Change each DoseRule's doseNumber (the dose rules to add) to the proper dose number
        int lDoseNumberFromWhichToSwitchInd = doseNumberFromWhichToBeginSwitch;
        for (final DoseRule dr : ssDoseRulesToAdd)
        {
            dr.setDoseNumber(lDoseNumberFromWhichToSwitchInd);
            lDoseNumberFromWhichToSwitchInd++;
        }
        thisSeriesDoseRules.addAll(ssDoseRulesToAdd);
        if (log.isDebugEnabled())
        {
            final StringBuilder debugStr =
                    new StringBuilder(_METHODNAME + "Final set of DoseRules with switch series doses added: ");
            for (final DoseRule dr : this.seriesRules.getSeriesDoseRules())
            {
                debugStr.append("(dose #: ")
                        .append(dr.getDoseNumber())
                        .append(") absoluteMinimumAge: ")
                        .append(dr.getAbsoluteMinimumAge())
                        .append(" minAge: ")
                        .append(dr.getMinimumAge())
                        .append(" absoluteMinimumInterval: ")
                        .append(dr.getAbsoluteMinimumInterval())
                        .append(" minInterval: ")
                        .append(dr.getMinimumInterval())
                        .append(" recommendedAge: ")
                        .append(dr.getEarliestRecommendedAge())
                        .append(" recommendedInterval ")
                        .append(dr.getEarliestRecommendedAge())
                        .append("; ");
            }
            log.debug(debugStr.toString());
        }
        this.seriesRules.setNumberOfDosesInSeries(thisSeriesDoseRules.size());
        this.seriesRules.setSeriesDoseRules(thisSeriesDoseRules);

        // Change the name of this series to the new series name
        this.seriesRules.setSeriesName(seriesToConvertTo);
    }

    public boolean containsRuleProcessed(final String ruleName)
    {
        return seriesRulesProcessed.contains(ruleName);
    }

    /**
     * Check to see if any of the shots administered was a live virus vaccine
     *
     * @return true if any of the shots administered was a live virus vaccine, false if not
     */
    public boolean oneOrMoreShotsAdministeredIsALiveVirusVaccine()
    {
        if (targetDoses == null)
            return false;

        return targetDoses.stream().anyMatch(d -> d.getVaccineComponent().isLiveVirusVaccine());
    }

    /**
     * Check if next recommended shot could be a live virus vaccine. Not subject to the manual override flag.
     *
     * @return true if recommended shot is a live virus vaccine or any vaccine that is in the vaccine group contains a live virus vaccine, false if recommended shot is
     * not a live virus vaccine, or if not recommendation has been made yet
     */
    public boolean isRecommendedVaccineOrVaccineGroupLevelRecommendationAnExpectedLiveVirusVaccineNoOverride()
    {
        if (this.recommendationStatus == null || (this.recommendationStatus != RecommendationStatus.RECOMMENDED
                && this.recommendationStatus != RecommendationStatus.RECOMMENDED_IN_FUTURE
                && this.recommendationStatus != RecommendationStatus.CONDITIONALLY_RECOMMENDED))
            return false;

        final int lTargetDoseNumber = determineEffectiveNumberOfDosesInSeries() + 1;
        final DoseRule dr = getSeriesRules().getSeriesDoseRuleByDoseNumber(lTargetDoseNumber);
        if (dr == null)
            return false;

        return dr.getAllPermittedVaccines().stream().anyMatch(AbstractVaccine::isLiveVirusVaccine);
    }

    /**
     * Check if next recommended shot is a live virus vaccine (and therefore recommendationStatus is either RECOMMENDED, RECOMMENDED_IN_FUTURE, or CONDITIONALLY_RECOMMENDED)
     *
     * @return true if recommended shot is a live virus vaccine or any vaccine that is in the vaccine group contains a live virus vaccine, false if recommended shot is
     * not a live virus vaccine, or if not recommendation has been made yet
     */
    public boolean isRecommendedVaccineOrVaccineGroupLevelRecommendationAnExpectedLiveVirusVaccine()
    {
        if (this.recommendationStatus == null || (this.recommendationStatus != RecommendationStatus.RECOMMENDED
                && this.recommendationStatus != RecommendationStatus.RECOMMENDED_IN_FUTURE
                && this.recommendationStatus != RecommendationStatus.CONDITIONALLY_RECOMMENDED))
            return false;

        final int lTargetDoseNumber = determineEffectiveNumberOfDosesInSeries() + 1;
        final DoseRule dr = getSeriesRules().getSeriesDoseRuleByDoseNumber(lTargetDoseNumber);
        if (dr == null)
            return false;

        return Objects.requireNonNullElseGet(manuallySetAccountForLiveVirusIntervalsInRecommendation,
                () -> dr.getAllPermittedVaccines().stream().anyMatch(AbstractVaccine::isLiveVirusVaccine));
    }

    /**
     * Check if next recommended shot is a live virus vaccine (and therefore recommendationStatus is either RECOMMENDED, RECOMMENDED_IN_FUTURE, or CONDITIONALLY_RECOMMENDED)
     *
     * @return true if recommended shot is a select adjuvant product vaccine or any vaccine that is in the vaccine group contains a select adjuvant product, false if
     * recommended shot is not a select adjuvant product, or if not recommendation has been made yet
     */
    public boolean isRecommendedVaccineOrVaccineGroupLevelRecommendationAnExpectedSelectAdjuvantProduct()
    {
        if (this.recommendationStatus == null || (this.recommendationStatus != RecommendationStatus.RECOMMENDED
                && this.recommendationStatus != RecommendationStatus.RECOMMENDED_IN_FUTURE
                && this.recommendationStatus != RecommendationStatus.CONDITIONALLY_RECOMMENDED))
            return false;

        final int lTargetDoseNumber = determineEffectiveNumberOfDosesInSeries() + 1;
        final DoseRule dr = getSeriesRules().getSeriesDoseRuleByDoseNumber(lTargetDoseNumber);
        if (dr == null)
            return false;

        return Objects.requireNonNullElseGet(manuallySetAccountForLiveVirusIntervalsInRecommendation,
                () -> dr.getAllPermittedVaccines().stream().anyMatch(AbstractVaccine::isSelectAdjuvantProduct));
    }

    public TargetDose getTargetDoseByAdministeredShotNumber(final int shotNumber)
    {
        if (targetDoses == null)
            return null;

        int i = 1;
        for (final TargetDose d : targetDoses)
        {
            if (i++ == shotNumber)
                return d;
        }

        return null;
    }

    public LocalDate getAdministrationDateOfTargetDoseByShotNumberNumber(final int shotNumber)
    {
        final TargetDose td = getTargetDoseByAdministeredShotNumber(shotNumber);
        if (td == null)
            return null;

        return td.getAdministrationDate();
    }

    /**
     * Skip the specified number of doses for all diseases associated with the target dose
     *
     * @throws IllegalArgumentException If doseNumberToSkipFrom and/or doseNumberToSkipTo is not possible for the series
     */
    public void addSkipDoseEntryForDose(final int doseNumberToSkipFrom, final int doseNumberToSkipTo)
    {
        if (doseNumberToSkipFrom == doseNumberToSkipTo)
            return;

        final int lTargetDoseNumber = determineDoseNumberInSeries();
        for (final String lSDC : getDiseasesSupportedByThisSeries())
            addSkipDoseEntryForSpecifiedDisease(doseNumberToSkipFrom, doseNumberToSkipTo, lSDC, lTargetDoseNumber);
    }

    /**
     * Skip the specified number of doses
     *
     * @throws IllegalArgumentException If doseNumberToSkipFrom and/or doseNumberToSkipTo is not possible for the series. The doseNumberToSkipFrom must be less than the doseNumberToSkipTo,
     *                                  and the doseNumberToSkipFrom must equal the target dose number in this series. Also, if the supplied argument is not a known disease as available through the supporting data
     */
    public void addSkipDoseEntryForSpecifiedDisease(final int pDoseNumberToSkipFrom, final int pDoseNumberToSkipTo,
            final String pDisease)
    {
        if (pDoseNumberToSkipFrom == pDoseNumberToSkipTo)
            return;

        addSkipDoseEntryForSpecifiedDisease(pDoseNumberToSkipFrom, pDoseNumberToSkipTo, pDisease, determineDoseNumberInSeries());
    }

    /**
     * Skip the specified number of doses
     *
     * @param pTargetDoseNumber If set to <= 0, this method will determine the target dose number of the series to check that the doseNumberToSkipFrom is equal to the target dose number;
     *                          otherwise it will use the targetDoseNumber value provided to check
     * @throws IllegalArgumentException If doseNumberToSkipFrom and/or doseNumberToSkipTo is not possible for the series. The doseNumberToSkipFrom must be less than the doseNumberToSkipTo,
     *                                  and the doseNumberToSkipFrom must equal the target dose number in this series
     */
    private void addSkipDoseEntryForSpecifiedDisease(final int pDoseNumberToSkipFrom, final int pDoseNumberToSkipTo,
            final String pDisease, final int pTargetDoseNumber)
    {
        final String _METHODNAME = "skipToDosesNumberForSpecifiedDisease(): ";

        if (pDisease == null)
        {
            final String lErrStr = "No disease was specified in an attempt to add a skip dose entry";
            log.warn(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        // HERE - check that it is a valid disease
        if (this.scheduleBackingSeries.getICESupportingDataConfiguration()
                .getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.DISEASE, new CdsConcept(pDisease)) == null)
        {
            final String lErrStr =
                    "An invalid disease was specified in an attempt to add a skip dose entry; disease specified: " + pDisease;
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final int numberOfDosesInSeries = getSeriesRules().getNumberOfDosesInSeries();
        if (pDoseNumberToSkipTo < 1 || pDoseNumberToSkipTo > numberOfDosesInSeries || pDoseNumberToSkipFrom < 1
                || pDoseNumberToSkipFrom > numberOfDosesInSeries)
        {
            final String errStr =
                    "number of doses in series %s is %d; dose number to skip from (%d) or dose number to skip to (%d) is not valid".formatted(
                            seriesRules.getSeriesName(), numberOfDosesInSeries, pDoseNumberToSkipFrom, pDoseNumberToSkipTo);
            log.warn(_METHODNAME + "{}", errStr);
            throw new IllegalArgumentException(errStr);
        }

        if (pDoseNumberToSkipTo == pDoseNumberToSkipFrom)
        {
            log.warn("dose number to skip from ({}) is equal to the dose number to skip to ({}). Nothing to do.",
                    pDoseNumberToSkipFrom, pDoseNumberToSkipTo);
            return;
        }

        final int lTargetDoseNumber;
        if (pTargetDoseNumber <= 0)
            lTargetDoseNumber = determineDoseNumberInSeries();
        else
            lTargetDoseNumber = pTargetDoseNumber;
        if (pDoseNumberToSkipFrom != lTargetDoseNumber)
        {
            log.warn(_METHODNAME + "dose number to skip from ({}) does not equal the target dose number of the series ({})",
                    pDoseNumberToSkipFrom, lTargetDoseNumber);
            // AI:
            return;
        }

        final Map<Integer, Integer> skipDoseEntry = interimDosesToSkipByDisease.get(pDisease);
        if (skipDoseEntry != null)
            skipDoseEntry.put(pDoseNumberToSkipFrom, pDoseNumberToSkipTo);

        interimEvaluationValidityCountByDisease.put(pDisease, pDoseNumberToSkipTo - 1);
    }

    public int determineDoseNumberInSeries()
    {
        final int lEffectiveNumberOfDoses = determineEffectiveNumberOfDosesInSeries();
        final Integer lEffectiveDoseNumberPlus1Int = lEffectiveNumberOfDoses + 1;

        // If dose number determined by disease count and there is a skip dose from the (next) target dose for all other diseases in this target series, take that into account
        if (this.seriesRules.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered())
        {
            final int lNumberOfDiseasesCoveredByThisTS = this.interimDosesToSkipByDisease.size();
            int lNumberOfDiseasesWithSkipDoseAtNextTargetDose = 0;
            int lLowestCount = lEffectiveNumberOfDoses;
            for (final Map<Integer, Integer> lSkipDose : interimDosesToSkipByDisease.values())
            {
                if (lSkipDose != null && lSkipDose.containsKey(lEffectiveDoseNumberPlus1Int))
                {
                    final int lSkipDoseTo = lSkipDose.get(lEffectiveDoseNumberPlus1Int);
                    if (lLowestCount == lEffectiveNumberOfDoses || lSkipDoseTo < lLowestCount)
                        lLowestCount = lSkipDoseTo;
                    lNumberOfDiseasesWithSkipDoseAtNextTargetDose++;
                }
            }

            return (lNumberOfDiseasesCoveredByThisTS == lNumberOfDiseasesWithSkipDoseAtNextTargetDose)
                   ? lLowestCount
                   : lEffectiveNumberOfDoses + 1;
        }
        // END taking skip dose into account

        return lEffectiveNumberOfDoses + 1;
    }

    public int determineDoseNumberInSeriesForDiseasesTargetedByThisDose(final TargetDose pTD)
    {
        if (pTD == null || pTD.getVaccineComponent() == null)
            return 0;

        // Call below does not update the state of this object
        return doseNumberDeterminationUpdateUtility(pTD, true, false, true,
                pTD.getVaccineComponent().getAllDiseasesTargetedForImmunity());
    }

    /**
     * Determines the series dose number of the specified dose.
     */
    public int determineDoseNumberInSeries(final TargetDose pTD)
    {
        // Call below _does not_ update the internal state of this object
        return doseNumberDeterminationUpdateUtility(pTD, true, false, true, interimEvaluationValidityCountByDisease.keySet());
    }

    /**
     * Update the dose validity count for the series and return the resulting series dose number (based on the supplied TargetDose).
     * Should be called if the dose validity was just set to VALID. Updates the internal state of this object.
     *
     * @param pTD TargetDose in question
     * @return the dose number for the specified TargetDose
     */
    public int updateDoseValidityCountAndReturnActualDoseCountInSeries(final TargetDose pTD)
    {
        final String _METHODNAME = "updateDoseValidityCountAndReturnActualDoseCountInSeries(): ";

        // Call below _does update_ the internal state of this object
        final int i =
                doseNumberDeterminationUpdateUtility(pTD, false, true, true, interimEvaluationValidityCountByDisease.keySet());
        //if (i == 0) {
        //	return 1;
        //}
        //else {
        log.debug(_METHODNAME + "returning {}", i);
        return i;
        //}
    }

    /**
     * Update the dose validity count for the series and return the resulting series dose number (based on the supplied TargetDose).
     * Should be called if the dose validity was just set to VALID
     *
     * @param pTD TargetDose in question
     * @return the dose number for the specified TargetDose
     */
    public int updateDoseValidityCountAndReturnTargetDoseNumberInSeries(final TargetDose pTD)
    {
        return doseNumberDeterminationUpdateUtility(pTD, true, true, true, interimEvaluationValidityCountByDisease.keySet());
    }

    /**
     * Determines the dose number of the specified target dose. Updates the series dose number for the series
     *
     * @param pTD                                 TargetDose to determine dose number for; if null and the number of targetDoses in this series is 0, returns the target dose number assuming no shots
     * @param updateInternalSeriesDoseNumberCount if set to true, increment the validity counters for all of the diseases
     * @param antigensToIncludeInDetermination    limit which diseases to look at in the determination of the dose number. Under usual circumstances, use
     *                                            interimDiseaseEvaluationValidityCount.keySet(), to take into account all diseases for which this series induces immunity
     * @param takeSkipDoseEntriesIntoAccount      If set to true, take any skip dose entries into account when determining dose number
     * @return the dose number or target dose number; or -1 if pTD is null or if it is not null but there are shots administered in this target series
     */
    private int doseNumberDeterminationUpdateUtility(final TargetDose pTD, final boolean returnTargetDoseNumber,
            final boolean updateInternalSeriesDoseNumberCount, final boolean takeSkipDoseEntriesIntoAccount,
            final Collection<String> antigensToIncludeInDetermination)
    {
        final String _METHODNAME = "doseNumberDeterminationUpdateUtility(): ";

        // If no shots administered, return dose number based solely on the count by diseases
        if (pTD != null && ObjectUtils.isEmpty(targetDoses))
        {
            final String str =
                    "Supplied TargetDose parameter is not null but there are no administered shots in this target series";
            log.warn(_METHODNAME + str);
            return -1;
        }

        if (pTD == null)
        {
            final String str = "Supplied TargetDose parameter is null";
            log.warn(_METHODNAME + str);
            return -1;
        }
        // END if no shots administered

        int highestSkipDoseNumberToEntry = 0;
        int highestNonSkipDoseNumberToEntry = 0;

        // Tally up objects for each disease, that is-- how many valid doses for each disease
        final Map<String, Integer> tallyOfDoseNumberByDisease = new HashMap<>();        // Disease -> dose number
        final Map<String, Integer> tallyOfRelevantDiseaseImmunity = new HashMap<>();    // Disease -> disease immunity count

        // Initialize tally for supported diseases
        for (final String disease : antigensToIncludeInDetermination)
            tallyOfDoseNumberByDisease.put(disease, 0);

        // Record tally for disease immunity
        final LocalDate targetDoseDate = pTD.getAdministrationDate();
        final Integer numberOfDosesInSeriesInt = getSeriesRules().getNumberOfDosesInSeries();
        for (final String sdc : this.diseaseImmunityDate.keySet())
        {
            final LocalDate lDiseaseImmunityDate = this.diseaseImmunityDate.get(sdc);
            if (targetDoseDate != null && lDiseaseImmunityDate != null && !targetDoseDate.isBefore(lDiseaseImmunityDate))
            {
                this.interimEvaluationValidityCountByDisease.put(sdc, numberOfDosesInSeriesInt);
                tallyOfRelevantDiseaseImmunity.put(sdc, numberOfDosesInSeriesInt);
            }
        }

        // Wherever disease immunity is recorded, remove the count from the tally so it does not skew the "least count" result
        final Collection<String> tallyOfRelevantDiseaseImmunityKeys = tallyOfRelevantDiseaseImmunity.keySet();
        if (!tallyOfRelevantDiseaseImmunityKeys.containsAll(tallyOfDoseNumberByDisease.keySet()))
        {
            for (final String sdc : tallyOfRelevantDiseaseImmunityKeys)
                tallyOfDoseNumberByDisease.remove(sdc);
        }

        final String pTDUniqueIdentifier = pTD.getUniqueId();
        TargetDose lPreviouslyProcessedTD = null;
        LocalDate lDuplicateShotSameDayValidDoseFoundDate = null;
        Set<String> lDuplicateShotDiseases = new HashSet<>();
        for (final TargetDose td : targetDoses)
        {
            if (!updateInternalSeriesDoseNumberCount && lPreviouslyProcessedTD != null && lPreviouslyProcessedTD.getUniqueId()
                    .equals(pTDUniqueIdentifier))
            {
                break;
            }

            if (lDuplicateShotSameDayValidDoseFoundDate != null && !td.getAdministrationDate()
                    .equals(lDuplicateShotSameDayValidDoseFoundDate))
            {
                lDuplicateShotSameDayValidDoseFoundDate = null;
                lDuplicateShotDiseases = new HashSet<>();
            }

            if (lDuplicateShotSameDayValidDoseFoundDate != null
                    && !this.seriesRules.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered()
                    && lPreviouslyProcessedTD.getAdministeredShotNumberInSeries() < td.getAdministeredShotNumberInSeries()
                    && td.getAdministrationDate().equals(lPreviouslyProcessedTD.getAdministrationDate()))
            {
                // Don't count prior duplicate shots if the disease immunity of the vaccines are not looked at from shot to shot
                continue;
            }

            final DoseStatus statusThisTD = td.getStatus();
            final Collection<String> diseasesTargetedByThisDose = td.getVaccineComponent().getAllDiseasesTargetedForImmunity();
            if (Collections.disjoint(diseasesTargetedByThisDose, antigensToIncludeInDetermination))
            {
                // There is no overlap in diseases between this dose and the antigens we're checking.
                lPreviouslyProcessedTD = td;
                continue;
            }

            for (final String diseaseTargeted : diseasesTargetedByThisDose)
            {
                final Integer numberOfValidDosesForDiseaseInt = tallyOfDoseNumberByDisease.get(diseaseTargeted);
                if (numberOfValidDosesForDiseaseInt != null)
                {
                    // Vaccine from this dose supports this disease in this series
                    final int numberOfValidDosesForDisease = numberOfValidDosesForDiseaseInt;
                    final int doseNumberForDisease = numberOfValidDosesForDisease + 1;
                    // BEGIN: Determine if the disease tally should be incremented or not - based on whether (1) this shot is valid; (2) it counts towards completion of the series,
                    // and/or (3) it is a duplicate shot, taking into account targeted diseases if this series bases its dose count on the count of targeted diseases
                    boolean lIncrementDoseNumber = false;
                    if (statusThisTD == DoseStatus.VALID && !td.isShotIgnored())
                    {
                        final boolean lDoseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered =
                                this.seriesRules.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered();
                        if (lDuplicateShotSameDayValidDoseFoundDate != null)
                        {
                            // If duplicate shot same day valid dose found date is not null, then it is equal to this shot date or it would have been null'd above
                            // If the diseases should be taken into account for this series in determining dose number, and this disease has already been accounted for
                            // on this day, then we have a duplicate shot
                            if (!lDoseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered)
                            {
                                lIncrementDoseNumber = true;
                            }
                            else
                                if (lDuplicateShotDiseases.contains(diseaseTargeted))
                                {
                                    lIncrementDoseNumber = true;
                                }
                                else
                                {
                                    lDuplicateShotDiseases.add(diseaseTargeted);
                                    lIncrementDoseNumber = true;
                                }
                        }
                        else
                        {
                            if (lPreviouslyProcessedTD != null && td.getAdministrationDate()
                                    .equals(lPreviouslyProcessedTD.getAdministrationDate()))
                            {
                                // The prior shot was the same date and this is the first shot of the same date that is valid. Find all shots of this date prior to this one
                                // and make note of the diseases targeted
                                int lInnerCountOfPriorValidShotsSameDay = 0;
                                for (final TargetDose lInnerTd : targetDoses)
                                {
                                    if (lInnerTd.getAdministeredShotNumberInSeries() >= td.getAdministeredShotNumberInSeries())
                                        break;
                                    if (lInnerTd.getStatus() == DoseStatus.VALID && lInnerTd.getAdministrationDate()
                                            .equals(td.getAdministrationDate()))
                                    {
                                        lInnerCountOfPriorValidShotsSameDay++;
                                        lDuplicateShotDiseases.addAll(
                                                lInnerTd.getVaccineComponent().getAllDiseasesTargetedForImmunity());
                                    }
                                }
                                if (!lDoseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered
                                        && lInnerCountOfPriorValidShotsSameDay > 0)
                                {
                                    lIncrementDoseNumber = true;
                                }
                                else
                                    if (lDoseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered
                                            && lDuplicateShotDiseases.contains(diseaseTargeted))
                                    {
                                        // A duplicate shot is noted. Make note of the diseases
                                        lIncrementDoseNumber = true;
                                    }
                                    else
                                    {
                                        // the lDoseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered is true
                                        lIncrementDoseNumber = true;
                                    }
                                lDuplicateShotSameDayValidDoseFoundDate = td.getAdministrationDate();
                                lDuplicateShotDiseases.add(diseaseTargeted);
                            }
                            else
                                lIncrementDoseNumber = true;
                        }
                    }
                    // END: determining whether or not disease tally should be tracked for this shot
                    if (!takeSkipDoseEntriesIntoAccount)
                    {
                        if (lIncrementDoseNumber)
                        {
                            tallyOfDoseNumberByDisease.put(diseaseTargeted, doseNumberForDisease);
                            if (doseNumberForDisease > highestNonSkipDoseNumberToEntry)
                                highestNonSkipDoseNumberToEntry = doseNumberForDisease;
                        }
                    }
                    else
                    {
                        final Map<Integer, Integer> skipDoseEntriesForDisease = interimDosesToSkipByDisease.get(diseaseTargeted);
                        final Integer skipDoseEntryFromInt = doseNumberForDisease;
                        if (skipDoseEntriesForDisease != null && skipDoseEntriesForDisease.containsKey(skipDoseEntryFromInt))
                        {
                            Integer skipDoseToInt = skipDoseEntriesForDisease.get(skipDoseEntryFromInt);
                            int skipDoseTo = skipDoseToInt;
                            if (statusThisTD != DoseStatus.VALID)
                            {
                                skipDoseTo = skipDoseTo - 1;
                                skipDoseToInt = skipDoseTo;
                            }
                            if (skipDoseTo > highestSkipDoseNumberToEntry)
                                highestSkipDoseNumberToEntry = skipDoseTo;
                            tallyOfDoseNumberByDisease.put(diseaseTargeted, skipDoseToInt);
                        }
                        else
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug(_METHODNAME + "dosenumber for disease: {}; dose number {}; else: {}", diseaseTargeted,
                                        doseNumberForDisease, td);
                            }
                            if (lIncrementDoseNumber)
                            {
                                tallyOfDoseNumberByDisease.put(diseaseTargeted, doseNumberForDisease);
                                if (doseNumberForDisease > highestNonSkipDoseNumberToEntry)
                                    highestNonSkipDoseNumberToEntry = doseNumberForDisease;
                            }
                        }
                    }
                }
            }
            lPreviouslyProcessedTD = td;
        }

        // Go through tally and for those diseases where skip dose is set, adjust it. If less than the dose to skip to, jump to the dose
        // number specified to skip to. If Remove all dose numbers tracked across immune diseases, but track the
        // highest dose number across them
        int leastDoseNumberAcrossDiseases = -1;
        int greatestDoseNumberAcrossDiseases = -1;
        for (final String sdc : tallyOfDoseNumberByDisease.keySet())
        {
            final Integer tallyDoseNumberInt = tallyOfDoseNumberByDisease.get(sdc);
            if (log.isDebugEnabled())
            {
                log.debug(_METHODNAME + "tally disease: {}", sdc);
                log.debug(_METHODNAME + "tally number: {}", (tallyDoseNumberInt != null) ? tallyDoseNumberInt : "null");
            }
            if (tallyDoseNumberInt != null)
            {
                final int tallyDoseNumber = tallyDoseNumberInt;
                // Record the leastDoseNumber
                if (leastDoseNumberAcrossDiseases == -1)
                {
                    leastDoseNumberAcrossDiseases = tallyDoseNumber;
                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "recorded least dose number {}", leastDoseNumberAcrossDiseases);
                    if (tallyDoseNumber > highestNonSkipDoseNumberToEntry)
                        highestNonSkipDoseNumberToEntry = tallyDoseNumber;
                }
                else
                    if (tallyDoseNumber < leastDoseNumberAcrossDiseases)
                    {
                        leastDoseNumberAcrossDiseases = tallyDoseNumber;
                        if (log.isDebugEnabled())
                            log.debug(_METHODNAME + "recorded least dose number {}", leastDoseNumberAcrossDiseases);
                    }
                // Now record the greatestDoseNunmber
                if (greatestDoseNumberAcrossDiseases == -1)
                    greatestDoseNumberAcrossDiseases = tallyDoseNumber;
                else
                    if (tallyDoseNumber > greatestDoseNumberAcrossDiseases)
                        greatestDoseNumberAcrossDiseases = tallyDoseNumber;
                if (log.isDebugEnabled())
                    log.debug(_METHODNAME + "leastdosenumber: {}", leastDoseNumberAcrossDiseases);
            }
        }

        for (final String sdc : diseaseImmunityDate.keySet())
        {
            if (!tallyOfDoseNumberByDisease.containsKey(sdc))
                tallyOfDoseNumberByDisease.put(sdc, leastDoseNumberAcrossDiseases);
        }

        if (updateInternalSeriesDoseNumberCount)
        {
            for (final String sdc : tallyOfDoseNumberByDisease.keySet())
            {
                if (tallyOfDoseNumberByDisease.containsKey(sdc))
                    this.interimEvaluationValidityCountByDisease.put(sdc, tallyOfDoseNumberByDisease.get(sdc));
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug(_METHODNAME
                            + "highestSkipDoseNumberToEntry: {}; highestNonSkipDoseNumberToEntry: {}; leastDoseNumberAcrossDiseases: {}; greatestDoseNumberAcrossDiseases: {}",
                    highestSkipDoseNumberToEntry, highestNonSkipDoseNumberToEntry, leastDoseNumberAcrossDiseases,
                    greatestDoseNumberAcrossDiseases);
        }

        int doseNumberToReturn = (this.seriesRules.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered())
                                 ? leastDoseNumberAcrossDiseases
                                 : greatestDoseNumberAcrossDiseases;
        if (returnTargetDoseNumber)
        {
            doseNumberToReturn = doseNumberToReturn + 1;
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Returning target count dose number: {}", doseNumberToReturn);
        }
        else
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Returning actual count dose number: {}", doseNumberToReturn);
        }

        return doseNumberToReturn;
    }

    /**
     * Get the last shot administered, null if none have been administered
     */
    public TargetDose getLastShotAdministeredInSeries()
    {
        if (targetDoses.isEmpty())
            return null;

        return targetDoses.last();
    }

    /**
     * Get last shot administered, excluding ignored shots
     */
    public TargetDose getLastShotAdministeredInSeriesExcludingIgnoredShots()
    {
        if (targetDoses.isEmpty())
            return null;

        final Iterator<TargetDose> tdIter = targetDoses.descendingIterator();
        while (tdIter.hasNext())
        {
            final TargetDose td = tdIter.next();
            if (!td.isShotIgnored())
                return td;
        }

        return null;
    }

    /**
     * Geth
     *
     * @return TargetDose, or null if not found
     */
    public TargetDose getValidShotByDoseNumber(final int doseNumber)
    {
        if (targetDoses.isEmpty())
            return null;

        return targetDoses.stream()
                .filter(td -> td.getDoseNumberInSeries() == doseNumber)
                .filter(td -> td.getStatus() == DoseStatus.VALID)
                .findFirst()
                .orElse(null);
    }

    /**
     * Invokes determineIfSeriesCompleteAndReturnLastDose(boolean exludeIgnoredShots) with excludeIgnoredShots set to true.
     */
    private void determineIfSeriesCompleteAndReturnLastDose()
    {
        determineIfSeriesCompleteAndReturnLastDose(false);
    }

    /**
     * Determines if the series is complete, updates the seriesComplete instance variable, and returns the last target dose in this TargetSeries. If the
     * excludeIgnoredShots parameter is set to true, the shot will only be returned if it is not a shot marked to be ignored.
     * Note that seriesCompleteFlagManuallySet is _not_ updated by this method.
     *
     * @param excludeIgnoredShots indicates whether an ignored shot may be returned.
     * @return last dose in the series, regardless of whether the series is complete or null is no doses have been administered
     */
    private TargetDose determineIfSeriesCompleteAndReturnLastDose(final boolean excludeIgnoredShots)
    {
        TargetDose lastDoseAdministered = null;

        final int numberOfEffectiveDoses = determineEffectiveNumberOfDosesInSeries();

        if (!excludeIgnoredShots && !targetDoses.isEmpty())
            lastDoseAdministered = targetDoses.last();
        else
            if (excludeIgnoredShots && !targetDoses.isEmpty())
            {
                final Iterator<TargetDose> tdIter = targetDoses.descendingIterator();
                while (tdIter.hasNext())
                {
                    final TargetDose td = tdIter.next();
                    if (!td.isShotIgnored())
                    {
                        lastDoseAdministered = td;
                        break;
                    }
                }
            }
            else
            {
                if (!seriesCompleteFlagManuallySet)
                {
                    if (seriesRules.getNumberOfDosesInSeries() == 0)
                    {
                        seriesComplete = true;
                        seriesCompleteAtDoseNumber = 1;
                    }
                    else
                        seriesComplete = false;

                    return null;
                }
            }

        if (!seriesCompleteFlagManuallySet)
        {
            final int numberOfDosesInSeriesRule = seriesRules.getNumberOfDosesInSeries();
            if (lastDoseAdministered != null)
            {
                if (numberOfEffectiveDoses >= numberOfDosesInSeriesRule)
                {
                    seriesComplete = true;
                    setSeriesCompleteAtSpecifiedDoseNumber(numberOfEffectiveDoses);
                }
                else
                    seriesComplete = false;
            }
            else
                seriesComplete = false;
        }

        return lastDoseAdministered;
    }

    /**
     * Return a simple count of valid shots administered in this series. Does NOT take into account immunity or skipped doses!
     */
    public int determineNumberOfDosesAdministeredInSeries()
    {
        if (targetDoses == null)
            return 0;

        return Math.toIntExact(targetDoses.stream().filter(TargetDose::getIsValid).count());
    }

    /**
     * Return a simple count of valid or accepted shots administered in this series before the specified date. If includeDate parameter
     * is true, the count also includes shots administered on the specified date.
     * Does not take into account immunity or skipped doses. If the supplied date is null, this methods returns 0.
     */
    public int determineNumberOfDosesAdministeredInSeriesByDate(final LocalDate pDate, final boolean includeDate)
    {
        if (targetDoses == null || pDate == null)
            return 0;

        int i = 0;
        for (final TargetDose td : targetDoses)
        {
            if (!td.getIsValid())
                continue;

            final LocalDate shotDate = td.getAdministrationDate();
            if (shotDate == null)
                continue;

            final int compareTo = pDate.compareTo(shotDate);
            if ((!includeDate || compareTo < 0) && (includeDate || compareTo <= 0))
                break;

            i++;
        }

        return i;
    }

    /**
     * Return a effective number of valid or accepted shots administered in this series before the specified date. If includeDate parameter
     * is true, the count also includes shots administered on the specified date. If the supplied date is null, this methods returns 0.
     * If no shots were administered, returns 0, which may not be the same as the number of effective doses for the series.
     */
    public int determineEffectiveNumberOfDosesInSeriesByDate(final LocalDate pDate, final boolean includeDate)
    {
        final String _METHODNAME = "determineEffectiveNumberOfDosesInSeriesByDate(): ";

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "parameters passed in: {}; includeDate {}", pDate, includeDate);

        if (targetDoses == null || pDate == null)
            return 0;

        // First, find the dose that is on or before the specified date to use to determine the dose number
        TargetDose lTargetDoseOfInterest = null;
        for (final TargetDose td : targetDoses)
        {
            final LocalDate shotDate = td.getAdministrationDate();
            if (shotDate == null)
                continue;

            final int compareTo = pDate.compareTo(shotDate);
            if ((!includeDate || compareTo < 0) && (includeDate || compareTo <= 0))
                break;

            lTargetDoseOfInterest = td;
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "{}",
                    (lTargetDoseOfInterest == null) ? "lTargetDoseOfInterest is null" : lTargetDoseOfInterest.toString());

        if (lTargetDoseOfInterest != null)
        {
            final int doseNumber = doseNumberDeterminationUpdateUtility(lTargetDoseOfInterest, false, false, true,
                    interimEvaluationValidityCountByDisease.keySet());
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "dose number returned for lTargetDoseOfInterest {}", doseNumber);
            return doseNumber;
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Returning dose number 0");

        return 0;
    }

    /**
     * Return the number of effective valid (meaning VALID) doses in this TargetSeries. Note that the number reflects immunity and any skip doses in the series
     * (e.g. - if there are 3 doses to complete a series and the patient has immunity and there are no other diseases in question, 3 is returned even if no shots were administered)
     *
     * @return effective number of valid and accepted doses, taking immunity and skip doses into account
     */
    public int determineEffectiveNumberOfDosesInSeries()
    {
        return determineEffectiveNumberOfDosesInSeriesForSpecifiedDiseasesOnly(new ArrayList<>());
    }

    /**
     * If diseasesOfInterest parameter is empty, assume interest is in all diseases.
     *
     * @return effective number of valid and accepted doses, taking immunity and skip doses into account
     */
    public int determineEffectiveNumberOfDosesInSeriesForSpecifiedDiseasesOnly(final Collection<String> pDiseasesOfInterest)
    {
        final String _METHODNAME = "determineEffectiveNumberOfDosesInSeriesForSpecifiedDiseasesOnly()";

        final boolean diseasesOfInterestSpecified;
        final Map<String, Integer> evaluationValidiityCountsBySpecifiedDiseases = new HashMap<>();
        if (ObjectUtils.isEmpty(pDiseasesOfInterest))
        {
            evaluationValidiityCountsBySpecifiedDiseases.putAll(this.interimEvaluationValidityCountByDisease);
            diseasesOfInterestSpecified = false;
        }
        else
        {
            diseasesOfInterestSpecified = true;
            for (final String lDiseaseOfInterest : pDiseasesOfInterest)
                evaluationValidiityCountsBySpecifiedDiseases.put(lDiseaseOfInterest,
                        getInterimDiseaseEvaluationValidityCount(lDiseaseOfInterest));
        }

        // Validate that all specified diseases are supported by this series
        if (diseasesOfInterestSpecified)
        {
            final Set<String> lAllDiseases = this.interimEvaluationValidityCountByDisease.keySet();
            if (!lAllDiseases.containsAll(pDiseasesOfInterest))
            {
                final String lErrStr =
                        "one or more diseases of interest specified is not supported by this series: " + getSeriesName();
                log.warn(_METHODNAME + "{}", lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }
        }

        if (ObjectUtils.isEmpty(targetDoses))
        {
            if (!this.seriesRules.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered())
                return 0;

            final Collection<Integer> lDiseaseValidityCountList = evaluationValidiityCountsBySpecifiedDiseases.values();
            int lLowestCount = 0;
            for (final Integer lValidityCountInt : lDiseaseValidityCountList)
            {
                if (lValidityCountInt != null)
                {
                    final int lValidityCount = lValidityCountInt;
                    if (lValidityCount <= 0)
                        return 0;

                    if (lLowestCount == 0)
                        lLowestCount = lValidityCount;
                    else
                        if (lLowestCount < lValidityCount)
                            lLowestCount = lValidityCount;
                }
            }

            return lLowestCount;
        }

        final TargetDose lastDose = this.targetDoses.last();
        if (lastDose == null)
            return 0;

        // Since this method is public and could be accessed as a Drools accessor method, the below call does NOT and MAY NOT update the state of this
        // object (third parameter), and the antigen set is appropriately constant as well.
        // TODO: cache this.
        return doseNumberDeterminationUpdateUtility(lastDose, false, false, true,
                evaluationValidiityCountsBySpecifiedDiseases.keySet());
    }

    public void recommendNextShotBasedOnEarliestAgeRule(final LocalDate pEvalPersonBirthTime, final LocalDate pEvalDate)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        recommendNextShotBasedOnSeriesAgeRule(pEvalPersonBirthTime, pEvalDate, RecommendationDateType.EARLIEST);
    }

    public void recommendNextShotBasedOnEarliestIntervalRule(final LocalDate pEvalDate)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        recommendNextShotBasedOnSeriesIntervalRule(pEvalDate, RecommendationDateType.EARLIEST);
    }

    public void recommendNextShotBasedOnEarliestRecommendedAgeRule(final LocalDate pEvalPersonBirthTime, final LocalDate pEvalDate)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        recommendNextShotBasedOnSeriesAgeRule(pEvalPersonBirthTime, pEvalDate, RecommendationDateType.EARLIEST_RECOMMENDED);
    }

    public void recommendNextShotBasedOnEarliestRecommendedIntervalRule(final LocalDate pEvalDate)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        recommendNextShotBasedOnSeriesIntervalRule(pEvalDate, RecommendationDateType.EARLIEST_RECOMMENDED);
    }

    public void recommendNextShotBasedOnLatestRecommendedAgeRule(final LocalDate pEvalPersonBirthTime, final LocalDate pEvalDate)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        recommendNextShotBasedOnSeriesAgeRule(pEvalPersonBirthTime, pEvalDate, RecommendationDateType.LATEST_RECOMMENDED);
    }

    public void recommendNextShotBasedOnLatestRecommendedIntervalRule(final LocalDate pEvalDate)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        recommendNextShotBasedOnSeriesIntervalRule(pEvalDate, RecommendationDateType.LATEST_RECOMMENDED);
    }

    /**
     * Check age against series rule for this dose and record a recommendations based on the age recommended in the series
     *
     * @param pEvalDate Evaluation Date that this recommendations should be made against. If null, the current date is used.
     */
    private void recommendNextShotBasedOnSeriesAgeRule(final LocalDate pEvalPersonBirthTime, LocalDate pEvalDate,
            final RecommendationDateType pRecommendationDateType)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "recommendNextShotBasedOnSeriesAgeRule(): ";

        if (pEvalPersonBirthTime == null)
        {
            final String errStr = "NULL Date of Birth supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        if (pRecommendationDateType == null)
        {
            final String errStr = "NULL RecommendationDateType supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        // Series is Complete if latest dose # is > # valid/accepted doses required, or if latest dose # == # valid/accepted doses required in
        // series and that latest dose is accepted/valid
        final boolean lIsSeriesComplete = isSeriesComplete();
        final boolean lTargetSeasonExists = targetSeasonExists();
        if (lIsSeriesComplete && !lTargetSeasonExists)
        {
            if (!this.seriesRules.recurringDosesOccurAfterSeriesComplete())
            {
                // The series is complete, and no other future shots are recommended. If shots are recurring for this series, it is assumed that a custom rule handles this
                final Recommendation rec = new Recommendation(this);
                rec.setRecommendationStatus(RecommendationStatus.NOT_RECOMMENDED);
                rec.setRecommendationReason(BaseDataRecommendationReason._NOT_RECOMMENDED_COMPLETE_REASON.getCdsListItemName());
                switch (pRecommendationDateType)
                {
                    case EARLIEST -> interimRecommendationsScheduleEarliestAge.add(rec);
                    case EARLIEST_RECOMMENDED -> interimRecommendationsScheduleEarliestRecommendedAge.add(rec);
                    case LATEST_RECOMMENDED -> interimRecommendationsScheduleLatestRecommendedAge.add(rec);
                }
            }

            return;
        }

        if (lTargetSeasonExists && lIsSeriesComplete)
            return;

        // If the date of last shot is before the birthdate, set it to the birthdate
        final int doseNumberInSeriesToRecommend;
        if (this.manuallySetDoseNumberToRecommend != 0)
            doseNumberInSeriesToRecommend = this.manuallySetDoseNumberToRecommend;
        else
            doseNumberInSeriesToRecommend = determineEffectiveNumberOfDosesInSeries() + 1;
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Dose number in series for recommendations: {}", doseNumberInSeriesToRecommend);

        // Obtain the right age and corresponding date
        final DoseRule vaccineGroupDoseRule = obtainDoseRuleForSeriesByDoseNumber(doseNumberInSeriesToRecommend);
        if (vaccineGroupDoseRule == null)
        {
            if (seriesCompleteFlagManuallySet || seriesRules.getNumberOfDosesInSeries() == 0)
            {
                // Series completion flag was manually set, so it's possible that we are getting extra doses due to the series complete being manually set
                // to incomplete
                return;
            }

            log.error(_METHODNAME + "Corresponding series vaccine group dose rule not found");
            throw new InconsistentConfigurationException("Corresponding series vaccine group dose rule not found");
        }

        final TimePeriod rAge = switch (pRecommendationDateType)
        {
            case EARLIEST -> Objects.requireNonNullElse(vaccineGroupDoseRule.getMinimumAge(), TimePeriod.ZERO);
            case EARLIEST_RECOMMENDED ->
                    Objects.requireNonNullElse(vaccineGroupDoseRule.getEarliestRecommendedAge(), TimePeriod.ZERO);
            case LATEST_RECOMMENDED -> vaccineGroupDoseRule.getLatestRecommendedAge();
            case null -> throw new IllegalArgumentException(
                    _METHODNAME + "unknown type specified for RecommendationDateType; not supported");
        };

        if (rAge == null)
            return;

        // Now calculate the date that the next shot should be administered according to age rule, but if age for the series is before the start
        // date of the season (if a seasonal series), use the seasonal series start date as the minimum age date
        LocalDate ageDate = TimePeriod.addTimePeriod(pEvalPersonBirthTime, rAge);
        if (targetSeasonExists())
        {
            final LocalDate seasonStartDate = targetSeason.getFullySpecifiedSeasonStartDate();
            if (ageDate.isBefore(seasonStartDate))
                ageDate = seasonStartDate;
            // Date lSeasonEndDate = targetSeason.getFullySpecifiedSeasonEndDate().toDate();
            // If the recommended age is after the end date of the season and there is no off-season start date (which is indicative that there are no other seasons for this
            // vaccine group, then return; no recommendations based on minimum age will be made.
        }

        if (pEvalDate == null)
            pEvalDate = LocalDate.now();

        switch (pRecommendationDateType)
        {
            case EARLIEST ->
            {
                final Recommendation lEarliest = new Recommendation(this);
                lEarliest.setEarliestDate(ageDate);
                populateInterimEarliestAgeRecommendation(lEarliest, pEvalDate.isBefore(ageDate)
                                                                    ? RecommendationStatus.RECOMMENDED_IN_FUTURE
                                                                    : RecommendationStatus.RECOMMENDED);
            }
            case EARLIEST_RECOMMENDED ->
            {
                final Recommendation lEarliestRec = new Recommendation(this);
                lEarliestRec.setRecommendationDate(ageDate);
                if (pEvalDate.isBefore(ageDate))
                    populateInterimEarliestRecommendedAgeRecommendation(lEarliestRec, RecommendationStatus.RECOMMENDED_IN_FUTURE);
                else
                    populateInterimEarliestRecommendedAgeRecommendation(lEarliestRec, RecommendationStatus.RECOMMENDED);
            }
            default ->
            {
                // Past due date is the latest recommended date (calculated via age or interval) + 1 day
                final LocalDate lLatestDate = TimePeriod.addTimePeriod(ageDate, new TimePeriod(-1, DurationType.DAYS));
                final Recommendation lLatestRecommended = new Recommendation(this);
                lLatestRecommended.setLatestRecommendationDate(lLatestDate);
                populateInterimLatestRecommendedAgeRecommendation(lLatestRecommended, pEvalDate.isBefore(lLatestDate)
                                                                                      ? RecommendationStatus.RECOMMENDED_IN_FUTURE
                                                                                      : RecommendationStatus.RECOMMENDED);
            }
        }
    }

    /**
     * Check interval against series rule for this dose and record a recommendations based on the routine interval
     *
     * @param pEvalDate Evaluation Date that this recommendations should be made against. If null, the current date is used.
     */
    private void recommendNextShotBasedOnSeriesIntervalRule(LocalDate pEvalDate,
            final RecommendationDateType pRecommendationDateType)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "recommendNextShotBasedOnSeriesIntervalRule(): ";

        if (pRecommendationDateType == null)
        {
            final String errStr = "NULL RecommendationDateType supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        final TargetDose lastDoseAdministered = determineIfSeriesCompleteAndReturnLastDose(
                true);        // Recommendation interval based on last shot that was not ignored
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Last dose # administered: {}",
                    (lastDoseAdministered == null) ? "none" : lastDoseAdministered.getDoseNumberInSeries());

        if (lastDoseAdministered == null)
            return;

        final int lastDoseAdministeredDoseNumber = lastDoseAdministered.getDoseNumberInSeries();
        final boolean lTargetSeasonExists = targetSeasonExists();
        if (!lTargetSeasonExists && lastDoseAdministeredDoseNumber > seriesRules.getNumberOfDosesInSeries())
            return;

        // Series is Complete if latest dose # is > # valid/accepted doses required, or if latest dose # == # valid/accepted doses required
        // in series and that latest dose is accepted/valid
        int doseRuleOfInterest;
        // Prior dose to calculate interval from not supplied in parameters
        if (this.manuallySetDoseNumberToRecommend != 0)
            doseRuleOfInterest = this.manuallySetDoseNumberToRecommend - 1;
        else
        {
            final boolean lIsSeriesComplete = isSeriesComplete();
            if (lIsSeriesComplete && !lTargetSeasonExists)
            { // (lTargetSeasonExists == false || (lTargetSeasonExists == true && targetSeason.getFullySpecifiedSeasonOffSeasonEndDate() == null))) {
                if (!this.seriesRules.recurringDosesOccurAfterSeriesComplete())
                {
                    // The series is complete, and no other future shots are recommended. If shots are recurring for this series, it is assumed that a custom rule handles this
                    final Recommendation rec = new Recommendation(this);
                    rec.setRecommendationStatus(RecommendationStatus.NOT_RECOMMENDED);
                    rec.setRecommendationReason(BaseDataRecommendationReason._NOT_RECOMMENDED_COMPLETE_REASON.getCdsListItemName());
                    switch (pRecommendationDateType)
                    {
                        case EARLIEST -> interimRecommendationsScheduleEarliestInterval.add(rec);
                        case EARLIEST_RECOMMENDED -> interimRecommendationsScheduleEarliestRecommendedInterval.add(rec);
                        case LATEST_RECOMMENDED -> interimRecommendationsScheduleLatestRecommendedInterval.add(rec);
                    }
                }
                return;
            }

            if (lIsSeriesComplete)
            {
                // We don't mark Seasonal Series complete here; some seasonal series can be completed and not others
                return;
            }

            doseRuleOfInterest = determineDoseNumberInSeries(lastDoseAdministered) - 1;
        }

        if (doseRuleOfInterest == 0)
        {
            if (pRecommendationDateType == RecommendationDateType.LATEST_RECOMMENDED)
            {
                // Latest recommended interval to target dose 1 are not considered for determining overdue date
                return;
            }

            doseRuleOfInterest = 1;
        }

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Dose number from which to calculate interval: {}", doseRuleOfInterest);

        final DoseRule doseRulePreviousDose = obtainDoseRuleForSeriesByDoseNumber(doseRuleOfInterest);
        if (doseRulePreviousDose == null)
        {
            if (seriesCompleteFlagManuallySet)
            {
                // Series completion flag was manually set, so it's possible that we are getting extra doses due to the series complete being manually set
                // to incomplete
                return;
            }

            log.error(_METHODNAME + "Corresponding series vaccine group dose rule not found");
            throw new InconsistentConfigurationException("Corresponding series vaccine group dose rule not found");
        }

        final TimePeriod rInterval;
        switch (pRecommendationDateType)
        {
            case EARLIEST -> rInterval = doseRulePreviousDose.getMinimumInterval();
            case EARLIEST_RECOMMENDED -> rInterval = doseRulePreviousDose.getEarliestRecommendedInterval();
            case LATEST_RECOMMENDED -> rInterval = doseRulePreviousDose.getLatestRecommendedInterval();
            default -> throw new IllegalArgumentException(_METHODNAME + "LATEST specified for date; not supported yet");
        }
        if (rInterval == null)
        {
            if (pRecommendationDateType == RecommendationDateType.EARLIEST_RECOMMENDED && log.isDebugEnabled())
                log.debug(_METHODNAME + "No routine interval specified for dose {} in Vaccine Group {}; Series Name: {}",
                        doseRulePreviousDose.getDoseNumber(), seriesRules.getVaccineGroup(), seriesRules.getSeriesName());

            return;
        }

        // Now calculate the date that the next shot should be administered according to internal rule
        LocalDate rIntervalDate = TimePeriod.addTimePeriod(lastDoseAdministered.getAdministrationDate(), rInterval);

        // AI: Look up the start date of the next season, if defined. Otherwise, set to the date of the default season.
        // If this is a Seasonal TargetSeries and the rIntervalDate is after the off-season end date, then set the recommendations to the beginning of the next season
        if (this.targetSeason != null && targetSeason.getFullySpecifiedSeasonOffSeasonEndDate() != null
                && targetSeason.getFullySpecifiedSeasonOffSeasonEndDate().isBefore(rIntervalDate))
        {
            rIntervalDate = targetSeason.getFullySpecifiedSeasonOffSeasonEndDate().plusDays(1);
        }

        // Otherwise, store the interval recommendations
        if (pEvalDate == null)
            pEvalDate = LocalDate.now();

        switch (pRecommendationDateType)
        {
            case EARLIEST ->
            {
                final Recommendation lEarliest = new Recommendation(this);
                lEarliest.setEarliestDate(rIntervalDate);
                populateInterimEarliestIntervalRecommendation(lEarliest, pEvalDate.isBefore(rIntervalDate)
                                                                         ? RecommendationStatus.RECOMMENDED_IN_FUTURE
                                                                         : RecommendationStatus.RECOMMENDED);
            }
            case LATEST_RECOMMENDED ->
            {
                // Past due date is the latest recommended date (calculated via age or interval) + 1
                final LocalDate lLatestDate = TimePeriod.addTimePeriod(rIntervalDate, new TimePeriod(-1, DurationType.DAYS));
                final Recommendation lLatestRecommended = new Recommendation(this);
                lLatestRecommended.setLatestRecommendationDate(lLatestDate);
                // populate this in interim structure.... if there are age rule recommendations, they will need to be removed later
                populateInterimLatestRecommendedIntervalRecommendation(lLatestRecommended, pEvalDate.isBefore(lLatestDate)
                                                                                           ? RecommendationStatus.RECOMMENDED_IN_FUTURE
                                                                                           : RecommendationStatus.RECOMMENDED);
            }
            default ->
            {
                final Recommendation rec = new Recommendation(this);
                rec.setRecommendationDate(rIntervalDate);
                // populate this in interim structure.... if there are age rule recommendations, they will need to be removed later
                if (pEvalDate.isBefore(rIntervalDate))
                    populateInterimEarliestRecommendedIntervalRecommendation(rec, RecommendationStatus.RECOMMENDED_IN_FUTURE);
                else
                    populateInterimEarliestRecommendedIntervalRecommendation(rec, RecommendationStatus.RECOMMENDED);
            }
        }
    }

    /**
     * Record recommendations status codes and date following the below business rules in the TargetSeries interimRecommendationsScheduleEarliest object.
     * - The supplied Recommendation object is updated with the chosen RecommendationStatus.
     *
     * @param rec                  Recommendation Object in which to record recommendations status codes and reasons. Simply returns if supplied parameters is null.
     * @param recommendationStatus may not be null
     */
    private void populateInterimEarliestAgeRecommendation(final Recommendation rec, final RecommendationStatus recommendationStatus)
    {
        final String _METHODNAME = "populateInterimEarliestRecommendation(): ";
        if (rec == null || recommendationStatus == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsScheduleEarliestAge, rec,
                recommendationStatus);
    }

    /**
     * Record recommendations status codes and date following the below business rules in the TargetSeries interimRecommendationsScheduleEarliest object.
     * - The supplied Recommendation object is updated with the chosen RecommendationStatus.
     *
     * @param rec Recommendation Object in which to record recommendations status codes and reasons. Simply returns if supplied parameters is null.
     */
    private void populateInterimEarliestIntervalRecommendation(final Recommendation rec,
            final RecommendationStatus recommendationStatus)
    {
        final String _METHODNAME = "populateInterimEarliestRecommendation(): ";
        if (rec == null || recommendationStatus == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsScheduleEarliestInterval, rec,
                recommendationStatus);
    }

    /**
     * Record recommendations status codes and date following the below business rules in the TargetSeries interimRecommendationsScheduleEarliestRecommendedAge object.
     * - The supplied Recommendation object is updated with the chosen RecommendationStatus.
     *
     * @param rec                  Recommendation Object in which to record recommendations status codes and reasons. Simply returns if supplied parameters is null.
     * @param recommendationStatus may not be null
     */
    private void populateInterimEarliestRecommendedAgeRecommendation(final Recommendation rec,
            final RecommendationStatus recommendationStatus)
    {
        final String _METHODNAME = "populateInterimEarliestRecommendedAgeRecommendation(): ";
        if (rec == null || recommendationStatus == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsScheduleEarliestRecommendedAge, rec,
                recommendationStatus);
    }

    /**
     * Record recommendations status codes and date following the below business rules in the TargetSeries interimRecommendationsScheduleEarliestRecommendedInterval object.
     * - The supplied Recommendation object is updated with the chosen RecommendationStatus.
     *
     * @param rec                  Recommendation Object in which to record recommendations status codes and reasons. Simply returns if supplied parameters is null.
     * @param recommendationStatus may be null
     */
    private void populateInterimEarliestRecommendedIntervalRecommendation(final Recommendation rec,
            final RecommendationStatus recommendationStatus)
    {
        final String _METHODNAME = "populateInterimEarliestRecommendedIntervalRecommendation(): ";
        if (rec == null || recommendationStatus == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsScheduleEarliestRecommendedInterval, rec,
                recommendationStatus);
    }

    /**
     * Record recommendations status codes and date following the below business rules in the TargetSeries interimRecommendationsScheduleLatestRecommendedAge object.
     * - The supplied Recommendation object is updated with the chosen RecommendationStatus.
     *
     * @param rec                  Recommendation Object in which to record recommendations status codes and reasons. Simply returns if any supplied
     *                             parameters is null.
     * @param recommendationStatus may be null
     */
    private void populateInterimLatestRecommendedAgeRecommendation(final Recommendation rec,
            final RecommendationStatus recommendationStatus)
    {
        final String _METHODNAME = "populateInterimLatestRecommendedAgeRecommendation(): ";
        if (rec == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsScheduleLatestRecommendedAge, rec,
                recommendationStatus);
    }

    /**
     * Record recommendations status codes and date following the below business rules in the TargetSeries interimRecommendationsScheduleLatestRecommendedInterval object.
     * - The supplied Recommendation object is updated with the chosen RecommendationStatus.
     *
     * @param rec Recommendation Object in which to record recommendations status codes and reasons. Simply returns if any supplied
     *            parameters is null.
     */
    private void populateInterimLatestRecommendedIntervalRecommendation(final Recommendation rec,
            final RecommendationStatus recommendationStatus)
    {
        final String _METHODNAME = "populateInterimLatestRecommendedIntervalRecommendation(): ";
        if (rec == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsScheduleLatestRecommendedInterval, rec,
                recommendationStatus);
    }

    /**
     * Helper method to modify supplied Recommendation object with record recommendations status codes and reasons, and record generic reason in the
     * specified interim Recommendations List. Also, the supplied Recommendation object is updated with the specified RecommendationStatus.
     * Generic reasons for the recorded RecommendationStatus are automatically populated in the Recommended object as follows:
     * + if RecommendationStatus.CONDITIONALLY_RECOMMENDED, then ICELogicHelper._RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON_CODE
     * + if RecommendationStatus.NOT_RECOMMENDED, then ICELogicHelper._NOT_RECOMMENDED_NOT_SPECIFIED_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED_IN_FUTURE,then ICELogicHelper._RECOMMENDED_IN_FUTURE_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED, then ICELogicHelper._RECOMMENDED_DUE_NOW_REASON_CODE
     * If you wish to supply different reasons, then you must manually populate a CD and record it yourself in the Recommendation object before passing it
     * into this one to be added to the Recommendations List
     *
     * @param interimRecommendationsListInstanceToUpdate reference to the interim recommendations List to add the supplied recommendations object to, as well as the reason. Returns if supplied parameters is null
     * @param rec                                        Recommendation Object in which to record recommendations status codes and reasons. Simply returns if supplied parameters is null.
     * @param pRecommendationStatus                      may be null
     */
    private void populateInterimRecommendationsAndRecordGenericReasonHelper(
            final List<Recommendation> interimRecommendationsListInstanceToUpdate, final Recommendation rec,
            final RecommendationStatus pRecommendationStatus)
    {
        final String _METHODNAME = "populateInterimRecommendationsAndRecordGenericReasonHelper(): ";
        if (rec == null || interimRecommendationsListInstanceToUpdate == null)
        {
            log.warn(_METHODNAME + "One or more supplied parameters null");
            return;
        }

        final BaseDataRecommendationReason lSRC = getGenericRecommendationReasonForRecommendationStatus(pRecommendationStatus);
        switch (pRecommendationStatus)
        {
            case CONDITIONALLY_RECOMMENDED ->
            {
                rec.setRecommendationStatus(RecommendationStatus.CONDITIONALLY_RECOMMENDED);
                if (rec.getRecommendationReason() == null && lSRC != null)
                    rec.setRecommendationReason(lSRC.getCdsListItemName());
                if (!interimRecommendationsListInstanceToUpdate.contains(rec))
                    interimRecommendationsListInstanceToUpdate.add(rec);
            }
            case NOT_RECOMMENDED ->
            {
                rec.setRecommendationStatus(RecommendationStatus.NOT_RECOMMENDED);
                if (rec.getRecommendationReason() == null && lSRC != null)
                    rec.setRecommendationReason(lSRC.getCdsListItemName());
                if (!interimRecommendationsListInstanceToUpdate.contains(rec))
                    interimRecommendationsListInstanceToUpdate.add(rec);
            }
            case RECOMMENDED_IN_FUTURE ->
            {
                rec.setRecommendationStatus(RecommendationStatus.RECOMMENDED_IN_FUTURE);
                if (rec.getRecommendationReason() == null && lSRC != null)
                    rec.setRecommendationReason(lSRC.getCdsListItemName());
                if (!interimRecommendationsListInstanceToUpdate.contains(rec))
                    interimRecommendationsListInstanceToUpdate.add(rec);
            }
            case RECOMMENDED ->
            {
                rec.setRecommendationStatus(RecommendationStatus.RECOMMENDED);
                if (rec.getRecommendationReason() == null && lSRC != null)
                    rec.setRecommendationReason(lSRC.getCdsListItemName());
                if (!interimRecommendationsListInstanceToUpdate.contains(rec))
                    interimRecommendationsListInstanceToUpdate.add(rec);
            }
            default ->
            {
                if (!interimRecommendationsListInstanceToUpdate.contains(rec))
                    interimRecommendationsListInstanceToUpdate.add(rec);
            }
        }
    }

    private BaseDataRecommendationReason getGenericRecommendationReasonForRecommendationStatus(
            final RecommendationStatus pRecommendationStatus)
    {
        if (pRecommendationStatus == null)
            return null;

        return switch (pRecommendationStatus)
        {
            case CONDITIONALLY_RECOMMENDED -> BaseDataRecommendationReason._RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON;
            case NOT_RECOMMENDED -> BaseDataRecommendationReason._NOT_RECOMMENDED_NOT_SPECIFIED_REASON;
            case RECOMMENDED_IN_FUTURE -> BaseDataRecommendationReason._RECOMMENDED_IN_FUTURE_REASON;
            case RECOMMENDED -> BaseDataRecommendationReason._RECOMMENDED_DUE_NOW_REASON;
            default -> null;
        };
    }

    /**
     * Add an earliest recommended recommendations with the specified earliest recommended date for consideration in this TargetSeries. Note that this method will record *generic* reasons for the
     * recommendations as follows:
     * + if RecommendationStatus.CONDITIONALLY_RECOMMENDED, then ICELogicHelper._RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON_CODE
     * + if RecommendationStatus.NOT_RECOMMENDED, then ICELogicHelper._NOT_RECOMMENDED_NOT_SPECIFIED_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED_IN_FUTURE, then ICELogicHelper._RECOMMENDED_IN_FUTURE_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED, then ICELogicHelper._RECOMMENDED_DUE_NOW_REASON_CODE
     * If you wish to supply different reasons, then you must manually populate a CD and record it yourself
     *
     * @param recommendationDate   Date of this recommendations
     * @param v                    Recommended vaccine
     * @param recommendationStatus Specify a RecommendationStatus if you wish to be explicit; otherwise, the recommendations will either
     *                             be in the future or now based on date calculations with the supplied evaluation date of the next parameters
     * @param pEvalDate            Evaluation Date that this recommendations should be made against. If null, the current date is used.
     */
    private void addInterimRecommendationForConsideration(final LocalDate recommendationDate, final Vaccine v,
            final RecommendationStatus recommendationStatus, final String recommendationReason, LocalDate pEvalDate)
    {
        final String _METHODNAME =
                "addInterimRecommendationForConsideration(LocalDate, Vaccine, RecommendationStatus, String, Date): ";

        final Recommendation rec;
        try
        {
            rec = new Recommendation(this);
        }
        catch (final IllegalArgumentException ie)
        {
            final String str = "Caught unexpected IllegalArgumentException instantiating a recommendations: this should not happen";
            log.error(_METHODNAME + str);
            throw new IllegalStateException(str);
        }

        if (pEvalDate == null)
            pEvalDate = LocalDate.now();

        rec.setRecommendationDate(recommendationDate);
        rec.setRecommendedVaccine(v);
        rec.setRecommendationReason(recommendationReason);

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Recommendation: {}", rec);

        if (recommendationDate == null || ((recommendationStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED
                || recommendationStatus == RecommendationStatus.NOT_RECOMMENDED
                || recommendationStatus == RecommendationStatus.RECOMMENDED
                || recommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE)))
        {
            populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsCustom, rec,
                    rec.getRecommendationStatus());
        }
        else
            if (pEvalDate.isBefore(recommendationDate))
            {
                populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsCustom, rec,
                        RecommendationStatus.RECOMMENDED_IN_FUTURE);
            }
            else
            {
                populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsCustom, rec,
                        RecommendationStatus.RECOMMENDED);
            }
    }

    /**
     * Add an earliest recommended recommendations with the specified earliest recommended date for consideration in this TargetSeries. Note that this method will record *generic* reasons for the
     * recommendations as follows:
     * + if RecommendationStatus.CONDITIONALLY_RECOMMENDED, then ICELogicHelper._RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON_CODE
     * + if RecommendationStatus.NOT_RECOMMENDED, then ICELogicHelper._NOT_RECOMMENDED_NOT_SPECIFIED_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED_IN_FUTURE, then ICELogicHelper._RECOMMENDED_IN_FUTURE_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED, then ICELogicHelper._RECOMMENDED_DUE_NOW_REASON_CODE
     * If you wish to supply different reasons, then you must manually populate a CD and record it yourself
     *
     * @param recommendationDate   Date of this recommendations
     * @param recommendationStatus Specify a RecommendationStatus if you wish to be explicit; otherwise, the recommendations will either
     *                             be in the future or now based on date calculations with the supplied evaluation date of the next parameters
     * @param pEvalDate            Evaluation Date that this recommendations should be made against. If null, the current date is used.
     */
    public void addInterimRecommendationForConsideration(final LocalDate recommendationDate,
            final RecommendationStatus recommendationStatus, final LocalDate pEvalDate)
    {
        addInterimRecommendationForConsideration(recommendationDate, null, recommendationStatus, null, pEvalDate);
    }

    /**
     * Add a recommendations with the specified earliest recommended date for consideration in this TargetSeries. Note that this method will record *generic* reasons for the
     * recommendations as follows:
     * + if RecommendationStatus.CONDITIONALLY_RECOMMENDED, then ICELogicHelper._RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON_CODE
     * + if RecommendationStatus.NOT_RECOMMENDED, then ICELogicHelper._NOT_RECOMMENDED_NOT_SPECIFIED_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED_IN_FUTURE, then ICELogicHelper._RECOMMENDED_IN_FUTURE_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED, then ICELogicHelper._RECOMMENDED_DUE_NOW_REASON_CODE
     * If you wish to supply different reasons, then you must manually populate a CD and record the recommendations yourself by some
     * other means. Note that the recommendations will either be in the future or now based on date calculations with the supplied evaluation date
     *
     * @param recommendationDate Date of this recommendations
     * @param pEvalDate          Evaluation Date that this recommendations should be made against. If null, the current date is used.
     */
    public void addInterimRecommendationForConsideration(final LocalDate recommendationDate, final LocalDate pEvalDate)
    {
        addInterimRecommendationForConsideration(recommendationDate, null, pEvalDate);
    }

    /**
     * Add a recommendations for consideration in this TargetSeries. Note that this method will record *generic* reasons for the
     * recommendations as follows:
     * + if RecommendationStatus.CONDITIONALLY_RECOMMENDED, then ICELogicHelper._RECOMMENDED_CONDITIONALLY_HIGH_RISK_REASON_CODE
     * + if RecommendationStatus.NOT_RECOMMENDED, then ICELogicHelper._NOT_RECOMMENDED_NOT_SPECIFIED_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED_IN_FUTURE, then ICELogicHelper._RECOMMENDED_IN_FUTURE_REASON_CODE
     * + if RecommendationStatus.RECOMMENDED, then ICELogicHelper._RECOMMENDED_DUE_NOW_REASON_CODE
     * If you wish to supply different reasons, then you must manually populate a CD and record it yourself
     *
     * @param recommendation Prepopulated recommendations to add. In this object, specify a RecommendationStatus if you wish to be explicit;
     *                       otherwise, the recommendations will either be in the future or now based on date calculations with the supplied evaluation date of the
     *                       next parameters and recommendations date
     * @param pEvalDate      Evaluation Date that this recommendations should be made against. If null, the current date is used.
     */
    public void addInterimRecommendationForConsideration(final Recommendation recommendation, LocalDate pEvalDate)
    {
        final String _METHODNAME = "addInterimRecommendationForConsideration(Recommendation, Date): ";
        if (recommendation == null)
            return;

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Recommendation: {}", recommendation);

        if (pEvalDate == null)
            pEvalDate = LocalDate.now();

        // First, add "regular" interim recommendations, which may or may not include a forecast date
        final RecommendationStatus lRS = recommendation.getRecommendationStatus();
        if ((lRS != RecommendationStatus.CONDITIONALLY_RECOMMENDED && lRS != RecommendationStatus.NOT_RECOMMENDED
                && lRS != RecommendationStatus.RECOMMENDED && lRS != RecommendationStatus.RECOMMENDED_IN_FUTURE))
        {
            addInterimRecommendationForConsideration(recommendation.getRecommendationDate(), recommendation.getRecommendedVaccine(),
                    null, recommendation.getRecommendationReason(), pEvalDate);
        }
        else
        {
            populateInterimRecommendationsAndRecordGenericReasonHelper(interimRecommendationsCustom, recommendation,
                    recommendation.getRecommendationStatus());
        }

        // Now include interim recommendations for earliest and latest recommended dates
        if (recommendation.getEarliestDate() != null)
        {
            if (!this.interimRecommendationsCustomEarliest.contains(recommendation))
                this.interimRecommendationsCustomEarliest.add(recommendation);
        }
        if (recommendation.getLatestRecommendationDate() != null)
        {
            if (!this.interimRecommendationsCustomLatest.contains(recommendation))
                this.interimRecommendationsCustomLatest.add(recommendation);
        }
    }

    /**
     * Finalizes the earliest, earliest recommended and latest recommended recommendations based on the interim recommendations made to this point.
     * After making this call, getFinalEarliestRecommendation(), getFinalRecommendation(), getFinalLatestRecommendation() can be called as desired.
     * If any of these dates are for some reason adjusted (manually or otherwise) or interim recommendations are added or removed, this method must be
     * called again so that the earliest, recommended, overdue and latest are updated in this series.
     */
    public void finalizeRecommendationsForForecasting()
    {
        final String _METHODNAME = "finalizeRecommendationsForForecasting(): ";

        // Obtain determine overall recommendations status-- priority is: NOT_RECOMMENDED, CONDITIONALLY_RECOMMENDED, FUTURE_RECOMMENDED then RECOMMENDED
        // Then choose recommendations with latest date collected across all interim recommendations (including earliest) if the status is not NOT_RECOMMENDED
        final List<Recommendation> lInterimRecommended = new ArrayList<>();
        if (!interimRecommendationsScheduleEarliestRecommendedAge.isEmpty())
        {
            // Take dates calculated via earliest recommended age(s) if available
            lInterimRecommended.addAll(interimRecommendationsScheduleEarliestRecommendedAge);
        }
        if (!interimRecommendationsScheduleEarliestRecommendedInterval.isEmpty())
        {
            // Otherwise use the earliest recommended date(s) calculated via earliest recommended interval
            lInterimRecommended.addAll(interimRecommendationsScheduleEarliestRecommendedInterval);
        }
        if (!interimRecommendationsCustom.isEmpty())
            lInterimRecommended.addAll(interimRecommendationsCustom);

        if (log.isDebugEnabled())
        {
            final StringBuilder lInterimRecommendedStr = new StringBuilder("Interim Recommendations to be examined. ");
            int i = 1;
            for (final Recommendation r : lInterimRecommended)
            {
                lInterimRecommendedStr.append(" -- Interim Recommendation: ")
                        .append(i++)
                        .append(": ")
                        .append(r.getTargetSeriesIdentifier())
                        .append("; status : ")
                        .append(r.getRecommendationStatus())
                        .append("; date: ")
                        .append(r.getRecommendationDate())
                        .append("; vaccine ")
                        .append(r.getRecommendedVaccine());
            }
            log.debug(_METHODNAME + "{}", lInterimRecommendedStr);
        }

        // Number of interim recommendations > 0 ? If so, continue; otherwise set to null all around
        final int lInterimRecommendedSize = lInterimRecommended.size();
        if (lInterimRecommendedSize == 0 && this.finalRecommendations.isEmpty())
        {
            setRecommendationStatus(RecommendationStatus.NOT_RECOMMENDED);
            setFinalRecommendations(null);
            setFinalRecommendationDate(null);
            setFinalEarliestDate(null);
            setFinalOverdueDate(null);
        }
        else
            if (lInterimRecommendedSize == 0 && this.recommendationStatusPrior != null)
            {
                setRecommendationStatus(this.recommendationStatusPrior);
                if (log.isDebugEnabled())
                    log.debug(_METHODNAME + "No interim recommendations to process.");
                return;
            }
            else
            {
                // Determine range of statuses in recommendations and SELECT one according to priority: NOT_RECOMMENDED, CONDITIONALLY_RECOMMENDED, FUTURE_RECOMMENDED,
                // then RECOMMENDED
                RecommendationStatus lFinalRecommendationStatus = null;
                // Date lFinalRecommendationDate = null;
                LocalDate lFinalRecommendationDate = (getFinalRecommendationDate() != null) ? getFinalRecommendationDate() : null;
                final List<RecommendationStatus> lRecommendationStatusesIterTmpvar = new ArrayList<>();
                for (final Recommendation lR : lInterimRecommended)
                {
                    final RecommendationStatus lRS = lR.getRecommendationStatus();
                    if (lRS == RecommendationStatus.NOT_RECOMMENDED)
                    {
                        // No reason to continue; NOT_RECOMMENDED is highest weighted status was found and the final recommendations date will be null
                        lFinalRecommendationStatus = RecommendationStatus.NOT_RECOMMENDED;
                        lFinalRecommendationDate = null;
                        break;
                    }
                    final LocalDate lRDate = lR.getRecommendationDate();
                    if (lRDate != null)
                    {
                        if (lFinalRecommendationDate == null)
                            lFinalRecommendationDate = lRDate;
                        else
                            if (lRDate.isAfter(lFinalRecommendationDate))
                                lFinalRecommendationDate = lRDate;
                    }
                    lRecommendationStatusesIterTmpvar.add(lR.getRecommendationStatus());
                }
                if (lFinalRecommendationStatus == null)
                {
                    // A final recommendations status has not been determined yet; determine final recommendations status now
                    if (lRecommendationStatusesIterTmpvar.contains(RecommendationStatus.CONDITIONALLY_RECOMMENDED))
                        lFinalRecommendationStatus = RecommendationStatus.CONDITIONALLY_RECOMMENDED;
                    else
                        if (lRecommendationStatusesIterTmpvar.contains(RecommendationStatus.RECOMMENDED_IN_FUTURE))
                            lFinalRecommendationStatus = RecommendationStatus.RECOMMENDED_IN_FUTURE;
                        else
                            lFinalRecommendationStatus = RecommendationStatus.RECOMMENDED;
                }

			/*
			/////// Adjust final recommendations date to be the same as the last shot administered in series if the final recommended date is before the last shot date. Adjust the
			/////// recommended status too, if necessary
			/////// if (lFinalRecommendationDate != null && getLastShotAdministeredInSeries() != null && getLastShotAdministeredInSeries().getAdministrationDate() != null &&
				/////// lFinalRecommendationDate.isBefore(getLastShotAdministeredInSeries().getAdministrationDate())) {
				/////// lFinalRecommendationDate = getLastShotAdministeredInSeries().getAdministrationDate();
				/////// if (lFinalRecommendationStatus == RecommendationStatus.RECOMMENDED) {
					/////// if (lFinalRecommendationDate.isAfter(this.evalTime)) {
						/////// lFinalRecommendationStatus = RecommendationStatus.RECOMMENDED_IN_FUTURE;
					/////// }
				/////// }
			/////// }
			*/

                // Now set the final recommendations (final recommendations date, recommendations status and Recommendation object list) for this TargetSeries instance
                setFinalRecommendationDate(lFinalRecommendationDate);
                setRecommendationStatus(lFinalRecommendationStatus);
                final List<RecommendationStatus> lRecStatusListOfInterestOtherNoDate = new ArrayList<>();
                lRecStatusListOfInterestOtherNoDate.add(RecommendationStatus.FORECASTING_COMPLETE);
                lRecStatusListOfInterestOtherNoDate.add(RecommendationStatus.FORECASTING_IN_PROGRESS);
                lRecStatusListOfInterestOtherNoDate.add(RecommendationStatus.NOT_FORECASTED);
                // Get List from these non-standard status of recommendations that do not have a recommendations date - e.g. - perhaps a vaccine is associated with them
                final List<Recommendation> others =
                        Recommendation.getRecommendationListSubsetWithSpecifiedStatuses(lInterimRecommended,
                                lRecStatusListOfInterestOtherNoDate);
                final List<Recommendation> eliminatedOthersWithRecDate = new ArrayList<>();
                for (final Recommendation otherRec : others)
                {
                    if (otherRec.getRecommendationDate() == null)
                        eliminatedOthersWithRecDate.add(otherRec);
                }
                if (lFinalRecommendationStatus == RecommendationStatus.NOT_RECOMMENDED)
                {
                    final List<RecommendationStatus> lRecStatusListOfInterest = new ArrayList<>();
                    lRecStatusListOfInterest.add(RecommendationStatus.NOT_RECOMMENDED);
                    addFinalRecommendations(Recommendation.getRecommendationListSubsetWithSpecifiedStatuses(lInterimRecommended,
                            lRecStatusListOfInterest));
                    addFinalRecommendations(eliminatedOthersWithRecDate);
                }
                else
                    if (lFinalRecommendationStatus == RecommendationStatus.CONDITIONALLY_RECOMMENDED)
                    {
                        final List<RecommendationStatus> lRecStatusListOfInterest;
                        lRecStatusListOfInterest = new ArrayList<>();
                        lRecStatusListOfInterest.add(RecommendationStatus.CONDITIONALLY_RECOMMENDED);
                        lRecStatusListOfInterest.add(RecommendationStatus.RECOMMENDED_IN_FUTURE);
                        lRecStatusListOfInterest.add(RecommendationStatus.RECOMMENDED);
                        addFinalRecommendations(Recommendation.getRecommendationListSubsetWithSpecifiedStatuses(lInterimRecommended,
                                lRecStatusListOfInterest));
                        addFinalRecommendations(eliminatedOthersWithRecDate);
                    }
                    else
                        if (lFinalRecommendationStatus == RecommendationStatus.RECOMMENDED_IN_FUTURE)
                        {
                            final List<RecommendationStatus> lRecStatusListOfInterest;
                            lRecStatusListOfInterest = new ArrayList<>();
                            lRecStatusListOfInterest.add(RecommendationStatus.RECOMMENDED_IN_FUTURE);
                            lRecStatusListOfInterest.add(RecommendationStatus.RECOMMENDED);
                            addFinalRecommendations(
                                    Recommendation.getRecommendationListSubsetWithSpecifiedStatuses(lInterimRecommended,
                                            lRecStatusListOfInterest));
                            addFinalRecommendations(eliminatedOthersWithRecDate);
                        }
                        else
                        {
                            final List<RecommendationStatus> lRecStatusListOfInterest = new ArrayList<>();
                            lRecStatusListOfInterest.add(RecommendationStatus.RECOMMENDED);
                            addFinalRecommendations(
                                    Recommendation.getRecommendationListSubsetWithSpecifiedStatuses(lInterimRecommended,
                                            lRecStatusListOfInterest));
                            addFinalRecommendations(eliminatedOthersWithRecDate);
                        }

                // Debug logging - list final recommendations
                if (log.isDebugEnabled())
                {
                    final StringBuilder lInterimRecommendedStr = new StringBuilder("Final Recommendations. ");
                    int i = 1;
                    for (final Recommendation r : lInterimRecommended)
                    {
                        lInterimRecommendedStr.append(" --Final Recommendation: ")
                                .append(i++)
                                .append(": ")
                                .append(r.getTargetSeriesIdentifier())
                                .append("; status : ")
                                .append(r.getRecommendationStatus())
                                .append("; date: ")
                                .append(r.getRecommendationDate())
                                .append("; vaccine ")
                                .append(r.getRecommendedVaccine());
                    }
                    log.debug(_METHODNAME + "{}", lInterimRecommendedStr);
                }

                // Record Earliest Recommendations
                final List<Recommendation> lInterimRecommendedEarliest = new ArrayList<>();
                if (!interimRecommendationsScheduleEarliestAge.isEmpty())
                    lInterimRecommendedEarliest.addAll(interimRecommendationsScheduleEarliestAge);
                if (!interimRecommendationsScheduleEarliestInterval.isEmpty())
                    lInterimRecommendedEarliest.addAll(interimRecommendationsScheduleEarliestInterval);
                if (!interimRecommendationsCustomEarliest.isEmpty())
                    lInterimRecommendedEarliest.addAll(interimRecommendationsCustomEarliest);
                if (log.isDebugEnabled())
                {
                    final StringBuilder lInterimRecommendedStr =
                            new StringBuilder("Interim Earliest Recommendations to be examined. ");
                    int i = 1;
                    for (final Recommendation r : lInterimRecommendedEarliest)
                    {
                        lInterimRecommendedStr.append(" -- Interim Earliest Recommendation: ")
                                .append(i++)
                                .append(": ")
                                .append(r.getTargetSeriesIdentifier())
                                .append("; status : ")
                                .append(r.getRecommendationStatus())
                                .append("; date: ")
                                .append(r.getEarliestDate())
                                .append("; vaccine ")
                                .append(r.getRecommendedVaccine());
                    }
                    log.debug(_METHODNAME + "{}", lInterimRecommendedStr);
                }
                // END Record Earliest Recommendations

                // Record Latest Recommendations
                final List<Recommendation> lInterimRecommendedLatest = new ArrayList<>();
                if (!interimRecommendationsScheduleLatestRecommendedAge.isEmpty())
                    lInterimRecommendedLatest.addAll(interimRecommendationsScheduleLatestRecommendedAge);
                else
                    if (!interimRecommendationsScheduleLatestRecommendedInterval.isEmpty())
                    {
                        // Latest recommended interval is only considered if there is no latest recommended age specified
                        lInterimRecommendedLatest.addAll(interimRecommendationsScheduleLatestRecommendedInterval);
                    }
                if (!interimRecommendationsCustomLatest.isEmpty())
                    lInterimRecommendedLatest.addAll(interimRecommendationsCustomLatest);
                if (log.isDebugEnabled())
                {
                    final StringBuilder lInterimRecommendedStr =
                            new StringBuilder("Interim Latest Recommendations to be examined. ");
                    int i = 1;
                    for (final Recommendation r : lInterimRecommendedLatest)
                    {
                        lInterimRecommendedStr.append(" -- Interim Latest Recommendation: ")
                                .append(i++)
                                .append(": ")
                                .append(r.getTargetSeriesIdentifier())
                                .append("; status : ")
                                .append(r.getRecommendationStatus())
                                .append("; date: ")
                                .append(r.getEarliestDate())
                                .append("; vaccine ")
                                .append(r.getRecommendedVaccine());
                    }
                    log.debug(_METHODNAME + "{}", lInterimRecommendedStr);
                }
                // END Record Latest Recommendations

                if (lFinalRecommendationStatus == RecommendationStatus.NOT_RECOMMENDED)
                {
                    setFinalEarliestDate(null);
                    setFinalOverdueDate(null);
                }
                else
                {
                    // Determine earliest age - If the recommendations date is before the earliest date, set the recommendations date to the earliest date
                    LocalDate lObtainLatestEarliest =
                            Recommendation.obtainMostRecentEarliestDateFromRecommendationsList(lInterimRecommendedEarliest);
                    final LocalDate lPrevFinalEarliestDate = getFinalEarliestDate();
                    if (lPrevFinalEarliestDate != null)
                    {
                        if (lObtainLatestEarliest == null)
                            lObtainLatestEarliest = lPrevFinalEarliestDate;
                        else
                            if (lPrevFinalEarliestDate.isAfter(lObtainLatestEarliest))
                                lObtainLatestEarliest = lPrevFinalEarliestDate;
                    }
                    if (lObtainLatestEarliest != null)
                    {
                        setFinalEarliestDate(lObtainLatestEarliest);
                        if (lFinalRecommendationDate != null && lObtainLatestEarliest.isAfter(lFinalRecommendationDate))
                            setFinalRecommendationDate(lObtainLatestEarliest);
                    }

                    // Now determine the latest recommended date. If the latest recommendations date is before the recommended date, set it to the recommended date
                    LocalDate lObtainUnadjustedLatest =
                            Recommendation.obtainMostRecentLatestRecommendationDateFromRecommendationsList(
                                    lInterimRecommendedLatest);
                    final LocalDate lPrevFinalLatestDate = getFinalOverdueDate();
                    if (lPrevFinalLatestDate != null)
                    {
                        if (lObtainUnadjustedLatest == null)
                            lObtainUnadjustedLatest = lPrevFinalLatestDate;
                        else
                            if (lPrevFinalLatestDate.isAfter(lObtainUnadjustedLatest))
                                lObtainUnadjustedLatest = lPrevFinalLatestDate;
                    }
                    if (lObtainUnadjustedLatest != null)
                    {
                        if (lFinalRecommendationDate != null && lObtainUnadjustedLatest.isBefore(lFinalRecommendationDate))
                            setFinalOverdueDate(lFinalRecommendationDate);
                        else
                            setFinalOverdueDate(lObtainUnadjustedLatest);
                    }
                }
            }

        // Reset interim recommendations tracking
        this.recommendationStatusPrior = getRecommendationStatus();
        interimRecommendationsScheduleEarliestAge = new ArrayList<>();
        interimRecommendationsScheduleEarliestInterval = new ArrayList<>();
        interimRecommendationsScheduleEarliestRecommendedAge = new ArrayList<>();
        interimRecommendationsScheduleEarliestRecommendedInterval = new ArrayList<>();
        interimRecommendationsScheduleLatestRecommendedAge = new ArrayList<>();
        interimRecommendationsScheduleLatestRecommendedInterval = new ArrayList<>();
        interimRecommendationsCustom = new ArrayList<>();
        interimRecommendationsCustomEarliest = new ArrayList<>();
        interimRecommendationsCustomLatest = new ArrayList<>();
    }

    public void evaluateVaccineGroupMinimumAgeandRecordReason(final LocalDate pEvalPersonBirthTime, final TargetDose pTD)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        evaluateVaccineGroupMinimumAgeOrMaximumAgeandRecordReason(pEvalPersonBirthTime, pTD, EvaluationType.MINIMUM_AGE);
    }

    public void evaluateVaccineGroupMaximumAgeandRecordReason(final LocalDate pEvalPersonBirthTime, final TargetDose pTD)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        evaluateVaccineGroupMinimumAgeOrMaximumAgeandRecordReason(pEvalPersonBirthTime, pTD, EvaluationType.MAXIMUM_AGE);
    }

    /**
     * Check age for the supplied dose and record evaluation reason in supplied TargetDose's validReasons, acceptedReasons and/or invalidReasons list.
     */
    private void evaluateVaccineGroupMinimumAgeOrMaximumAgeandRecordReason(final LocalDate pEvalPersonBirthTime,
            final TargetDose pTD, final EvaluationType minimumOrMaximumAgeChoice)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "evaluateVaccineGroupMinimumAgeandRecordReason(): ";

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "series {}", this.getSeriesName());

        if (pTD == null || pEvalPersonBirthTime == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        if (isSeriesComplete())
            return;

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRule == null)
        {
            final String msg = "No series dose rule specified for requested dose number " + pTD.getDoseNumberInSeries();
            log.warn(_METHODNAME + "{}", msg);
            return;
        }

        final LocalDate administrationDate = pTD.getAdministrationDate();
        if (administrationDate == null)
        {
            final String str = "Vaccination date not supplied";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }
        if (administrationDate.isBefore(pEvalPersonBirthTime))
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Vaccination date supplied before birth date");
            pTD.addInvalidReason(BaseDataEvaluationReason._PRIOR_TO_DOB.getCdsListItemName());
        }

        switch (minimumOrMaximumAgeChoice)
        {
            case MINIMUM_AGE ->
            {
                final TimePeriod minimumAge = seriesDoseRule.getAbsoluteMinimumAge();
                if (minimumAge == null)
                {
                    // There is no required age for this dose.
                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "No minimum age specified for dose: {}", pTD);
                    return;
                }

                final int compareTo =
                        TimePeriod.compareElapsedTimePeriodToDateRange(pEvalPersonBirthTime, administrationDate, minimumAge);
                if (compareTo < 0)
                    pTD.addInvalidReason(BaseDataEvaluationReason._BELOW_MINIMUM_AGE_EVALUATION_REASON.getCdsListItemName());
            }
            case MAXIMUM_AGE ->
            {
                final TimePeriod maximumAge = seriesDoseRule.getAbsoluteMaximumAge();
                if (maximumAge == null)
                {
                    // There is no required age for this dose.
                    if (log.isDebugEnabled())
                        log.debug(_METHODNAME + "No maximum age specified for dose: {}", pTD);
                    return;
                }

                final int compareTo =
                        TimePeriod.compareElapsedTimePeriodToDateRange(pEvalPersonBirthTime, administrationDate, maximumAge);
                if (compareTo > 0)
                    pTD.addInvalidReason(BaseDataEvaluationReason._ABOVE_MAXIMUM_AGE_EVALUATION_REASON.getCdsListItemName());
            }
            case null, default ->
            {
            }
        }

    }

    /**
     * Check interval and record evaluation reason in more recent TargetDose's validReasons, acceptedReasons and/or invalidReasons list.
     */
    public void evaluateVaccineGroupMinimumIntervalAndRecordReason(final TargetDose pTD, final TargetDose pTDprev)
            throws IllegalArgumentException, ICECoreError
    {
        final String _METHODNAME = "checkIntervalAndRecordEvaluationReason(): ";

        if (pTD == null || pTDprev == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        if (isSeriesComplete())
            return;

        final LocalDate previousDoseDate = pTDprev.getAdministrationDate();
        final LocalDate currentDoseDate = pTD.getAdministrationDate();
        int doseNumberForWhichToObtainRule = pTD.getDoseNumberInSeries();
        if (doseNumberForWhichToObtainRule > 1)
            doseNumberForWhichToObtainRule--;
        else
            doseNumberForWhichToObtainRule = 1;
        final DoseRule doseRulePreviousDose = obtainDoseRuleForSeriesByDoseNumber(doseNumberForWhichToObtainRule);
        final TimePeriod minimumInterval = doseRulePreviousDose.getAbsoluteMinimumInterval();
        if (minimumInterval == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No minimum interval specified for dose: {}", pTDprev);
            return;
        }

        final int compareTo = TimePeriod.compareElapsedTimePeriodToDateRange(previousDoseDate, currentDoseDate, minimumInterval);
        final TimePeriod elapsedTimePeriodBetweenDoses;
        try
        {
            elapsedTimePeriodBetweenDoses =
                    TimePeriod.calculateElapsedTimePeriod(previousDoseDate, currentDoseDate, DurationType.DAYS);
        }
        catch (final TimePeriodException tpe)
        {
            final String errStr = _METHODNAME + "caught an unexpected TimePeriodException. Cannot continue";
            log.error(errStr);
            throw new ICECoreError(errStr);
        }

        if (compareTo < 0 && !elapsedTimePeriodBetweenDoses.getTimePeriodStringRepresentation().equals("0d"))
        {
            // The elapsed time between administered doses is less than the minimum interval and the doses were not administered on the same day. Therefore, below minimum interval
            pTD.addInvalidReason(BaseDataEvaluationReason._BELOW_MINIMUM_INTERVAL_EVALUATION_REASON.getCdsListItemName());
        }
    }

    /**
     * Obtain list of all permitted vaccines for the specified target dose
     */
    public List<Vaccine> getAllPermittedVaccinesForTargetDose(final int doseNumber)
    {
        if (doseNumber < 0 || doseNumber > seriesRules.getNumberOfDosesInSeries())
            return null;

        final DoseRule lDR = obtainDoseRuleForSeriesByDoseNumber(doseNumber);
        if (lDR == null)
            return null;

        return lDR.getAllPermittedVaccines();
    }

    /**
     * Obtain list of preferable vaccines for the specified target dose
     */
    public List<Vaccine> getPreferableVaccinesForTargetDose(final int doseNumber)
    {
        if (doseNumber < 0 || doseNumber > seriesRules.getNumberOfDosesInSeries())
            return null;

        final DoseRule lDR = obtainDoseRuleForSeriesByDoseNumber(doseNumber);
        if (lDR == null)
            return null;

        return lDR.getPreferableVaccines();
    }

    /**
     * Obtain list of allowable vaccines for the specified target dose
     */
    public List<Vaccine> getAllowableVaccinesForTargetDose(final int doseNumber)
    {
        if (doseNumber < 0 || doseNumber > seriesRules.getNumberOfDosesInSeries())
            return null;

        final DoseRule lDR = obtainDoseRuleForSeriesByDoseNumber(doseNumber);
        if (lDR == null)
            return null;

        return lDR.getAllowableVaccines();
    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getAbsoluteMinimumIntervalForTargetDoseInStringFormat(final TargetDose pTD)
    {
        final String _METHODNAME = "getAbsoluteMinimumIntervalForTargetDoseInStringFormat(): ";
        final TimePeriod t;
        try
        {
            t = getAbsoluteMinimumIntervalForTargetDose(pTD);
        }
        catch (final IllegalArgumentException ie)
        {
            log.warn(_METHODNAME + "IllegalArgumentException caught");
            return null;
        }

        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    /**
     * Return the minimum interval for the specified dose; null if there is none
     *
     * @return TimePeriod
     */
    public TimePeriod getAbsoluteMinimumIntervalForTargetDose(final TargetDose pTD) throws IllegalArgumentException
    {
        final String _METHODNAME = "getAbsoluteMinimumIntervalForTargetDose(): ";
        if (pTD == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding series dose not found: " + getVaccineGroup() + "; " + getSeriesName() + "; "
                    + getTargetSeriesIdentifier();
            log.error(_METHODNAME + "{}", str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getAbsoluteMinimumInterval();
    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getAbsoluteMinimumIntervalForTargetDoseInStringFormat(final int targetDoseNumber)
    {
        final String _METHODNAME = "getAbsoluteMinimumIntervalForTargetDoseInStringFormat(): ";
        final TimePeriod t;
        try
        {
            t = getAbsoluteMinimumIntervalForTargetDose(targetDoseNumber);
        }
        catch (final IllegalArgumentException ie)
        {
            log.warn(_METHODNAME + "IllegalArgumentException caught");
            return null;
        }

        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    public TimePeriod getAbsoluteMinimumIntervalForTargetDose(final int targetDoseNumber) throws IllegalArgumentException
    {
        final String _METHODNAME = "getAbsoluteMinimumIntervalForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding series dose not found: " + getVaccineGroup() + "; " + getSeriesName() + "; "
                    + getTargetSeriesIdentifier();
            log.error(_METHODNAME + "{}", str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getAbsoluteMinimumInterval();
    }

    /**
     * @return TimePeriod representing the minimum age; null if the TargetDose supplied is null or there is no rule associated with this dose number; and finally, TimePeriod with duration set to 0 if there is no corresponding minimum age.
     */
    public TimePeriod getAbsoluteMinimumAgeForTargetDose(final int targetDoseNumber)
    {
        final String _METHODNAME = "getAbsoluteMinimumAgeForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRule == null)
            return null;

        final TimePeriod minimumAge = seriesDoseRule.getAbsoluteMinimumAge();
        if (minimumAge == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No minimum age associated with this dose rule");
            return TimePeriod.ZERO;
        }

        return minimumAge;
    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getAbsoluteMinimumAgeForTargetDoseInStringFormat(final int targetDoseNumber)
    {
        final TimePeriod t = getAbsoluteMinimumAgeForTargetDose(targetDoseNumber);
        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    /**
     * @return TimePeriod representing the minimum age; null if the TargetDose supplied is null or there is no rule associated with this dose number; and finally, TimePeriod with duration set to 0 if there is no corresponding minimum age.
     */
    public TimePeriod getAbsoluteMinimumAgeForTargetDose(final TargetDose pTD)
    {
        final String _METHODNAME = "getAbsoluteMinimumAgeForTargetDose(): ";
        if (pTD == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRule == null)
        {
            final String str = "Corresponding series dose not found";
            log.debug(_METHODNAME + str);
            return null;
        }

        final TimePeriod minimumAge = seriesDoseRule.getAbsoluteMinimumAge();
        if (minimumAge == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No minimum age associated with this dose rule");
            return TimePeriod.ZERO;
        }

        return minimumAge;

    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getMinimumIntervalForTargetDoseInStringFormat(final TargetDose pTD)
    {
        final String _METHODNAME = "getMinimumIntervalForTargetDoseInStringFormat(): ";
        final TimePeriod t;
        try
        {
            t = getMinimumIntervalForTargetDose(pTD);
        }
        catch (final IllegalArgumentException ie)
        {
            log.warn(_METHODNAME + "IllegalArgumentException caught");
            return null;
        }

        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    /**
     * Return the minimum interval for the specified dose; null if there is none
     *
     * @return TimePeriod
     */
    public TimePeriod getMinimumIntervalForTargetDose(final TargetDose pTD) throws IllegalArgumentException
    {
        final String _METHODNAME = "getMinimumIntervalForTargetDose(): ";
        if (pTD == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding series dose not found";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getMinimumInterval();
    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getMinimumIntervalForTargetDoseInStringFormat(final int targetDoseNumber)
    {
        final String _METHODNAME = "getMinimumIntervalForTargetDoseInStringFormat(): ";
        final TimePeriod t;
        try
        {
            t = getMinimumIntervalForTargetDose(targetDoseNumber);
        }
        catch (final IllegalArgumentException ie)
        {
            log.warn(_METHODNAME + "IllegalArgumentException caught");
            return null;
        }

        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    public TimePeriod getMinimumIntervalForTargetDose(final int targetDoseNumber) throws IllegalArgumentException
    {
        final String _METHODNAME = "getMinimumIntervalForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding series dose not found";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getMinimumInterval();
    }

    /**
     * @return TimePeriod representing the minimum age; null if the TargetDose
     * supplied is null or there is no rule associated with this dose
     * number; and finally, TimePeriod with duration set to 0 if there
     * is no corresponding minimum age
     */
    public TimePeriod getMinimumAgeForTargetDose(final int targetDoseNumber)
    {
        final String _METHODNAME = "getMinimumAgeForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRule == null)
        {
            final String str = "Corresponding series dose not found";
            log.debug(_METHODNAME + str);
            return null;
        }

        final TimePeriod minimumAge = seriesDoseRule.getMinimumAge();
        if (minimumAge == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No minimum age associated with this dose rule");
            return TimePeriod.ZERO;
        }

        return minimumAge;
    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getMinimumAgeForTargetDoseInStringFormat(final int targetDoseNumber)
    {
        final TimePeriod t = getMinimumAgeForTargetDose(targetDoseNumber);
        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    /**
     * @return TimePeriod representing the minimum age; null if the TargetDose
     * supplied is null or there is no rule associated with this dose
     * number; and finally, TimePeriod with duration set to 0 if there
     * is no corresponding minimum age
     */
    public TimePeriod getMinimumAgeForTargetDose(final TargetDose pTD)
    {
        final String _METHODNAME = "getMinimumAgeForTargetDose(): ";
        if (pTD == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRule == null)
        {
            final String str = "Corresponding series dose not found";
            log.debug(_METHODNAME + str);
            return null;
        }

        final TimePeriod minimumAge = seriesDoseRule.getMinimumAge();
        if (minimumAge == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No minimum age associated with this dose rule");
            return TimePeriod.ZERO;
        }

        return minimumAge;

    }

    /**
     * @return TimePeriod representing the recommended age; null if the TargetDose
     * supplied is null or there is no rule associated with this dose
     * number; and finally, TimePeriod with duration set to 0 if there
     * is no corresponding minimum age
     */
    public TimePeriod getRecommendedAgeForTargetDose(final TargetDose pTD)
    {
        final String _METHODNAME = "getRecommendedAgeForTargetDose(): ";
        if (pTD == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRule == null)
        {
            final String str = "Corresponding series dose not found";
            log.debug(_METHODNAME + str);
            return null;
        }

        final TimePeriod recommendedAge = seriesDoseRule.getEarliestRecommendedAge();
        if (recommendedAge == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No minimum age associated with this dose rule");
            return TimePeriod.ZERO;
        }

        return recommendedAge;

    }

    /**
     * @return TimePeriod representing the recommended age; null if the TargetDose
     * supplied is null or there is no rule associated with this dose
     * number; and finally, TimePeriod with duration set to 0 if there
     * is no corresponding minimum age
     */
    public TimePeriod getRecommendedAgeForTargetDose(final int targetDoseNumber)
    {
        final String _METHODNAME = "getRecommendedAgeForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRule = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRule == null)
        {
            final String str = "Corresponding series dose not found";
            log.debug(_METHODNAME + str);
            return null;
        }

        final TimePeriod recommendedAge = seriesDoseRule.getEarliestRecommendedAge();
        if (recommendedAge == null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "No recommended age associated with this dose rule");
            return TimePeriod.ZERO;
        }

        return recommendedAge;
    }

    /**
     * Obtain minimum interval in string format year, month, or day. e.g. -
     * "4y", "5m", "6d"
     *
     * @return string representation, or null if none
     */
    public String getRecommendedAgeForTargetDoseInStringFormat(final int targetDoseNumber)
    {
        final TimePeriod t = getRecommendedAgeForTargetDose(targetDoseNumber);
        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    /**
     * Return the recommended interval for the specified dose; null if there is
     * none
     *
     * @return TimePeriod
     */
    public TimePeriod getRecommendedIntervalForTargetDose(final TargetDose pTD) throws IllegalArgumentException
    {
        final String _METHODNAME = "getRecommendedIntervalForTargetDose(): ";
        if (pTD == null)
        {
            final String errStr = "Invalid parameters supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(_METHODNAME + errStr);
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByTargetDose(pTD);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding previous series dose not found";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getEarliestRecommendedInterval();
    }

    /**
     * Return the recommended interval for the specified dose; null if there is none
     *
     * @return TimePeriod
     */
    public TimePeriod getRecommendedIntervalForTargetDose(final int targetDoseNumber) throws IllegalArgumentException
    {
        final String _METHODNAME = "getRecommendedIntervalForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding previous series dose not found";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getEarliestRecommendedInterval();
    }

    /**
     * Return the latest recommended interval for the specified dose; null if there is none
     *
     * @return TimePeriod
     */
    public TimePeriod getLatestRecommendedIntervalForTargetDose(final int targetDoseNumber) throws IllegalArgumentException
    {
        final String _METHODNAME = "getLatestRecommendedIntervalForTargetDose(): ";
        if (targetDoseNumber <= 0)
        {
            final String errStr = "Invalid parameters supplied";
            log.debug(_METHODNAME + errStr);
            return null;
        }

        final DoseRule seriesDoseRulePrev = obtainDoseRuleForSeriesByDoseNumber(targetDoseNumber);
        if (seriesDoseRulePrev == null)
        {
            final String str = "Corresponding previous series dose not found";
            log.error(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        return seriesDoseRulePrev.getLatestRecommendedInterval();
    }

    public String getRecommendedIntervalForTargetDoseInStringFormat(final int targetDoseNumber) throws IllegalArgumentException
    {
        final TimePeriod t = getRecommendedIntervalForTargetDose(targetDoseNumber);
        if (t == null)
            return null;

        return t.getTimePeriodStringRepresentation();
    }

    public int getDoseNumberToRecommend()
    {
        final int doseNumberToRecommend;
        if (this.manuallySetDoseNumberToRecommend != 0)
            doseNumberToRecommend = this.manuallySetDoseNumberToRecommend;
        else
            doseNumberToRecommend = determineEffectiveNumberOfDosesInSeries() + 1;

        return doseNumberToRecommend;
    }

    /**
     * Return true if this TargetSeries contains the specified TargetDose; false
     * if not
     *
     * @return boolean true or false
     */
    public boolean containsTargetDose(final TargetDose pTD)
    {
        if (pTD == null)
            return false;

        return targetDoses.stream().anyMatch(td -> td.equals(pTD));
    }

    /**
     * @deprecated This routine will be retired
     */
    @Deprecated
    public boolean areNoEarlierAdministeredShotsNotEvaluated(final TargetDose pTD)
    {
        final String _METHODNAME = "noEarlierAdministeredShotNotEvaluated() ";

        if (pTD == null)
            return false;

        final LocalDate targetDoseDate = pTD.getAdministrationDate();
        final int administeredShotNumber = pTD.getAdministeredShotNumberInSeries();
        if (targetDoseDate == null)
        {
            final String errStr = "TargetDose supplied does not contain a date";
            log.warn(_METHODNAME + "{}", (Object) null);
            throw new IllegalArgumentException(errStr);
        }

        int maxAdministeredShotNumberWithSameDate = 0;
        int minAdminiteredShotNumberWithSameDate = 0;
        for (final TargetDose td : targetDoses)
        {
            final LocalDate tdShotDate = td.getAdministrationDate();
            final DoseStatus tdStatus = td.getStatus();
            final int tdAdministeredShotNumber = td.getAdministeredShotNumberInSeries();
            if (tdShotDate == null)
            {
                final String errStr = "shot in series found without a date";
                log.error(_METHODNAME + errStr);
                throw new InconsistentConfigurationException(errStr);
            }

            final int tdShotDateToTargetDoseDateComparison = tdShotDate.compareTo(targetDoseDate);
            if (tdStatus == DoseStatus.EVALUATION_NOT_STARTED && tdShotDateToTargetDoseDateComparison < 0)
                return false;

            if (tdStatus == DoseStatus.EVALUATION_NOT_STARTED && tdShotDateToTargetDoseDateComparison > 0)
                break;

            if (tdStatus == DoseStatus.EVALUATION_NOT_STARTED)
            {
                if (minAdminiteredShotNumberWithSameDate == 0)
                    minAdminiteredShotNumberWithSameDate = tdAdministeredShotNumber;
                if (maxAdministeredShotNumberWithSameDate == 0)
                    maxAdministeredShotNumberWithSameDate = tdAdministeredShotNumber;
                if (tdAdministeredShotNumber > maxAdministeredShotNumberWithSameDate)
                    maxAdministeredShotNumberWithSameDate = tdAdministeredShotNumber;
                if (tdAdministeredShotNumber < minAdminiteredShotNumberWithSameDate)
                    minAdminiteredShotNumberWithSameDate = tdAdministeredShotNumber;
            }
        }

        return minAdminiteredShotNumberWithSameDate >= administeredShotNumber;
    }

    public int getNumberOfShotsAdministeredInSeries()
    {
        if (targetDoses == null)
            return 0;

        return targetDoses.size();
    }

    public int getNumberOfShotsAdministeredInSeriesExcludingDuplicateShotsOnTheSameDay()
    {
        if (targetDoses == null)
            return 0;

        final String lDuplicateShotReason = BaseDataEvaluationReason._DUPLICATE_SAME_DAY_REASON.getCdsListItemName();
        return Math.toIntExact(targetDoses.stream().filter(td -> !td.containsReason(lDuplicateShotReason)).count());
    }

    /**
     * Return the dose rule for the dose number in question in this target series
     *
     * @param doseNumber If 0 is passed in, it is assumed that dose 1 is desured.
     * @return Dose containing rules for that dose, or null if there is no DoseRule for the specified dose number
     */
    public DoseRule obtainDoseRuleForSeriesByDoseNumber(final int doseNumber)
    {
        if (seriesRules == null)
            return null;

        final int lDoseNumber = (doseNumber == 0) ? 1 : doseNumber;
        return seriesRules.getSeriesDoseRules().stream().filter(d -> d.getDoseNumber() == lDoseNumber).findFirst().orElse(null);
    }

    /**
     * Return dose rule for the shot most recently administered prior to the date specified in this target series
     *
     * @return DoseRule containing the rules for the dose, or null if there is no shot administered prior to the specified date
     */
    public DoseRule obtainDoseRuleOfAdministeredShotWithMostRecentDatePriorToTargetDose(final TargetDose pTD)
    {
        if (seriesRules == null || pTD == null || !this.targetDoses.contains(pTD))
            return null;

        TargetDose lTDOfInterest = pTD;
        boolean priorTargetDoseIdentified = false;
        while (true)
        {
            final TargetDose lTDPrior = this.targetDoses.lower(lTDOfInterest);
            if (lTDPrior == null)
                break;

            // Business logic of TargetDate ensures that the administration date is not null
            final LocalDate lTDOfInterestShotDate = lTDOfInterest.getAdministrationDate();
            final LocalDate lTDPriorShotDate = lTDPrior.getAdministrationDate();
            if (lTDPriorShotDate.isBefore(lTDOfInterestShotDate))
            {
                priorTargetDoseIdentified = true;
                break;
            }

            lTDOfInterest = lTDPrior;
        }

        // Found the shot with the most recent prior date
        if (priorTargetDoseIdentified)
            return obtainDoseRuleForSeriesByTargetDose(lTDOfInterest);

        return null;

    }

    /**
     * Return the series dose rule corresponding to the supplied TargetDose.
     *
     * @param pTD TargetDose in this series for which we want to find the
     *            matching series dose rule
     * @return Series Dose rule, or null if not found
     */
    public DoseRule obtainDoseRuleForSeriesByTargetDose(final TargetDose pTD)
    {
        if (pTD == null || seriesRules == null)
            return null;

        final int doseNumber = pTD.getDoseNumberInSeries();
        return seriesRules.getSeriesDoseRules().stream().filter(d -> d.getDoseNumber() == doseNumber).findFirst().orElse(null);
    }

    /**
     * Add a new DoseRule to the Series witht the specified DoseRule.
     *
     * @throws IllegalArgumentException if the DoseRule's dose number is not equal to the next dose number in this TargetSeries
     */
    public void addVaccineGroupDoseRule(final DoseRule pDR)
    {
        final String _METHODNAME = "addVaccineGroupDoseRule(): ";
        if (pDR == null)
        {
            log.warn(_METHODNAME + "DoseRule supplied is null; DoseRule was not added");
            return;
        }

        final int lNextDoseNumber = seriesRules.getNumberOfDosesInSeries() + 1;
        if (pDR.getDoseNumber() != lNextDoseNumber)
        {
            final String errStr =
                    "Specified DoseRule's dose number is not the next dose number in this TargetSeries; addition cannot be made";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        // Make a copy of the SeriesRules object first... do not want to change default rules used by others
        final SeriesRules lSR = SeriesRules.constructDeepCopyOfSeriesRulesObject(this.seriesRules);
        lSR.addSeriesDoseRule(pDR);
        this.seriesRules = lSR;
        isSeriesComplete();        // reset series complete flag
    }

    /**
     * Modify the existing DoseRule in the Series with the specified DoseRule.
     *
     * @throws IllegalArgumentException if the dose number of the specified DoseRule is not a valid dose number in this TargetSeries.
     */
    public void modifyVaccineGroupDoseRule(final DoseRule pDR)
    {
        final String _METHODNAME = "modifyVaccineGroupDoseRule(): ";
        if (pDR == null)
        {
            log.warn(_METHODNAME + "DoseRule supplied is null; existing DoseRule was not modified");
            return;
        }

        final int lDoseNumber = pDR.getDoseNumber();
        if (lDoseNumber <= 0 || lDoseNumber > seriesRules.getNumberOfDosesInSeries())
        {
            final String errStr =
                    "Specified DoseRule's dose number is not a dose number that exists in this TargetSeries; modification cannot be made";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        // Make a copy of the SeriesRules object first... do not want to change default rules used by others
        final SeriesRules lSR = SeriesRules.constructDeepCopyOfSeriesRulesObject(this.seriesRules);
        lSR.modifySeriesDoseRule(pDR);
        this.seriesRules = lSR;
        isSeriesComplete();        // reset series complete flag
    }

    /**
     * Make notes that the patient has immunity to the specified series. This
     * will affect evaluation of all remaining doses not yet evaluated in the
     * series
     *
     * @param pSDC SupportedDiseaseConcept
     */
    public void markImmunityToSpecifiedDisease(final String pSDC, final LocalDate pDateOfImmunity)
    {
        if (pSDC == null || pDateOfImmunity == null)
            return;

        if (this.interimEvaluationValidityCountByDisease.containsKey(pSDC))
        {
            // Only mark the disease immunity if this series supports the
            // specified disease
            this.diseaseImmunityDate.put(pSDC, pDateOfImmunity);
        }
    }

    /**
     * Calls addTargetDose(TargetDose, boolean) with overrideSeasonDateRestriction set to false
     *
     * @return true if TargetDose added, false if not
     * If there is an associated Season in this TargetSeries, if the supplied TargetDose does not fall within the applicable Season, then it is not
     * added.
     */
    protected boolean addTargetDoseToSeries(final TargetDose targetDose)
    {
        return addTargetDoseToSeries(targetDose, false);
    }

    /**
     * Add TargetDose to the TargetSeries. Also, set the the administered shot number for all TargetDosees in this TargetSeries in increasing order,
     * based on the administered shot dates in this TargetSeries. If the current shot is not evaluated yet or invalid, this and other administered
     * unevaluated shots may be set to the same dose number-- they're next possible valid or accepted shots [i.e. - doses] amongst others in the
     * series. Upon initialization, all shots are set with doseNumber=1. Note that upon initialization, all shots are set to EVALUATION_NOT_STARTED (they have
     * not been evaluated yet), so all dose numbers will be set to 1 in this typical scenario for use of this method.
     * <p>
     * This version of the method allows the caller to override the check, for seasonal TargetSeries, which enforces the TargetDose fall between the
     * season start and end dates
     *
     * @return true if TargetDose added, false if not
     */
    protected boolean addTargetDoseToSeries(final TargetDose targetDose, final boolean overrideSeasonDateRestriction)
    {
        final String _METHODNAME = "addTargetDoseToSeries(): ";

        boolean targetDoseAdded = false;
        if (targetDose == null)
            return false;

        final LocalDate targetDoseDate = targetDose.getAdministrationDate();
        if (targetDoseDate == null)
        {
            final String str = "Supplied TargetDose does not have an administration date";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        if (this.targetSeason == null || overrideSeasonDateRestriction || this.targetSeason.dateIsApplicableToSeason(
                targetDoseDate))
        { // Off-Season incl if needed
            this.targetDoses.add(targetDose);
            targetDoseAdded = true;
        }

        int i = 1;
        for (final TargetDose td : this.targetDoses)
        {
            td.setAdministeredShotNumberInSeries(i++);
            td.setDoseNumberInSeries(determineDoseNumberInSeries(td));
        }

        return targetDoseAdded;
    }

    /**
     * Remove a target dose from TargetSeries. This method should be used in limited situations, such as prior to when the target series evaluation begins, or to allow rule author says that the vaccine should not be evaluated as a part
     * of this series. (e.g. CVX 109 may be evaluated as a part of PCV or PPSV depending circumstances).
     */
    public void removeTargetDoseFromSeries(final TargetDose targetDose)
    {
        // String _METHODNAME = "removeTargetDoseFromSeries(): ";
        if (targetDose == null)
            return;

        final NavigableSet<TargetDose> targetDosesNew =
                Collections.synchronizedNavigableSet(new TreeSet<>(new TargetSeriesComparator())); // Ordered by administration date
        // Iterate through doses in the TargetSeries, so that doseNumber can be renumbered properly
        int i = 1;
        boolean foundShotToRemove = false;
        int prevDoseNumber = 1;
        for (final TargetDose td : this.targetDoses)
        {
            if (!foundShotToRemove && td.equals(targetDose))
            {
                foundShotToRemove = true;
                prevDoseNumber = td.getDoseNumberInSeries();
            }
            else
            {        // found shot to remove is true
                final int nextDoseNumber = td.getDoseNumberInSeries();
                td.setAdministeredShotNumberInSeries(i++);
                td.setDoseNumberInSeries(prevDoseNumber);
                targetDosesNew.add(td);
                prevDoseNumber = nextDoseNumber;
            }
        }

        this.targetDoses = targetDosesNew;
    }

    public boolean targetSeasonExists()
    {
        return targetSeason != null;
    }

    /**
     * Get the start date of the season for this TargetSeries, if any- (if this is not a seasonal series, null is returned)
     *
     * @return Date of the start date of the season, or null if none
     */
    public LocalDate getSeasonStartDate()
    {
        if (targetSeason == null)
            return null;

        return targetSeason.getFullySpecifiedSeasonStartDate();
    }

    /**
     * Get the end date of the season for this TargetSeries, if any- (if this is not a seasonal series, null is returned)
     *
     * @return Date of the end date of the target season, or null if none
     */
    public LocalDate getSeasonEndDate()
    {
        if (targetSeason == null)
            return null;

        return targetSeason.getFullySpecifiedSeasonEndDate();
    }

    /**
     * Get the off-season start date of the season for this TargetSeries, if any - (if this is not a seasonal series, null is returned)
     *
     * @return Date of the end date of the target season, or null if none
     */
    public LocalDate getOffSeasonStartDate()
    {
        if (targetSeason == null)
            return null;

        return targetSeason.getFullySpecifiedSeasonOffSeasonStartDate();
    }

    /**
     * Get the off-season start date of the season for this TargetSeries, if any- (if this is not a seasonal series, null is returned)
     *
     * @return Date of the end date of the target season, or null if none
     */
    public LocalDate getOffSeasonEndDate()
    {
        if (targetSeason == null)
            return null;

        return targetSeason.getFullySpecifiedSeasonOffSeasonEndDate();
    }

    public String getSeriesName()
    {
        return seriesRules.getSeriesName();
    }

    public String getVaccineGroup()
    {
        return seriesRules.getVaccineGroup();
    }

    /*
     * Returns the Set of shots tracked by this class, in ascending order by date, or empty if there are none
     */
    public SortedSet<TargetDose> getTargetDoses()
    {
        return targetDoses;
    }

    public void setSeriesRules(final SeriesRules seriesRules)
    {
        final String _METHODNAME = "setSeriesRules(): ";
        if (seriesRules == null)
        {
            final String errStr = "SeriesRules parameters was not supplied";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        this.seriesRules = seriesRules;
    }

    public int getDoseAfterWhichSeriesWasMarkedComplete()
    {
        return this.seriesCompleteAtDoseNumber;
    }

    public boolean isSeriesComplete()
    {
        if (!seriesCompleteFlagManuallySet)
            determineIfSeriesCompleteAndReturnLastDose();
        return seriesComplete;
    }

    /**
     * Manually mark the series complete. If this method is called, it overrides any automated computations of series completeness based on the generic
     * series tables rules, and therefore this series' completeness value will not be changed automatically based on automated generic series
     * computations after this method has been called. The series completeness value will only be modified after the first call to this method if the
     * caller explicitly calls this method again
     */
    public void setSeriesComplete(final boolean pSeriesComplete)
    {
        this.seriesComplete = pSeriesComplete;
        if (pSeriesComplete)
        {
            if (this.seriesCompleteAtDoseNumber == 0)
                setSeriesCompleteDoseNumber();
        }
        else
            this.seriesCompleteAtDoseNumber = 0;

        this.seriesCompleteFlagManuallySet = true;
    }

    private void setSeriesCompleteDoseNumber()
    {
        if (this.seriesComplete && this.seriesCompleteAtDoseNumber == 0)
        {
            final int lEffectiveNumberOfDoses = determineEffectiveNumberOfDosesInSeries();
            final int lNumberOfDosesDefinedInSeries = getSeriesRules().getNumberOfDosesInSeries();
            if (lEffectiveNumberOfDoses > 0 && lEffectiveNumberOfDoses < lNumberOfDosesDefinedInSeries)
                this.seriesCompleteAtDoseNumber = lEffectiveNumberOfDoses;
            else
                this.seriesCompleteAtDoseNumber = lNumberOfDosesDefinedInSeries;
        }
    }

    private void setSeriesCompleteAtSpecifiedDoseNumber(final int pDoseNumberSpecified)
    {
        if (this.seriesComplete && this.seriesCompleteAtDoseNumber == 0)
        {
            final int lNumberOfDosesDefinedInSeries = getSeriesRules().getNumberOfDosesInSeries();
            if (pDoseNumberSpecified > 0 && pDoseNumberSpecified < lNumberOfDosesDefinedInSeries)
                this.seriesCompleteAtDoseNumber = pDoseNumberSpecified;
            else
                this.seriesCompleteAtDoseNumber = lNumberOfDosesDefinedInSeries;
        }
    }

    public void addLiveVirusDateAccountedForInRecommendedFinalEarliestDate(final LocalDate pLiveVirusDate)
    {
        if (pLiveVirusDate == null)
            return;

        if (!this.liveVirusDatesAccountedForInRecommendedFinalEarliestDate.contains(pLiveVirusDate))
            this.liveVirusDatesAccountedForInRecommendedFinalEarliestDate.add(pLiveVirusDate);
    }

    public void addLiveVirusDateAccountedForInRecommendedFinalDate(final LocalDate pLiveVirusDate)
    {
        if (pLiveVirusDate == null)
            return;

        if (!this.liveVirusDatesAccountedForInRecommendedFinalDate.contains(pLiveVirusDate))
            this.liveVirusDatesAccountedForInRecommendedFinalDate.add(pLiveVirusDate);
    }

    public void addAdjuvantDateAccountedForInRecommendedFinalEarliestDate(final LocalDate pAdjuvantDate)
    {
        if (pAdjuvantDate == null)
            return;

        if (!this.adjuvantDatesAccountedForInRecommendedFinalEarliestDate.contains(pAdjuvantDate))
            this.adjuvantDatesAccountedForInRecommendedFinalEarliestDate.add(pAdjuvantDate);
    }

    public void addAdjuvantDateAccountedForInRecommendedFinalDate(final LocalDate pAdjuvantDate)
    {
        if (pAdjuvantDate == null)
            return;

        if (!this.adjuvantDatesAccountedForInRecommendedFinalDate.contains(pAdjuvantDate))
            this.adjuvantDatesAccountedForInRecommendedFinalDate.add(pAdjuvantDate);
    }

    public void addSeriesRuleProcessed(final String ruleName)
    {
        if (ruleName != null)
            seriesRulesProcessed.add(ruleName);
    }

    public Collection<String> getDiseasesSupportedByThisSeries()
    {
        return interimEvaluationValidityCountByDisease.keySet();
    }

    private int getInterimDiseaseEvaluationValidityCount(final String disease)
    {
        if (disease == null)
            return 0;

        return interimEvaluationValidityCountByDisease.getOrDefault(disease, 0);
    }

    /**
     * Returns a copy of the evaluation validity counts tracked in this series for each diseases
     */
    public Map<String, Integer> getAllEvaluationValidityCountsByDisease()
    {
        return new HashMap<>(this.interimEvaluationValidityCountByDisease);
    }

    public Map<String, Integer> getEvaluationValidityCountsByDiseasesSpecified(final List<String> pDiseasesOfInterest)
    {
        final String _METHODNAME = "getEvaluationValidityCountsByDiseasesSpecified(): ";

        final Map<String, Integer> lValidityCountsOfInterest = new HashMap<>();
        if (ObjectUtils.isEmpty(pDiseasesOfInterest))
            return lValidityCountsOfInterest;

        final Map<String, Integer> lAllDiseaseDoseCounts = this.interimEvaluationValidityCountByDisease;

        if (!lAllDiseaseDoseCounts.keySet().containsAll(pDiseasesOfInterest))
        {
            final String lErrStr = "one or more diseases of interest specified is not supported by this series: " + getSeriesName();
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        for (final String lDiseaseOfInterest : pDiseasesOfInterest)
        {
            lValidityCountsOfInterest.put(lDiseaseOfInterest, lAllDiseaseDoseCounts.get(lDiseaseOfInterest));
        }

        return lValidityCountsOfInterest;
    }

    public void setManuallySetAccountForLiveVirusIntervalsInRecommendation(final boolean yesno)
    {
        manuallySetAccountForLiveVirusIntervalsInRecommendation = yesno;
    }

    /**
     * If recommendationStatus is RECOMMENDED or RECOMMENDED_IN_FUTURE, check to see if the recommendationStatus should be changed according to the
     * final recommendations date and evaluation time parameters; change it to the other if necessary
     * e.g. - current recommendations status is RECOMMENDED, pEvalTime is 4/2/2010, and final recommendations date is 4/29/2010. Therefore, change the
     * recommendationStatus from RECOMMENDED to RECOMMENDED_IN_FUTURE.
     * <p>
     * If the supplied pEvalTime is null, finalRecommendationDate is null, or the current RecommendationStatus is not RECOMMENDED or RECOMMENDED_IN_FUTURE,
     * then this method has no effect.
     *
     * @return true if recommendations status (and reason if applicable) was updated, false if not
     */
    public boolean adjustRecommendationStatusAndReasonByEvalTime(final LocalDate pEvalTime)
    {
        if (pEvalTime == null || this.finalRecommendationDate == null || (recommendationStatus != RecommendationStatus.RECOMMENDED
                && recommendationStatus != RecommendationStatus.RECOMMENDED_IN_FUTURE))
            return false;

        final RecommendationStatus lPriorRS = getRecommendationStatus();
        final int compareTo = pEvalTime.compareTo(this.finalRecommendationDate);
        if (compareTo < 0)
            setRecommendationStatus(RecommendationStatus.RECOMMENDED_IN_FUTURE);
        else
            setRecommendationStatus(RecommendationStatus.RECOMMENDED);

        final BaseDataRecommendationReason lPriorRSReason = getGenericRecommendationReasonForRecommendationStatus(lPriorRS);
        final BaseDataRecommendationReason lCurrentRSReason =
                getGenericRecommendationReasonForRecommendationStatus(getRecommendationStatus());
        final boolean lRecommendationStatusChanged = !lPriorRS.equals(getRecommendationStatus());
        for (final Recommendation lRec : this.finalRecommendations)
        {
            // TODO: H/I
            if ((lRec.getRecommendationStatus() == lPriorRS && lRecommendationStatusChanged)
                    || lRec.getRecommendationStatus() == RecommendationStatus.NOT_FORECASTED)
            {
                if (lRec.getRecommendationReason() != null && lRec.getRecommendationReason()
                        .equals(lPriorRSReason.getCdsListItemName()))
                {
                    lRec.setRecommendationReason(lCurrentRSReason.getCdsListItemName());
                }
                lRec.setRecommendationStatus(getRecommendationStatus());
            }
        }

        return true;
    }

    /**
     * Specify if forecast date should be displayed for conditional recommendations
     */
    public void setForecastDateToBeDisplayedForConditionalRecommendations(final boolean yesno)
    {
        this.displayForecastDateForConditionalRecommendations = yesno;
    }

    public boolean isForecastDateDisplayedForConditionalRecommendations()
    {
        return this.displayForecastDateForConditionalRecommendations;
    }

    /**
     * Clear any prior recommendations; typically will be used if one wishes to reprocess all recommendations
     */
    public void clearRecommendations()
    {
        setRecommendationVaccine(null);
        setFinalEarliestDate(null);
        setFinalRecommendationDate(null);
        setFinalOverdueDate(null);
        setFinalRecommendations(null);
    }

    /**
     * Set the final recommendations date. The final earliest and final latest recommendations dates are affected as follows:
     * 1) If the earliest date is present and after the supplied recommendations date, the earliest date is also changed to the supplied recommendations date.
     * 2) If the latest (overdue) date is present and before the supplied recommendations date, then the latest date is changed to the supplied recommendations date.
     * 3) If the supplied recommendations date is null, then the earliest and latest recommendations dates are changed to null.
     */
    public void setFinalRecommendationDate(final LocalDate pFinalRecommendationDate)
    {
        this.finalRecommendationDate = pFinalRecommendationDate;
        this.liveVirusDatesAccountedForInRecommendedFinalDate.clear();
        this.adjuvantDatesAccountedForInRecommendedFinalDate.clear();

        if (pFinalRecommendationDate != null)
        {
            // Check to ensure consistency with the earliest date
            LocalDate lDate = getFinalEarliestDate();
            if (lDate != null && pFinalRecommendationDate.isBefore(lDate))
                setFinalEarliestDate(pFinalRecommendationDate);
            // Check to ensure consistency with the latest date
            lDate = getFinalOverdueDate();
            if (lDate != null && pFinalRecommendationDate.isAfter(lDate))
                setFinalOverdueDate(pFinalRecommendationDate);
        }
        else
        {
            setFinalEarliestDate(null);
            setFinalOverdueDate(null);
        }
    }

    public List<String> getRecommendationReasonsForStatus(final RecommendationStatus status)
    {
        return this.finalRecommendations.stream()
                .filter(rec -> rec.getRecommendationStatus() == status)
                .map(Recommendation::getRecommendationReason)
                .toList();
    }

    private void setFinalRecommendations(final List<Recommendation> recommendation)
    {
        this.finalRecommendations = new ArrayList<>();
        if (recommendation == null)
            return;

        for (final Recommendation r : recommendation)
        {
            if (r != null)
                this.finalRecommendations.add(r);
        }
    }

    /**
     * Set the final earliest date. The final recommended and final latest recommended dates are affected as follows:
     * 1) If the recommended date is present and before the supplied earliest date, then the recommendations date is changed to the supplied earliest date.
     * 2) If the latest recommended date is present and before the supplied earliest date, then the latest date is changed to the supplied earliest date.
     * 3) If the supplied earliest date is null, no changes are made to the recommended and latest recommendations dates.
     */
    public void setFinalEarliestDate(final LocalDate finalEarliestDate)
    {
        this.finalEarliestDate = finalEarliestDate;
        this.liveVirusDatesAccountedForInRecommendedFinalEarliestDate.clear();
        this.adjuvantDatesAccountedForInRecommendedFinalEarliestDate.clear();

        if (finalEarliestDate != null)
        {
            // Check to ensure consistency with recommendations date
            LocalDate lDate = getFinalRecommendationDate();
            if (lDate != null && finalEarliestDate.isAfter(lDate))
                setFinalRecommendationDate(finalEarliestDate);
            // Check to ensure consistency with latest recommendations date
            lDate = getFinalOverdueDate();
            if (lDate != null && finalEarliestDate.isAfter(lDate))
                setFinalOverdueDate(finalEarliestDate);
        }
    }

    private void addFinalRecommendations(final List<Recommendation> recommendations)
    {
        if (recommendations == null)
            return;

        for (final Recommendation r : recommendations)
        {
            if (r != null)
                this.finalRecommendations.add(r);
        }
    }

    private void addFinalRecommendation(final Recommendation recommendation)
    {
        if (recommendation != null)
            this.finalRecommendations.add(recommendation);
    }

    @Override
    public String toString()
    {
        return "TargetSeries [ getSeriesName()=%s, getVaccineGroup()=%s, getTargetSeason()=%s, isSeriesComplete()=%s, isSelectedSeries()=%s, number of targetDose(s)=%d ]".formatted(
                getSeriesName(), getVaccineGroup(), getTargetSeason(), isSeriesComplete(), isSelectedSeries(),
                this.targetDoses.size());
    }
}
