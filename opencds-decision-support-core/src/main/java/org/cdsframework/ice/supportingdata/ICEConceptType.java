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

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ICEConceptType
{
    DISEASE("SUPPORTED_DISEASE_CONCEPT"),
    DISEASE_IMMUNITY_REASON("DISEASE_IMMUNITY_REASON_CONCEPT"),
    DISEASE_IMMUNITY_SOURCE("DISEASE_IMMUNITY_SOURCE_CONCEPT"),
    EVALUATION_STATUS("EVALUATION_STATUS_CONCEPT"),
    EVALUATION_REASON("EVALUATION_REASON_CONCEPT"),
    SUPPLEMENTAL_EVALUATION_REASON("SUPPLEMENTAL_EVALUATION_REASON_CONCEPT"),
    FACT("SUPPORTED_FACT_CONCEPT"),
    IMMUNITY("SUPPORTED_IMMUNITY_CONCEPT"),
    OPENCDS("OPENCDS_CDM"),
    PERSON("SUPPORTED_PERSON_CONCEPT"),
    RECOMMENDATION_STATUS("RECOMMENDATION_STATUS_CONCEPT"),
    RECOMMENDATION_REASON("RECOMMENDATION_REASON_CONCEPT"),
    SUPPLEMENTAL_RECOMMENDATION_REASON("SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT"),
    SEASON("SUPPORTED_SEASON"),
    SERIES("SUPPORTED_SERIES"),
    SERIES_DISPLAY_SELECTION_TYPE("SERIES_DISPLAY_SELECTION_TYPE"),
    SERIES_DISPLAY_OPTIONS("SERIES_DISPLAY_OPTIONS"),
    VACCINE("SUPPORTED_VACCINES"),
    VACCINE_GROUP("VACCINE_GROUP_CONCEPT");

    /**
     * Return SupportedEvaluationConcept for the specified concept code; null if no associated SupportedEvaluationConcept exists
     */
    public static ICEConceptType getSupportedIceConceptType(final String pIceConceptType)
    {
        if (pIceConceptType == null)
            return null;

        return Arrays.stream(values()).filter(vc -> pIceConceptType.equals(vc.getIceConceptTypeValue())).findFirst().orElse(null);
    }

    private final String iceConceptTypeValue;
}
