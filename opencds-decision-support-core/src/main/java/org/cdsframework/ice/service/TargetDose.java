/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.kie.api.definition.type.ClassReactive;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Slf4j
@ClassReactive
public class TargetDose
{
    @EqualsAndHashCode.Include
    private final String uniqueId;
    private final String doseId;
    private final TargetSeries associatedTargetSeries;
    private final Vaccine administeredVaccine;
    private final Set<String> notEvaluatedReasons;
    private final Set<String> doseRulesProcessed;
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
    private Set<String> validReasons;
    private Set<String> acceptedReasons;
    private Set<String> invalidReasons;
    private Set<String> supplementalTextsForValidShots;
    private Set<String> supplementalTextsForAcceptedShots;
    private Set<String> supplementalTextsForInvalidShots;
    private String evaluatedSeriesName;
    private SeriesDisplaySelectionType seriesDisplaySelectionType;
    private Season evaluatedSeriesSeason;

    /**
     * Initialize a TargetDose.
     *
     * @throws IllegalArgumentException if the Dose ID, vaccine or administration date is not populated
     */
    public TargetDose(final Vaccine pAdministeredVaccine, final VaccineComponent pVaccineComponentToBeEvaluated,
            final Date pAdministrationDate, final TargetSeries pEncompassingTargetSeries,
            final SubstanceAdministrationEvent pAssociatedSAE)
    {
        if (pAdministeredVaccine == null || pVaccineComponentToBeEvaluated == null || pAdministrationDate == null
                || pEncompassingTargetSeries == null || pAssociatedSAE == null)
        {
            log.error(
                    "TargetDose(): Dose ID, Vaccine, Vaccine Component to be Evaluated, Administration Date not supplied, Associated SubstanceAdministrationEvent and/or Encompassing TargetSeries not supplied");
            throw new IllegalArgumentException(
                    "TargetDose(): Dose ID, Vaccine Component to be Evaluated, Vaccine, Administration Date not supplied and/or Encompassing TargetSeries not supplied");
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
        validReasons = new HashSet<>();
        acceptedReasons = new HashSet<>();
        invalidReasons = new HashSet<>();
        notEvaluatedReasons = new HashSet<>();
        doseRulesProcessed = new HashSet<>();
        supplementalTextsForValidShots = new HashSet<>();
        supplementalTextsForAcceptedShots = new HashSet<>();
        supplementalTextsForInvalidShots = new HashSet<>();
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

    public void addDoseRuleProcessed(final String ruleName)
    {
        if (ruleName != null)
            doseRulesProcessed.add(ruleName);
    }

    public boolean containsRuleProcessed(final String ruleName)
    {
        return doseRulesProcessed.contains(ruleName);
    }

    public boolean containsInvalidReason(final String openCdsConceptCode)
    {
        return invalidReasons.contains(openCdsConceptCode);
    }

    public boolean onlyInvalidReasonsInSet(final Set<String> reasons)
    {
        return Optional.ofNullable(invalidReasons)
                .filter(ir -> !ir.isEmpty())
                .flatMap(ir -> Optional.ofNullable(reasons).filter(r -> r.containsAll(ir)))
                .isPresent();
    }

    public boolean containsAcceptedReason(final String openCdsConceptCode)
    {
        return acceptedReasons.contains(openCdsConceptCode);
    }

    public boolean containsValidReason(final String openCdsConceptCode)
    {
        return validReasons.contains(openCdsConceptCode);
    }

    public boolean containsNotEvaluatedReason(final String openCdsConceptCode)
    {
        return notEvaluatedReasons.contains(openCdsConceptCode);
    }

    public boolean containsReason(final String openCdsConceptCode)
    {
        return getAllEvaluationReasonsFromAllReasonSets().contains(openCdsConceptCode);
    }

    public Collection<String> getAllEvaluationReasonsFromAllReasonSets()
    {
        final List<String> allReasons = new ArrayList<>();
        allReasons.addAll(invalidReasons);
        allReasons.addAll(acceptedReasons);
        allReasons.addAll(validReasons);
        allReasons.addAll(notEvaluatedReasons);
        return allReasons;
    }

    public void removeAllEvaluationReasonsFromAllReasonSets()
    {
        validReasons = new HashSet<>();
        acceptedReasons = new HashSet<>();
        invalidReasons = new HashSet<>();
    }

    public void removeEvaluationReasonFromAllReasonSets(final String openCdsConceptCode)
    {
        if (openCdsConceptCode != null)
        {
            invalidReasons.remove(openCdsConceptCode);
            acceptedReasons.remove(openCdsConceptCode);
            validReasons.remove(openCdsConceptCode);
            notEvaluatedReasons.remove(openCdsConceptCode);
        }
    }

    public void removeAllSupplementalTextsForValidShot()
    {
        supplementalTextsForValidShots = new HashSet<>();
    }

    public void removeSupplementalTextForValidShot(final String supplementalText)
    {
        if (supplementalText != null)
            supplementalTextsForValidShots.remove(supplementalText);
    }

    public void removeAllSupplementalTextsForAcceptedShot()
    {
        supplementalTextsForAcceptedShots = new HashSet<>();
    }

    public void removeSupplementalTextForAcceptedShot(final String supplementalText)
    {
        if (supplementalText != null)
            supplementalTextsForAcceptedShots.remove(supplementalText);
    }

    public void removeAllSupplementalTextsForInvalidShot()
    {
        supplementalTextsForInvalidShots = new HashSet<>();
    }

    public void removeSupplementalTextForInvalidShot(final String supplementalText)
    {
        if (supplementalText != null)
            supplementalTextsForInvalidShots.remove(supplementalText);
    }

    public void removeValidReason(final String openCdsConceptCode)
    {
        if (openCdsConceptCode != null)
            validReasons.remove(openCdsConceptCode);
    }

    public void removeAcceptedReason(final String openCdsConceptCode)
    {
        if (openCdsConceptCode != null)
            acceptedReasons.remove(openCdsConceptCode);
    }

    public void removeInvalidReason(final String openCdsConceptCode)
    {
        if (openCdsConceptCode != null)
            invalidReasons.remove(openCdsConceptCode);
    }

    public void removeNotEvaluatedReason(final String openCdsConceptCode)
    {
        if (openCdsConceptCode != null)
            notEvaluatedReasons.remove(openCdsConceptCode);
    }

    public TargetSeries getTargetSeries()
    {
        return associatedTargetSeries;
    }

    public String getAssociatedVaccineGroup()
    {
        return getAssociatedTargetSeries().getVaccineGroup();
    }

    public String getAssociatedSeriesName()
    {
        return getAssociatedTargetSeries().getSeriesName();
    }

    public void setIsPrimarySeriesShot(final boolean yesno)
    {
        isPrimarySeriesShot = yesno;
    }

    /**
     * Returns whether this is a valid dose or not.
     *
     * @return true of the DoseStatus is either DoseStatus.VALID, false if not
     */
    public boolean getIsValid()
    {
        return isValid;
    }

    /**
     * This method is private; shot validity should be set via setStatus()
     */
    private void setIsValid(final boolean isValid)
    {
        this.isValid = isValid;
    }

    public void setIsShotIgnored(final boolean ignoredForCompletionOfSeries)
    {
        this.isShotIgnored = ignoredForCompletionOfSeries;
    }

    public boolean hasBeenEvaluated()
    {
        return hasBeenEvaluated;
    }

    public void setStatus(final DoseStatus status)
    {
        this.status = status;
        setHasBeenEvaluated(false);

        if (status != null)
        {
            if (status == DoseStatus.ACCEPTED || status == DoseStatus.INVALID || status == DoseStatus.VALID)
                setHasBeenEvaluated(true);
            if (status == DoseStatus.VALID)
                setIsValid(true);
            else
            {
                if (hasBeenEvaluated())
                    removeAllSupplementalTextsForValidShot();
                setIsValid(false);
            }
            if (status == DoseStatus.EVALUATION_NOT_STARTED || status == DoseStatus.PRIMARY_SHOT_DETERMINATION_IN_PROCESS)
            {
                setPostEvaluationCheckCompleted(false);
                setPreEvaluationCheckCompleted(false);
                removeAllEvaluationReasonsFromAllReasonSets();
            }
        }
    }

    /**
     * Set the administration date of the shot. If the administration date is null, throw an IllegalArgumentException
     *
     * @param administrationDate The administration date of the shot
     */
    public void setAdministrationDate(final Date administrationDate)
    {
        final String _METHODNAME = "setAdministrationDate(): ";
        if (administrationDate == null)
        {
            final String errStr = "Administration Date not supplied";
            log.warn(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        this.administrationDate = administrationDate;
    }

    public Collection<String> getValidReasons()
    {
        return validReasons;
    }

    /**
     * Adds the valid reason, if not already present
     *
     * @param reason null reasons are permitted
     */
    public void addValidReason(final String reason)
    {
        if (reason != null)
            validReasons.add(reason);
    }

    public Collection<String> getAcceptedReasons()
    {
        return acceptedReasons;
    }

    /**
     * Adds the accepted reason, if not already present
     */
    public void addAcceptedReason(final String reason)
    {
        if (reason != null)
            acceptedReasons.add(reason);
    }

    public Collection<String> getInvalidReasons()
    {
        return invalidReasons;
    }

    /**
     * Adds the invalid reason, if not already present
     */
    public void addInvalidReason(final String reason)
    {
        if (reason != null)
            invalidReasons.add(reason);
    }

    public Collection<String> getNotEvaluatedReasons()
    {
        return notEvaluatedReasons;
    }

    /**
     * Adds the invalid reason, if not already present
     */
    public void addNotEvalatedReason(final String reason)
    {
        if (reason != null)
            notEvaluatedReasons.add(reason);
    }

    /**
     * Add the supplemental text, if not already present
     */
    public void addSupplementalTextForValidShot(final String supplementalTextForValidShots)
    {
        addValidReason(BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName());
        if (supplementalTextForValidShots != null)
            supplementalTextsForValidShots.add(supplementalTextForValidShots);
    }

    public Collection<String> getSupplementalTextsForValidShot()
    {
        return supplementalTextsForValidShots;
    }

    public void addSupplementalTextForAcceptedShot(final String supplementalTextForAcceptedShots)
    {
        addAcceptedReason(BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName());
        if (supplementalTextForAcceptedShots != null)
            supplementalTextsForAcceptedShots.add(supplementalTextForAcceptedShots);
    }

    public Collection<String> getSupplementalTextsForAcceptedShot()
    {
        return supplementalTextsForAcceptedShots;
    }

    public void addSupplementalTextForInvalidShot(final String supplementalTextForInvalidShots)
    {
        addInvalidReason(BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName());
        if (supplementalTextForInvalidShots != null)
            supplementalTextsForInvalidShots.add(supplementalTextForInvalidShots);
    }

    public void captureEvaluationContext(final TargetSeries evaluatedSeries, final int vgSeriesCount)
    {
        this.evaluatedSeriesName = evaluatedSeries.getSeriesName();
        this.seriesDisplaySelectionType = vgSeriesCount > 1
                                          ? SeriesDisplaySelectionType.SERIES_DISPLAY_BEST_GUESS
                                          : SeriesDisplaySelectionType.SERIES_DISPLAY_UNAMBIGUOUS;
        this.evaluatedSeriesSeason = evaluatedSeries.getTargetSeason();
    }

    public Collection<String> getSupplementalTextsForInvalidShot()
    {
        return supplementalTextsForInvalidShots;
    }

    @Override
    public String toString()
    {
        final StringBuilder s = new StringBuilder(
                "TargetDose [uniqueId=%s, doseId=%s, administeredShotNumber=%d; doseNumber=%d, doseNumberCount=%d, vaccine=%s, isPrimarySeriesShot=%s; vaccineComponent=%s, administrationDate=%s, status=%s; isValid=%s; preEvaluationCheck=%s; isLiveVirus: %s; isCombinationVaccine: %s; componentIsLiveVirus: %s; isAdjuvant: %s; componentIsAdjuvant: %s; isDuplicateShotSameDayCheckCompleted: %s, isUnspecifiedFormulation(): %s; hasBeenEvaluated: %s".formatted(
                        uniqueId, doseId, administeredShotNumberInSeries, doseNumberInSeries, doseNumberCount,
                        administeredVaccine.getCdsConceptName(), isPrimarySeriesShot(), vaccineComponent.getCdsConceptName(),
                        administrationDate, status, isValid, preEvaluationCheckCompleted,
                        this.getAdministeredVaccine().isLiveVirusVaccine(), this.getAdministeredVaccine().isCombinationVaccine(),
                        this.getVaccineComponent().isLiveVirusVaccine(), this.getAdministeredVaccine().isSelectAdjuvantProduct(),
                        this.getVaccineComponent().isSelectAdjuvantProduct(), this.isDuplicateShotSameDayCheckCompleted(),
                        this.getVaccineComponent().isUnspecifiedFormulation(), hasBeenEvaluated()));

        int i = 0;
        for (final String reason : validReasons)
        {
            if (i++ == 0)
                s.append(", validReasons={\"").append(reason).append("\"");
            else
                s.append("\"").append(reason).append("\"");
        }

        if (i > 0)
            s.append("}");

        i = 0;
        for (final String reason : acceptedReasons)
        {
            if (i++ == 0)
                s.append(", acceptedReasons={\"").append(reason).append("\"");
            else
                s.append("\"").append(reason).append("\"");
        }

        if (i > 0)
            s.append("}");

        i = 0;
        for (final String reason : invalidReasons)
        {
            if (i++ == 0)
                s.append(", invalidReasons={\"").append(reason).append("\"");
            else
                s.append("\"").append(reason).append("\"");
        }

        if (i > 0)
            s.append("}");

        i = 0;
        for (final String supplementalText : supplementalTextsForValidShots)
        {
            if (i++ == 0)
                s.append(", supplementalTexts={\"").append(supplementalText).append("\"");
            else
                s.append("\"").append(supplementalText).append("\"");
        }

        if (i > 0)
            s.append("}");

        i = 0;
        for (final String reason : notEvaluatedReasons)
        {
            if (i++ == 0)
                s.append(", notEvaluatedReasons={\"").append(reason).append("\"");
            else
                s.append("\"").append(reason).append("\"");
        }

        if (i > 0)
            s.append("}");

        s.append("]");

        return s.toString();
    }
}
