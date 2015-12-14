package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.VaccineSD;
import org.opencds.common.exceptions.ImproperUsageException;

public class LocallyCodedVaccineItem {
	
	private String cdsListItemName;
	private Collection<String> cdsVersions;
	private VaccineSD vaccine;
	
	private static Log logger = LogFactory.getLog(LocallyCodedVaccineItem.class);
	
	/**
	 * Create a LocallyCodedVaccineItem. All parameters to this method must be specified, or an ImproperUsageException is thrown.
	 */
	protected LocallyCodedVaccineItem(String pVaccineCdsListItemName, Collection<String> pCdsVersions, VaccineSD pVaccine) 
		throws ImproperUsageException {
		
		String _METHODNAME = "LocallyCodedVaccineItem(): ";
		if (pVaccineCdsListItemName == null || pVaccineCdsListItemName.isEmpty()) {
			String lErrStr = "Vaccine CdsList Item name not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		if (pCdsVersions == null || pCdsVersions.isEmpty()) {
			String lErrStr = "CdsVersion(s) not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		if (pVaccine == null) {
			String lErrStr = "Vaccine parameter not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		this.cdsListItemName = pVaccineCdsListItemName;
		this.cdsVersions = pCdsVersions;
		this.vaccine = pVaccine;
	}

	public String getCdsListItemName() {
		return cdsListItemName;
	}

	public Collection<String> getCdsVersions() {
		return cdsVersions;
	}

	public VaccineSD getVaccine() {
		return vaccine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cdsListItemName == null) ? 0 : cdsListItemName.hashCode());
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
		LocallyCodedVaccineItem other = (LocallyCodedVaccineItem) obj;
		if (cdsListItemName == null) {
			if (other.cdsListItemName != null)
				return false;
		} else if (!cdsListItemName.equals(other.cdsListItemName))
			return false;
		return true;
	}

}
