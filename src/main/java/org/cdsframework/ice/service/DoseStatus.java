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

import org.cdsframework.ice.supportingdata.BaseData;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DoseStatus implements BaseData
{
    ACCEPTED("EVALUATION_STATUS_CONCEPT.ACCEPTED"),
    PRIMARY_SHOT_DETERMINATION_IN_PROCESS("EVALUATION_STATUS_CONCEPT.PRIMARY_SHOT_DETERMINATION_IN_PROCESS"),
    EVALUATION_IN_PROCESS("EVALUATION_STATUS_CONCEPT.EVALUATION_IN_PROCESS"),
    EVALUATION_COMPLETE("EVALUATION_STATUS_CONCEPT.EVALUATION_COMPLETE"),
    EVALUATION_NOT_STARTED("EVALUATION_STATUS_CONCEPT.EVALUATION_NOT_STARTED"),
    NOT_EVALUATED("EVALUATION_STATUS_CONCEPT.NOT_EVALUATED"),
    INVALID("EVALUATION_STATUS_CONCEPT.INVALID"),
    VALID("EVALUATION_STATUS_CONCEPT.VALID"),
    DECISION_RENDERED("EVALUATION_STATUS_CONCEPT.DECISION_RENDERED");

    private final String cdsListItemName;
}
