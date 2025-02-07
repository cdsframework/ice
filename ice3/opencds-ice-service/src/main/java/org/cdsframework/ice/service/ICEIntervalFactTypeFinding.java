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

package org.cdsframework.ice.service;

import org.kie.api.definition.type.ClassReactive;


@ClassReactive

public class ICEIntervalFactTypeFinding extends ICEFactTypeFinding {
	private TargetDose currentTargetDose;
	private IntervalFactType intervalFactType;

	public enum IntervalFactType {
		EVALUATION,
		EARLIEST_RECOMMENDED,
		RECOMMENDED,
		LATEST_RECOMMENDED
	}

	/**
	 * Evaluation Interval Fact Type
	 * @param pIceResultFinding Finding
	 * @param pPreviousTargetDose Previous Shot (Interval from)
	 * @param pTargetDose Current Shot (Interval to)
	 */
	public ICEIntervalFactTypeFinding(String pIceResultFinding, TargetDose pPreviousTargetDose, TargetDose pTargetDose) {
		super(pIceResultFinding, pPreviousTargetDose);
		this.currentTargetDose = pTargetDose;
		this.intervalFactType = IntervalFactType.EVALUATION;
	}

	/**
	 * Recommendation Interval Fact Type
	 * @param pIceResultFinding Finding
	 * @param pTargetDose Shot to Recommend from
	 */
	public ICEIntervalFactTypeFinding(String pIceResultFinding, TargetDose pTargetDose) {
		super(pIceResultFinding);
		this.currentTargetDose = pTargetDose;
		this.intervalFactType = IntervalFactType.RECOMMENDED;
	}

	public IntervalFactType getIntervalFactType() {
		return this.intervalFactType;
	}

	public TargetDose getAssociatedPreviousTargetDose() {
		return super.getTargetDose();
	}

	public TargetDose getAssociatedCurrentTargetDose() {
		return currentTargetDose;
	}

}
