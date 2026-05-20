package org.cdsframework.cds.supportingdata;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.springframework.util.ObjectUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public abstract class LocallyCodedCdsItem
{
    @EqualsAndHashCode.Include
    private final String cdsItemName;
    private CdsConcept cdsConcept;

    /**
     * Creates a LocallyCodedCdsItem. Both the CdsItemName and CdsVersions must be specified, or an ImproerUsageException is thrown is thrown. If the CdsItemName
     * does not follow the naming conventions set forth by LocallyCodedCdsListItem's attributeNameConformsToRequiredNamingConvention(), and furthermore that an call to
     * LocallyCodedCdsListItem.modifyAttributeNameToConformToRequiredNamingConvention would modify the supplied parameter (as it should not), an IllegalArgumentException
     * is thrown.
     */
    public LocallyCodedCdsItem(final String pCdsItemName) throws IllegalArgumentException
    {
        final String _METHODNAME = "LocallyCodedCdsItem(): ";

        if (ObjectUtils.isEmpty(pCdsItemName))
        {
            final String lErrStr = "CdsItem name not specified";
            log.warn(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        ConceptUtils.modifyAttributeNameToConformToRequiredNamingConvention(pCdsItemName);
        this.cdsItemName = pCdsItemName;
    }

    /**
     * Creates a LocallyCodedCdsItem. CdsItemName, CdsConceptName and CdsVersions must be specified, or an ImproerUsageException is thrown is thrown. If the CdsItemName
     * does not follow the naming conventions set forth by LocallyCodedCdsListItem's attributeNameConformsToRequiredNamingConvention(), and furthermore that an call to
     * LocallyCodedCdsListItem.modifyAttributeNameToConformToRequiredNamingConvention would modify the supplied parameter (as it should not), an IllegalArgumentException
     * is thrown. The CdsConceptName specifies the CdsConcept that is associated with this LocallyCodedCdsItem.
     */
    public LocallyCodedCdsItem(final String pCdsItemName, final CdsConcept pCdsConcept) throws IllegalArgumentException
    {
        this(pCdsItemName);

        final String _METHODNAME = "LocallyCodedCdsItem(): ";

        if (pCdsConcept == null || pCdsConcept.getOpenCdsConceptCode() == null)
        {                // the latter should not happen
            final String lErrStr = "CdsConcept name not specified";
            log.warn(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.cdsConcept = pCdsConcept;
    }

    public String getCdsConceptName()
    {
        return this.cdsConcept.getOpenCdsConceptCode();
    }
}
