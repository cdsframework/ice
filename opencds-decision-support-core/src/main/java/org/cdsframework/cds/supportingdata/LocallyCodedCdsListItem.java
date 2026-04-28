package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConcept;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.supportingdata.SupplementalReasonSupport;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class LocallyCodedCdsListItem
{
    private final List<CodeSystemConceptProperty> properties = new ArrayList<>();
    @EqualsAndHashCode.Include
    private String cdsListItemName;
    @EqualsAndHashCode.Include
    private String cdsListCode;
    private String cdsListType;
    private String cdsListDescription;
    @EqualsAndHashCode.Include
    private String cdsListCodeSystem;
    private String cdsListCodeSystemName;
    private String cdsListValueSet;
    private String cdsListOpenCdsConceptType;
    @EqualsAndHashCode.Include
    private String cdsListItemKey;
    @EqualsAndHashCode.Include
    private String cdsListItemValue;
    private Collection<CdsConcept> opencdsConceptMappings;
    private Collection<String> cdsListVersions;
    private CD cdsListItemOutboundCD;
    private CD cdsListItemCD;
    private boolean supplementalText;
    private SupplementalReasonSupport.SupplementalReasonType supplementalReasonType =
            SupplementalReasonSupport.SupplementalReasonType.NONE;
    private boolean supported = true;

    /**
     * Create a SupportedListConceptItem object based on FHIR CodeSystem and a concept.
     *
     * @param pCodeSystem    FHIR CodeSystem
     * @param pConcept       FHIR CodeSystem Concept
     * @param pCodeSystemOid validated code system OID
     */
    protected LocallyCodedCdsListItem(final CodeSystem pCodeSystem, final CodeSystemConcept pConcept, final String pCodeSystemOid)
            throws IllegalArgumentException
    {
        final String _METHODNAME = "LocallyCodedCdsListItem(CodeSystem, ConceptDefinitionComponent): ";

        if (pCodeSystem == null || pConcept == null)
            return;

        this.cdsListType = "CODE_SYSTEM";

        this.cdsListCode = pCodeSystem.name();
        if (cdsListCode == null)
        {
            final String lErrStr = "required element cdsListCode (CodeSystem.name) not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (!ConceptUtils.attributeNameConformsToRequiredNamingConvention(cdsListCode))
        {
            final String lErrStr =
                    "required element cdsListCode \"" + this.cdsListCode + "\" (CodeSystem.name) contains invalid characters "
                            + ConceptUtils._attributeNamingConvention;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final String lCdsListItemKey = pConcept.code();
        if (lCdsListItemKey == null)
        {
            final String lErrStr = "required element cdsListItemKey (ConceptDefinitionComponent.code) not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (!ConceptUtils.attributeNameConformsToRequiredNamingConvention(lCdsListItemKey))
        {
            final String lErrStr =
                    "required element cdsListItemKey \"%s\" (ConceptDefinitionComponent.code) contains invalid characters; must conform to %s".formatted(
                            lCdsListItemKey, ConceptUtils._attributeNamingConvention);
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListDescription = pCodeSystem.description();
        this.cdsListCodeSystem = pCodeSystemOid;
        if (this.cdsListCodeSystem == null || this.cdsListCodeSystem.isBlank())
        {
            final String lErrStr = "required element cdsListCodeSystem OID not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }
        this.cdsListValueSet = null;

        this.cdsListCodeSystemName = pCodeSystem.title();
        this.cdsListOpenCdsConceptType = null;
        this.cdsListItemKey = lCdsListItemKey.replaceAll("[ \t\n\f\r]", "_");
        this.cdsListItemValue = pConcept.display();
        this.supplementalReasonType = SupplementalReasonSupport.getSupplementalReasonTypeForCodeSystem(this.cdsListCode);
        this.opencdsConceptMappings = new ArrayList<>();

        this.cdsListVersions = java.util.Collections.singletonList(Optional.ofNullable(pCodeSystem.version()).orElse(""));
        Optional.ofNullable(pConcept.property()).ifPresent(this.properties::addAll);
        this.cdsListItemName = "%s.%s".formatted(this.cdsListCode, this.cdsListItemKey);

        // Handle outbound coding or concept mapping if defined as a property
        final String OUTBOUND_CODE_PROPERTY = "outboundCode";
        final String CONCEPT_MAPPING_PROPERTY = "conceptMapping";
        final String SUPPORTED_PROPERTY = "supported";
        for (final CodeSystemConceptProperty cp : Optional.ofNullable(pConcept.property()).orElseGet(List::of))
        {
            final String propertyCode = cp.code();
            if (propertyCode == null)
                continue;

            switch (propertyCode)
            {
                case OUTBOUND_CODE_PROPERTY ->
                {
                    if (cp.valueCoding() instanceof final Coding outboundCoding)
                    {
                        this.cdsListItemOutboundCD = new CD();
                        this.cdsListItemOutboundCD.setCode(outboundCoding.code());
                        this.cdsListItemOutboundCD.setDisplayName(outboundCoding.display());
                        this.cdsListItemOutboundCD.setCodeSystem(outboundCoding.system());
                        // TODO: Add codeSystemName lookup by oid
                        this.cdsListItemOutboundCD.setCodeSystemName(null);
                        this.cdsListItemOutboundCD.setOriginalText(
                                SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE.equals(this.cdsListItemOutboundCD.getCode())
                                ? this.cdsListItemValue
                                : null);
                        this.supplementalText = Optional.ofNullable(this.cdsListItemOutboundCD.getCode())
                                .map(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE::equals)
                                .orElse(false);
                    }
                }
                case CONCEPT_MAPPING_PROPERTY ->
                {
                    if (cp.valueCoding() instanceof final Coding conceptMappingCoding && conceptMappingCoding.code() != null)
                    {
                        final CdsConcept lC = new CdsConcept(conceptMappingCoding.code());
                        lC.setDisplayName(conceptMappingCoding.display());
                        lC.setConceptTargetId(conceptMappingCoding.system());
                        lC.setIsOpenCdsSupportedConcept(true);
                        this.opencdsConceptMappings.add(lC);
                    }
                }
                case SUPPORTED_PROPERTY ->
                {
                    if (cp.valueBoolean() != null)
                        this.supported = cp.valueBoolean();
                }
            }
        }
        initializeSupplementalOutboundCDForReasonConceptIfApplicable();

        this.cdsListItemCD = CD.builder()
                .code(this.cdsListItemKey)
                .displayName(this.cdsListItemValue)
                .codeSystem(this.cdsListCodeSystem)
                .codeSystemName(this.cdsListCodeSystemName)
                .build();
    }

    private void initializeSupplementalOutboundCDForReasonConceptIfApplicable()
    {
        if (!this.supplementalReasonType.isSupplemental())
            return;

        if (this.cdsListItemOutboundCD == null)
            this.cdsListItemOutboundCD = new CD();

        this.cdsListItemOutboundCD.setCode(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE);
        this.cdsListItemOutboundCD.setOriginalText(this.cdsListItemValue);
        this.cdsListItemOutboundCD.setCodeSystemName(
                Optional.ofNullable(this.cdsListItemOutboundCD.getCodeSystemName()).orElse(this.cdsListCodeSystemName));

        this.supplementalText = true;
    }

    /**
     * Return the associated code system or value set OID for this cdsListItem. Equivalent to getCdsListItemCD().getCodeSystem().
     */
    public String getCdsListCodeSystem()
    {
        return this.cdsListItemCD.getCodeSystem();
    }

    /**
     * Equivalent to getCdsListItemCD().getCode();
     */
    public String getCdsListItemKey()
    {
        return this.getCdsListItemCD().getCode();
    }

    /**
     * Equivalent to getCdsListItemCD.getDisplayName();
     */
    public String getCdsListItemValue()
    {
        return this.getCdsListItemCD().getDisplayName();
    }

    public Collection<CdsConcept> getCdsListItemOpencdsConceptMappings()
    {
        return this.opencdsConceptMappings;
    }

    @Override
    public String toString()
    {
        final StringBuilder lStr = new StringBuilder().append("[SupportedCdsListItem=")
                .append(cdsListItemName)
                .append("\ncdsListCode=")
                .append(cdsListCode)
                .append("\ncdsListType=")
                .append(cdsListType)
                .append("\ncdsListDescription=")
                .append(cdsListDescription)
                .append("\ncdsListCodeSystem=")
                .append(cdsListCodeSystem)
                .append("\ncdsListCodeSystemName=")
                .append(cdsListCodeSystemName)
                .append("\ncdsListValueSet=")
                .append(cdsListValueSet)
                .append("\ncdsListOpenCdsConceptType=")
                .append(cdsListOpenCdsConceptType)
                .append("\ncdsListItemKey=")
                .append(cdsListItemKey)
                .append("\ncdsListItemValue=")
                .append(cdsListItemValue);
        lStr.append("\nopencdsConceptMappings= [");

        for (final CdsConcept icc : getCdsListItemOpencdsConceptMappings())
            lStr.append("\tICEConcept=").append(icc.toString()).append("\n");
        lStr.append("\t]\n");
        for (final String lVersionStr : getCdsListVersions())
            lStr.append("\tCdsVersion=").append(lVersionStr).append("\n");
        lStr.append("]");
        lStr.append("\n");

        return lStr.toString();
    }
}
