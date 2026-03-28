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

package org.cdsframework.ice.supportingdata;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.service.VaccineComponent;
import org.cdsframework.ice.util.TimePeriod;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportedVaccines implements SupportingData
{
    // Supporting Data Cds List from which this vaccine supporting data is built
    private final SupportedCdsLists supportedCdsLists;
    // Keep track of which vaccine items are fully specified; in order for a vaccine to be fully specified, all of its component vaccines must be fully specified as well. We
    // keep track of which Vaccines each VaccineComponent is associated so that they can be associated with the combination vaccine when/if that information comes available.
    private final Map<String, LocallyCodedVaccineItem> cdsListItemNameToVaccineItem;
    // cdsListItemName (cdsListCode.cdsListItemKey) to Vaccine
    private final Map<CD, VaccineComponent> cDToVaccineComponentsMap;
    // VaccineComponents which have been encountered in a Vaccine object but not yet defined
    // VaccineComponents previously defined, keyed by CD
    private final Map<CD, Set<Vaccine>> vaccineComponentCDToVaccinesNotFullySpecified;

    /**
     * Create a SupportedVaccines object. If the ICESupportingDataConfiguration or its associated SupportedCdsLists argument is null, an IllegalArgumentException is thrown.
     */
    protected SupportedVaccines(final ICESupportingDataConfiguration isdc) throws IllegalArgumentException
    {
        final String _METHODNAME = "SupportedCdsVaccines(): ";

        if (isdc == null)
        {
            final String lErrStr = "ICESupportingDataConfiguration argument is null; a valid argument must be provided.";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportedCdsLists = isdc.getSupportedCdsLists();
        if (this.supportedCdsLists == null)
        {
            final String lErrStr = "Supporting cds list data not set in ICESupportingDataConfiguration; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListItemNameToVaccineItem = new HashMap<>();
        this.cDToVaccineComponentsMap = new HashMap<>();
        this.vaccineComponentCDToVaccinesNotFullySpecified = new HashMap<>();
    }

    /**
     * Obtain the CdsVaccineItem by name (<CdsListCode>.<CdsListItemName>).
     *
     * @return LocallyCodedVaccineItem CdsVaccineItem, or null if not found
     */
    public LocallyCodedVaccineItem getVaccineItem(final String pCdsVaccineItemName)
    {
        if (pCdsVaccineItemName == null)
            return null;

        return this.cdsListItemNameToVaccineItem.get(pCdsVaccineItemName);
    }

    @Override
    public boolean isEmpty()
    {
        return this.cdsListItemNameToVaccineItem.isEmpty();
    }

    /**
     * Initialize supported vaccines from the SUPPORTED_VACCINES CdsList.
     *
     * @throws InconsistentConfigurationException if the information provided in the CdsList is not consistent
     */
    public void initializeFromCdsLists() throws InconsistentConfigurationException
    {
        final String _METHODNAME = "initializeFromCdsLists(): ";

        final Collection<LocallyCodedCdsListItem> vaccineCdsListItems =
                this.supportedCdsLists.getCdsListItemsAssociatedWithCdsListCode("SUPPORTED_VACCINES");

        if (ObjectUtils.isEmpty(vaccineCdsListItems))
        {
            log.warn(_METHODNAME + "No SUPPORTED_VACCINES CdsList items found");
            return;
        }

        // We need to process monovalent vaccines (those with 1 component) before combination vaccines
        // to ensure vaccine components are defined.
        final List<LocallyCodedCdsListItem> sortedItems = vaccineCdsListItems.stream().sorted((a, b) ->
        {
            final int countA = countProperties(a, "vaccineComponent");
            final int countB = countProperties(b, "vaccineComponent");
            return Integer.compare(countA, countB);
        }).toList();

        for (final LocallyCodedCdsListItem lcccli : sortedItems)
        {
            addVaccineItemFromCdsListItem(lcccli);
        }
    }

    private int countProperties(final LocallyCodedCdsListItem item, final String propertyCode)
    {
        return (int) item.getProperties().stream().filter(p -> propertyCode.equals(p.getCode())).count();
    }

    private void addVaccineItemFromCdsListItem(final LocallyCodedCdsListItem lcccli) throws InconsistentConfigurationException
    {
        final String _METHODNAME = "addVaccineItemFromCdsListItem(): ";

        if (!lcccli.isSupported())
        {
            log.debug("{}Skipping unsupported vaccine: {}", _METHODNAME, lcccli.getCdsListItemName());
            return;
        }

        // Identify the primary OpenCDS concept
        final Collection<CdsConcept> lOpenCDSConcepts = lcccli.getOpencdsConceptMappings();
        if (ObjectUtils.isEmpty(lOpenCDSConcepts))
        {
            final String lErrStr = "No OpenCDS Concept mappings found for vaccine: " + lcccli.getCdsListItemName();
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Use the first mapping as primary (similar to SupportedVaccineGroups)
        final CdsConcept ic = lOpenCDSConcepts.iterator().next();

        final CD lVaccineCD = lcccli.getCdsListItemCD();

        // Extract properties from LocallyCodedCdsListItem
        final List<String> lRelatedDiseasesCdsListItems = new ArrayList<>();
        final List<CD> lVaccineComponentCDs = new ArrayList<>();
        final List<CD> lConflictingVaccineCDs = new ArrayList<>();

        boolean lLiveVirusVaccine = false;
        boolean lUnspecifiedFormulation = false;
        boolean lSelectAdjuvantProduct = false;
        Date lMinimumDateForUse = null;
        Date lMaximumDateForUse = null;
        TimePeriod lValidMinimumAgeForUse = null;
        TimePeriod lValidMaximumAgeForUse = null;
        TimePeriod lRecommendedMinimumAgeForUse = null;
        TimePeriod lRecommendedMaximumAgeForUse = null;

        for (final CodeSystemConceptProperty cp : lcccli.getProperties())
        {
            final String code = cp.getCode();
            switch (code)
            {
                case "diseaseImmunity" ->
                {
                    if (cp.getValueCoding() instanceof final Coding c)
                    {
                        final CD diseaseCD = new CD();
                        diseaseCD.setCode(c.getCode());
                        diseaseCD.setCodeSystem(c.getSystem());
                        final LocallyCodedCdsListItem lRelatedDiseaseCdsListItem = this.supportedCdsLists.getCdsListItem(diseaseCD);
                        if (lRelatedDiseaseCdsListItem != null
                                && ICEConceptType.DISEASE == ICEConceptType.getSupportedIceConceptType(
                                lRelatedDiseaseCdsListItem.getCdsListCode()))
                            lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getCdsListItemName());
                    }
                }
                case "vaccineComponent" ->
                {
                    if (cp.getValueCoding() instanceof final Coding c)
                    {
                        final CD compCD = new CD();
                        compCD.setCode(c.getCode());
                        compCD.setCodeSystem(c.getSystem());
                        lVaccineComponentCDs.add(compCD);
                    }
                }
                case "conflictingVaccine" ->
                {
                    if (cp.getValueCoding() instanceof final Coding c)
                    {
                        final CD conflictCD = new CD();
                        conflictCD.setCode(c.getCode());
                        conflictCD.setCodeSystem(c.getSystem());
                        lConflictingVaccineCDs.add(conflictCD);
                    }
                }
                case "liveVirusVaccine" ->
                {
                    if (cp.isValueBoolean() != null)
                        lLiveVirusVaccine = cp.isValueBoolean();
                }
                case "unspecifiedFormulation" ->
                {
                    if (cp.isValueBoolean() != null)
                        lUnspecifiedFormulation = cp.isValueBoolean();
                }
                case "selectAdjuvantProduct" ->
                {
                    if (cp.isValueBoolean() != null)
                        lSelectAdjuvantProduct = cp.isValueBoolean();
                }
                case "minimumDateForUse" -> lMinimumDateForUse = parseDate(cp.getValueString());
                case "maximumDateForUse" -> lMaximumDateForUse = parseDate(cp.getValueString());
                case "validMinimumAgeForUse" ->
                {
                    if (cp.getValueString() != null)
                        lValidMinimumAgeForUse = new TimePeriod(cp.getValueString());
                }
                case "validMaximumAgeForUse" ->
                {
                    if (cp.getValueString() != null)
                        lValidMaximumAgeForUse = new TimePeriod(cp.getValueString());
                }
                case "recommendedMinimumAgeForUse" ->
                {
                    if (cp.getValueString() != null)
                        lRecommendedMinimumAgeForUse = new TimePeriod(cp.getValueString());
                }
                case "recommendedMaximumAgeForUse" ->
                {
                    if (cp.getValueString() != null)
                        lRecommendedMaximumAgeForUse = new TimePeriod(cp.getValueString());
                }
                // already processed
                case "conceptMapping", "supported", "outboundCode" ->
                {
                }
                default -> log.warn(_METHODNAME + "Unsupported property found for vaccine: {} - {}", lcccli.getCdsListItemName(),
                        code);
            }
        }

        if (lVaccineComponentCDs.isEmpty())
        {
            log.debug("{}No vaccine components found for vaccine: {}; skipping", _METHODNAME, lcccli.getCdsListItemName());
            return;
        }

        final List<VaccineComponent> lVaccineComponentsToAddToVaccine = new ArrayList<>();
        final List<CD> lVaccineComponentsNotSpecified = new ArrayList<>();
        boolean lCombinationVaccine = lVaccineComponentCDs.size() > 1;

        if (lVaccineComponentCDs.size() == 1)
        {
            final CD lVaccineComponentCD = lVaccineComponentCDs.get(0);
            if (!lVaccineComponentCD.getCodeSystem().equals(lVaccineCD.getCodeSystem()))
            {
                throw new InconsistentConfigurationException(
                        "Vaccine and Vaccine Component are specified using two different code systems.");
            }

            final boolean lVaccineAndOnlyVaccineComponentNotEqual =
                    !ConceptUtils.cDElementsAreEqual(lVaccineCD, lVaccineComponentCD);
            if (lVaccineAndOnlyVaccineComponentNotEqual)
            {
                lCombinationVaccine = true;
            }

            if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD) && !lVaccineAndOnlyVaccineComponentNotEqual)
            {
                throw new InconsistentConfigurationException(
                        "Monovalent vaccine with VaccineComponent previously defined encountered.");
            }

            if (!lVaccineAndOnlyVaccineComponentNotEqual)
            {
                final VaccineComponent lVaccineComponent = new VaccineComponent(ic, lRelatedDiseasesCdsListItems);
                lVaccineComponent.setValidMinimumAgeForUse(lValidMinimumAgeForUse);
                lVaccineComponent.setValidMaximumAgeForUse(lValidMaximumAgeForUse);
                lVaccineComponent.setRecommendedMinimumAgeForUse(lRecommendedMinimumAgeForUse);
                lVaccineComponent.setRecommendedMaximumAgeForUse(lRecommendedMaximumAgeForUse);
                lVaccineComponent.setMinimumDateForUse(lMinimumDateForUse);
                lVaccineComponent.setMaximumDateForUse(lMaximumDateForUse);
                lVaccineComponent.setLiveVirusVaccine(lLiveVirusVaccine);
                lVaccineComponent.setSelectAdjuvantProduct(lSelectAdjuvantProduct);
                lVaccineComponent.setUnspecifiedFormulation(lUnspecifiedFormulation);

                lVaccineComponentsToAddToVaccine.add(lVaccineComponent);
                this.cDToVaccineComponentsMap.put(lVaccineComponentCD, lVaccineComponent);
                populatePreviouslyDefinedVaccinesAssociatedWithVaccineComponentWithSpecifiedVaccineComponentInfo(
                        lVaccineComponentCD, lVaccineComponent);
            }
        }

        if (lCombinationVaccine)
        {
            for (final CD lVaccineComponentCD : lVaccineComponentCDs)
            {
                if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD))
                {
                    lVaccineComponentsToAddToVaccine.add(this.cDToVaccineComponentsMap.get(lVaccineComponentCD));
                }
                else
                {
                    lVaccineComponentsNotSpecified.add(lVaccineComponentCD);
                }
            }
        }

        final Vaccine lVaccine = lVaccineComponentsToAddToVaccine.isEmpty()
                                 ? new Vaccine(ic)
                                 : new Vaccine(ic, lVaccineComponentsToAddToVaccine, true);

        lVaccine.setCombinationVaccine(lCombinationVaccine);
        if (!lCombinationVaccine)
        {
            lVaccine.setUnspecifiedFormulation(lUnspecifiedFormulation);
        }
        lVaccine.setLiveVirusVaccine(lLiveVirusVaccine);
        lVaccine.setSelectAdjuvantProduct(lSelectAdjuvantProduct);
        lVaccine.setMinimumDateForUse(lMinimumDateForUse);
        lVaccine.setMaximumDateForUse(lMaximumDateForUse);

        final LocallyCodedVaccineItem locallyCodedVaccineItem =
                new LocallyCodedVaccineItem(lcccli.getCdsListItemName(), ic, lcccli.getCdsListVersions(), lVaccine);

        this.cdsListItemNameToVaccineItem.put(lcccli.getCdsListItemName(), locallyCodedVaccineItem);

        for (final CD compCD : lVaccineComponentsNotSpecified)
        {
            final Set<Vaccine> lVaccinesNotFullySpecifiedSet =
                    this.vaccineComponentCDToVaccinesNotFullySpecified.computeIfAbsent(compCD, _ -> new HashSet<>());
            lVaccinesNotFullySpecifiedSet.add(lVaccine);
        }
    }

    private Date parseDate(final String value)
    {
        if (value == null)
            return null;
        try
        {
            return Date.from(
                    LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant());
        }
        catch (final DateTimeParseException e)
        {
            try
            {
                return Date.from(java.time.LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant());
            }
            catch (final DateTimeParseException e2)
            {
                log.warn("Failed to parse date: {}", value);
            }
        }
        return null;
    }

    /**
     * Returns the vaccineItem associated with the local CD. If none exists, returns null.
     */
    public LocallyCodedVaccineItem getVaccineItem(final CD pVaccineCD)
    {
        if (pVaccineCD == null)
            return null;

        final LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(pVaccineCD);
        if (llccli == null)
            return null;

        return this.cdsListItemNameToVaccineItem.getOrDefault(llccli.getCdsListItemName(), null);
    }

    @Override
    public boolean isSupportingDataConsistent()
    {
        return this.vaccineComponentCDToVaccinesNotFullySpecified.isEmpty();
    }

    private void populatePreviouslyDefinedVaccinesAssociatedWithVaccineComponentWithSpecifiedVaccineComponentInfo(
            final CD pVaccineComponentCD, final VaccineComponent pVaccineComponent)
    {
        if (pVaccineComponent == null || pVaccineComponentCD == null)
            return;

        final String _METHODNAME =
                "populatePreviouslyDefinedVaccinesAssociatedWithVaccineComponentWithSpecifiedVaccineComponentInfo(): ";

        // If this VaccineComponent was previously encountered by a Vaccine, add this VaccineComponent to those Vaccines as well.
        if (this.vaccineComponentCDToVaccinesNotFullySpecified.containsKey(pVaccineComponentCD))
        {
            final Set<Vaccine> lAllPreviouslyEncounteredVaccinesWVaccineComponent =
                    this.vaccineComponentCDToVaccinesNotFullySpecified.get(pVaccineComponentCD);
            if (lAllPreviouslyEncounteredVaccinesWVaccineComponent == null)
            {
                final String lErrStr =
                        "Error: Unaccounted for inconsistency encountered during processing of vaccine supporting data. (Unaccounted for vaccine component when no vaccines have been defined)";
                log.error(_METHODNAME + lErrStr);
                throw new ICECoreError(lErrStr);
            }

            final List<Vaccine> lVaccinesToRemoveFromSetOfPreviouslyEncounteredVaccinesWVaccineComponent = new ArrayList<>();
            for (final Vaccine lVaccine : lAllPreviouslyEncounteredVaccinesWVaccineComponent)
            {
                lVaccine.addMemberVaccineComponent(pVaccineComponent);
                lVaccinesToRemoveFromSetOfPreviouslyEncounteredVaccinesWVaccineComponent.add(lVaccine);
            }
            for (final Vaccine lPreviousVaccineEncountered : lVaccinesToRemoveFromSetOfPreviouslyEncounteredVaccinesWVaccineComponent)
                lAllPreviouslyEncounteredVaccinesWVaccineComponent.remove(lPreviousVaccineEncountered);
            if (lAllPreviouslyEncounteredVaccinesWVaccineComponent.isEmpty())
            {
                // If all vaccines with this pending vaccine component have been handled, remove the fact that there were previously encountered
                // vaccines that need this (now) fully specified vaccine component to be added to it
                this.vaccineComponentCDToVaccinesNotFullySpecified.remove(pVaccineComponentCD);
            }
        }
    }

    @Override
    public String toString()
    {
        final Set<String> cdsListItemNames = this.cdsListItemNameToVaccineItem.keySet();
        int i = 1;
        final StringBuilder ltoStringStr = new StringBuilder();
        for (final String s : cdsListItemNames)
            ltoStringStr.append("{")
                    .append(i++)
                    .append("} ")
                    .append(s)
                    .append(" = [ ")
                    .append(this.cdsListItemNameToVaccineItem.get(s).toString())
                    .append(" ]\n");

        return ltoStringStr.toString();
    }
}
