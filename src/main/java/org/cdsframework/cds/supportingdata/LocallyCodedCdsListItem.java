package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListItemConceptMapping;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class LocallyCodedCdsListItem
{
    /**
     * Local codes mapped to concepts
     * supportedCdsListTypes = { "VALUE_SET", "CODE_SYSTEM" };
     * e.g.
     * <cdsListSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.cds.list">
     * <listId>1f30111EET5bc0568822e2608f22be9d1bac9c</listId>
     * <code>SUPPORTED_VACCINES</code>
     * <name>Supported Vaccines</name>
     * <listType>CODE_SYSTEM</listType>
     * <description>CVX code vaccines supported by ICE</description>
     * <codeSystem>2.16.840.1.113883.12.292</codeSystem>
     * <codeSystemName>Vaccines (CVX)</codeSystemName>
     * <cdsListItem>
     * <cdsListItemKey>08</cdsListItemKey>
     * <cdsListItemValue>Hep B peds, less than 20yrs</cdsListItemValue>
     * <cdsListItemConceptMapping>
     * <code>ICE08</code>
     * <displayName>Hep B peds, less than 20yrs</displayName>
     * </cdsListItemConceptMapping>
     * </cdsListItem>
     * </cdsVersion>
     * </cdsListSpecificationFile>
     */

    @EqualsAndHashCode.Include
    private String cdsListItemName;
    private String cdsListId;
    private String cdsListCode;
    private String cdsListType;
    private String cdsListDescription;
    private String cdsListCodeSystem;
    private String cdsListCodeSystemName;
    private String cdsListValueSet;
    private String cdsListOpenCdsConceptType;
    private String cdsListItemKey;
    private String cdsListItemValue;
    private Collection<CdsConcept> opencdsConceptMappings;
    private Collection<String> cdsListVersions;
    private CD cdsListItemOutboundCD;
    private CD cdsListItemCD;
    private boolean supplementalText;

    /**
     * Create a SupportedListConceptItem object based on the CdsListSpecificationFile and a CdsListItem. The CdsListItem object must be one that is in the CdsListSpecificationFile,
     * based on its cdsListItemKey value. It must conform to _attributeNamingConvention.
     * <p>
     * The CdsList must contain populated cdsListCode and cdsListCodeSystem values (required values).
     * <p>
     * If any of these things occur, an IllegalArgumentException is thrown.
     *
     * @param pCdsLsf CdsListSpecificationFile containing common data elements - such as a code representing an associated value set - for all CdsListItems contained by it
     * @param pCdsLi  The CdsListItem
     */
    protected LocallyCodedCdsListItem(final CdsListSpecificationFile pCdsLsf, final CdsListItem pCdsLi)
            throws IllegalArgumentException
    {
        final String _METHODNAME = "LocallyCodedCdsListItem(): ";

        if (pCdsLsf == null || pCdsLi == null)
            return;

        this.cdsListType =
                Stream.of("VALUE_SET", "CODE_SYSTEM").filter(s -> s.equals(pCdsLsf.getListType())).findAny().orElseThrow(() ->
                {
                    final String lErrStr = "cdsListType \"" + pCdsLsf.getListType() + "\" not supported by this class";
                    log.error(_METHODNAME + "{}", lErrStr);
                    return new IllegalArgumentException(lErrStr);
                });

        this.cdsListId = pCdsLsf.getListId();
        this.cdsListCode = pCdsLsf.getCode();
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

        final String lCdsListItemKey = pCdsLi.getCdsListItemKey();
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

        final List<CdsListItem> lPCdsListItems = pCdsLsf.getCdsListItems();
        if (lPCdsListItems == null)
        {
            final String lErrStr = "specified cdsListItem not found in specified cdsListSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (lPCdsListItems.stream().noneMatch(lCdsListItem -> lCdsListItemKey.equals(lCdsListItem.getCdsListItemKey())))
        {
            final String lErrStr = "specified cdsListItem not found in specified cdsListSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsListDescription = pCdsLsf.getDescription();
        // this.cdsListEnumClass = pCdsLsf.getEnumClass();
        this.cdsListCodeSystem = pCdsLsf.getCodeSystem();
        this.cdsListValueSet = pCdsLsf.getValueSet();

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

        this.cdsListCodeSystemName = pCdsLsf.getCodeSystemName();
        this.cdsListOpenCdsConceptType = pCdsLsf.getOpenCdsConceptType();
        this.cdsListItemKey = lCdsListItemKey.replaceAll("[ \t\n\f\r]", "_");
        this.cdsListItemValue = pCdsLi.getCdsListItemValue();
        this.opencdsConceptMappings = new ArrayList<>();

        for (final CdsListItemConceptMapping clic : pCdsLi.getCdsListItemConceptMappings())
        {
            final CdsConcept ic = new CdsConcept(clic.getCode(), clic.getDisplayName());
            ic.setIsOpenCdsSupportedConcept(true);
            ic.setDeterminationMethodCode(clic.getConceptDeterminationMethod());
            // CdsListItemConceptMapping has codeSystem and codeSystemName attributes, but this is N/A for an OpenCDS concept code so not included here
            this.opencdsConceptMappings.add(ic);
        }

        this.cdsListVersions = pCdsLsf.getCdsVersions();
        this.cdsListItemName = "%s.%s".formatted(this.cdsListCode, this.cdsListItemKey);

        // Create
        if (pCdsLi.getOutboundCoding() != null)
        {
            this.cdsListItemOutboundCD = new CD();
            this.cdsListItemOutboundCD.setCode(pCdsLi.getOutboundCoding().getCode());
            this.cdsListItemOutboundCD.setDisplayName(pCdsLi.getOutboundCoding().getDisplayName());
            this.cdsListItemOutboundCD.setCodeSystem(pCdsLi.getOutboundCoding().getCodeSystem());
            this.cdsListItemOutboundCD.setCodeSystemName(pCdsLi.getOutboundCoding().getCodeSystemName());
            this.cdsListItemOutboundCD.setOriginalText(Optional.ofNullable(pCdsLi.getOutboundCoding().getOriginalText())
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
     * Return the associated code system or value set OID for this cdsListItem. Equivalent to getCdsListItemCD().getCodeSystem().
     */
    public String getCdsListCodeSystem()
    {
        // return this.cdsListCodeSystem;
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
