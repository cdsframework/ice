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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SupportedVaccineGroups implements SupportingData
{
    private final SupportedCdsLists supportedCdsLists;
    // Supporting Data CdsLists from which this vaccine group supporting data is built
    private final Map<String, LocallyCodedVaccineGroupItem> cdsListItemNameToVaccineGroupItem;
    // LOCAL CODE-RELATED: cdsListCode().cdsListItemKey -> LocallyCodedVaccineGroupItem
    private final boolean isSupportingDataConsistent;

    /**
     * Create a SupportedCdsVaccineGroups object. If the SupportedCdsLists argument is null, an IllegalArgumentException is thrown.
     */
    protected SupportedVaccineGroups(final ICESupportingDataConfiguration isdc) throws IllegalArgumentException
    {
        final String _METHODNAME = "SupportedCdsVaccineGroups(): ";

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

        this.cdsListItemNameToVaccineGroupItem = new HashMap<>();
        this.isSupportingDataConsistent = true;
    }

    @Override
    public boolean isEmpty()
    {
        return this.cdsListItemNameToVaccineGroupItem.isEmpty();
    }

    protected SupportedCdsLists getAssociatedSupportedCdsLists()
    {
        return this.supportedCdsLists;
    }

    /**
     * Initialize supported vaccine groups from the VACCINE_GROUP_CONCEPT CdsList.
     *
     * @throws InconsistentConfigurationException if the information provided in the CdsList is not consistent
     */
    public void initializeFromCdsLists() throws InconsistentConfigurationException
    {
        final String _METHODNAME = "initializeFromCdsLists(): ";

        final Collection<LocallyCodedCdsListItem> vaccineGroupCdsListItems =
                this.supportedCdsLists.getCdsListItemsAssociatedWithCdsListCode("VACCINE_GROUP_CONCEPT");

        if (ObjectUtils.isEmpty(vaccineGroupCdsListItems))
        {
            log.warn(_METHODNAME + "No VACCINE_GROUP_CONCEPT CdsList items found");
            return;
        }

        for (final LocallyCodedCdsListItem lcccli : vaccineGroupCdsListItems)
        {
            final String lVaccineGroupCdsListItemName = lcccli.getCdsListItemName();

            // CdsListItem is a CdsConcept of type vaccine group? - check to make sure that the specified vaccine group CdsConcept has been specified with this vaccine group's cdsListItem definition
            final CdsConcept lPrimaryOpenCdsConcept = new CdsConcept(lVaccineGroupCdsListItemName, lcccli.getCdsListItemValue());
            if (!lcccli.equals(this.supportedCdsLists.getSupportedCdsConcepts()
                    .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE_GROUP, lPrimaryOpenCdsConcept)))
            {
                final String lErrStr =
                        "Vaccine group with a Primary CdsConcept that is not associated with the vaccine group; vaccine group"
                                + lVaccineGroupCdsListItemName + "; Primary CdsConcept: " + lPrimaryOpenCdsConcept;
                log.warn(_METHODNAME + "{}", lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }

            final List<String> lRelatedDiseasesCdsListItems = new ArrayList<>();
            int lPriority = 0;
            boolean lRoutine = true;

            for (final CodeSystemConceptProperty cp : lcccli.getProperties())
            {
                final String propertyCode = cp.getCode();
                switch (propertyCode)
                {
                    case "priority" ->
                    {
                        if (cp.getValueInteger() != null)
                            lPriority = cp.getValueInteger();
                    }
                    case "routine" ->
                    {
                        if (cp.isValueBoolean() != null)
                            lRoutine = cp.isValueBoolean();
                    }
                    case "diseaseImmunity" ->
                    {
                        if (cp.getValueCoding() instanceof final Coding diseaseImmunityCoding)
                        {
                            final CD diseaseCD = new CD();
                            diseaseCD.setCode(diseaseImmunityCoding.getCode());
                            diseaseCD.setCodeSystem(diseaseImmunityCoding.getSystem());
                            final LocallyCodedCdsListItem lRelatedDiseaseCdsListItem =
                                    this.supportedCdsLists.getCdsListItem(diseaseCD);
                            if (lRelatedDiseaseCdsListItem == null)
                            {
                                final String lErrStr =
                                        "Related disease specified for vaccine group %s not found in SupportedCdsLists: %s".formatted(
                                                lVaccineGroupCdsListItemName, diseaseCD);
                                log.warn(_METHODNAME + "{}", lErrStr);
                                throw new InconsistentConfigurationException(lErrStr);
                            }
                            if (ICEConceptType.DISEASE != ICEConceptType.getSupportedIceConceptType(
                                    lRelatedDiseaseCdsListItem.getCdsListCode()))
                            {
                                final String lErrStr =
                                        "Item specified as a related disease to vaccine group %s is not a DISEASE ICEConceptType; item: %s".formatted(
                                                lVaccineGroupCdsListItemName, lRelatedDiseaseCdsListItem);
                                log.warn(_METHODNAME + "{}", lErrStr);
                                throw new InconsistentConfigurationException(lErrStr);
                            }
                            lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getCdsListItemName());
                        }
                    }
                    // already processed
                    case "conceptMapping", "supported", "outboundCode" ->
                    {
                    }
                    default -> log.warn(_METHODNAME + "Unsupported property found for vaccine group: {} - {}",
                            lVaccineGroupCdsListItemName, propertyCode);
                }
            }

            final LocallyCodedVaccineGroupItem locallyCodedVaccineGroupItem;
            try
            {
                locallyCodedVaccineGroupItem =
                        new LocallyCodedVaccineGroupItem(lVaccineGroupCdsListItemName, lPrimaryOpenCdsConcept,
                                lcccli.getCdsListVersions(), lRelatedDiseasesCdsListItems, lPriority, lRoutine);
            }
            catch (final IllegalArgumentException e)
            {
                final String lErrStr =
                        "Caught an unexpected IllegalArgumentException during instantiation of LocallyCodedVaccineGroupItem for vaccine group "
                                + lVaccineGroupCdsListItemName;
                log.error(_METHODNAME + "{}", lErrStr);
                throw new ICECoreError(lErrStr);
            }

            this.cdsListItemNameToVaccineGroupItem.put(lVaccineGroupCdsListItemName, locallyCodedVaccineGroupItem);
        }
    }

    public Collection<LocallyCodedVaccineGroupItem> getAllVaccineGroupItems()
    {
        return this.cdsListItemNameToVaccineGroupItem.values();
    }

    public boolean vaccineGroupItemExists(final String pVaccineGroupItemName)
    {
        if (pVaccineGroupItemName == null)
            return false;

        return this.cdsListItemNameToVaccineGroupItem.get(pVaccineGroupItemName) != null;
    }

    public LocallyCodedVaccineGroupItem getVaccineGroupItem(final String pCdsListItemName)
    {
        if (pCdsListItemName == null)
            return null;

        return this.cdsListItemNameToVaccineGroupItem.get(pCdsListItemName);
    }

    /**
     * Returns the associated LocallyCodedCdsListItem
     */
    public LocallyCodedCdsListItem getCdsListItem(final String pCdsListItemName)
    {
        if (pCdsListItemName == null)
            return null;

        if (!vaccineGroupItemExists(pCdsListItemName))
            return null;

        return this.supportedCdsLists.getCdsListItem(pCdsListItemName);
    }

    /**
     * Returns the vaccineGroupItem associated with the local CD. If none exists, returns null.
     */
    public LocallyCodedVaccineGroupItem getVaccineGroupItem(final CD pVaccineGroupCD)
    {
        if (pVaccineGroupCD == null)
            return null;

        final LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(pVaccineGroupCD);
        if (llccli == null)
            return null;

        return this.cdsListItemNameToVaccineGroupItem.getOrDefault(llccli.getCdsListItemName(), null);
    }

    @Override
    public String toString()
    {
        final Collection<LocallyCodedVaccineGroupItem> slcvgis = this.cdsListItemNameToVaccineGroupItem.values();
        int i = 1;
        final StringBuilder ltoStringStr = new StringBuilder("[ ");
        for (final LocallyCodedVaccineGroupItem slcvgi : slcvgis)
            ltoStringStr.append("\n{").append(i++).append("} ").append(slcvgi.toString());

        return ltoStringStr.toString();
    }
}
