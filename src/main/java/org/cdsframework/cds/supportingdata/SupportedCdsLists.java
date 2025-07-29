package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Creates and manages CdsLists, CdsListItems, and SupportedCdsConcepts.
 */
@Slf4j
@Getter
public class SupportedCdsLists implements SupportingData
{
    /**
     * Representative of a concept and one associated local code, that can be represented by an enumeration as follows (for example):
     * _RECOMMENDED_RECOMMENDATION_STATUS("ICE219", "Recommended", "2.16.840.1.113883.3.795.12.100.5", "ICE3 Immunization Recommendation", "RECOMMENDED", "Due Now"),
     * private CD supportedRecommendationConcept;
     * private CD localRecommendationCodeConcept;
     * <p>
     * Note that references to "cdsListName" below are equivalent to LocallyCodedCdsListItem.getCdsListCode()
     */
    private final List<String> cdsVersions;                                                // Supported CDS List Versions
    private final Map<String, LocallyCodedCdsListItem> cdsListItemNameToCdsListItem = new HashMap<>();
    // LOCAL CODE-RELATED: cdsListCode.cdsListItemKey -> CodedCdsListItem
    private final Map<String, String> cdsListNameToCodeSystem = new HashMap<>();
    // LOCAL CODE-RELATED: cdsListCode -> cdsListCodeSystem (to ensure there are no conflicts)
    private final Map<String, String> codeSystemToCdsListName = new HashMap<>();
    // LOCAL CODE-RELATED: cdsListCodeSystem -> cdsListCode (to ensure there are no conflicts)
    private final Map<String, Integer> countOfCdsListItemsPerCdsList = new HashMap<>();
    // LOCAL CODE-RELATED: cdsListCode -> number of cdsListItemCodes for the cdsList
    private final Map<String, Set<LocallyCodedCdsListItem>> cdsListNameToCdsListItems = new HashMap<>();
    // This class provides public methods for other classes to populate the IceConceptType.OPENCDS supported concepts, but other IceConceptType concepts
    // are populated by this class when reading the XML. Concepts are stored in the following structure.
    private final SupportedCdsConcepts supportedCdsConcepts = new SupportedCdsConcepts();
    // LOCAL CODE-RELATED: cdsListCode -> Set of CodedCdsListItems
    private boolean isSupportingDataConsistent = true;

    public SupportedCdsLists(final List<String> pCdsVersions)
    {
        this.cdsVersions = Objects.requireNonNullElseGet(pCdsVersions, ArrayList::new);
    }

    public boolean isEmpty()
    {
        return this.cdsListItemNameToCdsListItem.isEmpty();
    }

    /**
     * Adds an individual CdsListItem to the map of supported list concepts tracked by this class. This is currently a private method as all CdsListItems in the
     * CdsListSpecificationFile should ideally be added all at once.
     *
     * @throws IllegalArgumentException If the caller tries to add a SupportedListConcept that has already been added to the supported list concepts; or if the specified CdsListItem
     *                                  is not an item in the CdsListSpecificationFile, or if not all required elements have been populated
     */
    private void addSupportedCdsListItemAndConcept(final CdsListSpecificationFile pCdsListSpecificationFile,
            final CdsListItem pCdsListItem) throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedListConcept(): ";

        if (pCdsListSpecificationFile == null || pCdsListItem == null)
            return;

        // If adding a code that is not one of the supported cdsVersions, then return
        if (ObjectUtils.isEmpty(
                CollectionUtils.intersectionOfStringCollections(pCdsListSpecificationFile.getCdsVersions(), this.cdsVersions)))
            return;

        final LocallyCodedCdsListItem slci = new LocallyCodedCdsListItem(pCdsListSpecificationFile, pCdsListItem);
        final String lSupportedListItemName = slci.getCdsListItemName();
        if (this.cdsListItemNameToCdsListItem.containsKey(lSupportedListItemName))
        {
            final String lErrStr =
                    "Attempt to add duplicate SupportedListItem: cannot add a supported list concept that already been added; must first remove the prior SupportedCdsListItem of the same name "
                            + lSupportedListItemName;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        final String lSLCCdsListCode = slci.getCdsListCode();
        final String lSLCCdsListCodeSystem = slci.getCdsListCodeSystem();
        if (lSLCCdsListCode == null || lSLCCdsListCodeSystem == null)
        {
            final String lErrStr = "Attempt to add an unpopulated CdsList code or CdsList Code System";
            log.error(_METHODNAME + lErrStr);
            this.isSupportingDataConsistent = false;
            throw new IllegalArgumentException(lErrStr);
        }

        // Consistency Check: For Cds List Code -> Code System; if mapping already present, enforce consistency of the previous mapping
        final String lPreviousMappedCdsListCodeSystem = cdsListNameToCodeSystem.get(lSLCCdsListCode);
        if (lPreviousMappedCdsListCodeSystem != null && !lSLCCdsListCodeSystem.equals(lPreviousMappedCdsListCodeSystem))
        {
            final String lErrStr =
                    "Attempt to add a supported list concept whose code system does not match the code system of a previously previously added concept by the same cdsListCode";
            log.error(_METHODNAME + lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Consistency check: For Cds List Code System -> Cds List Code; if mapping already present, enforce consistency of the previous mapping
        final String lPreviousMappedCdsListName = this.codeSystemToCdsListName.get(lSLCCdsListCodeSystem);

        //
        // Add the CdsList Name-> Code System and Code System -> CdsList Name mappings
        if (lPreviousMappedCdsListCodeSystem == null)
            this.cdsListNameToCodeSystem.put(slci.getCdsListCode(), slci.getCdsListCodeSystem());
        if (lPreviousMappedCdsListName == null)
            this.codeSystemToCdsListName.put(slci.getCdsListCodeSystem(), slci.getCdsListCode());

        ///////
        // Add the mapping from the CdsListItem Name to CdsListItem
        ///////
        this.cdsListItemNameToCdsListItem.put(lSupportedListItemName, slci);

        ///////
        // Keep track of the number of mappings of Cds List Items per locally coded cds lists
        ///////
        this.countOfCdsListItemsPerCdsList.merge(lSLCCdsListCode, 1, Integer::sum);

        ///////
        // Keep track of all of the CdsListItems associated with the CdsList
        ///////
        final Set<LocallyCodedCdsListItem> lcclis = this.cdsListNameToCdsListItems.getOrDefault(lSLCCdsListCode, new HashSet<>());
        lcclis.add(slci);
        this.cdsListNameToCdsListItems.put(lSLCCdsListCode, lcclis);

        ////////////// Supported Concepts initialization START //////////////
        ///////
        // Add the OpenCDS Concepts (if any) to SupportedConcepts, only if the CdsList is of an IceConceptType
        ///////
        for (final CdsConcept lC : slci.getCdsListItemOpencdsConceptMappings())
        {
            lC.setIsOpenCdsSupportedConcept(true);
            this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(ICEConceptType.OPENCDS, lC, slci);
        }

        ///////
        // Add this CdsListItem as an ICEConcept of the correct type (Disease, Evaluation, Recommendation, etc. (that is, only if the CdsList is of an IceConceptType)
        ///////
        final ICEConceptType lIceConceptType = ICEConceptType.getSupportedIceConceptType(lSLCCdsListCode);
        if (lIceConceptType != null)
        {
            final CdsConcept lIC = new CdsConcept(lSupportedListItemName);            // Not an OpenCDS concept
            lIC.setIsOpenCdsSupportedConcept(false);
            this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(lIceConceptType, lIC, slci);
        }

        ////////////// Supported Concepts initialization END //////////////

    }

    public void addSupportedCdsListItemsAndConceptsFromCdsListSpecificationFile(
            final CdsListSpecificationFile pCdsListSpecificationFile) throws InconsistentConfigurationException
    {
        if (pCdsListSpecificationFile == null)
            return;

        try
        {
            for (final CdsListItem cli : pCdsListSpecificationFile.getCdsListItems())
                addSupportedCdsListItemAndConcept(pCdsListSpecificationFile, cli);
        }
        catch (final IllegalArgumentException iue)
        {
            throw new InconsistentConfigurationException(iue.getMessage());
        }
    }

    public void removeSupportedCdsListItem(final String pSupportedCdsListItemName)
    {
        if (pSupportedCdsListItemName == null)
            return;

        final LocallyCodedCdsListItem lSLCI = cdsListItemNameToCdsListItem.remove(pSupportedCdsListItemName);
        if (lSLCI == null)
            return;

        countOfCdsListItemsPerCdsList.computeIfPresent(lSLCI.getCdsListCode(), (k, v) -> v > 0 ? v - 1 : v);
    }

    /**
     * Obtain the Cds List Code associated with a specified code system.
     *
     * @param pCodeSystem Code System
     * @return cdsListCode value associated with the specified code system
     */
    public String getCdsListCodeAssociatedWithCodeSystem(final String pCodeSystem)
    {
        if (pCodeSystem == null)
            return null;

        return this.codeSystemToCdsListName.get(pCodeSystem);
    }

    /**
     * Obtain Code System associated with a specified Cds List Code
     *
     * @return Code System associated with the Cds List Code, or null if there is no associated code system
     */
    public String getCodeSystemAssociatedWithCdsListCode(final String pCdsListCode)
    {
        if (pCdsListCode == null)
            return null;

        return this.cdsListNameToCodeSystem.get(pCdsListCode);
    }

    /**
     * Get all locally coded cds list items associated with a Cds List code
     *
     * @return Set of LocallyCodedCdsItem objects associated with the Cds List, or null if there are no associated cds list items
     */
    public Set<LocallyCodedCdsListItem> getCdsListItemsAssociatedWithCdsListCode(final String pCdsListCode)
    {
        if (pCdsListCode == null)
            return null;

        return this.cdsListNameToCdsListItems.get(pCdsListCode);
    }

    /**
     * Check to see (by name) if CdsListItem exists
     *
     * @return true if CdsListItem is found; false if not
     */
    public boolean cdsListItemExists(final String pCdsListItemName)
    {
        if (pCdsListItemName == null)
            return false;

        return this.cdsListItemNameToCdsListItem.containsKey(pCdsListItemName);
    }

    /**
     * Check to see (by CD) if CdsListItem exists
     *
     * @return true if CdsListItem is found; false if not
     */
    public boolean cdsListItemExists(final CD pCdsListItemCD)
    {
        if (pCdsListItemCD == null)
            return false;

        return cdsListItemExists("%s.%s".formatted(getCdsListCodeAssociatedWithCodeSystem(pCdsListItemCD.getCodeSystem()),
                pCdsListItemCD.getCode()));
    }

    /**
     * Obtain the CdsListItem associated with a CD
     *
     * @return CdsLIstItem, or null if not found
     */
    public LocallyCodedCdsListItem getCdsListItem(final CD pCdsListCD)
    {
        if (pCdsListCD == null || pCdsListCD.getCode() == null || pCdsListCD.getCodeSystem() == null)
            return null;

        return getCdsListItem(
                "%s.%s".formatted(getCdsListCodeAssociatedWithCodeSystem(pCdsListCD.getCodeSystem()), pCdsListCD.getCode()));
    }

    /**
     * Obtain the CdsListItem by name (<CdsListCode>.<CdsListItemName>).
     *
     * @return CdsListItem, or null if not found
     */
    public LocallyCodedCdsListItem getCdsListItem(final String pCdsListItemName)
    {
        if (pCdsListItemName == null)
            return null;

        return cdsListItemNameToCdsListItem.get(pCdsListItemName);
    }

    @Override
    public String toString()
    {
        // First build string of the list of LocallyCodedCdsListItems stored in the cdsListItemNameToCdsListItem map.
        final Collection<LocallyCodedCdsListItem> slcic = this.cdsListItemNameToCdsListItem.values();
        int i = 1;
        final StringBuilder ltoStringStr = new StringBuilder("[ ");
        for (final LocallyCodedCdsListItem slci : slcic)
            ltoStringStr.append("\n{").append(i++).append("} ").append(slci.toString());

        // Second build string of the associated SupportedConcepts
        ltoStringStr.append(this.supportedCdsConcepts);

        return ltoStringStr.toString();
    }
}
