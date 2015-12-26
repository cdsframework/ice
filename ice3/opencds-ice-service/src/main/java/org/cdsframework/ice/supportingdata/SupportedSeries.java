package org.cdsframework.ice.supportingdata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.util.support.data.ice.series.IceSeriesSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;

public class SupportedSeries implements SupportingData {

	private SupportedVaccineGroups supportedVaccineGroups;						// Supporting vaccine groups from which this series data is built
	private SupportedVaccines supportedVaccines;
	private SupportedSeasons supportedSeasons;
	
	private static Log logger = LogFactory.getLog(SupportedSeasons.class);	

	
	/**
	 * Create a SupportedSeries object. If the ICESupportingDataConfiguration or its associated SupportedCdsLists, SupportedVaccineGroups, or SupportedSeasons argument is null, 
	 * an IllegalArgumentException is thrown.
	 */
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
		
		return true;
	}
	
	
	public void addSupportedSeriesItemFromIceSeriesSpecificationFile(IceSeriesSpecificationFile pIceSeriesSpecificationFile) {
	
		String _METHODNAME = "addSupportedSeriesItemFromIceSeriesSpecificationFile(): ";
		
		
		/**
		 * Examples
		 * 		
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// DTP Vaccine Group
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
			///////
			// Create Series "DTP 5-Dose Series" //////////////////////////////////////////////////////////////////////////////////
			// Allowable CVX codes: 01, 20, 28, 106, 107, 115*, 09*, 113*, 138*, 139*, 22, 50, 102, 110, 120, 130, 132, 146
			///////
	
			// DTP allowable vaccines
			List<Vaccine> dtpVaccines = new ArrayList<Vaccine>();
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_5PERTUSSIS_ANTIGENS));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_UNSPECIFIED));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HIB));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTP_HIB_HEPB));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HEPB_IPV));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_HIB_IPV));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_IPV));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_IPV_HIB_HEPB_HISTORICAL));
			dtpVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DTAP_IPV_HIB_HEPB));
	
			// Tdap vccines
			List<Vaccine> tdapVaccines = new ArrayList<Vaccine>();
			tdapVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TDAP));
			
			// Td vaccines
			List<Vaccine> tdVaccines = new ArrayList<Vaccine>();
			tdVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._DT));
			tdVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TD_ABSORBED));
			tdVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TD_NOTABSORBED));
			tdVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TD_NOS));
			tdVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TD_PRESERVATIVEFREE));
			tdVaccines.add(supportedVaccinesMap.get(SupportedVaccineConcept._TDAP));
			
			DoseRule dtpSeries1DoseRule1 = new DoseRule();
			dtpSeries1DoseRule1.setDoseNumber(1);
			dtpSeries1DoseRule1.setAbsoluteMinimumAge(new TimePeriod("38d"));
			dtpSeries1DoseRule1.setMinimumAge(new TimePeriod("42d"));
			dtpSeries1DoseRule1.setEarliestRecommendedAge(new TimePeriod("2m"));
			dtpSeries1DoseRule1.setAbsoluteMinimumInterval(new TimePeriod("24d"));
			dtpSeries1DoseRule1.setMinimumInterval(new TimePeriod("28d"));
			dtpSeries1DoseRule1.setEarliestRecommendedInterval(new TimePeriod("28d"));
			dtpSeries1DoseRule1.setPreferableVaccines(dtpVaccines);
			dtpSeries1DoseRule1.setAllowableVaccines(tdapVaccines);
			dtpSeries1DoseRule1.addAllowableVaccines(tdVaccines);
			
			DoseRule dtpSeries1DoseRule2 = new DoseRule();
			dtpSeries1DoseRule2.setDoseNumber(2);
			dtpSeries1DoseRule2.setAbsoluteMinimumAge(new TimePeriod("66d"));
			dtpSeries1DoseRule2.setMinimumAge(new TimePeriod("70d"));
			dtpSeries1DoseRule2.setEarliestRecommendedAge(new TimePeriod("4m"));
			dtpSeries1DoseRule2.setAbsoluteMinimumInterval(new TimePeriod("24d"));
			dtpSeries1DoseRule2.setMinimumInterval(new TimePeriod("28d"));
			dtpSeries1DoseRule2.setEarliestRecommendedInterval(new TimePeriod("28d"));
			dtpSeries1DoseRule2.setPreferableVaccines(dtpVaccines);
			dtpSeries1DoseRule2.setAllowableVaccines(tdapVaccines);
			dtpSeries1DoseRule2.addAllowableVaccines(tdVaccines);
			
			DoseRule dtpSeries1DoseRule3 = new DoseRule();
			dtpSeries1DoseRule3.setDoseNumber(3);
			dtpSeries1DoseRule3.setAbsoluteMinimumAge(new TimePeriod("94d"));
			dtpSeries1DoseRule3.setMinimumAge(new TimePeriod("98d"));
			dtpSeries1DoseRule3.setEarliestRecommendedAge(new TimePeriod("6m"));
			dtpSeries1DoseRule3.setAbsoluteMinimumInterval(new TimePeriod("4m"));
			dtpSeries1DoseRule3.setMinimumInterval(new TimePeriod("6m"));
			dtpSeries1DoseRule3.setEarliestRecommendedInterval(new TimePeriod("6m"));
			dtpSeries1DoseRule3.setPreferableVaccines(dtpVaccines);
			dtpSeries1DoseRule3.setAllowableVaccines(tdapVaccines);
			dtpSeries1DoseRule3.addAllowableVaccines(tdVaccines);
	
			DoseRule dtpSeries1DoseRule4 = new DoseRule();
			dtpSeries1DoseRule4.setDoseNumber(4);
			dtpSeries1DoseRule4.setAbsoluteMinimumAge(new TimePeriod("361d"));
			dtpSeries1DoseRule4.setMinimumAge(new TimePeriod("365d"));
			dtpSeries1DoseRule4.setEarliestRecommendedAge(new TimePeriod("15m"));
			dtpSeries1DoseRule4.setAbsoluteMinimumInterval(new TimePeriod("6m-4d"));
			dtpSeries1DoseRule4.setMinimumInterval(new TimePeriod("6m"));
			dtpSeries1DoseRule4.setEarliestRecommendedInterval(new TimePeriod("6m"));
			dtpSeries1DoseRule4.setPreferableVaccines(dtpVaccines);
			dtpSeries1DoseRule4.setAllowableVaccines(tdapVaccines);
			dtpSeries1DoseRule4.addAllowableVaccines(tdVaccines);
	
			DoseRule dtpSeries1DoseRule5 = new DoseRule();
			dtpSeries1DoseRule5.setDoseNumber(5);
			dtpSeries1DoseRule5.setAbsoluteMinimumAge(new TimePeriod("4y-4d"));
			dtpSeries1DoseRule5.setMinimumAge(new TimePeriod("4y"));
			dtpSeries1DoseRule5.setEarliestRecommendedAge(new TimePeriod("4y"));
			dtpSeries1DoseRule5.setPreferableVaccines(dtpVaccines);
			dtpSeries1DoseRule5.setAllowableVaccines(tdapVaccines);
			dtpSeries1DoseRule5.addAllowableVaccines(tdVaccines);
	
			// Create encompassing Series Rule for "DTP 5-Dose Series" /////////////////////////////////////////////////////
			SeriesRules dtpSeries1Rules = new SeriesRules("DTP 5-Dose Series", SupportedVaccineGroupConcept.DTP);
			tds = new ArrayList<DoseRule>();
			tds.add(dtpSeries1DoseRule1);
			tds.add(dtpSeries1DoseRule2);
			tds.add(dtpSeries1DoseRule3);
			tds.add(dtpSeries1DoseRule4);
			tds.add(dtpSeries1DoseRule5);
			dtpSeries1Rules.setSeriesDoseRules(tds);
			dtpSeries1Rules.setRecurringDosesAfterSeriesComplete(true);
			dtpSeries1Rules.setDoseNumberCalculatationBasedOnDiseasesTargetedByEachVaccineAdministered(false);
	
			// Insert Series into the Schedule ///////////////////////////////////////////////////////////////////////////////////////
			addSeriesToSchedule(dtpSeries1Rules);
		*/
	}
	
}
