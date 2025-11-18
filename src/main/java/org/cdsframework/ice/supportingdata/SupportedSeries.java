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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.DoseRule;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.service.SeriesRules;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.service.VaccineComponent;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.TimePeriod;
import org.cdsframework.util.support.data.ice.series.IceDoseIntervalSpecification;
import org.cdsframework.util.support.data.ice.series.IceDoseVaccineSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesDoseSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesSpecificationFile;
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

    /**
     * Return a copy of all SeriesRules supported by this installation. If none, an empty list is returned.
     */
    protected List<SeriesRules> getAllSeriesRules()
    {
        return getAllSeriesRules(false);
    }

    private List<SeriesRules> getAllSeriesRules(final boolean copyOf)
    {
        return vaccineGroupItemToSeriesRules.values()
                .stream()
                .flatMap(Collection::stream)
                .map(lSR -> (copyOf) ? SeriesRules.constructDeepCopyOfSeriesRulesObject(lSR) : lSR)
                .toList();
    }

    /**
     * Add the Series specified in the IceSeriesSpecificationFile to the supporting data tracked by this class.
     *
     * @throws InconsistentConfigurationException If data supplied in the supporting data file is inconsistent in some way
     * @throws IllegalArgumentException           If any data supplied in the supporting data file is not permitted
     */
    public void addSupportedSeriesItemFromIceSeriesSpecificationFile(final IceSeriesSpecificationFile pIceSeriesSpecificationFile)
            throws InconsistentConfigurationException, IllegalArgumentException
    {
        final String _METHODNAME = "addSupportedSeriesItemFromIceSeriesSpecificationFile(): ";

        if (pIceSeriesSpecificationFile == null)
        {
            log.warn(_METHODNAME + "IceSeriesSpecificationFile parameter is null; cannot process and returning");
            return;
        }

        ///////
        // CDS Version validation checks
        ///////
        // Validation Check: If no cdsVersions are specified, thrown an error
        if (ObjectUtils.isEmpty(pIceSeriesSpecificationFile.getCdsVersions()))
        {
            final String lErrStr = "No cdsVersion(s) specified in series supporting data file.";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // If adding a code that is not one of the supported cdsVersions, then return
        final Collection<String> lCdsVersions =
                CollectionUtils.intersectionOfCollections(pIceSeriesSpecificationFile.getCdsVersions(),
                        this.supportedCdsLists.getCdsVersions());
        if (ObjectUtils.isEmpty(lCdsVersions))
            return;

        ///////
        // Series code must be specified and not previously specified (unique)
        ///////
        String lSeriesCode = pIceSeriesSpecificationFile.getCode();
        if (ObjectUtils.isEmpty(lSeriesCode))
        {
            final String lErrStr = "Series code not specified in supporting data file. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        lSeriesCode = ConceptUtils.modifyAttributeNameToConformToRequiredNamingConvention(lSeriesCode);

        if (this.cdsListItemNameToSeriesItem.containsKey(lSeriesCode))
        {
            final String lErrStr = "Attempt to add a Series that was already specified previously; series code:  " + lSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        ///////
        // Get the associated Vaccine Group for this series. (Must specify one and it must have been previously specified LocallyCodedVaccineGroupItem)
        ///////
        final Collection<org.opencds.vmr.v1_0.schema.CD> lVaccineGroups = pIceSeriesSpecificationFile.getVaccineGroups();
        if (ObjectUtils.isEmpty(lVaccineGroups))
        {
            final String lErrStr = "Vaccine groups not specified in supporting data file. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        if (lVaccineGroups.size() > 1)
        {
            final String lErrStr = "More than once vaccine group specified for series " + lSeriesCode
                    + " in supporting data file. Currently, only one vaccine group per series is supported";
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        final CD lVaccineGroupCD = ConceptUtils.toInternalCD(lVaccineGroups.iterator().next());
        final LocallyCodedVaccineGroupItem lVGI = this.supportedVaccineGroups.getVaccineGroupItem(lVaccineGroupCD);
        if (lVGI == null)
        {
            final String lErrStr =
                    "Vaccine group specified for series supporting data file not a previously specified Vaccine Group item: "
                            + lVaccineGroupCD;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        ///////
        // At least one dose must be specified and the number of doses must match the IceSeriesDoseSpecification elements
        ///////
        final Collection<IceSeriesDoseSpecification> isdss = pIceSeriesSpecificationFile.getIceSeriesDoses();
        if (ObjectUtils.isEmpty(isdss))
        {
            final String lErrStr = "No series doses have been specified for the series. Series: " + lSeriesCode;
            log.error(lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // ... and the number of doses must be specified and match what is provided
        final BigInteger bi = pIceSeriesSpecificationFile.getNumberOfDosesInSeries();
        if (bi == null)
        {
            final String lErrStr = "Number of doses in series not specified. Series: " + lSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        if (bi.intValue() != isdss.size())
        {
            final String lErrStr =
                    "Number of doses specified for the Series does not match the number of IceSeriesDoseSpecification elements; cannot continue. Series "
                            + lSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        ///////
        // Gather the Seasons from this supporting data
        ///////
        // Plus, if any Seasons are specified, verify that they are seasons that have been previously specified
        final Collection<String> lSeasonCodesFromIceSeriesSpecificationFile = pIceSeriesSpecificationFile.getSeasonCodes();
        final List<Season> lSeasons = new ArrayList<>();
        if (lSeasonCodesFromIceSeriesSpecificationFile != null)
        {
            for (final String s : lSeasonCodesFromIceSeriesSpecificationFile)
            {
                final LocallyCodedSeasonItem lcsi = this.supportedSeasons.getSeasonItem(s);
                if (lcsi == null || lcsi.getSeason() == null)
                {
                    final String lErrStr = "Season \"" + s + "\" specified for the Series " + lSeriesCode + " does not exist";
                    log.error(_METHODNAME + "{}", lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                lSeasons.add(lcsi.getSeason());
            }
        }

        ///////
        // Create the SeriesRules object
        ///////
        /////// SeriesRules series1Rules = (lSeasons.isEmpty()) ? new SeriesRules(lSeriesCode, lVGI.getCdsItemName()) : new SeriesRules(lSeriesCode, lVGI.getCdsItemName(), lSeasons);
        final SeriesRules series1Rules = (lSeasons.isEmpty())
                                         ? new SeriesRules(lSeriesCode, lVGI.getCdsConcept())
                                         : new SeriesRules(lSeriesCode, lVGI.getCdsConcept(), lSeasons);

        ////////////// Gather the DoseRules START //////////////

        ///////
        // Add each dose. Check that each TimePeriod specified is a valid time period, and every vaccine specified is a vaccine that has been previously defined.
        ///////
        final List<DoseRule> seriesDoseRules = new ArrayList<>();
        int icseSeriesDoseSpecificationNumber = 1;
        for (final IceSeriesDoseSpecification isds : isdss)
        {
            ///////
            // Basic validation checks
            ///////
            if (isds == null)
            {
                final String lErrStr = "Encountered a null IceSeriesDoseSpecification; this should not happen. Cannot continue.";
                log.error(_METHODNAME + lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }

            if (isds.getDoseNumber() == null)
            {
                final String lErrStr = "Dose number not specified in IceSeriesDoseSpecification file";
                log.error(_METHODNAME + lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            final int lDoseNumber = isds.getDoseNumber().intValue();
            if (lDoseNumber != icseSeriesDoseSpecificationNumber)
            {
                final String lErrStr =
                        "Dose number is not a valid dose number for series (1 <= [dose number]<= [number of doses in series]) or is not in *sequential order*. Cannot continue.";
                log.error(_METHODNAME + lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            final Collection<IceDoseVaccineSpecification> lIDVSS = isds.getDoseVaccines();
            if (ObjectUtils.isEmpty(lIDVSS))
            {
                final String lWarnStr = "No valid vaccines were specified for Series " + lSeriesCode + ", dose number "
                        + icseSeriesDoseSpecificationNumber;
                log.warn(_METHODNAME + "{}", lWarnStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lWarnStr);
            }
            ///////
            // Obtain the permitted and allowable vaccines for inclusion in the series
            ///////
            final List<Vaccine> lPreferredDoseVaccines = new ArrayList<>();
            final List<Vaccine> lAllowableDoseVaccines = new ArrayList<>();
            final Map<VaccineComponent, TimePeriod> lAllowableVaccineMinimumAges = new HashMap<>();
            final Map<VaccineComponent, TimePeriod> lAllowableVaccineMaximumAges = new HashMap<>();
            for (final IceDoseVaccineSpecification lIDVS : lIDVSS)
            {
                if (lIDVS == null)
                {
                    final String lErrStr = "Encountered a null IceDoseVaccineSpecification in Series " + lSeriesCode
                            + "; this should not happen. Not continuing.";
                    log.error(_METHODNAME + "{}", lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                final org.opencds.vmr.v1_0.schema.CD lVaccineCD = lIDVS.getVaccine();
                final LocallyCodedVaccineItem lcvi = this.supportedVaccines.getVaccineItem(ConceptUtils.toInternalCD(lVaccineCD));
                if (lcvi == null)
                {
                    final String lErrStr =
                            "A vaccine which was not previously defined was specified for Series " + lSeriesCode + "; dose number "
                                    + icseSeriesDoseSpecificationNumber + "; vaccine: " + ConceptUtils.toStringCD(lVaccineCD);
                    log.error(_METHODNAME + "{}", lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (lPreferredDoseVaccines.contains(lcvi.getVaccine()) || lAllowableDoseVaccines.contains(lcvi.getVaccine()))
                {
                    final String lErrStr = "A vaccine was specified more than once for Series " + lSeriesCode + ", dose number "
                            + icseSeriesDoseSpecificationNumber + "; vaccine in question: " + lcvi.getVaccine();
                    log.error(_METHODNAME + "{}", lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (lIDVS.isPreferred())
                    lPreferredDoseVaccines.add(lcvi.getVaccine());
                else
                    lAllowableDoseVaccines.add(lcvi.getVaccine());

                // Obtain the allowable minimum and maximum ages for the vaccine in the series, if any
                if (!lcvi.getVaccine().isCombinationVaccine())
                {
                    final boolean lMinimumVaccineAgeDoseRuleSpecified = lIDVS.getAllowableMinimumAgeOfUse() != null;
                    final boolean lMaximumVaccineAgeDoseRuleSpecified = lIDVS.getAllowableMaximumAgeOfUse() != null;
                    if (log.isDebugEnabled())
                    {
                        log.debug(_METHODNAME
                                        + "minimum for series {}; dose number: {} vaccine: {}; DoseRule allowable minimum age: {}",
                                pIceSeriesSpecificationFile.getName(), isds.getDoseNumber(), lcvi.getVaccine(),
                                lIDVS.getAllowableMinimumAgeOfUse());
                        log.debug(_METHODNAME
                                        + "maximum for series {}; dose number: {} vaccine: {}; DoseRule allowable maximum age: {}",
                                pIceSeriesSpecificationFile.getName(), isds.getDoseNumber(), lcvi.getVaccine(),
                                lIDVS.getAllowableMaximumAgeOfUse());
                    }

                    if (lMinimumVaccineAgeDoseRuleSpecified || lMaximumVaccineAgeDoseRuleSpecified)
                    {
                        final VaccineComponent lVaccineComponentForVaccine =
                                this.supportedVaccines.getVaccineComponent(ConceptUtils.toInternalCD(lVaccineCD));
                        if (lVaccineComponentForVaccine == null)
                        {
                            // This should not happen due to validation in SupportedVaccines, nonetheless...
                            final String lErrStr =
                                    "Encountered a Vaccine with no associated VaccineComponent in Series " + lSeriesCode
                                            + "; this should not happen. Cannot continue.";
                            log.error(_METHODNAME + "{}", lErrStr);
                            this.isSupportingDataConsistent = false;
                            throw new InconsistentConfigurationException(lErrStr);
                        }

                        if (lMinimumVaccineAgeDoseRuleSpecified)
                        {
                            lAllowableVaccineMinimumAges.put(lVaccineComponentForVaccine,
                                    new TimePeriod(lIDVS.getAllowableMinimumAgeOfUse()));
                        }

                        if (lMaximumVaccineAgeDoseRuleSpecified)
                        {
                            lAllowableVaccineMaximumAges.put(lVaccineComponentForVaccine,
                                    new TimePeriod(lIDVS.getAllowableMaximumAgeOfUse()));
                        }
                    }
                }
            }
            ///////
            // Obtain the allowable minimum and maximum ages for the vaccine in the series, if any
            ///////

            ///////
            // Get absolute minimum age, minimum age, maximum age, earliest recommended age, latest recommended age, absolute minimum interval, minimum interval,
            // earliest recommended interval, latest recommended interval...
            ///////
            final String absoluteMinimumAge = isds.getAbsoluteMinimumAge();
            final String minimumAge = isds.getMinimumAge();
            final String earliestRecommendedAge = isds.getEarliestRecommendedAge();
            // absolute minimum age, minimum age and earliest recommended age are mandatory
            if (absoluteMinimumAge == null || minimumAge == null)
                log.warn(_METHODNAME + "Absolute minimum age and/or minimum age not specified in a dose: {}; Series {}",
                        lDoseNumber, lSeriesCode);

            final String maximumAge = isds.getAbsoluteMaximumAge();
            final String latestRecommendedAge = isds.getLatestRecommendedAge();
            String absoluteMinimumInterval = null;
            String minimumInterval = null;
            String earliestRecommendedInterval = null;
            String latestRecommendedInterval = null;
            final List<IceDoseIntervalSpecification> idiss = pIceSeriesSpecificationFile.getDoseIntervals();
            if (idiss != null)
            {
                ///////
                // Cycle through the intervals (IceDoseIntervalSpecifications) from this dose to the next (doseNumber+1) dose
                boolean thisDoseToNextDoseIntervalFound = false;
                for (final IceDoseIntervalSpecification idis : idiss)
                {
                    if (idis == null)
                    {
                        final String lErrStr = "Encountered a null IceDoseIntervalSpecification in Series " + lSeriesCode
                                + "; this should not happen. Not continuing.";
                        log.error(_METHODNAME + "{}", lErrStr);
                        this.isSupportingDataConsistent = false;
                        throw new InconsistentConfigurationException(lErrStr);
                    }

                    final BigInteger fromDoseNumberBI = idis.getFromDoseNumber();
                    final BigInteger toDoseNumberBI = idis.getToDoseNumber();
                    if (fromDoseNumberBI == null || toDoseNumberBI == null)
                    {
                        final String lErrStr =
                                "IceDoseIntervalSpecification fromDoseNumber or toDoseNumber elements not provided in Series "
                                        + lSeriesCode + "; cannot continue";
                        log.error(_METHODNAME + "{}", lErrStr);
                        this.isSupportingDataConsistent = false;
                        throw new InconsistentConfigurationException(lErrStr);
                    }

                    final int fromDoseNumber = fromDoseNumberBI.intValue();
                    final int toDoseNumber = toDoseNumberBI.intValue();
                    if (fromDoseNumber == icseSeriesDoseSpecificationNumber && toDoseNumber == fromDoseNumber + 1)
                    {
                        if (thisDoseToNextDoseIntervalFound)
                        {
                            final String lErrStr =
                                    "Encountered more than one interval from dose number " + fromDoseNumber + " to dose number"
                                            + fromDoseNumber + 1 + " for Series " + lSeriesCode;
                            log.error(_METHODNAME + "{}", lErrStr);
                            this.isSupportingDataConsistent = false;
                            throw new InconsistentConfigurationException(lErrStr);
                        }

                        thisDoseToNextDoseIntervalFound = true;
                        // Interval from this dose to the next (doseNumber+1) dose
                        absoluteMinimumInterval = idis.getAbsoluteMinimumInterval();
                        minimumInterval = idis.getMinimumInterval();
                        earliestRecommendedInterval = idis.getEarliestRecommendedInterval();
                        latestRecommendedInterval = idis.getLatestRecommendedInterval();
                    }
                    else
                        if (fromDoseNumber == icseSeriesDoseSpecificationNumber && toDoseNumber != fromDoseNumber + 1)
                            log.warn(_METHODNAME
                                            + "Warning: skipping interval from dose number {} to non-consecutive dose number {}found for Series {}; only consecutive intervals currently supported",
                                    fromDoseNumber, fromDoseNumber + 1, lSeriesCode);
                }
            }

            ///////
            // Create the SeriesRules and DoseRule and add it to the list of Doses for this Series
            ///////

            final DoseRule dr = new DoseRule(series1Rules);
            // Mandatory
            dr.setDoseNumber(icseSeriesDoseSpecificationNumber);
            dr.setPreferableVaccines(lPreferredDoseVaccines);
            dr.setAllowableVaccines(lAllowableDoseVaccines);
            if (absoluteMinimumAge != null)
                dr.setAbsoluteMinimumAge(new TimePeriod(absoluteMinimumAge));
            if (minimumAge != null)
                dr.setMinimumAge(new TimePeriod(minimumAge));
            if (earliestRecommendedAge != null)
                dr.setEarliestRecommendedAge(new TimePeriod(earliestRecommendedAge));
            if (maximumAge != null)
            {
                // Only if specified
                dr.setAbsoluteMaximumAge(new TimePeriod(maximumAge));
            }
            if (latestRecommendedAge != null)
            {
                // Only if specified
                dr.setLatestRecommendedAge(new TimePeriod(latestRecommendedAge));
            }
            if (absoluteMinimumInterval != null)
            {
                // Only if specified
                dr.setAbsoluteMinimumInterval(new TimePeriod(absoluteMinimumInterval));
            }
            if (minimumInterval != null)
            {
                // Only if specified
                dr.setMinimumInterval(new TimePeriod(minimumInterval));
            }
            if (earliestRecommendedInterval != null)
            {
                // Only if specified
                dr.setEarliestRecommendedInterval(new TimePeriod(earliestRecommendedInterval));
            }
            if (latestRecommendedInterval != null)
            {
                // Only if specified
                dr.setLatestRecommendedInterval(new TimePeriod(latestRecommendedInterval));
            }
            if (!lAllowableVaccineMinimumAges.isEmpty())
                dr.setAllowableMinimumAgesForVaccines(lAllowableVaccineMinimumAges);
            if (!lAllowableVaccineMaximumAges.isEmpty())
                dr.setAllowableMaximumAgesForVaccines(lAllowableVaccineMaximumAges);

            // Add the DoseRule to the list of DoseRules for this series
            seriesDoseRules.add(dr);
            icseSeriesDoseSpecificationNumber++;
        }

        ////////////// Gather the DoseRules END //////////////

        // Series Group Info
        if (pIceSeriesSpecificationFile.getSeriesGroup() != null)
            series1Rules.setSeriesGroup(pIceSeriesSpecificationFile.getSeriesGroup().intValue());

        ///////
        // Gather patient age information and the associated start/end ages for the series (if any), and add to the SeriesRules object
        ///////
        if (pIceSeriesSpecificationFile.getPatientStartAge() != null)
            series1Rules.setSeriesStartAge(new TimePeriod(pIceSeriesSpecificationFile.getPatientStartAge()));
        if (pIceSeriesSpecificationFile.getPatientEndAge() != null)
            series1Rules.setSeriesEndAge(new TimePeriod(pIceSeriesSpecificationFile.getPatientEndAge()));

        ///////
        // Add the DoseRule information to the SeriesRules object
        ///////
        series1Rules.setSeriesDoseRules(seriesDoseRules);
        // Determine whether or not there are recurring doses for this series (**default false if not specified**)
        if (pIceSeriesSpecificationFile.isRecurringDosesAfterSeriesComplete() != null)
            series1Rules.setRecurringDosesAfterSeriesComplete(pIceSeriesSpecificationFile.isRecurringDosesAfterSeriesComplete());
        else
        {
            // If not specified, assume there are no recurring doses of some kind after the series has been completed
            series1Rules.setRecurringDosesAfterSeriesComplete(false);
        }
        // Determine if the dose number should be calculated based on the targeted diseases of each vaccine administered (**default true if not specified**)
        if (pIceSeriesSpecificationFile.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered() != null)
        {
            series1Rules.setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(
                    pIceSeriesSpecificationFile.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered());
        }
        else
            series1Rules.setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(true);

        ///////
        // Create the SeriesItem and store it
        ///////
        final LocallyCodedSeriesItem lcsi;
        try
        {
            lcsi = new LocallyCodedSeriesItem(lSeriesCode, lCdsVersions, series1Rules);
        }
        catch (final IllegalArgumentException iue)
        {
            final String lErrStr =
                    "Caught an unexpected IllegalArgumentException during instantiation of LocallyCodedSeriesItem for series"
                            + lSeriesCode;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }

        // Add the mapping from the String to reference the Series to LocallyCodedSeriesItem
        this.cdsListItemNameToSeriesItem.put(lSeriesCode, lcsi);

        ///////
        // Add the Series to the list of Series being tracked for each vaccine group START
        ///////
        List<SeriesRules> lSeriesRulesListForVG = this.vaccineGroupItemToSeriesRules.get(lVGI);
        if (lSeriesRulesListForVG == null)
            lSeriesRulesListForVG = new ArrayList<>();
        lSeriesRulesListForVG.add(series1Rules);
        this.vaccineGroupItemToSeriesRules.put(lVGI, lSeriesRulesListForVG);
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
