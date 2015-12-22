package org.cdsframework.ice.supportingdata;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsItem;
import org.cdsframework.ice.service.Vaccine;
import org.opencds.common.exceptions.ImproperUsageException;

public class LocallyCodedVaccineItem extends LocallyCodedCdsItem {
	
	private Vaccine vaccine;
	
	private static Log logger = LogFactory.getLog(LocallyCodedVaccineItem.class);
	
	/**
	 * Create a LocallyCodedVaccineItem. All parameters to this method must be specified, or an ImproperUsageException is thrown.
	 */
	protected LocallyCodedVaccineItem(String pVaccineCdsListItemName, Collection<String> pCdsVersions, Vaccine pVaccine) 
		throws ImproperUsageException {
		
		super(pVaccineCdsListItemName, pCdsVersions);
		
		String _METHODNAME = "LocallyCodedVaccineItem(): ";
		if (pVaccine == null) {
			String lErrStr = "Vaccine parameter not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}

		this.vaccine = pVaccine;
	}

	
	public Vaccine getVaccine() {
		return vaccine;
	}

}
