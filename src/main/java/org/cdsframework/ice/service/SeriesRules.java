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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.util.TimePeriod;
import org.springframework.util.ObjectUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Slf4j
public class SeriesRules
{
    /**
     * Construct a copy of this object and return it
     *
     * @return SeriesRules full copy of the SeriesRules object passed into this method; null if there series passed in is null.
     */
    public static SeriesRules constructDeepCopyOfSeriesRulesObject(final SeriesRules pSR)
    {
        if (pSR == null)
            return null;

        final SeriesRules lSR = new SeriesRules(pSR.getSeriesName(), pSR.getVaccineGroupConcept());
        lSR.seriesId = ICELogicHelper.generateUniqueString();
        lSR.seriesName = pSR.seriesName;
        lSR.seriesGroup = pSR.seriesGroup;
        lSR.vaccineGroupConcept = CdsConcept.constructDeepCopyOfCdsConceptObject(pSR.getVaccineGroupConcept());
        lSR.numberOfDosesInSeries = pSR.numberOfDosesInSeries;
        lSR.seriesStartAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pSR.seriesStartAge);
        lSR.seriesEndAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pSR.seriesEndAge);
        lSR.recurringDosesAfterSeriesComplete = pSR.recurringDosesAfterSeriesComplete;
        lSR.doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered =
                pSR.doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered;
        lSR.applicableSeasons = new ArrayList<>();

        // Copy Doses
        final List<DoseRule> lDoseRules = new ArrayList<>();
        for (final DoseRule pDR : pSR.getSeriesDoseRules())
            lDoseRules.add(DoseRule.constructDeepCopyOfDoseRuleObject(pDR));
        lSR.setSeriesDoseRules(lDoseRules);

        // Copy Seasons
        final List<Season> lSeasons = new ArrayList<>();
        for (final Season pS : pSR.getSeasons())
            lSeasons.add(Season.constructDeepCopyOfSeasonObject(pS));
        lSR.applicableSeasons = lSeasons;

        return lSR;
    }

    private String seriesId;
    @EqualsAndHashCode.Include
    private String seriesName;
    @EqualsAndHashCode.Include
    private CdsConcept vaccineGroupConcept;
    private int numberOfDosesInSeries;
    private int seriesGroup;
    private int seriesGroupToTransitionTo;
    private TimePeriod seriesStartAge;
    private TimePeriod seriesEndAge;
    private boolean recurringDosesAfterSeriesComplete;
    private boolean doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered;
    private List<DoseRule> seriesDoseRules;
    private List<Season> applicableSeasons;

    /**
     * Instantiate a series rules instance. SeriesDoseRules and applicableSeasons set to empty. Set flag to calculate dose number based on disease tally to true by default.
     *
     * @param pSeriesName   Series name, must be provided
     * @param pVaccineGroup CdsConcept representing the vaccine group, must be provided
     * @throws IllegalArgumentException of series name or vaccine group is null
     */
    public SeriesRules(final String pSeriesName, final CdsConcept pVaccineGroup)
    {
        final String _METHODNAME = "Series(): ";
        if (pSeriesName == null || pVaccineGroup == null || pVaccineGroup.getOpenCdsConceptCode() == null)
        {        // the latter condition should never occur
            final String str = "series name and/or vaccine group name is not supplied";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        // seriesId = pSeriesName;
        seriesId = ICELogicHelper.generateUniqueString();
        seriesName = pSeriesName;
        seriesGroup = 0;
        seriesGroupToTransitionTo = 0;
        vaccineGroupConcept = pVaccineGroup;
        seriesDoseRules = new ArrayList<>();
        applicableSeasons = new ArrayList<>();
        numberOfDosesInSeries = 0;
        seriesStartAge = null;
        seriesEndAge = null;
        doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered = true;
        recurringDosesAfterSeriesComplete = false;
    }

    /**
     * Instantiate a series rules instance with the specified Seasons. If seasons are not specified, then the series is not treated as a Seasonal series.
     */
    /////// public SeriesRules(String pSeriesName, String pVaccineGroup, List<Season> pApplicableSeasons) {
    public SeriesRules(final String pSeriesName, final CdsConcept pVaccineGroup, final List<Season> pApplicableSeasons)
    {
        this(pSeriesName, pVaccineGroup);

        if (!ObjectUtils.isEmpty(pApplicableSeasons))
            applicableSeasons = pApplicableSeasons;
    }

    /**
     * Set the Series Name. Cannot be null.
     */
    public void setSeriesName(final String seriesName)
    {
        if (seriesName == null)
            throw new IllegalArgumentException("seriesName cannot be null");

        this.seriesName = seriesName;
    }

    private CdsConcept getVaccineGroupConcept()
    {
        return vaccineGroupConcept;
    }

    public String getVaccineGroup()
    {
        return vaccineGroupConcept.getOpenCdsConceptCode();
    }

    public boolean isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered()
    {
        return doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered;
    }

    public void setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(final boolean yesno)
    {
        doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered = yesno;
    }

    public boolean recurringDosesOccurAfterSeriesComplete()
    {
        return recurringDosesAfterSeriesComplete;
    }

    /**
     * Set the List of DoseRules for this series. Update the numberOfDosesInSeries based on the provided number of DoseRules. There must be a dose number
     * that matches the size of the List. If the size does not match with each dose number accounted for, an IllegalArgumentException is thrown.
     */
    public void setSeriesDoseRules(final List<DoseRule> pDoseRules)
    {
        final String _METHODNAME = "setSeriesDoseRules(List<DoseRule>): ";
        if (pDoseRules == null)
        {
            this.seriesDoseRules = new ArrayList<>();
            setNumberOfDosesInSeries(0);
        }
        else
        {
            // Go through list of DoseRules provided; there must be a dose number (unique) that matches the size of the List
            final int lNumberOfDoses = pDoseRules.size();
            final Set<Integer> lDoseNumbers = new HashSet<>();
            for (final DoseRule lDR : pDoseRules)
            {
                final int lDRDoseNumber = lDR.getDoseNumber();
                if (lDRDoseNumber > lNumberOfDoses || lDoseNumbers.contains(lDRDoseNumber))
                {
                    final String lErrStr =
                            "Invalid Dose Number supplied for DoseRule; either a duplicate dose number or greater than the number of Doses for the Series";
                    log.warn(_METHODNAME + lErrStr);
                    throw new IllegalArgumentException(lErrStr);
                }

                lDoseNumbers.add(lDRDoseNumber);
            }

            this.seriesDoseRules = pDoseRules;
            setNumberOfDosesInSeries(lNumberOfDoses);
        }
    }

    /**
     * Get DoseRule by dose number. If 0 is passed in, it is assumed that dose 1 is desired.
     *
     * @return DoseRule, or NULL if there is no such DoseRule in this series
     */
    public DoseRule getSeriesDoseRuleByDoseNumber(final int doseNumber)
    {
        final int lDoseNumber = (doseNumber == 0) ? 1 : doseNumber;

        return seriesDoseRules.stream().filter(dr -> dr.getDoseNumber() == lDoseNumber).findFirst().orElse(null);
    }

    /**
     * Return relevant Seasons for Series, or empty set if there are none
     */
    public List<Season> getSeasons()
    {
        return applicableSeasons;
    }

    /**
     * Add additional Season to the list of Seasons supported by this Series. If a default Season is provided, an IllegalArgumentException is thrown.
     */
    public void addFullySpecifiedSeason(final Season pS)
    {
        final String _METHODNAME = "addSeason(Season): ";
        if (pS == null)
            return;

        if (pS.isDefaultSeason())
        {
            final String errStr = "a default Season was supplied as a Season parameter to this series when one already exists";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        applicableSeasons.add(pS);
    }

    public boolean vaccineIsAllowableInOneOrMoreDoseRules(final Vaccine v)
    {
        return isAllowableVaccineForDoseRule(v, true, 0);
    }

    public boolean isAllowableVaccineForDoseRule(final Vaccine v, final int doseNumber)
    {
        return isAllowableVaccineForDoseRule(v, false, doseNumber);
    }

    private boolean isAllowableVaccineForDoseRule(final Vaccine v, final boolean allowableForAnyDose, final int doseNumber)
    {
        final String _METHODNAME = "isAllowableVaccineForDoseRule(): ";
        if (seriesDoseRules == null || v == null)
            return false;

        final String vCdsListItemName = v.getCdsConceptName();
        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "vaccine cdsListItemName: {}; doseNumber: {}; allowable any {}", vCdsListItemName, doseNumber,
                    allowableForAnyDose);

        if (vCdsListItemName == null)
            return false;

        for (final DoseRule dr : seriesDoseRules)
        {
            final int drDoseNumber = dr.getDoseNumber();
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "dose number: {}", drDoseNumber);

            if (allowableForAnyDose || drDoseNumber >= doseNumber)
            {
                if (!allowableForAnyDose && dr.getDoseNumber() != doseNumber)
                    break;

                final List<Vaccine> allPermittedComponentVaccines = dr.getAllPermittedVaccines();
                for (final Vaccine permittedVaccine : allPermittedComponentVaccines)
                {
                    if (permittedVaccine != null)
                    {
                        final String permittedVaccineCdsListItemName = permittedVaccine.getCdsConceptName();
                        if (permittedVaccineCdsListItemName == null)
                            continue;

                        if (vCdsListItemName.equals(permittedVaccineCdsListItemName))
                        {
                            if (log.isDebugEnabled())
                                log.debug(_METHODNAME + "allowable vaccine: {}", v.getCdsConceptName());
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Add a DoseRule to the Series. Doses must be added to the SeriesRules in sequential order (1...n), or an IllegalArgumentException is thrown.
     */
    public void addSeriesDoseRule(final DoseRule pDoseRule)
    {
        final String _METHODNAME = "addSeriesDoseRule(): ";
        if (pDoseRule == null)
            return;

        final int lDoseRuleDoseNumber = pDoseRule.getDoseNumber();
        if (lDoseRuleDoseNumber <= 0)
        {
            final String errStr = "No dose number is supplied for the specified DoseRule";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        final int lCurrentNumberOfDoses = this.seriesDoseRules.size();
        if (lDoseRuleDoseNumber != lCurrentNumberOfDoses + 1)
        {
            final String errStr =
                    "Dose number supplied for the specified DoseRule is not the next dose number for this SeriesRules. Doses must be added in sequential order by dose number";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        this.seriesDoseRules.add(pDoseRule);
        setNumberOfDosesInSeries(lDoseRuleDoseNumber);
    }

    /**
     * Modify an existing DoseRule in the Series. If the dose number (via specifide DoseRule) does not exist in the SeriesRules, then an IllegalArgumentException
     * is thrown.
     */
    public void modifySeriesDoseRule(final DoseRule pDoseRule)
    {
        final String _METHODNAME = "modifySeriesDoseRule(): ";
        if (pDoseRule == null)
            return;

        final int lDoseRuleDoseNumber = pDoseRule.getDoseNumber();
        if (lDoseRuleDoseNumber <= 0 || lDoseRuleDoseNumber > getNumberOfDosesInSeries())
        {
            final String errStr =
                    "DoseRule specified does not have a dose number that is valid for this SeriesRules. SeriesDose DoseRule was not modified.";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        final List<DoseRule> lSeriesDoseRules = new ArrayList<>();
        for (final DoseRule lDR : this.seriesDoseRules)
        {
            if (lDR.getDoseNumber() == lDoseRuleDoseNumber)
                lSeriesDoseRules.add(pDoseRule);
            else
                lSeriesDoseRules.add(lDR);
        }
        this.seriesDoseRules = lSeriesDoseRules;
    }

    @Override
    public String toString()
    {
        final StringBuilder toStr = new StringBuilder(
                "SeriesRules [seriesId = %s; Series Name = %s; vaccineGroupConcept name = %s; Number of Doses In Series = %d; Recurring Doses (After Series Complete)? = %s; Dose Number Calculated By Diseases Targeted By Each Vaccine = %s".formatted(
                        seriesId, seriesName, vaccineGroupConcept.getOpenCdsConceptCode(), numberOfDosesInSeries,
                        recurringDosesAfterSeriesComplete, doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered));

        int i = 1;
        toStr.append("\nDose Rules [[ ");
        for (final DoseRule dr : seriesDoseRules)
            toStr.append("\n\tDoseRule {").append(i++).append("}: ").append(dr.toString());

        toStr.append("\t]]");

        i = 1;
        toStr.append("\nSeasons [[ ");
        for (final Season s : applicableSeasons)
            toStr.append("\n\tSeason {").append(i++).append("}: ").append(s.toString());

        toStr.append("\t]]");
        toStr.append("\n]\n");

        return toStr.toString();
    }
}
