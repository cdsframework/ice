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

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.supportingdata.ICESupportingDataConfiguration;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineGroupItem;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineItem;
import org.cdsframework.ice.supportingdata.SupportedVaccineGroups;
import org.cdsframework.ice.supportingdata.SupportedVaccines;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class Schedule
{
    private final ICESupportingDataConfiguration iceSupportingDataConfiguration;
    private String scheduleId;
    private boolean scheduleHasBeenInitialized;
    private IceProperties.SupplementalTextMode supplementalTextMode;

    /**
     * Initialize the Immunization Schedule. Throws an IllegalArgumentException if any data (including supporting data) is improperly specified. Throws an
     * InconsistentConfigurationException if the supporting data is "inconsistent" in some manner
     *
     * @param pScheduleId                  The ID of the schedule
     * @param pKnowledgeModules            The CDS versions supported by this schedule
     * @param pKnowledgeRepositoryLocation the knowledge base directory location; where all of the knowledge modules are
     */
    public Schedule(final String pScheduleId, final String pCommonLogicModule, final Path pCommonLogicModuleLocation,
            final List<String> pKnowledgeModules, final Path pKnowledgeRepositoryLocation,
            final IceProperties.SupplementalTextMode supplementalTextMode)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "ScheduleImpl(): ";

        this.scheduleHasBeenInitialized = false;
        if (pScheduleId == null || pCommonLogicModule == null || pCommonLogicModuleLocation == null
                || pKnowledgeRepositoryLocation == null || ObjectUtils.isEmpty(pKnowledgeModules))
        {
            final String lErrStr = "Schedule not properly initialized: one or more parameters null";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.scheduleId = pScheduleId;

        // Initialize the supporting data for the common logic and knowledge modules specified and available
        this.iceSupportingDataConfiguration =
                new ICESupportingDataConfiguration(pCommonLogicModule, pCommonLogicModuleLocation, pKnowledgeModules,
                        pKnowledgeRepositoryLocation);

        this.supplementalTextMode = supplementalTextMode;

        // Log initialization of Schedule
        log.debug(_METHODNAME + "Completed Initialization of Schedule: {}", this.scheduleId);
        this.scheduleHasBeenInitialized = true;
    }

    /**
     * Get SupportedCdsConcepts associated with this schedule
     */
    public SupportedCdsConcepts getSupportedCdsConcepts()
    {
        return this.iceSupportingDataConfiguration.getSupportedCdsConcepts();
    }

    /**
     * Get SupportedCdsLists associated with this schedule
     */
    public SupportedCdsLists getSupportedCdsLists()
    {
        return this.iceSupportingDataConfiguration.getSupportedCdsLists();
    }

    /**
     * Get SupportedVaccineGroups associated with this schedule
     */
    public SupportedVaccineGroups getSupportedVaccineGroups()
    {
        return this.iceSupportingDataConfiguration.getSupportedVaccineGroups();
    }

    /**
     * Get SupportedVaccines associated with this schedule
     */
    public SupportedVaccines getSupportedVaccines()
    {
        return this.iceSupportingDataConfiguration.getSupportedVaccines();
    }

    public boolean isScheduleInitialized()
    {
        return this.scheduleHasBeenInitialized;
    }

    public ICESupportingDataConfiguration getICESupportingDataConfiguration()
    {
        return this.iceSupportingDataConfiguration;
    }

    /**
     * Get SeriesRules based on vaccine group and series name. Returns the SeriesRules representing the specified series by name, or null if not found
     */
    public SeriesRules getScheduleSeriesByName(final String svg, final String seriesName)
    {
        if (svg == null || seriesName == null)
            return null;

        final LocallyCodedVaccineGroupItem lcvgi =
                this.iceSupportingDataConfiguration.getSupportedVaccineGroups().getVaccineGroupItem(svg);
        if (lcvgi == null)
            return null;

        final List<SeriesRules> lSRs =
                this.iceSupportingDataConfiguration.getSupportedSeries().getCopyOfSeriesRulesForVaccineGroup(lcvgi);
        if (ObjectUtils.isEmpty(lSRs))
            return null;

        return lSRs.stream().filter(sr -> seriesName.equals(sr.getSeriesName())).findFirst().orElse(null);
    }

    /**
     * Utilizing supporting data. Return the Vaccine associated with its OpenCDS concept code value
     *
     * @return Vaccine, or null if there is no associated Vaccine for the OpenCDS concept code provided
     */
    public Vaccine getVaccineByCdsConceptValue(final String openCdsConceptValue)
    {
        if (openCdsConceptValue == null)
            return null;

        final LocallyCodedCdsListItem lVaccineCdsItem = this.iceSupportingDataConfiguration.getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS,
                        new CdsConcept(openCdsConceptValue));
        if (lVaccineCdsItem == null)
            return null;

        // Supporting data restrictions ensure all of the values are non-null
        final LocallyCodedVaccineItem lcvi =
                this.iceSupportingDataConfiguration.getSupportedVaccines().getVaccineItem(lVaccineCdsItem.getCdsListItemName());
        if (lcvi == null)
            return null;

        return lcvi.getVaccine();
    }

    /**
     * Utilizing supporting data. Obtain the list of diseases targeted by the specified vaccine group. Both the String supplied as the parameter and String returned
     * are compliant to LocallyCodedCdsListItem.getSupportedListConceptItemName().
     *
     * @return Collection of Strings representing the diseases targeted by the vaccine group; empty collection if none. If the specified vaccine group is
     * either null or not a vaccine group tracked in the supporting data, null is returned.
     */
    public Collection<String> getDiseasesTargetedByVaccineGroup(final String pVaccineGroupConceptName)
    {
        if (pVaccineGroupConceptName == null)
            return null;

        return getDiseasesTargetedByVaccineGroup(new CdsConcept(pVaccineGroupConceptName));
    }

    /**
     * Utilizing supporting data. Obtain the list of diseases targeted by the specified vaccine group. Both the String supplied as the parameter and String returned
     * are compliant to LocallyCodedCdsListItem.getSupportedListConceptItemName().
     *
     * @return Collection of Strings representing the diseases targeted by the vaccine group; empty collection if none. If the specified vaccine group is
     * either null or not a vaccine group tracked in the supporting data, null is returned.
     */
    public Collection<String> getDiseasesTargetedByVaccineGroup(final CdsConcept pVaccineGroupConcept)
    {
        if (pVaccineGroupConcept == null)
            return null;

        final LocallyCodedCdsListItem lCodedCdsListItem = this.iceSupportingDataConfiguration.getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE_GROUP, pVaccineGroupConcept);
        final LocallyCodedVaccineGroupItem lCodedVaccineGroupItem = this.iceSupportingDataConfiguration.getSupportedVaccineGroups()
                .getVaccineGroupItem(lCodedCdsListItem.getCdsListItemName());
        if (lCodedVaccineGroupItem == null)
            return null;

        // It's okay to simply return the list of cdsListItemNames directly; all these have been added as ICEConcepts too during supporting data initialization, and
        // verified that they are indeed DISEASE ice concepts at that point in the process
        return lCodedVaccineGroupItem.getCopyOfRelatedDiseasesCdsListItemNames();
    }

    /**
     * Return true if the vaccine targets one or more of the specified diseases, false if it does not.
     *
     * @param vaccine  the vaccine in question to inspect
     * @param diseases the list of diseases in question; see supporting data file that has the ICEConceptType.DISEASE codes specified for a list of diseases
     */
    public boolean vaccineTargetsOneOrMoreOfSpecifiedDiseases(final Vaccine vaccine, final Collection<String> diseases)
    {
        if (ObjectUtils.isEmpty(diseases) || vaccine == null)
            return false;

        return vaccine.getAllDiseasesTargetedForImmunity().stream().anyMatch(diseases::contains);
    }

    /**
     * Utilizing supporting data. Get the number of vaccine groups across which the specified diseases are handled.
     *
     * @param pSDCs The list of diseases in question; see supporting data file that has the ICEConceptType.DISEASE codes specified for a list of diseases
     * @return int specifying number of vaccine groups encompassing the union of the specified diseases
     */
    private int getCountOfVaccineGroupsEncompassingDiseases(final List<String> pSDCs)
    {
        if (pSDCs == null)
            return 0;

        return iceSupportingDataConfiguration.getSupportedVaccineGroups()
                .getAllVaccineGroupItems()
                .stream()
                .map(LocallyCodedVaccineGroupItem::getCopyOfRelatedDiseasesCdsListItemNames)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .mapToInt(lRelatedDiseaseName -> pSDCs.contains(lRelatedDiseaseName) ? 1 : 0)
                .sum();
    }

    // Get a List of all Seasons supported by this Schedule excluding vaccine group exclusions
    public List<Season> getSeasonsExcludingVaccineGroupExclusions(final List<String> vgExclusions)
    {
        if (ObjectUtils.isEmpty(vgExclusions))
            return getAllSeasons();

        return iceSupportingDataConfiguration.getSupportedSeasons()
                .getCopyOfAllSeasons()
                .stream()
                .filter(lSeason -> !vgExclusions.contains(lSeason.getVaccineGroup()))
                .toList();
    }

    // Get a List of all SeriesRules supported by this Schedule excluding vaccine group exclusions
    public List<SeriesRules> getSeriesRulesExcludingVaccineGroupExclusions(final List<String> vgExclusions)
    {
        if (ObjectUtils.isEmpty(vgExclusions))
            return getAllSeries();

        return iceSupportingDataConfiguration.getSupportedSeries()
                .getCopyOfAllSeriesRules()
                .stream()
                .filter(lSeriesRule -> !vgExclusions.contains(lSeriesRule.getVaccineGroup()))
                .toList();
    }

    // Get a list of all SeriesRules supported by this Schedule.
    public List<SeriesRules> getAllSeries()
    {
        return this.iceSupportingDataConfiguration.getSupportedSeries().getCopyOfAllSeriesRules();
    }

    // Get a list of all SeriesRules supported by this Schedule.
    public List<Season> getAllSeasons()
    {
        return this.iceSupportingDataConfiguration.getSupportedSeasons().getCopyOfAllSeasons();
    }

    /**
     * This is deprecated; simply returns all series
     */
    @Deprecated
    public List<SeriesRules> getCandidateSeries()
    {
        return getAllSeries();
    }
}
