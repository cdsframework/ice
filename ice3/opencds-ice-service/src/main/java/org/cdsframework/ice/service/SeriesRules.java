/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

package org.cdsframework.ice.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.CdsConcept;

public class SeriesRules {

	private String seriesId;
	private String seriesName;
	private CdsConcept vaccineGroupConcept;
	private int numberOfDosesInSeries;
	private boolean recurringDosesAfterSeriesComplete;
	private boolean doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered;
	private List<DoseRule> seriesDoseRules;
	private List<Season> applicableSeasons;
	
	private static Log logger = LogFactory.getLog(SeriesRules.class);

	
	/**
	 * Instantiate a series rules instance. SeriesDoseRules and applicableSeasons set to empty. Set flag to calculate dose number based on disease tally to true by default.
	 * @param pSeriesName Series name, must be provided
	 * @param pVaccineGroup CdsConcept representing the vaccine group, must be provided
	 * @throws IllegalArgumentException of series name or vaccine group is null
	 */
	/////// public SeriesRules(String pSeriesName, String pVaccineGroup) {
	public SeriesRules(String pSeriesName, CdsConcept pVaccineGroup) {

		String _METHODNAME = "Series(): ";
		if (pSeriesName == null || pVaccineGroup == null || pVaccineGroup.getOpenCdsConceptCode() == null) {		// the latter condition should never occur
			String str = "series name and/or vaccine group name is not supplied";
			logger.warn(_METHODNAME + str);
			throw new IllegalArgumentException(str);
		}
		// seriesId = pSeriesName;
		seriesId = ICELogicHelper.generateUniqueString();
		seriesName = pSeriesName;
		vaccineGroupConcept = pVaccineGroup;
		seriesDoseRules = new ArrayList<DoseRule>();
		applicableSeasons = new ArrayList<Season>();
		numberOfDosesInSeries = 0;
		doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered = true;
		recurringDosesAfterSeriesComplete = false;
	}

	
	/**
	 * Instantiate a series rules instance with the specified Seasons. If seasons are not specified, then the series is not treated as a Seasonal series.
	 * @param pSeriesName
	 * @param pVaccineGroup
	 * @param pApplicableSeasons
	 */
	/////// public SeriesRules(String pSeriesName, String pVaccineGroup, List<Season> pApplicableSeasons) {
	public SeriesRules(String pSeriesName, CdsConcept pVaccineGroup, List<Season> pApplicableSeasons) {
	
		this(pSeriesName, pVaccineGroup);
		if (pApplicableSeasons != null && pApplicableSeasons.isEmpty() == false) {
			applicableSeasons = pApplicableSeasons;
		}
	}
	
	
	/**
	 * Construct a copy of this object and return i
	 * @return
	 */
	public static SeriesRules constructDeepCopyOfSeriesRulesObject(SeriesRules pSR) {
		
		if (pSR == null) {
			return null;
		}
		
		SeriesRules lSR = new SeriesRules(pSR.getSeriesName(), pSR.getVaccineGroupConcept());
		lSR.seriesId = ICELogicHelper.generateUniqueString();
		lSR.seriesName = pSR.seriesName;
		lSR.vaccineGroupConcept = CdsConcept.constructDeepCopyOfCdsConceptObject(pSR.getVaccineGroupConcept());
		lSR.recurringDosesAfterSeriesComplete = pSR.recurringDosesAfterSeriesComplete;
		lSR.doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered = pSR.doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered;
		lSR.applicableSeasons = new ArrayList<Season>();
		
		// Copy Doses
		List<DoseRule> lDoseRules = new ArrayList<DoseRule>();
		List<DoseRule> pDoseRules = pSR.getSeriesDoseRules();
		for (DoseRule pDR : pDoseRules) {
			DoseRule lDR = DoseRule.constructDeepCopyOfDoseRuleObject(pDR);
			lDoseRules.add(lDR);
		}
		lSR.setSeriesDoseRules(lDoseRules);
		
		// Copy Seasons
		List<Season> lSeasons = new ArrayList<Season>();
		List<Season> pSeasons = pSR.getSeasons();
		for (Season pS : pSeasons) {
			Season lS = Season.constructDeepCopyOfSeasonObject(pS);
			lSeasons.add(lS);
		}
		lSR.applicableSeasons = lSeasons;
		
		return lSR;
	}
	
	
	public String getSeriesId() {
		return seriesId;		
	}

	
	public String getSeriesName() {
		return seriesName;
	}

	/**
	 * Set the Series Name. Cannot be null. 	 
	 * @param seriesName
	 */
	public void setSeriesName(String seriesName) {
		if (seriesName == null) {
			throw new IllegalArgumentException("seriesName cannot be null");
		}
		this.seriesName = seriesName;
	}
	
	private CdsConcept getVaccineGroupConcept() {
		return vaccineGroupConcept;
	}
	
	public String getVaccineGroup() {
		return vaccineGroupConcept.getOpenCdsConceptCode();
	}
	
	public int getNumberOfDosesInSeries() {
		return numberOfDosesInSeries;
	}

	public void setNumberOfDosesInSeries(int numberOfDosesInSeries) {
		this.numberOfDosesInSeries = numberOfDosesInSeries;
	}
	
	public boolean isDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered() {
		return doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered;
	}
	
	public void setDoseNumberCalculationBasedOnDiseasesTargetedByVaccinesAdministered(boolean yesno) {
		doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered = yesno;
	}
	
	public boolean recurringDosesOccurAfterSeriesComplete() {
		return recurringDosesAfterSeriesComplete;
	}

	public void setRecurringDosesAfterSeriesComplete(boolean yesno) {
		this.recurringDosesAfterSeriesComplete = yesno;
	}


	/**
	 * Return Dose Rules for Series, or empty set if there are none
	 * @return
	 */
	public List<DoseRule> getSeriesDoseRules() {
		return seriesDoseRules;
	}
	
	
	/**
	 * Get DoseRule by dose number
	 * @param doseNumber
	 * @return DoseRule, or NULL if there is no such DoseRule in this series
	 */
	public DoseRule getSeriesDoseRuleByDoseNumber(int doseNumber) {
		
		for (DoseRule dr : seriesDoseRules) {
			if (dr.getDoseNumber() == doseNumber) {
				return dr;
			}
		}
		
		return null;
	}
	
	/**
	 * Return relevant Seasons for Series, or empty set if there are none
	 * @return
	 */
	public List<Season> getSeasons() {
		return applicableSeasons;
	}

	/**
	 * Add additional Season to the list of Seasons supported by this Series. If a default Season is provided, an IllegalArgumentException is thrown.
	 * @param v
	 * @return
	 */
	public void addFullySpecifiedSeason(Season pS) {
		
		String _METHODNAME = "addSeason(Season): ";
		if (pS == null) {
			return;
		}
		
		if (pS.isDefaultSeason()) {
			String errStr = "a default Season was supplied as a Season parameter to this series when one already exists";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		applicableSeasons.add(pS);
	}

	public boolean vaccineIsAllowableInOneOrMoreDoseRules(Vaccine v) {
		
		return isAllowableVaccineForDoseRule(v, true, 0);	
	}
	
	
	public boolean isAllowableVaccineForDoseRule(Vaccine v, int doseNumber) {
		
		return isAllowableVaccineForDoseRule(v, false, doseNumber);
	}

	
	private boolean isAllowableVaccineForDoseRule(Vaccine v, boolean allowableForAnyDose, int doseNumber) {

		String _METHODNAME = "isAllowableVaccineForDoseRule(): ";
		if (seriesDoseRules == null || v == null) {
			return false;
		}

		String vCdsListItemName = v.getCdsConceptName();
		if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "vaccine cdsListItemName: " + vCdsListItemName + "; doseNumber: " + doseNumber + "; allowable any " + allowableForAnyDose);
		}
		if (vCdsListItemName == null) {
			return false;
		}

		for (DoseRule dr : seriesDoseRules) {
			int drDoseNumber = dr.getDoseNumber();
			if (logger.isDebugEnabled()) { 
				logger.debug(_METHODNAME + "dose number: " + drDoseNumber);
			}
			if (! allowableForAnyDose && drDoseNumber < doseNumber) {
				continue;
			}
			else if (allowableForAnyDose || dr.getDoseNumber() == doseNumber) {
				List<Vaccine> allPermittedComponentVaccines = dr.getAllPermittedVaccines();
				for (Vaccine permittedVaccine : allPermittedComponentVaccines) {
					if (permittedVaccine != null) {
						String permittedVaccineCdsListItemName = permittedVaccine.getCdsConceptName();
						if (permittedVaccineCdsListItemName == null) {
							continue;
						}
						if (vCdsListItemName.equals(permittedVaccineCdsListItemName)) {
							if (logger.isDebugEnabled()) {
								logger.debug(_METHODNAME + "allowable vaccine: " + v.getCdsConceptName());
							}
							return true;
						}
					}
				}
			}
			else {
				break;
			}
		}
		
		return false;
	}

	/**
	 * Add a DoseRule to the Series. Doses must be added to the SeriesRules in sequential order (1...n), or an IllegalArgumentException is thrown.
	 */
	public void addSeriesDoseRule(DoseRule pDoseRule) {
	
		String _METHODNAME = "addSeriesDoseRule(): ";
		if (pDoseRule == null) {
			return;
		}
		
		int lDoseRuleDoseNumber = pDoseRule.getDoseNumber();
		if (lDoseRuleDoseNumber <= 0) {
			String errStr = "No dose number is supplied for the specified DoseRule";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		int lCurrentNumberOfDoses = this.seriesDoseRules.size();
		if (lDoseRuleDoseNumber != lCurrentNumberOfDoses+1) {
			String errStr = "Dose number supplied for the specified DoseRule is not the next dose number for this SeriesRules. Doses must be added in sequential order by dose number";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		
		this.seriesDoseRules.add(pDoseRule);
		setNumberOfDosesInSeries(lDoseRuleDoseNumber);
	}
	
	
	/**
	 * Modify an existing DoseRule in the Series. If the dose number (via specifide DoseRule) does not exist in the SeriesRules, then an IllegalArgumentException
	 * is thrown. 
	 */
	public void modifySeriesDoseRule(DoseRule pDoseRule) {
		
		String _METHODNAME = "modifySeriesDoseRule(): ";
		if (pDoseRule == null) {
			return;
		}
		
		int lDoseRuleDoseNumber = pDoseRule.getDoseNumber();
		if (lDoseRuleDoseNumber <= 0 || lDoseRuleDoseNumber > getNumberOfDosesInSeries()) {
			String errStr = "DoseRule specified does not have a dose number that is valid for this SeriesRules. SeriesDose DoseRule was not modified.";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}

		List<DoseRule> lSeriesDoseRules = new ArrayList<DoseRule>();
		for (DoseRule lDR : this.seriesDoseRules) {
			if (lDR.getDoseNumber() == lDoseRuleDoseNumber) {
				lSeriesDoseRules.add(pDoseRule);
			}
			else {
				lSeriesDoseRules.add(lDR);
			}
		}
		this.seriesDoseRules = lSeriesDoseRules;
	}
	
	
	/**
	 * Set the List of DoseRules for this series. Update the numberOfDosesInSeries based on the provided number of DoseRules. There must be a dose number  
	 * that matches the size of the List. If the size does not match with each dose number accounted for, an IllegalArgumentException is thrown.
	 * @param pDoseRules
	 */
	public void setSeriesDoseRules(List<DoseRule> pDoseRules) {

		String _METHODNAME = "setSeriesDoseRules(List<DoseRule>): "; 
		if (pDoseRules == null) {
			this.seriesDoseRules = new ArrayList<DoseRule>();
			setNumberOfDosesInSeries(0);
		}
		else {
			// Go through list of DoseRules provided; there must be a dose number (unique) that matches the size of the List
			int lNumberOfDoses = pDoseRules.size();
			HashSet<Integer> lDoseNumbers = new HashSet<Integer>();
			for (DoseRule lDR : pDoseRules) {
				int lDRDoseNumber = lDR.getDoseNumber();
				int lDRDoseNumberInt = new Integer(lDRDoseNumber);
				if (lDRDoseNumber > lNumberOfDoses || lDoseNumbers.contains(lDRDoseNumberInt)) {
					String lErrStr = "Invalid Dose Number supplied for DoseRule; either a duplicate dose number or greater than the number of Doses for the Series";
					logger.warn(_METHODNAME + lErrStr);
					throw new IllegalArgumentException(lErrStr);
				}
				else {
					lDoseNumbers.add(lDRDoseNumberInt);
				}
			}
			
			this.seriesDoseRules = pDoseRules;
			setNumberOfDosesInSeries(lNumberOfDoses);
		}		
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seriesName == null) ? 0 : seriesName.hashCode());
		result = prime * result + ((vaccineGroupConcept == null) ? 0 : vaccineGroupConcept.hashCode());
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
		SeriesRules other = (SeriesRules) obj;
		if (seriesName == null) {
			if (other.seriesName != null)
				return false;
		} else if (!seriesName.equals(other.seriesName))
			return false;
		if (vaccineGroupConcept == null) {
			if (other.vaccineGroupConcept != null)
				return false;
		} else if (!vaccineGroupConcept.equals(other.vaccineGroupConcept))
			return false;
		return true;
	}


	@Override
	public String toString() {
		
		String toStr = "SeriesRules [seriesId = " + seriesId + "; Series Name = " + seriesName + "; vaccineGroupConcept name = " + vaccineGroupConcept.getOpenCdsConceptCode()
				+ "; Number of Doses In Series = " + numberOfDosesInSeries + "; Recurring Doses (After Series Complete)? = "
				+ recurringDosesAfterSeriesComplete + "; Dose Number Calculated By Diseases Targeted By Each Vaccine = " 
				+ doseNumberCalculatedBasedOnDiseasesTargetedByEachVaccineAdministered; 
		
		int i=1;
		toStr += "\nDose Rules [[ ";
		for (DoseRule dr : seriesDoseRules) {
			toStr += "\n\tDoseRule {" + i + "}: " + dr.toString();
			i++;
		}
		toStr += "\t]]";
		i=1;
		toStr += "\nSeasons [[ ";
		for (Season s : applicableSeasons) {
			toStr +="\n\tSeason {" + i + "}: " + s.toString();
			i++;
		}
		toStr += "\t]]";
		toStr += "\n]\n";
		
		return toStr;
	}
	
}
