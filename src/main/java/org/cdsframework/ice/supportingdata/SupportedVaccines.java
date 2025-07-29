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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.service.VaccineComponent;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.TimePeriod;
import org.cdsframework.util.support.data.ice.vaccine.IceVaccineSpecificationFile;
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

    public boolean isEmpty()
    {
        return this.cdsListItemNameToVaccineItem.isEmpty();
    }

    /**
     * Return the vaccine component
     */
    public VaccineComponent getVaccineComponent(final CD pVaccineComponentCD)
    {
        if (pVaccineComponentCD == null)
            return null;

        return this.cDToVaccineComponentsMap.get(pVaccineComponentCD);
    }

    /**
     * Returns true if there is a vaccineItem associated with the local CD, false if not. (Invoked getGroupItem(CD) to determine.)
     */
    public boolean vaccineItemExists(final CD pVaccineCD)
    {
        return getVaccineItem(pVaccineCD) != null;
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

    protected SupportedCdsLists getAssociatedSupportedCdsLists()
    {
        return this.supportedCdsLists;
    }

    public boolean isSupportingDataConsistent()
    {
        return this.vaccineComponentCDToVaccinesNotFullySpecified.isEmpty();
    }

    /**
     * Add the vaccine group information specified in the ice vaccine specification file to the list of supported vaccines. If the IceVaccineSpecificationFile is not
     * specified, then this method simply returns. If the IceVaccineSpecificationFile does not contain all of the provided information, or refers to a code system and
     * value that is not known a known CdsListItem, an IllegalArgumentException is thrown.
     *
     * @throws InconsistentConfigurationException if the information provided in the IceVaccineSpecificationFile is not consistent
     * @throws IllegalArgumentException           if this method is used improperly
     */
    protected void addSupportedVaccineItemFromIceVaccineSpecificationFile(
            final IceVaccineSpecificationFile pIceVaccineSpecificationFile)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedVaccineItemFromIceVaccineSpecificationFile(): ";

        // If the vaccine specified is not in the list of SupportedCdsLists, throw an InconsistentConfigurationException
        final LocallyCodedCdsListItem llccli =
                this.supportedCdsLists.getCdsListItem(ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine()));
        if (llccli == null)
        {
            final String lErrStr = "Attempt to add vaccine that is not in the list of SupportedCdsLists: " + (
                    pIceVaccineSpecificationFile.getVaccine() == null
                    ? "null"
                    : ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine()));
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        if (ICEConceptType.VACCINE != ICEConceptType.getSupportedIceConceptType(llccli.getCdsListCode()))
        {
            final String lErrStr = "Attempt to add an item as a vaccine which is not a VACCINE ICEConceptType";
            log.warn(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // If one of the supporting data files already defined this vaccine, thrown an InconsistentConfigurationException
        if (this.cdsListItemNameToVaccineItem.containsKey(llccli.getCdsListItemName()))
        {
            final String lErrStr = "Attempt to add vaccine that was already specified previously: " + (
                    pIceVaccineSpecificationFile.getVaccine() == null
                    ? "null"
                    : ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine()));
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // If adding a code that is not one of the supported cdsVersions, then return
        final Collection<String> lIntersectionOfSupportedCdsVersions =
                CollectionUtils.intersectionOfStringCollections(pIceVaccineSpecificationFile.getCdsVersions(),
                        this.supportedCdsLists.getCdsVersions());
        if (ObjectUtils.isEmpty(lIntersectionOfSupportedCdsVersions))
            return;

        ////////////// Determine Primary OpenCDS Concept START //////////////
        ///////
        // The Vaccine Concept will be stored as a Concept; doublecheck that a SupportedCdsConcept with the specified primary OpenCDS Concept for this Vaccine exists
        // That is, check to make sure that the ICEConcept associated with this Vaccine (CdsListItem) is also a OpenCDS concept
        ///////
        final org.opencds.vmr.v1_0.schema.CD lPrimaryOpenCdsConcept = pIceVaccineSpecificationFile.getPrimaryOpenCdsConcept();
        if (lPrimaryOpenCdsConcept == null)
        {
            final String lErrStr = "OpenCDS Concept not specified for vaccine: " + llccli;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }
        // The corresponding OpenCDS concept was specified in the file. Was the OpenCDS concept previously specified with this CdsListItem?  (OR: could alternatively just check for a CdsConcept of type vaccine)
        final CdsConcept ic = new CdsConcept(lPrimaryOpenCdsConcept.getCode(), lPrimaryOpenCdsConcept.getDisplayName());
        ic.setIsOpenCdsSupportedConcept(true);
        final Collection<CdsConcept> lOpenCDSConcepts =
                this.supportedCdsLists.getSupportedCdsConcepts().getOpenCDSICEConceptsAssociatedWithCdsListItem(llccli);
        boolean lPrimaryOpenCDSConceptForVaccineIdentified = false;
        for (final CdsConcept lIC : lOpenCDSConcepts)
        {
            if (ic.equals(lIC))
            {
                lPrimaryOpenCDSConceptForVaccineIdentified = true;
                if (lPrimaryOpenCdsConcept.getDisplayName() == null && lIC.getDisplayName() != null)
                    lPrimaryOpenCdsConcept.setDisplayName(lIC.getDisplayName());
                break;
            }
        }
        if (!lPrimaryOpenCDSConceptForVaccineIdentified)
        {
            final String lErrStr =
                    "Attempt to specify Primary OpenCDS Concept for vaccine but OpenCDS concept not associated CdsListItem; vaccine: "
                            + llccli;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        ////////////// Determine Primary OpenCDS Concept END //////////////

        ////////////// Vaccine properties to track START //////////////
        boolean lVaccineAndOnlyVaccineComponentNotEqual = false;
        final List<CD> lVaccineComponentsNotSpecified = new ArrayList<>();

        // Vaccine CD
        final CD lVaccineCD = ConceptUtils.toInternalCD(pIceVaccineSpecificationFile.getVaccine());
        if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineCD))
        {
            final String lErrStr = "Vaccine Code specified is not fully populated: " + lVaccineCD;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Combination vaccine?
        boolean lCombinationVaccine = false;

        ////////
        // START RELATED DISEASES: Get all the related diseases that the vaccine targets (as specified in the configuration data).
        // Related diseases is only used for monovalent vaccines; for combination vaccines, the related diseases are determined by the vaccine components
        ///////
        final List<org.opencds.vmr.v1_0.schema.CD> lRelatedDiseases = pIceVaccineSpecificationFile.getDiseaseImmunities();
        final List<String> lRelatedDiseasesCdsListItems = new ArrayList<>();
        if (ObjectUtils.isEmpty(lRelatedDiseases))
        {
            final String lErrStr = "Related diseases not specified";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

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

            if (ICEConceptType.DISEASE != ICEConceptType.getSupportedIceConceptType(lRelatedDiseaseCdsListItem.getCdsListCode()))
            {
                final String lErrStr = "Attempt to add an item as a related disease which is not a DISEASE ICEConceptType; item: "
                        + lRelatedDiseaseCdsListItem;
                log.warn(_METHODNAME + "{}", lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }

            lRelatedDiseasesCdsListItems.add(lRelatedDiseaseCdsListItem.getCdsListItemName());
        }
        ////////
        // END RELATED DISEASES: Get all the related diseases that the vaccine targets (as specified in the configuration data).
        ///////

        // Vaccine Components
        final List<VaccineComponent> lVaccineComponentsToAddToVaccine = new ArrayList<>();
        final List<org.opencds.vmr.v1_0.schema.CD> lVaccineComponentsCD = pIceVaccineSpecificationFile.getVaccineComponents();
        // Either the
        if (ObjectUtils.isEmpty(lVaccineComponentsCD))
        {
            final String lErrStr =
                    "vaccine components not specified; at least one vaccine component must be specified for a vaccine. Monovalent vaccines contain a vaccine "
                            + "component code that is in most cases equal to the encompassing vaccine's code, though exception to this is possible";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        ////////////// Vaccine properties to track END //////////////

        ///////
        // START to Determine which vaccine components can be incorporated now in the initialization of this vaccine
        ///////
        // If the vaccine code of the component is the same as that of the vaccine (or there is only one vaccine component?), then add the vaccine component with the specified diseases
        if (lVaccineComponentsCD.size() == 1)
        {
            final CD lVaccineComponentCD = ConceptUtils.toInternalCD(lVaccineComponentsCD.getFirst());
            if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineComponentCD))
            {
                final String lErrStr = "VaccineComponent not fully specified: " + lVaccineComponentCD;
                log.error(_METHODNAME + "{}", lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }

            if (!lVaccineComponentCD.getCodeSystem().equals(lVaccineCD.getCodeSystem()))
            {
                final String lErrStr =
                        "Vaccine and Vaccine Component are specified using two different code systems. This is not permitted";
                log.error(_METHODNAME + lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }

            // This is a monovalent vaccine; this is the definition. Create the vaccine component and store it. It targets all of the diseases targeted by this vaccine
            if (!ConceptUtils.cDElementsAreEqual(lVaccineCD, lVaccineComponentCD))
            {
                // FYI - the vaccine component and vaccine local codes are not equal, and we don't require the VaccneComponent to be previously specified
                lVaccineAndOnlyVaccineComponentNotEqual = true;
            }

            if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD) && !lVaccineAndOnlyVaccineComponentNotEqual)
            {
                // The vaccine component should not have been defined before - throw an InconsistentConfigurationException
                final String lErrStr =
                        "Monovalent vaccine with VaccineComponent previously defined encountered. VaccineComponents cannot be defined more than once.";
                log.error(_METHODNAME + lErrStr);
                throw new InconsistentConfigurationException(lErrStr);
            }

            if (!lVaccineAndOnlyVaccineComponentNotEqual)
            {
                ///////
                // Create the new VaccineComponent, and make note of this a vaccine component; record and will be a part of this Vaccine
                final VaccineComponent lVaccineComponent = new VaccineComponent(ic, lRelatedDiseasesCdsListItems);
                /////// VaccineComponent lVaccineComponent = new VaccineComponent(llccli.getCdsListItemName(), lRelatedDiseasesCdsListItems);
                addPropertiesFromSDToVaccineComponent(lVaccineComponent, pIceVaccineSpecificationFile);
                lVaccineComponentsToAddToVaccine.add(lVaccineComponent);

                // Make note that this monovalent vaccine and/or vaccine component has been processed by taking note of the mapping
                this.cDToVaccineComponentsMap.put(lVaccineComponentCD, lVaccineComponent);

                // If this VaccineComponent was previously encountered by a Vaccine, add this VaccineComponent to those Vaccines as well.
                populatePreviouslyDefinedVaccinesAssociatedWithVaccineComponentWithSpecifiedVaccineComponentInfo(
                        lVaccineComponentCD, lVaccineComponent);
            }
        }
        // More than one vaccine component was specified, or vaccine component of monovalent vaccine does not equal the vaccine itself; include the vaccine components that have already
        // been specified previously. If not all of them have been specified before, then mark that the vaccine component specified in this combo vaccine has not been specified yet.
        if (lVaccineComponentsCD.size() != 1 || lVaccineAndOnlyVaccineComponentNotEqual)
        {
            lCombinationVaccine = true;
            for (final org.opencds.vmr.v1_0.schema.CD lVaccineComponentCDSchema : lVaccineComponentsCD)
            {
                final CD lVaccineComponentCD = ConceptUtils.toInternalCD(lVaccineComponentCDSchema);
                if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineComponentCD))
                {
                    final String lErrStr = "VaccineComponent not fully specified: " + lVaccineComponentCD;
                    log.error(_METHODNAME + "{}", lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (lVaccineComponentCD.getCodeSystem() == null || lVaccineCD.getCodeSystem() == null
                        || !lVaccineComponentCD.getCodeSystem().equals(lVaccineCD.getCodeSystem()))
                {
                    final String lErrStr =
                            "Vaccine and Vaccine Component are specified using two different code systems in the supporting data. This is not permitted";
                    log.error(_METHODNAME + lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }

                if (this.cDToVaccineComponentsMap.containsKey(lVaccineComponentCD))
                {
                    //
                    // This VaccineComponent has been defined previously. Make note that this previously encountered VaccineComponent is a part of this vaccine
                    //
                    lVaccineComponentsToAddToVaccine.add(this.cDToVaccineComponentsMap.get(lVaccineComponentCD));
                }
                else
                {
                    // Make note that this vaccine component has not been defined previously; therefore this vaccine has vaccine components not specified
                    lVaccineComponentsNotSpecified.add(lVaccineComponentCD);
                }
            }
        }
        ///////
        // END of Determine which vaccine components can be incorporated now in the initialization of this vaccine
        ///////

        ///////
        // START Create the Vaccine and store it this object
        ///////
        final Vaccine lVaccine;
        if (lVaccineComponentsToAddToVaccine.isEmpty())
        {
            lVaccine = new Vaccine(ic);
            /////// lVaccine = new Vaccine(llccli.getCdsListItemName());
        }
        else
        {
            lVaccine = new Vaccine(ic, lVaccineComponentsToAddToVaccine, true);
            /////// lVaccine = new Vaccine(llccli.getCdsListItemName(), lVaccineComponentsToAddToVaccine, true);
        }

        // Set combination vaccine, live virus, and unformulated formulations, if applicable
        lVaccine.setCombinationVaccine(lCombinationVaccine);
        if (!lCombinationVaccine)
        {
            // Determine if the formulation of this vaccine is unformulated or not
            boolean lUnspecifiedFormulation = false;
            final Boolean pIceVaccineSpecificationFormulation = pIceVaccineSpecificationFile.isUnspecifiedFormulation();
            if (pIceVaccineSpecificationFormulation != null && pIceVaccineSpecificationFormulation)
                lUnspecifiedFormulation = true;
            lVaccine.setUnspecifiedFormulation(lUnspecifiedFormulation);
        }
        // Set live virus vaccine boolean
        if (pIceVaccineSpecificationFile.isLiveVirusVaccine() != null)
            lVaccine.setLiveVirusVaccine(pIceVaccineSpecificationFile.isLiveVirusVaccine());
        else
            lVaccine.setLiveVirusVaccine(false);
        // Set select adjuvant product boolean
        if (pIceVaccineSpecificationFile.isSelectAdjuvantProduct() != null)
            lVaccine.setSelectAdjuvantProduct(pIceVaccineSpecificationFile.isSelectAdjuvantProduct());
        else
            lVaccine.setSelectAdjuvantProduct(false);
        this.cdsListItemNameToVaccineItem.put(llccli.getCdsListItemName(),
                new LocallyCodedVaccineItem(llccli.getCdsListItemName(), ic, lIntersectionOfSupportedCdsVersions, lVaccine));

        ///////
        // END Creating and persisting the Vaccine
        ///////

        ///////
        // For those vaccine components not specified in this combination vaccine, add this vaccine we're dealing with now to the list of vaccines that contain this unspecified
        // vaccine component
        //
        for (final CD lVaccineComponentCD : lVaccineComponentsNotSpecified)
        {
            final Set<Vaccine> lVaccinesNotFullySpecifiedSet =
                    this.vaccineComponentCDToVaccinesNotFullySpecified.getOrDefault(lVaccineComponentCD, new HashSet<>());
            lVaccinesNotFullySpecifiedSet.add(lVaccine);
            this.vaccineComponentCDToVaccinesNotFullySpecified.put(lVaccineComponentCD, lVaccinesNotFullySpecifiedSet);
        }
        ///////

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

    /**
     * Adds the minimum age, maximum age, unspecified formulation flag, and live virus vaccine flag from the ICE vaccine supporting data file to the vaccine component
     *
     * @throws IllegalArgumentException, propagated from TimePeriod, if specified ages are in the wrong format.
     */
    private void addPropertiesFromSDToVaccineComponent(final VaccineComponent pVaccineComponent,
            final IceVaccineSpecificationFile pIVSF)
    {
        if (pVaccineComponent == null || pIVSF == null)
            return;

        // Valid Minimum Age
        String lAge = pIVSF.getValidMinimumAgeForUse();
        if (lAge != null)
            pVaccineComponent.setValidMinimumAgeForUse(new TimePeriod(lAge));
        // Valid Maximum Age
        lAge = pIVSF.getValidMaximumAgeForUse();
        if (lAge != null)
            pVaccineComponent.setValidMaximumAgeForUse(new TimePeriod(lAge));
        // Recommended Minimum Age
        lAge = pIVSF.getRecommendedMinimumAgeForUse();
        if (lAge != null)
            pVaccineComponent.setRecommendedMinimumAgeForUse(new TimePeriod(lAge));
        // Recommended Maximum Age
        lAge = pIVSF.getRecommendedMaximumAgeForUse();
        if (lAge != null)
            pVaccineComponent.setRecommendedMaximumAgeForUse(new TimePeriod(lAge));
        // Live virus
        Boolean lBL = pIVSF.isLiveVirusVaccine();
        if (lBL != null)
            pVaccineComponent.setLiveVirusVaccine(lBL);
        // Select Adjuvant Product
        final Boolean lBLSA = pIVSF.isSelectAdjuvantProduct();
        if (lBLSA != null)
            pVaccineComponent.setSelectAdjuvantProduct(lBLSA);
        // Unspecified Formulation
        lBL = pIVSF.isUnspecifiedFormulation();
        if (lBL != null)
            pVaccineComponent.setUnspecifiedFormulation(lBL);

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
