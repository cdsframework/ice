/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */
 
package org.cdsframework.ice.supportingdata;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.DoseRule;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.service.SeriesRules;
import org.cdsframework.ice.service.Vaccine;
import org.cdsframework.ice.util.CollectionUtils;
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

	private boolean isSupportingDataConsistent;
	private Map<String, LocallyCodedSeriesItem> cdsListItemNameToSeriesItem;					// cdsListItemName (cdsListCode.cdsListItemKey) to LocallyCodedSeriesItem
	private Map<LocallyCodedVaccineGroupItem, List<SeriesRules>> vaccineGroupItemToSeriesRules;	// vaccine group -> List of associated series
	
	private static final Logger logger = LogManager.getLogger();

	
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
		this.isSupportingDataConsistent = true;
	}

	
	public boolean isEmpty() {		
		if (this.cdsListItemNameToSeriesItem.size() == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public boolean isSupportingDataConsistent() {
		
		if (this.isSupportingDataConsistent == false) {
			return false;
		}
		else {
			return checkConsistencyOfSeasonsSupportingDataAcrossAllSeriesInAllVaccineGroups();
		}
	}
	
	
	/**
	 * Return a *copy* of the list of SeriesRules associated with the specified vaccine group. If the vaccine group is not supported, null is returned. 
	 * If the vaccine group is supported but not SeriesRules have been specified for the vaccine group, an empty list is returned.
	 */
	public List<SeriesRules> getCopyOfSeriesRulesForVaccineGroup(LocallyCodedVaccineGroupItem plcvg) {
		
		return getSeriesRulesForVaccineGroup(plcvg, false);
	}
		
	/**
	 * Return a reference to the list of SeriesRules associated with the specified vaccine group. If the vaccine group is not supported, null is returned. 
	 * If the vaccine group is supported but not SeriesRules have been specified for the vaccine group, an empty list is returned.
	 */
	protected List<SeriesRules> getSeriesRulesForVaccineGroup(LocallyCodedVaccineGroupItem plcvg) {
		
		return getSeriesRulesForVaccineGroup(plcvg, false);
	}
	
	private List<SeriesRules> getSeriesRulesForVaccineGroup(LocallyCodedVaccineGroupItem plcvg, boolean copyOf) {

		List<SeriesRules> lSRs = this.vaccineGroupItemToSeriesRules.get(plcvg);
		if (lSRs == null) {
			return null;
		}

		List<SeriesRules> lSRsResult = new ArrayList<SeriesRules>();
		for (SeriesRules lSR : lSRs) {
			SeriesRules lSRcopy = (copyOf == true) ? SeriesRules.constructDeepCopyOfSeriesRulesObject(lSR) : lSR;
			lSRsResult.add(lSRcopy);
		}

		return lSRsResult;		
	}
	
	
	/**
	 * Return a copy of all SeriesRules supported by this installation. If none, an empty list is returned.
	 */
	public List<SeriesRules> getCopyOfAllSeriesRules() {
		
		return getAllSeriesRules(true);
	}	
	
	/**
	 * Return a copy of all SeriesRules supported by this installation. If none, an empty list is returned.
	 */
	protected List<SeriesRules> getAllSeriesRules() {
		
		return getAllSeriesRules(false);
	}
	
	private List<SeriesRules> getAllSeriesRules(boolean copyOf) {
		
		Collection<List<SeriesRules>> lCollectionOfSRs = this.vaccineGroupItemToSeriesRules.values();
		if (lCollectionOfSRs == null) {
			return new ArrayList<SeriesRules>();
		}
		
		List<SeriesRules> lSRsCopy = new ArrayList<SeriesRules>();
		for (List<SeriesRules> lSRs : lCollectionOfSRs) {
			for (SeriesRules lSR : lSRs) {
				SeriesRules lSRcopy = (copyOf == true) ? SeriesRules.constructDeepCopyOfSeriesRulesObject(lSR) : lSR;
				lSRsCopy.add(lSRcopy);
			}
		}
		
		return lSRsCopy;
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
		
		///////
		// CDS Version validation checks
		///////
		// Validation Check: If no cdsVersions are specified, thrown an error
		if (CollectionUtils.isNullOrEmpty(pIceSeriesSpecificationFile.getCdsVersions())) {
			String lErrStr = "No cdsVersion(s) specified in series supporting data file.";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
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
			lSeriesCode = ConceptUtils.modifyAttributeNameToConformToRequiredNamingConvention(lSeriesCode);
		}
		if (this.cdsListItemNameToSeriesItem.containsKey(lSeriesCode)) {
			String lErrStr = "Attempt to add a Series that was already specified previously; series code:  " + lSeriesCode;
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
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
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		CD lVaccineGroupCD = ConceptUtils.toInternalCD(lVaccineGroups.iterator().next());
		LocallyCodedVaccineGroupItem lVGI = this.supportedVaccineGroups.getVaccineGroupItem(lVaccineGroupCD);
		if (lVGI == null) {
			String lErrStr = "Vaccine group specified for series supporting data file not a previously specified Vaccine Group item: " + lVaccineGroupCD;
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
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
			this.isSupportingDataConsistent = false;
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
				this.isSupportingDataConsistent = false;
				throw new InconsistentConfigurationException(lErrStr);
			}
			int lDoseNumber = isds.getDoseNumber().intValue();
			if (lDoseNumber != icseSeriesDoseSpecificationNumber) {
				String lErrStr = "Dose number is not a valid dose number for series (1 <= [dose number]<= [number of doses in series]) or is not in *sequential order*. Cannot continue.";
				logger.error(_METHODNAME + lErrStr);
				this.isSupportingDataConsistent = false;
				throw new InconsistentConfigurationException(lErrStr);
			}
			
			Collection<IceDoseVaccineSpecification> lIDVSS = isds.getDoseVaccines();
			if (CollectionUtils.isNullOrEmpty(lIDVSS)) {
				String lWarnStr = "No valid vaccines were specified for Series " + lSeriesCode + ", dose number " + icseSeriesDoseSpecificationNumber;
				logger.warn(_METHODNAME + lWarnStr);
				this.isSupportingDataConsistent = false;
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
						this.isSupportingDataConsistent = false;
						throw new InconsistentConfigurationException(lErrStr);
					}
					org.opencds.vmr.v1_0.schema.CD lVaccineCD = lIDVS.getVaccine();
					LocallyCodedVaccineItem lcvi = this.supportedVaccines.getVaccineItem(ConceptUtils.toInternalCD(lVaccineCD));
					if (lcvi == null) {
						String lErrStr = "A vaccine which was not previously defined was specified for Series " + lSeriesCode + "; dose number " + icseSeriesDoseSpecificationNumber + "; vaccine: " + ConceptUtils.toStringCD(lVaccineCD);
						logger.error(_METHODNAME + lErrStr);
						this.isSupportingDataConsistent = false;
						throw new InconsistentConfigurationException(lErrStr);
					}
					if (lPreferredDoseVaccines.contains(lcvi.getVaccine()) || lAllowableDoseVaccines.contains(lcvi.getVaccine())) {
						String lErrStr = "A vaccine was specified more than once for Series " + lSeriesCode + ", dose number " + icseSeriesDoseSpecificationNumber + "; vaccine in question: " + lcvi.getVaccine();
						logger.error(_METHODNAME + lErrStr);
						this.isSupportingDataConsistent = false;
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
			if (absoluteMinimumAge == null || minimumAge == null) {
				String lInfoStr = "Absolute minimum age and/or minimum age not specified in a dose: " + lDoseNumber + "; Series " + lSeriesCode;
				logger.warn(_METHODNAME + lInfoStr);
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
						this.isSupportingDataConsistent = false;
						throw new InconsistentConfigurationException(lErrStr);
					}
					BigInteger fromDoseNumberBI = idis.getFromDoseNumber();
					BigInteger toDoseNumberBI = idis.getToDoseNumber();
					if (fromDoseNumberBI == null || toDoseNumberBI == null) {
						String lErrStr = "IceDoseIntervalSpecification fromDoseNumber or toDoseNumber elements not provided in Series " + lSeriesCode + "; cannot continue";
						logger.error(_METHODNAME + lErrStr);
						this.isSupportingDataConsistent = false;
						throw new InconsistentConfigurationException(lErrStr);
					}
					int fromDoseNumber = fromDoseNumberBI.intValue();
					int toDoseNumber = toDoseNumberBI.intValue();
					if (fromDoseNumber == icseSeriesDoseSpecificationNumber && toDoseNumber == fromDoseNumber+1) {
						if (thisDoseToNextDoseIntervalFound == true) {
							String lErrStr = "Encountered more than one interval from dose number " + fromDoseNumber + " to dose number" + fromDoseNumber+1 + " for Series " +lSeriesCode;
							logger.error(_METHODNAME + lErrStr);
							this.isSupportingDataConsistent = false;
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
						String lWarnStr = "Warning: skipping interval from dose number " + fromDoseNumber + " to non-consecutive dose number " + fromDoseNumber+1 + "found for Series " + 
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
			dr.setPreferableVaccines(lPreferredDoseVaccines);
			dr.setAllowableVaccines(lAllowableDoseVaccines);
			if (absoluteMinimumAge != null) {
			dr.setAbsoluteMinimumAge(new TimePeriod(absoluteMinimumAge));
			}
			if (minimumAge != null) {
				dr.setMinimumAge(new TimePeriod(minimumAge));
			}
			if (earliestRecommendedAge != null) {
				dr.setEarliestRecommendedAge(new TimePeriod(earliestRecommendedAge));
			}
			if (maximumAge != null) {
				// Only if specified
				dr.setMaximumAge(new TimePeriod(maximumAge));
			}
			if (latestRecommendedAge != null) {
				// Only if specified
				dr.setLatestRecommendedAge(new TimePeriod(latestRecommendedAge));
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
				dr.setLatestRecommendedInterval(new TimePeriod(latestRecommendedInterval));
			}
			
			// Add the DoseRule to the list of DoseRules for this series
			seriesDoseRules.add(dr);
			icseSeriesDoseSpecificationNumber++;
		}
		
		////////////// Gather the DoseRules END //////////////

		///////
		// Store the Series in this supporting data 
		///////
		// Plus, if any Seasons are specified, verify that they are seasons that have been previously specified
		Collection<String> lSeasonCodesFromIceSeriesSpecificationFile = pIceSeriesSpecificationFile.getSeasonCodes();
		List<Season> lSeasons = new ArrayList<Season>();
		if (lSeasonCodesFromIceSeriesSpecificationFile != null) {
			for (String s : lSeasonCodesFromIceSeriesSpecificationFile) {
				LocallyCodedSeasonItem lcsi = this.supportedSeasons.getSeasonItem(s);
				if (lcsi == null || lcsi.getSeason() == null) {
					String lErrStr = "Season \"" + s + "\" specified for the Series " + lSeriesCode + " does not exist";
					logger.error(_METHODNAME + lErrStr);
					this.isSupportingDataConsistent = false;
					throw new InconsistentConfigurationException(lErrStr);
				}
				else {
					lSeasons.add(lcsi.getSeason());
				}
			}
		}

		///////
		// Now create SeriesRules
		///////
		/////// SeriesRules series1Rules = (lSeasons.isEmpty()) ? new SeriesRules(lSeriesCode, lVGI.getCdsItemName()) : new SeriesRules(lSeriesCode, lVGI.getCdsItemName(), lSeasons);
		SeriesRules series1Rules = (lSeasons.isEmpty()) ? new SeriesRules(lSeriesCode, lVGI.getCdsConcept()) : new SeriesRules(lSeriesCode, lVGI.getCdsConcept(), lSeasons);
		series1Rules.setSeriesDoseRules(seriesDoseRules);
		// Determine whether or not there are recurring doses for this series (**default false if not specified**)
		if (pIceSeriesSpecificationFile.isRecurringDosesAfterSeriesComplete() != null) {
			series1Rules.setRecurringDosesAfterSeriesComplete(pIceSeriesSpecificationFile.isRecurringDosesAfterSeriesComplete());
		}
		else {
			// If not specified, assume there are no recurring doses of some kind after the series has been completed
			series1Rules.setRecurringDosesAfterSeriesComplete(false);
		}
		// Determine if the dose number should be calculated based on the targeted diseases of each vaccine administered (**default true if not specified**)
		if (pIceSeriesSpecificationFile.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered() != null) {
			series1Rules.setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(pIceSeriesSpecificationFile.isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered());
		}
		else {
			series1Rules.setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(true);
		}
				
		///////
		// Create the SeriesItem and store it
		///////
		LocallyCodedSeriesItem lcsi = null;
		try {
			lcsi = new LocallyCodedSeriesItem(lSeriesCode, lCdsVersions, series1Rules);
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
		List<SeriesRules> lSeriesRulesListForVG = this.vaccineGroupItemToSeriesRules.get(lVGI);
		if (lSeriesRulesListForVG == null) {
			lSeriesRulesListForVG = new ArrayList<SeriesRules>();
		}
		lSeriesRulesListForVG.add(series1Rules);
		this.vaccineGroupItemToSeriesRules.put(lVGI, lSeriesRulesListForVG);
	}


	/**
	 * Check to make sure that all of the seasons in each vaccine group are "consistent". Invokes checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup()
	 * for each season. Results must be true for all invocations to to that method, or this method returns false.
	 */
	private boolean checkConsistencyOfSeasonsSupportingDataAcrossAllSeriesInAllVaccineGroups() {
		
		Set<LocallyCodedVaccineGroupItem> lVaccineGroupsToCheck = this.vaccineGroupItemToSeriesRules.keySet();
		for (LocallyCodedVaccineGroupItem lcvgi : lVaccineGroupsToCheck) {
			boolean consistentInVG = checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(lcvgi);
			if (! consistentInVG) { 
				return false;
			}
		}
		
		return true;
	}
	

	/** 
	 * Check to make sure that all seasons do not overlap with each other, or if they do, they have the exact same season start and end dates. 
	 * All series in the vaccine group must be seasonal series, or none of them. If some are or others aren't, this method logs a warning and returns false.
	 * In addition, there cannot be more than one default series in a vaccine group.
	 * @param svgc vaccine group in which to check series consistency.
	 * @return true of these conditions are met, false if not.
	 */
	private boolean checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(LocallyCodedVaccineGroupItem pcvgi) {
		
		String _METHODNAME = "checkConsistencyOfSeasonsSupportingDataAcrossSeriesInVaccineGroup(): ";
		if (pcvgi == null) {
			return false;
		}
		
		List<SeriesRules> srs = getSeriesRulesForVaccineGroup(pcvgi);
		if (srs == null) {
			// Vaccine group is not supported - although this should not happen - just return true
			return true;
		}

		int countOfDefaultSeasonsAcrossSeries = 0;
		int countOfSeasons = 0;
		boolean aNonSeasonalSeriesExists = false;
		List<Season> seasonsTracker = new ArrayList<Season>();
		for (SeriesRules sr : srs) {
			if (countOfDefaultSeasonsAcrossSeries > 1) {
				logger.warn(_METHODNAME + "more than one default season across in vaccine group " + pcvgi.getCdsItemName());
				return false;
			}
			List<Season> seriesSeasons = sr.getSeasons();
			if (seriesSeasons == null || seriesSeasons.isEmpty()) {
				aNonSeasonalSeriesExists = true;
				if (countOfSeasons > 0) {
					logger.warn(_METHODNAME + "a non-seasonal series was found in a vaccine group with seasons " + pcvgi.getCdsItemName());
					return false;
				}
			}
			for (Season s : seriesSeasons) {
				if (aNonSeasonalSeriesExists) {
					logger.warn(_METHODNAME + "a non-seasonal series was found in a vaccine group with seasons " + pcvgi.getCdsItemName());
					return false;
				}
				boolean lSeasonAlreadyEncountered = false;
				if (seasonsTracker.contains(s)) {
					lSeasonAlreadyEncountered = true;
				}
				else {
					countOfSeasons++;
				}
				if (s.isDefaultSeason() == true) {
					countOfDefaultSeasonsAcrossSeries++;
					if (countOfDefaultSeasonsAcrossSeries >= 2) {
						logger.warn(_METHODNAME + "more than one default season in Series in vaccine group " + pcvgi.getCdsItemName());
						return false;
					}
				}
				else if (lSeasonAlreadyEncountered == false) {
					for (Season seasonIter : seasonsTracker) {
						// Check to see if the season start or end dates overlaps with another season. Overlaps are only allowed if the start and end dates 
						// of the season for the different series are exactly the same. Default seasons do not have a specified start or end date, so they are 
						// not checked here. (This is because if a fully-specified season can take place at a time when a default season is specified; it  
						// overrides the default season which will then not be used.)
						if (! s.seasonsHaveEquivalentStartAndEndDates(seasonIter) && s.seasonOverlapsWith(seasonIter)) {
							logger.warn(_METHODNAME + "overlapping seasons exist in vaccine group " + pcvgi.getCdsItemName());
							return false;
						}
					}
					seasonsTracker.add(s);
				}
			}
		}
		
		int lNumberOfDistinctSeasons = seasonsTracker.size();
		if (lNumberOfDistinctSeasons > 0) {
			if (countOfDefaultSeasonsAcrossSeries != 1 && countOfDefaultSeasonsAcrossSeries != 0 ) {
				logger.warn(_METHODNAME + "a seasonal vaccine group must have exactly either 0 or 1 default seasons defined. The # of seasonal series " + 
					"found for " + "vaccine group " + pcvgi.getCdsItemName() + ": " + countOfDefaultSeasonsAcrossSeries);
				return false;
			}
			else if (lNumberOfDistinctSeasons > 1 && countOfDefaultSeasonsAcrossSeries == 0) {
				logger.warn(_METHODNAME + "a seasonal vaccine group wiht more than one season defined must also have a default season defined. No default season has been defined");
				return false;
			}
			else {
				// This is a properly configured seasonal vaccine group with a default season for evaluation
				return true;
			}
		}
		else if (countOfDefaultSeasonsAcrossSeries == 0 && seasonsTracker.size() == 0) {
			// This is not a seasonal vaccine group
			return true;
		}
		else {
			return false;
		}
	}
	
	
	@Override
	public String toString() {
		
		String ltoStringStr = "";
		
		// First, print out all of the series name -> series value map entries
		ltoStringStr += "\ncdsListItemNameToSeriesItem: ";
		Set<String> cdsListItemNames = this.cdsListItemNameToSeriesItem.keySet();
		int i=1;
		for (String s : cdsListItemNames) {
			ltoStringStr += "\n{" + i + "} " + s + " = [ " + this.cdsListItemNameToSeriesItem.get(s).toString() + " ]\n";
			i++;
		}

		// Second, print out which series are associated with which vaccine groups
		Set<LocallyCodedVaccineGroupItem> llcvgs = this.vaccineGroupItemToSeriesRules.keySet();
		i=1;
		for (LocallyCodedVaccineGroupItem llcvg : llcvgs) {
			List<SeriesRules> seriesRulesAssociatedWithVG = this.vaccineGroupItemToSeriesRules.get(llcvg);
			if (seriesRulesAssociatedWithVG != null) {
				ltoStringStr += "\n{" + i + "} Series Rules for Vaccine Group: " + llcvg.getCdsItemName();
				int j=1;
				for (SeriesRules sr : seriesRulesAssociatedWithVG) {
					ltoStringStr += "\n\t(" + j + "): LocallyCodedVaccineGroupItem " + llcvg + "; SeriesRule " + sr.toString();
					j++;
				}
			}
			else {
				ltoStringStr += "\n\tNo SeriesRules defined"; 
			}
			i++;
		}

		// Finally, print out whether or not the series data read in is consistent
		ltoStringStr += "\n\nisSupportingDataConsistent(): " + isSupportingDataConsistent();

		return ltoStringStr;
	}
	
}
