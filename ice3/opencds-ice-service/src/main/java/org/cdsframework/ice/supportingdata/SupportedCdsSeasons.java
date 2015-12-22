package org.cdsframework.ice.supportingdata;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.util.ConceptUtils;
import org.cdsframework.util.support.data.ice.season.IceSeasonSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

public class SupportedCdsSeasons implements SupportingData {
	
	private Map<String, LocallyCodedSeasonItem> seasonItemNameToSeasonItem;	// cdsListItemName (cdsListCode.cdsListItemKey) to Vaccine 	
	private SupportedCdsVaccineGroups supportedVaccineGroups;				// Supporting vaccine groups from which this season data is built
	
	private static Log logger = LogFactory.getLog(SupportedCdsSeasons.class);	

	
	protected SupportedCdsSeasons(SupportedCdsVaccineGroups pSupportedVaccineGroups) {

		this.supportedVaccineGroups = pSupportedVaccineGroups;
	}

	
	public boolean isEmpty() {		
		// TODO:
		return true;
	}
	
	
	protected void addSupportedSeasonItemFromIceSeasonSpecificationFile(IceSeasonSpecificationFile pIceSeasonSpecificationFile) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportedSeasonItemFromIceSeasonSpecificationFile(): ";
		
		if (pIceSeasonSpecificationFile == null || this.supportedVaccineGroups == null) {
			return;
		}
		
		String lSeasonCode = pIceSeasonSpecificationFile.getCode();
		if (lSeasonCode == null) {
			String lErrStr = "Required supporting data seasonCode element not provided in IceSeasonSpecificationFile";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		///////
		// Check to make sure that this season code has not already been defined
		///////
		if (this.seasonItemNameToSeasonItem.containsKey(lSeasonCode)) {
			String lErrStr = "Attempt to add a Season that was already specified previously: " + lSeasonCode;
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		///////
		// Check to make sure that the vaccine group specified is a valid vaccine group; one that has been previously specified
		CD lVaccineGroupCD = ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup());
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD)) {
			String lErrStr = "Required supporting data item vaccineGroup element not provided in IceVaccineGroupSpecificationFile:";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		// Check to make sure that the specified vaccine group is a supported vaccine group
		LocallyCodedCdsListItem lccli = this.supportedVaccineGroups.getAssociatedSupportedCdsLists().getCdsListItem(lVaccineGroupCD);
		if (lccli == null) {
			String lErrStr = "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsLists: " + 
					(pIceSeasonSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);			
		}		
		LocallyCodedVaccineGroupItem lcvgi = this.supportedVaccineGroups.getVaccineGroupItem(lccli.getCdsListItemName());
		if (lcvgi == null) {
			String lErrStr = "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsVaccineGroups: " + 
					(pIceSeasonSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);			
		}
			
		/**
		 * Examples
		 * 
			List<Season> influenzaSeasons = new ArrayList<Season>();
			influenzaSeasons.add(new Season("2015-2016 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2015, 6, 30, 2016));
			influenzaSeasons.add(new Season("2014-2015 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2014, 6, 30, 2015));
			influenzaSeasons.add(new Season("2013-2014 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2013, 6, 30, 2014));
			influenzaSeasons.add(new Season("2012-2013 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2012, 6, 30, 2013));
			SeriesRules influenza2DoseSeriesRules = new SeriesRules("Influenza 2-Dose Series", SupportedVaccineGroupConcept.Influenza, influenzaSeasons);
			
			// Default Season
			Season influenzaSeasonDefault = new Season("Default Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 6, 30);
			List<Season> influenzaDefaultSeasons = new ArrayList<Season>();
			influenzaDefaultSeasons.add(influenzaSeasonDefault);		
	
			// Create encompassing Series Rule for "Influena 2-Dose Series" /////////////////////////////////////////////////////
			SeriesRules influenza2DoseDefaultSeriesRules = new SeriesRules("Influenza 2-Dose Default Series", SupportedVaccineGroupConcept.Influenza, influenzaDefaultSeasons);			
		  *
		  **/
	}
	
}
