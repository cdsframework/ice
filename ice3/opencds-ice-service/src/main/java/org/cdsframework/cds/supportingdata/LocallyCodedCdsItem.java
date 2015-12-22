package org.cdsframework.cds.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.ImproperUsageException;

public abstract class LocallyCodedCdsItem {

	private String cdsItemName;
	private Collection<String> cdsVersions;

	private static Log logger = LogFactory.getLog(LocallyCodedCdsItem.class);
	
	/**
	 * Creates a LocallyCodedCdsItem. Both the CdsItemName and CdsVersions must be specified, or an IllegalArgumentException is thrown.
	 */
	public LocallyCodedCdsItem(String pCdsItemName, Collection<String> pCdsVersions) 
		throws ImproperUsageException {
		
		String _METHODNAME = "LocallyCodedCdsItem(): ";
		
		if (pCdsItemName == null || pCdsItemName.isEmpty()) {
			String lErrStr = "CdsItem name not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}
		if (pCdsVersions == null || pCdsVersions.isEmpty()) {
			String lErrStr = "CdsVersion(s) not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		this.cdsItemName = pCdsItemName;
		this.cdsVersions = pCdsVersions;
	}

	public String getCdsItemName() {
		return cdsItemName;
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
}
