package org.cdsframework.cds.supportingdata;

import java.util.Collection;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.ConceptUtils;
import org.opencds.common.exceptions.ImproperUsageException;

public abstract class LocallyCodedCdsItem {

	private String cdsItemName;
	private CdsConcept cdsConcept;
	private Collection<String> cdsVersions;

	private static final Logger logger = LogManager.getLogger();
	
	/**
	 * Creates a LocallyCodedCdsItem. Both the CdsItemName and CdsVersions must be specified, or an ImproerUsageException is thrown is thrown. If the CdsItemName 
	 * does not follow the naming conventions set forth by LocallyCodedCdsListItem's attributeNameConformsToRequiredNamingConvention(), and furthermore that an call to 
	 * LocallyCodedCdsListItem.modifyAttributeNameToConformToRequiredNamingConvention would modify the supplied parameter (as it should not), an IllegalArgumentException 
	 * is thrown.
	 */
	public LocallyCodedCdsItem(String pCdsItemName, Collection<String> pCdsVersions) 
		throws ImproperUsageException, IllegalArgumentException {
		
		String _METHODNAME = "LocallyCodedCdsItem(): ";
		
		if (pCdsItemName == null || pCdsItemName.isEmpty()) {
			String lErrStr = "CdsItem name not specified";
			logger.warn(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		if (! pCdsItemName.equals(ConceptUtils.modifyAttributeNameToConformToRequiredNamingConvention(pCdsItemName))) {
			String lErrStr = "CdsItemName supplied \"" + pCdsItemName + "\" does not follow contains invalid characters; must conform to attribute naming conventions set forth by LocallyCodedCdsListItems";
			logger.warn(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(_METHODNAME);
		}
		if (pCdsVersions == null || pCdsVersions.isEmpty()) {
			String lErrStr = "CdsVersion(s) not specified";
			logger.warn(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		this.cdsItemName = pCdsItemName;
		this.cdsVersions = pCdsVersions;
	}

	/**
	 * Creates a LocallyCodedCdsItem. CdsItemName, CdsConceptName and CdsVersions must be specified, or an ImproerUsageException is thrown is thrown. If the CdsItemName  
	 * does not follow the naming conventions set forth by LocallyCodedCdsListItem's attributeNameConformsToRequiredNamingConvention(), and furthermore that an call to 
	 * LocallyCodedCdsListItem.modifyAttributeNameToConformToRequiredNamingConvention would modify the supplied parameter (as it should not), an IllegalArgumentException 
	 * is thrown. The CdsConceptName specifies the CdsConcept that is associated with this LocallyCodedCdsItem.
	 */
	public LocallyCodedCdsItem(String pCdsItemName, CdsConcept pCdsConcept, Collection<String> pCdsVersions) 
		throws ImproperUsageException, IllegalArgumentException {
		
		this(pCdsItemName, pCdsVersions);
		String _METHODNAME = "LocallyCodedCdsItem(): ";
		
		if (pCdsConcept == null || pCdsConcept.getOpenCdsConceptCode() == null) {				// the latter should not happen
			String lErrStr = "CdsConcept name not specified";
			logger.warn(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		this.cdsConcept = pCdsConcept;
	}

	public String getCdsItemName() {
		return cdsItemName;
	}
	
	public CdsConcept getCdsConcept() {
		return this.cdsConcept;
	}
	
	public String getCdsConceptName() {
		return this.cdsConcept.getOpenCdsConceptCode();
	}

	public Collection<String> getCdsVersions() {
		return cdsVersions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cdsItemName == null) ? 0 : cdsItemName.hashCode());
		return result;
	}

	/**
	 * Two LocallyCodedCdsItems are equal if their cdsItemNames are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocallyCodedCdsItem other = (LocallyCodedCdsItem) obj;
		if (cdsItemName == null) {
			if (other.cdsItemName != null)
				return false;
		} else if (!cdsItemName.equals(other.cdsItemName))
			return false;
		return true;
	}


	/*************************************************************************************************************************************************/
	
	/*
	 * Inclusion of cdsVersions in equality?
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cdsItemName == null) ? 0 : cdsItemName.hashCode());
		for (String s : cdsVersions) {
			// Order does not matter
			result = prime * result + ((s == null) ? 0 : s.hashCode());
		}
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
		LocallyCodedCdsItem other = (LocallyCodedCdsItem) obj;
		if (cdsItemName == null) {
			if (other.cdsItemName != null)
				return false;
		} else if (!cdsItemName.equals(other.cdsItemName))
			return false;
		if (cdsVersions == null) {
			if (other.cdsVersions != null)
				return false;
		} 
		else { 
			Collection<String> lInter = CollectionUtils.intersectionOfStringCollections(cdsVersions, other.cdsVersions);
			if (lInter == null) {
				return false;
			}
			else if (cdsVersions.size() == lInter.size()) {
				// Each collection contains the same set of versions, though they could be in a different order
				return true;
			}
			else {
				return false;
			}
		}
		return true;
	}
	*/
	
}
