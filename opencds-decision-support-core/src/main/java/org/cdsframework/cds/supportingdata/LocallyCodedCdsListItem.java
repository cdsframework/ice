package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.ice.config.iceSupportingProperties.CdsListData;
import org.cdsframework.ice.config.iceSupportingProperties.CdsListItemConceptMappingData;
import org.cdsframework.ice.config.iceSupportingProperties.CdsListItemData;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConcept;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.dto.Identifier;
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
    private String cdsListId;
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
    private boolean supported = true;

    /**
     * Create a SupportedListConceptItem object based on the CdsListData and a CdsListItemData.
     *
     * @param pCdsLsf CdsListData
     * @param pCdsLi  CdsListItemData
     */
    protected LocallyCodedCdsListItem(final CdsListData pCdsLsf, final CdsListItemData pCdsLi) throws IllegalArgumentException
    {
        final String _METHODNAME = "LocallyCodedCdsListItem(): ";

        if (pCdsLsf == null || pCdsLi == null)
            return;

        this.cdsListType =
                Stream.of("VALUE_SET", "CODE_SYSTEM").filter(s -> s.equals(pCdsLsf.listType())).findAny().orElseThrow(() ->
                {
                    final String lErrStr = "cdsListType \"" + pCdsLsf.listType() + "\" not supported by this class";
                    log.error(_METHODNAME + "{}", lErrStr);
                    return new IllegalArgumentException(lErrStr);
                });

        this.cdsListId = pCdsLsf.listId();
        this.cdsListCode = pCdsLsf.code();
        if (cdsListCode == null)
        {
            final String lErrStr = "required element cdsListCode not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (!ConceptUtils.attributeNameConformsToRequiredNamingConvention(cdsListCode))
        {
            final String lErrStr = "required element cdsListCode \"" + this.cdsListCode + "\"  contains invalid characters "
                    + ConceptUtils._attributeNamingConvention;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final String lCdsListItemKey = pCdsLi.cdsListItemKey();
        if (lCdsListItemKey == null)
        {
            final String lErrStr = "required element cdsListItemKey not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (!ConceptUtils.attributeNameConformsToRequiredNamingConvention(lCdsListItemKey))
        {
            final String lErrStr =
                    "required element cdsListItemKey \"%s\" contains invalid characters; must conform to %s".formatted(
                            lCdsListItemKey, ConceptUtils._attributeNamingConvention);
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final Map<String, CdsListItemData> lPCdsListItems = pCdsLsf.cdsListItem();
        if (lPCdsListItems == null)
        {
            final String lErrStr = "specified cdsListItem not found in specified cdsListSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (lPCdsListItems.values().stream().noneMatch(lCdsListItem -> lCdsListItemKey.equals(lCdsListItem.cdsListItemKey())))
        {
            final String lErrStr = "specified cdsListItem not found in specified cdsListSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListDescription = pCdsLsf.description();
        this.cdsListCodeSystem = pCdsLsf.codeSystem();
        this.cdsListValueSet = pCdsLsf.valueSet();

        if (this.cdsListCodeSystem == null && this.cdsListValueSet == null)
        {
            final String lErrStr = "specified cdsList does not have a specified code system or value set OID";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (this.cdsListCodeSystem != null && this.cdsListValueSet != null)
        {
            final String lErrStr = "specified cdsList has both a code system OID and value set OID set";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListCodeSystemName = pCdsLsf.codeSystemName();
        this.cdsListOpenCdsConceptType = pCdsLsf.openCdsConceptType();
        this.cdsListItemKey = lCdsListItemKey.replaceAll("[ \t\n\f\r]", "_");
        this.cdsListItemValue = pCdsLi.cdsListItemValue();
        this.opencdsConceptMappings = new ArrayList<>();

        if (pCdsLi.cdsListItemConceptMapping() != null)
        {
            for (final CdsListItemConceptMappingData clic : pCdsLi.cdsListItemConceptMapping().values())
            {
                final CdsConcept ic = new CdsConcept(clic.code(), clic.displayName());
                ic.setIsOpenCdsSupportedConcept(true);
                ic.setDeterminationMethodCode(clic.conceptDeterminationMethod());
                this.opencdsConceptMappings.add(ic);
            }
        }

        this.cdsListVersions = pCdsLsf.cdsVersion().values();
        this.cdsListItemName = "%s.%s".formatted(this.cdsListCode, this.cdsListItemKey);

        if (pCdsLi.outboundCoding() != null)
        {
            this.cdsListItemOutboundCD = new CD();
            this.cdsListItemOutboundCD.setCode(pCdsLi.outboundCoding().code());
            this.cdsListItemOutboundCD.setDisplayName(pCdsLi.outboundCoding().displayName());
            this.cdsListItemOutboundCD.setCodeSystem(pCdsLi.outboundCoding().codeSystem());
            this.cdsListItemOutboundCD.setCodeSystemName(pCdsLi.outboundCoding().codeSystemName());
            this.cdsListItemOutboundCD.setOriginalText(Optional.ofNullable(pCdsLi.outboundCoding().originalText())
                    .map(text -> text.replaceAll("\\s+", " "))
                    .orElse(null));
            this.supplementalText =
                    Optional.ofNullable(this.cdsListItemOutboundCD.getCode()).map("SUPPLEMENTAL_TEXT"::equals).orElse(false);
        }
        this.cdsListItemCD = new CD();
        this.cdsListItemCD.setCode(this.cdsListItemKey);
        this.cdsListItemCD.setDisplayName(this.cdsListItemValue);
        if (this.cdsListValueSet != null)
            this.cdsListItemCD.setCodeSystem(this.cdsListValueSet);
        else
            this.cdsListItemCD.setCodeSystem(this.cdsListCodeSystem);
        this.cdsListItemCD.setCodeSystemName(this.cdsListCodeSystemName);
    }

    /**
     * Create a SupportedListConceptItem object based on FHIR CodeSystem and a concept.
     *
     * @param pCodeSystem FHIR CodeSystem
     * @param pConcept    FHIR CodeSystem Concept
     */
    protected LocallyCodedCdsListItem(final CodeSystem pCodeSystem, final CodeSystemConcept pConcept)
            throws IllegalArgumentException
    {
        final String _METHODNAME = "LocallyCodedCdsListItem(CodeSystem, ConceptDefinitionComponent): ";

        if (pCodeSystem == null || pConcept == null)
            return;

        this.cdsListType = "CODE_SYSTEM";

        this.cdsListId = Optional.ofNullable(pCodeSystem.getIdentifiers())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Identifier::getValue)
                .orElse(null);

        this.cdsListCode = pCodeSystem.getName();
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

        final String lCdsListItemKey = pConcept.getCode();
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

        this.cdsListDescription = pCodeSystem.getDescription();
        this.cdsListCodeSystem = pCodeSystem.getUrl();
        this.cdsListValueSet = null;

        if (this.cdsListCodeSystem == null)
        {
            final String lErrStr = "specified cdsList does not have a specified code system URL (CodeSystem.url)";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListCodeSystemName = pCodeSystem.getTitle();
        this.cdsListOpenCdsConceptType = null;
        this.cdsListItemKey = lCdsListItemKey.replaceAll("[ \t\n\f\r]", "_");
        this.cdsListItemValue = pConcept.getDisplay();
        this.opencdsConceptMappings = new ArrayList<>();

        this.cdsListVersions = java.util.Collections.singletonList(Optional.ofNullable(pCodeSystem.getVersion()).orElse(""));
        Optional.ofNullable(pConcept.getProperties()).ifPresent(this.properties::addAll);
        this.cdsListItemName = "%s.%s".formatted(this.cdsListCode, this.cdsListItemKey);

        // Handle outbound coding or concept mapping if defined as a property
        final String OUTBOUND_CODE_PROPERTY = "outboundCode";
        final String CONCEPT_MAPPING_PROPERTY = "conceptMapping";
        final String SUPPORTED_PROPERTY = "supported";
        for (final CodeSystemConceptProperty cp : Optional.ofNullable(pConcept.getProperties()).orElseGet(List::of))
        {
            final String propertyCode = cp.getCode();
            if (propertyCode == null)
                continue;

            switch (propertyCode)
            {
                case OUTBOUND_CODE_PROPERTY ->
                {
                    if (cp.getValueCoding() instanceof final Coding outboundCoding)
                    {
                        this.cdsListItemOutboundCD = new CD();
                        this.cdsListItemOutboundCD.setCode(outboundCoding.getCode());
                        this.cdsListItemOutboundCD.setDisplayName(outboundCoding.getDisplay());
                        this.cdsListItemOutboundCD.setCodeSystem(outboundCoding.getSystem());
                        // TODO: Add codeSystemName lookup by oid
                        this.cdsListItemOutboundCD.setCodeSystemName(null);
                        this.cdsListItemOutboundCD.setOriginalText(
                                "SUPPLEMENTAL_TEXT".equals(this.cdsListItemOutboundCD.getCode()) ? this.cdsListItemValue : null);
                        this.supplementalText = Optional.ofNullable(this.cdsListItemOutboundCD.getCode())
                                .map("SUPPLEMENTAL_TEXT"::equals)
                                .orElse(false);
                    }
                }
                case CONCEPT_MAPPING_PROPERTY ->
                {
                    if (cp.getValueCoding() instanceof final Coding conceptMappingCoding && conceptMappingCoding.getCode() != null)
                    {
                        final CdsConcept lC = new CdsConcept(conceptMappingCoding.getCode());
                        lC.setDisplayName(conceptMappingCoding.getDisplay());
                        lC.setConceptTargetId(conceptMappingCoding.getSystem());
                        lC.setIsOpenCdsSupportedConcept(true);
                        this.opencdsConceptMappings.add(lC);
                    }
                }
                case SUPPORTED_PROPERTY ->
                {
                    if (cp.isValueBoolean() != null)
                        this.supported = cp.isValueBoolean();
                }
            }
        }

        this.cdsListItemCD = new CD();
        this.cdsListItemCD.setCode(this.cdsListItemKey);
        this.cdsListItemCD.setDisplayName(this.cdsListItemValue);
        this.cdsListItemCD.setCodeSystem(this.cdsListCodeSystem);
        this.cdsListItemCD.setCodeSystemName(this.cdsListCodeSystemName);
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
                .append("\ncdsListId=")
                .append(cdsListId)
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
