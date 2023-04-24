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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.definition.type.ClassReactive;


@ClassReactive
public class TargetSeriesSelection {

	public enum SeriesSelectionStatus {
		SERIES_SELECTION_NOT_STARTED, SERIES_SELECTION_IN_PREPROCESS, SERIES_SELECTION_IN_PROCESS, SERIES_SELECTION_IN_POSTPROCESS, SERIES_SELECTION_COMPLETE
	}
	
	private String seriesSelectionVaccineGroup;
	private Season seriesSelectionSeason;
	private SeriesSelectionStatus seriesSelectionStatus;
	private String selectedSeriesName;
	private int seriesSelectionPriority;
	
	private static final Logger logger = LogManager.getLogger();
	
	private TargetSeriesSelection(String seriesSelectionVG, Schedule pSchedule) {
		
		String _METHODNAME = "TargetSeriesSelection(): ";
		if (pSchedule == null) {
			String lErrStr = "Schedule supplied is not initialized";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}
		
		if (! pSchedule.getSupportedVaccineGroups().vaccineGroupItemExists(seriesSelectionVG)) {
			String lErrStr = "specified vaccine group does not exist for this schedule; vaccine group specified: " + seriesSelectionVG;
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}
		else {
			this.seriesSelectionPriority = pSchedule.getSupportedVaccineGroups().getVaccineGroupItem(seriesSelectionVG).getPriority();
		}
		this.seriesSelectionVaccineGroup = seriesSelectionVG;
		this.seriesSelectionSeason = null;
		this.seriesSelectionStatus = SeriesSelectionStatus.SERIES_SELECTION_NOT_STARTED;
		this.selectedSeriesName = null;
	}
	
	/**
	 * Initialize TargetSeriesSelection
	 * @param seriesSelectionVG representing the vaccine group
	 * @param season the season, or null if none
	 * @param schedule the backing schedule
	 * @throws if specified vaccine group or schedule does not exist
	 */
	public TargetSeriesSelection(String seriesSelectionVG, Season season, Schedule schedule) {
		
		this(seriesSelectionVG, schedule);
		this.seriesSelectionSeason = season;
	}
	
	public String getSeriesSelectionVaccineGroup() {
		return seriesSelectionVaccineGroup;
	}

	public void setSeriesSelectionVaccineGroup(String seriesSelection) {
		this.seriesSelectionVaccineGroup = seriesSelection;
	}
	
	public int getSeriesSelectionPriority() {
		return seriesSelectionPriority;
	}

	public Season getSeriesSelectionSeason() {
		return this.seriesSelectionSeason;
	}
	
	public void setSeriesSelectionSeason(Season s) {
		this.seriesSelectionSeason = s;
	}

	public String getSelectedSeriesName() {
		return selectedSeriesName;
	}

	public void setSelectedSeriesName(String selectedSeriesName) {
		this.selectedSeriesName = selectedSeriesName;
	}

	public SeriesSelectionStatus getSeriesSelectionStatus() {
		return seriesSelectionStatus;
	}

	public void setSeriesSelectionStatus(SeriesSelectionStatus seriesSelectionStatus)  {
		
		this.seriesSelectionStatus = seriesSelectionStatus;
	}
	
	
	@Override
	public String toString() {
		return "TargetSeriesSelection [seriesSelectionVaccineGroup="
				+ seriesSelectionVaccineGroup + ", seriesSelectionSeason="
				+ seriesSelectionSeason + ", seriesSelectionStatus="
				+ seriesSelectionStatus + ", selectedSeriesName="
				+ selectedSeriesName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((selectedSeriesName == null) ? 0 : selectedSeriesName
						.hashCode());
		result = prime
				* result
				+ ((seriesSelectionSeason == null) ? 0 : seriesSelectionSeason
						.hashCode());
		result = prime
				* result
				+ ((seriesSelectionVaccineGroup == null) ? 0
						: seriesSelectionVaccineGroup.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TargetSeriesSelection other = (TargetSeriesSelection) obj;
		if (selectedSeriesName == null) {
			if (other.selectedSeriesName != null)
				return false;
		} else if (!selectedSeriesName.equals(other.selectedSeriesName))
			return false;
		if (seriesSelectionSeason == null) {
			if (other.seriesSelectionSeason != null)
				return false;
		} else if (!seriesSelectionSeason.equals(other.seriesSelectionSeason))
			return false;
		if (seriesSelectionVaccineGroup != other.seriesSelectionVaccineGroup)
			return false;
		return true;
	}
	
}