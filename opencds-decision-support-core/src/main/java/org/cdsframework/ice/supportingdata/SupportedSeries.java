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

package org.cdsframework.ice.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.DoseRule;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.service.SeriesRules;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.service.VaccineComponent;
import org.cdsframework.ice.util.TimePeriod;
import org.jspecify.annotations.NonNull;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportedSeries implements SupportingData
{
    private final SupportedCdsLists supportedCdsLists;
    private final SupportedVaccineGroups supportedVaccineGroups;
    // Supporting vaccine groups from which this series data is built
    private final SupportedVaccines supportedVaccines;
    private final SupportedSeasons supportedSeasons;
    // Supporting Data CdsLists from which this vaccine group supporting data is built
    private final Map<String, LocallyCodedSeriesItem> cdsListItemNameToSeriesItem;
    // vaccine group -> List of associated series
    // cdsListItemName (cdsListCode.cdsListItemKey) to LocallyCodedSeriesItem
    private final Map<LocallyCodedVaccineGroupItem, List<SeriesRules>> vaccineGroupItemToSeriesRules;
    private boolean isSupportingDataConsistent;

    /**
     * Create a SupportedSeries object. If the ICESupportingDataConfiguration or its associated SupportedCdsLists, SupportedVaccineGroups, or SupportedSeasons argument is null,
     * an IllegalArgumentException is thrown.
     */
    protected SupportedSeries(final ICESupportingDataConfiguration isdc) throws IllegalArgumentException
    {
        final String _METHODNAME = "SupportedCdsSeries(): ";
        if (isdc == null)
        {
            final String lErrStr = "ICESupportingDataConfiguration argument is null; a valid argument must be provided.";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportedCdsLists = isdc.getSupportedCdsLists();
        if (this.supportedCdsLists == null)
        {
            final String lErrStr = "Supporting CdsList data not set in ICESupportingDataConfiguration; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportedVaccineGroups = isdc.getSupportedVaccineGroups();
        if (this.supportedVaccineGroups == null)
        {
            final String lErrStr = "Supporting vaccine group data not set in ICESupportingDataConfiguration; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportedVaccines = isdc.getSupportedVaccines();
        if (this.supportedVaccines == null)
        {
            final String lErrStr = "Supporting vaccine data not set in ICESupportingDataConfiguration; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportedSeasons = isdc.getSupportedSeasons();
        if (this.supportedSeasons == null)
        {
            final String lErrStr = "Supporting season data not set in ICESupportingDataConfiguration; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListItemNameToSeriesItem = new HashMap<>();
        this.vaccineGroupItemToSeriesRules = new HashMap<>();
        this.isSupportingDataConsistent = true;
    }

    @Override
    public boolean isEmpty()
    {
        return this.cdsListItemNameToSeriesItem.isEmpty();
    }

    @Override
    public boolean isSupportingDataConsistent()
    {
        if (!this.isSupportingDataConsistent)
            return false;

        return checkConsistencyOfSeasonsSupportingDataAcrossAllSeriesInAllVaccineGroups();
    }

    /**
     * Return a *copy* of the list of SeriesRules associated with the specified vaccine group. If the vaccine group is not supported, null is returned.
     * If the vaccine group is supported but not SeriesRules have been specified for the vaccine group, an empty list is returned.
     */
    public List<SeriesRules> getCopyOfSeriesRulesForVaccineGroup(final LocallyCodedVaccineGroupItem plcvg)
    {
        return getSeriesRulesForVaccineGroup(plcvg, false);
    }

    /**
     * Return a reference to the list of SeriesRules associated with the specified vaccine group. If the vaccine group is not supported, null is returned.
     * If the vaccine group is supported but not SeriesRules have been specified for the vaccine group, an empty list is returned.
     */
    protected List<SeriesRules> getSeriesRulesForVaccineGroup(final LocallyCodedVaccineGroupItem plcvg)
    {
        return getSeriesRulesForVaccineGroup(plcvg, false);
    }

    private List<SeriesRules> getSeriesRulesForVaccineGroup(final LocallyCodedVaccineGroupItem plcvg, final boolean copyOf)
    {
        final List<SeriesRules> lSRs = this.vaccineGroupItemToSeriesRules.get(plcvg);
        if (lSRs == null)
            return null;

        return lSRs.stream().map(lSR -> copyOf ? SeriesRules.constructDeepCopyOfSeriesRulesObject(lSR) : lSR).toList();
    }

    /**
     * Return a copy of all SeriesRules supported by this installation. If none, an empty list is returned.
     */
    public List<SeriesRules> getCopyOfAllSeriesRules()
    {
        return getAllSeriesRules(true);
    }

    private List<SeriesRules> getAllSeriesRules(final boolean copyOf)
    {
        return vaccineGroupItemToSeriesRules.values()
                .stream()
                .flatMap(Collection::stream)
                .map(lSR -> (copyOf) ? SeriesRules.constructDeepCopyOfSeriesRulesObject(lSR) : lSR)
                .toList();
    }

    private void addSeriesToVaccineGroup(final String pSeriesCode, final CdsConcept pSeriesCdsConcept,
            final SeriesRules pSeriesRules, final LocallyCodedVaccineGroupItem pVGI)
    {
        final String _METHODNAME = "addSeriesToVaccineGroup(): ";

        // Create the SeriesItem and store it
        final LocallyCodedSeriesItem lcsi;
        try
        {
            lcsi = new LocallyCodedSeriesItem(pSeriesCode, pSeriesCdsConcept, pSeriesRules);
        }
        catch (final IllegalArgumentException iue)
        {
            final String lErrStr =
                    "Caught an unexpected IllegalArgumentException during instantiation of LocallyCodedSeriesItem for series"
                            + pSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }

        // Add the mapping from the String to reference the Series to LocallyCodedSeriesItem
        this.cdsListItemNameToSeriesItem.put(pSeriesCode, lcsi);

        // Add the Series to the list of Series being tracked for each vaccine group START
        List<SeriesRules> lSeriesRulesListForVG = this.vaccineGroupItemToSeriesRules.get(pVGI);
        if (lSeriesRulesListForVG == null)
            lSeriesRulesListForVG = new ArrayList<>();
        lSeriesRulesListForVG.add(pSeriesRules);
        this.vaccineGroupItemToSeriesRules.put(pVGI, lSeriesRulesListForVG);

        // Add the mapping from the String to reference the vaccine group to LocallyCodedVaccineGroupItem
        this.cdsListItemNameToSeriesItem.put(pSeriesCode, lcsi);
    }

    private List<Season> validateAndGetSeasons(final String pSeriesCode,
            final Collection<org.cdsframework.ice.supportingdata.Season> pSeasons, final String pMethodName)
            throws InconsistentConfigurationException
    {
        final List<Season> lSeasons = new ArrayList<>();
        if (pSeasons != null)
        {
            for (final org.cdsframework.ice.supportingdata.Season s : pSeasons)
            {
                final CD lInternalSeasonCD = ConceptUtils.toInternalCD(s);
                final LocallyCodedCdsListItem locallyCodedCdsSeasonListItem =
                        this.supportedSeasons.getSupportedCdsLists().getCdsListItem(lInternalSeasonCD);

                if (locallyCodedCdsSeasonListItem == null)
                {
                    final String lErrStr =
                            "Season \"" + s.code() + "\" specified for the Series " + pSeriesCode + " does not exist";
                    log.error("{}{}", pMethodName, lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                final LocallyCodedSeasonItem lcsi =
                        this.supportedSeasons.getSeasonItem(locallyCodedCdsSeasonListItem.getCdsListItemName());
                lSeasons.add(lcsi.getSeason());
            }
        }
        return lSeasons;
    }

    private SeriesRules createSeriesRules(final String pSeriesCode, final LocallyCodedVaccineGroupItem pVGI,
            final List<Season> pSeasons)
    {
        return (pSeasons.isEmpty())
               ? new SeriesRules(pSeriesCode, pVGI.getCdsConcept())
               : new SeriesRules(pSeriesCode, pVGI.getCdsConcept(), pSeasons);
    }

    private LocallyCodedVaccineGroupItem getLocallyCodedVaccineGroupItem(final VaccineGroup pVaccineGroup)
            throws InconsistentConfigurationException
    {
        final String _METHODNAME = "getLocallyCodedVaccineGroupItem(): ";
        final CD lVaccineGroupCD = ConceptUtils.toInternalCD(pVaccineGroup);
        final LocallyCodedVaccineGroupItem lVGI = this.supportedVaccineGroups.getVaccineGroupItem(lVaccineGroupCD);
        if (lVGI == null)
        {
            final String lErrStr =
                    "Vaccine group specified for series supporting data file not a previously specified Vaccine Group item: "
                            + lVaccineGroupCD;
            log.error("{}{}", _METHODNAME, lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }
        return lVGI;
    }

    private DoseRule createDoseRule(final int pDoseNumber, final SeriesRules pSeriesRules, final List<Vaccine> pPreferredVaccines,
            final List<Vaccine> pAllowableVaccines, final Map<VaccineComponent, TimePeriod> pAllowableVaccineMinimumAges,
            final Map<VaccineComponent, TimePeriod> pAllowableVaccineMaximumAges, final String pAbsoluteMinimumAge,
            final String pMinimumAge, final String pEarliestRecommendedAge, final String pMaximumAge,
            final String pLatestRecommendedAge, final String pAbsoluteMinimumInterval, final String pMinimumInterval,
            final String pEarliestRecommendedInterval, final String pLatestRecommendedInterval)
    {
        final DoseRule dr = new DoseRule(pSeriesRules);
        dr.setDoseNumber(pDoseNumber);
        dr.setPreferableVaccines(pPreferredVaccines);
        dr.setAllowableVaccines(pAllowableVaccines);
        if (pAbsoluteMinimumAge != null)
            dr.setAbsoluteMinimumAge(new TimePeriod(pAbsoluteMinimumAge));
        if (pMinimumAge != null)
            dr.setMinimumAge(new TimePeriod(pMinimumAge));
        if (pEarliestRecommendedAge != null)
            dr.setEarliestRecommendedAge(new TimePeriod(pEarliestRecommendedAge));
        if (pMaximumAge != null)
            dr.setAbsoluteMaximumAge(new TimePeriod(pMaximumAge));
        if (pLatestRecommendedAge != null)
            dr.setLatestRecommendedAge(new TimePeriod(pLatestRecommendedAge));
        if (pAbsoluteMinimumInterval != null)
            dr.setAbsoluteMinimumInterval(new TimePeriod(pAbsoluteMinimumInterval));
        if (pMinimumInterval != null)
            dr.setMinimumInterval(new TimePeriod(pMinimumInterval));
        if (pEarliestRecommendedInterval != null)
            dr.setEarliestRecommendedInterval(new TimePeriod(pEarliestRecommendedInterval));
        if (pLatestRecommendedInterval != null)
            dr.setLatestRecommendedInterval(new TimePeriod(pLatestRecommendedInterval));
        if (!pAllowableVaccineMinimumAges.isEmpty())
            dr.setAllowableMinimumAgesForVaccines(pAllowableVaccineMinimumAges);
        if (!pAllowableVaccineMaximumAges.isEmpty())
            dr.setAllowableMaximumAgesForVaccines(pAllowableVaccineMaximumAges);
        return dr;
    }

    /**
     * Add the Series specified in the iceSupportingProperties.Series instance to the supporting data tracked by this class.
     *
     * @param pSeriesData The Series instance to add
     * @throws InconsistentConfigurationException If data supplied in the properties is inconsistent in some way
     * @throws IllegalArgumentException           If any data supplied in the properties is not permitted
     */
    public void addSupportedSeriesItemFromProperties(final SeriesData pSeriesData)
            throws InconsistentConfigurationException, IllegalArgumentException
    {
        final String _METHODNAME = "addSupportedSeriesItemFromProperties(): ";

        if (pSeriesData == null)
        {
            log.warn(_METHODNAME + "Series parameters is null; cannot process and returning");
            return;
        }

        // Determine the series name. No duplicates allowed; no series that weren't previously defined LocallyCodedCdsListItem allowed.
        final Series lPropSeries = pSeriesData.series();

        final LocallyCodedCdsListItem locallyCodedCdsListItem =
                this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(lPropSeries));

        // Now verify that there is a CdsListItem for this series (i.e. - we are tracking the codes and code systems in SupportedCdsLists - it must be there too).
        if (locallyCodedCdsListItem == null)
        {
            final String lErrStr = "Attempt to add the following series which is not in the list of SupportedCdsLists: "
                    + ConceptUtils.toInternalCD(lPropSeries);
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        if (ICEConceptType.SERIES != ICEConceptType.getSupportedIceConceptType(locallyCodedCdsListItem.getCdsListCode()))
        {
            final String lErrStr = "Attempt to add an item as a series which is not a SERIES ICEConceptType";
            log.warn(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        final String lSeriesCdsListItemName = locallyCodedCdsListItem.getCdsListItemName();
        if (this.cdsListItemNameToSeriesItem.containsKey(lSeriesCdsListItemName))
        {
            final String lErrStr =
                    "Attempt to add series that was already specified previously: " + ConceptUtils.toInternalCD(lPropSeries);
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // CdsListItem is a CdsConcept of type series? - check to make sure that the specified series CdsConcept has been specified with this series' cdsListItem definition
        final CdsConcept lPrimaryOpenCdsConcept =
                new CdsConcept(lSeriesCdsListItemName, locallyCodedCdsListItem.getCdsListItemValue());
        if (!locallyCodedCdsListItem.equals(this.supportedCdsLists.getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.SERIES, lPrimaryOpenCdsConcept)))
        {
            final String lErrStr =
                    "Attempt to add series with a Primary CdsConcept that is not associated with the series; series: "
                            + lSeriesCdsListItemName + "; Primary CdsConcept: " + lPrimaryOpenCdsConcept;
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Get the associated Vaccine Group for this series.
        if (ObjectUtils.isEmpty(pSeriesData.vaccineGroup()))
        {
            final String lErrStr = "Vaccine group not specified in properties. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        final String lSeriesCode = locallyCodedCdsListItem.getCdsListItemName();
        if (pSeriesData.vaccineGroup().size() > 1)
        {
            final String lErrStr = "More than one vaccine group specified for series " + lSeriesCode
                    + " in supporting data file. Currently, only one vaccine group per series is supported";
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        final VaccineGroup lPropVaccineGroup = pSeriesData.vaccineGroup().values().iterator().next();

        final LocallyCodedVaccineGroupItem lVGI = getLocallyCodedVaccineGroupItem(lPropVaccineGroup);

        // At least one dose must be specified and the number of doses must match the Dose elements
        final Map<String, Dose> lDosesMap = pSeriesData.doses();
        if (ObjectUtils.isEmpty(lDosesMap))
        {
            final String lErrStr = "No series doses have been specified for the series. Series: " + lSeriesCode;
            log.error(lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // ... and the number of doses must be specified and match what is provided
        final Integer lNumberOfDosesInSeries = pSeriesData.numberOfDosesInSeries();
        if (lNumberOfDosesInSeries == null)
        {
            final String lErrStr = "Number of doses in series not specified. Series: " + lSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        if (lNumberOfDosesInSeries != lDosesMap.size())
        {
            final String lErrStr =
                    "Number of doses specified for the Series does not match the number of Dose elements; cannot continue. Series "
                            + lSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Gather the Seasons from this supporting data
        final List<Season> lSeasons =
                validateAndGetSeasons(lSeriesCode, Optional.ofNullable(pSeriesData.seasons()).map(Map::values).orElse(null),
                        _METHODNAME);

        // Create the SeriesRules object
        final SeriesRules series1Rules = createSeriesRules(lSeriesCode, lVGI, lSeasons);
        series1Rules.setNumberOfDosesInSeries(pSeriesData.numberOfDosesInSeries());
        if (pSeriesData.seriesGroup() != null)
            series1Rules.setSeriesGroup(pSeriesData.seriesGroup());
        if (pSeriesData.patientStartAge() != null)
            series1Rules.setSeriesStartAge(new TimePeriod(pSeriesData.patientStartAge()));
        if (pSeriesData.patientEndAge() != null)
            series1Rules.setSeriesEndAge(new TimePeriod(pSeriesData.patientEndAge()));

        series1Rules.setSeriesDoseRules(getDoseRules(pSeriesData, lDosesMap, lSeriesCode, series1Rules));
        series1Rules.setRecurringDosesAfterSeriesComplete(
                Optional.ofNullable(pSeriesData.recurringDosesAfterSeriesComplete()).orElse(false));
        series1Rules.setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(
                Optional.ofNullable(pSeriesData.doseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered()).orElse(true));

        addSeriesToVaccineGroup(lSeriesCode, lPrimaryOpenCdsConcept, series1Rules, lVGI);
    }

    private @NonNull List<DoseRule> getDoseRules(final SeriesData pSeriesData, final Map<String, Dose> lDosesMap,
            final String lSeriesCode, final SeriesRules series1Rules)
    {
        final String _METHODNAME = "getDoseRules(): ";
        // Add each dose.
        final List<DoseRule> seriesDoseRules = new ArrayList<>();
        final List<Dose> lDosesList = lDosesMap.entrySet()
                .stream()
                .map(e -> e.getValue().copyWithDoseNumber(Integer.valueOf(e.getKey())))
                .sorted(Comparator.comparingInt(Dose::doseNumber))
                .toList();

        int lExpectedDoseNumber = 1;
        for (final Dose lDose : lDosesList)
        {
            if (lDose.doseNumber() == null)
            {
                final String lErrStr = "Dose number not specified in properties";
                log.error(_METHODNAME + lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            final int lDoseNumber = lDose.doseNumber();
            if (lDoseNumber != lExpectedDoseNumber)
            {
                final String lErrStr =
                        "Dose number is not a valid dose number for series (1 <= [dose number]<= [number of doses in series]) or is not in *sequential order*. Cannot continue.";
                log.error(_METHODNAME + lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            final Map<String, DoseVaccine> lDoseVaccinesMap = lDose.doseVaccines();
            if (ObjectUtils.isEmpty(lDoseVaccinesMap))
            {
                final String lWarnStr =
                        "No valid vaccines were specified for Series " + lSeriesCode + ", dose number " + lExpectedDoseNumber;
                log.warn(_METHODNAME + "{}", lWarnStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lWarnStr);
            }

            // Obtain the permitted and allowable vaccines for inclusion in the series
            final List<Vaccine> lPreferredDoseVaccines = new ArrayList<>();
            final List<Vaccine> lAllowableDoseVaccines = new ArrayList<>();
            final Map<VaccineComponent, TimePeriod> lAllowableVaccineMinimumAges = new HashMap<>();
            final Map<VaccineComponent, TimePeriod> lAllowableVaccineMaximumAges = new HashMap<>();

            for (final DoseVaccine lDV : lDoseVaccinesMap.values())
            {
                final org.cdsframework.ice.supportingdata.Vaccine lPropVaccine = lDV.vaccine();

                final LocallyCodedVaccineItem lcvi = this.supportedVaccines.getVaccineItem(ConceptUtils.toInternalCD(lPropVaccine));
                if (lcvi == null)
                {
                    final String lErrStr =
                            "A vaccine which was not previously defined was specified for Series " + lSeriesCode + "; dose number "
                                    + lExpectedDoseNumber + "; vaccine: " + lPropVaccine;
                    log.error(_METHODNAME + "{}", lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (lPreferredDoseVaccines.contains(lcvi.getVaccine()) || lAllowableDoseVaccines.contains(lcvi.getVaccine()))
                {
                    final String lErrStr = "A vaccine was specified more than once for Series " + lSeriesCode + ", dose number "
                            + lExpectedDoseNumber + "; vaccine in question: " + lcvi.getVaccine();
                    log.error(_METHODNAME + "{}", lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (Boolean.TRUE.equals(lDV.preferred()))
                    lPreferredDoseVaccines.add(lcvi.getVaccine());
                else
                    lAllowableDoseVaccines.add(lcvi.getVaccine());

                if (lDV.allowableMinimumAgeOfUse() != null)
                {
                    for (final VaccineComponent lVC : lcvi.getVaccine().getVaccineComponents())
                        lAllowableVaccineMinimumAges.put(lVC, new TimePeriod(lDV.allowableMinimumAgeOfUse()));
                }
                if (lDV.allowableMaximumAgeOfUse() != null)
                {
                    for (final VaccineComponent lVC : lcvi.getVaccine().getVaccineComponents())
                        lAllowableVaccineMaximumAges.put(lVC, new TimePeriod(lDV.allowableMaximumAgeOfUse()));
                }
            }

            final String lAbsoluteMinimumAge = lDose.absoluteMinimumAge();
            final String lMinimumAge = lDose.minimumAge();
            final String lEarliestRecommendedAge = lDose.earliestRecommendedAge();
            if (lAbsoluteMinimumAge == null || lMinimumAge == null)
                log.debug(_METHODNAME + "Absolute minimum age and/or minimum age not specified in a dose: {}; Series {}",
                        lDoseNumber, lSeriesCode);

            final String lMaximumAge = lDose.absoluteMaximumAge();
            final String lLatestRecommendedAge = lDose.latestRecommendedAge();

            String lAbsoluteMinimumInterval = null;
            String lMinimumInterval = null;
            String lEarliestRecommendedInterval = null;
            String lLatestRecommendedInterval = null;

            final Map<String, DoseInterval> lDoseIntervalsMap = pSeriesData.doseIntervals();
            if (lDoseIntervalsMap != null)
            {
                boolean thisDoseToNextDoseIntervalFound = false;
                for (final DoseInterval lDI : lDoseIntervalsMap.values())
                {
                    final Integer lFromDoseNumber = lDI.fromDoseNumber();
                    final Integer lToDoseNumber = lDI.toDoseNumber();
                    if (lFromDoseNumber == null || lToDoseNumber == null)
                    {
                        final String lErrStr =
                                "DoseInterval fromDoseNumber or toDoseNumber elements not provided in Series " + lSeriesCode
                                        + "; cannot continue";
                        log.error(_METHODNAME + "{}", lErrStr);
                        this.isSupportingDataConsistent = false;
                        throw new InconsistentConfigurationException(lErrStr);
                    }

                    if (lFromDoseNumber == lExpectedDoseNumber && lToDoseNumber == lFromDoseNumber + 1)
                    {
                        if (thisDoseToNextDoseIntervalFound)
                        {
                            final String lErrStr =
                                    "Encountered more than one interval from dose number " + lFromDoseNumber + " to dose number" + (
                                            lFromDoseNumber + 1) + " for Series " + lSeriesCode;
                            log.error(_METHODNAME + "{}", lErrStr);
                            this.isSupportingDataConsistent = false;
                            throw new InconsistentConfigurationException(lErrStr);
                        }

                        thisDoseToNextDoseIntervalFound = true;
                        lAbsoluteMinimumInterval = lDI.absoluteMinimumInterval();
                        lMinimumInterval = lDI.minimumInterval();
                        lEarliestRecommendedInterval = lDI.earliestRecommendedInterval();
                        lLatestRecommendedInterval = lDI.latestRecommendedInterval();
                    }
                }
            }

            final DoseRule lDR = createDoseRule(lExpectedDoseNumber, series1Rules, lPreferredDoseVaccines, lAllowableDoseVaccines,
                    lAllowableVaccineMinimumAges, lAllowableVaccineMaximumAges, lAbsoluteMinimumAge, lMinimumAge,
                    lEarliestRecommendedAge, lMaximumAge, lLatestRecommendedAge, lAbsoluteMinimumInterval, lMinimumInterval,
                    lEarliestRecommendedInterval, lLatestRecommendedInterval);

            seriesDoseRules.add(lDR);
            lExpectedDoseNumber++;
        }
        return seriesDoseRules;
    }

    /**
     * Check to make sure that all of the seasons in each vaccine group are "consistent". Invokes checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup()
     * for each season. Results must be true for all invocations to to that method, or this method returns false.
     */
    private boolean checkConsistencyOfSeasonsSupportingDataAcrossAllSeriesInAllVaccineGroups()
    {
        return vaccineGroupItemToSeriesRules.keySet()
                .stream()
                .noneMatch(this::checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup);
    }

    /**
     * Check to make sure that all seasons do not overlap with each other, or if they do, they have the exact same season start and end dates.
     * All series in the vaccine group must be seasonal series, or none of them. If some are or others aren't, this method logs a warning and returns false.
     * In addition, there cannot be more than one default series in a vaccine group.
     *
     * @return true of these conditions are met, false if not.
     */
    private boolean checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(final LocallyCodedVaccineGroupItem pcvgi)
    {
        final String _METHODNAME = "checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(): ";
        if (pcvgi == null)
            return false;

        final List<SeriesRules> srs = getSeriesRulesForVaccineGroup(pcvgi);
        if (srs == null)
        {
            // Vaccine group is not supported - although this should not happen - just return true
            return true;
        }

        int countOfDefaultSeasonsAcrossSeries = 0;
        int countOfSeasons = 0;
        boolean aNonSeasonalSeriesExists = false;
        final List<Season> seasonsTracker = new ArrayList<>();
        for (final SeriesRules sr : srs)
        {
            final List<Season> seriesSeasons = sr.getSeasons();
            if (ObjectUtils.isEmpty(seriesSeasons))
            {
                aNonSeasonalSeriesExists = true;
                if (countOfSeasons > 0)
                {
                    log.warn(_METHODNAME + "a non-seasonal series was found in a vaccine group with seasons {}",
                            pcvgi.getCdsItemName());
                    return false;
                }
            }

            for (final Season s : Objects.requireNonNull(seriesSeasons))
            {
                if (aNonSeasonalSeriesExists)
                {
                    log.warn(_METHODNAME + "a non-seasonal series was found in a vaccine group with seasons {}",
                            pcvgi.getCdsItemName());
                    return false;
                }

                boolean lSeasonAlreadyEncountered = false;
                if (seasonsTracker.contains(s))
                    lSeasonAlreadyEncountered = true;
                else
                    countOfSeasons++;
                if (s.isDefaultSeason())
                {
                    countOfDefaultSeasonsAcrossSeries++;
                    if (countOfDefaultSeasonsAcrossSeries >= 2)
                    {
                        log.warn(_METHODNAME + "more than one default season in Series in vaccine group {}",
                                pcvgi.getCdsItemName());
                        return false;
                    }
                }
                else
                    if (!lSeasonAlreadyEncountered)
                    {
                        for (final Season seasonIter : seasonsTracker)
                        {
                            // Check to see if the season start or end dates overlaps with another season. Overlaps are only allowed if the start and end dates
                            // of the season for the different series are exactly the same. Default seasons do not have a specified start or end date, so they are
                            // not checked here. (This is because if a fully-specified season can take place at a time when a default season is specified; it
                            // overrides the default season which will then not be used.)
                            if (!s.seasonsHaveEquivalentStartAndEndDates(seasonIter) && s.seasonOverlapsWith(seasonIter))
                            {
                                log.warn(_METHODNAME + "overlapping seasons exist in vaccine group {}", pcvgi.getCdsItemName());
                                return false;
                            }
                        }
                        seasonsTracker.add(s);
                    }
            }
        }

        final int lNumberOfDistinctSeasons = seasonsTracker.size();
        // This is not a seasonal vaccine group
        if (lNumberOfDistinctSeasons == 0)
            return countOfDefaultSeasonsAcrossSeries == 0;

        if (countOfDefaultSeasonsAcrossSeries != 1 && countOfDefaultSeasonsAcrossSeries != 0)
        {
            log.warn(_METHODNAME
                            + "a seasonal vaccine group must have exactly either 0 or 1 default seasons defined. The # of seasonal series found for vaccine group {}: {}",
                    pcvgi.getCdsItemName(), countOfDefaultSeasonsAcrossSeries);
            return false;
        }

        if (lNumberOfDistinctSeasons > 1 && countOfDefaultSeasonsAcrossSeries == 0)
        {
            log.warn(_METHODNAME
                    + "a seasonal vaccine group wiht more than one season defined must also have a default season defined. No default season has been defined");
            return false;
        }

        // This is a properly configured seasonal vaccine group with a default season for evaluation
        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder ltoStringStr = new StringBuilder();

        // First, print out all of the series name -> series value map entries
        ltoStringStr.append("\ncdsListItemNameToSeriesItem: ");
        final Set<String> cdsListItemNames = this.cdsListItemNameToSeriesItem.keySet();
        int i = 1;
        for (final String s : cdsListItemNames)
            ltoStringStr.append("\n{")
                    .append(i++)
                    .append("} ")
                    .append(s)
                    .append(" = [ ")
                    .append(this.cdsListItemNameToSeriesItem.get(s).toString())
                    .append(" ]\n");

        // Second, print out which series are associated with which vaccine groups
        final Set<LocallyCodedVaccineGroupItem> llcvgs = this.vaccineGroupItemToSeriesRules.keySet();
        i = 1;
        for (final LocallyCodedVaccineGroupItem llcvg : llcvgs)
        {
            final List<SeriesRules> seriesRulesAssociatedWithVG = this.vaccineGroupItemToSeriesRules.get(llcvg);
            if (seriesRulesAssociatedWithVG != null)
            {
                ltoStringStr.append("\n{").append(i).append("} Series Rules for Vaccine Group: ").append(llcvg.getCdsItemName());
                int j = 1;
                for (final SeriesRules sr : seriesRulesAssociatedWithVG)
                    ltoStringStr.append("\n\t(")
                            .append(j++)
                            .append("): LocallyCodedVaccineGroupItem ")
                            .append(llcvg)
                            .append("; SeriesRule ")
                            .append(sr.toString());
            }
            else
                ltoStringStr.append("\n\tNo SeriesRules defined");
            i++;
        }

        // Finally, print out whether or not the series data read in is consistent
        ltoStringStr.append("\n\nisSupportingDataConsistent(): ").append(isSupportingDataConsistent());

        return ltoStringStr.toString();
    }
}
