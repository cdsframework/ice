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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.config.iceSupportingProperties.Dose;
import org.cdsframework.ice.config.iceSupportingProperties.DoseInterval;
import org.cdsframework.ice.config.iceSupportingProperties.DoseVaccine;
import org.cdsframework.ice.config.iceSupportingProperties.Season;
import org.cdsframework.ice.config.iceSupportingProperties.SeriesData;
import org.cdsframework.ice.config.iceSupportingProperties.VaccineGroup;
import org.cdsframework.ice.service.DoseStatus;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.RecommendationStatus;
import org.cdsframework.ice.service.SupportingDataService;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.opencds.config.api.model.KMId;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ICESupportingDataConfiguration
{
    private final SupportedCdsLists supportedCdsLists;
    private final SupportedVaccineGroups supportedVaccineGroups;
    private final SupportedVaccines supportedVaccines;
    private final SupportedSeasons supportedSeasons;
    private final SupportedSeries supportedSeries;

    /**
     * Initialize all supporting data. Note that order matters: first CdsLists (i.e. - code systems and value sets) must be initialized; then vaccine groups;
     * vaccines; seasons; finally, series
     */
    public ICESupportingDataConfiguration(final String pCommonLogicModule, final List<String> pSupportedKnowledgeModules,
            final IceSupportingDataProperties iceSupportingDataProperties, final SupportingDataService supportingDataService)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "ICESupportingDataConfiguration(): ";

        if (ObjectUtils.isEmpty(pCommonLogicModule) || ObjectUtils.isEmpty(pSupportedKnowledgeModules))
        {
            final String lErrStr = "Applicable CDS versions for this set of supporting data not specified; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (log.isDebugEnabled())
        {
            int i = 1;
            log.debug(_METHODNAME + "Common Knowledge: {}", pCommonLogicModule);
            for (final String lCdsVersion : pSupportedKnowledgeModules)
                log.debug(_METHODNAME + "CDS version #{}: {}", i++, lCdsVersion);
        }

        final List<String> supportedCdsVersions = new ArrayList<>();
        final StringBuilder lSbSDlocation = new StringBuilder(720);
        final StringBuilder lSbCdsVersion = new StringBuilder(160);
        lSbSDlocation.append("Supporting Data Directories: ");
        lSbCdsVersion.append("CDS versions: ");

        // First the common logic
        final KMId lCommonLogicKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(pCommonLogicModule);
        if (lCommonLogicKMId == null)
        {
            final String lErrStr =
                    "Common Logic Module not specified in proper format: " + pCommonLogicModule + ". Cannot continue";
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        lSbCdsVersion.append(pCommonLogicModule);
        supportedCdsVersions.add(pCommonLogicModule);

        // Then the knowledge module logic for each specified knowledge module
        for (final String lCdsVersion : pSupportedKnowledgeModules)
        {
            final KMId lKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lCdsVersion);
            if (lKMId == null)
            {
                final String lErrStr = "Knowledge Module not specified in proper format: " + lCdsVersion + ". Cannot continue";
                log.error(_METHODNAME + "{}", lErrStr);
                throw new IllegalArgumentException(lErrStr);
            }

            lSbSDlocation.append("; ");
            lSbCdsVersion.append("; ");
            lSbCdsVersion.append(lCdsVersion);
            supportedCdsVersions.add(lCdsVersion);
        }

        // Initialize Code Systems/Value Sets supporting data
        this.supportedCdsLists = new SupportedCdsLists(supportedCdsVersions);
        try
        {
            Stream.concat(Stream.of(pCommonLogicModule), pSupportedKnowledgeModules.stream())
                    .distinct()
                    .filter(iceSupportingDataProperties.getKnowledgeModules()::containsKey)
                    .forEach(kmId -> Optional.ofNullable(iceSupportingDataProperties.getKnowledgeModules().get(kmId).codeSystems())
                            .ifPresent(codeSystemMap -> codeSystemMap.values()
                                    .forEach(codeSystem -> this.supportedCdsLists.addSupportedCodeSystem(codeSystem,
                                            supportingDataService.extractCodeSystemOid(codeSystem)))));
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *CdsLists* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following CdsLists have been initialized into the " + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedCdsLists.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Check to make sure that the required base data codes have been supplied
        if (!allBaseSupportingDataCdsListItemInitialized())
        {
            final String lErrStr =
                    "Some base supporting data cdsListItems not supplied; a minimum set of cdsListItem codes must be specified. See ICE documentation";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }
        this.supportedCdsLists.validateSupplementalReasonSupportingData();

        // Initialize the Vaccine Group supporting data
        this.supportedVaccineGroups = new SupportedVaccineGroups(this);
        try
        {
            this.supportedVaccineGroups.initializeFromCdsLists();
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Vaccine Groups* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Vaccine Groups have been initialized into the " + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedVaccineGroups.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Initialize the Vaccine supporting data
        this.supportedVaccines = new SupportedVaccines(this);
        try
        {
            this.supportedVaccines.initializeFromCdsLists();
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Vaccines* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Vaccines have been initialized into the " + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedVaccines.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        if (!this.supportedVaccines.isSupportingDataConsistent())
        {
            final String lErrStr =
                    "The vaccine data supplied is inconsistent. Please ensure that all vaccine components for all vaccines have been defined in the supporting data";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Initialize Seasons supporting data
        this.supportedSeasons = new SupportedSeasons(this);
        try
        {
            this.supportedSeasons.initializeFromCdsLists();
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Seasons* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }

        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Seasons have been initialized into the " + this.getClass().getName() + ":\n";
            lDebugStr += this.supportedSeasons.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Initialize Series supporting data
        this.supportedSeries = new SupportedSeries(this);
        try
        {
            pSupportedKnowledgeModules.forEach(kmId -> iceSupportingDataProperties.getKnowledgeModules()
                    .get(kmId)
                    .series()
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(sd -> sd.series().displayName()))
                    .forEach(this::addSupportedSeriesFromIceProperties));
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Series* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Series have been initialized into the " + this.getClass().getName() + ":\n";
            lDebugStr += this.supportedSeries.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Log configuration data parameters of data initialized
        lSbCdsVersion.append("; ");
        lSbSDlocation.insert(0, lSbCdsVersion);
        lSbSDlocation.insert(0, _METHODNAME);
        log.debug(lSbSDlocation.toString());
    }

    /**
     * Get the ICE SupportedVaccineGroups data for this supporting data configuration
     */
    public SupportedCdsConcepts getSupportedCdsConcepts()
    {
        return getSupportedCdsLists().getSupportedCdsConcepts();
    }

    private boolean allBaseSupportingDataCdsListItemInitialized()
    {
        // Verify that all DoseStatus enumeration items been provided
        if (!verifyCdsListItemExistsForAllEnumConstants(DoseStatus.class))
        {
            log.warn("All DoseStatus enumeration items have not been provided");
            return false;
        }

        // Verify that all EvaluationReason items been provided
        if (!verifyCdsListItemExistsForAllEnumConstants(BaseDataEvaluationReason.class))
        {
            log.warn("All BaseDataEvaluationReason enumeration items have not been provided");
            return false;
        }

        // Verify that all RecommendationStatus items have been provided
        if (!verifyCdsListItemExistsForAllEnumConstants(RecommendationStatus.class))
        {
            log.warn("All RecommendationStatus enumeration items have not been provided");
            return false;
        }

        // Verify that all RecommendationReason items have been provided
        return verifyCdsListItemExistsForAllEnumConstants(BaseDataRecommendationReason.class);
    }

    private <E extends Enum<E>> boolean verifyCdsListItemExistsForAllEnumConstants(final Class<E> pEnum)
    {
        final String _METHODNAME = "verifyCdsListItemForAllSpecifiedEnumConstants(): ";

        if (pEnum == null)
        {
            log.warn(_METHODNAME + "enumeration supplied is not of type BaseData");
            return false;
        }

        for (final Enum<E> enumVal : pEnum.getEnumConstants())
        {
            if (!(enumVal instanceof final BaseData baseData))
            {
                log.warn(_METHODNAME + "enumeration supplied not of type BaseData");
                return false;
            }

            final String lCdsListItemName = baseData.getCdsListItemName();
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "BaseData cdsListItemName{}.{}", pEnum.getSimpleName(), lCdsListItemName);
            if (lCdsListItemName != null && !this.supportedCdsLists.cdsListItemExists(lCdsListItemName))
            {
                log.warn(_METHODNAME + "CdsListItemName {} not found", lCdsListItemName);
                return false;
            }
        }

        return true;
    }

    public void addSupportedSeriesFromIceProperties(final SeriesData pSeriesData) throws InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedSeriesFromIceProperties(): ";

        Optional.ofNullable(pSeriesData).ifPresentOrElse(s ->
        {
            Optional.ofNullable(s.series()).filter(sd -> sd.code() != null).orElseThrow(() ->
            {
                final String lErrStr = "Required supporting data season element not provided in Season properties";
                log.error(_METHODNAME + lErrStr);
                return new InconsistentConfigurationException(lErrStr);
            });

            if (log.isDebugEnabled())
            {
                final StringBuilder lDebugStrb = new StringBuilder();
                lDebugStrb.append(_METHODNAME).append(s.getClass().getName());
                // ID
                lDebugStrb.append("\ngetSeriesId(): ").append(s.seriesId());
                // Code
                lDebugStrb.append("\ngetCode(): ").append(s.series().code());
                // Name
                lDebugStrb.append("\ngetName(): ").append(s.series().displayName());
                // Number of Doses in Series
                lDebugStrb.append("\ngetNumberOfDosesInSeries(): ").append(s.numberOfDosesInSeries());
                // Vaccine groups
                lDebugStrb.append("\ngetVaccineGroups(): ");
                Optional.ofNullable(s.vaccineGroup()).map(Map::values).ifPresentOrElse(values ->
                {
                    int i = 1;
                    for (final VaccineGroup lVaccineGroup : values)
                        lDebugStrb.append("\n\t(").append(i++).append("): ").append(lVaccineGroup);
                }, () -> lDebugStrb.append("\n\tNo Vaccine Group information supplied"));

                // Seasons
                lDebugStrb.append("\ngetSeasonCodes(): ");
                Optional.ofNullable(s.seasons()).map(Map::values).ifPresentOrElse(values ->
                {
                    int i = 1;
                    for (final Season lSeason : values)
                        lDebugStrb.append("\n\t(").append(i++).append("): ").append(lSeason.code());
                }, () -> lDebugStrb.append("\n\tNo Seasons information supplied"));

                // CdsVersions
                lDebugStrb.append("\ngetCdsVersions(): ");
                Optional.ofNullable(s.cdsVersion()).map(Map::values).ifPresentOrElse(values ->
                {
                    int i = 1;
                    for (final String lCdsVersion : values)
                        lDebugStrb.append("\n\t(").append(i++).append("): ").append(lCdsVersion);
                }, () -> lDebugStrb.append("\n\tNo CdsVersion information supplied"));

                // Series Dose Specifications
                lDebugStrb.append("\ngetIceSeriesDoses(): ");
                Optional.ofNullable(s.doses()).map(Map::values).ifPresentOrElse(values ->
                {
                    int i = 1;
                    for (final Dose lDose : values)
                    {
                        lDebugStrb.append("\n\t(")
                                .append(i++)
                                .append("): absolute minimum age: ")
                                .append(lDose.absoluteMinimumAge())
                                .append("; earliest recommended age: ")
                                .append(lDose.earliestRecommendedAge())
                                .append("; latest recommended age: ")
                                .append(lDose.latestRecommendedAge())
                                .append("; minimum age: ")
                                .append(lDose.minimumAge())
                                .append("; maximum age: ")
                                .append(lDose.absoluteMaximumAge());

                        // Doses -> Vaccines
                        lDebugStrb.append("\n\t\tgetDoseVaccines(): ");
                        Optional.ofNullable(lDose.doseVaccines()).map(Map::values).ifPresentOrElse(doseVaccines ->
                        {
                            int j = 1;
                            for (final DoseVaccine lDV : doseVaccines)
                            {
                                lDebugStrb.append("\n\t\t\t(")
                                        .append(j++)
                                        .append("): preferred: ")
                                        .append(lDV.preferred())
                                        .append("; vaccine: ")
                                        .append(lDV.vaccine());
                            }
                        }, () -> lDebugStrb.append("\n\t\t\tNo Vaccine information supplied"));
                    }
                }, () -> lDebugStrb.append("\t\nNo IceSeriesDoses specified"));

                // Series Dose Intervals
                lDebugStrb.append("\ngetDoseIntervals(): ");
                Optional.ofNullable(s.doseIntervals()).map(Map::values).ifPresentOrElse(values ->
                {
                    int i = 1;
                    for (final DoseInterval lDoseInterval : values)
                        lDebugStrb.append("\n\t(")
                                .append(i++)
                                .append("): from dose number: ")
                                .append(lDoseInterval.fromDoseNumber())
                                .append("; to dose number: ")
                                .append(lDoseInterval.toDoseNumber())
                                .append("; absolute minimum interval: ")
                                .append(lDoseInterval.absoluteMinimumInterval())
                                .append("; minimum interval: ")
                                .append(lDoseInterval.minimumInterval())
                                .append("; earliest recommended interval: ")
                                .append(lDoseInterval.earliestRecommendedInterval())
                                .append("; latest recommended interval: ")
                                .append(lDoseInterval.latestRecommendedInterval());
                }, () -> lDebugStrb.append("\t\nNo Dose Interval information provided"));

                log.debug(lDebugStrb.toString());
            }

            this.supportedSeries.addSupportedSeriesItemFromProperties(s);
        }, () -> log.warn(_METHODNAME + "Series object not specified; read of supporting data properties skipped"));
    }

}
