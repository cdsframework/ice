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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cdsframework.ice.util.TimePeriod;
import org.kie.api.definition.type.ClassReactive;
import org.springframework.util.ObjectUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The DoseRule contains the preferable, allowable, ages and intervals. Intervals expressed here are currently from this dose to the next (dosenumber+1) dose. This model be
 * extended to permit expressing intervals from any dose number to any other dose number in a future release. In the meantime, a custom rule will need to be written if this
 * capability is needed.
 */
@Slf4j
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ClassReactive
public class DoseRule
{
    /**
     * Construct deep copy of DoseRule Object and return newly created object which is encompassed by the same series as the DoseRule passed in to this method.
     */
    public static DoseRule constructDeepCopyOfDoseRuleObject(final DoseRule pDR)
    {
        if (pDR == null)
            return null;

        final DoseRule lDR = new DoseRule();
        lDR.doseNumber = pDR.doseNumber;
        lDR.encompassingSeriesRules = pDR.encompassingSeriesRules;
        lDR.absoluteMinimumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.absoluteMinimumAge);
        lDR.minimumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.minimumAge);
        lDR.maximumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.maximumAge);
        lDR.earliestRecommendedAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.earliestRecommendedAge);
        lDR.latestRecommendedAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.latestRecommendedAge);
        lDR.absoluteMinimumInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.absoluteMinimumInterval);
        lDR.minimumInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.minimumInterval);
        lDR.earliestRecommendedInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.earliestRecommendedInterval);
        lDR.latestRecommendedInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.latestRecommendedInterval);

        lDR.setPreferableVaccines(pDR.getPreferableVaccines().stream().map(Vaccine::constructDeepCopyOfVaccineObject).toList());

        lDR.setAllowableVaccines(pDR.getAllowableVaccines().stream().map(Vaccine::constructDeepCopyOfVaccineObject).toList());

        lDR.setAllowableVaccineMinimumAges(pDR.allowableVaccineMinimumAges.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> VaccineComponent.constructDeepCopyOfVaccineComponentObject(entry.getKey()),
                        entry -> TimePeriod.constructDeepCopyOfTimePeriodObject(entry.getValue()))));

        lDR.setAllowableVaccineMaximumAges(pDR.allowableVaccineMaximumAges.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> VaccineComponent.constructDeepCopyOfVaccineComponentObject(entry.getKey()),
                        entry -> TimePeriod.constructDeepCopyOfTimePeriodObject(entry.getValue()))));

        return lDR;
    }

    @EqualsAndHashCode.Include
    private String uniqueId;
    private int doseNumber;
    private TimePeriod absoluteMinimumAge;                // absolute minimum age is usually the minimum age - grace period
    private TimePeriod minimumAge;
    private TimePeriod maximumAge;
    private TimePeriod earliestRecommendedAge;
    private TimePeriod latestRecommendedAge;
    private TimePeriod absoluteMinimumInterval;
    // absolute minimum interval is usually the minimum interval - grace period.
    private TimePeriod minimumInterval;
    private TimePeriod earliestRecommendedInterval;
    private TimePeriod latestRecommendedInterval;
    private List<Vaccine> preferableVaccines;
    private List<Vaccine> allowableVaccines;
    private SeriesRules encompassingSeriesRules;
    private Map<VaccineComponent, TimePeriod> allowableVaccineMinimumAges;
    private Map<VaccineComponent, TimePeriod> allowableVaccineMaximumAges;

    /**
     * Creates a dose level rule that is at the vaccine group level
     */
    public DoseRule(final SeriesRules pEncompassingSeriesRules)
    {
        this();

        final String _METHODNAME = "DoseRule(): ";

        if (pEncompassingSeriesRules == null)
        {
            final String lErrStr = "Encompassing SeriesRules not supplied for DoseRule; cannot continue";
            log.error(_METHODNAME + "{}", (Object) null);
            throw new ICECoreError(lErrStr);
        }

        uniqueId = ICELogicHelper.generateUniqueString();
        encompassingSeriesRules = pEncompassingSeriesRules;
        preferableVaccines = new ArrayList<>();
        allowableVaccines = new ArrayList<>();
        allowableVaccineMinimumAges = new HashMap<>();
        allowableVaccineMaximumAges = new HashMap<>();
    }

    /**
     * Creates a dose level rule that is specific to a vaccine
     */
    public DoseRule(final List<Vaccine> preferableComponentVaccines, final List<Vaccine> allowableComponentVaccines)
    {
        // String _METHODNAME = "Dose(List<Vaccine>, List<Vaccine>): ";

        setPreferableVaccines(preferableComponentVaccines);
        setAllowableVaccines(allowableComponentVaccines);
    }

    private DoseRule()
    {
        uniqueId = ICELogicHelper.generateUniqueString();
        preferableVaccines = new ArrayList<>();
        allowableVaccines = new ArrayList<>();
        allowableVaccineMinimumAges = new HashMap<>();
        allowableVaccineMaximumAges = new HashMap<>();
    }

    /**
     * Sets the preferable vaccines for this dose rule. If the vaccines supplied is null, preferable vaccines is set to the empty set
     */
    public void setPreferableVaccines(final List<Vaccine> vaccines)
    {
        this.preferableVaccines = new ArrayList<>();
        if (vaccines != null)
        {
            for (final Vaccine pV : vaccines)
                addPreferableVaccine(pV);
        }
    }

    public TimePeriod getAllowableVaccineMinimumAge(final VaccineComponent pVaccineComponent)
    {
        if (pVaccineComponent == null)
            return null;

        return allowableVaccineMinimumAges.get(pVaccineComponent);
    }

    public TimePeriod getAllowableVaccineMaximumAge(final VaccineComponent pVaccineComponent)
    {
        if (pVaccineComponent == null)
            return null;

        return allowableVaccineMaximumAges.get(pVaccineComponent);
    }

    public void setAllowableMinimumAgesForVaccines(final Map<VaccineComponent, TimePeriod> pAllowableVaccineMinimumAges)
    {
        if (ObjectUtils.isEmpty(pAllowableVaccineMinimumAges))
            return;

        this.allowableVaccineMinimumAges.putAll(pAllowableVaccineMinimumAges);
    }

    public void setAllowableMaximumAgesForVaccines(final Map<VaccineComponent, TimePeriod> pAllowableVaccineMaximumAges)
    {
        if (ObjectUtils.isEmpty(pAllowableVaccineMaximumAges))
            return;

        this.allowableVaccineMaximumAges.putAll(pAllowableVaccineMaximumAges);
    }

    public void allPreferableVaccines(final List<Vaccine> pVaccines)
    {
        if (pVaccines == null)
            return;

        for (final Vaccine lV : pVaccines)
            addPreferableVaccine(lV);
    }

    public void addAllowableVaccines(final List<Vaccine> pVaccines)
    {
        if (pVaccines == null)
            return;

        for (final Vaccine lV : pVaccines)
            addAllowableVaccine(lV);
    }

    public void addPreferableVaccine(final Vaccine v)
    {
        if (v == null)
            return;

        if (this.preferableVaccines == null)
            setPreferableVaccines(null);

        if (!this.preferableVaccines.contains(v))
            this.preferableVaccines.add(v);
    }

    /**
     * Sets the allowable vaccines for this dose rule. If the vaccines supplied is null, preferable vaccines is set to the empty set
     */
    public void setAllowableVaccines(final List<Vaccine> vaccines)
    {
        this.allowableVaccines = new ArrayList<>();

        if (vaccines != null)
        {
            for (final Vaccine pV : vaccines)
                addAllowableVaccine(pV);
        }
    }

    public void addAllowableVaccine(final Vaccine v)
    {
        if (v == null)
            return;

        if (this.allowableVaccines == null)
            setAllowableVaccines(null);

        if (!this.allowableVaccines.contains(v))
            this.allowableVaccines.add(v);
    }

    public List<Vaccine> getAllPermittedVaccines()
    {
        return Stream.concat(preferableVaccines != null ? preferableVaccines.stream() : Stream.empty(),
                        allowableVaccines != null ? allowableVaccines.stream() : Stream.empty())
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public TimePeriod getAbsoluteMaximumAge()
    {
        return maximumAge;
    }

    public void setAbsoluteMaximumAge(final TimePeriod maximumAge)
    {
        this.maximumAge = maximumAge;
    }

    @Override
    public String toString()
    {
        final StringBuilder toString = new StringBuilder(
                "DoseRule [doseNumber=%d; absoluteMinimumAge=%s; minimumAge=%s; maximumAge=%s; earliestRecommendedAge=%s; latestRecommendedAge=%s; absoluteMinimumInterval=%s; minimumInterval=%s; earliestRecommendedInterval=%s; latestRecommendedInterval=%s".formatted(
                        doseNumber, absoluteMinimumAge, minimumAge, maximumAge, earliestRecommendedAge, latestRecommendedAge,
                        absoluteMinimumInterval, minimumInterval, earliestRecommendedInterval, latestRecommendedInterval));

        toString.append("\npreferableVaccines [[ ");

        int i = 1;
        for (final Vaccine v : preferableVaccines)
            toString.append("\n\tpreferableVaccine {").append(i++).append("}: ").append(v.toString());
        toString.append("\n\t]]");

        i = 1;
        for (final Vaccine v : allowableVaccines)
            toString.append("\n\tallowableVaccine {").append(i++).append("}: ").append(v.toString());

        toString.append("\n\t]]");
        toString.append("\n]");

        return toString.toString();
    }
}
