package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.VaccineSD;
import org.opencds.common.exceptions.ImproperUsageException;

public class LocallyCodedVaccineItem {
	
	private String vaccineCdsListItemName;
	private Collection<String> cdsVersions;
	private VaccineSD vaccine;
	
	private static Log logger = LogFactory.getLog(LocallyCodedVaccineItem.class);
	
	protected LocallyCodedVaccineItem(String pVaccineCdsListItemName, Collection<String> pCdsVersions, VaccineSD pVaccine) 
		throws ImproperUsageException {
		
		String _METHODNAME = "LocallyCodedVaccineItem(): ";
		if (pVaccineCdsListItemName == null || pVaccineCdsListItemName.isEmpty()) {
			String lErrStr = "Vaccine Cds List Item name not specified";
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
		
		this.vaccineCdsListItemName = pVaccineCdsListItemName;
		this.cdsVersions = pCdsVersions;
		this.vaccine = pVaccine;
	}

	public String getVaccineCdsListItemName() {
		return vaccineCdsListItemName;
	}

	public Collection<String> getCdsVersions() {
		return cdsVersions;
	}

	public VaccineSD getVaccine() {
		return vaccine;
	}

}
