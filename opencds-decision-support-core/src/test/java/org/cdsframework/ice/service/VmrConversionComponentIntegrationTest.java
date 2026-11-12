package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.cdsframework.fhir.AdministrativeGender;
import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.CodeSystemContentModeEnum;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.GuidanceResponse;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Immunization;
import org.cdsframework.fhir.ImmunizationEvaluation;
import org.cdsframework.fhir.ImmunizationRecommendation;
import org.cdsframework.fhir.Observation;
import org.cdsframework.fhir.OperationOutcome;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.fhir.Patient;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.fhir.PublicationStatusEnum;
import org.cdsframework.fhir.Reference;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.conversion.FhirToVmrInputAdapter;
import org.cdsframework.ice.service.conversion.ScheduleAuthorityExtensionBuilder;
import org.cdsframework.ice.service.conversion.SelectionContextExtensionBuilder;
import org.cdsframework.ice.service.conversion.VaccineGroupRulesArtifactExtensionBuilder;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.FinalKMEvaluationResponse;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.SemanticPayload;
import org.opencds.vmr.v1_0.schema.AdministrableSubstance;
import org.opencds.vmr.v1_0.schema.CD;
import org.opencds.vmr.v1_0.schema.CDSOutput;
import org.opencds.vmr.v1_0.schema.EvaluatedPerson;
import org.opencds.vmr.v1_0.schema.II;
import org.opencds.vmr.v1_0.schema.ObservationResult;
import org.opencds.vmr.v1_0.schema.RelatedClinicalStatement;
import org.opencds.vmr.v1_0.schema.ST;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.schema.VMR;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.ObjectUtils;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

class VmrConversionComponentIntegrationTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String KNOWLEDGE_BASE = "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String VACCINE_GROUP_OID = "2.16.840.1.113883.3.795.12.100.1";
    private static final String SERIES_OPTIONS_OID = "2.16.840.1.113883.3.795.12.100.500";
    private static final String SERIES_SELECTION_TYPE_OID = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SCHEDULE_FLAGS_OID = "2.16.840.1.113883.3.795.12.100.502";
    private static final String SERIES_OID = "2.16.840.1.113883.3.795.12.100.10";
    private static final String SEASON_OID = "2.16.840.1.113883.3.795.12.100.11";
    private static final String RECOMMENDATION_REASON_OID = "2.16.840.1.113883.3.795.12.100.6";
    private static final String SELECTION_CONTEXT_EXTENSION_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/immunization-selection-context";
    private static final String SCHEDULE_AUTHORITY_EXTENSION_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/ice-schedule-authority";

    private static CdsEngineProperties createCdsEngineProperties()
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setKnowledgeBaseDefinitionMap(Map.of(KNOWLEDGE_BASE, createKnowledgeBaseDefinition()));
        return properties;
    }

    private static IceProperties createIceProperties(final boolean outputSeriesInformationForBothOutputs,
            final boolean outputNumberOfDosesRemaining, final boolean outputScheduleAuthorities)
    {
        final IceProperties properties = new IceProperties();
        properties.setIceBaseKnowledgeBase(KNOWLEDGE_BASE);
        properties.setKnowledgeBases(Map.of(KNOWLEDGE_BASE,
                new IceProperties.KnowledgeBaseProperties(true, false, true, outputNumberOfDosesRemaining,
                        outputSeriesInformationForBothOutputs, outputSeriesInformationForBothOutputs, outputScheduleAuthorities,
                        false, false, List.of(), List.of(), false, IceProperties.SupplementalTextMode.LEGACY,
                        new ByteArrayResource(new byte[0]))));
        return properties;
    }

    private static IceProperties createIceProperties(final boolean outputSeriesInformationForBothOutputs,
            final boolean outputNumberOfDosesRemaining)
    {
        return createIceProperties(outputSeriesInformationForBothOutputs, outputNumberOfDosesRemaining, false);
    }

    private static CodeSystem simpleCodeSystem(final String name, final String oid, final String url,
            final CodeSystemConcept... concepts)
    {
        final CodeSystem.CodeSystemBuilder builder = CodeSystem.builder()
                .name(name)
                .identifier(Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:%s".formatted(oid)).build())
                .url(url)
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE);
        if (concepts != null)
        {
            for (final CodeSystemConcept concept : concepts)
                builder.concept(concept);
        }
        return builder.build();
    }

    private static CdsEngineProperties.KnowledgeBaseDefinition createKnowledgeBaseDefinition()
    {
        return new CdsEngineProperties.KnowledgeBaseDefinition(PlanDefinition.builder()
                .identifier(Identifier.builder()
                        .system("https://terminology.cdsframework.org/ice/identifiers/knowledge-bases")
                        .value(KM_ID)
                        .build())
                .build(), Map.of(), Map.of(), Map.of("VACCINE_GROUP_CONCEPT",
                simpleCodeSystem("VACCINE_GROUP_CONCEPT", VACCINE_GROUP_OID,
                        "https://terminology.cdsframework.org/ice/vaccine-group",
                        CodeSystemConcept.builder().code("100").display("Test Vaccine Group").build()), "SUPPORTED_SERIES",
                simpleCodeSystem("SUPPORTED_SERIES", SERIES_OID, "https://terminology.cdsframework.org/ice/series",
                        CodeSystemConcept.builder().code("ZOSTER_SERIES").display("Zoster Series").build()),
                "SERIES_DISPLAY_SELECTION_TYPE", simpleCodeSystem("SERIES_DISPLAY_SELECTION_TYPE", SERIES_SELECTION_TYPE_OID,
                        "https://terminology.cdsframework.org/ice/series-display-selection-type", CodeSystemConcept.builder()
                                .code("SERIES_DISPLAY_UNAMBIGUOUS")
                                .display("Series Selected for Display")
                                .build()), "SUPPORTED_SEASON",
                simpleCodeSystem("SUPPORTED_SEASON", SEASON_OID, "https://terminology.cdsframework.org/ice/season",
                        CodeSystemConcept.builder().code("SEASON_2026_2027").display("2026-2027 Season").build()),
                "RECOMMENDATION_REASON_CONCEPT", simpleCodeSystem("RECOMMENDATION_REASON_CONCEPT", RECOMMENDATION_REASON_OID,
                        "https://terminology.cdsframework.org/ice/recommendation-reason",
                        CodeSystemConcept.builder().code("RECOMMENDED").display("Recommended").build(),
                        CodeSystemConcept.builder().code("DUE_NOW").display("Due Now").build()),
                "SUPPORTED_CONTEXTUAL_CONDITION_CODES",
                simpleCodeSystem("SUPPORTED_CONTEXTUAL_CONDITION_CODES", "2.16.840.1.113883.3.795.12.100.52",
                        "https://ivci.org/contextual-conditions/usa/cdc-cdsi"), "SUPPORTED_VACCINES",
                simpleCodeSystem("SUPPORTED_VACCINES", "2.16.840.1.113883.12.292", "http://hl7.org/fhir/sid/cvx",
                        CodeSystemConcept.builder()
                                .code("10")
                                .display("IPV")
                                .property(CodeSystemConceptProperty.builder()
                                        .code("diseaseImmunity")
                                        .valueCoding(Coding.builder().code("Polio").build())
                                        .build())
                                .build(), CodeSystemConcept.builder()
                                .code("24")
                                .display("Anthrax, pre-exposure prophylaxis, post-exposure prophylaxis")
                                .property(CodeSystemConceptProperty.builder().code("supported").valueBoolean(false).build())
                                .build()), "SUPPORTED_DISEASE_CONCEPT",
                simpleCodeSystem("SUPPORTED_DISEASE_CONCEPT", "2.16.840.1.113883.3.795.12.100.2",
                        "https://terminology.cdsframework.org/ice/disease", CodeSystemConcept.builder()
                                .code("Polio")
                                .display("Polio")
                                .property(CodeSystemConceptProperty.builder()
                                        .code("outboundCode")
                                        .valueCoding(Coding.builder()
                                                .system("2.16.840.1.113883.6.96")
                                                .code("398102009")
                                                .display("Polio")
                                                .build())
                                        .build())
                                .build()), "SUPPORTED_SCHEDULE_FLAGS",
                simpleCodeSystem("SUPPORTED_SCHEDULE_FLAGS", SCHEDULE_FLAGS_OID,
                        "https://terminology.cdsframework.org/ice/schedule-flags", CodeSystemConcept.builder()
                                .code("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                                .display("Evaluate Invalid 3rd Hep B Dose as Accepted Extra Dose")
                                .build())),
                Map.of("2.16.840.1.113883.6.96", "http://snomed.info/sct", "2.16.840.1.113883.6.1", "http://loinc.org",
                        "2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm", "2.16.840.1.113883.6.90",
                        "http://hl7.org/fhir/sid/icd-10-cm", "2.16.840.1.113883.6.3", "http://hl7.org/fhir/sid/icd-10",
                        "2.16.840.1.113883.3.795.12.100.4", "https://terminology.cdsframework.org/ice/unknown",
                        "2.16.840.1.113883.3.795.12.100.500", "https://terminology.cdsframework.org/ice/series-display-options"));
    }

    private static VmrConversionComponent createVmrConversionComponent(final SupportingDataService supportingDataService,
            final IceProperties iceProperties)
    {
        return new VmrConversionComponent(supportingDataService, iceProperties, new FhirToVmrInputAdapter(supportingDataService),
                new SelectionContextExtensionBuilder(supportingDataService), new VaccineGroupRulesArtifactExtensionBuilder(),
                new ScheduleAuthorityExtensionBuilder());
    }

    private static Parameters createRequestParameters()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical(KNOWLEDGE_BASE).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .build();
    }

    private static Parameters createRequestParametersWithUnsupportedCvx()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical(KNOWLEDGE_BASE).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("i-1")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("999")
                                                .display("Unsupported")
                                                .build())
                                        .text("Unsupported")
                                        .build())
                                .occurrenceDateTime("2026-04-01")
                                .build())
                        .build())
                .build();
    }

    private static Parameters createRequestParametersWithUnsupportedCodeSystem()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical(KNOWLEDGE_BASE).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("i-2")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvxs")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .occurrenceDateTime("2026-04-01")
                                .build())
                        .build())
                .build();
    }

    private static Parameters createRequestParametersWithConfiguredUnsupportedCvx()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical(KNOWLEDGE_BASE).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("i-24")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("24")
                                                .display("Anthrax, pre-exposure prophylaxis, post-exposure prophylaxis")
                                                .build())
                                        .text("Anthrax, pre-exposure prophylaxis, post-exposure prophylaxis")
                                        .build())
                                .occurrenceDateTime("2026-04-01")
                                .build())
                        .build())
                .build();
    }

    private static Parameters createRequestParametersWithUnsupportedContextualCondition()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical(KNOWLEDGE_BASE).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("https://ivci.org/contextual-conditions/usa/cdc-cdsi")
                                                .code("999")
                                                .display("Unsupported contextual condition")
                                                .build())
                                        .build())
                                .valueDateTime("2026-04-01")
                                .build())
                        .build())
                .build();
    }

    private static Parameters createRequestParametersWithUnsupportedCvxAndCodeSystem()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("knowledgeBase").valueCanonical(KNOWLEDGE_BASE).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("1990-01-01"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("i-warning")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("999")
                                                .display("Unsupported")
                                                .build())
                                        .text("Unsupported")
                                        .build())
                                .occurrenceDateTime("2026-04-01")
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("i-error")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvxs")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .occurrenceDateTime("2026-04-01")
                                .build())
                        .build())
                .build();
    }

    private static CD createCd(final String code, final String codeSystem, final String display)
    {
        final CD cd = new CD();
        cd.setCode(code);
        cd.setCodeSystem(codeSystem);
        cd.setDisplayName(display);
        return cd;
    }

    private static RelatedClinicalStatement asRelatedObservation(final ObservationResult observationResult)
    {
        final RelatedClinicalStatement related = new RelatedClinicalStatement();
        related.setObservationResult(observationResult);
        return related;
    }

    private static ObservationResult createSeriesDisplayOptionsObservation()
    {
        return createSeriesDisplayOptionsObservation("2");
    }

    private static ObservationResult createSeriesDisplayOptionsObservation(final String dosesRemainingValue)
    {
        final ObservationResult dosesRemaining = new ObservationResult();
        dosesRemaining.setObservationFocus(createCd("NUMBER_OF_DOSES_REMAINING", SERIES_OID, "Doses Remaining"));
        final ObservationResult.ObservationValue dosesValue = new ObservationResult.ObservationValue();
        final ST st = new ST();
        st.setValue(dosesRemainingValue);
        dosesValue.setText(st);
        dosesRemaining.setObservationValue(dosesValue);

        final ObservationResult seriesSelection = new ObservationResult();
        seriesSelection.setObservationFocus(createCd("SERIES_DISPLAY_UNAMBIGUOUS", SERIES_SELECTION_TYPE_OID, null));
        final ObservationResult.ObservationValue seriesSelectionValue = new ObservationResult.ObservationValue();
        seriesSelectionValue.setConcept(createCd("ZOSTER_SERIES", SERIES_OID, "Zoster Series"));
        seriesSelection.setObservationValue(seriesSelectionValue);
        seriesSelection.getRelatedClinicalStatement().add(asRelatedObservation(dosesRemaining));

        final ObservationResult season = new ObservationResult();
        season.setObservationFocus(createCd("SEASON", SEASON_OID, null));
        final ObservationResult.ObservationValue seasonValue = new ObservationResult.ObservationValue();
        seasonValue.setConcept(createCd("SEASON_2026_2027", SEASON_OID, "2026-2027 Season"));
        season.setObservationValue(seasonValue);

        final ObservationResult seriesOptions = new ObservationResult();
        seriesOptions.setObservationFocus(createCd("SERIES_DISPLAY_OPTIONS", SERIES_OPTIONS_OID, null));
        seriesOptions.getRelatedClinicalStatement().add(asRelatedObservation(seriesSelection));
        seriesOptions.getRelatedClinicalStatement().add(asRelatedObservation(season));
        return seriesOptions;
    }

    private static ObservationResult createPrimaryRecommendationObservation()
    {
        final ObservationResult primary = new ObservationResult();
        primary.setObservationFocus(createCd("100", VACCINE_GROUP_OID, "Test Vaccine Group"));
        final ObservationResult.ObservationValue value = new ObservationResult.ObservationValue();
        value.setConcept(createCd("RECOMMENDED", RECOMMENDATION_REASON_OID, "Recommended"));
        primary.setObservationValue(value);
        primary.getInterpretation().add(createCd("DUE_NOW", RECOMMENDATION_REASON_OID, "Due Now"));
        return primary;
    }

    private static SubstanceAdministrationProposal createProposalWithSeriesContext()
    {
        return createProposalWithSeriesContext("2");
    }

    private static SubstanceAdministrationProposal createProposalWithSeriesContext(final String dosesRemainingValue)
    {
        final SubstanceAdministrationProposal proposal = new SubstanceAdministrationProposal();
        final II id = new II();
        id.setExtension("rec-1");
        proposal.setId(id);
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createPrimaryRecommendationObservation()));
        proposal.getRelatedClinicalStatement()
                .add(asRelatedObservation(createSeriesDisplayOptionsObservation(dosesRemainingValue)));
        return proposal;
    }

    private static SubstanceAdministrationProposal createSecondProposalWithSeriesContext()
    {
        final SubstanceAdministrationProposal proposal = new SubstanceAdministrationProposal();
        final II id = new II();
        id.setExtension("rec-2");
        proposal.setId(id);
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createPrimaryRecommendationObservation()));
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(createSeriesDisplayOptionsObservation()));
        return proposal;
    }

    private static SubstanceAdministrationEvent createEventWithSeriesContext(final String dosesRemainingValue)
    {
        return createEventWithSeriesContext(dosesRemainingValue, "10");
    }

    private static SubstanceAdministrationEvent createEventWithSeriesContext(final String dosesRemainingValue, final String cvx)
    {
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        final II id = new II();
        id.setExtension("imm-1");
        event.setId(id);
        final AdministrableSubstance substance = new AdministrableSubstance();
        substance.setSubstanceCode(createCd(cvx, "2.16.840.1.113883.12.292", "Evaluated vaccine component"));
        event.setSubstance(substance);
        event.getRelatedClinicalStatement().add(asRelatedObservation(createPrimaryRecommendationObservation()));
        event.getRelatedClinicalStatement().add(asRelatedObservation(createSeriesDisplayOptionsObservation(dosesRemainingValue)));
        return event;
    }

    private static EvaluationResponse createEvaluationResponseWithSeriesContext()
    {
        return createEvaluationResponseWithSeriesContext("2");
    }

    private static EvaluationResponse createEvaluationResponseWithSeriesContext(final String dosesRemainingValue)
    {
        return createEvaluationResponseWithSeriesContext(dosesRemainingValue, "10");
    }

    private static EvaluationResponse createEvaluationResponseWithSeriesContext(final String dosesRemainingValue, final String cvx)
    {
        final CDSOutput cdsOutput = new CDSOutput();
        final VMR vmrOutput = new VMR();
        final EvaluatedPerson person = new EvaluatedPerson();
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals proposals =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals();
        proposals.getSubstanceAdministrationProposal().add(createProposalWithSeriesContext(dosesRemainingValue));
        clinicalStatements.setSubstanceAdministrationProposals(proposals);
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents events =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents();
        events.getSubstanceAdministrationEvent().add(createEventWithSeriesContext(dosesRemainingValue, cvx));
        clinicalStatements.setSubstanceAdministrationEvents(events);
        person.setClinicalStatements(clinicalStatements);
        vmrOutput.setPatient(person);
        cdsOutput.setVmrOutput(vmrOutput);

        final XmlMapper xmlMapper = XmlMapper.xmlBuilder().defaultUseWrapper(false).findAndAddModules().build();
        final byte[] payload = xmlMapper.writeValueAsBytes(cdsOutput);

        final SemanticPayload semanticPayload = new SemanticPayload();
        semanticPayload.getBase64EncodedPayload().add(payload);
        final KMEvaluationResultData resultData = new KMEvaluationResultData();
        resultData.setData(semanticPayload);
        final FinalKMEvaluationResponse finalResponse = new FinalKMEvaluationResponse();
        finalResponse.getKmEvaluationResultData().add(resultData);
        final EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.getFinalKMEvaluationResponse().add(finalResponse);
        return evaluationResponse;
    }

    private static ObservationResult createScheduleAuthoritiesObservation()
    {
        final ObservationResult observation = new ObservationResult();
        final CD focus = new CD();
        focus.setCode("ICE_VACCINE_GROUP_SCHEDULE_AUTHORITIES");
        focus.setCodeSystem(SERIES_OPTIONS_OID);
        observation.setObservationFocus(focus);

        final CD acip = new CD();
        acip.setCode("ACIP");
        acip.setDisplayName("Advisory Committee on Immunization Practices");
        final CD cdc = new CD();
        cdc.setCode("CDC");
        cdc.setDisplayName("Centers for Disease Control and Prevention");
        observation.getInterpretation().add(acip);
        observation.getInterpretation().add(cdc);
        return observation;
    }

    private static EvaluationResponse createEvaluationResponseWithScheduleAuthorities()
    {
        return createEvaluationResponseWithScheduleAuthorities("ACIP", "CDC");
    }

    private static EvaluationResponse createEvaluationResponseWithScheduleAuthorities(final String... authorityCodes)
    {
        final SubstanceAdministrationProposal proposal = createProposalWithSeriesContext();
        final ObservationResult authorityObservation = createScheduleAuthoritiesObservation();
        final List<CD> availableAuthorities = List.copyOf(authorityObservation.getInterpretation());
        authorityObservation.getInterpretation().clear();
        for (final String code : authorityCodes)
            authorityObservation.getInterpretation()
                    .add(availableAuthorities.stream()
                            .filter(authority -> code.equals(authority.getCode()))
                            .findFirst()
                            .orElseThrow());
        proposal.getRelatedClinicalStatement().add(asRelatedObservation(authorityObservation));
        final CDSOutput cdsOutput = new CDSOutput();
        final VMR vmrOutput = new VMR();
        final EvaluatedPerson person = new EvaluatedPerson();
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals proposals =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals();
        proposals.getSubstanceAdministrationProposal().add(proposal);
        final SubstanceAdministrationEvent event = createEventWithSeriesContext("2");
        event.getRelatedClinicalStatement().add(asRelatedObservation(authorityObservation));
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents events =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationEvents();
        events.getSubstanceAdministrationEvent().add(event);
        clinicalStatements.setSubstanceAdministrationEvents(events);
        clinicalStatements.setSubstanceAdministrationProposals(proposals);
        person.setClinicalStatements(clinicalStatements);
        vmrOutput.setPatient(person);
        cdsOutput.setVmrOutput(vmrOutput);

        final XmlMapper xmlMapper = XmlMapper.xmlBuilder().defaultUseWrapper(false).findAndAddModules().build();
        final SemanticPayload semanticPayload = new SemanticPayload();
        semanticPayload.getBase64EncodedPayload().add(xmlMapper.writeValueAsBytes(cdsOutput));
        final KMEvaluationResultData resultData = new KMEvaluationResultData();
        resultData.setData(semanticPayload);
        final FinalKMEvaluationResponse finalResponse = new FinalKMEvaluationResponse();
        finalResponse.getKmEvaluationResultData().add(resultData);
        final EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.getFinalKMEvaluationResponse().add(finalResponse);
        return evaluationResponse;
    }

    private static EvaluationResponse createEvaluationResponseWithTwoRecommendations()
    {
        final CDSOutput cdsOutput = new CDSOutput();
        final VMR vmrOutput = new VMR();
        final EvaluatedPerson person = new EvaluatedPerson();
        final EvaluatedPerson.ClinicalStatements clinicalStatements = new EvaluatedPerson.ClinicalStatements();
        final EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals proposals =
                new EvaluatedPerson.ClinicalStatements.SubstanceAdministrationProposals();
        proposals.getSubstanceAdministrationProposal().add(createProposalWithSeriesContext());
        proposals.getSubstanceAdministrationProposal().add(createSecondProposalWithSeriesContext());
        clinicalStatements.setSubstanceAdministrationProposals(proposals);
        person.setClinicalStatements(clinicalStatements);
        vmrOutput.setPatient(person);
        cdsOutput.setVmrOutput(vmrOutput);

        final XmlMapper xmlMapper = XmlMapper.xmlBuilder().defaultUseWrapper(false).findAndAddModules().build();
        final byte[] payload = xmlMapper.writeValueAsBytes(cdsOutput);

        final SemanticPayload semanticPayload = new SemanticPayload();
        semanticPayload.getBase64EncodedPayload().add(payload);
        final KMEvaluationResultData resultData = new KMEvaluationResultData();
        resultData.setData(semanticPayload);
        final FinalKMEvaluationResponse finalResponse = new FinalKMEvaluationResponse();
        finalResponse.getKmEvaluationResultData().add(resultData);
        final EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.getFinalKMEvaluationResponse().add(finalResponse);
        return evaluationResponse;
    }

    private static ImmunizationRecommendation firstRecommendation(final Parameters response)
    {
        return streamParameters(response).filter(param -> "recommendation".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(ImmunizationRecommendation.class::isInstance)
                .map(ImmunizationRecommendation.class::cast)
                .findFirst()
                .orElseThrow();
    }

    private static List<ImmunizationRecommendation> recommendationResources(final Parameters response)
    {
        return streamParameters(response).filter(param -> "recommendation".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(ImmunizationRecommendation.class::isInstance)
                .map(ImmunizationRecommendation.class::cast)
                .toList();
    }

    private static ImmunizationEvaluation firstEvaluation(final Parameters response)
    {
        return streamParameters(response).filter(param -> "evaluation".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(ImmunizationEvaluation.class::isInstance)
                .map(ImmunizationEvaluation.class::cast)
                .findFirst()
                .orElseThrow();
    }

    private static OperationOutcome operationOutcome(final Parameters response)
    {
        return streamParameters(response).filter(param -> "operationOutcome".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(OperationOutcome.class::isInstance)
                .map(OperationOutcome.class::cast)
                .findFirst()
                .orElseThrow();
    }

    private static GuidanceResponse guidanceResponse(final Parameters response)
    {
        return streamParameters(response).filter(param -> "guidanceResponse".equals(param.name()))
                .map(ParametersParameter::resource)
                .filter(GuidanceResponse.class::isInstance)
                .map(GuidanceResponse.class::cast)
                .findFirst()
                .orElseThrow();
    }

    private static Stream<ParametersParameter> streamParameters(final Parameters parameters)
    {
        return Optional.ofNullable(parameters)
                .map(Parameters::parameter)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(VmrConversionComponentIntegrationTest::flattenParameterTree);
    }

    private static Stream<ParametersParameter> flattenParameterTree(final ParametersParameter parameter)
    {
        if (parameter == null)
            return Stream.empty();

        return Stream.concat(Stream.of(parameter), Optional.ofNullable(parameter.part())
                .stream()
                .flatMap(Collection::stream)
                .flatMap(VmrConversionComponentIntegrationTest::flattenParameterTree));
    }

    private static String informationalIssueDetailsText(final OperationOutcome operationOutcome)
    {
        return operationOutcome.issue()
                .stream()
                .filter(issue -> "information".equals(issue.severity()) && "informational".equals(issue.code()))
                .map(OperationOutcome.Issue::details)
                .map(CodeableConcept::text)
                .findFirst()
                .orElseThrow();
    }

    /** A converter whose vaccine group 100 has the group diseases and whose one vaccine, the CVX, has the vaccine diseases. */
    private static VmrConversionComponent createDiseaseMappingConverter(final String vaccineGroup, final String cvx,
            final List<String> groupDiseaseNames, final List<String> groupDiseaseCodes, final List<String> diseaseNames)
    {
        final CdsEngineProperties.KnowledgeBaseDefinition base = createKnowledgeBaseDefinition();
        final Map<String, CodeSystem> codeSystems = new HashMap<>(base.codeSystems());
        final CodeSystemConcept.CodeSystemConceptBuilder group = CodeSystemConcept.builder().code("100").display(vaccineGroup);
        final List<CodeSystemConcept> diseases = new ArrayList<>();
        for (int i = 0; i < groupDiseaseNames.size(); i++)
        {
            final String disease = groupDiseaseNames.get(i);
            group.property(CodeSystemConceptProperty.builder()
                    .code("diseaseImmunity")
                    .valueCoding(Coding.builder().code(disease).build())
                    .build());
            diseases.add(CodeSystemConcept.builder()
                    .code(disease)
                    .display(disease)
                    .property(CodeSystemConceptProperty.builder()
                            .code("outboundCode")
                            .valueCoding(Coding.builder()
                                    .system("2.16.840.1.113883.6.96")
                                    .code(groupDiseaseCodes.get(i))
                                    .display(disease)
                                    .build())
                            .build())
                    .build());
        }
        codeSystems.put("VACCINE_GROUP_CONCEPT", simpleCodeSystem("VACCINE_GROUP_CONCEPT", VACCINE_GROUP_OID,
                "https://terminology.cdsframework.org/ice/vaccine-group", group.build()));
        codeSystems.put("SUPPORTED_DISEASE_CONCEPT",
                simpleCodeSystem("SUPPORTED_DISEASE_CONCEPT", "2.16.840.1.113883.3.795.12.100.2",
                        "https://terminology.cdsframework.org/ice/disease", diseases.toArray(CodeSystemConcept[]::new)));
        final CodeSystemConcept.CodeSystemConceptBuilder vaccine = CodeSystemConcept.builder().code(cvx).display(vaccineGroup);
        for (final String disease : diseaseNames)
            vaccine.property(CodeSystemConceptProperty.builder()
                    .code("diseaseImmunity")
                    .valueCoding(Coding.builder().code(disease).build())
                    .build());
        codeSystems.put("SUPPORTED_VACCINES",
                simpleCodeSystem("SUPPORTED_VACCINES", "2.16.840.1.113883.12.292", "http://hl7.org/fhir/sid/cvx", vaccine.build()));
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setKnowledgeBaseDefinitionMap(Map.of(KNOWLEDGE_BASE,
                new CdsEngineProperties.KnowledgeBaseDefinition(base.knowledgeBasePlanDefinition(), base.planDefinitions(),
                        base.series(), codeSystems, base.outboundCodeSystemMap())));
        final IceProperties iceProperties = createIceProperties(false, false);
        return createVmrConversionComponent(new SupportingDataService(properties, iceProperties), iceProperties);
    }

    // Each disease list is separated by |: the vaccine group's diseases and codes, then the evaluated vaccine's.
    @ParameterizedTest(name = "{0} preserves one concept per disease")
    @CsvSource({ "MMR, 03, Measles|Mumps|Rubella, 14189004|36989005|36653000, Measles|Mumps|Rubella, 14189004|36989005|36653000",
            "DTP, 01, Diphtheria|Pertussis|Tetanus, 397428000|27836007|76902006, Diphtheria|Pertussis|Tetanus, 397428000|27836007|76902006",
            "MR, 04, Measles|Mumps|Rubella, 14189004|36989005|36653000, Measles|Rubella, 14189004|36653000",
            "DT, 28, Diphtheria|Pertussis|Tetanus, 397428000|27836007|76902006, Diphtheria|Tetanus, 397428000|76902006",
            "Polio, 10, Polio, 398102009, Polio, 398102009" })
    void preservesDistinctTargetDiseasesInEvaluationAndRecommendation(final String vaccineGroup, final String cvx,
            final String groupDiseaseNameList, final String groupDiseaseCodeList, final String diseaseNameList,
            final String diseaseCodeList)
    {
        final List<String> groupDiseaseNames = List.of(groupDiseaseNameList.split("\\|"));
        final List<String> groupDiseaseCodes = List.of(groupDiseaseCodeList.split("\\|"));
        final List<String> diseaseNames = List.of(diseaseNameList.split("\\|"));
        final List<String> diseaseCodes = List.of(diseaseCodeList.split("\\|"));
        final VmrConversionComponent converter =
                createDiseaseMappingConverter(vaccineGroup, cvx, groupDiseaseNames, groupDiseaseCodes, diseaseNames);

        final Parameters response = converter.convertToParametersResponse(createEvaluationResponseWithSeriesContext("2", cvx),
                createRequestParameters(), LocalDateTime.now());
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final List<CodeableConcept> targetDiseases = evaluation.targetDisease();
        assertEquals(diseaseNames, targetDiseases.stream().map(CodeableConcept::text).toList());
        assertEquals(diseaseCodes, targetDiseases.stream().map(concept -> concept.coding().getFirst().code()).toList());
        assertTrue(targetDiseases.stream()
                .allMatch(concept -> concept.coding().size() == 1 && "http://snomed.info/sct".equals(
                        concept.coding().getFirst().system())));
        assertEquals(groupDiseaseNames, firstRecommendation(response).recommendation()
                .getFirst()
                .targetDisease()
                .stream()
                .map(CodeableConcept::text)
                .toList());
        assertTrue(evaluation.text().div().contains(String.join(", ", diseaseNames)));
        assertEquals(1, streamParameters(response).filter(param -> param.resource() instanceof ImmunizationEvaluation).count());

        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final var json = mapper.readTree(mapper.writeValueAsString(evaluation));
        assertTrue(json.get("targetDisease").isArray());
        assertEquals(diseaseNames.size(), json.get("targetDisease").size());
        for (int i = 0; i < diseaseNames.size(); i++)
        {
            assertEquals(1, json.get("targetDisease").get(i).get("coding").size());
            assertEquals(diseaseCodes.get(i), json.get("targetDisease").get(i).get("coding").get(0).get("code").asString());
        }
    }

    @Test
    void usesTheVaccineGroupsDiseasesWhenTheEvaluatedCvxHasNoDiseaseMapping()
    {
        // A supporting-data gap for one vaccine must not fail the whole forecast.
        final VmrConversionComponent converter = createDiseaseMappingConverter("MMR", "03", List.of("Measles", "Mumps", "Rubella"),
                List.of("14189004", "36989005", "36653000"), List.of());

        final Parameters response = converter.convertToParametersResponse(createEvaluationResponseWithSeriesContext("2", "03"),
                createRequestParameters(), LocalDateTime.now());

        assertEquals(List.of("Measles", "Mumps", "Rubella"),
                firstEvaluation(response).targetDisease().stream().map(CodeableConcept::text).toList());
        assertEquals(1, firstRecommendation(response).recommendation().size());
    }

    @Test
    void rejectsEvaluationWithoutATargetDisease()
    {
        assertThrows(IllegalArgumentException.class, () -> ImmunizationEvaluation.builder().build());
    }

    @Test
    void doesNotEmitSeriesSeasonOrDosesWhenOutputFlagsAreDisabled()
    {
        final IceProperties iceProperties = createIceProperties(false, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertTrue(response.parameter().stream().noneMatch(param -> "assessmentDate".equals(param.name())));
        assertEquals(LocalDate.parse("2026-04-09"), recommendation.date());
        assertNull(rec.series());
        assertNull(evaluation.series());
        assertNull(evaluation.season());
        assertNull(rec.season());
        assertNull(rec.seriesDosesRemaining());
        assertNull(rec.seriesDoses());
        assertTrue(ObjectUtils.isEmpty(rec.extension()));
        assertTrue(evaluation.extension().isEmpty());
        assertNotNull(rec.description());
        assertTrue(rec.description().startsWith("Recommendation status:"));
    }

    @ParameterizedTest
    @CsvSource(
            { "false,false,false", "true,false,false", "false,true,false", "false,true,true", "true,true,false", "true,true,true" })
    void independentlyControlsEvaluationAndRecommendationSeriesContext(final boolean evaluations, final boolean recommendations,
            final boolean doses)
    {
        final IceProperties properties = createIceProperties(false, doses);
        properties.setOutputSeriesInformationForEvaluations(Optional.of(evaluations));
        properties.setOutputSeriesInformationForRecommendations(Optional.of(recommendations));
        final SupportingDataService supportingData = new SupportingDataService(createCdsEngineProperties(), properties);
        properties.setSeriesOverrides(Map.of("ZOSTER_SERIES", new IceProperties.SeriesOverride(null, null, 3, null, null)));
        final VmrConversionComponent converter = createVmrConversionComponent(supportingData, properties);
        final Parameters response =
                converter.convertToParametersResponse(createEvaluationResponseWithSeriesContext(), createRequestParameters(),
                        LocalDateTime.now());
        final org.cdsframework.fhir.ImmunizationRecommendationRecommendation recommendation =
                firstRecommendation(response).recommendation().getFirst();
        final ImmunizationEvaluation evaluation = firstEvaluation(response);

        assertEquals(evaluations ? "Zoster Series" : null, evaluation.series() == null ? null : evaluation.series().text());
        assertEquals(recommendations ? "Zoster Series" : null,
                recommendation.series() == null ? null : recommendation.series().text());
        assertEquals(evaluations ? "2026-2027 Season" : null, evaluation.season() == null ? null : evaluation.season().text());
        assertEquals(recommendations ? "2026-2027 Season" : null,
                recommendation.season() == null ? null : recommendation.season().text());
        assertEquals(evaluations,
                evaluation.extension().stream().anyMatch(ext -> SELECTION_CONTEXT_EXTENSION_URL.equals(ext.url())));
        assertEquals(recommendations,
                recommendation.extension().stream().anyMatch(ext -> SELECTION_CONTEXT_EXTENSION_URL.equals(ext.url())));
        assertEquals(evaluations ? "3" : null, evaluation.seriesDoses() == null ? null : evaluation.seriesDoses().text());
        assertEquals(recommendations ? "3" : null,
                recommendation.seriesDoses() == null ? null : recommendation.seriesDoses().text());
        assertEquals(doses ? "2" : null,
                recommendation.seriesDosesRemaining() == null ? null : recommendation.seriesDosesRemaining().text());
        assertEquals(evaluations, evaluation.description().contains("Series: Zoster Series"));
        assertEquals(recommendations, recommendation.description().contains("Season: 2026-2027 Season"));
    }

    @Test
    void emitsSeriesSeasonAndDosesWhenOutputFlagsAreEnabled()
    {
        final IceProperties iceProperties = createIceProperties(true, true);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        iceProperties.setSeriesOverrides(Map.of("ZOSTER_SERIES", new IceProperties.SeriesOverride(null, null, 3, null, null)));
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertEquals(evaluation.series(), rec.series());
        assertEquals("Zoster Series", evaluation.series().text());
        assertEquals("ZOSTER_SERIES", evaluation.series().coding().getFirst().code());
        assertEquals("Zoster Series", evaluation.series().coding().getFirst().display());
        assertEquals("https://terminology.cdsframework.org/ice/series", evaluation.series().coding().getFirst().system());
        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final var evaluationJson = mapper.readTree(mapper.writeValueAsString(evaluation));
        assertTrue(evaluationJson.get("series").isObject());
        assertEquals("ZOSTER_SERIES", evaluationJson.get("series").get("coding").get(0).get("code").asString());
        assertEquals("Zoster Series", evaluationJson.get("series").get("text").asString());
        final var recommendationJson = mapper.readTree(mapper.writeValueAsString(recommendation));
        assertTrue(recommendationJson.get("recommendation").get(0).get("series").isObject());
        assertEquals("Zoster Series", recommendationJson.get("recommendation").get(0).get("series").get("text").asString());
        assertEquals("ZOSTER_SERIES",
                recommendationJson.get("recommendation").get(0).get("series").get("coding").get(0).get("code").asString());
        assertEquals(rec.season(), evaluation.season());
        assertEquals("2026-2027 Season", evaluation.season().text());
        assertEquals("SEASON_2026_2027", evaluation.season().coding().getFirst().code());
        assertEquals("https://terminology.cdsframework.org/ice/season", evaluation.season().coding().getFirst().system());
        assertTrue(evaluationJson.get("season").isObject());
        assertEquals("SEASON_2026_2027", evaluationJson.get("season").get("coding").get(0).get("code").asString());
        assertEquals(evaluationJson.get("season"), recommendationJson.get("recommendation").get(0).get("season"));
        assertEquals("2", recommendationJson.get("recommendation").get(0).get("seriesDosesRemaining").get("text").asString());
        assertNotNull(rec.seriesDosesRemaining());
        assertEquals("2", rec.seriesDosesRemaining().text());
        assertEquals(1, rec.extension().size());
        final var contextExtension = rec.extension().getFirst();
        assertEquals(SELECTION_CONTEXT_EXTENSION_URL, contextExtension.url());
        assertEquals(1, contextExtension.extension().size());
        assertEquals("seriesSelectionType", contextExtension.extension().getFirst().url());
        assertEquals("SERIES_DISPLAY_UNAMBIGUOUS",
                contextExtension.extension().getFirst().valueCodeableConcept().coding().getFirst().code());
        assertTrue(rec.extension()
                .stream()
                .flatMap(ext -> ext.extension().stream())
                .noneMatch(ext -> "selectedSeries".equals(ext.url()) || "selectedSeason".equals(ext.url())));
        assertEquals(rec.extension(), evaluation.extension());
        assertEquals(1, evaluation.extension().size());
        assertEquals(SELECTION_CONTEXT_EXTENSION_URL, evaluation.extension().getFirst().url());
        assertTrue(rec.description().contains("Series: Zoster Series"));
        assertTrue(rec.description().contains("Season: 2026-2027 Season"));
        assertTrue(rec.description().contains("Doses remaining in series: 2"), rec.description());
        // seriesDoses is the selected series' size; only recommendations have the engine's remaining count.
        assertEquals("3", rec.seriesDoses().text());
        assertEquals("3", evaluation.seriesDoses().text());
        assertFalse(evaluation.description().contains("remaining"), evaluation.description());
    }

    @Test
    void emitsZeroDosesRemainingOnlyAsSeriesDosesRemaining()
    {
        final IceProperties iceProperties = createIceProperties(true, true);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response =
                vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext("0"),
                        createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertNull(rec.seriesDoses());
        assertEquals("0", rec.seriesDosesRemaining().text());
        assertNull(evaluation.seriesDoses());
        assertTrue(rec.description().contains("Series: Zoster Series"));
        assertTrue(rec.description().contains("Season: 2026-2027 Season"));
        assertTrue(rec.description().contains("Doses remaining in series: 0"), rec.description());
    }

    @Test
    void emitsSingleRecommendationResourceWithNestedEntries()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response =
                vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithTwoRecommendations(),
                        createRequestParameters(), LocalDateTime.now());

        final List<ImmunizationRecommendation> recommendationResources = recommendationResources(response);
        assertEquals(1, recommendationResources.size());
        assertEquals(2, recommendationResources.getFirst().recommendation().size());
    }

    @Test
    void emitsWarningWhenUnsupportedCvxImmunizationIsIgnored()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParametersWithUnsupportedCvx(), LocalDateTime.now());
        final GuidanceResponse guidanceResponse = guidanceResponse(response);
        final OperationOutcome operationOutcome = operationOutcome(response);

        assertTrue(operationOutcome.issue()
                .stream()
                .anyMatch(issue -> "warning".equals(issue.severity()) && issue.details() != null && issue.details().text() != null
                        && issue.details().text().contains("Unsupported CVX code '999'") && issue.details()
                        .text()
                        .contains("not evaluated")));
        assertTrue(guidanceResponse.text().div().contains("generated successfully with warnings."));
        assertTrue(informationalIssueDetailsText(operationOutcome).contains("completed successfully using ICE"));
        assertTrue(informationalIssueDetailsText(operationOutcome).contains("with warnings."));
    }

    @Test
    void emitsWarningWhenUnsupportedContextualConditionIsIgnored()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);
        final Parameters request = createRequestParametersWithUnsupportedContextualCondition();

        final String vmrPayload = new String(vmrConversionComponent.convertImmDsToEvaluateAtSpecifiedTime(request)
                .getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst(), StandardCharsets.UTF_8);
        final Parameters response =
                vmrConversionComponent.convertImmDsToParametersResponse(createEvaluationResponseWithSeriesContext(), request,
                        LocalDateTime.now());
        final OperationOutcome operationOutcome = operationOutcome(response);

        assertFalse(vmrPayload.contains("code=\"999\""));
        assertTrue(operationOutcome.issue()
                .stream()
                .anyMatch(issue -> "warning".equals(issue.severity()) && issue.details() != null && issue.details().text() != null
                        && issue.details().text().contains("Unsupported contextual-condition code '999'") && issue.details()
                        .text()
                        .contains("observation was ignored")));
    }

    @Test
    void convertsImmDsScheduleFlag()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);
        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(createRequestParameters().parameter())
                .parameter(ParametersParameter.builder()
                        .name("scheduleFlag")
                        .valueCode("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .build())
                .build();

        final String vmrPayload = new String(vmrConversionComponent.convertImmDsToEvaluateAtSpecifiedTime(request)
                .getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst(), StandardCharsets.UTF_8);

        assertTrue(vmrPayload.contains("code=\"HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID\""));
        assertTrue(vmrPayload.contains("codeSystem=\"%s\"".formatted(SCHEDULE_FLAGS_OID)));
    }

    @Test
    void emitsErrorWhenUnsupportedImmunizationCodeSystemIsIgnored()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParametersWithUnsupportedCodeSystem(), LocalDateTime.now());
        final GuidanceResponse guidanceResponse = guidanceResponse(response);
        final OperationOutcome operationOutcome = operationOutcome(response);

        assertTrue(operationOutcome.issue()
                .stream()
                .anyMatch(issue -> "error".equals(issue.severity()) && issue.details() != null && issue.details().text() != null
                        && issue.details().text().contains("Unsupported code system 'http://hl7.org/fhir/sid/cvxs'")
                        && issue.details().text().contains("knowledgeBase '%s'".formatted(KNOWLEDGE_BASE)) && !issue.details()
                        .text()
                        .contains("org.nyc.cir^ICE^1.0.0") && issue.details().text().contains("not evaluated")));
        assertTrue(guidanceResponse.text().div().contains("generated successfully with errors."));
        assertTrue(informationalIssueDetailsText(operationOutcome).contains("completed successfully using ICE"));
        assertTrue(informationalIssueDetailsText(operationOutcome).contains("with errors."));
    }

    @Test
    void emitsErrorAndWarningSummaryWhenBothArePresent()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParametersWithUnsupportedCvxAndCodeSystem(), LocalDateTime.now());
        final GuidanceResponse guidanceResponse = guidanceResponse(response);
        final OperationOutcome operationOutcome = operationOutcome(response);

        assertTrue(guidanceResponse.text().div().contains("generated successfully with errors and warnings."));
        assertTrue(informationalIssueDetailsText(operationOutcome).contains("with errors and warnings."));
    }

    @Test
    void emitsWarningWhenConfiguredUnsupportedCvxImmunizationIsIgnored()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParametersWithConfiguredUnsupportedCvx(), LocalDateTime.now());
        final GuidanceResponse guidanceResponse = guidanceResponse(response);
        final OperationOutcome operationOutcome = operationOutcome(response);

        assertTrue(operationOutcome.issue()
                .stream()
                .anyMatch(issue -> "warning".equals(issue.severity()) && issue.details() != null && issue.details().text() != null
                        && issue.details().text().contains("CVX code '24' is configured as not supported") && issue.details()
                        .text()
                        .contains("not evaluated")));
        assertTrue(guidanceResponse.text().div().contains("generated successfully with warnings."));
        assertTrue(informationalIssueDetailsText(operationOutcome).contains("with warnings."));
    }

    @Test
    void emitsNativeAuthorityOnEachRecommendationWhenOutputFlagIsEnabled()
    {
        final IceProperties iceProperties = createIceProperties(true, false, true);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent converter = createVmrConversionComponent(supportingDataService, iceProperties);
        final Parameters response =
                converter.convertToParametersResponse(createEvaluationResponseWithScheduleAuthorities(), createRequestParameters(),
                        LocalDateTime.now());
        assertEquals(1, recommendationResources(response).size());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        assertEquals(1, recommendation.recommendation().size());
        final List<Reference> authorities = recommendation.recommendation().getFirst().authority();
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        assertEquals(authorities, evaluation.authority());
        assertEquals(1, streamParameters(response).filter(param -> param.resource() instanceof ImmunizationEvaluation).count());
        assertEquals(List.of("ACIP", "CDC"), authorities.stream().map(authority -> authority.identifier().value()).toList());
        assertEquals(List.of("Advisory Committee on Immunization Practices", "Centers for Disease Control and Prevention"),
                authorities.stream().map(Reference::display).toList());
        assertTrue(authorities.stream()
                .allMatch(authority -> "Organization".equals(authority.type())
                        && "http://terminology.cdsframework.org/ice/schedule-authority".equals(authority.identifier().system())));
        assertTrue(recommendation.recommendation()
                .stream()
                .flatMap(rec -> rec.extension().stream())
                .noneMatch(extension -> SCHEDULE_AUTHORITY_EXTENSION_URL.equals(extension.url())));

        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final String json = mapper.writeValueAsString(recommendation);
        final var tree = mapper.readTree(json);
        assertFalse(tree.has("authority"));
        assertTrue(tree.get("recommendation").get(0).get("authority").isArray());
        assertEquals(2, tree.get("recommendation").get(0).get("authority").size());
        assertEquals("ACIP", tree.get("recommendation").get(0).get("authority").get(0).get("identifier").get("value").asString());
        assertEquals("CDC", tree.get("recommendation").get(0).get("authority").get(1).get("identifier").get("value").asString());
        final var evaluationJson = mapper.readTree(mapper.writeValueAsString(evaluation));
        assertTrue(evaluationJson.get("authority").isArray());
        assertEquals(2, evaluationJson.get("authority").size());
        assertEquals("ACIP", evaluationJson.get("authority").get(0).get("identifier").get("value").asString());
        assertEquals("CDC", evaluationJson.get("authority").get(1).get("identifier").get("value").asString());
        assertFalse(json.contains(SCHEDULE_AUTHORITY_EXTENSION_URL));
    }

    @Test
    void suppressesAuthorityWhenOutputFlagIsDisabled()
    {
        final IceProperties iceProperties = createIceProperties(true, false, false);
        final VmrConversionComponent converter =
                createVmrConversionComponent(new SupportingDataService(createCdsEngineProperties(), iceProperties), iceProperties);
        final ImmunizationRecommendation recommendation = firstRecommendation(
                converter.convertToParametersResponse(createEvaluationResponseWithScheduleAuthorities(), createRequestParameters(),
                        LocalDateTime.now()));
        assertEquals(1, recommendation.recommendation().size());
        assertTrue(recommendation.recommendation().getFirst().authority().isEmpty());
        assertTrue(recommendation.recommendation()
                .getFirst()
                .extension()
                .stream()
                .noneMatch(extension -> SCHEDULE_AUTHORITY_EXTENSION_URL.equals(extension.url())));
    }

    @Test
    void evaluationAuthorityIsEmptyWhenDisabledOrAbsentAndAnArrayForOneAuthority()
    {
        final IceProperties disabled = createIceProperties(false, false, false);
        final VmrConversionComponent disabledConverter =
                createVmrConversionComponent(new SupportingDataService(createCdsEngineProperties(), disabled), disabled);
        assertTrue(firstEvaluation(disabledConverter.convertToParametersResponse(createEvaluationResponseWithScheduleAuthorities(),
                createRequestParameters(), LocalDateTime.now())).authority().isEmpty());
        final IceProperties enabled = createIceProperties(false, false, true);
        final VmrConversionComponent enabledConverter =
                createVmrConversionComponent(new SupportingDataService(createCdsEngineProperties(), enabled), enabled);
        assertTrue(firstEvaluation(
                enabledConverter.convertToParametersResponse(createEvaluationResponseWithSeriesContext(), createRequestParameters(),
                        LocalDateTime.now())).authority().isEmpty());
        final ImmunizationEvaluation evaluation = firstEvaluation(
                enabledConverter.convertToParametersResponse(createEvaluationResponseWithScheduleAuthorities("ACIP", "ACIP"),
                        createRequestParameters(), LocalDateTime.now()));
        assertEquals(1, evaluation.authority().size());
        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final var json = mapper.readTree(mapper.writeValueAsString(evaluation));
        assertTrue(json.get("authority").isArray());
        assertEquals(1, json.get("authority").size());
        assertEquals("ACIP", json.get("authority").get(0).get("identifier").get("value").asString());
    }

    @Test
    void preservesRecommendationWithoutAuthorityAndDeduplicatesRepeatedAuthority()
    {
        final IceProperties iceProperties = createIceProperties(false, false, true);
        final VmrConversionComponent converter =
                createVmrConversionComponent(new SupportingDataService(createCdsEngineProperties(), iceProperties), iceProperties);
        final ImmunizationRecommendation withoutAuthority = firstRecommendation(
                converter.convertToParametersResponse(createEvaluationResponseWithSeriesContext(), createRequestParameters(),
                        LocalDateTime.now()));
        assertEquals(1, withoutAuthority.recommendation().size());
        assertTrue(withoutAuthority.recommendation().getFirst().authority().isEmpty());
        final ImmunizationRecommendation withAuthority = firstRecommendation(
                converter.convertToParametersResponse(createEvaluationResponseWithScheduleAuthorities("ACIP", "ACIP"),
                        createRequestParameters(), LocalDateTime.now()));
        assertEquals(1, withAuthority.recommendation().size());
        assertEquals(1, withAuthority.recommendation().getFirst().authority().size());
        assertEquals("ACIP", withAuthority.recommendation().getFirst().authority().getFirst().identifier().value());
        final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();
        final var json = mapper.readTree(mapper.writeValueAsString(withAuthority));
        assertTrue(json.get("recommendation").get(0).get("authority").isArray());
        assertEquals(1, json.get("recommendation").get(0).get("authority").size());
        assertTrue(withAuthority.recommendation().getFirst().extension().isEmpty());
    }
}
