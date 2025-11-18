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

import java.util.ArrayList;
import java.util.List;

import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.supportingdata.ICEPropertiesDataConfiguration;
import org.cdsframework.ice.supportingdata.IceSupportingDataProperties;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.opencds.config.api.model.KMId;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.PreProcessPlugin;
import org.opencds.plugin.SupportingData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ICESupportingDataLoaderPlugin implements PreProcessPlugin
{
    // AI: determine supportingData.identifier dynamically (fix upon OpenCDS upgrade)
    private static final String SD_ICE = "ice-supporting-data";

    private static final IceSupportingDataProperties iceProps =
            IceSupportingDataProperties.create(new ICEPropertiesDataConfiguration().getProperties());

    public static SupportingData getSupportingData(final PreProcessPluginContext context)
    {
        return context.getSupportingData().get(SD_ICE);
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
            log.info("Loading immunization schedule for Knowledge Module: {}", lKMId);
            loadImmunizationSchedule(sd, cache);
            log.info(_METHODNAME + "Immunization schedule loaded for knowledge module: {}", lKMId);

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

        context.getGlobals().put("patientAgeTimeOfInterest", null);

        if (iceProps.outputEarliestOverdueDates() == null)
        {
            final String lErrStr =
                    "An error occurred: knowledge module not properly initialized: output earliest/overdue flag not set; this should not happen. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.getGlobals().put("outputEarliestOverdueDates", iceProps.outputEarliestOverdueDates());

        if (iceProps.doseOverrideFeatureEnabled() == null)
        {
            final String lErrStr =
                    "An error occurred: knowledge module not properly initialized: dose override flag not set; this should not happen. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.getGlobals().put("doseOverrideFeatureEnabled", iceProps.doseOverrideFeatureEnabled());

        if (iceProps.outputSupplementalText() == null)
        {
            final String lErrStr =
                    "An error occurred: knowledge module not properly initialized: dose override flag not set; this should not happen. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.getGlobals().put("outputSupplementalText", iceProps.outputSupplementalText());

        context.getGlobals().put("vaccineGroupExclusions", iceProps.vaccineGroupExclusions());

        if (iceProps.enableUnsupportedVaccinesGroup() == null)
        {
            final String lErrStr =
                    "An error occurred: knowledge module not properly initialized: unsupported vaccine group flag not set; this should not happen. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.getGlobals().put("enableUnsupportedVaccinesGroup", iceProps.enableUnsupportedVaccinesGroup());

        if (iceProps.disableCovid19DoseNumberReset() == null)
        {
            final String lErrStr =
                    "An error occurred: knowledge module not properly initialized: enableCovid19DoseNumberReset flag not set; this should not happen. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.getGlobals().put("disableCovid19DoseNumberReset", iceProps.disableCovid19DoseNumberReset());

        if (iceProps.disableOutputEarliestOverdueDatesForPneumococcalAdultSeries() == null)
        {
            final String lErrStr =
                    "An error occurred: knowledge module not properly initialized: disableOutputEarliestOverdueDatesForPneumococcalAdultSeries flag not set; this should not happen. Cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        context.getGlobals()
                .put("disableOutputEarliestOverdueDatesForPneumococcalAdultSeries",
                        iceProps.disableOutputEarliestOverdueDatesForPneumococcalAdultSeries());
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
        final ICEPropertiesDataConfiguration iceProps = new ICEPropertiesDataConfiguration();
        final String lBaseRulesScopingKmId =
                KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(iceProps.getBaseRulesScopingEntityId(),
                        lRequestedKMIdObject.getBusinessId(), iceProps.getBaseRulesVersion());

        // Initialize schedule
        log.info("Initializing Schedule");
        final List<String> cdsVersions = new ArrayList<>();
        final String lRequestedKmIdStr =
                pRequestedKMIdStr.equals("org.nyc.cir^ICE^1.0.0") ? "gov.nyc.cir^ICE^1.0.0" : pRequestedKMIdStr;
        cdsVersions.add(lRequestedKmIdStr);
        try
        {
            s = new Schedule("requestedKmId", lBaseRulesScopingKmId, iceProps.getKnowledgeCommonDirectory(), cdsVersions,
                    iceProps.getKnowledgeModulesDirectory());
        }
        catch (final IllegalArgumentException | InconsistentConfigurationException ii)
        {
            final String lErrStr = "Failed to initialize immunization schedule";
            log.error(_METHODNAME + lErrStr, ii);
            throw new RuntimeException(lErrStr, ii);
        }
        log.info("Schedule Initialization complete");

        // Store Schedule into cache
        pCache.put(supportingData, s);
    }
}
