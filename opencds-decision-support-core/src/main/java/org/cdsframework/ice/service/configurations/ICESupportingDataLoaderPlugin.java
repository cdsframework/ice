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

package org.cdsframework.ice.service.configurations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.service.SupportingDataService;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.opencds.config.api.model.KMId;
import org.opencds.plugin.api.OpencdsPlugin;
import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.api.PreProcessPluginContext;
import org.opencds.plugin.api.SupportingData;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ICESupportingDataLoaderPlugin implements OpencdsPlugin<PreProcessPluginContext>
{
    // Caches per KM ID so we only compute once per knowledge module
    private static final Map<String, Map<String, String>> cachedNumericToKeyByKmId = new HashMap<>();
    private static final Map<String, String> cachedRawSignatureByKmId = new HashMap<>();
    private static final Map<String, List<String>> cachedNormalizedExclusionsByKmId = new HashMap<>();

    private static final Map<String, Schedule> preloadedSchedules = new HashMap<>();

    @Setter
    private static IceProperties iceProperties;
    @Setter
    private static SupportingDataService supportingDataService;

    private static synchronized List<String> normalizeVaccineGroupExclusionsForSchedule(final List<String> vaccineGroupExclusions,
            final List<String> vaccineGroupInclusions, final Schedule schedule, final String kmId)
    {
        // nothing to do if no inclusions or exclusions
        if ((vaccineGroupInclusions.isEmpty() && vaccineGroupExclusions.isEmpty()) || schedule == null)
            return List.of();

        // normalize and validate inclusions and exclusions
        if (!vaccineGroupInclusions.isEmpty() && !vaccineGroupExclusions.isEmpty())
        {
            final String lErrStr = "Both vaccine group inclusions and exclusions specified; cannot continue";
            log.error(lErrStr);
            throw new ICECoreError(lErrStr);
        }

        final List<String> filteredInclusions = vaccineGroupInclusions.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .toList();
        final List<String> filteredExclusions = vaccineGroupExclusions.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .toList();

        if (!filteredInclusions.isEmpty())
            log.debug("Vaccine group inclusions: {}", filteredInclusions);

        if (!filteredExclusions.isEmpty())
            log.debug("Vaccine group exclusions: {}", filteredExclusions);

        if (!filteredInclusions.isEmpty()
                && filteredInclusions.stream().filter(inclusion -> inclusion.startsWith("HEP_")).count() == 1)
        {
            final String lErrStr =
                    "Inclusion of vaccine group HEP_A or HEP_B requires inclusion of both HEP_A and HEP_B; cannot continue";
            log.error(lErrStr);
            throw new ICECoreError(lErrStr);
        }

        if (!filteredExclusions.isEmpty()
                && filteredExclusions.stream().filter(exclusion -> exclusion.startsWith("HEP_")).count() == 1)
        {
            final String lErrStr =
                    "Exclusion of vaccine group HEP_A or HEP_B requires exclusion of both HEP_A and HEP_B; cannot continue";
            log.error(lErrStr);
            throw new ICECoreError(lErrStr);
        }

        // check if we have already processed this combination of inclusions and exclusions and cached the result
        final String sig = String.join("+", String.join("|", filteredExclusions), String.join("|", filteredInclusions));
        final String prev = cachedRawSignatureByKmId.get(kmId);
        if (prev != null && prev.equals(sig))
        {
            final List<String> cachedExclusions = cachedNormalizedExclusionsByKmId.get(kmId);
            if (cachedExclusions != null)
                return cachedExclusions;
        }

        // build vaccine group name map (CDS item key -> CDS item name)
        final Map<String, String> vaccineGroupNameMap =
                cachedNumericToKeyByKmId.computeIfAbsent(kmId, _ -> buildVaccineGroupNameMap(schedule));

        // check for unknown inclusions and exclusions
        if (!vaccineGroupInclusions.isEmpty())
        {
            final List<String> unknownInclusions =
                    filteredInclusions.stream().filter(inclusion -> !vaccineGroupNameMap.containsKey(inclusion)).toList();
            if (!unknownInclusions.isEmpty())
            {
                final String lErrStr = "Unknown vaccine group inclusions specified: " + unknownInclusions;
                log.error(lErrStr);
                throw new ICECoreError(lErrStr);
            }
        }

        if (!vaccineGroupExclusions.isEmpty())
        {
            final List<String> unknownExclusions =
                    filteredExclusions.stream().filter(exclusion -> !vaccineGroupNameMap.containsKey(exclusion)).toList();
            if (!unknownExclusions.isEmpty())
            {
                final String lErrStr = "Unknown vaccine group exclusions specified: " + unknownExclusions;
                log.error(lErrStr);
                throw new ICECoreError(lErrStr);
            }
        }

        // build exclusions list
        final List<String> exclusions = vaccineGroupNameMap.entrySet()
                .stream()
                .filter(e -> filteredInclusions.isEmpty() || !filteredInclusions.contains(e.getKey()))
                .filter(e -> filteredExclusions.isEmpty() || filteredExclusions.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        cachedRawSignatureByKmId.put(kmId, sig);
        cachedNormalizedExclusionsByKmId.put(kmId, exclusions);

        return exclusions;
    }

    private static Map<String, String> buildVaccineGroupNameMap(final Schedule schedule)
    {
        try
        {
            return Optional.ofNullable(
                            schedule.getSupportedCdsLists().getCdsListItemsAssociatedWithCdsListCode("VACCINE_GROUP_CONCEPT"))
                    .map(items -> items.stream()
                            .collect(Collectors.toMap(LocallyCodedCdsListItem::getCdsListItemKey,
                                    LocallyCodedCdsListItem::getCdsListItemName, (existing, _) -> existing)))
                    .orElse(new HashMap<>());
        }
        catch (final Exception e)
        {
            log.error("Failed building vaccine group numeric-to-key map", e);
            return new HashMap<>();
        }
    }

    public static synchronized void preloadSchedules()
    {
        if (iceProperties == null)
        {
            log.warn("Cannot preload schedules: properties not initialized");
            return;
        }

        if (supportingDataService == null)
        {
            log.warn("Cannot preload schedules: SupportingDataService not initialized");
            return;
        }

        supportingDataService.getKnowledgeModulePropertiesByKmId().forEach((kmId, properties) ->
        {
            if (preloadedSchedules.containsKey(kmId))
                return;

            log.info("Preloading immunization schedule for Knowledge Module: {}", kmId);
            try
            {
                preloadedSchedules.put(kmId, new ICESupportingDataLoaderPlugin().loadImmunizationSchedule(kmId, properties));
            }
            catch (final Exception e)
            {
                log.error("Failed to preload immunization schedule for Knowledge Module: {}", kmId, e);
            }
        });
    }

    @Override
    public void execute(final PreProcessPluginContext context)
    {
        final String _METHODNAME = "execute(PreProcessPluginContext): ";
        if (context == null)
        {
            final String lErrStr = "PreProcessPluginContext not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (ObjectUtils.isEmpty(context.supportingData()))
        {
            final String lErrStr = "SupportingData not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (context.supportingData().size() > 1)
        {
            final String lErrStr = "Invalid SupportingData size: " + context.supportingData().size();
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final SupportingData sd = context.supportingData().values().iterator().next();

        final PluginDataCache cache = context.cache();

        final String lKMId = sd.kmId();

        if (supportingDataService == null)
        {
            final String lErrStr = "SupportingDataService not initialized";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalStateException(lErrStr);
        }

        final IceProperties.KnowledgeModuleProperties knowledgeModuleProperties =
                supportingDataService.getKnowledgeModulePropertiesForKmId(lKMId);

        Schedule schedule;
        synchronized (cache)
        {
            schedule = cache.get(sd);
            if (schedule == null)
            {
                // check for preloaded schedule
                schedule = preloadedSchedules.get(lKMId);
                if (schedule != null)
                {
                    log.debug("Found preloaded immunization schedule for Knowledge Module: {}", lKMId);
                    cache.put(sd, schedule);
                }
                else
                {
                    // Schedule has not been stored in supporting data - load it - This should only happen once.
                    log.debug("Loading immunization schedule for Knowledge Module: {}", lKMId);
                    schedule = loadImmunizationSchedule(lKMId, knowledgeModuleProperties);
                    log.debug(_METHODNAME + "Immunization schedule loaded for knowledge module: {}", lKMId);

                    cache.put(sd, schedule);
                }
            }
            else
                if (log.isDebugEnabled())
                    log.debug(_METHODNAME + "Immunization schedule previously loaded");
        }

        if (schedule == null)
        {
            // Immunization schedule not loaded
            final String lErrStr =
                    "Immunization schedule not loaded; something went wrong. Incorrect configuration or requested knowledge module ID likely: "
                            + lKMId;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }

        if (!schedule.isScheduleInitialized())
        {
            final String lErrStr = "Schedule has not been fully initialized; something went wrong; cannot process request";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.globals().put("schedule", schedule);

        //context.globals().put("patientAgeTimeOfInterest", null);

        context.globals()
                .put("outputEarliestOverdueDates", iceProperties.getOutputEarliestAndOverdueDates()
                        .orElseGet(knowledgeModuleProperties::outputEarliestAndOverdueDates));

        context.globals()
                .put("outputNumberOfDosesRemaining", iceProperties.getOutputNumberOfDosesRemaining()
                        .orElseGet(knowledgeModuleProperties::outputNumberOfDosesRemaining));

        context.globals()
                .put("outputSeriesInformation",
                        iceProperties.getOutputSeriesInformation().orElseGet(knowledgeModuleProperties::outputSeriesInformation));

        context.globals()
                .put("outputScheduleAuthorities",
                        iceProperties.getOutputScheduleAuthorities().orElseGet(knowledgeModuleProperties::outputScheduleAuthorities));

        context.globals()
                .put("doseOverrideFeatureEnabled", iceProperties.getEnableDoseOverrideFeature()
                        .orElseGet(knowledgeModuleProperties::enableDoseOverrideFeature));

        context.globals()
                .put("outputSupplementalText",
                        iceProperties.getOutputSupplementalText().orElseGet(knowledgeModuleProperties::outputSupplementalText));

        context.globals()
                .put("outputVaccineGroupRulesArtifact", iceProperties.getOutputVaccineGroupRulesArtifact()
                        .orElseGet(knowledgeModuleProperties::outputVaccineGroupRulesArtifact));

        context.globals()
                .put("vaccineGroupExclusions", normalizeVaccineGroupExclusionsForSchedule(
                        Optional.ofNullable(iceProperties.getVaccineGroupExclusions())
                                .orElseGet(knowledgeModuleProperties::vaccineGroupExclusions),
                        Optional.ofNullable(iceProperties.getVaccineGroupInclusions())
                                .orElseGet(knowledgeModuleProperties::vaccineGroupInclusions), schedule, lKMId));

        context.globals()
                .put("enableUnsupportedVaccinesGroup", iceProperties.getEnableUnsupportedVaccinesGroup()
                        .orElseGet(knowledgeModuleProperties::enableUnsupportedVaccinesGroup));

        context.globals()
                .put("disableCovid19DoseNumberReset", iceProperties.getDisableCovid19Sep2023DoseNumberReset()
                        .orElseGet(knowledgeModuleProperties::disableCovid19Sep2023DoseNumberReset));
    }

    /**
     * Given an ICE knowledge module identifier in the correct format, load its corresponding Schedule into the provided cache
     */
    protected synchronized Schedule loadImmunizationSchedule(final String kmId,
            final IceProperties.KnowledgeModuleProperties knowledgeModuleProperties)
    {
        final String _METHODNAME = "loadImmunizationSchedule(): ";

        // Determine requested KM ID Object
        final KMId lRequestedKMIdObject = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(kmId);
        if (lRequestedKMIdObject == null)
        {
            final String lErrStr = "Invalid knowledge module provided; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        // Initialize schedule
        log.debug("Initializing Schedule");

        final Schedule s;
        try
        {
            if (supportingDataService == null)
                throw new IllegalStateException("SupportingDataService not initialized");

            s = new Schedule("requestedKmId", supportingDataService.getBaseKnowledgeModuleId(), List.of(kmId),
                    supportingDataService,
                    iceProperties.getSupplementalTextMode().orElse(knowledgeModuleProperties.supplementalTextMode()));
        }
        catch (final IllegalArgumentException | IllegalStateException | InconsistentConfigurationException ii)
        {
            final String lErrStr = "Failed to initialize immunization schedule";
            log.error(_METHODNAME + lErrStr, ii);
            throw new RuntimeException(lErrStr, ii);
        }
        log.debug("Schedule Initialization complete");

        return s;
    }
}
