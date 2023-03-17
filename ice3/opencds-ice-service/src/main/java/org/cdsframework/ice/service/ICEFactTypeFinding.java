/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
public class ICEFactTypeFinding {

	private String iceResultFinding;
	private TargetDose associatedTargetDose;
	private TargetSeries associatedTargetSeries;
	
	public ICEFactTypeFinding(String pIceResultFinding) {
		this.iceResultFinding = pIceResultFinding;
	}
	
	public ICEFactTypeFinding(String pIceResultFinding, TargetDose pTargetDose) {
		this.iceResultFinding = pIceResultFinding;
		this.associatedTargetDose = pTargetDose;
		this.associatedTargetSeries = pTargetDose != null ? pTargetDose.getAssociatedTargetSeries() : null;
	}

	public ICEFactTypeFinding(String pIceResultFinding, TargetSeries pTargetSeries) {
		this.iceResultFinding = pIceResultFinding;
		this.associatedTargetSeries = pTargetSeries;
	}

	public String getIceResultFinding() {
		return iceResultFinding;
	}
	
	public TargetDose getAssociatedTargetDose() {
		return associatedTargetDose;
	}
	
	/**
	 * Invokes getAssociatedTargetDose()
	 * @return TargetDose associated with the IceFactTypeFinding, if any
	 */
	public TargetDose getTargetDose() {
		return associatedTargetDose;
	}
	
	public TargetSeries getAssociatedTargetSeries() {
		return associatedTargetSeries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((iceResultFinding == null) ? 0 : iceResultFinding.hashCode());
		result = prime * result
				+ ((associatedTargetDose == null) ? 0 : associatedTargetDose.hashCode());
		result = prime * result
				+ ((associatedTargetSeries == null) ? 0 : associatedTargetSeries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ICEFactTypeFinding))
			return false;
		ICEFactTypeFinding other = (ICEFactTypeFinding) obj;
		if (iceResultFinding == null) {
			if (other.iceResultFinding != null)
				return false;
		} else if (!iceResultFinding.equals(other.iceResultFinding))
			return false;
		if (associatedTargetDose == null) {
			if (other.associatedTargetDose != null)
				return false;
		} else if (!associatedTargetDose.equals(other.associatedTargetDose))
			return false;
		if (associatedTargetSeries == null) {
			if (other.associatedTargetSeries != null)
				return false;
		} else if (!associatedTargetSeries.equals(other.associatedTargetSeries))
			return false;
		return true;
	}
}

