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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.ice.util.StringUtils;
import org.cdsframework.util.support.data.ice.season.IceSeasonSpecificationFile;
import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

public class SupportedSeasons implements SupportingData {
	
	private Map<String, LocallyCodedSeasonItem> cdsListItemNameToSeasonItem;					// cdsListItemName (cdsListCode.cdsListItemKey) to LocallyCodedSeasonItem
	private Map<LocallyCodedVaccineGroupItem, List<Season>> vaccineGroupItemToSeasons;			// Internal tracking structure: List of Seasons supported for each vaccine group	
	private SupportedVaccineGroups supportedVaccineGroups;										// Supporting vaccine groups from which this season data is built
	private boolean isSupportingDataConsistent;
	
	private static final Logger logger = LogManager.getLogger();

	
	protected SupportedSeasons(ICESupportingDataConfiguration isdc) 
		throws ImproperUsageException, IllegalArgumentException {

		String _METHODNAME = "SupportedCdsSeasons(): ";
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
		this.cdsListItemNameToSeasonItem = new HashMap<String, LocallyCodedSeasonItem>();
		this.vaccineGroupItemToSeasons = new HashMap<LocallyCodedVaccineGroupItem, List<Season>>();
		this.isSupportingDataConsistent = true;
	}

	
	public boolean isEmpty() {		
		if (this.cdsListItemNameToSeasonItem.size() == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public boolean isSupportingDataConsistent() {
		return this.isSupportingDataConsistent;
	}
	
	
	public boolean seasonItemExists(String pSeasonItemName) {
		
		if (pSeasonItemName == null) {
			return false;
		}
		if (this.cdsListItemNameToSeasonItem.get(pSeasonItemName) != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public LocallyCodedSeasonItem getSeasonItem(String pSeasonItemName) {
		
		if (pSeasonItemName == null) {
			return null;
		}
		LocallyCodedSeasonItem lLCSI = this.cdsListItemNameToSeasonItem.get(pSeasonItemName);
		if (lLCSI == null) {
			return null;
		}
		else {
			return lLCSI;
		}
	}

	
	protected SupportedVaccineGroups getAssociatedSupportedCdsVaccineGroups() {
		
		return this.supportedVaccineGroups;
	}
	
	
	protected void addSupportedSeasonItemFromIceSeasonSpecificationFile(IceSeasonSpecificationFile pIceSeasonSpecificationFile) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportedSeasonItemFromIceSeasonSpecificationFile(): ";
		
		if (pIceSeasonSpecificationFile == null || this.supportedVaccineGroups == null) {
			return;
		}

		String lSeasonCode = pIceSeasonSpecificationFile.getCode();
		if (StringUtils.isNullOrEmpty(lSeasonCode)) {
			String lErrStr = "Required supporting data seasonCode element not provided in IceSeasonSpecificationFile";
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new ImproperUsageException(lErrStr);
		}
		else {
			lSeasonCode = ConceptUtils.modifyAttributeNameToConformToRequiredNamingConvention(lSeasonCode);
		}
		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lIntersectionOfSupportedCdsVersions = CollectionUtils.intersectionOfStringCollections(pIceSeasonSpecificationFile.getCdsVersions(), 
			this.supportedVaccineGroups.getAssociatedSupportedCdsLists().getCdsVersions());
		if (lIntersectionOfSupportedCdsVersions == null || lIntersectionOfSupportedCdsVersions.size() == 0) {
			logger.warn(_METHODNAME + "Skipping attempt to add a Season \"" + lSeasonCode + "\" which does not have a cdsVersion that is supported by SupportedCdsLists");
			return;
		}
				
		///////
		// Check to make sure that this season code has not already been defined
		///////
		if (this.cdsListItemNameToSeasonItem.containsKey(lSeasonCode)) {
			String lErrStr = "Attempt to add a Season that was already specified previously: " + lSeasonCode;
			logger.warn(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);
		}
		
		///////
		// Check to make sure that the vaccine group specified is a valid vaccine group; one that has been previously specified
		CD lVaccineGroupCD = ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup());
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD)) {
			String lErrStr = "Required supporting data item vaccineGroup element not provided in IceVaccineGroupSpecificationFile:";
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new ImproperUsageException(lErrStr);
		}
		
		// Check to make sure that the specified vaccine group is a supported vaccine group
		LocallyCodedCdsListItem lccli = this.supportedVaccineGroups.getAssociatedSupportedCdsLists().getCdsListItem(lVaccineGroupCD);
		if (lccli == null) {
			String lErrStr = "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsLists: " + 
					(pIceSeasonSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);			
		}		
		LocallyCodedVaccineGroupItem lcvgi = this.supportedVaccineGroups.getVaccineGroupItem(lccli.getCdsListItemName());
		if (lcvgi == null) {
			String lErrStr = "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsVaccineGroups: " + 
					(pIceSeasonSpecificationFile.getVaccineGroup() == null ? "null" : ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup()));
			logger.warn(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);			
		}
		
		////////////// Create new season and add it to the list of seasons being tracked for each vaccine group START //////////////
		List<Season> lSeasonsListForVG = this.vaccineGroupItemToSeasons.get(lcvgi);
		if (lSeasonsListForVG == null) {
			lSeasonsListForVG = new ArrayList<Season>();
		}
		if (pIceSeasonSpecificationFile.isDefaultSeason() == false) {
			///////
			// Add fully-specified season
			///////
			if (pIceSeasonSpecificationFile.getStartDate() == null || pIceSeasonSpecificationFile.getEndDate() == null) {
				String lErrStr = "Fully-specified season start and/or fully-specified season end date not specified for non-default season; start date: " + 
						pIceSeasonSpecificationFile.getStartDate() + "; end date: " + pIceSeasonSpecificationFile.getEndDate();
				logger.warn(_METHODNAME + lErrStr);
				this.isSupportingDataConsistent = false;
				throw new InconsistentConfigurationException(lErrStr);
			}

			LocalDate lJodaFullySpecifiedSeasonStartDate = LocalDate.fromDateFields(pIceSeasonSpecificationFile.getStartDate());
			LocalDate lJodaFullySpecifiedSeasonEndDate = LocalDate.fromDateFields(pIceSeasonSpecificationFile.getEndDate());
			Season lS = new Season(lSeasonCode, lcvgi.getCdsItemName(), true, 
					lJodaFullySpecifiedSeasonStartDate.getMonthOfYear(), lJodaFullySpecifiedSeasonStartDate.getDayOfMonth(), lJodaFullySpecifiedSeasonStartDate.getYear(), 
					lJodaFullySpecifiedSeasonEndDate.getMonthOfYear(), lJodaFullySpecifiedSeasonEndDate.getDayOfMonth(), lJodaFullySpecifiedSeasonEndDate.getYear());
			lSeasonsListForVG.add(lS);
			this.cdsListItemNameToSeasonItem.put(lSeasonCode, new LocallyCodedSeasonItem(lSeasonCode, pIceSeasonSpecificationFile.getCdsVersions(), lS));
			this.vaccineGroupItemToSeasons.put(lcvgi, lSeasonsListForVG);
		}
		else {
			///////
			// Add default season
			///////
			String lDefaultSeasonStartDate = pIceSeasonSpecificationFile.getDefaultStartMonthAndDay();
			String lDefaultSeasonEndDate = pIceSeasonSpecificationFile.getDefaultStopMonthAndDay();
			if (lDefaultSeasonStartDate == null || lDefaultSeasonEndDate == null) {
				String lErrStr = "Default season start and/or default season end date not specified for default season; start date: " + 
						lDefaultSeasonStartDate + "; end date: " + lDefaultSeasonEndDate;
				logger.warn(_METHODNAME + lErrStr);
				this.isSupportingDataConsistent = false;
				throw new InconsistentConfigurationException(lErrStr);
			}

			// Check validity of specified default start and stop dates
			MonthDay lStartMonthDay = null;
			MonthDay lEndMonthDay = null;
			try {
				lStartMonthDay = getMonthDayObjectForSDMonthDayStr(lDefaultSeasonStartDate);
				lEndMonthDay = getMonthDayObjectForSDMonthDayStr(lDefaultSeasonEndDate);
			}
			catch (IllegalArgumentException e) {
				String lErrStr = "Default season start and/or default season end date invalid format; start date: " + lDefaultSeasonStartDate + "; end date: " + lDefaultSeasonEndDate;
				logger.warn(_METHODNAME + lErrStr);
				this.isSupportingDataConsistent = false;
				throw new InconsistentConfigurationException(lErrStr);
			}
			Season lS = new Season(lSeasonCode, lcvgi.getCdsItemName(), true, lStartMonthDay.getMonthOfYear(), lStartMonthDay.getDayOfMonth(), 
				lEndMonthDay.getMonthOfYear(), lEndMonthDay.getDayOfMonth());
			lSeasonsListForVG.add(lS);
			this.cdsListItemNameToSeasonItem.put(lSeasonCode, new LocallyCodedSeasonItem(lSeasonCode, pIceSeasonSpecificationFile.getCdsVersions(), lS));
			this.vaccineGroupItemToSeasons.put(lcvgi, lSeasonsListForVG);
		}
		
		/**
		 * Examples
		 * 
		 	// Fully-Specified Seasons
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

	
	/**
	 * Get MonthDay object for month and day string represented by MM-DD 
	 * @param pMonthAndDay
	 * @return
	 * @throws IllegalArgumentException
	 */
	private MonthDay getMonthDayObjectForSDMonthDayStr(String pMonthAndDay) 
		throws IllegalArgumentException {

		String _METHODNAME = "getMonthDayObjectForSDMonthDayStr(): ";

		if (pMonthAndDay == null || pMonthAndDay.isEmpty()) {
			String lErrStr = _METHODNAME + "MonthDay argument not specified";
			throw new IllegalArgumentException(lErrStr);
		}
		
		final String regex="(\\d+)(-)(\\d+)";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(pMonthAndDay);
		if (m.find()) {
			try {
				String monthStr=m.group(1);
				String dayStr=m.group(3);
				MonthDay md = new MonthDay(Integer.parseInt(monthStr), Integer.parseInt(dayStr));
				return md;
			}
			catch (IllegalStateException ise) {
				 String lErrStr = "An IllegalStateException was encountered for month and day: " + pMonthAndDay;
				 logger.error(_METHODNAME + lErrStr);
				 throw new ICECoreError(lErrStr);
			}
			catch (IndexOutOfBoundsException iobe) {
				 String lErrStr = "An IllegalStateException was encountered for month and day: " + pMonthAndDay;
				 logger.error(_METHODNAME + lErrStr);
				 throw new ICECoreError(_METHODNAME + lErrStr);
			}
		}
		else {
			final String regexParm="(\\d)?(\\d)(-)(\\d)?(\\d)";
			String lErrStr = _METHODNAME + "MonthDay argument is in invalid format. Format is: " + regexParm;
			throw new IllegalArgumentException(lErrStr);
		}
	}
	
	
	@Override
	public String toString() {
		
		// First, print out all of the season name->season value map entries
		Set<String> cdsListItemNames = this.cdsListItemNameToSeasonItem.keySet();
		int i = 1 ;
		String ltoStringStr = "";
		for (String s : cdsListItemNames) {
			ltoStringStr += "{" + i + "} " + s + " = [ " + this.cdsListItemNameToSeasonItem.get(s).toString() + " ]\n";
			i++;
		}
		
		// Second, print out which seasons are associated with which vaccine groups
		Set<LocallyCodedVaccineGroupItem> lcvc = this.vaccineGroupItemToSeasons.keySet();
		i=1;
		ltoStringStr += "Vaccine Group -> Seasons list:";
		for (LocallyCodedVaccineGroupItem lcvg : lcvc) {
			ltoStringStr += "\n\t(" + i + ") Vaccine Group " + lcvg.getCdsItemName() + " ==> ";
			List<Season> vgSeasons = this.vaccineGroupItemToSeasons.get(lcvg);
			for (Season s: vgSeasons) {
				ltoStringStr += s.getSeasonName() + "; ";
			}
			i++;
		}
		
		return ltoStringStr;
	}
	
}
