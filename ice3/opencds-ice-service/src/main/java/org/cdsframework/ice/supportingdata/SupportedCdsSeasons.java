package org.cdsframework.ice.supportingdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;

public class SupportedCdsSeasons implements SupportingData {
	
	// CDS versions covered by this supporting datavaccine manager
	private List<String> cdsVersions;
	
	private Map<String, LocallyCodedVaccineItem> cdsListItemNameToVaccineItem;		// cdsListItemName (cdsListCode.cdsListItemKey) to Vaccine 
	
	// Supporting Data Cds List from which this vaccine supporting data is built
	private SupportedCdsLists supportedCdsLists;
		
	private static Log logger = LogFactory.getLog(SupportedCdsSeasons.class);	

	
	protected SupportedCdsSeasons(List<String> pCdsVersions, SupportedCdsLists pSupportedCdsLists) {

		if (pCdsVersions == null) {
			this.cdsVersions = new ArrayList<String>();
		}
		else {
			this.cdsVersions = pCdsVersions;
		}
		
		if (pSupportedCdsLists == null) {
			this.supportedCdsLists = new SupportedCdsLists(this.cdsVersions);
		}
		else {
			this.supportedCdsLists = pSupportedCdsLists;
		}
	}
	
	public boolean isEmpty() {
		
		// TODO:
		return true;
	}
	
}
