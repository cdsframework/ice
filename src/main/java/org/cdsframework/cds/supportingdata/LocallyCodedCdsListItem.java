/*
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private String cdsListName;
    private String cdsListType;
    private String cdsListDescription;
    // private String cdsListEnumClass;
    private String cdsListCodeSystem;
    private String cdsListCodeSystemName;
    private String cdsListValueSet;
    private String cdsListOpenCdsConceptType;
    private String cdsListItemKey;
    private String cdsListItemValue;
    private Collection<CdsConcept> opencdsConceptMappings;
    private Collection<String> cdsListVersions;
    private CD cdsListItemCD;

    /**
     * Create a SupportedListConceptItem object based on the CdsListSpecificationFile and a CdsListItem. The CdsListItem object must be one that is in the CdsListSpecificationFile,
     * based on its cdsListItemKey value. It must conform to _attributeNamingConvention.
     * <p>
     * The CdsList must contain populated cdsListCode and a cdsListCodeSystem values (required values).
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

        // Create CD
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
                .append("\ncdsListName=")
                .append(cdsListName)
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
