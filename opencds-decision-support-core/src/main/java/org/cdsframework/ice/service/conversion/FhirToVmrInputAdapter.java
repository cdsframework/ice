package org.cdsframework.ice.service.conversion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.cdsframework.fhir.AdministrativeGender;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Immunization;
import org.cdsframework.fhir.Observation;
import org.cdsframework.fhir.Patient;
import org.cdsframework.ice.service.SupportingDataService;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.BL;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSContext;
import org.opencds.vmr.v1_0.schema.CDSInput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.IVLTS;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.TS;
import org.opencds.vmr.v1_0.schema.VMR;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FhirToVmrInputAdapter
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SCHEDULE_FLAGS_CODE_SYSTEM_OID = "2.16.840.1.113883.3.795.12.100.502";
    private static final String ADMIN_GENDER_CODE_MALE = "M";
    private static final String ADMIN_GENDER_CODE_FEMALE = "F";
    private static final String ADMIN_GENDER_CODE_UNKNOWN = "UN";
    private static final String V3_ADMINISTRATIVE_GENDER_OID = "2.16.840.1.113883.5.1";
    private static final String CLIENT_LANGUAGE_CODE = "en";
    private static final String CLIENT_LANGUAGE_DISPLAY = "English";
    private static final String CLIENT_LANGUAGE_CODE_SYSTEM = "2.16.840.1.113883.6.99";
    private static final String TEMPLATE_ID_CDS_INPUT = "2.16.840.1.113883.3.795.11.1.1";
    private static final String TEMPLATE_ID_EVALUATED_PERSON = "2.16.840.1.113883.3.795.11.2.1.1";
    private static final String TEMPLATE_ID_OBSERVATION_RESULT = "2.16.840.1.113883.3.795.11.6.3.1";
    private static final String TEMPLATE_ID_SUBSTANCE_ADMINISTRATION_EVENT = "2.16.840.1.113883.3.795.11.9.1.1";
    private static final String EVALUATED_PERSON_ID_ROOT = "2.16.840.1.113883.3.795.12.100.11";
    private static final String IMMUNIZATION_ID_ROOT = "2.16.840.1.113883.3.795.12.100.10";
    private static final String SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE = "384810002";
    private static final String SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE_SYSTEM = "2.16.840.1.113883.6.5";

    private static CD createDisplayCd(final String code, final String codeSystem, final String displayName)
    {
        final CD cd = new CD();
        cd.setCode(code);
        cd.setCodeSystem(codeSystem);
        cd.setDisplayName(displayName);
        return cd;
    }

    private static String formatVmrDate(final LocalDate date)
    {
        return date.format(DATE_FORMAT);
    }

    private final II templateIdCdsInputPrototype = createIi(TEMPLATE_ID_CDS_INPUT, null);
    private final II templateIdEvaluatedPersonPrototype = createIi(TEMPLATE_ID_EVALUATED_PERSON, null);
    private final II templateIdObservationResultPrototype = createIi(TEMPLATE_ID_OBSERVATION_RESULT, null);
    private final II templateIdSubstanceAdministrationEventPrototype = createIi(TEMPLATE_ID_SUBSTANCE_ADMINISTRATION_EVENT, null);
    private final CD clientLanguageCdPrototype =
            createDisplayCd(CLIENT_LANGUAGE_CODE, CLIENT_LANGUAGE_CODE_SYSTEM, CLIENT_LANGUAGE_DISPLAY);
    private final CD substanceAdministrationGeneralPurposeCdPrototype =
            createDisplayCd(SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE, SUBSTANCE_ADMINISTRATION_GENERAL_PURPOSE_CODE_SYSTEM,
                    null);
    private final SupportingDataService supportingDataService;

    public CDSInput createCdsInput(final String kmId, final Patient patient, final List<Immunization> immunizations,
            final List<Observation> observations, final List<String> scheduleFlags)
    {
        return createCdsInput(List.of(copyIi(templateIdCdsInputPrototype)), createCdsContext(copyCd(clientLanguageCdPrototype)),
                createVmr(List.of(copyIi(templateIdCdsInputPrototype)),
                        createEvaluatedPerson(kmId, patient, immunizations, observations, scheduleFlags)));
    }

    private EvaluatedPerson createEvaluatedPerson(final String kmId, final Patient patient, final List<Immunization> immunizations,
            final List<Observation> observations, final List<String> scheduleFlags)
    {
        final EvaluatedPerson evaluatedPerson = new EvaluatedPerson();

        evaluatedPerson.getTemplateId().add(copyIi(templateIdEvaluatedPersonPrototype));
        final String patientIdentifier =
                Optional.ofNullable(patient).map(Patient::identifier).map(this::extractPrimaryIdentifierValue).orElse(null);
        evaluatedPerson.setId(createIi(EVALUATED_PERSON_ID_ROOT, patientIdentifier));

        final EvaluatedPerson.Demographics demographics = new EvaluatedPerson.Demographics();
        evaluatedPerson.setDemographics(demographics);

        final TS birthTime = new TS();
        birthTime.setValue(formatVmrDate(Optional.ofNullable(patient)
                .map(Patient::birthDate)
                .orElseThrow(() -> new IllegalArgumentException("Patient birthDate is required"))));
        demographics.setBirthTime(birthTime);
        Optional.of(patient)
                .map(Patient::gender)
                .map(this::toVmrAdministrativeGenderCode)
                .filter(StringUtils::hasText)
                .map(code -> createDisplayCd(code, V3_ADMINISTRATIVE_GENDER_OID, code))
                .ifPresent(demographics::setGender);
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        evaluatedPerson.setClinicalStatements(clinicalStatements);
        clinicalStatements.setObservationResults(new EvaluatedPerson.ClinicalStatements.ObservationResults());

        clinicalStatements.getObservationResults()
                .getObservationResult()
                .addAll(Optional.ofNullable(observations)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(observation -> createObservationResult(kmId, observation))
                        .toList());
        clinicalStatements.getObservationResults()
                .getObservationResult()
                .addAll(Optional.ofNullable(scheduleFlags)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(scheduleFlag -> createScheduleFlagObservationResult(kmId, scheduleFlag))
                        .toList());

        clinicalStatements.setSubstanceAdministrationEvents(createSubstanceAdministrationEvents(Optional.ofNullable(immunizations)
                .stream()
                .flatMap(Collection::stream)
                .map(immunization -> createSubstanceAdministrationEvent(kmId, immunization))
                .toList()));

        return evaluatedPerson;
    }

    private EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents createSubstanceAdministrationEvents(
            final List<SubstanceAdministrationEvent> substanceAdministrationEvent)
    {
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents substanceAdministrationEvents =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents();
        if (substanceAdministrationEvent != null)
            substanceAdministrationEvents.getSubstanceAdministrationEvent().addAll(substanceAdministrationEvent);
        return substanceAdministrationEvents;
    }

    private CDSInput createCdsInput(final List<II> templateId, final CDSContext cdsContext, final VMR vmrInput)
    {
        final CDSInput cdsInput = new CDSInput();
        if (templateId != null)
            cdsInput.getTemplateId().addAll(templateId);
        cdsInput.setCdsContext(cdsContext);
        cdsInput.setVmrInput(vmrInput);
        return cdsInput;
    }

    private CDSContext createCdsContext(final CD cdsSystemUserPreferredLanguage)
    {
        final CDSContext cdsContext = new CDSContext();
        cdsContext.setCdsSystemUserPreferredLanguage(cdsSystemUserPreferredLanguage);
        return cdsContext;
    }

    private VMR createVmr(final List<II> templateId, final EvaluatedPerson patient)
    {
        final VMR vmr = new VMR();
        if (templateId != null)
            vmr.getTemplateId().addAll(templateId);
        vmr.setPatient(patient);
        return vmr;
    }

    private II createIi(final String root, final String extension)
    {
        final II ii = new II();
        ii.setRoot(root);
        ii.setExtension(extension);
        return ii;
    }

    private II copyIi(final II prototype)
    {
        return createIi(prototype.getRoot(), prototype.getExtension());
    }

    private CD copyCd(final CD prototype)
    {
        return createDisplayCd(prototype.getCode(), prototype.getCodeSystem(), prototype.getDisplayName());
    }

    private CD createCd(final String kmId, final CodeableConcept codeableConcept)
    {
        final CD cd = new CD();
        if (codeableConcept != null && !ObjectUtils.isEmpty(codeableConcept.coding()))
        {
            codeableConcept.coding().stream().findFirst().ifPresent((coding) ->
            {
                cd.setCode(coding.code());
                cd.setCodeSystem(supportingDataService.toRequiredInternalCodeSystemOid(kmId, coding.system()));
                cd.setDisplayName(StringUtils.hasText(coding.display()) ? coding.display() : codeableConcept.text());
            });
        }
        else
        {
            log.warn("Unable to convert codeable concept to CD: {}", codeableConcept);
        }

        return cd;
    }

    private ObservationResult createObservationResult(final String kmId, final Observation observation)
    {
        final ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(copyIi(templateIdObservationResultPrototype));
        observationResult.setId(createIi(UUID.randomUUID().toString(), null));
        observationResult.setObservationEventTime(Optional.ofNullable(observation.effectiveDateTime())
                .map(this::parseIsoLocalDate)
                .map(FhirToVmrInputAdapter::formatVmrDate)
                .map(d -> createIvlts(d, d))
                .orElse(null));
        observationResult.setObservationFocus(createCd(kmId, observation.code()));
        observationResult.getInterpretation()
                .addAll(Optional.ofNullable(observation.interpretation())
                        .stream()
                        .flatMap(Collection::stream)
                        .map(codeableConcept -> createObservationInterpretationCd(kmId, codeableConcept))
                        .toList());
        final ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        observationResult.setObservationValue(observationValue);
        observationValue.setConcept(createObservationValueCd(kmId, observation.valueCodeableConcept()));
        return observationResult;
    }

    private ObservationResult createScheduleFlagObservationResult(final String kmId, final String scheduleFlag)
    {
        final CodeableConcept scheduleFlagConcept =
                supportingDataService.getCodeableConcept(kmId, scheduleFlag, null, SCHEDULE_FLAGS_CODE_SYSTEM_OID, null);
        final CD scheduleFlagCd = createCd(kmId, scheduleFlagConcept);

        final ObservationResult observationResult = new ObservationResult();
        observationResult.getTemplateId().add(copyIi(templateIdObservationResultPrototype));
        observationResult.setId(createIi(UUID.randomUUID().toString(), null));
        observationResult.setObservationFocus(scheduleFlagCd);

        final ObservationResult.ObservationValue observationValue = new ObservationResult.ObservationValue();
        final BL value = new BL();
        value.setValue(true);
        observationValue.setBoolean(value);
        observationResult.setObservationValue(observationValue);

        return observationResult;
    }

    private CD createObservationValueCd(final String kmId, final CodeableConcept valueCodeableConcept)
    {
        final CD cd = createCd(kmId, valueCodeableConcept);
        if (!StringUtils.hasText(cd.getCode()) || !StringUtils.hasText(cd.getCodeSystem()))
            return cd;

        cd.setCodeSystem(supportingDataService.normalizeObservationValueCodeSystemOid(kmId, cd.getCodeSystem(), cd.getCode()));

        return cd;
    }

    private CD createObservationInterpretationCd(final String kmId, final CodeableConcept interpretationCodeableConcept)
    {
        final CD cd = createCd(kmId, interpretationCodeableConcept);
        if (!StringUtils.hasText(cd.getCode()))
            return cd;

        cd.setCodeSystem(
                supportingDataService.normalizeObservationInterpretationCodeSystemOid(kmId, cd.getCodeSystem(), cd.getCode()));

        return cd;
    }

    private IVLTS createIvlts(final String high, final String low)
    {
        final IVLTS ivlts = new IVLTS();
        ivlts.setHigh(high);
        ivlts.setLow(low);
        return ivlts;
    }

    private SubstanceAdministrationEvent createSubstanceAdministrationEvent(final String kmId, final Immunization immunization)
    {
        final SubstanceAdministrationEvent substanceAdministrationEvent = new SubstanceAdministrationEvent();
        substanceAdministrationEvent.getTemplateId().add(copyIi(templateIdSubstanceAdministrationEventPrototype));
        substanceAdministrationEvent.setSubstanceAdministrationGeneralPurpose(
                copyCd(substanceAdministrationGeneralPurposeCdPrototype));
        final String immunizationIdentifier = extractPrimaryIdentifierValue(immunization.identifier());
        final String eventIdentifier = StringUtils.hasText(immunizationIdentifier)
                                       ? immunizationIdentifier
                                       : StringUtils.hasText(immunization.id()) ? immunization.id() : UUID.randomUUID().toString();
        substanceAdministrationEvent.setId(createIi(IMMUNIZATION_ID_ROOT, eventIdentifier));
        substanceAdministrationEvent.setAdministrationTimeInterval(Optional.ofNullable(immunization.occurrenceDateTime())
                .map(this::parseIsoLocalDate)
                .map(FhirToVmrInputAdapter::formatVmrDate)
                .map(d -> createIvlts(d, d))
                .orElse(null));
        if (Boolean.TRUE.equals(immunization.isSubpotent()))
        {
            final BL isValid = new BL();
            isValid.setValue(false);
            substanceAdministrationEvent.setIsValid(isValid);
        }
        substanceAdministrationEvent.setSubstance(createAdministrableSubstance(createIi(UUID.randomUUID().toString(), null),
                createCd(kmId, immunization.vaccineCode())));
        return substanceAdministrationEvent;
    }

    private AdministrableSubstance createAdministrableSubstance(final II id, final CD substanceCode)
    {
        final AdministrableSubstance administrableSubstance = new AdministrableSubstance();
        administrableSubstance.setId(id);
        administrableSubstance.setSubstanceCode(substanceCode);
        return administrableSubstance;
    }

    private String toVmrAdministrativeGenderCode(final AdministrativeGender administrativeGender)
    {
        if (administrativeGender == null)
            return null;
        return switch (administrativeGender)
        {
            case MALE -> ADMIN_GENDER_CODE_MALE;
            case FEMALE -> ADMIN_GENDER_CODE_FEMALE;
            case OTHER, UNKNOWN -> ADMIN_GENDER_CODE_UNKNOWN;
        };
    }

    private String extractPrimaryIdentifierValue(final List<Identifier> identifiers)
    {
        return Optional.ofNullable(identifiers)
                .stream()
                .flatMap(Collection::stream)
                .map(Identifier::value)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private LocalDate parseIsoLocalDate(final String value)
    {
        return ConversionDateSupport.parseIsoLocalDate(value);
    }
}
