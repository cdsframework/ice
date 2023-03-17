/*
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListItemConceptMapping;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;


public class LocallyCodedCdsListItem {

	/**
	 * Local codes mapped to concepts 
	 * supportedCdsListTypes = { "VALUE_SET", "CODE_SYSTEM" };
	 * e.g.
	   <cdsListSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.cds.list">
    	<listId>1f30111EET5bc0568822e2608f22be9d1bac9c</listId>
    	<code>SUPPORTED_VACCINES</code>
    	<name>Supported Vaccines</name>
    	<listType>CODE_SYSTEM</listType>
    	<description>CVX code vaccines supported by ICE</description>
    	<codeSystem>2.16.840.1.113883.12.292</codeSystem>
    	<codeSystemName>Vaccines (CVX)</codeSystemName>
    	<cdsListItem>
    		<cdsListItemKey>08</cdsListItemKey>
    		<cdsListItemValue>Hep B peds, less than 20yrs</cdsListItemValue>
    		<cdsListItemConceptMapping>
    			<code>ICE08</code>
    			<displayName>Hep B peds, less than 20yrs</displayName>
    		</cdsListItemConceptMapping>
    	</cdsListItem>
    	</cdsVersion>
	 * </cdsListSpecificationFile>
	 * @author daryl
	 *
	 */

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

	private static final Logger logger = LogManager.getLogger();

	
	/**
	 * Create a SupportedListConceptItem object based on the CdsListSpecificationFile and a CdsListItem. The CdsListItem object must be one that is in the CdsListSpecificationFile,
	 * based on its cdsListItemKey value. It must conform to _attributeNamingConvention. 
	 * 
	 * The CdsList must contain populated cdsListCode and a cdsListCodeSystem values (required values).
	 *  
	 * If any of these things occur, an ImproperUsageException is thrown.
	 * @param pCdsLsf CdsListSpecificationFile containing common data elements - such as a code representing an associated value set - for all CdsListItems contained by it
	 * @param pCdsLi The CdsListItem
	 * @throws ImproperUsageException
	 */
	protected LocallyCodedCdsListItem(CdsListSpecificationFile pCdsLsf, CdsListItem pCdsLi) 
		throws ImproperUsageException {

		String _METHODNAME = "LocallyCodedCdsListItem(): ";

		if (pCdsLsf == null || pCdsLi == null) {
			return;
		}

		String[] supportedCdsListTypes = { "VALUE_SET", "CODE_SYSTEM" };

		boolean lSupportedByThisClass = false;
		for (String supportedCdsListType : supportedCdsListTypes) {
			if (supportedCdsListType.equals(pCdsLsf.getListType())) {
				lSupportedByThisClass = true;
				this.cdsListType = pCdsLsf.getListType();
				break;
			}
		}
		if (lSupportedByThisClass == false) {
			String lErrStr = "cdsListType \"" + pCdsLsf.getListType() + "\" not supported by this class";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);				
		}
		this.cdsListId = pCdsLsf.getListId();
		this.cdsListCode = pCdsLsf.getCode();			
		if (cdsListCode != null) {
			if (! ConceptUtils.attributeNameConformsToRequiredNamingConvention(cdsListCode)) {
				String lErrStr = "required element cdsListCode \"" + this.cdsListCode + "\"  contains invalid characters " + ConceptUtils._attributeNamingConvention;
				logger.error(_METHODNAME + lErrStr);
				throw new ImproperUsageException(lErrStr);
			}
			cdsListCode.replaceAll("[ \t\n\f\r]", "_");
		}
		else {
			String lErrStr = "required element cdsListCode not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		String lCdsListItemKey = pCdsLi.getCdsListItemKey();
		if (lCdsListItemKey != null) {
			if (! ConceptUtils.attributeNameConformsToRequiredNamingConvention(lCdsListItemKey)) {
				String lErrStr = "required element cdsListItemKey \"" + lCdsListItemKey + "\" contains invalid characters; must conform to " + ConceptUtils._attributeNamingConvention;
				logger.error(_METHODNAME + lErrStr);
				throw new ImproperUsageException(lErrStr);
			}
		}
		else {
			String lErrStr = "required element cdsListItemKey not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		List<CdsListItem> lPCdsListItems = pCdsLsf.getCdsListItems();
		if (lPCdsListItems == null) {
			String lErrStr = "specified cdsListItem not found in specified cdsListSpecificationFile";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		else {
			boolean matchingCdsListItemFoundInCdsSpecificationFile = false;
			for (CdsListItem lCdsListItem : lPCdsListItems) {
				if (lCdsListItemKey.equals(lCdsListItem.getCdsListItemKey())) {
					matchingCdsListItemFoundInCdsSpecificationFile = true;
					break;
				}
			}
			if (matchingCdsListItemFoundInCdsSpecificationFile == false) {
				String lErrStr = "specified cdsListItem not found in specified cdsListSpecificationFile";
				logger.error(_METHODNAME + lErrStr);
				throw new ImproperUsageException(lErrStr);
			}
		}

		this.cdsListDescription = pCdsLsf.getDescription();
		// this.cdsListEnumClass = pCdsLsf.getEnumClass();
		this.cdsListCodeSystem = pCdsLsf.getCodeSystem();
		this.cdsListValueSet = pCdsLsf.getValueSet();
		if (this.cdsListCodeSystem == null && this.cdsListValueSet == null) {
			String lErrStr = "specified cdsList does not have a specified code system or value set OID";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		if (this.cdsListCodeSystem != null && this.cdsListValueSet != null) {
			String lErrStr = "specified cdsList has both a code system OID and value set OID set";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		this.cdsListCodeSystemName = pCdsLsf.getCodeSystemName();
		this.cdsListOpenCdsConceptType = pCdsLsf.getOpenCdsConceptType();			
		this.cdsListItemKey = lCdsListItemKey.replaceAll("[ \t\n\f\r]", "_");
		this.cdsListItemValue = pCdsLi.getCdsListItemValue();
		this.opencdsConceptMappings = new ArrayList<CdsConcept>();
		List<CdsListItemConceptMapping> clicm = pCdsLi.getCdsListItemConceptMappings();
		for (CdsListItemConceptMapping clic : clicm) {
			CdsConcept ic = new CdsConcept(clic.getCode(), clic.getDisplayName());
			ic.setIsOpenCdsSupportedConcept(true);
			ic.setDeterminationMethodCode(clic.getConceptDeterminationMethod());
			// CdsListItemConceptMapping has codeSystem and codeSystemName attributes, but this is N/A for an OpenCDS concept code so not included here
			this.opencdsConceptMappings.add(ic);
		}

		this.cdsListVersions = pCdsLsf.getCdsVersions();
		this.cdsListItemName = this.cdsListCode + "." + this.cdsListItemKey; 
		
		// Create CD
		this.cdsListItemCD = new CD();
		this.cdsListItemCD.setCode(this.cdsListItemKey);
		this.cdsListItemCD.setDisplayName(this.cdsListItemValue);
		if (this.cdsListValueSet != null) {
			this.cdsListItemCD.setCodeSystem(this.cdsListValueSet);
		}
		else {
			this.cdsListItemCD.setCodeSystem(this.cdsListCodeSystem);
		}
		this.cdsListItemCD.setCodeSystemName(this.cdsListCodeSystemName);
	}


	/**
	 * Returns the CdsListItemName that has been assigned to this CdsListItem. The CdsListItemName is always populated.
	 */
	public String getCdsListItemName() {
		return this.cdsListItemName;
	}
	
	public String getCdsListId() {
		return this.cdsListId;
	}

	public String getCdsListCode() {
		return this.cdsListCode;
	}

	public String getCdsListName() {
		return this.cdsListName;
	}

	public String getCdsListType() {
		return this.cdsListType;
	}

	public String getCdsListDescription() {
		return this.cdsListDescription;
	}

	public Collection<String> getCdsListVersions() {
		
		return this.cdsListVersions;
	}
	
	/**
	 * Return the associated code system or value set OID for this cdsListItem. Equivalent to getCdsListItemCD().getCodeSystem().
	 */
	public String getCdsListCodeSystem() {
		// return this.cdsListCodeSystem;
		return this.cdsListItemCD.getCodeSystem();
	}

	/**
	 * Return the associated code system or value set name for this cdsListItem. Equivalent to getCdsListItemCD().getCodeSystemName().
	 */
	public String getCdsListCodeSystemName() {
		return this.cdsListCodeSystemName;
	}

	/*
	public String getCdsListValueSet() {
		return this.cdsListValueSet;
	}
	*/
	
	/**
	 * Equivalent to getCdsListItemCD().getCode();
	 */
	public String getCdsListItemKey() {
		return this.getCdsListItemCD().getCode();
	}

	/**
	 * Equivalent to getCdsListItemCD.getDisplayName();
	 */
	public String getCdsListItemValue() {
		return this.getCdsListItemCD().getDisplayName();
	}

	public CD getCdsListItemCD() {
		return this.cdsListItemCD;
	}

	public String getCdsListOpenCdsConceptType() {
		return this.cdsListOpenCdsConceptType;
	}

	public Collection<CdsConcept> getCdsListItemOpencdsConceptMappings() {
		return this.opencdsConceptMappings;
	}

	@Override
	public String toString() {

		String lStr = "[SupportedCdsListItem=" + cdsListItemName + "\ncdsListId=" + cdsListId	+ 
				"\ncdsListCode=" + cdsListCode + "\ncdsListName=" + cdsListName + "\ncdsListType=" + cdsListType + "\ncdsListDescription=" + cdsListDescription +
				"\ncdsListCodeSystem=" + cdsListCodeSystem + "\ncdsListCodeSystemName=" + cdsListCodeSystemName + "\ncdsListValueSet=" + cdsListValueSet + 
				"\ncdsListOpenCdsConceptType=" + cdsListOpenCdsConceptType + "\ncdsListItemKey=" + cdsListItemKey +	"\ncdsListItemValue=" + cdsListItemValue;
		lStr += "\nopencdsConceptMappings= [";

		for (CdsConcept icc : getCdsListItemOpencdsConceptMappings()) {
			lStr += "\tICEConcept=" + icc.toString() + "\n";
		}
		lStr += "\t]\n";		
		for (String lVersionStr : getCdsListVersions()) {
			lStr += "\tCdsVersion=" + lVersionStr + "\n";
		}
		lStr += "]";
		lStr += "\n";

		return lStr;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((cdsListItemName == null) ? 0
						: cdsListItemName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocallyCodedCdsListItem other = (LocallyCodedCdsListItem) obj;
		if (cdsListItemName == null) {
			if (other.cdsListItemName != null)
				return false;
		} 
		else if (!cdsListItemName.equals(other.cdsListItemName))
			return false;
		return true;
	}
}
