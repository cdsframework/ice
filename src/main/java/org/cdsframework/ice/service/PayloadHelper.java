package org.cdsframework.ice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationReason;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.drools.model.Drools;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.EvalTime;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.internal.datatypes.BL;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.INT;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record PayloadHelper(Schedule backingSchedule)
{
    /**
     * Return local ICE3 recommendation code for the OpenCDS reason code value.
     *
     * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
     */
    public static CD getLocalCodeForRecommendationReason(final String pReasonCode, final Schedule s)
    {
        final String _METHODNAME = "getLocalCodeForRecommendationReason(): ";

        if (pReasonCode == null || s == null)
        {
            log.warn(_METHODNAME + "no reason code or schedule supplied; returning null");
            return null;
        }

        final LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(pReasonCode);
        if (sv == null)
        {
            log.debug(_METHODNAME + "reason code supplied is not one that is defined in the supporting data; returning null");
            return null;
        }

        final CD lListItemCD = sv.getCdsListItemCD();
        final CD lLocalCDDup = new CD();
        lLocalCDDup.setCodeSystem(lListItemCD.getCodeSystem());
        lLocalCDDup.setCode(lListItemCD.getCode());
        lLocalCDDup.setDisplayName(lListItemCD.getDisplayName());
        lLocalCDDup.setCodeSystemName(lListItemCD.getCodeSystemName());
        lLocalCDDup.setAny(lListItemCD.getAny());
        lLocalCDDup.setOriginalText(lListItemCD.getOriginalText());

        return lLocalCDDup;
    }
    // TODO: CDSOutput Template codes... Make configurable

    public static LocallyCodedCdsListItem getCdsListItemForReasonCode(final String pReasonCode, final Schedule s)
    {
        final String _METHODNAME = "getCdsListItemForReasonCode(): ";
        if (pReasonCode == null || s == null)
        {
            log.warn(_METHODNAME + "no concept code or schedule supplied; returning null");
            return null;
        }

        final LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration().getSupportedCdsLists().getCdsListItem(pReasonCode);
        if (sv == null)
        {
            log.debug(_METHODNAME + "reason code supplied is not one that is defined in the supporting data; returning null");
            return null;
        }

        return sv;
    }

    /**
     * Return local ICE3 code for the OpenCDS reason code value.
     *
     * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
     */
    public static CD getLocalCodeForEvaluationReason(final String pReasonCode, final Schedule s)
    {
        final String _METHODNAME = "getLocalCodeForEvaluationReason(): ";
        final LocallyCodedCdsListItem sv = getCdsListItemForReasonCode(pReasonCode, s);

        if (sv == null)
            return null;

        if ("EVALUATION_REASON_CONCEPT.UNSPECIFIED_REASON".equals(sv.getCdsListItemName()))
        {
            log.debug(_METHODNAME + "Unspecified reason for this this shot; no reason for this evaluated shot will be returned");
            return null;
        }

        return sv.getCdsListItemCD();
    }

    /**
     * Determine if the specified reason code is a supplemental text reason.
     *
     * @param pReasonCode The reason code (e.g., "EVALUATION_REASON_CONCEPT.COVID_INTERVAL_5M_BOOSTER")
     * @param s           The schedule containing supporting data
     * @return true if supplemental text, false otherwise
     */
    private static boolean isSupplementalTextReason(final String pReasonCode, final Schedule s)
    {
        final String _METHODNAME = "isSupplementalTextReason(): ";

        if (pReasonCode == null || s == null)
        {
            log.warn(_METHODNAME + "no reason code or schedule supplied; returning false");
            return false;
        }

        final LocallyCodedCdsListItem sv = getCdsListItemForReasonCode(pReasonCode, s);

        return Optional.ofNullable(sv).map(LocallyCodedCdsListItem::isSupplementalText).orElse(false);
    }

    /**
     * Retrieve the outbound CD with originalText for an evaluation supplemental text reason code.
     * Similar to how getLocalCodeForEvaluationConcept works for vaccine groups.
     *
     * @param pReasonCode The reason code (e.g., "EVALUATION_REASON_CONCEPT.COVID_INTERVAL_5M_BOOSTER")
     * @param s           The schedule containing supporting data
     * @return CD with code=SUPPLEMENTAL_TEXT and originalText set to the supplemental text message, or null if not found
     */
    private static List<CD> getOutboundCDForSupplementalTextReason(final String pReasonCode, final Schedule s)
    {
        final String _METHODNAME = "getOutboundCDForSupplementalTextReason(): ";

        if (pReasonCode == null || s == null)
        {
            log.warn(_METHODNAME + "no reason code or schedule supplied; returning null");
            return null;
        }

        final LocallyCodedCdsListItem sv = getCdsListItemForReasonCode(pReasonCode, s);

        if (sv == null)
            return null;

        // Get the outbound CD which contains the SUPPLEMENTAL_TEXT code and originalText
        final CD outboundCD = sv.getCdsListItemOutboundCD();
        if (outboundCD == null)
        {
            log.warn(_METHODNAME + "no outbound coding defined for reason code: {}", pReasonCode);
            return null;
        }

        // Verify this is actually a supplemental text reason
        if (!sv.isSupplementalText())
        {
            log.warn(_METHODNAME + "outbound coding for reason code {} does not have code=SUPPLEMENTAL_TEXT; found: {}",
                    pReasonCode, outboundCD.getCode());
            return null;
        }

        // Create a copy to avoid modifying the cached version
        final CD legacyResult = new CD();
        legacyResult.setCode(outboundCD.getCode());
        legacyResult.setDisplayName(outboundCD.getDisplayName());
        legacyResult.setCodeSystem(outboundCD.getCodeSystem());
        legacyResult.setCodeSystemName(outboundCD.getCodeSystemName());
        legacyResult.setOriginalText(outboundCD.getOriginalText());

        final CD newResult = new CD();
        newResult.setCode(sv.getCdsListItemKey());
        newResult.setDisplayName(outboundCD.getOriginalText());
        newResult.setCodeSystem(outboundCD.getCodeSystem());
        newResult.setCodeSystemName(outboundCD.getCodeSystemName());

        return (switch (s.getSupplementalTextMode())
        {
            case LEGACY -> List.of(legacyResult);
            case NEW -> List.of(newResult);
            case BOTH -> List.of(legacyResult, newResult);
        });
    }

    /**
     * Return the local ICE3 code value for the DoseStatus.
     *
     * @return local ICE3 code value, null if DoseStatus is null, "" if local code value for DoseStatus is not found
     */
    public static CD getLocalCodeForEvaluationStatus(final DoseStatus pDS, final Schedule s)
    {
        final String _METHODNAME = "getLocalCodeForEvaluationStatus(): ";
        if (pDS == null || s == null)
            return null;

        final DoseStatus lDoseStatusToReturn = switch (pDS)
        {
            case VALID, ACCEPTED, INVALID, NOT_EVALUATED -> pDS;
            default -> DoseStatus.ACCEPTED;
        };

        final LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration()
                .getSupportedCdsLists()
                .getCdsListItem(lDoseStatusToReturn.getCdsListItemName());
        if (sv == null)
        {
            log.warn(_METHODNAME + "status code supplied is not one that is defined in the supporting data; returning null");
            return null;
        }

        return sv.getCdsListItemCD();
    }

    /**
     * Return local ICE3 code value for Recommendation
     *
     * @return null if the provided value is null; local ICE3 CD code value; CD with no code value set if the provided recommendation status is not either
     * RecommendationStatus.RECOMMENDED, RecommendationStatus.RECOMMENDED_IN_FUTURE, RecommendationStatus.CONDITIONALLY_RECOMMENDED,
     * or RecommendationStatus.NOT_RECOMMENDED
     */
    public static CD getLocalCodeForRecommendationStatus(final RecommendationStatus recStatus, final Schedule s)
    {
        final String _METHODNAME = "getLocalCodeForRecommendationStatus(): ";
        if (recStatus == null || s == null)
            return null;

        final RecommendationStatus lRecStatusToReturn = switch (recStatus)
        {
            case RECOMMENDED, CONDITIONALLY_RECOMMENDED, RECOMMENDED_IN_FUTURE, NOT_RECOMMENDED, RECOMMENDATION_NOT_AVAILABLE ->
                    recStatus;
            default -> RecommendationStatus.RECOMMENDED;
        };

        final LocallyCodedCdsListItem sv = s.getICESupportingDataConfiguration()
                .getSupportedCdsLists()
                .getCdsListItem(lRecStatusToReturn.getCdsListItemName());
        if (sv == null)
        {
            log.warn(_METHODNAME + "status code supplied is not one that is defined in the supporting data; returning null");
            return null;
        }

        return sv.getCdsListItemCD();
    }

    public PayloadHelper
    {
        if (backingSchedule == null || !backingSchedule.isScheduleInitialized())
        {
            final String lExStr = "Schedule has not been provided or has not been initialized; cannot continue";
            log.error("PayloadHelper(): " + lExStr);
            throw new IllegalArgumentException(lExStr);
        }
    }

    public void outputNestedImmEvaluationResult(final Drools k, final Map<String, Object> pNamedObjects, final EvalTime evalTime,
            final String focalPersonId, final String cdsSource, final SubstanceAdministrationEvent sae, final String vg,
            final TargetDose d, final boolean outputSupplementalText, final boolean outputDoseCountInsteadOfDoseNumberInSeries)
    {
        outputNestedImmEvaluationResult(k, pNamedObjects, evalTime, focalPersonId, cdsSource, sae, vg, d, outputSupplementalText,
                outputDoseCountInsteadOfDoseNumberInSeries, -1);
    }

    public void outputNestedImmEvaluationResult(final Drools k, final Map<String, Object> pNamedObjects, final EvalTime evalTime,
            final String focalPersonId, final String cdsSource, final SubstanceAdministrationEvent sae, final String vg,
            final TargetDose d, final boolean outputSupplementalText, final boolean outputDoseCountInsteadOfDoseNumberInSeries,
            final int doseNumberCountToOutput)
    {
        final String _METHODNAME = "outputNestedImmEvaluationResult: ";
        if (k == null || pNamedObjects == null || evalTime == null || sae == null || d == null)
        {
            final String str =
                    "Caller supplied either NULL KnowledgeHelper, NamedObject HashMap, evalTime, SubstanceAdministrationEvent or TargetDose parameter";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        if (log.isDebugEnabled())
            log.debug("focalPersonId {}, sae: {}, VG: {}, Dose unique ID: {}, Dose ID {}, Dose all: {}", focalPersonId, sae.getId(),
                    vg, d.getUniqueId(), d.getDoseId(), d);

        final String conceptTargetId = sae.getId();

        // Embedded SubstanceAdministrationEvent
        final SubstanceAdministrationEvent lSAE = new SubstanceAdministrationEvent();
        final String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
        lSAE.setId(uniqueSarIdValue);
        final String[] subsAdmEvtTemplateArr = { "2.16.840.1.113883.3.795.11.9.1.1" };
        lSAE.setTemplateId(subsAdmEvtTemplateArr);
        lSAE.setEvaluatedPersonId(focalPersonId);
        lSAE.setSubjectIsFocalPerson(true);

        // Record Substance AdministrationEvent CDS System Data Source
        if (!ObjectUtils.isEmpty(cdsSource))
        {
            final CD cdsDataSource = new CD();
            cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
            cdsDataSource.setCodeSystemName("org.cdsframework source");
            cdsDataSource.setCode(cdsSource);
            lSAE.setDataSourceType(cdsDataSource);
        }

        // Substance Proposal General Purpose
        final CD subsAdmGeneralPurposeCD = new CD();
        subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
        subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
        subsAdmGeneralPurposeCD.setCode("384810002");
        subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
        lSAE.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);

        // Dose number information
        final INT lINTDoseNumber = new INT();
        if (outputDoseCountInsteadOfDoseNumberInSeries && doseNumberCountToOutput >= 1)
            lINTDoseNumber.setValue(doseNumberCountToOutput);
        else
            if (outputDoseCountInsteadOfDoseNumberInSeries && d.getDoseNumberInSeries() > d.getDoseNumberCount())
                lINTDoseNumber.setValue(d.getDoseNumberCount());
            else
                lINTDoseNumber.setValue(Math.min(d.getDoseNumberInSeries(), d.getDoseNumberCount()));
        lSAE.setDoseNumber(lINTDoseNumber);
        // Administration Time Interval
        lSAE.setAdministrationTimeInterval(sae.getAdministrationTimeInterval());
        // Validity flag
        final BL lSAEValidity = new BL();
        lSAEValidity.setValue(d.getIsValid());
        lSAE.setIsValid(lSAEValidity);
        // AdministrableSubstance
        final AdministrableSubstance lAS = new AdministrableSubstance();
        lAS.setId(ICELogicHelper.generateUniqueString());
        final CD asSubstanceCode = new CD();
        // Get the associated vaccine concept associated with the TargetDose
        final LocallyCodedCdsListItem lSVC = this.backingSchedule.getICESupportingDataConfiguration()
                .getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS,
                        d.getVaccineComponent().getCdsConcept());
        if (lSVC == null)
        {
            final String lErrStr = "LocallyCodedCdsListItem Vaccine not found for specified TargetDose; this should not occur";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }
        asSubstanceCode.setCodeSystem(lSVC.getCdsListCodeSystem());
        asSubstanceCode.setCode(lSVC.getCdsListItemKey());
        asSubstanceCode.setDisplayName(lSVC.getCdsListItemValue());
        lAS.setSubstanceCode(asSubstanceCode);
        lAS.setToBeReturned(true);
        lSAE.setSubstance(lAS);

        // This is a nested clinical statement
        lSAE.setClinicalStatementToBeRoot(false);
        lSAE.setToBeReturned(true);
        // k.insert(lSAE);
        k.insert(lSAE);
        pNamedObjects.put("lSAE" + uniqueSarIdValue, lSAE);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(conceptTargetId);
        rel.setTargetId(uniqueSarIdValue);
        final CD relCodeSR = new CD();
        relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSR.setCode("PERT");
        relCodeSR.setDisplayName("has pertinent information");
        rel.setTargetRelationshipToSource(relCodeSR);
        // k.insert(rel);
        k.insert(rel);
        pNamedObjects.put("rel" + uniqueSarIdValue, rel);

        //
        // Observation
        //
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        final ObservationResult childObs = new ObservationResult();
        final String[] obsTemplateArr = { "2.16.840.1.113883.3.795.11.6.1.1" };
        childObs.setTemplateId(obsTemplateArr);

        // Eval Time
        final IVLDate obsTime = new IVLDate();
        obsTime.setLow(evalTime.getEvalTimeValue());
        obsTime.setHigh(evalTime.getEvalTimeValue());
        childObs.setId(nestedIdValue);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setObservationEventTime(obsTime);
        childObs.setSubjectIsFocalPerson(true);

        // Observation Focus
        final CD localCD = getLocalCodeForEvaluationConcept(vg);
        childObs.setObservationFocus(localCD);

        // Observation Value
        final DoseStatus doseStatus = d.getStatus();
        final CD localObsCD = getLocalCodeForEvaluationStatus(doseStatus, this.backingSchedule);
        final ObservationValue childObsValue = new ObservationValue();
        childObsValue.setConcept(localObsCD);
        childObs.setObservationValue(childObsValue);

        // Observation interpretation
        if (doseStatus == DoseStatus.VALID || doseStatus == DoseStatus.INVALID || doseStatus == DoseStatus.ACCEPTED
                || doseStatus == DoseStatus.NOT_EVALUATED)
        {
            final List<CD> interpretations = new ArrayList<>();

            final Collection<String> lReasons = switch (doseStatus)
            {
                case VALID -> d.getValidReasons();
                case INVALID -> d.getInvalidReasons();
                case ACCEPTED -> d.getAcceptedReasons();
                case NOT_EVALUATED -> d.getNotEvaluatedReasons();
                default -> new ArrayList<>();
            };

            for (final String interp : lReasons)
            {
                if (interp == null)
                    continue;

                final boolean lSupplementalTextToOutput = isSupplementalTextReason(interp, this.backingSchedule);
                if (outputSupplementalText || !lSupplementalTextToOutput)
                {
                    if (lSupplementalTextToOutput)
                        interpretations.addAll(
                                Optional.ofNullable(getOutboundCDForSupplementalTextReason(interp, this.backingSchedule))
                                        .orElseGet(ArrayList::new));
                    else
                    {
                        final CD localCDInterp = getLocalCodeForEvaluationReason(interp, this.backingSchedule);

                        if (localCDInterp != null && !interpretations.contains(localCDInterp))
                            interpretations.add(localCDInterp);
                    }
                }
            }

            if (!interpretations.isEmpty())
                childObs.setInterpretation(interpretations);
        }

        // This is a nested clinical statement
        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);
        // k.insert(childObs);
        k.insert(childObs);
        pNamedObjects.put("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship relO = new ClinicalStatementRelationship();
        relO.setSourceId(uniqueSarIdValue);
        relO.setTargetId(nestedIdValue);
        final CD relCodeSO = new CD();
        relCodeSO.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSO.setCode("PERT");
        relCodeSO.setDisplayName("has pertinent information");
        rel.setTargetRelationshipToSource(relCodeSO);
        // k.insert(relO);
        k.insert(relO);
        pNamedObjects.put("rel" + nestedIdValue, relO);
    }

    public void outputNestedImmEvaluationNotSupported(final Drools k, final Map<String, Object> pNamedObjects,
            final EvalTime evalTime, final String focalPersonId, final String cdsSource, final SubstanceAdministrationEvent sae,
            final String vg)
    {
        final String _METHODNAME = "outputNestedImmEvaluationNotSupported: ";
        if (k == null || pNamedObjects == null || evalTime == null || sae == null || vg == null)
        {
            final String str =
                    "Caller supplied either NULL KnowledgeHelper, NamedObject HashMap, evalTime, or SubstanceAdministrationEvent parameter";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        if (log.isDebugEnabled())
            log.debug("focalPersonId {}, sae: {}, VG: {}", focalPersonId, sae.getId(), vg);

        final String conceptTargetId = sae.getId();

        // SubstanceAdministrationEvent
        final SubstanceAdministrationEvent lSAE = new SubstanceAdministrationEvent();
        final String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
        lSAE.setId(uniqueSarIdValue);
        lSAE.setTemplateId(new String[] { "2.16.840.1.113883.3.795.11.9.1.1" });
        lSAE.setEvaluatedPersonId(focalPersonId);
        lSAE.setSubjectIsFocalPerson(true);

        // Record Substance AdministrationEvent CDS System Data Source
        if (!ObjectUtils.isEmpty(cdsSource))
        {
            final CD cdsDataSource = new CD();
            cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
            cdsDataSource.setCodeSystemName("org.cdsframework source");
            cdsDataSource.setCode(cdsSource);
            lSAE.setDataSourceType(cdsDataSource);
        }

        // Substance Proposal General Purpose
        final CD subsAdmGeneralPurposeCD = new CD();
        subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
        subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
        subsAdmGeneralPurposeCD.setCode("384810002");
        subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
        lSAE.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);
        // Administration Time Interval
        lSAE.setAdministrationTimeInterval(sae.getAdministrationTimeInterval());
        // AdministrableSubstance sae
        // AdministrableSubstance (new)
        sae.getSubstance().setToBeReturned(true);
        lSAE.setSubstance(sae.getSubstance());

        // This is a nested clinical statement
        lSAE.setClinicalStatementToBeRoot(false);
        lSAE.setToBeReturned(true);
        // k.insert(lSAE);
        k.insert(lSAE);
        pNamedObjects.put("lSAE" + uniqueSarIdValue, lSAE);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(conceptTargetId);
        rel.setTargetId(uniqueSarIdValue);
        final CD relCodeSR = new CD();
        relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSR.setCode("PERT");
        relCodeSR.setDisplayName("has pertinent information");
        rel.setTargetRelationshipToSource(relCodeSR);
        // k.insert(rel);
        k.insert(rel);
        pNamedObjects.put("rel" + uniqueSarIdValue, rel);

        //
        // Observation
        //
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        final ObservationResult childObs = new ObservationResult();
        childObs.setTemplateId(new String[] { "2.16.840.1.113883.3.795.11.6.1.1" });

        // Eval Time
        final IVLDate obsTime = new IVLDate();
        obsTime.setLow(evalTime.getEvalTimeValue());
        obsTime.setHigh(evalTime.getEvalTimeValue());
        childObs.setId(nestedIdValue);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setObservationEventTime(obsTime);
        childObs.setSubjectIsFocalPerson(true);

        // Observation Focus
        final CD localCD = getLocalCodeForEvaluationConcept(vg);
        childObs.setObservationFocus(localCD);

        // Observation Value
        final DoseStatus doseStatus = DoseStatus.NOT_EVALUATED;
        final CD localObsCD = getLocalCodeForEvaluationStatus(doseStatus, this.backingSchedule);
        final ObservationValue childObsValue = new ObservationValue();
        childObsValue.setConcept(localObsCD);
        childObs.setObservationValue(childObsValue);

        // Observation interpretation
        final List<CD> interpretations = new ArrayList<>();
        interpretations.add(
                getLocalCodeForEvaluationReason(BaseDataEvaluationReason._VACCINE_NOT_SUPPORTED_REASON.getCdsListItemName(),
                        this.backingSchedule));  // "EVALUATION_REASON_CONCEPT.VACCINE_NOT_SUPPORTED"
        childObs.setInterpretation(interpretations);

        // This is a nested clinical statement
        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);
        // k.insert(childObs);
        k.insert(childObs);
        pNamedObjects.put("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship relO = new ClinicalStatementRelationship();
        relO.setSourceId(uniqueSarIdValue);
        relO.setTargetId(nestedIdValue);
        final CD relCodeSO = new CD();
        relCodeSO.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSO.setCode("PERT");
        relCodeSO.setDisplayName("has pertinent information");
        rel.setTargetRelationshipToSource(relCodeSO);
        // k.insert(relO);
        k.insert(relO);
        pNamedObjects.put("rel" + nestedIdValue, relO);
    }

    /**
     * This method creates a root SubstanceAdministrationProposal in the following format
     * <substanceAdministrationProposal>
     * <templateId root="2.16.840.1.113883.3.795.11.9.3.1"/>
     * <id root="47e4ad13-e11d-4ca5-ae63-93619baa3e92"/>
     * <substanceAdministrationGeneralPurpose displayName="Immunization/vaccination management (procedure)" codeSystemName="SNOMED CT" codeSystem="2.16.840.1.113883.6.5" code="384810002"/>
     * <substance>
     * <id root="051e605d-46a3-4f2b-8e4e-1901cf4eca9b"/>
     * <substanceCode displayName="TBD - vaccine group: 810 - substance code: 104" codeSystem="2.16.840.1.113883.12.292" code="810"/>
     * </substance>
     * <validAdministrationTimeInterval low="20101201"/>	<!-- earliest date a shot can be administered (only returned if option enabled) -->
     * <proposedAdministrationTimeInterval low="20111201" high="20121201"/>		<!-- recommended forecast date and latest recommended forecast date (latest only if option enabled; otherwise it is set to same value as forecast date) -->
     * <relatedClinicalStatement>
     * <targetRelationshipToSource codeSystem="2.16.840.1.113883.5.1002" code="RSON" displayName="has reason"/>
     * <observationResult>
     * <templateId root="2.16.840.1.113883.10.20.1.31"/>
     * <id root="1815afc5-3b70-4b7b-bc64-4af8cb424b5a"/>
     * <observationFocus code="PROPOSAL (HEP A)" codeSystem="2.16.840.1.113883.3.795.12.100.4"/>
     * <observationValue>
     * <concept displayName="TBD" codeSystem="2.16.840.1.113883.3.795.12.100.5" code="RECOMMENDED"/>
     * </observationValue>
     * <interpretation displayName="TBD" codeSystem="2.16.840.1.113883.3.795.12.100.6" code="DUE_NOW"/>
     * </observationResult>
     * </relatedClinicalStatement>
     * </substanceAdministrationProposal>
     */
    public SubstanceAdministrationProposal outputRootImmRecommendationSubstanceAdministrationProposal(final Drools drools,
            final Map<String, Object> pNamedObjects, final String focalPersonId, final String cdsSource, final TargetSeries ts,
            final boolean outputEarliestOverdue, final boolean outputSupplementalText)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "outputRootImmRecommendationSubstanceAdministrationProposal: ";

        if (ts == null || pNamedObjects == null || drools == null || ts.getTargetSeriesIdentifier() == null)
        {
            final String lErrStr =
                    "Error outputting SubstanceAdministrationProposal: one or more method parameters not initialized";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final SubstanceAdministrationProposal sap = new SubstanceAdministrationProposal();
        // String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
        final String uniqueSarIdValue = ts.getTargetSeriesIdentifier();
        sap.setId(uniqueSarIdValue);
        final String[] subsAdmPropTemplateArr = { "2.16.840.1.113883.3.795.11.9.3.1" };
        sap.setTemplateId(subsAdmPropTemplateArr);
        sap.setEvaluatedPersonId(focalPersonId);
        sap.setSubjectIsFocalPerson(true);

        // Substance Administration Proposal CDS System Data Source
        if (!ObjectUtils.isEmpty(cdsSource))
        {
            final CD cdsDataSource = new CD();
            cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
            cdsDataSource.setCodeSystemName("org.cdsframework source");
            cdsDataSource.setCode(cdsSource);
            sap.setDataSourceType(cdsDataSource);
        }

        // Substance Proposal General Purpose
        final CD subsAdmGeneralPurposeCD = new CD();
        subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
        subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
        subsAdmGeneralPurposeCD.setCode("384810002");
        subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
        sap.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);

        // Set the Earliest valid date, recommendation date and/or latest recommendation date
        final Date finalEarliestDate = ts.getFinalEarliestDate();
        final Date finalRecommendationDate = ts.getFinalRecommendationDate();
        if (!outputEarliestOverdue)
        {
            // Only the recommended forecast date should be set
            if (finalRecommendationDate != null)
            {
                final IVLDate obsTime = new IVLDate();
                obsTime.setLow(finalRecommendationDate);
                obsTime.setHigh(finalRecommendationDate);
                sap.setProposedAdministrationTimeInterval(obsTime);
            }
        }
        else
        {
            // The earliest, recommended and latest recommended should be set
            final Date finalLatestRecommendationDate = ts.getFinalOverdueDate();
            // We do not support returning the "latest" possible date separately in payload, as of now
            if (finalRecommendationDate != null || finalLatestRecommendationDate != null)
            {
                final IVLDate obsTime = new IVLDate();
                if (finalRecommendationDate != null)
                    obsTime.setLow(finalRecommendationDate);
                else
                {
                    // (This should not happen; however, since an earliest recommendation date, will just use the latest date. Log that this occurred.
                    log.warn(_METHODNAME
                            + "No earliest recommendation date was calculated but a latest recommendation date was calculated! This should not happen");
                }
                if (finalLatestRecommendationDate != null)
                    obsTime.setHigh(finalLatestRecommendationDate);
                sap.setProposedAdministrationTimeInterval(obsTime);
            }
            if (finalEarliestDate != null)
            {
                final IVLDate obsTime = new IVLDate();
                obsTime.setLow(finalEarliestDate);
                sap.setValidAdministrationTimeInterval(obsTime);
            }
        }
        // Set the AdministrableSubstance - may be a vaccine or a vaccine group
        final CD vaccGroupCode = getLocalCodeConceptForRecommendationConcept(ts, true);
        final AdministrableSubstance substance = new AdministrableSubstance();
        substance.setId(ICELogicHelper.generateUniqueString());
        substance.setSubstanceCode(vaccGroupCode);
        sap.setSubstance(substance);

        // Set as a root clinical statement
        sap.setClinicalStatementToBeRoot(true);
        sap.setToBeReturned(true);
        drools.insert(sap);
        pNamedObjects.put("sap" + uniqueSarIdValue, sap);

        // Now create the nested observation result
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        // Observation
        final ObservationResult childObs = new ObservationResult();
        childObs.setId(nestedIdValue);
        final String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
        sap.setTemplateId(observationResultTemplateArr);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setSubjectIsFocalPerson(true);

        // Set the Observation Focus - always the vaccine group
        final CD localCD = getLocalCodeConceptForRecommendationConcept(ts, false);
        childObs.setObservationFocus(localCD);

        // Observation Value
        final CD localObsCD = getLocalCodeForRecommendationStatus(ts.getRecommendationStatus(), this.backingSchedule);
        if (localObsCD == null || localObsCD.getCode() == null)
        {
            final String errStr = "Invalid Recommendation Supplied for output";
            log.warn(_METHODNAME + errStr + "; CD: {}", localObsCD);
            throw new IllegalArgumentException(errStr);
        }

        final ObservationValue childObsValue = new ObservationValue();
        childObsValue.setConcept(localObsCD);
        childObs.setObservationValue(childObsValue);

        // Observation interpretation
        final RecommendationStatus rs = ts.getRecommendationStatus();
        final List<Recommendation> recs = ts.getFinalRecommendations();
        final List<CD> interpretations = new ArrayList<>();
        if (recs != null)
        {
            for (final Recommendation rec : recs)
            {
                final String recommendationReasonCode = rec.getRecommendationReason();
                if (recommendationReasonCode != null && (rec.getRecommendationStatus() == RecommendationStatus.FORECASTING_COMPLETE
                        || rec.getRecommendationStatus() == rs))
                {
                    final boolean lSupplementalTextFound = isSupplementalTextReason(recommendationReasonCode, this.backingSchedule);
                    if (outputSupplementalText || !lSupplementalTextFound)
                    {
                        if (lSupplementalTextFound)
                        {
                            interpretations.addAll(Optional.ofNullable(
                                            getOutboundCDForSupplementalTextReason(recommendationReasonCode, this.backingSchedule))
                                    .orElseGet(ArrayList::new));
                        }
                        else
                        {
                            final CD localCDInterp =
                                    getLocalCodeForRecommendationReason(recommendationReasonCode, this.backingSchedule);
                            if (localCDInterp != null && !interpretations.contains(localCDInterp))
                                interpretations.add(localCDInterp);
                        }
                    }
                }
            }
            if (!interpretations.isEmpty())
                childObs.setInterpretation(interpretations);
        }

        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);
        drools.insert(childObs);
        pNamedObjects.put("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(uniqueSarIdValue);
        rel.setTargetId(nestedIdValue);
        final CD relCodeSR = new CD();
        relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSR.setCode("RSON");
        relCodeSR.setDisplayName("has pertaining reason");
        rel.setTargetRelationshipToSource(relCodeSR);
        drools.insert(rel);
        pNamedObjects.put("rel" + nestedIdValue, rel);

        return sap;
    }

    public SubstanceAdministrationProposal outputOtherImmRecommendationSubstanceAdministrationProposal(final Drools drools,
            final Map<String, Object> pNamedObjects, final String focalPersonId, final String cdsSource)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "outputRootImmRecommendationSubstanceAdministrationProposal: ";

        if (pNamedObjects == null || drools == null)
        {
            final String lErrStr =
                    "Error outputting SubstanceAdministrationProposal: one or more method parameters not initialized";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final SubstanceAdministrationProposal sap = new SubstanceAdministrationProposal();

        final String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
        sap.setId(uniqueSarIdValue);
        final String[] subsAdmPropTemplateArr = { "2.16.840.1.113883.3.795.11.9.3.1" };
        sap.setTemplateId(subsAdmPropTemplateArr);
        sap.setEvaluatedPersonId(focalPersonId);
        sap.setSubjectIsFocalPerson(true);

        // Substance Administration Proposal CDS System Data Source
        if (!ObjectUtils.isEmpty(cdsSource))
        {
            final CD cdsDataSource = new CD();
            cdsDataSource.setCodeSystem("2.16.840.1.113883.3.795.5.4.12.1.2");
            cdsDataSource.setCodeSystemName("CAT CDS Source");
            cdsDataSource.setCode(cdsSource);
            cdsDataSource.setDisplayName("ICE Version");
            sap.setDataSourceType(cdsDataSource);
        }

        // Substance Proposal General Purpose
        final CD subsAdmGeneralPurposeCD = new CD();
        subsAdmGeneralPurposeCD.setCodeSystem("2.16.840.1.113883.6.5");
        subsAdmGeneralPurposeCD.setCodeSystemName("SNOMED CT");
        subsAdmGeneralPurposeCD.setCode("384810002");
        subsAdmGeneralPurposeCD.setDisplayName("Immunization/vaccination management (procedure)");
        sap.setSubstanceAdministrationGeneralPurpose(subsAdmGeneralPurposeCD);

        // Set the AdministrableSubstance - may be a vaccine or a vaccine group
        final CD localObservationFocusCD = getLocalCodeForRecommendationReason("VACCINE_GROUP_CONCEPT.OTHER", this.backingSchedule);
        final AdministrableSubstance substance = new AdministrableSubstance();
        substance.setId(ICELogicHelper.generateUniqueString());
        substance.setSubstanceCode(localObservationFocusCD);
        sap.setSubstance(substance);

        // Set as a root clinical statement
        sap.setClinicalStatementToBeRoot(true);
        sap.setToBeReturned(true);
        drools.insert(sap);
        pNamedObjects.put("sap" + uniqueSarIdValue, sap);

        // Now create the nested observation result
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        // Observation
        final ObservationResult childObs = new ObservationResult();
        childObs.setId(nestedIdValue);
        final String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
        sap.setTemplateId(observationResultTemplateArr);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setSubjectIsFocalPerson(true);

        // Set the Observation Focus - always the vaccine group
        childObs.setObservationFocus(localObservationFocusCD);

        // Observation Value
        final ObservationValue childObsValue = new ObservationValue();
        final CD recommendationCode =
                getLocalCodeForRecommendationStatus(RecommendationStatus.RECOMMENDATION_NOT_AVAILABLE, this.backingSchedule);
        childObsValue.setConcept(recommendationCode);
        childObs.setObservationValue(childObsValue);

        // Observation interpretation
        final List<CD> interpretations = new ArrayList<>();
        interpretations.add(
                getLocalCodeForRecommendationReason(BaseDataRecommendationReason._RECOMMENDATION_NOT_SUPPORTED.getCdsListItemName(),
                        this.backingSchedule));
        childObs.setInterpretation(interpretations);
        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);
        drools.insert(childObs);
        pNamedObjects.put("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(uniqueSarIdValue);
        rel.setTargetId(nestedIdValue);
        final CD relCodeSR = new CD();
        relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSR.setCode("RSON");
        relCodeSR.setDisplayName("has pertaining reason");
        rel.setTargetRelationshipToSource(relCodeSR);
        drools.insert(rel);
        pNamedObjects.put("rel" + nestedIdValue, rel);

        return sap;
    }

    public void outputEmbeddedDosesRemainingInSubstanceAdministrationProposal(final Drools drools,
            final Map<String, Object> pNamedObjects, final String focalPersonId, final String pDosesRemaining,
            final SubstanceAdministrationProposal pSAP) throws IllegalArgumentException
    {
        final String _METHODNAME = "outputEmbeddedDosesRemainingInSubstanceAdministrationProposal: ";
        if (drools == null || pNamedObjects == null || pSAP == null || pDosesRemaining == null)
        {
            final String lErrStr = "Unable to output doses remaining: one or more parameters not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        // Now create the nested observation result
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        // Observation
        final ObservationResult childObs = new ObservationResult();
        childObs.setId(nestedIdValue);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setSubjectIsFocalPerson(true);

        // Set the Observation Focus - always the vaccine group
        final CD localCD = new CD();
        localCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.10");
        localCD.setCode("NUMBER_OF_DOSES_REMAINING");
        localCD.setDisplayName("Doses Remaining");
        localCD.setOriginalText("Number of doses remaining in the series, as of the evaluation date");
        childObs.setObservationFocus(localCD);

        // Observation Value
        final ObservationValue childObsValue = new ObservationValue();
        childObsValue.setText(pDosesRemaining);
        childObs.setObservationValue(childObsValue);
        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);
        drools.insert(childObs);
        pNamedObjects.put("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(pSAP.getId());
        rel.setTargetId(nestedIdValue);
        final CD relCodeSR = new CD();
        relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSR.setCode("RSON");
        relCodeSR.setDisplayName("has pertaining reason");
        rel.setTargetRelationshipToSource(relCodeSR);
        drools.insert(rel);
        pNamedObjects.put("rel" + nestedIdValue, rel);
    }

    private ObservationResult generateObservationResult(final String pIdToAssignToObservationResult, final String pFocalPersonId,
            final boolean pSubjectIsFocalPerson)
    {
        final ObservationResult lObservationResult = new ObservationResult();
        lObservationResult.setId(pIdToAssignToObservationResult);
        lObservationResult.setEvaluatedPersonId(pFocalPersonId);
        lObservationResult.setSubjectIsFocalPerson(pSubjectIsFocalPerson);

        return lObservationResult;
    }

    public void outputSeriesDisplaySelectionsAndDosesRemainingInEmbeddedSubstanceAdministrationProposals(final Drools drools,
            final Map<String, Object> pNamedObjects, final String focalPersonId, final List<SeriesDisplaySelection> pSeriesDisplays,
            final SubstanceAdministrationProposal pSAP) throws IllegalArgumentException
    {
        final String _METHODNAME = "outputEmbeddedDosesRemainingInSubstanceAdministrationProposal: ";
        if (drools == null || pNamedObjects == null || pSAP == null || pSeriesDisplays == null)
        {
            final String lErrStr = "Unable to output doses remaining: one or more parameters not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (pSeriesDisplays.isEmpty())
            return;

        // START - Top Level Observation for the Series Display

        final ObservationResult lCollectionOfSeriesDisplaysObs =
                generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);

        // Set the Observation Focus for the Series Display
        final CD localCD = new CD();    // TODO: incorporate into and pull from supporting data
        localCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.500");
        localCD.setCode("SERIES_DISPLAY_OPTIONS");
        localCD.setDisplayName("Series Options for Display");
        lCollectionOfSeriesDisplaysObs.setObservationFocus(localCD);

        // Loop through all the scored series selections for display
        for (final SeriesDisplaySelection lSDS : pSeriesDisplays)
        {
            // Create an ObservationResult containing the Series Display Selection and its series display type
            // Set the ObservationFocus to the Series Display Type, and its ObservationValue to the coded value representing the Series itself
            final ObservationResult lSeriesToDisplayObs =
                    generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);
            final CD lSeriesToDisplayFocusCD = new CD();    // TODO: incorporate into and pull from supporting data
            lSeriesToDisplayFocusCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.501");
            lSeriesToDisplayFocusCD.setCode(lSDS.getSeriesDisplaySelectionType().toString());
            lSeriesToDisplayObs.setObservationFocus(lSeriesToDisplayFocusCD);
            final ObservationValue lSeriesToDisplayObsValue = new ObservationValue();
            final CD lSeriesCD = new CD();    // TODO: incorporate into and pull from supporting data
            lSeriesCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.10");
            lSeriesCD.setCode(lSDS.getSeriesName());
            lSeriesToDisplayObsValue.setConcept(lSeriesCD);
            lSeriesToDisplayObs.setObservationValue(lSeriesToDisplayObsValue);

            // Doses Remaining - Embed the number of doses remaining for the series within the series display type, with the observation value as text representing the number of doses remaining
            final ObservationResult lDosesRemainingObs =
                    generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);
            final CD lDosesRemainingCD = new CD();    // TODO: incorporate into and pull from supporting data
            lDosesRemainingCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.500");
            lDosesRemainingCD.setCode("NUMBER_OF_DOSES_REMAINING");
            lDosesRemainingCD.setDisplayName("Doses Remaining");
            lDosesRemainingCD.setOriginalText("Number of doses remaining in the series, as of the evaluation date");
            lDosesRemainingObs.setObservationFocus(lDosesRemainingCD);
            final ObservationValue lDosesRemainingObsValue = new ObservationValue();
            lDosesRemainingObsValue.setText(lSDS.getNumberOfDosesRemaining());
            lDosesRemainingObs.setObservationValue(lDosesRemainingObsValue);
            // Insert the doses remaining structure on the fact and named objects lists
            drools.insert(lDosesRemainingObs);
            pNamedObjects.put("childObs" + lDosesRemainingObs.getId(), lDosesRemainingObs);

            // Relate the doses remaining observation to the series display selection, and place on fact and named objects lists
            final ClinicalStatementRelationship relDR = new ClinicalStatementRelationship();
            relDR.setSourceId(lSeriesToDisplayObs.getId());
            relDR.setTargetId(lDosesRemainingObs.getId());
            final CD relCodeDR = new CD();
            relCodeDR.setCodeSystem("2.16.840.1.113883.5.1002");
            relCodeDR.setCode("RSON");
            relCodeDR.setDisplayName("has pertaining reason");
            relDR.setTargetRelationshipToSource(relCodeDR);
            drools.insert(relDR);
            pNamedObjects.put("rel" + lDosesRemainingObs.getId(), relDR);
            // END Doses Remaining - Embed the number of doses remaining for the series...

            // Place the display series and designated display score on the named objects list
            lSeriesToDisplayObs.setClinicalStatementToBeRoot(false);
            lSeriesToDisplayObs.setToBeReturned(true);
            drools.insert(lSeriesToDisplayObs);
            pNamedObjects.put("childObs" + lSeriesToDisplayObs.getId(), lSeriesToDisplayObs);

            // Relate the display series with the collection of series
            final ClinicalStatementRelationship srel = new ClinicalStatementRelationship();
            srel.setSourceId(lCollectionOfSeriesDisplaysObs.getId());
            srel.setTargetId(lSeriesToDisplayObs.getId());
            final CD srelCodeSR = new CD();
            srelCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
            srelCodeSR.setCode("RSON");
            srelCodeSR.setDisplayName("has pertaining reason");
            srel.setTargetRelationshipToSource(srelCodeSR);
            drools.insert(srel);
            pNamedObjects.put("rel" + lSeriesToDisplayObs.getId(), srel);
        }

        // Place Collection of Series Displays on the named objects list
        lCollectionOfSeriesDisplaysObs.setClinicalStatementToBeRoot(false);
        lCollectionOfSeriesDisplaysObs.setToBeReturned(true);
        drools.insert(lCollectionOfSeriesDisplaysObs);
        pNamedObjects.put("childObs" + lCollectionOfSeriesDisplaysObs.getId(), lCollectionOfSeriesDisplaysObs);

        // Finally, relate the top-level ObservationResult collection to the SubstanceAdministrationProposal
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(pSAP.getId());
        rel.setTargetId(lCollectionOfSeriesDisplaysObs.getId());
        final CD relCodeSR = new CD();
        relCodeSR.setCodeSystem("2.16.840.1.113883.5.1002");
        relCodeSR.setCode("RSON");
        relCodeSR.setDisplayName("has pertaining reason");
        rel.setTargetRelationshipToSource(relCodeSR);
        drools.insert(rel);
        pNamedObjects.put("rel" + lCollectionOfSeriesDisplaysObs.getId(), rel);
    }

    /**
     * Return local ICE3 Observation Evaluation Focus code for the Vaccine Group
     *
     * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
     */
    private CD getLocalCodeForEvaluationConcept(final String pVG)
    {
        final String _METHODNAME = "getLocalCodeForEvaluationConcept(): ";
        if (pVG == null)
        {
            log.warn(_METHODNAME + "VaccineGroup parameter supplied is null");
            return null;
        }

        final LocallyCodedCdsListItem lccli =
                this.backingSchedule.getICESupportingDataConfiguration().getSupportedVaccineGroups().getCdsListItem(pVG);
        if (lccli == null)
        {
            final String lErrStr = "No associated LocallyCodedCdsListItem for the supplied LocallyCodedVaccineGroupItem: " + pVG;
            log.warn(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }

        return lccli.getCdsListItemOutboundCD();
    }

    /**
     * Return local ICE3 Observation Recommendation Focus code for Vaccine or Vaccine Group
     *
     * @return local ICE3 code value, null if parameter supplied is null, null if local code value for supplied code is not found
     */
    private CD getLocalCodeConceptForRecommendationConcept(final TargetSeries pTS,
            final boolean atVaccineConceptLevelIfSpecificVaccineRecommended)
    {
        final String _METHODNAME = "getLocalCodeConceptForRecommendationConcept(): ";

        if (pTS == null)
        {
            log.error(_METHODNAME + "TargetSeries parameter supplied is null; cannot supply a recommendation focus");
            return null;
        }

        final Vaccine lRecommendedVaccine = pTS.getRecommendationVaccine();
        if (lRecommendedVaccine != null && atVaccineConceptLevelIfSpecificVaccineRecommended)
        {
            final LocallyCodedCdsListItem sv = this.backingSchedule.getICESupportingDataConfiguration()
                    .getSupportedCdsConcepts()
                    .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS,
                            lRecommendedVaccine.getCdsConcept());
            if (sv != null)
            {
                // A specific vaccine was recommended; indicate the vaccine recommended instead of the vaccine group
                return sv.getCdsListItemCD();
            }

            log.warn(_METHODNAME
                            + "A vaccine was recommended but no corresponding SupportedVaccineConcept exists; cannot recommend by vaccine; vaccine: {}",
                    lRecommendedVaccine);
        }

        // No recommended vaccine specifically; focus will be vaccine group
        final String lcvg = pTS.getVaccineGroup();
        if (lcvg == null)
        {
            log.warn(_METHODNAME + "VaccineGroup parameter supplied is null");
            return null;
        }

        final LocallyCodedCdsListItem lcvgi =
                this.backingSchedule.getICESupportingDataConfiguration().getSupportedVaccineGroups().getCdsListItem(lcvg);
        if (lcvgi == null)
        {
            final String lErrStr =
                    "LocallyCodedVaccineGroupItem not found for specified vaccine group in TargetSeries (this should not happen); vaccine group: "
                            + lcvg;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }

        return lcvgi.getCdsListItemOutboundCD();
    }
}
