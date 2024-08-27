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

import java.util.Objects;

import org.kie.api.definition.type.ClassReactive;

@ClassReactive
public class SeriesDisplaySelection {

	private String uniqueId;
	private TargetSeries targetSeries;
	private SeriesDisplaySelectionType seriesSelectionDisplayType;
	private String numberOfDosesRemaining;
	private boolean seriesSelectionDisplayDeterminationComplete;

	public enum SeriesDisplaySelectionType {
		SERIES_DISPLAY_NOT_SELECTED, SERIES_DISPLAY_UNAMBIGUOUS, SERIES_DISPLAY_BEST_GUESS, SERIES_DISPLAY_ALTERNATIVE, SERIES_DISPLAY_NONE
	}


	public SeriesDisplaySelection(TargetSeries pTargetSeries) {
		uniqueId = ICELogicHelper.generateUniqueString();
		this.targetSeries = pTargetSeries;
		this.seriesSelectionDisplayType = SeriesDisplaySelectionType.SERIES_DISPLAY_NOT_SELECTED;
		this.numberOfDosesRemaining = null;
		this.seriesSelectionDisplayDeterminationComplete = false;
	}


	public SeriesDisplaySelection(TargetSeries pTargetSeries, SeriesDisplaySelectionType pSST) {
		uniqueId = ICELogicHelper.generateUniqueString();
		this.targetSeries = pTargetSeries;
		this.seriesSelectionDisplayType = pSST;
		this.numberOfDosesRemaining = null;
		this.seriesSelectionDisplayDeterminationComplete = false;
	}


	public SeriesDisplaySelection(TargetSeries pTargetSeries, SeriesDisplaySelectionType pSST, String pDosesRemaining) {
		this(pTargetSeries, pSST);
		this.numberOfDosesRemaining = pDosesRemaining;
	}


	public String getUniqueId() {
		return this.uniqueId;
	}


	public TargetSeries getTargetSeries() {
		return this.targetSeries;
	}


	public String getSeriesName() {
		return this.targetSeries.getSeriesName();
	}


	public String getVaccineGroup() {
		return this.targetSeries.getVaccineGroup();
	}


	public SeriesDisplaySelectionType getSeriesDisplaySelectionType() {
		return seriesSelectionDisplayType;
	}


	public void setSeriesDisplaySelectionType(SeriesDisplaySelectionType pSST) {
		this.seriesSelectionDisplayType = pSST;
	}


	public String getNumberOfDosesRemaining() {
		return numberOfDosesRemaining;
	}


	public void setNumberOfDosesRemaining(String pDosesRemaining) {
		this.numberOfDosesRemaining = pDosesRemaining;
	}


	public boolean isSeriesDisplaySelectionDeterminationComplete() {
		return this.seriesSelectionDisplayDeterminationComplete;
	}


	public void setSeriesDisplaySelectionDeterminationComplete(boolean pIsDeterminationComplete) {
		this.seriesSelectionDisplayDeterminationComplete = pIsDeterminationComplete;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SeriesDisplaySelection [ targetSeries=\"").append(targetSeries.getSeriesName())
				.append("\", seriesSelectionOutputType=").append(seriesSelectionDisplayType)
				.append(", numberOfDosesRemaining=").append(numberOfDosesRemaining)
				.append(", uniqueId=").append(uniqueId).append(" ]");
		return builder.toString();
	}


	@Override
	public int hashCode() {
		return Objects.hash(uniqueId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeriesDisplaySelection other = (SeriesDisplaySelection) obj;
		return Objects.equals(uniqueId, other.uniqueId);
	}

}
