package org.cdsframework.ice.supportingdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.opencds.common.exceptions.ImproperUsageException;

public class SupportedSeries implements SupportingData {

	private SupportedVaccineGroups supportedVaccineGroups;						// Supporting vaccine groups from which this series data is built
	private SupportedVaccines supportedVaccines;
	private SupportedSeasons supportedSeasons;
	
	private static Log logger = LogFactory.getLog(SupportedSeasons.class);	

	
	protected SupportedSeries(ICESupportingDataConfiguration isdc) 
		throws ImproperUsageException, IllegalArgumentException {

		String _METHODNAME = "SupportedCdsSeries(): ";
		if (isdc == null) {
			String lErrStr = "ICESupportingDataConfiguration argument is null; a valid argument must be provided.";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}
		
		this.supportedVaccineGroups = isdc.getSupportedVaccineGroups();
		if (this.supportedVaccineGroups == null) {
			String lErrStr = "Supporting vaccine group data not set in ICESupportingDataConfiguration; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		this.supportedVaccines = isdc.getSupportedVaccines();
		if (this.supportedVaccines == null) {
			String lErrStr = "Supporting vaccine data not set in ICESupportingDataConfiguration; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		this.supportedSeasons = isdc.getSupportedSeasons();
		if (this.supportedSeasons == null) {
			String lErrStr = "Supporting season data not set in ICESupportingDataConfiguration; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
	}

	
	public boolean isEmpty() {		
		
		// TODO:
		return true;
	}
}
