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

import java.nio.file.Path;
import java.util.ArrayList;
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
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.opencds.config.api.model.KMId;
import org.opencds.plugin.api.PluginDataCache;
import org.opencds.plugin.api.PreProcessPlugin;
import org.opencds.plugin.api.PreProcessPluginContext;
import org.opencds.plugin.api.SupportingData;
import org.springframework.util.StringUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ICESupportingDataLoaderPlugin implements PreProcessPlugin
{
    // AI: determine supportingData.identifier dynamically (fix upon OpenCDS upgrade)
    private static final String SD_ICE = "ice-supporting-data";
    // Caches per KM ID so we only compute once per knowledge module
    private static final Map<String, Map<String, String>> cachedNumericToKeyByKmId = new HashMap<>();
    private static final Map<String, String> cachedRawSignatureByKmId = new HashMap<>();
    private static final Map<String, List<String>> cachedNormalizedExclusionsByKmId = new HashMap<>();
    @Setter
    private static IceProperties iceProperties;
    @Setter
    private static Path configPath;

    public static SupportingData getSupportingData(final PreProcessPluginContext context)
    {
        return context.getSupportingData().get(SD_ICE);
    }

    private static synchronized List<String> normalizeVaccineGroupExclusionsForSchedule(final List<String> vaccineGroupInclusions,
            final List<String> vaccineGroupExclusions, final Schedule schedule, final String kmId)
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

        final PluginDataCache cache = context.getCache();

        final SupportingData sd = getSupportingData(context);
        if (sd == null)
        {
            final String lErrStr = "SupportingData not found";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        final String lKMId = sd.getKmId();
        Schedule schedule = cache.get(sd);
        if (schedule == null)
        {
            // Schedule has not been stored in supporting data - load it - This should only happen once.
            log.debug("Loading immunization schedule for Knowledge Module: {}", lKMId);
            loadImmunizationSchedule(sd, cache);
            log.debug(_METHODNAME + "Immunization schedule loaded for knowledge module: {}", lKMId);

            schedule = cache.get(sd);
        }
        else
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Immunization schedule previously loaded");

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

        context.getGlobals().put("schedule", schedule);

        //context.getGlobals().put("patientAgeTimeOfInterest", null);

        context.getGlobals().put("outputEarliestOverdueDates", iceProperties.getOutputEarliestAndOverdueDates());

        context.getGlobals().put("doseOverrideFeatureEnabled", iceProperties.getEnableDoseOverrideFeature());

        context.getGlobals().put("outputSupplementalText", iceProperties.getOutputSupplementalText());

        context.getGlobals()
                .put("vaccineGroupExclusions", normalizeVaccineGroupExclusionsForSchedule(iceProperties.getVaccineGroupInclusions(),
                        iceProperties.getVaccineGroupExclusions(), schedule, lKMId));

        context.getGlobals().put("enableUnsupportedVaccinesGroup", iceProperties.getEnableUnsupportedVaccinesGroup());

        context.getGlobals().put("disableCovid19DoseNumberReset", iceProperties.getDisableCovid19Sep2023DoseNumberReset());
    }

    /**
     * Given an ICE knowledge module identifier in the correct format, load its corresponding Schedule into the provided cache
     */
    private synchronized void loadImmunizationSchedule(final SupportingData supportingData, final PluginDataCache pCache)
    {
        final String _METHODNAME = "loadImmunizationSchedule(): ";

        if (pCache == null)
        {
            final String lErrStr = "PluginDataCache parameter not specified";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        Schedule s = pCache.get(supportingData);
        if (s != null)
        {
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Immunization schedule already loaded");
            // Schedule is already loaded
            return;
        }

        final String pRequestedKMIdStr = supportingData.getKmId();

        // Determine requested KM ID Object
        final KMId lRequestedKMIdObject = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(pRequestedKMIdStr);
        if (lRequestedKMIdObject == null)
        {
            final String lErrStr = "Invalid knowledge module provided; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        // Determine base rules KM ID in String format
        final String lBaseRulesScopingKmId =
                KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(iceProperties.getIceBaseRulesScopingEntityId(),
                        lRequestedKMIdObject.getBusinessId(), iceProperties.getIceBaseRulesVersion());

        // Initialize schedule
        log.debug("Initializing Schedule");
        final List<String> cdsVersions = new ArrayList<>();
        final String lRequestedKmIdStr =
                pRequestedKMIdStr.equals("org.nyc.cir^ICE^1.0.0") ? "gov.nyc.cir^ICE^1.0.0" : pRequestedKMIdStr;
        cdsVersions.add(lRequestedKmIdStr);
        try
        {
            s = new Schedule("requestedKmId", lBaseRulesScopingKmId, configPath.resolve("knowledgeCommon"), cdsVersions,
                    configPath.resolve("knowledgeModule"), iceProperties.getSupplementalTextMode());
        }
        catch (final IllegalArgumentException | InconsistentConfigurationException ii)
        {
            final String lErrStr = "Failed to initialize immunization schedule";
            log.error(_METHODNAME + lErrStr, ii);
            throw new RuntimeException(lErrStr, ii);
        }
        log.debug("Schedule Initialization complete");

        // Store Schedule into cache
        pCache.put(supportingData, s);
    }

}
