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

import java.time.LocalDate;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.util.TimePeriod;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public abstract class AbstractVaccine
{
    @EqualsAndHashCode.Include
    private final CdsConcept cdsConcept;
    private boolean unspecifiedFormulation;
    private boolean liveVirusVaccine;
    private boolean selectAdjuvantProduct;
    private TimePeriod validMinimumAgeOfUse;
    private TimePeriod validMaximumAgeOfUse;
    private TimePeriod recommendedForUseMinimumAge;
    private TimePeriod recommendedForUseMaximumAge;
    private LocalDate minimumDateForUse;
    private LocalDate maximumDateForUse;

    public AbstractVaccine(final AbstractVaccine pAbstractVaccine)
    {
        final String _METHODNAME = "AbstractVaccine(AbstractVaccine): ";
        if (pAbstractVaccine == null)
        {
            final String errStr = "Vaccine instance not specified";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        this.cdsConcept = CdsConcept.constructDeepCopyOfCdsConceptObject(pAbstractVaccine.getCdsConcept());
        this.liveVirusVaccine = pAbstractVaccine.isLiveVirusVaccine();
        this.selectAdjuvantProduct = pAbstractVaccine.isSelectAdjuvantProduct();
        this.unspecifiedFormulation = pAbstractVaccine.isUnspecifiedFormulation();
        this.validMinimumAgeOfUse = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getValidMinimumAgeForUse());
        this.validMaximumAgeOfUse = TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getValidMaximumAgeForUse());
        this.recommendedForUseMinimumAge =
                TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getRecommendedMinimumAgeForUse());
        this.recommendedForUseMaximumAge =
                TimePeriod.constructDeepCopyOfTimePeriodObject(pAbstractVaccine.getRecommendedMaximumAgeForUse());
        this.minimumDateForUse = pAbstractVaccine.getMinimumDateForUse();
        this.maximumDateForUse = pAbstractVaccine.getMaximumDateForUse();
    }

    /**
     * Constructor for AbstractVaccine object
     */
    public AbstractVaccine(final CdsConcept pCC)
    {
        final String _METHODNAME = "AbstractVaccine(String): ";
        if (pCC == null || pCC.getOpenCdsConceptCode() == null)
        {            // The latter condition should never occur
            final String errStr = "cdsConceptName code not supplied";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        this.cdsConcept = pCC;
        this.liveVirusVaccine = false;
        this.unspecifiedFormulation = false;
        this.selectAdjuvantProduct = false;
        this.validMinimumAgeOfUse = null;
        this.validMaximumAgeOfUse = null;
        this.recommendedForUseMinimumAge = null;
        this.recommendedForUseMaximumAge = null;
        this.minimumDateForUse = null;
        this.maximumDateForUse = null;
    }

    public String getCdsConceptName()
    {
        return this.cdsConcept.getOpenCdsConceptCode();
    }

    /**
     * Return the minimum age for the vaccine. If not specified previously, null is returned
     *
     * @return TimePeriod representing the minimum age for the vaccine
     */
    public TimePeriod getValidMinimumAgeForUse()
    {
        return validMinimumAgeOfUse;
    }

    public void setValidMinimumAgeForUse(final TimePeriod validMinimumAgeForUse)
    {
        this.validMinimumAgeOfUse = validMinimumAgeForUse;
    }

    /**
     * Return the maximum age for the vaccine. If not specified previously, null is returned.
     */
    public TimePeriod getValidMaximumAgeForUse()
    {
        return validMaximumAgeOfUse;
    }

    public void setValidMaximumAgeForUse(final TimePeriod validMaximumAgeForUse)
    {
        this.validMaximumAgeOfUse = validMaximumAgeForUse;
    }

    public TimePeriod getRecommendedMinimumAgeForUse()
    {
        return recommendedForUseMinimumAge;
    }

    public void setRecommendedMinimumAgeForUse(final TimePeriod recommendedForUseMinimumAge)
    {
        this.recommendedForUseMinimumAge = recommendedForUseMinimumAge;
    }

    public TimePeriod getRecommendedMaximumAgeForUse()
    {
        return recommendedForUseMaximumAge;
    }

    public void setRecommendedMaximumAgeForUse(final TimePeriod recommendedForUseMaximumAge)
    {
        this.recommendedForUseMaximumAge = recommendedForUseMaximumAge;
    }
}

