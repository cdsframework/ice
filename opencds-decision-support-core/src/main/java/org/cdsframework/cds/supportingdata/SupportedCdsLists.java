package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationReason;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.supportingdata.SupplementalReasonSupport;
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
    private static void validateNoOutboundCodePropertyOnSupplementalReasonConcept(final CodeSystem pCodeSystem,
            final CodeSystemConcept pConcept)
    {
        if (pCodeSystem == null || pConcept == null)
            return;

        if (!SupplementalReasonSupport.isSupplementalReasonConceptCodeSystem(pCodeSystem.name()))
            return;

        final boolean hasOutboundCodeProperty = Stream.ofNullable(pConcept.property())
                .flatMap(Collection::stream)
                .map(CodeSystemConceptProperty::code)
                .anyMatch("outboundCode"::equals);
        if (!hasOutboundCodeProperty)
            return;

        throw new InconsistentConfigurationException(
                "Supplemental reason code systems must not define outboundCode property. Found on %s.%s".formatted(
                        pCodeSystem.name(), pConcept.code()));
    }

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

    @Override
    public boolean isEmpty()
    {
        return this.cdsListItemNameToCdsListItem.isEmpty();
    }

    public void addSupportedCodeSystem(final CodeSystem pCodeSystem, final String codeSystemOid)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        if (pCodeSystem == null)
            return;

        try
        {
            Optional.ofNullable(pCodeSystem.concept())
                    .ifPresent(concepts -> concepts.forEach(c -> addSupportedCodeSystemConcept(pCodeSystem, codeSystemOid, c)));
        }
        catch (final IllegalArgumentException iue)
        {
            throw new InconsistentConfigurationException(iue.getMessage());
        }
    }

    private void addSupportedCodeSystemConcept(final CodeSystem pCodeSystem, final String codeSystemOid,
            final CodeSystemConcept pConcept) throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedCodeSystemConcept(): ";

        if (pCodeSystem == null || pConcept == null)
            return;

        validateNoOutboundCodePropertyOnSupplementalReasonConcept(pCodeSystem, pConcept);

        final LocallyCodedCdsListItem locallyCodedCdsListItem = new LocallyCodedCdsListItem(pCodeSystem, pConcept, codeSystemOid);
        final String lSupportedListItemName = locallyCodedCdsListItem.getCdsListItemName();
        if (this.cdsListItemNameToCdsListItem.containsKey(lSupportedListItemName))
        {
            if (this.cdsListItemNameToCdsListItem.get(lSupportedListItemName).equals(locallyCodedCdsListItem))
            {
                log.debug("{}SupportedListItem already present; skipping: {}", _METHODNAME, lSupportedListItemName);
                return;
            }
            final String lErrStr =
                    "Attempt to add duplicate SupportedListItem: cannot add a supported list concept that already been added; must first remove the prior SupportedCdsListItem of the same name "
                            + lSupportedListItemName;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        final String lSLCCdsListCode = locallyCodedCdsListItem.getCdsListCode();
        final String lSLCCdsListCodeSystem = locallyCodedCdsListItem.getCdsListCodeSystem();
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
                    "Attempt to add a CdsList code which has a different Code System mapping than was previously specified; CdsList code: "
                            + lSLCCdsListCode + "; Code System provided: " + lSLCCdsListCodeSystem
                            + "; Code System previously specified: " + lPreviousMappedCdsListCodeSystem;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Consistency Check: For Code System -> Cds List Code; if mapping already present, enforce consistency of the previous mapping
        final String lPreviousMappedCdsListName = codeSystemToCdsListName.get(lSLCCdsListCodeSystem);
        if (lPreviousMappedCdsListName != null && !lSLCCdsListCode.equals(lPreviousMappedCdsListName))
        {
            final String lErrStr =
                    "Attempt to add a Code System which has a different CdsList code mapping than was previously specified; Code System: "
                            + lSLCCdsListCodeSystem + "; CdsList code provided: " + lSLCCdsListCode
                            + "; CdsList code previously specified: " + lPreviousMappedCdsListName;
            log.error(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Populate consistency tracking maps
        this.cdsListNameToCodeSystem.put(lSLCCdsListCode, lSLCCdsListCodeSystem);
        this.codeSystemToCdsListName.put(lSLCCdsListCodeSystem, lSLCCdsListCode);

        final Integer lCount = this.countOfCdsListItemsPerCdsList.getOrDefault(lSLCCdsListCode, 0);
        this.countOfCdsListItemsPerCdsList.put(lSLCCdsListCode, lCount + 1);

        final Set<LocallyCodedCdsListItem> lSet =
                this.cdsListNameToCdsListItems.computeIfAbsent(lSLCCdsListCode, _ -> new HashSet<>());
        lSet.add(locallyCodedCdsListItem);

        this.cdsListItemNameToCdsListItem.put(lSupportedListItemName, locallyCodedCdsListItem);

        // Add this CdsListItem as an ICEConcept of the correct type (Disease, Evaluation, Recommendation, etc. (that is, only if the CdsList is of an IceConceptType)
        final ICEConceptType lIceConceptType = ICEConceptType.getSupportedIceConceptType(lSLCCdsListCode);
        if (lIceConceptType != null)
        {
            final org.cdsframework.cds.CdsConcept lIC =
                    new org.cdsframework.cds.CdsConcept(lSupportedListItemName);            // Not an OpenCDS concept
            lIC.setIsOpenCdsSupportedConcept(false);
            this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(lIceConceptType, lIC, locallyCodedCdsListItem);

            // Add the OpenCDS Concepts (if any) to SupportedConcepts, only if the CdsList is of an IceConceptType
            for (final org.cdsframework.cds.CdsConcept lC : locallyCodedCdsListItem.getOpencdsConceptMappings())
            {
                this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(lIceConceptType, lC, locallyCodedCdsListItem);
            }
        }

        // Add the OpenCDS Concepts mapping to the OpenCDS Concept Type as well
        for (final org.cdsframework.cds.CdsConcept lC : locallyCodedCdsListItem.getOpencdsConceptMappings())
        {
            this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(ICEConceptType.OPENCDS, lC, locallyCodedCdsListItem);
        }
    }

    public void validateSupplementalReasonSupportingData()
    {
        final boolean hasSupplementalEvaluationCodeSystem =
                hasCdsListItemsAssociatedWithCdsListCode(SupplementalReasonSupport.SUPPLEMENTAL_EVALUATION_REASON_CONCEPT);
        final boolean hasSupplementalRecommendationCodeSystem =
                hasCdsListItemsAssociatedWithCdsListCode(SupplementalReasonSupport.SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT);
        final boolean hasLegacySupplementalEvaluationReason =
                cdsListItemExists(BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName());
        final boolean hasLegacySupplementalRecommendationReason =
                cdsListItemExists(BaseDataRecommendationReason._SUPPLEMENTAL_TEXT.getCdsListItemName());

        if (hasSupplementalEvaluationCodeSystem && hasSupplementalRecommendationCodeSystem && hasLegacySupplementalEvaluationReason
                && hasLegacySupplementalRecommendationReason)
            return;

        throw new InconsistentConfigurationException(
                "Supplemental reason supporting data is incomplete. Required: code systems %s and %s plus base legacy items %s and %s.".formatted(
                        SupplementalReasonSupport.SUPPLEMENTAL_EVALUATION_REASON_CONCEPT,
                        SupplementalReasonSupport.SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT,
                        BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName(),
                        BaseDataRecommendationReason._SUPPLEMENTAL_TEXT.getCdsListItemName()));
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

    public boolean hasCdsListItemsAssociatedWithCdsListCode(final String pCdsListCode)
    {
        return !ObjectUtils.isEmpty(getCdsListItemsAssociatedWithCdsListCode(pCdsListCode));
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
