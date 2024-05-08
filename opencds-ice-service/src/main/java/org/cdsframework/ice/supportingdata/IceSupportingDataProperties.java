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

package org.cdsframework.ice.supportingdata;

import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record IceSupportingDataProperties(Boolean outputEarliestOverdueDates, Boolean doseOverrideFeatureEnabled, Boolean outputSupplementalText,
                                          List<String> vaccineGroupExclusions, Boolean enableUnsupportedVaccinesGroup, Boolean disableCovid19DoseNumberReset) {
    
    private static final Logger logger = LogManager.getLogger();

    public static IceSupportingDataProperties create(Properties props) {
        String _METHODNAME = "create(Properties): ";

        String lOutputEarliestOverdueDates = props.getProperty("output_earliest_and_overdue_dates");
        final boolean outputEarliestOverdueDates = lOutputEarliestOverdueDates != null && lOutputEarliestOverdueDates.equals("Y");
        if (logger.isInfoEnabled()) {
            logger.info(_METHODNAME + "output_earliest_and_overdue_dates set to " + outputEarliestOverdueDates);
        }

        // Permit Dose Override by client?
        String lEnableDoseOverrideFeature = props.getProperty("enable_dose_override_feature");
        final boolean doseOverrideFeatureEnabled = lEnableDoseOverrideFeature != null && lEnableDoseOverrideFeature.equals("Y");
        if (logger.isInfoEnabled()) {
            logger.info(_METHODNAME + "enable_dose_override_feature set to " + doseOverrideFeatureEnabled);
        }

        // Output Supplemental Text, when available?
        String lOutputSupplementalText = props.getProperty("output_supplemental_text");
        final boolean outputSupplementalText = lOutputSupplementalText != null && lOutputSupplementalText.equals("Y");
        if (logger.isInfoEnabled()) {
            logger.info(_METHODNAME + "output_supplemental_text set to " + outputSupplementalText);
        }

        // Determine vaccine group exclusions
        String vaccineGroupExclusionsProp = props.getProperty("vaccine_group_exclusions");
        final List<String> vaccineGroupExclusions =
                vaccineGroupExclusionsProp == null ? List.of() : List.of(vaccineGroupExclusionsProp.replaceAll("\\s+", "").split("\\,"));
        logger.info("Vaccine Group Exclusions: " + vaccineGroupExclusions);

        // Enable/Disable unsupported vaccines group
        String enableUnsupportedVaccinesGroupProp = props.getProperty("enable_unsupported_vaccines_group");
        final boolean enableUnsupportedVaccinesGroup = enableUnsupportedVaccinesGroupProp != null && enableUnsupportedVaccinesGroupProp.equals("Y");
        logger.info("Enable Unsupported Vaccines Group: " + enableUnsupportedVaccinesGroup);

        // Reset Dose Numbering for COVID-19
        String lEnableCovid19DoseNumberReset = props.getProperty("disable_covid19_sep2023_dose_number_reset");
        final boolean disableCovid19DoseNumberReset = lEnableCovid19DoseNumberReset != null && lEnableCovid19DoseNumberReset.equals("Y");
        if (logger.isInfoEnabled()) {
            logger.info(_METHODNAME + "enable_covid19_sep2023_dose_number_reset set to " + disableCovid19DoseNumberReset);
        }

        return new IceSupportingDataProperties(outputEarliestOverdueDates, doseOverrideFeatureEnabled, outputSupplementalText, vaccineGroupExclusions,
                enableUnsupportedVaccinesGroup, disableCovid19DoseNumberReset);
    }
}
