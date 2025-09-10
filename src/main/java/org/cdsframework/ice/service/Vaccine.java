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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cdsframework.cds.CdsConcept;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class Vaccine extends AbstractVaccine
{
    /**
     * private String strength;
     * private String lotNo;
     */

    public static Vaccine constructDeepCopyOfVaccineObject(final Vaccine pV)
    {
        if (pV == null)
            return null;

        final Vaccine lVaccine = new Vaccine(pV);
        lVaccine.setCombinationVaccine(pV.combinationVaccine);
        for (final VaccineComponent pVC : pV.vaccineComponents)
            lVaccine.getVaccineComponents().add(VaccineComponent.constructDeepCopyOfVaccineComponentObject(pVC));

        return lVaccine;
    }

    private final List<VaccineComponent> vaccineComponents;
    private boolean combinationVaccine;

    private Vaccine(final Vaccine pVaccine)
    {
        super(pVaccine);

        this.vaccineComponents = new ArrayList<>();
    }

    public Vaccine(final CdsConcept pVaccineConcept)
    {
        super(pVaccineConcept);

        this.vaccineComponents = new ArrayList<>();
    }

    /**
     * Instantiate a vaccine object. If there is only one vaccine component, the vaccine component code must be the same as that specified by the ICEConcept if using this constructor.
     * The Unspecified Formulation on constructed vaccine object is set to the same value as the monovalent vaccine, which is true by default if not otherwise specified;
     * it is set to false if any component vaccine is not an unspecified formulation; otherwise set to true. Be sure to set unspecified formulation flag
     * for each vaccine component appropriately to ensure proper behavior of rules, or update all components and this object appropriately if changed.
     *
     * @param pVaccineConcept    ICEConcept representing the vaccine
     * @param pVaccineComponents At least one vaccine component is required; composite vaccines will contain more than one VaccineComponent;
     *                           monovalent vaccines must contain the antigen for itself (and therefore the name concept code). This is necessary to specify the valid
     *                           ages for the antigens
     * @throws IllegalArgumentException If parameters are not correctly populated (or either are null) with valid values; monovalent vaccines must have a vaccine component
     *                                  with the same ICEConcept ID
     */
    public Vaccine(final CdsConcept pVaccineConcept, final List<VaccineComponent> pVaccineComponents)
    {
        this(pVaccineConcept, pVaccineComponents, false);
    }

    /**
     * Instantiate a Vaccine object. Both parameters are mandatory.
     *
     * @param pVaccineConcept                                           ICEConcept representing the vaccine
     * @param pVaccineComponents                                        At least one vaccine component is required; composite vaccines will contain more than one VaccineComponent;
     *                                                                  monovalent vaccines must contain the antigen for itself (and therefore the name concept code). This is necessary to specify the valid
     *                                                                  ages for the antigens. If more than one vaccine component is specified, than this is set to a combinationVaccine: otherwise, it is not. It is recommended that the
     *                                                                  caller set the combinationVaccine property manually if this behavior is not desired.
     * @param permitUnequalVaccineComponentCodeValueInMonovalentVaccine If true and monovalent vaccine, permit the vaccine component code to be a different value from the
     *                                                                  encompassing vaccine's code values. This is a rare circumstance and by default is set to false by other constructors.
     * @throws IllegalArgumentException If parameters are not correctly populated (or either are null) with valid values; monovalent vaccines must have a vaccine component
     *                                  with the same ICEConcept ID if permitUnequalVacconeComponentValueInMonovalentVaccine is false (which by default it is).
     */
    public Vaccine(final CdsConcept pVaccineConcept, final List<VaccineComponent> pVaccineComponents,
            final boolean permitUnequalVaccineComponentCodeValueInMonovalentVaccine)
    {
        super(pVaccineConcept);

        final String _METHODNAME = "Vaccine(): ";

        if (ObjectUtils.isEmpty(pVaccineComponents))
        {
            final String errStr = "vaccine component not populated";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        if (pVaccineComponents.contains(null))
        {
            final String errStr = "one or more vaccine components is null; not permitted";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        // Usually the monovalent vaccine must have a component vaccine with the same code as the encompassing vaccine object. Check this condition
        final int lVaccineComponentsSize = pVaccineComponents.size();
        if (!permitUnequalVaccineComponentCodeValueInMonovalentVaccine && lVaccineComponentsSize == 1)
        {
            final VaccineComponent vc = pVaccineComponents.getFirst();
            // if (! vc.getVaccineConcept().equals(pVaccineConcept)) {
            if (!vc.getCdsConceptName().equals(pVaccineConcept.getOpenCdsConceptCode()))
            {
                final String errStr = "vaccine component supplied for this monovalent vaccine has an unequal concept code value";
                log.warn(_METHODNAME + errStr);
                throw new IllegalArgumentException(errStr);
            }
        }

        this.vaccineComponents = pVaccineComponents;
        this.combinationVaccine = false;
        boolean lUnspecifiedFormulation = true;
        if (lVaccineComponentsSize > 1)
        {
            this.combinationVaccine = true;
            for (final VaccineComponent lVC : pVaccineComponents)
            {
                if (!lVC.isUnspecifiedFormulation())
                {
                    lUnspecifiedFormulation = false;
                    break;
                }
            }
        }
        else
            lUnspecifiedFormulation = pVaccineComponents.getFirst().isUnspecifiedFormulation();

        this.setUnspecifiedFormulation(lUnspecifiedFormulation);
    }

    /**
     * Add the specified VaccineComponent as a VaccineComponent of this Vaccine.
     */
    public void addMemberVaccineComponent(final VaccineComponent pVaccineComponent)
    {
        final String _METHODNAME = "addMemberVaccineComponent(): ";

        if (pVaccineComponent == null)
            return;

        // Set the unspecified formulation boolean
        final int lVaccineComponentsSize = getVaccineComponents().size();
        if (lVaccineComponentsSize > 1 && !pVaccineComponent.isUnspecifiedFormulation())
            this.setUnspecifiedFormulation(false);
        else
            if (lVaccineComponentsSize == 1 || lVaccineComponentsSize == 0)
                this.setUnspecifiedFormulation(pVaccineComponent.isUnspecifiedFormulation());
            else
                this.setUnspecifiedFormulation(true);

        if (!this.vaccineComponents.contains(pVaccineComponent))
            this.vaccineComponents.add(pVaccineComponent);
        else
        {
            log.warn(_METHODNAME + "Attempt to add duplicate vaccine component ignored. Vaccine Component : {}; Vaccine: {}",
                    pVaccineComponent, this);
        }
    }

    /**
     * Get list of diseases targeted for immunity by this vaccine
     *
     * @return List<String> of diseases targeted by this vaccine; empty list if none
     */
    public Collection<String> getAllDiseasesTargetedForImmunity()
    {
        if (this.vaccineComponents == null)
            return Set.of();

        return vaccineComponents.stream()
                .map(VaccineComponent::getDiseaseImmunityList)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Get all VaccineComponent member objects of this vaccine that targets the specified list of diseases
     *
     * @param pTargetedDiseases Collection of diseases from which to ascertain targeting VaccineComponents
     */
    public Collection<VaccineComponent> getVaccineComponentsTargetingSpecifiedDiseases(final Collection<String> pTargetedDiseases)
    {
        if (ObjectUtils.isEmpty(pTargetedDiseases))
            return Set.of();

        return vaccineComponents.stream()
                .filter(vc -> vc.getDiseaseImmunityList().stream().anyMatch(pTargetedDiseases::contains))
                .collect(Collectors.toSet());
    }

    @Override
    public String toString()
    {
        final StringBuilder toStr = new StringBuilder(
                "Vaccine [getCdsListItemName()=%s, isLiveVirusVaccine()=%s, getValidMinimumAgeForUse()=%s, getValidMaximumAgeForUse()=%s, getTradeName()=%s, getManufacturerCode()=%s, getLicensedMinimumAgeForUse()=%s, getLicensedMaximumAgeForUse()=%s, isUnspecifiedFormulation()=%s, isCombinationVaccine()=%s, isSelectAdjuvantProduct()=%s, VaccineComponent [[ ".formatted(
                        getCdsConceptName(), isLiveVirusVaccine(), getValidMinimumAgeForUse(), getValidMaximumAgeForUse(),
                        getTradeName(), getManufacturerCode(), getRecommendedMinimumAgeForUse(), getRecommendedMaximumAgeForUse(),
                        isUnspecifiedFormulation(), isCombinationVaccine(), isSelectAdjuvantProduct()));

        int i = 1;
        for (final VaccineComponent lVaccineComponent : this.getVaccineComponents())
            toStr.append("\n(").append(i++).append(") ").append(lVaccineComponent);
        toStr.append("\n]]\n]");

        return toStr.toString();
    }
}
