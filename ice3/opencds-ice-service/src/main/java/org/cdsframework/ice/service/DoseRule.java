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

package org.cdsframework.ice.service;

import java.util.ArrayList;
import java.util.List;
import org.cdsframework.ice.util.TimePeriod;
import org.kie.api.definition.type.ClassReactive;


/**
 * The DoseRule contains the preferable, allowable, ages and intervals. Intervals expressed here are currently from this dose to the next (dosenumber+1) dose. This model be 
 * extended to permit expressing intervals from any dose number to any other dose number in a future release. In the meantime, a custom rule will need to be written if this 
 * capability is needed.
 */
@ClassReactive
public class DoseRule {

	private int doseNumber;
	private TimePeriod absoluteMinimumAge;				// absolute minimum age is usually the minimum age - grace period
	private TimePeriod minimumAge;
	private TimePeriod maximumAge;
	private TimePeriod earliestRecommendedAge;
	private TimePeriod latestRecommendedAge;
	private TimePeriod absoluteMinimumInterval;			// absolute minimum interval is usually the minimum interval - grace period. 
	private TimePeriod minimumInterval;					
	private TimePeriod earliestRecommendedInterval;		
	private TimePeriod latestRecommendedInterval;		
	private List<Vaccine> preferableVaccines;
	private List<Vaccine> allowableVaccines;
	
	// private static Log logger = LogFactory.getLog(DoseRules.class);
	
	/**
	 * Creates a dose level rule that is at the vaccine group level
	 */
	public DoseRule() {
		preferableVaccines = new ArrayList<Vaccine>();
		allowableVaccines = new ArrayList<Vaccine>();
	}
	
	/**
	 * Creates a dose level rule that is specific to a vaccine
	 * @param pVaccine
	 */
	public DoseRule(List<Vaccine> preferableComponentVaccines, List<Vaccine> allowableComponentVaccines) {
		// String _METHODNAME = "Dose(List<Vaccine>, List<Vaccine>): ";
	
		setPreferableVaccines(preferableComponentVaccines);
		setAllowableVaccines(allowableComponentVaccines);
	}
	
	/**
	 * Construct deep copy of DoseRule Object and return newly created object
	 */
	public static DoseRule constructDeepCopyOfDoseRuleObject(DoseRule pDR) {
		
		if (pDR == null) {
			return null;
		}
		
		DoseRule lDR = new DoseRule();
		lDR.doseNumber = pDR.doseNumber;
		lDR.absoluteMinimumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.absoluteMinimumAge);
		lDR.minimumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.minimumAge);
		lDR.maximumAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.maximumAge);
		lDR.earliestRecommendedAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.earliestRecommendedAge);
		lDR.latestRecommendedAge = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.latestRecommendedAge);
		lDR.absoluteMinimumInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.absoluteMinimumInterval);
		lDR.minimumInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.minimumInterval);
		lDR.earliestRecommendedInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.earliestRecommendedInterval);
		lDR.latestRecommendedInterval = TimePeriod.constructDeepCopyOfTimePeriodObject(pDR.latestRecommendedInterval);
		
		List<Vaccine> lPreferableVaccines = new ArrayList<Vaccine>();
		List<Vaccine> pPreferableVaccines = pDR.getPreferableVaccines();
		for (Vaccine pV : pPreferableVaccines) {
			lPreferableVaccines.add(Vaccine.constructDeepCopyOfVaccineObject(pV));
		}
		lDR.setPreferableVaccines(lPreferableVaccines);

		List<Vaccine> lAllowableVaccines = new ArrayList<Vaccine>();
		List<Vaccine> pAllowableVaccines = pDR.getAllowableVaccines();
		for (Vaccine pV : pAllowableVaccines) {
			lAllowableVaccines.add(Vaccine.constructDeepCopyOfVaccineObject(pV));
		}
		lDR.setAllowableVaccines(lAllowableVaccines);
		
		return lDR;
	}

	
	/**
	 * Gets the preferable vaccines
	 */
	public List<Vaccine> getPreferableVaccines() {
		// return vaccine;
		return preferableVaccines;
	}

	/**
	 * Sets the preferable vaccines for this dose rule. If the vaccines supplied is null, preferable vaccines is set to the empty set
	 * @param vaccines
	 */
	public void setPreferableVaccines(List<Vaccine> vaccines) {

		this.preferableVaccines = new ArrayList<Vaccine>();
		if (vaccines == null) {
			return;
		}
		else {
			for (Vaccine pV : vaccines) {
				addPreferableVaccine(pV);
			}
		}
	}	
	
	
	public void allPreferableVaccines(List<Vaccine> pVaccines) {
		
		if (pVaccines == null) {
			return;
		}
		
		for (Vaccine lV : pVaccines) {
			addPreferableVaccine(lV);
		}
	}
	
	
	public void addAllowableVaccines(List<Vaccine> pVaccines) {
		
		if (pVaccines == null) {
			return;
		}
		
		for (Vaccine lV : pVaccines) {
			addAllowableVaccine(lV);
		}
	}
	
	public void addPreferableVaccine(Vaccine v) {
		
		if (v == null) {
			return;
		}

		if (this.preferableVaccines == null) {
			setPreferableVaccines(null);
		}
		if (! this.preferableVaccines.contains(v)) {		// TODO: V
			this.preferableVaccines.add(v);
		}
	}

	
	public List<Vaccine> getAllowableVaccines() {
		return this.allowableVaccines;
	}
	
	/**
	 * Sets the allowable vaccines for this dose rule. If the vaccines supplied is null, preferable vaccines is set to the empty set
	 * @param vaccine
	 */
	public void setAllowableVaccines(List<Vaccine> vaccines) {
		
		this.allowableVaccines = new ArrayList<Vaccine>();
		if (vaccines == null) {
			return;
		}
		else {
			for (Vaccine pV : vaccines) {
				addAllowableVaccine(pV);
			}
		}
	}
	
	
	public void addAllowableVaccine(Vaccine v) {
		
		if (v == null) {
			return;
		}
		
		if (this.allowableVaccines == null) {
			setAllowableVaccines(null);
		}
		if (! this.allowableVaccines.contains(v)) {			// TODO: V
			this.allowableVaccines.add(v);
		}
	}
	

	public List<Vaccine> getAllPermittedVaccines() {
	
		List<Vaccine> all = new ArrayList<Vaccine>();
		if (this.preferableVaccines != null) {
			for (Vaccine pref : preferableVaccines) {
				if (pref != null && ! all.contains(pref))
					all.add(pref);
			}
		}
		
		if (this.allowableVaccines != null) {
			for (Vaccine allowed : allowableVaccines) {
				if (allowed != null && ! all.contains(allowed)) {
					all.add(allowed);
				}
			}
		}
		
		return all;
	}
	
	
	public int getDoseNumber() {
		return doseNumber;
	}
	
	public void setDoseNumber(int doseNumber) {
		this.doseNumber = doseNumber;
	}
	
	public void setAbsoluteMinimumAge(TimePeriod absoluteMinimumAge) {
		this.absoluteMinimumAge = absoluteMinimumAge;
	}
	
	public TimePeriod getAbsoluteMinimumAge() {
		return absoluteMinimumAge;
	}
	
	public TimePeriod getMinimumAge() {
		return minimumAge;
	}
	
	public void setMinimumAge(TimePeriod minimumAge) {
		this.minimumAge = minimumAge;
	}
	
	public TimePeriod getMaximumAge() {
		return maximumAge;
	}
	
	public void setMaximumAge(TimePeriod maximumAge) {
		this.maximumAge = maximumAge;
	}
	
	public TimePeriod getEarliestRecommendedAge() {
		return earliestRecommendedAge;
	}
	
	public void setEarliestRecommendedAge(TimePeriod routineAge) {
		this.earliestRecommendedAge = routineAge;
	}
	
	public TimePeriod getLatestRecommendedAge() {
		return latestRecommendedAge;
	}
	
	public void setLatestRecommendedAge(TimePeriod routineAge) {
		this.latestRecommendedAge = routineAge;
	}
	
	public void setAbsoluteMinimumInterval(TimePeriod absoluteMinimumInterval) {
		this.absoluteMinimumInterval = absoluteMinimumInterval;
	}
	
	public TimePeriod getAbsoluteMinimumInterval() {
		return absoluteMinimumInterval;
	}
	
	public TimePeriod getMinimumInterval() {
		return minimumInterval;
	}
	
	public void setMinimumInterval(TimePeriod minimumInterval) {
		this.minimumInterval = minimumInterval;
	}
	
	public TimePeriod getEarliestRecommendedInterval() {
		return earliestRecommendedInterval;
	}
	
	public void setEarliestRecommendedInterval(TimePeriod recommendedInterval) {
		this.earliestRecommendedInterval = recommendedInterval;
	}
	
	public TimePeriod getLatestRecommendedInterval() {
		return latestRecommendedInterval;
	}
	
	public void setLatestRecommendedInterval(TimePeriod recommendedInterval) {
		this.latestRecommendedInterval = recommendedInterval;
	}

	@Override
	public String toString() {
		
		String toString = "DoseRule [doseNumber=" + doseNumber + "; absoluteMinimumAge=" + absoluteMinimumAge + "; minimumAge="
				+ minimumAge + "; maximumAge=" + maximumAge + "; earliestRecommendedAge=" + earliestRecommendedAge
				+ "; latestRecommendedAge=" + latestRecommendedAge + "; absoluteMinimumInterval="
				+ absoluteMinimumInterval + "; minimumInterval=" + minimumInterval + "; earliestRecommendedInterval="
				+ earliestRecommendedInterval + "; latestRecommendedInterval=" + latestRecommendedInterval;
		
		toString += "\npreferableVaccines [[ ";
		int i=1;
		for (Vaccine v : preferableVaccines) {
			toString += "\n\tpreferableVaccine {" + i + "}: " + v.toString();
			i++;
		}
		toString += "\n\t]]";
		i=1;
		for (Vaccine v : allowableVaccines) {
			toString += "\n\tallowableVaccine {" + i + "}: " + v.toString();
			i++;
		}
		toString += "\n\t]]";
		toString += "\n]";
		
		return toString;
	}

	
}
