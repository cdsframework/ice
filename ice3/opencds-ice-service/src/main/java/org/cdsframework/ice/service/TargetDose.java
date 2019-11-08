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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;


public class TargetDose {
	
	private String uniqueId;
	private String doseId;
	private TargetSeries associatedTargetSeries;
	private Vaccine administeredVaccine;
	private VaccineComponent vaccineComponent;
	private int administeredShotNumberInSeries;
	private Date administrationDate;
	private int doseNumberInSeries;
	private int doseNumberCount;
	private boolean isPrimarySeriesShot;
	private boolean isValid;
	private boolean isShotIgnored;
	private boolean hasBeenEvaluated;
	private boolean preEvaluationCheckCompleted;
	private boolean postEvaluationCheckCompleted;
	private boolean duplicateShotSameDayEvaluationOrderCompleted;
	private boolean duplicateShotSameDayCheckCompleted;
	private boolean doseStatusOverridden;
	private DoseStatus status;
	private HashSet<String> validReasons;
	private HashSet<String> acceptedReasons;
	private HashSet<String> invalidReasons;
	private HashSet<String> notEvaluatedReasons;
	private HashSet<String> doseRulesProcessed;
	
	private static Log logger = LogFactory.getLog(TargetDose.class);

	
	/**
	 * Initialize a TargetDose.
	 * @param pDoseId
	 * @param pVaccineComponentToBeEvaluated
	 * @param pAdministrationDate
	 * @throws IllegalArgumentException if the Dose ID, vaccine or administration date is not populated
	 */
	public TargetDose(Vaccine pAdministeredVaccine, VaccineComponent pVaccineComponentToBeEvaluated, Date pAdministrationDate, TargetSeries pEncompassingTargetSeries,
			SubstanceAdministrationEvent pAssociatedSAE) {	
		
		if (pAdministeredVaccine == null || pVaccineComponentToBeEvaluated == null || pAdministrationDate == null || pEncompassingTargetSeries == null || pAssociatedSAE == null) {
			logger.error("TargetDose(): Dose ID, Vaccine, Vaccine Component to be Evaluated, Administration Date not supplied, Associated SubstanceAdministrationEvent and/or Encompassing TargetSeries not supplied");
			throw new IllegalArgumentException("TargetDose(): Dose ID, Vaccine Component to be Evaluated, Vaccine, Administration Date not supplied and/or Encompassing TargetSeries not supplied");
		}
		uniqueId = ICELogicHelper.generateUniqueString();
		doseId = pAssociatedSAE.getId();
		associatedTargetSeries = pEncompassingTargetSeries;
		administeredVaccine = pAdministeredVaccine;
		vaccineComponent = pVaccineComponentToBeEvaluated;
		administeredShotNumberInSeries = 0;
		administrationDate = pAdministrationDate;
		doseNumberInSeries = 1;
		doseNumberCount = 1;
		status = DoseStatus.EVALUATION_NOT_STARTED;
		validReasons = new HashSet<String>();
		acceptedReasons = new HashSet<String>();
		invalidReasons = new HashSet<String>();
		notEvaluatedReasons = new HashSet<String>();
		doseRulesProcessed = new HashSet<String>();
		isPrimarySeriesShot = false;
		isValid = false;
		isShotIgnored = false;
		hasBeenEvaluated = false;
		preEvaluationCheckCompleted = false;
		postEvaluationCheckCompleted = false;
		doseStatusOverridden = false;
		duplicateShotSameDayEvaluationOrderCompleted = false;
		duplicateShotSameDayCheckCompleted = false;
	}

	public void addDoseRuleProcessed(String ruleName) {
		if (ruleName != null && ! doseRulesProcessed.contains(ruleName)) {
			doseRulesProcessed.add(ruleName);
		}
	}
	
	public boolean containsRuleProcessed(String ruleName) {
		
		return doseRulesProcessed.contains(ruleName);
	}
	
	public boolean containsInvalidReason(String openCdsConceptCode) {
		
		return invalidReasons.contains(openCdsConceptCode);
	}
	
	public boolean containsAcceptedReason(String openCdsConceptCode) {

		return acceptedReasons.contains(openCdsConceptCode);
	}

	public boolean containsValidReason(String openCdsConceptCode) {

		return validReasons.contains(openCdsConceptCode);
	}
	
	public boolean containsNotEvaluatedReason(String openCdsConceptCode) {

		return notEvaluatedReasons.contains(openCdsConceptCode);
	}
	
	public boolean containsReason(String openCdsConceptCode) {
		
		return getAllEvaluationReasonsFromAllReasonSets().contains(openCdsConceptCode);
	}
	
	public Collection<String> getAllEvaluationReasonsFromAllReasonSets() {
	
		List<String> allReasons = new ArrayList<String>();
		allReasons.addAll(invalidReasons);
		allReasons.addAll(acceptedReasons);
		allReasons.addAll(validReasons);
		allReasons.addAll(notEvaluatedReasons);
		return allReasons;
	}
	
	public void removeAllEvaluationReasonsFromAllReasonSets() {
		validReasons = new HashSet<String>();
		acceptedReasons = new HashSet<String>();
		invalidReasons = new HashSet<String>();
	}
	
	public void removeEvaluationReasonFromAllReasonSets(String openCdsConceptCode) {
		
		if (openCdsConceptCode != null) {
			invalidReasons.remove(openCdsConceptCode);
			acceptedReasons.remove(openCdsConceptCode);
			validReasons.remove(openCdsConceptCode);
			notEvaluatedReasons.remove(openCdsConceptCode);
		}
	}
	
	public void removeValidReason(String openCdsConceptCode) {
		
		if (openCdsConceptCode != null) {
			validReasons.remove(openCdsConceptCode);
		}
	}
	
	public void removeAcceptedReason(String openCdsConceptCode) {
		
		if (openCdsConceptCode != null) {
			acceptedReasons.remove(openCdsConceptCode);
		}
	}
	
	public void removeInvalidReason(String openCdsConceptCode) {
		
		if (openCdsConceptCode != null) {
			invalidReasons.remove(openCdsConceptCode);
		}
	}

	public void removeNotEvaluatedReason(String openCdsConceptCode) {
		
		if (openCdsConceptCode != null) {
			notEvaluatedReasons.remove(openCdsConceptCode);
		}
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public TargetSeries getAssociatedTargetSeries() {
		
		return associatedTargetSeries;
	}
	
	public TargetSeries getTargetSeries() {
		
		return associatedTargetSeries;
	}

	public String getAssociatedVaccineGroup() {
		return getAssociatedTargetSeries().getVaccineGroup();
	}

	public String getAssociatedSeriesName() {
		return getAssociatedTargetSeries().getSeriesName();
	}
		
	/**
	 * Return ID of this TargetDose, which matches the ID of the associated SubstanceAdministrationEvent when this TargetDose was created
	 */
	public String getDoseId() {
		return doseId;
	}

	/**
	 * Return administered shot number in series, which is ordered by increasing administration date relative to all other shots in the series, 
	 * not whether it is valid or not
	 * @return administered shot number in series, regardless of whether it is a valid shot or not.
	 */
	public int getAdministeredShotNumberInSeries() {
		return administeredShotNumberInSeries;
	}

	/**
	 * Likely to only be called by the TargetSeries that contains this administered dose
	 * @param administedShotNumber
	 */
	public void setAdministeredShotNumberInSeries(int administeredShotNumber) {
		this.administeredShotNumberInSeries = administeredShotNumber;
	}
	
	/**
	 * Returns the valid shot number in the series (relative to all other valid shots). 
	 * @return valid dose number in series
	 */
	public int getDoseNumberInSeries() {
		return doseNumberInSeries;
	}
	
	/**
	 * Likely to only be called by the TargetSeries that contains this administered dose
	 * @param pDoseNumber
	 */
	public void setDoseNumberInSeries(int pDoseNumber) {
		this.doseNumberInSeries = pDoseNumber;
	}

	/**
	 * Returns the valid shot number in the series (relative to all other valid shots). 
	 * @return valid dose number in series
	 */
	public int getDoseNumberCount() {
		return doseNumberCount;
	}
	
	/**
	 * Likely to only be called by the TargetSeries that contains this administered dose
	 * @param pDoseNumber
	 */
	public void setDoseNumberCount(int pDoseNumber) {
		this.doseNumberCount = pDoseNumber;
	}
	
	/**
	 * Returns whether or not this shot is a part of the primary series
	 */
	public boolean isPrimarySeriesShot() {
		return isPrimarySeriesShot;
	}
	
	public void setIsPrimarySeriesShot(boolean yesno) {
		isPrimarySeriesShot = yesno;
	}
	
	/**
	 * Returns whether this is a valid dose or not. 
	 * @return true of the DoseStatus is either DoseStatus.VALID, false if not
	 */
	public boolean getIsValid() {
		return isValid;
	}

	/**
	 * This method is private; shot validity should be set via setStatus()
	 * @param isValid
	 */
	private void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isShotIgnoredForCompletionOfSeries() {
		return isShotIgnored;
	}

	public void setIsShotIgnoredForCompletionOfSeries(boolean ignoredForCompletionOfSeries) {
		this.isShotIgnored = ignoredForCompletionOfSeries;
	}

	public boolean hasBeenEvaluated() {
		return hasBeenEvaluated;
	}

	public void setHasBeenEvaluated(boolean hasBeenEvaluated) {
		this.hasBeenEvaluated = hasBeenEvaluated;
	}

	public boolean isPreEvaluationCheckCompleted() {
		return preEvaluationCheckCompleted;
	}
	
	public void setPreEvaluationCheckCompleted(boolean truefalse) {
		this.preEvaluationCheckCompleted = truefalse;
	}
	
	public boolean isPostEvaluationCheckCompleted() {
		return postEvaluationCheckCompleted;
	}
	
	public void setPostEvaluationCheckCompleted(boolean truefalse) {
		this.postEvaluationCheckCompleted = truefalse;
	}
	
	public boolean isDuplicateShotSameDayEvaluationOrderCompleted() {
		return duplicateShotSameDayEvaluationOrderCompleted;
	}
	
	public void setDuplicateShotSameDayEvaluationOrderCompleted(boolean truefalse) {
		this.duplicateShotSameDayEvaluationOrderCompleted = truefalse;
	}
	
	public boolean isDuplicateShotSameDayCheckCompleted() {
		return duplicateShotSameDayCheckCompleted;
	}
	
	public void setDuplicateShotSameDayCheckCompleted(boolean truefalse) {
		this.duplicateShotSameDayCheckCompleted = truefalse;
	}
	
	public boolean isDoseStatusOverridden() {
		return this.doseStatusOverridden;
	}
	
	public void setDoseStatusOverridden(boolean truefalse) {
		this.doseStatusOverridden = truefalse;
	}

	public Vaccine getAdministeredVaccine() {
		return administeredVaccine;
	}
	
	public VaccineComponent getVaccineComponent() {
		return vaccineComponent;
	}

	public void setVaccineComponent(VaccineComponent vaccine) {
		this.vaccineComponent = vaccine;
	}
	
	public DoseStatus getStatus() {
		return status;
	}

	public void setStatus(DoseStatus status) {

		this.status = status;
		setHasBeenEvaluated(false);
		
		if (status != null) {
			if (status == DoseStatus.ACCEPTED || status == DoseStatus.INVALID || status == DoseStatus.VALID)
				setHasBeenEvaluated(true);
			if (status == DoseStatus.VALID) {
				setIsValid(true);
			}
			else {
				setIsValid(false);
			}
			if (status == DoseStatus.EVALUATION_NOT_STARTED || status == DoseStatus.PRIMARY_SHOT_DETERMINATION_IN_PROCESS) {
				setPostEvaluationCheckCompleted(false);
				setPreEvaluationCheckCompleted(false);
				removeAllEvaluationReasonsFromAllReasonSets();
			}
		}
	}

	public Date getAdministrationDate() {
		return administrationDate;
	}

	/**
	 * Set the administration date of the shot. If the administration date is null, throw an IllegalArgumentException
	 * @param administrationDate The administration date of the shot
	 */
	public void setAdministrationDate(Date administrationDate) {
		
		String _METHODNAME = "setAdministrationDate(): ";
		if (administrationDate == null) {
			String errStr = "Administration Date not supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		this.administrationDate = administrationDate;
	}

	public Collection<String> getValidReasons() {
		return validReasons;
	}
	
	/**
	 * Adds the valid reason, if not already present
	 * @param reason null reasons are permitted
	 */
	public void addValidReason(String reason) {
		if (reason != null && ! validReasons.contains(reason))
			validReasons.add(reason);
	}

	public Collection<String> getAcceptedReasons() {
		return acceptedReasons;
	}
	
	/**
	 * Adds the accepted reason, if not already present
	 * @param reason
	 */
	public void addAcceptedReason(String reason) {
		
		if (reason != null && ! acceptedReasons.contains(reason))
			acceptedReasons.add(reason);
	}

	public Collection<String> getInvalidReasons() {
		return invalidReasons;
	}
	
	/**
	 * Adds the invalid reason, if not already present
	 * @param reason
	 */
	public void addInvalidReason(String reason) {
		if (reason != null && ! invalidReasons.contains(reason))
			invalidReasons.add(reason);
	}

	public Collection<String> getNotEvaluatedReasons() {
		return notEvaluatedReasons;
	}
	
	/**
	 * Adds the invalid reason, if not already present
	 * @param reason
	 */
	public void addNotEvalatedReason(String reason) {
		if (reason != null && ! notEvaluatedReasons.contains(reason))
			notEvaluatedReasons.add(reason);
	}
	
	@Override
	public String toString() {
		
		String s = "TargetDose [uniqueId=" + uniqueId + ", doseId=" + doseId + ", administeredShotNumber=" + administeredShotNumberInSeries + 
				"; doseNumber=" + doseNumberInSeries + ", doseNumberCount=" + doseNumberCount + "vaccine=" + administeredVaccine.getCdsConceptName() + ", isPrimarySeriesShot=" + isPrimarySeriesShot() + 
				"; vaccineComponent=" + vaccineComponent.getCdsConceptName() + ", administrationDate=" + administrationDate + ", status=" + status + 
				"; isValid=" + isValid + "; preEvaluationCheck=" + preEvaluationCheckCompleted + "; isLiveVirus: " + this.getAdministeredVaccine().isLiveVirusVaccine() + 
				"; isCombinationVaccine: " + this.getAdministeredVaccine().isCombinationVaccine() + "; componentIsLiveVirus: " + this.getVaccineComponent().isLiveVirusVaccine() + 
				"; isAdjuvant: " + this.getAdministeredVaccine().isSelectAdjuvantProduct() + "; componentIsAdjuvant: " + this.getVaccineComponent().isSelectAdjuvantProduct() +
				"; isDuplicateShotSameDayCheckCompleted: " + this.isDuplicateShotSameDayCheckCompleted() + ", isUnspecifiedFormulation(): " + this.getVaccineComponent().isUnspecifiedFormulation();
		int i=0;
		for (String reason : validReasons) {
			if (i == 0)
				s += ", validReasons={\"" + reason + "\"";
			else
				s += "\"" + reason + "\"";
			i++;
		}
		if (i > 0) {
			s += "}";
		}
		i = 0;
		for (String reason : acceptedReasons) {
			if (i == 0)
				s += ", acceptedReasons={\"" + reason + "\"";
			else
				s += "\"" + reason + "\"";
			i++;
		}
		if (i > 0) {
			s += "}";
		}
		i = 0;
		for (String reason : invalidReasons) {
			if (i == 0)
				s += ", invalidReasons={\"" + reason + "\"";
			else
				s += "\"" + reason + "\"";
			i++;
		}
		if (i > 0) {
			s += "}";
		}
		for (String reason : notEvaluatedReasons) {
			if (i == 0)
				s += ", notEvaluatedReasons={\"" + reason + "\"";
			else
				s += "\"" + reason + "\"";
			i++;
		}
		if (i > 0) {
			s += "}";
		}
		s+= "]";
		
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((uniqueId == null) ? 0 : uniqueId.hashCode());
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
		TargetDose other = (TargetDose) obj;
		if (uniqueId == null) {
			if (other.uniqueId != null)
				return false;
		} else if (!uniqueId.equals(other.uniqueId))
			return false;
		return true;
	}

}
