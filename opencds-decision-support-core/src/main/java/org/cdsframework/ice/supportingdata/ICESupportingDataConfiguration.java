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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.PlanDefinition;

import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.DoseStatus;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.PlanDefinitionSeriesDataConsumer;
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
    private static final String SUPPORTED_SERIES_CODE_SYSTEM_NAME = "SUPPORTED_SERIES";
    private static final PlanDefinitionSeriesDataConsumer PLAN_DEFINITION_SERIES_DATA_CONSUMER =
            new PlanDefinitionSeriesDataConsumer();

    private final SupportedCdsLists supportedCdsLists;
    private final SupportedVaccineGroups supportedVaccineGroups;
    private final SupportedVaccines supportedVaccines;
    private final SupportedScheduleAuthorities supportedScheduleAuthorities;
    private final SupportedSeasons supportedSeasons;
    private final SupportedSeries supportedSeries;

    /**
     * Initialize all supporting data. Note that order matters: first CdsLists (i.e. - code systems and value sets) must be initialized; then vaccine groups;
     * vaccines; seasons; finally, series
     */
    public ICESupportingDataConfiguration(final String pCommonLogicModule, final List<String> pSupportedKnowledgeModules,
            final SupportingDataService supportingDataService) throws IllegalArgumentException, InconsistentConfigurationException
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
                    .map(supportingDataService::getSupportingKnowledgeModuleByKmId)
                    .forEach(knowledgeModule -> Optional.ofNullable(knowledgeModule.codeSystems())
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
            log.debug(_METHODNAME + "{}", StringUtils.normalizeSpace(lDebugStr));
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
            log.debug(_METHODNAME + "{}", StringUtils.normalizeSpace(lDebugStr));
        }

        if (!this.supportedVaccines.isSupportingDataConsistent())
        {
            final String lErrStr =
                    "The vaccine data supplied is inconsistent. Please ensure that all vaccine components for all vaccines have been defined in the supporting data";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Initialize Schedule Authority supporting data
        this.supportedScheduleAuthorities = new SupportedScheduleAuthorities(this);
        try
        {
            this.supportedScheduleAuthorities.initializeFromCdsLists();
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Schedule Authority* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Schedule Authorities have been initialized into the "
                    + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedScheduleAuthorities.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        if (!this.supportedScheduleAuthorities.isSupportingDataConsistent())
        {
            final String lErrStr =
                    "The schedule authority data supplied is inconsistent. Please ensure that all schedule authorities have been defined in the supporting data";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Initialize Seasons supporting data
        this.supportedSeasons = new SupportedSeasons(this);
        try
        {
            this.supportedSeasons.initializeFromCdsLists(supportingDataService.getSeasonOverrides());
        }
        catch (final InconsistentConfigurationException e)
        {
            throw e;
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
            log.debug(_METHODNAME + "{}", StringUtils.normalizeSpace(lDebugStr));
        }

        // Initialize Series supporting data
        this.supportedSeries = new SupportedSeries(this);
        final List<SeriesData> loadedSeriesData = new ArrayList<>();
        try
        {
            pSupportedKnowledgeModules.forEach(
                    kmId -> Optional.ofNullable(supportingDataService.getSupportingKnowledgeModuleByKmId(kmId).series())
                            .map(Map::values)
                            .stream()
                            .flatMap(java.util.Collection::stream)
                            .sorted(Comparator.comparing(sd -> sd.series().displayName()))
                            .forEach(loadedSeriesData::add));
            final List<SeriesData> effectiveSeriesData =
                    applySeriesOverrides(loadedSeriesData, supportingDataService.getSeriesOverrides());
            effectiveSeriesData.forEach(this::addSupportedSeriesFromIceProperties);
            validateSupportedSeriesConsistency(pSupportedKnowledgeModules, supportingDataService, effectiveSeriesData);
            validateSupportedSeasonsReferencedBySeriesData(effectiveSeriesData);
        }
        catch (final InconsistentConfigurationException e)
        {
            throw e;
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
            log.debug(_METHODNAME + "{}", StringUtils.normalizeSpace(lDebugStr));
        }

        // Log configuration data parameters of data initialized
        lSbCdsVersion.append("; ");
        lSbSDlocation.insert(0, lSbCdsVersion);
        lSbSDlocation.insert(0, _METHODNAME);
        log.debug(lSbSDlocation.toString());
    }

    private void validateSupportedSeriesConsistency(final List<String> supportedKnowledgeModules,
            final SupportingDataService supportingDataService, final List<SeriesData> seriesDataItems)
            throws InconsistentConfigurationException
    {
        final Set<String> supportedSeriesCodes = extractSupportedSeriesCodes(supportedKnowledgeModules, supportingDataService);
        final Set<String> seriesPlanDefinitionNames =
                validateSeriesPlanDefinitionMapKeysAndNames(supportedKnowledgeModules, supportingDataService);
        validateSeriesPlanDefinitionsExistInSupportedSeries(seriesPlanDefinitionNames, supportedSeriesCodes);
        validateSupportedSeriesReferencedBySeriesData(supportedSeriesCodes, seriesDataItems);
    }

    private Set<String> extractSupportedSeriesCodes(final List<String> supportedKnowledgeModules,
            final SupportingDataService supportingDataService)
    {
        final Set<String> supportedSeriesCodes = new TreeSet<>();
        Optional.ofNullable(supportedKnowledgeModules)
                .orElse(List.of())
                .stream()
                .map(supportingDataService::getSupportingKnowledgeModuleByKmId)
                .map(CdsEngineProperties.ModuleCanonicalDefinition::codeSystems)
                .filter(Objects::nonNull)
                .map(codeSystems -> codeSystems.get(SUPPORTED_SERIES_CODE_SYSTEM_NAME))
                .filter(Objects::nonNull)
                .map(CodeSystem::concept)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(CodeSystemConcept::code)
                .map(this::normalizeSeriesCodeForComparison)
                .filter(code -> !code.isEmpty())
                .forEach(supportedSeriesCodes::add);
        return supportedSeriesCodes;
    }

    private List<SeriesData> applySeriesOverrides(final List<SeriesData> loadedSeriesData,
            final Map<String, IceProperties.SeriesOverride> seriesOverrides)
    {
        if (ObjectUtils.isEmpty(loadedSeriesData))
            return List.of();

        if (ObjectUtils.isEmpty(seriesOverrides))
            return loadedSeriesData;

        final String _METHODNAME = "applySeriesOverrides(): ";
        final Set<String> validSeriesCodes = loadedSeriesData.stream()
                .map(SeriesData::series)
                .filter(Objects::nonNull)
                .map(Series::code)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Set<String> unknownSeriesOverrides = new TreeSet<>(seriesOverrides.keySet());
        unknownSeriesOverrides.removeAll(validSeriesCodes);
        if (!unknownSeriesOverrides.isEmpty())
        {
            final String lErrStr = "Series override key(s) do not reference existing series: " + unknownSeriesOverrides;
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        return loadedSeriesData.stream().map(sd -> applySeriesOverride(sd, seriesOverrides)).toList();
    }

    private SeriesData applySeriesOverride(final SeriesData loadedSeriesData,
            final Map<String, IceProperties.SeriesOverride> seriesOverrides)
    {
        if (loadedSeriesData == null || loadedSeriesData.series() == null || loadedSeriesData.series().code() == null)
            return loadedSeriesData;

        final String seriesCode = loadedSeriesData.series().code();
        final IceProperties.SeriesOverride seriesOverride = seriesOverrides.get(seriesCode);
        if (seriesOverride == null)
            return loadedSeriesData;

        return new SeriesData(loadedSeriesData.series(),
                firstNonNull(seriesOverride.numberOfDosesInSeries(), loadedSeriesData.numberOfDosesInSeries()),
                firstNonNull(seriesOverride.recurringDosesAfterSeriesComplete(),
                        loadedSeriesData.recurringDosesAfterSeriesComplete()),
                overrideSeasonsMap(loadedSeriesData.seasons(), seriesOverride.seasons()), loadedSeriesData.vaccineGroup(),
                loadedSeriesData.seriesGroup(), loadedSeriesData.patientStartAge(), loadedSeriesData.patientEndAge(),
                loadedSeriesData.doseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(),
                overrideDosesMap(seriesCode, loadedSeriesData.doses(), seriesOverride.doseOverrides()),
                overrideDoseIntervalsMap(seriesCode, loadedSeriesData.doseIntervals(), seriesOverride.doseIntervalOverrides()));
    }

    private Map<String, Season> overrideSeasonsMap(final Map<String, Season> originalSeasons,
            final List<String> overrideSeasonCodes)
    {
        if (overrideSeasonCodes == null)
            return originalSeasons;

        final Map<String, Season> source = Optional.ofNullable(originalSeasons)
                .map(map -> map.values().stream().collect(Collectors.toMap(Season::code, Function.identity())))
                .orElseGet(HashMap::new);

        return overrideSeasonCodes.stream().filter(Objects::nonNull).map(seasonCode ->
        {
            final Season resolvedSeason = resolveSeasonOverride(seasonCode, source);
            return resolvedSeason != null ? Map.entry(seasonCode, resolvedSeason) : null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Season resolveSeasonOverride(final String seasonCode, final Map<String, Season> source)
    {
        final Season existing = source.get(seasonCode);
        if (existing != null)
            return existing;

        final LocallyCodedSeasonItem supportedSeasonItem = Optional.ofNullable(this.supportedSeasons)
                .map(supported -> supported.getSeasonItem(
                        String.format("%s.%s", ICEConceptType.SEASON.getIceConceptTypeValue(), seasonCode)))
                .orElse(null);

        if (supportedSeasonItem == null)
            return null;

        final Season templateSeason = source.values().stream().filter(Objects::nonNull).findFirst().orElse(null);
        if (templateSeason == null)
            return null;

        return new Season(seasonCode, templateSeason.codeSystem(), templateSeason.codeSystemName(),
                Optional.ofNullable(supportedSeasonItem.getSeason())
                        .map(org.cdsframework.ice.service.Season::getSeasonName)
                        .orElse(seasonCode));
    }

    private Map<String, Dose> overrideDosesMap(final String seriesCode, final Map<String, Dose> originalDoses,
            final Map<Integer, IceProperties.SeriesDoseOverride> doseOverrides)
    {
        final Map<String, Dose> originalDosesWithDoseNumbers = Optional.ofNullable(originalDoses)
                .map(doses -> doses.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> entry.getValue().copyWithDoseNumber(Integer.valueOf(entry.getKey()))))).orElseGet(Map::of);

        if (ObjectUtils.isEmpty(doseOverrides))
            return originalDosesWithDoseNumbers;

        final String _METHODNAME = "overrideDosesMap(): ";
        final Set<Integer> validDoseNumbers =
                originalDosesWithDoseNumbers.values().stream().map(Dose::doseNumber).collect(Collectors.toSet());

        final Set<Integer> unknownDoseOverrides = new TreeSet<>(doseOverrides.keySet());
        unknownDoseOverrides.removeAll(validDoseNumbers);
        if (!unknownDoseOverrides.isEmpty())
        {
            final String lErrStr = "Dose override key(s) for series '%s' do not reference existing doses: %s".formatted(seriesCode,
                    unknownDoseOverrides);
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        return originalDosesWithDoseNumbers.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> applyDoseOverride(seriesCode, e.getValue(), doseOverrides),
                        (_, b) -> b, LinkedHashMap::new));
    }

    private Dose applyDoseOverride(final String seriesCode, final Dose dose,
            final Map<Integer, IceProperties.SeriesDoseOverride> doseOverrides)
    {
        if (dose == null)
            return null;

        final Integer doseNumber = dose.doseNumber();
        if (doseNumber == null)
            return dose;

        final IceProperties.SeriesDoseOverride doseOverride = doseOverrides.get(doseNumber);
        if (doseOverride == null)
            return dose;

        return new Dose(doseNumber, firstNonNull(doseOverride.absoluteMinimumAge(), dose.absoluteMinimumAge()),
                firstNonNull(doseOverride.minimumAge(), dose.minimumAge()),
                firstNonNull(resolveEarliestRecommendedAgeOverride(doseOverride), dose.earliestRecommendedAge()),
                dose.latestRecommendedAge(), firstNonNull(doseOverride.absoluteMaximumAge(), dose.absoluteMaximumAge()),
                overrideDoseVaccinesMap(seriesCode, doseNumber, dose.doseVaccines(), doseOverride.seriesVaccineOverrides()));
    }

    private String resolveEarliestRecommendedAgeOverride(final IceProperties.SeriesDoseOverride doseOverride)
    {
        if (doseOverride == null)
            return null;

        if (doseOverride.earliestRecommendedAge() != null)
            return doseOverride.earliestRecommendedAge();

        return Optional.ofNullable(doseOverride.earliestRecommendedDate()).map(Object::toString).orElse(null);
    }

    private Map<String, DoseVaccine> overrideDoseVaccinesMap(final String seriesCode, final Integer doseNumber,
            final Map<String, DoseVaccine> originalDoseVaccines,
            final Map<String, IceProperties.SeriesDoseVaccineOverride> vaccineOverrides)
    {
        if (ObjectUtils.isEmpty(vaccineOverrides))
            return originalDoseVaccines;

        final String _METHODNAME = "overrideDoseVaccinesMap(): ";
        final Set<String> validVaccineCodes =
                originalDoseVaccines.values().stream().map(DoseVaccine::vaccine).map(Vaccine::code).collect(Collectors.toSet());

        final Set<String> unknownVaccineOverrides = new TreeSet<>(vaccineOverrides.keySet());
        unknownVaccineOverrides.removeAll(validVaccineCodes);
        if (!unknownVaccineOverrides.isEmpty())
        {
            final String lErrStr =
                    "Series vaccine override key(s) for series '%s', dose '%d' do not reference vaccine codes for doses for the series: %s".formatted(
                            seriesCode, doseNumber, unknownVaccineOverrides);
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        return Map.copyOf(getDoseVaccineOverrides(originalDoseVaccines, vaccineOverrides));
    }

    private Map<String, DoseVaccine> getDoseVaccineOverrides(final Map<String, DoseVaccine> originalDoseVaccines,
            final Map<String, IceProperties.SeriesDoseVaccineOverride> vaccineOverrides)
    {
        return Optional.ofNullable(originalDoseVaccines)
                .orElse(Map.of())
                .entrySet()
                .stream()
                .collect(LinkedHashMap::new, (map, e) -> map.put(e.getKey(),
                        Optional.ofNullable(vaccineOverrides.get(e.getValue().vaccine().code()))
                                .map(vaccineOverride -> new DoseVaccine(
                                        firstNonNull(vaccineOverride.preferred(), e.getValue().preferred()), e.getValue().vaccine(),
                                        firstNonNull(vaccineOverride.allowableMinimumAgeOfUse(),
                                                e.getValue().allowableMinimumAgeOfUse()), e.getValue().allowableMaximumAgeOfUse()))
                                .orElse(e.getValue())), LinkedHashMap::putAll);
    }

    private Map<String, DoseInterval> overrideDoseIntervalsMap(final String seriesCode,
            final Map<String, DoseInterval> originalDoseIntervals,
            final Map<Integer, Map<Integer, IceProperties.SeriesDoseIntervalOverride>> doseIntervalOverrides)
    {
        if (ObjectUtils.isEmpty(doseIntervalOverrides))
            return originalDoseIntervals;

        final String _METHODNAME = "overrideDoseIntervalsMap(): ";
        final Map<String, DoseInterval> source = Optional.ofNullable(originalDoseIntervals).orElse(Map.of());

        for (final Map.Entry<Integer, Map<Integer, IceProperties.SeriesDoseIntervalOverride>> fromEntry : doseIntervalOverrides.entrySet())
        {
            final Integer fromDoseNumber = fromEntry.getKey();
            for (final Integer toDoseNumber : fromEntry.getValue().keySet())
            {
                if (source.values()
                        .stream()
                        .noneMatch(di -> Objects.equals(di.fromDoseNumber(), fromDoseNumber) && Objects.equals(di.toDoseNumber(),
                                toDoseNumber)))
                {
                    final String lErrStr =
                            "Dose interval override (from: %d, to: %d) for series '%s' does not reference an existing dose interval".formatted(
                                    fromDoseNumber, toDoseNumber, seriesCode);
                    log.error("{}{}", _METHODNAME, lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }
            }
        }

        return source.entrySet()
                .stream()
                .collect(LinkedHashMap::new, (map, e) -> map.put(e.getKey(), e.getValue() == null
                                                                             ? null
                                                                             : applyDoseIntervalOverride(e.getValue(),
                                                                                     doseIntervalOverrides.get(
                                                                                             e.getValue().fromDoseNumber()))),
                        LinkedHashMap::putAll);
    }

    private DoseInterval applyDoseIntervalOverride(final DoseInterval doseInterval,
            final Map<Integer, IceProperties.SeriesDoseIntervalOverride> fromOverrides)
    {
        if (doseInterval == null || ObjectUtils.isEmpty(fromOverrides))
            return doseInterval;

        final IceProperties.SeriesDoseIntervalOverride intervalOverride = fromOverrides.get(doseInterval.toDoseNumber());
        if (intervalOverride == null)
            return doseInterval;

        return new DoseInterval(doseInterval.fromDoseNumber(), doseInterval.toDoseNumber(),
                firstNonNull(intervalOverride.absoluteMinimumInterval(), doseInterval.absoluteMinimumInterval()),
                firstNonNull(intervalOverride.minimumInterval(), doseInterval.minimumInterval()),
                firstNonNull(intervalOverride.earliestRecommendedInterval(), doseInterval.earliestRecommendedInterval()),
                firstNonNull(intervalOverride.latestRecommendedInterval(), doseInterval.latestRecommendedInterval()));
    }

    private <T> T firstNonNull(final T candidate, final T fallback)
    {
        return candidate != null ? candidate : fallback;
    }

    private Set<String> validateSeriesPlanDefinitionMapKeysAndNames(final List<String> supportedKnowledgeModules,
            final SupportingDataService supportingDataService) throws InconsistentConfigurationException
    {
        final String _METHODNAME = "validateSeriesPlanDefinitionMapKeysAndNames(): ";
        final Set<String> seriesPlanDefinitionNames = new TreeSet<>();

        Optional.ofNullable(supportedKnowledgeModules)
                .orElse(List.of())
                .stream()
                .map(supportingDataService::getSupportingKnowledgeModuleByKmId)
                .map(CdsEngineProperties.ModuleCanonicalDefinition::planDefinitions)
                .filter(Objects::nonNull)
                .forEach(planDefinitions -> planDefinitions.forEach((planDefinitionKey, planDefinition) ->
                {
                    if (!PLAN_DEFINITION_SERIES_DATA_CONSUMER.isSeriesPlanDefinition(planDefinition))
                        return;

                    final String normalizedMapKey = normalizeSeriesCodeForComparison(planDefinitionKey);
                    final String normalizedName = Optional.ofNullable(planDefinition)
                            .map(PlanDefinition::name)
                            .map(this::normalizeSeriesCodeForComparison)
                            .orElse("");

                    if (normalizedMapKey.isEmpty() || normalizedName.isEmpty())
                    {
                        final String lErrStr =
                                "Series PlanDefinition map key and name must both be populated. key='%s', name='%s'".formatted(
                                        planDefinitionKey,
                                        Optional.ofNullable(planDefinition).map(PlanDefinition::name).orElse(null));
                        log.error("{}{}", _METHODNAME, lErrStr);
                        throw new InconsistentConfigurationException(lErrStr);
                    }
                    if (!normalizedMapKey.equals(normalizedName))
                    {
                        final String lErrStr =
                                "Series PlanDefinition map key must match PlanDefinition.name. key='%s', name='%s'".formatted(
                                        planDefinitionKey, planDefinition.name());
                        log.error("{}{}", _METHODNAME, lErrStr);
                        throw new InconsistentConfigurationException(lErrStr);
                    }

                    seriesPlanDefinitionNames.add(normalizedName);
                }));

        return seriesPlanDefinitionNames;
    }

    private void validateSeriesPlanDefinitionsExistInSupportedSeries(final Set<String> seriesPlanDefinitionNames,
            final Set<String> supportedSeriesCodes) throws InconsistentConfigurationException
    {
        final String _METHODNAME = "validateSeriesPlanDefinitionsExistInSupportedSeries(): ";
        if (seriesPlanDefinitionNames.isEmpty())
            return;

        if (supportedSeriesCodes.isEmpty())
        {
            final String lErrStr = "No SUPPORTED_SERIES concepts configured, but series PlanDefinitions were found";
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        final Set<String> unknownSeriesPlanDefinitionNames = new TreeSet<>(seriesPlanDefinitionNames);
        unknownSeriesPlanDefinitionNames.removeAll(supportedSeriesCodes);
        if (!unknownSeriesPlanDefinitionNames.isEmpty())
        {
            final String lErrStr = "Series PlanDefinition name(s) are missing from SUPPORTED_SERIES concept list: %s".formatted(
                    String.join(", ", unknownSeriesPlanDefinitionNames));
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }
    }

    private void validateSupportedSeriesReferencedBySeriesData(final Set<String> supportedSeriesCodes,
            final List<SeriesData> seriesDataItems) throws InconsistentConfigurationException
    {
        final String _METHODNAME = "validateSupportedSeriesReferencedBySeriesData(): ";
        if (supportedSeriesCodes.isEmpty())
            return;

        final Set<String> seriesCodesReferencedBySeriesData = new TreeSet<>();
        Optional.ofNullable(seriesDataItems)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .map(SeriesData::series)
                .filter(Objects::nonNull)
                .map(Series::code)
                .map(this::normalizeSeriesCodeForComparison)
                .filter(code -> !code.isEmpty())
                .forEach(seriesCodesReferencedBySeriesData::add);

        final Set<String> missingSeriesCodes = new TreeSet<>(supportedSeriesCodes);
        missingSeriesCodes.removeAll(seriesCodesReferencedBySeriesData);
        if (!missingSeriesCodes.isEmpty())
        {
            final String lErrStr = "Supported series code(s) are not referenced by any series data: %s".formatted(
                    String.join(", ", missingSeriesCodes));
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }
    }

    private String normalizeSeriesCodeForComparison(final String seriesCode)
    {
        if (seriesCode == null)
            return "";

        final String trimmed = seriesCode.trim();
        if (trimmed.isEmpty())
            return trimmed;

        final String seriesPrefix = ICEConceptType.SERIES.getIceConceptTypeValue() + ".";
        return trimmed.startsWith(seriesPrefix) ? trimmed.substring(seriesPrefix.length()) : trimmed;
    }

    private void validateSupportedSeasonsReferencedBySeriesData(final List<SeriesData> seriesDataItems)
            throws InconsistentConfigurationException
    {
        final String _METHODNAME = "validateSupportedSeasonsReferencedBySeriesData(): ";
        final Set<String> supportedSeasonCodes = new TreeSet<>();
        Optional.ofNullable(this.supportedSeasons)
                .map(SupportedSeasons::getCopyOfAllSeasons)
                .orElse(List.of())
                .stream()
                .map(org.cdsframework.ice.service.Season::getSeasonName)
                .map(this::normalizeSeasonCodeForComparison)
                .filter(code -> !code.isEmpty())
                .forEach(supportedSeasonCodes::add);

        if (supportedSeasonCodes.isEmpty())
            return;

        final Set<String> seasonCodesReferencedBySeriesData = new TreeSet<>();
        Optional.ofNullable(seriesDataItems)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .map(SeriesData::seasons)
                .filter(Objects::nonNull)
                .map(Map::values)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(Season::code)
                .map(this::normalizeSeasonCodeForComparison)
                .filter(code -> !code.isEmpty())
                .forEach(seasonCodesReferencedBySeriesData::add);

        supportedSeasonCodes.removeAll(seasonCodesReferencedBySeriesData);
        if (!supportedSeasonCodes.isEmpty())
        {
            final String lErrStr = "Supported season code(s) are not referenced by any series data: %s".formatted(
                    String.join(", ", supportedSeasonCodes));
            log.error("{}{}", _METHODNAME, lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }
    }

    private String normalizeSeasonCodeForComparison(final String seasonCode)
    {
        if (seasonCode == null)
            return "";

        final String trimmed = seasonCode.trim();
        if (trimmed.isEmpty())
            return trimmed;

        final String seasonPrefix = ICEConceptType.SEASON.getIceConceptTypeValue() + ".";
        return trimmed.startsWith(seasonPrefix) ? trimmed.substring(seasonPrefix.length()) : trimmed;
    }

    /**
     * Get the ICE SupportedVaccineGroups data for this supporting data configuration
     */
    public SupportedCdsConcepts getSupportedCdsConcepts()
    {
        return getSupportedCdsLists().getSupportedCdsConcepts();
    }

    public SupportedScheduleAuthorities getSupportedScheduleAuthorities()
    {
        return this.supportedScheduleAuthorities;
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
            if (lCdsListItemName != null && !supportedCdsLists.cdsListItemExists(lCdsListItemName))
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

            this.supportedSeries.addSupportedSeriesItemFromProperties(s);
        }, () -> log.warn(_METHODNAME + "Series object not specified; read of supporting data properties skipped"));
    }
}
