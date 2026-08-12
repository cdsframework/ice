package org.cdsframework.ice.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.ice.supportingdata.BaseDataEvaluationReason;
import org.cdsframework.ice.supportingdata.BaseDataRecommendationReason;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.supportingdata.LocallyCodedVaccineGroupItem;
import org.cdsframework.ice.supportingdata.SupplementalReasonSupport;
import org.drools.model.Drools;
import org.jspecify.annotations.NonNull;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayloadHelper
{
    private static final DateTimeFormatter MONTH_DAY_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Create a CD instance with the specified properties.
     *
     * @param codeSystem     The code system OID
     * @param code           The code value
     * @param displayName    The display name (optional)
     * @param codeSystemName The code system name (optional)
     * @return A new CD instance with the specified properties
     */
    private static CD createCD(final String codeSystem, final String code, final String displayName, final String codeSystemName)
    {
        final CD cd = new CD();
        cd.setCodeSystem(codeSystem);
        cd.setCode(code);
        if (displayName != null)
            cd.setDisplayName(displayName);
        if (codeSystemName != null)
            cd.setCodeSystemName(codeSystemName);
        return cd;
    }

    /**
     * Create a CD instance by copying properties from a source CD.
     *
     * @param source The source CD to copy from
     * @return A new CD instance with all properties copied from the source
     */
    private static CD copyCD(final CD source)
    {
        if (source == null)
            return null;

        final CD copy = new CD();
        copy.setCodeSystem(source.getCodeSystem());
        copy.setCode(source.getCode());
        copy.setDisplayName(source.getDisplayName());
        copy.setCodeSystemName(source.getCodeSystemName());
        copy.setAny(source.getAny());
        copy.setOriginalText(source.getOriginalText());

        return copy;
    }

    /**
     * Create a ClinicalStatementRelationship with "has pertaining reason" semantics.
     *
     * @param sourceId The source statement ID
     * @param targetId The target statement ID
     * @return A new ClinicalStatementRelationship configured for "RSON"
     */
    private static ClinicalStatementRelationship createReasonRelationship(final String sourceId, final String targetId)
    {
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(sourceId);
        rel.setTargetId(targetId);
        rel.setTargetRelationshipToSource(createCD("2.16.840.1.113883.5.1002", "RSON", "has pertaining reason", null));
        return rel;
    }

    /**
     * Create a ClinicalStatementRelationship with "has pertinent information" semantics (PERT).
     *
     * @param sourceId The source statement ID
     * @param targetId The target statement ID
     * @return A new ClinicalStatementRelationship configured for "PERT"
     */
    private static ClinicalStatementRelationship createPertinentRelationship(final String sourceId, final String targetId)
    {
        final ClinicalStatementRelationship rel = new ClinicalStatementRelationship();
        rel.setSourceId(sourceId);
        rel.setTargetId(targetId);
        rel.setTargetRelationshipToSource(createCD("2.16.840.1.113883.5.1002", "PERT", "has pertinent information", null));
        return rel;
    }

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

        return copyCD(sv.getCdsListItemCD());
    }

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

        return Optional.ofNullable(sv)
                .map(lccli -> lccli.isSupplementalText() || isSupplementalReasonCodeSystem(lccli))
                .orElse(false);
    }

    /**
     * Retrieve the outbound CD(s) for a supplemental text reason code.
     *
     * @param pReasonCode The reason code (e.g., "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT.COVID_INTERVAL_5M_BOOSTER")
     * @param s           The schedule containing supporting data
     * @return Supplemental text reason coding(s) based on supplemental text output mode, or null if reason not found
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

        if (!(sv.isSupplementalText() || isSupplementalReasonCodeSystem(sv)))
        {
            log.warn(_METHODNAME + "reason code {} is not recognized as supplemental text", pReasonCode);
            return null;
        }

        final CD legacyResult;
        if (isSupplementalReasonCodeSystem(sv))
        {
            legacyResult = getLegacySupplementalCDFromBaseDataReasonType(sv, s);
            if (legacyResult == null)
                return null;

            legacyResult.setOriginalText(sv.getCdsListItemValue());
        }
        else
        {
            final CD outboundCD = sv.getCdsListItemOutboundCD();
            if (outboundCD == null)
            {
                log.warn(_METHODNAME + "no outbound coding defined for reason code: {}", pReasonCode);
                return null;
            }

            legacyResult = copyCD(outboundCD);
            if (legacyResult.getOriginalText() == null && SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE.equals(
                    legacyResult.getCode()))
                legacyResult.setOriginalText(sv.getCdsListItemValue());
        }

        final CD newResult = copyCD(sv.getCdsListItemCD());
        if (newResult != null)
            newResult.setOriginalText(null);

        return (switch (s.getSupplementalTextMode())
        {
            case LEGACY -> List.of(legacyResult);
            case CODED ->
            {
                assert newResult != null;
                yield List.of(newResult);
            }
            case BOTH ->
            {
                assert newResult != null;
                yield List.of(legacyResult, newResult);
            }
        });
    }
    // TODO: CDSOutput Template codes... Make configurable

    private static boolean isSupplementalReasonCodeSystem(final LocallyCodedCdsListItem sv)
    {
        return sv != null && sv.getSupplementalReasonType().isSupplemental();
    }

    private static CD getLegacySupplementalCDFromBaseDataReasonType(final LocallyCodedCdsListItem sv, final Schedule s)
    {
        final String _METHODNAME = "getLegacySupplementalCDFromBaseDataReasonType(): ";
        if (sv == null || s == null)
            return null;

        final String legacyReasonCode = switch (sv.getSupplementalReasonType())
        {
            case EVALUATION -> BaseDataEvaluationReason._SUPPLEMENTAL_TEXT.getCdsListItemName();
            case RECOMMENDATION -> BaseDataRecommendationReason._SUPPLEMENTAL_TEXT.getCdsListItemName();
            case NONE -> null;
        };
        if (legacyReasonCode == null)
            return null;

        final LocallyCodedCdsListItem legacyReason = getCdsListItemForReasonCode(legacyReasonCode, s);
        if (legacyReason == null)
        {
            log.warn(_METHODNAME + "unable to locate legacy supplemental reason {}; returning null", legacyReasonCode);
            return null;
        }

        final CD legacyResult = copyCD(legacyReason.getCdsListItemCD());
        if (legacyResult == null)
            return null;

        legacyResult.setCode(SupplementalReasonSupport.SUPPLEMENTAL_TEXT_CODE);
        return legacyResult;
    }

    private static INT getDoseNumberINT(final TargetDose targetDose, final boolean outputDoseCountInsteadOfDoseNumberInSeries,
            final int doseNumberCountToOutput)
    {
        final INT lINTDoseNumber = new INT();
        if (outputDoseCountInsteadOfDoseNumberInSeries && doseNumberCountToOutput >= 1)
            lINTDoseNumber.setValue(doseNumberCountToOutput);
        else
            if (outputDoseCountInsteadOfDoseNumberInSeries && targetDose.getDoseNumberInSeries() > targetDose.getDoseNumberCount())
                lINTDoseNumber.setValue(targetDose.getDoseNumberCount());
            else
                lINTDoseNumber.setValue(Math.min(targetDose.getDoseNumberInSeries(), targetDose.getDoseNumberCount()));
        return lINTDoseNumber;
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

    private final Schedule backingSchedule;
    private final Drools drools;
    private final Map<String, Object> namedObjects;
    private final boolean outputSupplementalText;
    private final boolean outputScheduleAuthorities;
    private final boolean outputVaccineGroupRulesArtifact;

    public PayloadHelper(final Schedule backingSchedule, final Drools drools, final Map<String, Object> namedObjects,
            final boolean outputSupplementalText, final boolean outputScheduleAuthorities,
            final boolean outputVaccineGroupRulesArtifact)
    {
        if (backingSchedule == null || !backingSchedule.isScheduleInitialized())
        {
            final String lExStr = "Schedule has not been provided or has not been initialized; cannot continue";
            log.error("PayloadHelper(): " + lExStr);
            throw new IllegalArgumentException(lExStr);
        }

        if (drools == null)
        {
            final String lExStr = "Drools has not been provided; cannot continue";
            log.error("PayloadHelper(): " + lExStr);
            throw new IllegalArgumentException(lExStr);
        }

        if (namedObjects == null)
        {
            final String lExStr = "NamedObjects has not been provided; cannot continue";
            log.error("PayloadHelper(): " + lExStr);
            throw new IllegalArgumentException(lExStr);
        }

        this.backingSchedule = backingSchedule;
        this.drools = drools;
        this.namedObjects = namedObjects;
        this.outputSupplementalText = outputSupplementalText;
        this.outputScheduleAuthorities = outputScheduleAuthorities;
        this.outputVaccineGroupRulesArtifact = outputVaccineGroupRulesArtifact;
    }

    @SuppressWarnings("unused")
    public void outputNestedImmEvaluationResult(final EvalTime evalTime, final String focalPersonId, final String cdsSource,
            final SubstanceAdministrationEvent incomingSAE, final String vg, final TargetDose targetDose,
            final boolean outputDoseCountInsteadOfDoseNumberInSeries)
    {
        outputNestedImmEvaluationResult(evalTime, focalPersonId, cdsSource, incomingSAE, vg, targetDose,
                outputDoseCountInsteadOfDoseNumberInSeries, -1);
    }

    public void outputNestedImmEvaluationResult(final EvalTime evalTime, final String focalPersonId, final String cdsSource,
            final SubstanceAdministrationEvent incomingSAE, final String vg, final TargetDose targetDose,
            final boolean outputDoseCountInsteadOfDoseNumberInSeries, final int doseNumberCountToOutput)
    {
        final String _METHODNAME = "outputNestedImmEvaluationResult: ";
        if (evalTime == null || incomingSAE == null || targetDose == null)
        {
            final String str = "Caller supplied either NULL evalTime, SubstanceAdministrationEvent or TargetDose parameter";
            log.warn(_METHODNAME + str);
            throw new IllegalArgumentException(str);
        }

        if (log.isDebugEnabled())
            log.debug("focalPersonId {}, sae: {}, VG: {}, Dose unique ID: {}, Dose ID {}, Dose all: {}", focalPersonId,
                    incomingSAE.getId(), vg, targetDose.getUniqueId(), targetDose.getDoseId(), targetDose);

        // Create an Embedded SubstanceAdministrationEvent
        final SubstanceAdministrationEvent embeddedSAE = new SubstanceAdministrationEvent();
        final String uniqueSarIdValue = ICELogicHelper.generateUniqueString();
        embeddedSAE.setId(uniqueSarIdValue);
        final String[] subsAdmEvtTemplateArr = { "2.16.840.1.113883.3.795.11.9.1.1" };
        embeddedSAE.setTemplateId(subsAdmEvtTemplateArr);
        embeddedSAE.setEvaluatedPersonId(focalPersonId);
        embeddedSAE.setSubjectIsFocalPerson(true);

        // Record Substance AdministrationEvent CDS System Data Source
        if (!ObjectUtils.isEmpty(cdsSource))
            embeddedSAE.setDataSourceType(
                    createCD("2.16.840.1.113883.3.795.5.4.12.1.2", cdsSource, null, "org.cdsframework source"));

        // Substance Proposal General Purpose
        embeddedSAE.setSubstanceAdministrationGeneralPurpose(
                createCD("2.16.840.1.113883.6.5", "384810002", "Immunization/vaccination management (procedure)", "SNOMED CT"));

        // Dose number information
        final INT lINTDoseNumber =
                getDoseNumberINT(targetDose, outputDoseCountInsteadOfDoseNumberInSeries, doseNumberCountToOutput);
        embeddedSAE.setDoseNumber(lINTDoseNumber);

        // Administration Time Interval
        embeddedSAE.setAdministrationTimeInterval(incomingSAE.getAdministrationTimeInterval());

        // Validity flag
        final BL lSAEValidity = new BL();
        lSAEValidity.setValue(targetDose.getIsValid());
        embeddedSAE.setIsValid(lSAEValidity);

        // AdministrableSubstance
        embeddedSAE.setSubstance(getAdministrableSubstance(targetDose));

        // This is a nested clinical statement
        embeddedSAE.setClinicalStatementToBeRoot(false);
        embeddedSAE.setToBeReturned(true);
        droolsInsert("lSAE" + uniqueSarIdValue, embeddedSAE);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = createPertinentRelationship(incomingSAE.getId(), uniqueSarIdValue);
        droolsInsert("rel" + uniqueSarIdValue, rel);

        // Include Wiki URL if additional info enabled and URL exists for this vaccine group
        if (outputVaccineGroupRulesArtifact)
            createVaccineGroupUrlObservation(focalPersonId, vg, embeddedSAE.getId());

        // Include series display if captured
        if (targetDose.getSeriesDisplaySelectionType() != null)
            createEvaluationSeriesDisplayObservation(focalPersonId, targetDose, embeddedSAE);

        // Create observation for dose evaluation
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        final DoseStatus doseStatus = targetDose.getStatus();

        final Collection<String> lReasons = switch (doseStatus)
        {
            case VALID -> targetDose.getValidReasons();
            case INVALID -> targetDose.getInvalidReasons();
            case ACCEPTED -> targetDose.getAcceptedReasons();
            case NOT_EVALUATED -> targetDose.getNotEvaluatedReasons();
            default -> new ArrayList<>();
        };

        createEvaluationObservation(uniqueSarIdValue, nestedIdValue, focalPersonId, evalTime, vg, doseStatus, lReasons, null);
    }

    private void createEvaluationSeriesDisplayObservation(final String focalPersonId, final TargetDose targetDose,
            final SubstanceAdministrationEvent embeddedSAE)
    {
        final ObservationResult lCollectionOfSeriesDisplaysObs =
                createSeriesDisplayCollectionObservation(embeddedSAE.getId(), focalPersonId);

        // Create an ObservationResult containing the Series Display Selection and its series display type
        final ObservationResult lSeriesDisplayObservation =
                createSeriesDisplayObservation(lCollectionOfSeriesDisplaysObs.getId(), focalPersonId,
                        targetDose.getSeriesDisplaySelectionType(), targetDose.getEvaluatedSeriesName());

        // Add season if target series has a season
        Optional.ofNullable(targetDose.getEvaluatedSeriesSeason())
                .ifPresent(season -> createSeasonObservation(lSeriesDisplayObservation.getId(),
                        "Season-" + ICELogicHelper.generateUniqueString(), focalPersonId, season));
    }

    private @NonNull AdministrableSubstance getAdministrableSubstance(final TargetDose targetDose)
    {
        final String _METHODNAME = "getAdministrableSubstance: ";

        final AdministrableSubstance lAS = new AdministrableSubstance();
        lAS.setId(ICELogicHelper.generateUniqueString());
        // Get the associated vaccine concept associated with the TargetDose
        final VaccineComponent vaccineComponentForOutput = targetDose.getReportingVaccineComponent() == null
                                                           ? targetDose.getVaccineComponent()
                                                           : targetDose.getReportingVaccineComponent();
        final LocallyCodedCdsListItem lSVC = this.backingSchedule.getICESupportingDataConfiguration()
                .getSupportedCdsConcepts()
                .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.OPENCDS,
                        vaccineComponentForOutput.getCdsConcept());
        if (lSVC == null)
        {
            final String lErrStr = "LocallyCodedCdsListItem Vaccine not found for specified TargetDose; this should not occur";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }

        lAS.setSubstanceCode(createCD(lSVC.getCdsListCodeSystem(), lSVC.getCdsListItemKey(), lSVC.getCdsListItemValue(), null));
        lAS.setToBeReturned(true);
        return lAS;
    }

    @SuppressWarnings("unused")
    public void outputNestedImmEvaluationNotSupported(final EvalTime evalTime, final String focalPersonId, final String cdsSource,
            final SubstanceAdministrationEvent sae, final String vg)
    {
        final String _METHODNAME = "outputNestedImmEvaluationNotSupported: ";
        if (evalTime == null || sae == null || vg == null)
        {
            final String str = "Caller supplied either NULL evalTime, or SubstanceAdministrationEvent parameter";
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
            lSAE.setDataSourceType(createCD("2.16.840.1.113883.3.795.5.4.12.1.2", cdsSource, null, "org.cdsframework source"));

        // Substance Proposal General Purpose
        lSAE.setSubstanceAdministrationGeneralPurpose(
                createCD("2.16.840.1.113883.6.5", "384810002", "Immunization/vaccination management (procedure)", "SNOMED CT"));
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
        droolsInsert("lSAE" + uniqueSarIdValue, lSAE);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = createPertinentRelationship(conceptTargetId, uniqueSarIdValue);
        droolsInsert("rel" + uniqueSarIdValue, rel);

        // Create observation for vaccine not supported
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        final List<CD> notSupportedReasons = new ArrayList<>();
        notSupportedReasons.add(
                getLocalCodeForEvaluationReason(BaseDataEvaluationReason._VACCINE_NOT_SUPPORTED_REASON.getCdsListItemName(),
                        this.backingSchedule));

        createEvaluationObservation(uniqueSarIdValue, nestedIdValue, focalPersonId, evalTime, vg, DoseStatus.NOT_EVALUATED, null,
                notSupportedReasons);
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
    @SuppressWarnings("unused")
    public SubstanceAdministrationProposal outputRootImmRecommendationSubstanceAdministrationProposal(final String focalPersonId,
            final String cdsSource, final TargetSeries ts, final boolean outputEarliestOverdue)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "outputRootImmRecommendationSubstanceAdministrationProposal: ";

        if (ts == null || ts.getTargetSeriesIdentifier() == null)
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
            sap.setDataSourceType(createCD("2.16.840.1.113883.3.795.5.4.12.1.2", cdsSource, null, "org.cdsframework source"));

        // Substance Proposal General Purpose
        sap.setSubstanceAdministrationGeneralPurpose(
                createCD("2.16.840.1.113883.6.5", "384810002", "Immunization/vaccination management (procedure)", "SNOMED CT"));

        // Set the Earliest valid date, recommendation date and/or latest recommendation date
        final LocalDate finalEarliestDate = ts.getFinalEarliestDate();
        final LocalDate finalRecommendationDate = ts.getFinalRecommendationDate();
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
            final LocalDate finalLatestRecommendationDate = ts.getFinalOverdueDate();
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
        droolsInsert("sap" + uniqueSarIdValue, sap);

        // Now create the nested observation result
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        final String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
        sap.setTemplateId(observationResultTemplateArr);

        createRecommendationObservation(uniqueSarIdValue, nestedIdValue, focalPersonId, ts);

        if (outputVaccineGroupRulesArtifact)
            createVaccineGroupUrlObservation(focalPersonId, ts.getVaccineGroup(), uniqueSarIdValue);

        if (outputScheduleAuthorities)
            createVaccineGroupScheduleAuthoritiesObservation(focalPersonId, ts.getVaccineGroup(), uniqueSarIdValue);

        return sap;
    }

    @SuppressWarnings("unused")
    public SubstanceAdministrationProposal outputOtherImmRecommendationSubstanceAdministrationProposal(final String focalPersonId,
            final String cdsSource) throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "outputRootImmRecommendationSubstanceAdministrationProposal: ";

        if (namedObjects == null || drools == null)
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
            final CD cdsDataSource = createCD("2.16.840.1.113883.3.795.5.4.12.1.2", cdsSource, "ICE Version", "CAT CDS Source");
            sap.setDataSourceType(cdsDataSource);
        }

        // Substance Proposal General Purpose
        sap.setSubstanceAdministrationGeneralPurpose(
                createCD("2.16.840.1.113883.6.5", "384810002", "Immunization/vaccination management (procedure)", "SNOMED CT"));

        // Set the AdministrableSubstance - may be a vaccine or a vaccine group
        final CD localObservationFocusCD = getLocalCodeForRecommendationReason("VACCINE_GROUP_CONCEPT.OTHER", this.backingSchedule);
        final AdministrableSubstance substance = new AdministrableSubstance();
        substance.setId(ICELogicHelper.generateUniqueString());
        substance.setSubstanceCode(localObservationFocusCD);
        sap.setSubstance(substance);

        // Set as a root clinical statement
        sap.setClinicalStatementToBeRoot(true);
        sap.setToBeReturned(true);
        droolsInsert("sap" + uniqueSarIdValue, sap);

        // Now create the nested observation result
        final String nestedIdValue = ICELogicHelper.generateUniqueString();
        final String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
        sap.setTemplateId(observationResultTemplateArr);

        createUnavailableRecommendationObservation(uniqueSarIdValue, nestedIdValue, focalPersonId, localObservationFocusCD);

        return sap;
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

    /**
     * Create an evaluation observation for a dose status.
     *
     * @param nestedIdValue Unique ID for the observation
     * @param focalPersonId Patient ID
     * @param evalTime      Evaluation time
     * @param vaccineGroup  Vaccine group code
     * @param doseStatus    Status of the dose
     * @param reasons       Collection of reasons for the status
     * @return Configured ObservationResult for evaluation
     */
    private ObservationResult createEvaluationObservation(final String sourceId, final String nestedIdValue,
            final String focalPersonId, final EvalTime evalTime, final String vaccineGroup, final DoseStatus doseStatus,
            final Collection<String> reasons, final List<CD> interpretationsOverride)
    {
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
        final CD localCD = getLocalCodeForEvaluationConcept(vaccineGroup);
        childObs.setObservationFocus(localCD);

        // Observation Value
        final CD localObsCD = getLocalCodeForEvaluationStatus(doseStatus, this.backingSchedule);
        final ObservationValue childObsValue = new ObservationValue();
        childObsValue.setConcept(localObsCD);
        childObs.setObservationValue(childObsValue);

        // Observation interpretation
        if (interpretationsOverride != null)
        {
            childObs.setInterpretation(interpretationsOverride);
        }
        else
            if (doseStatus == DoseStatus.VALID || doseStatus == DoseStatus.INVALID || doseStatus == DoseStatus.ACCEPTED
                    || doseStatus == DoseStatus.NOT_EVALUATED)
            {
                final List<CD> interpretations = new ArrayList<>();

                if (reasons != null)
                {
                    for (final String interp : reasons)
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
                }

                if (!interpretations.isEmpty())
                    childObs.setInterpretation(interpretations);
            }

        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);

        droolsInsert("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship relO = createPertinentRelationship(sourceId, nestedIdValue);
        droolsInsert("rel" + nestedIdValue, relO);

        return childObs;
    }

    /**
     * Create a recommendation observation for a target series.
     *
     * @param nestedIdValue Unique ID for the observation
     * @param focalPersonId Patient ID
     * @param targetSeries  The target series with recommendation info
     * @return Configured ObservationResult for recommendation
     * @throws IllegalArgumentException If recommendation status is invalid
     */
    private ObservationResult createRecommendationObservation(final String sourceId, final String nestedIdValue,
            final String focalPersonId, final TargetSeries targetSeries) throws IllegalArgumentException
    {
        final String _METHODNAME = "createRecommendationObservation: ";

        // Observation
        final ObservationResult childObs = new ObservationResult();
        childObs.setId(nestedIdValue);
        final String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };
        childObs.setTemplateId(observationResultTemplateArr);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setSubjectIsFocalPerson(true);

        // Set the Observation Focus - always the vaccine group
        final CD localCD = getLocalCodeConceptForRecommendationConcept(targetSeries, false);
        childObs.setObservationFocus(localCD);

        // Observation Value
        final CD localObsCD = getLocalCodeForRecommendationStatus(targetSeries.getRecommendationStatus(), this.backingSchedule);
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
        final RecommendationStatus rs = targetSeries.getRecommendationStatus();
        final List<Recommendation> recs = targetSeries.getFinalRecommendations();
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
                                    .map(cdList -> cdList.stream().filter(cd -> !interpretations.contains(cd)).toList())
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

        droolsInsert("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = createReasonRelationship(sourceId, nestedIdValue);
        droolsInsert("rel" + nestedIdValue, rel);

        return childObs;
    }

    /**
     * Create an observation for recommendations not available/supported.
     *
     * @param nestedIdValue      Unique ID for the observation
     * @param focalPersonId      Patient ID
     * @param observationFocusCD CD representing observation focus (vaccine group)
     * @return Configured ObservationResult for unavailable recommendation
     */
    private ObservationResult createUnavailableRecommendationObservation(final String sourceId, final String nestedIdValue,
            final String focalPersonId, final CD observationFocusCD)
    {
        final String[] observationResultTemplateArr = { "2.16.840.1.113883.3.795.11.6.3.1" };

        final ObservationResult childObs = new ObservationResult();
        childObs.setId(nestedIdValue);
        childObs.setTemplateId(observationResultTemplateArr);
        childObs.setEvaluatedPersonId(focalPersonId);
        childObs.setSubjectIsFocalPerson(true);

        // Set the Observation Focus
        childObs.setObservationFocus(observationFocusCD);

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

        droolsInsert("childObs" + nestedIdValue, childObs);

        // Therefore, create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = createReasonRelationship(sourceId, nestedIdValue);
        droolsInsert("rel" + nestedIdValue, rel);

        return childObs;
    }

    /**
     * Create a doses remaining observation.
     *
     * @param nestedIdValue  Unique ID for the observation
     * @param focalPersonId  Patient ID
     * @param dosesRemaining Text representing number of doses remaining
     */
    private void createDosesRemainingObservation(final String sourceId, final String nestedIdValue, final String focalPersonId,
            final String dosesRemaining)
    {
        final ObservationResult childObs = generateObservationResult(nestedIdValue, focalPersonId, true);

        // Set the Observation Focus - always the vaccine group
        final CD localCD = new CD();
        localCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.10");
        localCD.setCode("NUMBER_OF_DOSES_REMAINING");
        localCD.setDisplayName("Doses Remaining");
        localCD.setOriginalText("Number of doses remaining in the series, as of the evaluation date");
        childObs.setObservationFocus(localCD);

        // Observation Value
        final ObservationValue childObsValue = new ObservationValue();
        childObsValue.setText(dosesRemaining);
        childObs.setObservationValue(childObsValue);

        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);

        droolsInsert("childObs" + nestedIdValue, childObs);

        // Create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = createReasonRelationship(sourceId, nestedIdValue);
        droolsInsert("rel" + nestedIdValue, rel);
    }

    private void createSeasonObservation(final String sourceId, final String nestedIdValue, final String focalPersonId,
            final Season targetSeason)
    {
        final ObservationResult childObs = generateObservationResult(nestedIdValue, focalPersonId, true);

        // Set the Observation Focus - always the season
        final CD localCD = new CD();
        localCD.setCodeSystem("2.16.840.1.113883.3.795.12.100.11");
        localCD.setCodeSystemName("ICE Vaccine Season");
        localCD.setCode("SEASON");
        localCD.setDisplayName("Season");
        localCD.setOriginalText("Season associated with the series");
        childObs.setObservationFocus(localCD);

        // Observation Value (make a copy of the CD so that it can be safely modified)
        final String seasonName = targetSeason.getSeasonName();
        final ObservationValue lSeasonToDisplayObsValue = new ObservationValue();
        Optional.ofNullable(this.backingSchedule.getICESupportingDataConfiguration()
                        .getSupportedCdsConcepts()
                        .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.SEASON,
                                new CdsConcept(targetSeason.getSeasonName())))
                .map(LocallyCodedCdsListItem::getCdsListItemCD)
                .map(PayloadHelper::copyCD)
                .ifPresentOrElse(lSeasonToDisplayObsValue::setConcept, () ->
                {
                    log.warn("Season not found in supporting data: {}", seasonName);
                    lSeasonToDisplayObsValue.setConcept(
                            createCD("2.16.840.1.113883.3.795.12.100.11", seasonName, seasonName, "ICE Vaccine Season"));
                });

        // Append start and end season dates to display text
        lSeasonToDisplayObsValue.getConcept()
                .setDisplayName(String.format("%s (%s - %s)", lSeasonToDisplayObsValue.getConcept().getDisplayName(),
                        MONTH_DAY_YEAR_FORMATTER.format(targetSeason.getSeasonStartDate()),
                        MONTH_DAY_YEAR_FORMATTER.format(targetSeason.getSeasonEndDate())));

        childObs.setObservationValue(lSeasonToDisplayObsValue);

        childObs.setClinicalStatementToBeRoot(false);
        childObs.setToBeReturned(true);

        droolsInsert("childObs" + nestedIdValue, childObs);

        // Create as a relatedClinicalStatement
        final ClinicalStatementRelationship rel = createReasonRelationship(sourceId, nestedIdValue);
        droolsInsert("rel" + nestedIdValue, rel);
    }

    /**
     * Create a series display observation for a specific series selection.
     *
     * @param focalPersonId              Patient ID
     * @param seriesDisplaySelectionType The series display selection type
     * @param seriesName                 The series name
     * @return Configured ObservationResult for series display
     */
    private ObservationResult createSeriesDisplayObservation(final String sourceId, final String focalPersonId,
            final SeriesDisplaySelectionType seriesDisplaySelectionType, final String seriesName)
    {
        final ObservationResult lSeriesToDisplayObs =
                generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);

        // Set the Observation Focus to the Series Display Type
        lSeriesToDisplayObs.setObservationFocus(getLocalCodeForSeriesDisplaySelectionType(seriesDisplaySelectionType));

        // Set the Observation Value to the coded value representing the Series
        final ObservationValue lSeriesToDisplayObsValue = new ObservationValue();
        Optional.ofNullable(this.backingSchedule.getICESupportingDataConfiguration()
                        .getSupportedCdsConcepts()
                        .getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType.SERIES, new CdsConcept(seriesName)))
                .map(LocallyCodedCdsListItem::getCdsListItemCD)
                .ifPresentOrElse(lSeriesToDisplayObsValue::setConcept, () ->
                {
                    log.warn("Series not found in supporting data: {}", seriesName);
                    lSeriesToDisplayObsValue.setConcept(createCD("2.16.840.1.113883.3.795.12.100.10", seriesName, null, null));
                });

        lSeriesToDisplayObs.setObservationValue(lSeriesToDisplayObsValue);

        lSeriesToDisplayObs.setClinicalStatementToBeRoot(false);
        lSeriesToDisplayObs.setToBeReturned(true);

        droolsInsert("childObs" + lSeriesToDisplayObs.getId(), lSeriesToDisplayObs);

        // Relate the display series with the collection of series
        final ClinicalStatementRelationship srel = createReasonRelationship(sourceId, lSeriesToDisplayObs.getId());
        droolsInsert("rel" + lSeriesToDisplayObs.getId(), srel);

        return lSeriesToDisplayObs;
    }

    private void createVaccineGroupUrlObservation(final String focalPersonId, final String vg, final String sourceId)
    {
        Optional.ofNullable(this.backingSchedule.getICESupportingDataConfiguration()
                        .getSupportedVaccineGroups()
                        .getVaccineGroupItem(vg))
                .map(LocallyCodedVaccineGroupItem::getVaccineGroupRulesUrl)
                .ifPresent(url ->
                {
                    final ObservationResult lVaccineGroupUrlObservation =
                            generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);

                    // Set the Observation Focus to additional information (this is a placeholder code and should be replaced with the actual code from supporting data)
                    lVaccineGroupUrlObservation.setObservationFocus(
                            createCD("2.16.840.1.113883.3.795.12.100.500", "VACCINE_GROUP_RULES_URL",
                                    "URL for the vaccine group rules (i.e., logic specification)", null));

                    final ObservationValue lSeriesToDisplayObsValue = new ObservationValue();
                    lSeriesToDisplayObsValue.setText(url);
                    lVaccineGroupUrlObservation.setObservationValue(lSeriesToDisplayObsValue);

                    lVaccineGroupUrlObservation.setClinicalStatementToBeRoot(false);
                    lVaccineGroupUrlObservation.setToBeReturned(true);

                    droolsInsert("childObs" + lVaccineGroupUrlObservation.getId(), lVaccineGroupUrlObservation);

                    // Finally, relate the top-level ObservationResult collection to the SubstanceAdministrationProposal
                    final ClinicalStatementRelationship rel =
                            createPertinentRelationship(sourceId, lVaccineGroupUrlObservation.getId());
                    droolsInsert("rel" + lVaccineGroupUrlObservation.getId(), rel);
                });
    }

    private void createVaccineGroupScheduleAuthoritiesObservation(final String focalPersonId, final String vg,
            final String sourceId)
    {
        final String _METHODNAME = "createVaccineGroupScheduleAuthoritiesObservation: ";
        final LocallyCodedVaccineGroupItem lVGI = this.backingSchedule.getICESupportingDataConfiguration()
                .getSupportedVaccineGroups()
                .getVaccineGroupItem(vg);

        if (lVGI == null)
        {
            log.warn(_METHODNAME + "LocallyCodedVaccineGroupItem not found for vaccine group: " + vg);
            return;
        }

        final Collection<String> lScheduleAuthorityCodes = lVGI.getScheduleAuthorityCdsListItemNames();
        if (ObjectUtils.isEmpty(lScheduleAuthorityCodes))
            return;

        final ObservationResult lObservationResult =
                generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);

        lObservationResult.setObservationFocus(
                createCD("2.16.840.1.113883.3.795.12.100.500", "ICE_VACCINE_GROUP_SCHEDULE_AUTHORITIES",
                        "Schedule authority or authorities for the vaccine group.", null));

        final List<CD> lInterpretations = new ArrayList<>();
        final String scheduleAuthorityCdsListCode = ICEConceptType.SCHEDULE_AUTHORITY.getIceConceptTypeValue();
        for (final String lSACode : lScheduleAuthorityCodes)
        {
            final String lSAItemName = "%s.%s".formatted(scheduleAuthorityCdsListCode, lSACode);
            final LocallyCodedCdsListItem lSALI = this.backingSchedule.getICESupportingDataConfiguration()
                    .getSupportedCdsLists()
                    .getCdsListItem(lSAItemName);

            if (lSALI != null)
            {
                final CD lCD = copyCD(lSALI.getCdsListItemCD());
                if (lCD != null && !lInterpretations.contains(lCD))
                    lInterpretations.add(lCD);
            }
        }

        if (!lInterpretations.isEmpty())
            lObservationResult.setInterpretation(lInterpretations);
        else
            return;

        lObservationResult.setClinicalStatementToBeRoot(false);
        lObservationResult.setToBeReturned(true);

        droolsInsert("childObs" + lObservationResult.getId(), lObservationResult);

        final ClinicalStatementRelationship rel = createPertinentRelationship(sourceId, lObservationResult.getId());
        droolsInsert("rel" + lObservationResult.getId(), rel);
    }

    private CD getLocalCodeForSeriesDisplaySelectionType(final SeriesDisplaySelectionType seriesDisplaySelectionType)
    {
        final String _METHODNAME = "getLocalCodeForSeriesDisplaySelectionType(): ";
        if (seriesDisplaySelectionType == null)
        {
            log.warn(_METHODNAME + "Series display selection type parameter supplied is null");
            return null;
        }

        final String seriesDisplaySelectionCode = seriesDisplaySelectionType.getCode();
        final String seriesDisplayCdsListCode = ICEConceptType.SERIES_DISPLAY_SELECTION_TYPE.getIceConceptTypeValue();
        final String seriesDisplaySelectionTypeListItemName =
                "%s.%s".formatted(seriesDisplayCdsListCode, seriesDisplaySelectionCode);
        final LocallyCodedCdsListItem lccli = this.backingSchedule.getICESupportingDataConfiguration()
                .getSupportedCdsLists()
                .getCdsListItem(seriesDisplaySelectionTypeListItemName);
        if (lccli != null)
            return copyCD(lccli.getCdsListItemCD());

        final String fallbackCodeSystem = Optional.ofNullable(this.backingSchedule.getICESupportingDataConfiguration()
                        .getSupportedCdsLists()
                        .getCdsListItemsAssociatedWithCdsListCode(seriesDisplayCdsListCode))
                .stream()
                .flatMap(Collection::stream)
                .map(LocallyCodedCdsListItem::getCdsListCodeSystem)
                .filter(codeSystem -> codeSystem != null && !codeSystem.isBlank())
                .findFirst()
                .orElse(null);

        log.warn(_METHODNAME + "No associated LocallyCodedCdsListItem for supplied series display selection code: {}",
                seriesDisplaySelectionCode);
        return createCD(fallbackCodeSystem, seriesDisplaySelectionCode, null, null);
    }

    /**
     * Create a collection observation for series display options.
     *
     * @param focalPersonId Patient ID
     * @return Configured ObservationResult for series display collection
     */
    private ObservationResult createSeriesDisplayCollectionObservation(final String sourceId, final String focalPersonId)
    {
        final ObservationResult lCollectionOfSeriesDisplaysObs =
                generateObservationResult(ICELogicHelper.generateUniqueString(), focalPersonId, true);

        // Set the Observation Focus for the Series Display
        // TODO: incorporate into and pull from supporting data
        lCollectionOfSeriesDisplaysObs.setObservationFocus(
                createCD("2.16.840.1.113883.3.795.12.100.500", "SERIES_DISPLAY_OPTIONS", "Series Options for Display", null));

        lCollectionOfSeriesDisplaysObs.setClinicalStatementToBeRoot(false);
        lCollectionOfSeriesDisplaysObs.setToBeReturned(true);

        droolsInsert("childObs" + lCollectionOfSeriesDisplaysObs.getId(), lCollectionOfSeriesDisplaysObs);

        // Finally, relate the top-level ObservationResult collection to the SubstanceAdministrationProposal
        final ClinicalStatementRelationship rel = createReasonRelationship(sourceId, lCollectionOfSeriesDisplaysObs.getId());
        droolsInsert("rel" + lCollectionOfSeriesDisplaysObs.getId(), rel);

        return lCollectionOfSeriesDisplaysObs;
    }

    @SuppressWarnings("unused")
    public void outputSeriesDisplaySelectionsAndDosesRemainingInEmbeddedSubstanceAdministrationProposals(final String focalPersonId,
            final List<SeriesDisplaySelection> pSeriesDisplays, final SubstanceAdministrationProposal pSAP,
            final Boolean outputNumberOfDosesRemaining) throws IllegalArgumentException
    {
        final String _METHODNAME = "outputSeriesDisplaySelectionsAndDosesRemainingInEmbeddedSubstanceAdministrationProposals: ";
        if (pSAP == null || pSeriesDisplays == null)
        {
            final String lErrStr = "Unable to output doses remaining: one or more parameters not specified";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (pSeriesDisplays.isEmpty())
            return;

        // START - Top Level Observation for the Series Display
        final ObservationResult lCollectionOfSeriesDisplaysObs =
                createSeriesDisplayCollectionObservation(pSAP.getId(), focalPersonId);

        // Loop through all the scored series selections for display
        for (final SeriesDisplaySelection lSDS : pSeriesDisplays)
        {
            // Create an ObservationResult containing the Series Display Selection and its series display type
            final ObservationResult lSeriesToDisplayObs =
                    createSeriesDisplayObservation(lCollectionOfSeriesDisplaysObs.getId(), focalPersonId,
                            lSDS.getSeriesDisplaySelectionType(), lSDS.getTargetSeries().getSeriesName());

            // Create an ObservationResult for the season if the target series has an associated season
            Optional.ofNullable(lSDS.getTargetSeries().getTargetSeason())
                    .ifPresent(season -> createSeasonObservation(lSeriesToDisplayObs.getId(),
                            "Season-" + ICELogicHelper.generateUniqueString(), focalPersonId,
                            lSDS.getTargetSeries().getTargetSeason()));

            // Doses Remaining - Embed the number of doses remaining for the series within the series display type
            if (outputNumberOfDosesRemaining)
                createDosesRemainingObservation(lSeriesToDisplayObs.getId(),
                        "Doses-remaining-" + ICELogicHelper.generateUniqueString(), focalPersonId,
                        lSDS.getNumberOfDosesRemaining());
        }
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

    private void droolsInsert(final String key, final Object object)
    {
        this.drools.insert(object);
        this.namedObjects.put(key, object);
    }
}
