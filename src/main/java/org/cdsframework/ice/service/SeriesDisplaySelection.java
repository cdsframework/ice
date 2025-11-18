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

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ClassReactive
public class SeriesDisplaySelection
{
    public enum SeriesDisplaySelectionType
    {
        SERIES_DISPLAY_NOT_SELECTED,
        SERIES_DISPLAY_UNAMBIGUOUS,
        SERIES_DISPLAY_BEST_GUESS,
        SERIES_DISPLAY_ALTERNATIVE,
        SERIES_DISPLAY_NONE
    }

    @EqualsAndHashCode.Include
    private final String uniqueId;
    private final TargetSeries targetSeries;
    private SeriesDisplaySelectionType seriesSelectionDisplayType;
    private String numberOfDosesRemaining;
    private boolean seriesSelectionDisplayDeterminationComplete;

    public SeriesDisplaySelection(final TargetSeries pTargetSeries)
    {
        uniqueId = ICELogicHelper.generateUniqueString();
        this.targetSeries = pTargetSeries;
        this.seriesSelectionDisplayType = SeriesDisplaySelectionType.SERIES_DISPLAY_NOT_SELECTED;
        this.numberOfDosesRemaining = null;
        this.seriesSelectionDisplayDeterminationComplete = false;
    }

    public SeriesDisplaySelection(final TargetSeries pTargetSeries, final SeriesDisplaySelectionType pSST)
    {
        uniqueId = ICELogicHelper.generateUniqueString();
        this.targetSeries = pTargetSeries;
        this.seriesSelectionDisplayType = pSST;
        this.numberOfDosesRemaining = null;
        this.seriesSelectionDisplayDeterminationComplete = false;
    }

    public SeriesDisplaySelection(final TargetSeries pTargetSeries, final SeriesDisplaySelectionType pSST,
            final String pDosesRemaining)
    {
        this(pTargetSeries, pSST);
        this.numberOfDosesRemaining = pDosesRemaining;
    }

    public String getSeriesName()
    {
        return this.targetSeries.getSeriesName();
    }

    public String getVaccineGroup()
    {
        return this.targetSeries.getVaccineGroup();
    }

    public SeriesDisplaySelectionType getSeriesDisplaySelectionType()
    {
        return seriesSelectionDisplayType;
    }

    public void setSeriesDisplaySelectionType(final SeriesDisplaySelectionType pSST)
    {
        this.seriesSelectionDisplayType = pSST;
    }

    public boolean isSeriesDisplaySelectionDeterminationComplete()
    {
        return this.seriesSelectionDisplayDeterminationComplete;
    }

    public void setSeriesDisplaySelectionDeterminationComplete(final boolean pIsDeterminationComplete)
    {
        this.seriesSelectionDisplayDeterminationComplete = pIsDeterminationComplete;
    }

    @Override
    public String toString()
    {
        return "SeriesDisplaySelection [ targetSeries=\"%s\", seriesSelectionOutputType=%s, numberOfDosesRemaining=%s, uniqueId=%s ]".formatted(
                targetSeries.getSeriesName(), seriesSelectionDisplayType, numberOfDosesRemaining, uniqueId);
    }
}
