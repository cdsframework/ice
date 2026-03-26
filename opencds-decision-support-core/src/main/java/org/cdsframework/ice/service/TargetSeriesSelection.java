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

import org.kie.api.definition.type.ClassReactive;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ClassReactive
public class TargetSeriesSelection
{
    public enum SeriesSelectionStatus
    {
        SERIES_SELECTION_NOT_STARTED,
        SERIES_SELECTION_IN_PREPROCESS,
        SERIES_SELECTION_IN_PROCESS,
        SERIES_SELECTION_IN_POSTPROCESS,
        SERIES_SELECTION_COMPLETE,
        SERIES_SELECTION_PENDING_OTHERS
    }

    private final int seriesSelectionPriority;
    private final int seriesGroup;
    @EqualsAndHashCode.Include
    private String seriesSelectionVaccineGroup;
    @EqualsAndHashCode.Include
    private Season seriesSelectionSeason;
    private SeriesSelectionStatus seriesSelectionStatus;
    @EqualsAndHashCode.Include
    private String selectedSeriesName;

    /**
     * Initialize TargetSeriesSelection
     *
     * @param pTS representing the vaccine group
     */
    public TargetSeriesSelection(final TargetSeries pTS, final Schedule pSchedule)
    {
        final String _METHODNAME = "TargetSeriesSelection(): ";

        if (pTS == null)
        {
            final String lErrStr = "TargetSeries supplied is not initialized";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (pSchedule == null)
        {
            final String lErrStr = "Schedule supplied is not initialized";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (!pSchedule.getSupportedVaccineGroups().vaccineGroupItemExists(pTS.getVaccineGroup()))
        {
            final String lErrStr =
                    "specified vaccine group does not exist for this schedule; vaccine group specified: " + pTS.getVaccineGroup();
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.seriesSelectionPriority =
                pSchedule.getSupportedVaccineGroups().getVaccineGroupItem(pTS.getVaccineGroup()).getPriority();
        this.seriesSelectionVaccineGroup = pTS.getVaccineGroup();
        if (pTS.getTargetSeason() != null)
            this.seriesSelectionSeason = pTS.getTargetSeason();
        else
            seriesSelectionSeason = null;
        this.seriesSelectionStatus = SeriesSelectionStatus.SERIES_SELECTION_NOT_STARTED;
        this.selectedSeriesName = null;
        this.seriesGroup = pTS.getSeriesRules().getSeriesGroup();
    }

    @Override
    public String toString()
    {
        return "TargetSeriesSelection [seriesSelectionVaccineGroup=%s, seriesSelectionSeason=%s, seriesSelectionStatus=%s, selectedSeriesName=%s]".formatted(
                seriesSelectionVaccineGroup, seriesSelectionSeason, seriesSelectionStatus, selectedSeriesName);
    }
}
