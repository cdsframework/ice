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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.util.support.data.ice.vaccinegroup.IceVaccineGroupSpecificationFile;
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
     * Add the vaccine group information specified in the ice vaccine group specification file to the list of supported vaccine groups. If the IceVaccineGroupSpecificationFile is
     * not specified, then this method simply returns. If the IceVaccineGroupSpecificationFile does not contain all of the provided information, or refers to a code system and
     * value that is not known a known CdsListItem, an IllegalArgumentException is thrown.
     *
     * @throws InconsistentConfigurationException if the information provided in the IceVaccineGroupSpecificationFile is not consistent
     */
    protected void addVaccineGroupItemFromIceVaccineGroupSpecificationFile(
            final IceVaccineGroupSpecificationFile pIceVaccineGroupSpecificationFile)
            throws InconsistentConfigurationException, IllegalArgumentException
    {
        final String _METHODNAME = "addSupportedVaccineGroupItem(): ";

        if (pIceVaccineGroupSpecificationFile == null || this.supportedCdsLists == null)
            return;

        // If adding a code that is not one of the supported cdsVersions, then return
        final Collection<String> lCdsVersions =
                CollectionUtils.intersectionOfStringCollections(pIceVaccineGroupSpecificationFile.getCdsVersions(),
                        this.supportedCdsLists.getCdsVersions());
        if (ObjectUtils.isEmpty(lCdsVersions))
            return;

        // Verify that there is a primary opencds concept code
        if (pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept() == null
                || pIceVaccineGroupSpecificationFile.getPrimaryOpenCdsConcept().getCode() == null)
        {
            final String lErrStr =
                    "Attempt to add the following vaccine group which has no specified corresponding primary OpenCDS concept: " + (
                            pIceVaccineGroupSpecificationFile.getVaccineGroup() == null
                            ? "null"
                            : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Determine the vaccine group name. No duplicates allowed; no vaccine groups that weren't previously defined LocallyCodedCdsListItem allowed.
        final LocallyCodedCdsListItem llccli = this.supportedCdsLists.getCdsListItem(
                ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
        // Now verify that there is a CdsListItem for this vaccine group (i.e. - we are tracking the codes and code systems in SupportedCdsLists - it must be there too).
        if (llccli == null)
        {
            final String lErrStr = "Attempt to add the following vaccine group which is not in the list of SupportedCdsLists: " + (
                    pIceVaccineGroupSpecificationFile.getVaccineGroup() == null
                    ? "null"
                    : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        if (ICEConceptType.VACCINE_GROUP != ICEConceptType.getSupportedIceConceptType(llccli.getCdsListCode()))
        {
            final String lErrStr = "Attempt to add an item as a vaccine group which is not a VACCINE_GROUP ICEConceptType";
            log.warn(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        final String lVaccineGroupCdsListItemName = llccli.getCdsListItemName();
        if (this.cdsListItemNameToVaccineGroupItem.containsKey(lVaccineGroupCdsListItemName))
        {
            final String lErrStr = "Attempt to add vaccine group that was already specified previously: " + (
                    pIceVaccineGroupSpecificationFile.getVaccineGroup() == null
                    ? "null"
                    : ConceptUtils.toInternalCD(pIceVaccineGroupSpecificationFile.getVaccineGroup()));
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // CdsListItem is a CdsConcept of type vaccine group? - check to make sure that the specified vaccine group CdsConcept has been specified with this vaccine group's cdsListItem definition
        final CdsConcept lPrimaryOpenCdsConcept = new CdsConcept(lVaccineGroupCdsListItemName, llccli.getCdsListItemValue());
        if (!llccli.equals(this.supportedCdsLists.getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.VACCINE_GROUP, lPrimaryOpenCdsConcept)))
        {
            final String lErrStr =
                    "Attempt to add vaccine group with a Primary CdsConcept that is not associated with the vaccine group; vaccine group"
                            + lVaccineGroupCdsListItemName + "; Primary CdsConcept: " + lPrimaryOpenCdsConcept;
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Related Diseases
        final List<org.opencds.vmr.v1_0.schema.CD> lRelatedDiseases = pIceVaccineGroupSpecificationFile.getDiseaseImmunities();
        final List<String> lRelatedDiseasesCdsListItems = new ArrayList<>();
        if (!ObjectUtils.isEmpty(lRelatedDiseases))
        {
            for (final org.opencds.vmr.v1_0.schema.CD lRelatedDisease : lRelatedDiseases)
            {
                final LocallyCodedCdsListItem lRelatedDiseaseCdsListItem =
                        this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(lRelatedDisease));
                if (lRelatedDiseaseCdsListItem == null)
                {
                    ConceptUtils.toInternalCD(lRelatedDisease);
                    final String lErrStr = ConceptUtils.toInternalCD(lRelatedDisease).toString();
                    log.warn(_METHODNAME + "{}", lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (ICEConceptType.DISEASE != ICEConceptType.getSupportedIceConceptType(
                        lRelatedDiseaseCdsListItem.getCdsListCode()))
                {
                    final String lErrStr =
                            "Attempt to add an item as a related disease to a vaccine group which is not a DISEASE ICEConceptType; item: "
                                    + lRelatedDiseaseCdsListItem;
                    log.warn(_METHODNAME + "{}", lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }

                lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getCdsListItemName());
            }
        }

        // Vaccine group priority
        int lVaccineGroupPriority = 0;
        final BigInteger lVaccineGroupPriorityInt = pIceVaccineGroupSpecificationFile.getPriority();
        if (lVaccineGroupPriorityInt != null)
            lVaccineGroupPriority = lVaccineGroupPriorityInt.intValue();

        // Create and add the LocallyCodedVaccineGroupItem
        final LocallyCodedVaccineGroupItem lcvgi;
        try
        {
            lcvgi = new LocallyCodedVaccineGroupItem(lVaccineGroupCdsListItemName, lPrimaryOpenCdsConcept, lCdsVersions,
                    lRelatedDiseasesCdsListItems, lVaccineGroupPriority);
        }
        catch (final IllegalArgumentException iue)
        {
            final String lErrStr =
                    "Caught an unexpected IllegalArgumentException during instantiation of LocallyCodedVaccineGroupItem for vaccine group"
                            + lVaccineGroupCdsListItemName;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }

        // Add the mapping from the String to reference the vaccine group to LocallyCodedVaccineGroupItem
        this.cdsListItemNameToVaccineGroupItem.put(lVaccineGroupCdsListItemName, lcvgi);
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
     * Returns true if there is a vaccineGroupItem associated with the local CD, false if not. (Invoked getGroupVaccineGroupItem(CD) to determine.)
     */
    public boolean vaccineGroupItemExists(final CD pVaccineGroupCD)
    {
        return getVaccineGroupItem(pVaccineGroupCD) != null;
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
