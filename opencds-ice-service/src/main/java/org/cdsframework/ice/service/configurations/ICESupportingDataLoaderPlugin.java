/**
 * Copyright (C) 2024 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service.configurations;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.supportingdata.ICEPropertiesDataConfiguration;
import org.cdsframework.ice.supportingdata.IceSupportingDataProperties;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.config.api.model.KMId;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.PreProcessPlugin;
import org.opencds.plugin.SupportingData;

public class ICESupportingDataLoaderPlugin implements PreProcessPlugin {
	private static final Logger logger = LogManager.getLogger();

	// AI: determine supportingData.identifier dynamically (fix upon OpenCDS upgrade)
	private static final String SD_ICE = "ice-supporting-data";

	private static final IceSupportingDataProperties iceProps = IceSupportingDataProperties.create(new ICEPropertiesDataConfiguration().getProperties());

	@Override
	public void execute(PreProcessPluginContext context) {
		String _METHODNAME = "execute(PreProcessPluginContext): ";
		if (context == null) {
			String lErrStr = "PreProcessPluginContext not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}
		PluginDataCache cache = context.getCache();

		SupportingData sd = getSupportingData(context);
		if (sd == null) {
			String lErrStr = "SupportingData not found";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

		String lKMId = sd.getKmId();
		Schedule schedule = cache.get(sd);
		if (schedule == null) {
			// Schedule has not been stored in supporting data - load it - This should only happen once.
			logger.info("Loading immunization schedule for Knowledge Module: " + lKMId);
			loadImmunizationSchedule(sd, cache);
			logger.info(_METHODNAME + "Immunization schedule loaded for knowledge module: " + lKMId);

			schedule = cache.get(sd);
		}
		else
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "Immunization schedule previously loaded");
			}

		if (schedule == null) {
			// Immunization schedule not loaded
			String lErrStr = "Immunization schedule not loaded; something went wrong. Incorrect configuration or requested knowledge module ID likely: " + lKMId;
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		if (!schedule.isScheduleInitialized()) {
			String lErrStr = "Schedule has not been fully initialized; something went wrong; cannot process request";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		context.getGlobals().put("schedule", schedule);

		context.getGlobals().put("patientAgeTimeOfInterest", null);

		if (iceProps.outputEarliestOverdueDates() == null) {
			String lErrStr =
					"An error occurred: knowledge module not properly initialized: output earliest/overdue flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		context.getGlobals().put("outputEarliestOverdueDates", iceProps.outputEarliestOverdueDates());

		if (iceProps.doseOverrideFeatureEnabled() == null) {
			String lErrStr = "An error occurred: knowledge module not properly initialized: dose override flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		context.getGlobals().put("doseOverrideFeatureEnabled", iceProps.doseOverrideFeatureEnabled());

		if (iceProps.outputSupplementalText() == null) {
			String lErrStr = "An error occurred: knowledge module not properly initialized: dose override flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		context.getGlobals().put("outputSupplementalText", iceProps.outputSupplementalText());

		context.getGlobals().put("vaccineGroupExclusions", iceProps.vaccineGroupExclusions());

		if (iceProps.enableUnsupportedVaccinesGroup() == null) {
			String lErrStr =
					"An error occurred: knowledge module not properly initialized: unsupported vaccine group flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		context.getGlobals().put("enableUnsupportedVaccinesGroup", iceProps.enableUnsupportedVaccinesGroup());

		if (iceProps.disableCovid19DoseNumberReset() == null) {
			String lErrStr =
					"An error occurred: knowledge module not properly initialized: enableCovid19DoseNumberReset flag not set; this should not happen. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		context.getGlobals().put("disableCovid19DoseNumberReset", iceProps.disableCovid19DoseNumberReset());
	}

	public static SupportingData getSupportingData(PreProcessPluginContext context) {
		return context.getSupportingData().get(SD_ICE);
	}

	/**
	 * Given an ICE knowledge module identifier in the correct format, load its corresponding Schedule into the provided cache
	 */
	private synchronized void loadImmunizationSchedule(SupportingData supportingData, PluginDataCache pCache) {

		String _METHODNAME = "loadImmunizationSchedule(): ";

		if (pCache == null) {
			String lErrStr = "PluginDataCache parameter not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

		Schedule s = pCache.get(supportingData);
		if (s != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + "Immunization schedule already loaded");
			}
			// Schedule is already loaded
			return;
		}

		String pRequestedKMIdStr = supportingData.getKmId();

		// Determine requested KM ID Object
		KMId lRequestedKMIdObject = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(pRequestedKMIdStr);
		if (lRequestedKMIdObject == null) {
			String lErrStr = "Invalid knowledge module provided; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}

		// Determine base rules KM ID in String format
		ICEPropertiesDataConfiguration iceProps = new ICEPropertiesDataConfiguration();
		String lBaseRulesScopingKmId =
				KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(iceProps.getBaseRulesScopingEntityId(), lRequestedKMIdObject.getBusinessId(),
						iceProps.getBaseRulesVersion());

		// Initialize schedule
		logger.info("Initializing Schedule");
		List<String> cdsVersions = new ArrayList<String>();
		String lRequestedKmIdStr = (pRequestedKMIdStr != null && pRequestedKMIdStr.equals("org.nyc.cir^ICE^1.0.0")) ? "gov.nyc.cir^ICE^1.0.0" : pRequestedKMIdStr;
		cdsVersions.add(lRequestedKmIdStr);
		try {
			s = new Schedule("requestedKmId", lBaseRulesScopingKmId, iceProps.getKnowledgeCommonDirectory(), cdsVersions, iceProps.getKnowledgeModulesDirectory());
		}
		catch (ImproperUsageException | InconsistentConfigurationException ii) {
			String lErrStr = "Failed to initialize immunization schedule";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		logger.info("Schedule Initialization complete");

		// Store Schedule into cache
		pCache.put(supportingData, s);
	}
}
