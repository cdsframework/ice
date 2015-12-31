package org.cdsframework.ice.supportingdata;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.DoseRule;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.service.SeriesRules;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.ConceptUtils;
import org.cdsframework.ice.util.StringUtils;
import org.cdsframework.ice.util.TimePeriod;
import org.cdsframework.util.support.data.ice.series.IceDoseIntervalSpecification;
import org.cdsframework.util.support.data.ice.series.IceDoseVaccineSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesDoseSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;


public class SupportedSeries implements SupportingData {

	private SupportedCdsLists supportedCdsLists;
	private SupportedVaccineGroups supportedVaccineGroups;						// Supporting vaccine groups from which this series data is built
	private SupportedVaccines supportedVaccines;
	private SupportedSeasons supportedSeasons;

	private Map<String, LocallyCodedSeriesItem> cdsListItemNameToSeriesItem;	// cdsListItemName (cdsListCode.cdsListItemKey) to LocallyCodedSeasonItem
	private Map<LocallyCodedVaccineGroupItem, List<SeriesRules>> vaccineGroupItemToSeriesRules;
	
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
		
		this.supportedCdsLists = isdc.getSupportedCdsLists();
		if (this.supportedCdsLists == null) {
			String lErrStr = "Supporting CdsList data not set in ICESupportingDataConfiguration; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
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
		
		this.cdsListItemNameToSeriesItem = new HashMap<String,LocallyCodedSeriesItem>();
		this.vaccineGroupItemToSeriesRules = new HashMap<LocallyCodedVaccineGroupItem, List<SeriesRules>>();
	}

	
	public boolean isEmpty() {		
		
		if (this.cdsListItemNameToSeriesItem.size() == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Add the Series specified in the IceSeriesSpecificationFile to the supporting data tracked by this class.
	 * @param pIceSeriesSpecificationFile
	 * @throws InconsistentConfigurationException If data supplied in the supporting data file is inconsistent in some way
	 * @throws IllegalArgumentException If any data supplied in the supporting data file is not permitted
	 */
	public void addSupportedSeriesItemFromIceSeriesSpecificationFile(IceSeriesSpecificationFile pIceSeriesSpecificationFile) 
		throws InconsistentConfigurationException, IllegalArgumentException {
	
		String _METHODNAME = "addSupportedSeriesItemFromIceSeriesSpecificationFile(): ";
		
		if (pIceSeriesSpecificationFile == null) {
			String lWarnStr = "IceSeriesSpecificationFile parameter is null; cannot process and returning";
			logger.warn(_METHODNAME + lWarnStr);
			return;			
		}
		
		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lCdsVersions = CollectionUtils.intersectionOfCollections(pIceSeriesSpecificationFile.getCdsVersions(), this.supportedCdsLists.getCdsVersions());
		if (CollectionUtils.isNullOrEmpty(lCdsVersions)) {
			return;
		}
		
		///////
		// Series code must be specified and not previously specified (unique)
		///////
		String lSeriesCode = pIceSeriesSpecificationFile.getCode();
		if (StringUtils.isNullOrEmpty(lSeriesCode)) {
			String lErrStr = "Series code not specified in supporting data file. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		else {
			lSeriesCode = LocallyCodedCdsListItem.modifyAttributeNameToConformToRequiredNamingConvention(lSeriesCode);
		}
		if (this.cdsListItemNameToSeriesItem.containsKey(lSeriesCode)) {
			String lErrStr = "Attempt to add a Series that was already specified previously; series code:  " + lSeriesCode;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		///////
		// Get the associated Vaccine Group for this series. (Must specify one and it must have been previously specified LocallyCodedVaccineGroupItem)
		///////
		Collection<org.opencds.vmr.v1_0.schema.CD> lVaccineGroups = pIceSeriesSpecificationFile.getVaccineGroups();
		if (CollectionUtils.isNullOrEmpty(lVaccineGroups)) {
			String lErrStr = "Vaccine groups not specified in supporting data file. Cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		if (lVaccineGroups.size() > 1) {
			String lErrStr = "More than once vaccine group specified for series " + lSeriesCode + " in supporting data file. Currently, only one vaccine group per series is supported";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		CD lVaccineGroupCD = ConceptUtils.toInternalCD(lVaccineGroups.iterator().next());
		LocallyCodedVaccineGroupItem lVGI = this.supportedVaccineGroups.getVaccineGroupItem(lVaccineGroupCD);
		if (lVGI == null) {
			String lErrStr = "Vaccine group specified for series supporting data file not a previously specified Vaccine Group item: " + lVaccineGroupCD;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}

		///////
		// At least one dose must be specified and the number of doses must match the IceSeriesDoseSpecification elements
		///////
		Collection<IceSeriesDoseSpecification> isdss = pIceSeriesSpecificationFile.getIceSeriesDoses();
		if (CollectionUtils.isNullOrEmpty(isdss)) {
			String lErrStr = "No series doses have been specified for the series. Series: " + lSeriesCode;
			logger.error(lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		// ... and the number of doses must be specified and match what is provided
		BigInteger bi = pIceSeriesSpecificationFile.getNumberOfDosesInSeries();
		if (bi == null) {
			String lErrStr = "Number of doses in series not specified. Series: " + lSeriesCode;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		if (bi.intValue() != isdss.size()) {
			String lErrStr = "Number of doses specified for the Series does not match the number of IceSeriesDoseSpecification elements; cannot continue. Series " + lSeriesCode;
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		////////////// Gather the DoseRules START //////////////
		
		///////
		// Add each dose. Check that each TimePeriod specified is a valid time period, and every vaccine specified is a vaccine that has been previously defined.
		///////
		List<DoseRule> seriesDoseRules = new ArrayList<DoseRule>();
		int icseSeriesDoseSpecificationNumber=1;
		for (IceSeriesDoseSpecification isds : isdss) {
			///////
			// Basic validation checks
			///////
			if (isds == null) {
				String lErrStr = "Encountered a null IceSeriesDoseSpecification; this should not happen. Cannot continue.";
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			if (isds.getDoseNumber() == null) {
				String lErrStr = "Dose number not specified in IceSeriesDoseSpecification file";
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			int lDoseNumber = isds.getDoseNumber().intValue();
			if (lDoseNumber != icseSeriesDoseSpecificationNumber) {
				String lErrStr = "Dose number is not a valid dose number for series (1 <= [dose number]<= [number of doses in series]) or is not in *sequential order*. Cannot continue.";
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			
			Collection<IceDoseVaccineSpecification> lIDVSS = isds.getDoseVaccines();
			if (CollectionUtils.isNullOrEmpty(lIDVSS)) {
				String lWarnStr = "No valid vaccines were specified for Series " + lSeriesCode + ", dose number " + icseSeriesDoseSpecificationNumber;
				logger.warn(_METHODNAME + lWarnStr);
				throw new InconsistentConfigurationException(lWarnStr);
			}
			///////
			// Obtain the permitted and allowable vaccines for inclusion in the series
			///////
			List<Vaccine> lPreferredDoseVaccines = new ArrayList<Vaccine>();
			List<Vaccine> lAllowableDoseVaccines = new ArrayList<Vaccine>();
			if (lIDVSS != null) {
				for (IceDoseVaccineSpecification lIDVS : lIDVSS) {
					if (lIDVS == null) {
						String lErrStr = "Encountered a null IceDoseVaccineSpecification in Series " + lSeriesCode + "; this should not happen. Not continuing.";
						logger.error(_METHODNAME + lErrStr);
						throw new InconsistentConfigurationException(lErrStr);
					}
					org.opencds.vmr.v1_0.schema.CD lVaccineCD = lIDVS.getVaccine();
					LocallyCodedVaccineItem lcvi = this.supportedVaccines.getVaccineItem(ConceptUtils.toInternalCD(lVaccineCD));
					if (lcvi == null) {
						String lErrStr = "A vaccine was specified for Series " + lSeriesCode + ", dose number " + icseSeriesDoseSpecificationNumber + " which is not a previously defined vaccine.";
						logger.error(_METHODNAME + lErrStr);
						throw new InconsistentConfigurationException(lErrStr);
					}
					if (lIDVS.isPreferred()) {
						lPreferredDoseVaccines.add(lcvi.getVaccine());
					}
					else {
						lAllowableDoseVaccines.add(lcvi.getVaccine());
					}
				}
			}
			///////
			// Get absolute minimum age, minimum age, maximum age, earliest recommended age, latest recommended age, absolute minimum interval, minimum interval, 
			// earliest recommended interval, latest recommended interval...
			///////
			String absoluteMinimumAge = isds.getAbsoluteMinimumAge();
			String minimumAge = isds.getMinimumAge();
			String earliestRecommendedAge = isds.getEarliestRecommendedAge();
			// absolute minimum age, minimum age and earliest recommended age are mandatory 
			if (absoluteMinimumAge == null || minimumAge == null || earliestRecommendedAge == null) {
				String lErrStr = "Absolute minimum age, minimum age and/or earliest recommended age not specified in Series " + lSeriesCode;
				logger.error(_METHODNAME + lErrStr);
				throw new InconsistentConfigurationException(lErrStr);
			}
			String maximumAge = isds.getMaximumAge();			
			String latestRecommendedAge = isds.getLatestRecommendedAge();
			String absoluteMinimumInterval = null;
			String minimumInterval = null;
			String earliestRecommendedInterval = null;
			String latestRecommendedInterval = null;
			List<IceDoseIntervalSpecification> idiss = pIceSeriesSpecificationFile.getDoseIntervals();
			if (idiss != null) {
				///////
				// Cycle through the intervals (IceDoseIntervalSpecifications) from this dose to the next (doseNumber+1) dose
				boolean thisDoseToNextDoseIntervalFound = false;
				for (IceDoseIntervalSpecification idis : idiss) {
					if (idis == null) {
						String lErrStr = "Encountered a null IceDoseIntervalSpecification in Series " + lSeriesCode + "; this should not happen. Not continuing.";
						logger.error(_METHODNAME + lErrStr);
						throw new InconsistentConfigurationException(lErrStr);
					}
					BigInteger fromDoseNumberBI = idis.getFromDoseNumber();
					BigInteger toDoseNumberBI = idis.getToDoseNumber();
					if (fromDoseNumberBI == null || toDoseNumberBI == null) {
						String lErrStr = "IceDoseIntervalSpecification fromDoseNumber or toDoseNumber elements not provided in Series " + lSeriesCode + "; cannot continue";
						logger.error(_METHODNAME + lErrStr);
						throw new InconsistentConfigurationException(lErrStr);
					}
					int fromDoseNumber = fromDoseNumberBI.intValue();
					int toDoseNumber = toDoseNumberBI.intValue();
					if (fromDoseNumber == icseSeriesDoseSpecificationNumber && toDoseNumber == fromDoseNumber+1) {
						if (thisDoseToNextDoseIntervalFound == true) {
							String lErrStr = "Encountered more than one interval from dose number " + fromDoseNumber + " to dose number" + fromDoseNumber+1 + " for Series " +lSeriesCode;
							logger.error(_METHODNAME + lErrStr);
							throw new InconsistentConfigurationException(lErrStr);
						}
						else {
							thisDoseToNextDoseIntervalFound = true;
							// Interval from this dose to the next (doseNumber+1) dose
							absoluteMinimumInterval = idis.getAbsoluteMinimumInterval();
							minimumInterval = idis.getMinimumInterval();
							earliestRecommendedInterval = idis.getEarliestRecommendedInterval();
							latestRecommendedInterval = idis.getLatestRecommendedInterval();
						}
					}
					else if (fromDoseNumber == icseSeriesDoseSpecificationNumber && toDoseNumber != fromDoseNumber+1) {
						String lWarnStr = "Warning: interval from dose number " + fromDoseNumber + " to non-consecutive dose number " + fromDoseNumber+1 + "found for Series " + 
							lSeriesCode + "; only consecutive intervals currently supported";
						logger.warn(_METHODNAME + lWarnStr);
					}
				}
			}

			///////
			// Create the DoseRule and add it to the list of Doses for this Series
			///////
			DoseRule dr = new DoseRule();
			// Mandatory
			dr.setDoseNumber(icseSeriesDoseSpecificationNumber);
			// Mandatory
			dr.setAbsoluteMinimumAge(new TimePeriod(absoluteMinimumAge));
			// Mandatory
			dr.setMinimumAge(new TimePeriod(minimumAge));
			// Mandatory
			dr.setEarliestRecommendedAge(new TimePeriod(earliestRecommendedAge));
			dr.setPreferableVaccines(lPreferredDoseVaccines);
			dr.setAllowableVaccines(lAllowableDoseVaccines);
			if (maximumAge != null) {
				// Only if specified
				dr.setMaximumAge(new TimePeriod(maximumAge));
			}
			if (latestRecommendedAge != null) {
				// Only if specified
				dr.setMaximumAge(new TimePeriod(latestRecommendedAge));
			}
			if (absoluteMinimumInterval != null) {
				// Only if specified
				dr.setAbsoluteMinimumInterval(new TimePeriod(absoluteMinimumInterval));
			}
			if (minimumInterval != null) {
				// Only if specified
				dr.setMinimumInterval(new TimePeriod(minimumInterval));
			}
			if (earliestRecommendedInterval != null) {
				// Only if specified
				dr.setEarliestRecommendedInterval(new TimePeriod(earliestRecommendedInterval));
			}
			if (latestRecommendedInterval != null) {
				// Only if specified
				dr.setLatestRecommendedInterval(new TimePeriod(latestRecommendedAge));
			}
			
			// Add the DoseRule to the list of DoseRules for this series
			seriesDoseRules.add(dr);
			icseSeriesDoseSpecificationNumber++;
		}
		
		////////////// Gather the DoseRules END //////////////
		
		///////
		// Create SeriesRules
		///////
		SeriesRules series1Rules = new SeriesRules(lSeriesCode, lVGI.getCdsItemName());
		series1Rules.setSeriesDoseRules(seriesDoseRules);
		series1Rules.setRecurringDosesAfterSeriesComplete(pIceSeriesSpecificationFile.isRecurringDosesAfterSeriesComplete());
		series1Rules.setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(pIceSeriesSpecificationFile.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered());
		
		///////
		// Store the Series in this supporting data 
		///////
		// If any Seasons are specified, verify that they are seasons that have been previously specified
		Collection<String> lSeasonCodesFromIceSeriesSpecificationFile = pIceSeriesSpecificationFile.getSeasonCodes();
		List<Season> lSeasons = new ArrayList<Season>();
		if (lSeasonCodesFromIceSeriesSpecificationFile != null) {
			for (String s : lSeasonCodesFromIceSeriesSpecificationFile) {
				LocallyCodedSeasonItem lcsi = this.supportedSeasons.getSeasonItem(s);
				if (lcsi == null || lcsi.getSeason() == null) {
					String lErrStr = "Season \"" + s + "\" specified for the Series " + lSeriesCode + " does not exist";
					logger.error(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
				else {
					lSeasons.add(lcsi.getSeason());
				}
			}
		}
		
		///////
		// Create the SeriesItem and store it
		///////
		LocallyCodedSeriesItem lcsi = null;
		try {
			new LocallyCodedSeriesItem(lSeriesCode, lCdsVersions, series1Rules);
		}
		catch (ImproperUsageException iue) {
			String lErrStr = "Caught an unexpected ImproperUsageException during instantiation of LocallyCodedSeriesItem for series" + lSeriesCode;
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		
		// Add the mapping from the String to reference the Series to LocallyCodedSeriesItem
		this.cdsListItemNameToSeriesItem.put(lSeriesCode, lcsi);
		
		/////// 
		// Add the Series to the list of Series being tracked for each vaccine group START 
		///////
		List<SeriesRules> lSeriesRulesListForVG = this.vaccineGroupItemToSeriesRules.get(lcsi);
		if (lSeriesRulesListForVG == null) {
			lSeriesRulesListForVG = new ArrayList<SeriesRules>();
		}
		this.vaccineGroupItemToSeriesRules.put(lVGI, lSeriesRulesListForVG);
		
		/**
		 * Example Seasons declarations:
		 * 
		   Fully-Specified Seasons:
		
			List<Season> influenzaSeasons = new ArrayList<Season>();
			influenzaSeasons.add(new Season("2015-2016 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2015, 6, 30, 2016));
			influenzaSeasons.add(new Season("2014-2015 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2014, 6, 30, 2015));
			influenzaSeasons.add(new Season("2013-2014 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2013, 6, 30, 2014));
			influenzaSeasons.add(new Season("2012-2013 Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 2012, 6, 30, 2013));

		   Default Season: 

			Season influenzaSeasonDefault = new Season("Default Influenza Season", SupportedVaccineGroupConcept.Influenza, true, 8, 1, 6, 30);
			List<Season> influenzaDefaultSeasons = new ArrayList<Season>();
			influenzaDefaultSeasons.add(influenzaSeasonDefault);	

		 *
		 * Example population of SeriesRules and DoseRule classes
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
