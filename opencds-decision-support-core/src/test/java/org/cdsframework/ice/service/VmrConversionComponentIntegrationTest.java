package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
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
import org.cdsframework.fhir.OperationOutcome;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.fhir.Patient;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.fhir.PublicationStatusEnum;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.conversion.FhirToVmrInputAdapter;
import org.cdsframework.ice.service.conversion.SelectionContextExtensionBuilder;
import org.cdsframework.ice.service.conversion.VaccineGroupRulesArtifactExtensionBuilder;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.junit.jupiter.api.Test;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.FinalKMEvaluationResponse;
import org.omg.dss.KMEvaluationResultData;
import org.omg.dss.SemanticPayload;
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

import tools.jackson.dataformat.xml.XmlMapper;

class VmrConversionComponentIntegrationTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String MODULE_CANONICAL = "http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String VACCINE_GROUP_OID = "2.16.840.1.113883.3.795.12.100.1";
    private static final String SERIES_OPTIONS_OID = "2.16.840.1.113883.3.795.12.100.500";
    private static final String SERIES_SELECTION_TYPE_OID = "2.16.840.1.113883.3.795.12.100.501";
    private static final String SERIES_OID = "2.16.840.1.113883.3.795.12.100.10";
    private static final String SEASON_OID = "2.16.840.1.113883.3.795.12.100.11";
    private static final String RECOMMENDATION_REASON_OID = "2.16.840.1.113883.3.795.12.100.6";
    private static final String SELECTION_CONTEXT_EXTENSION_URL =
            "http://terminology.cdsframework.org/fhir/StructureDefinition/immunization-selection-context";

    private static CdsEngineProperties createCdsEngineProperties()
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setModuleCanonicalDefinitionMap(Map.of(MODULE_CANONICAL, createModuleCanonicalDefinition()));
        return properties;
    }

    private static IceProperties createIceProperties(final boolean outputSeriesInformation,
            @SuppressWarnings("SameParameterValue") final boolean outputNumberOfDosesRemaining)
    {
        final IceProperties properties = new IceProperties();
        properties.setIceBaseModuleCanonical(MODULE_CANONICAL);
        properties.setKnowledgeModules(Map.of(MODULE_CANONICAL,
                new IceProperties.KnowledgeModuleProperties(true, false, true, outputNumberOfDosesRemaining,
                        outputSeriesInformation, false, false, List.of(), List.of(), false,
                        IceProperties.SupplementalTextMode.LEGACY, new ByteArrayResource(new byte[0]))));
        return properties;
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

    private static CdsEngineProperties.ModuleCanonicalDefinition createModuleCanonicalDefinition()
    {
        return new CdsEngineProperties.ModuleCanonicalDefinition(PlanDefinition.builder()
                .identifier(
                        Identifier.builder().system("http://cdsframework.org/identifiers/knowledge-modules").value(KM_ID).build())
                .build(), Map.of(), Map.of(), Map.of("VACCINE_GROUP_CONCEPT",
                simpleCodeSystem("VACCINE_GROUP_CONCEPT", VACCINE_GROUP_OID,
                        "http://terminology.cdsframework.org/ice/vaccine-group",
                        CodeSystemConcept.builder().code("100").display("Test Vaccine Group").build()), "SUPPORTED_SERIES",
                simpleCodeSystem("SUPPORTED_SERIES", SERIES_OID, "http://terminology.cdsframework.org/ice/series",
                        CodeSystemConcept.builder().code("ZOSTER_SERIES").display("Zoster Series").build()),
                "SERIES_DISPLAY_SELECTION_TYPE", simpleCodeSystem("SERIES_DISPLAY_SELECTION_TYPE", SERIES_SELECTION_TYPE_OID,
                        "http://terminology.cdsframework.org/ice/series-display-selection-type", CodeSystemConcept.builder()
                                .code("SERIES_DISPLAY_UNAMBIGUOUS")
                                .display("Series Selected for Display")
                                .build()), "SUPPORTED_SEASON",
                simpleCodeSystem("SUPPORTED_SEASON", SEASON_OID, "http://terminology.cdsframework.org/ice/season",
                        CodeSystemConcept.builder().code("SEASON_2026_2027").display("2026-2027 Season").build()),
                "RECOMMENDATION_REASON_CONCEPT", simpleCodeSystem("RECOMMENDATION_REASON_CONCEPT", RECOMMENDATION_REASON_OID,
                        "http://terminology.cdsframework.org/ice/recommendation-reason",
                        CodeSystemConcept.builder().code("RECOMMENDED").display("Recommended").build(),
                        CodeSystemConcept.builder().code("DUE_NOW").display("Due Now").build()), "SUPPORTED_VACCINES",
                simpleCodeSystem("SUPPORTED_VACCINES", "2.16.840.1.113883.12.292", "http://hl7.org/fhir/sid/cvx",
                        CodeSystemConcept.builder().code("10").display("IPV").build(), CodeSystemConcept.builder()
                                .code("24")
                                .display("Anthrax, pre-exposure prophylaxis, post-exposure prophylaxis")
                                .property(CodeSystemConceptProperty.builder().code("supported").valueBoolean(false).build())
                                .build())),
                Map.of("2.16.840.1.113883.6.96", "http://snomed.info/sct", "2.16.840.1.113883.6.1", "http://loinc.org",
                        "2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm", "2.16.840.1.113883.6.90",
                        "http://hl7.org/fhir/sid/icd-10-cm", "2.16.840.1.113883.6.3", "http://hl7.org/fhir/sid/icd-10",
                        "2.16.840.1.113883.3.795.12.100.4", "http://terminology.cdsframework.org/ice/unknown",
                        "2.16.840.1.113883.3.795.12.100.500", "http://terminology.cdsframework.org/ice/series-display-options"));
    }

    private static VmrConversionComponent createVmrConversionComponent(final SupportingDataService supportingDataService,
            final IceProperties iceProperties)
    {
        return new VmrConversionComponent(supportingDataService, iceProperties, new FhirToVmrInputAdapter(supportingDataService),
                new SelectionContextExtensionBuilder(supportingDataService), new VaccineGroupRulesArtifactExtensionBuilder());
    }

    private static Parameters createRequestParameters()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
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
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
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
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
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
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
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

    private static Parameters createRequestParametersWithUnsupportedCvxAndCodeSystem()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
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

    private static SubstanceAdministrationEvent createEventWithSeriesContext()
    {
        return createEventWithSeriesContext("2");
    }

    private static SubstanceAdministrationEvent createEventWithSeriesContext(final String dosesRemainingValue)
    {
        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        final II id = new II();
        id.setExtension("imm-1");
        event.setId(id);
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
        events.getSubstanceAdministrationEvent().add(createEventWithSeriesContext(dosesRemainingValue));
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
        assertNull(rec.seriesDoses());
        assertTrue(ObjectUtils.isEmpty(rec.extension()));
        assertTrue(evaluation.extension().isEmpty());
        assertNotNull(rec.description());
        assertTrue(rec.description().startsWith("Recommendation status:"));
    }

    @Test
    void emitsSeriesSeasonAndDosesWhenOutputFlagIsEnabled()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response = vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext(),
                createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertEquals("Zoster Series", rec.series());
        assertEquals("Zoster Series", evaluation.series());
        assertNotNull(rec.seriesDoses());
        assertEquals("2", rec.seriesDoses().text());
        assertEquals(1, rec.extension().size());
        final var contextExtension = rec.extension().getFirst();
        assertEquals(SELECTION_CONTEXT_EXTENSION_URL, contextExtension.url());
        assertEquals(3, contextExtension.extension().size());
        assertTrue(contextExtension.extension()
                .stream()
                .anyMatch(ext -> "selectedSeries".equals(ext.url()) && ext.valueCodeableConcept()
                        .coding()
                        .stream()
                        .anyMatch(coding -> "ZOSTER_SERIES".equals(coding.code()))));
        assertTrue(contextExtension.extension()
                .stream()
                .anyMatch(ext -> "seriesSelectionType".equals(ext.url()) && ext.valueCodeableConcept()
                        .coding()
                        .stream()
                        .anyMatch(coding -> "SERIES_DISPLAY_UNAMBIGUOUS".equals(coding.code()))));
        assertTrue(contextExtension.extension()
                .stream()
                .anyMatch(ext -> "selectedSeason".equals(ext.url()) && ext.valueCodeableConcept()
                        .coding()
                        .stream()
                        .anyMatch(coding -> "SEASON_2026_2027".equals(coding.code()))));
        assertEquals(1, evaluation.extension().size());
        assertEquals(SELECTION_CONTEXT_EXTENSION_URL, evaluation.extension().getFirst().url());
        assertTrue(rec.description().contains("Series: Zoster Series"));
        assertTrue(rec.description().contains("Season: 2026-2027 Season"));
    }

    @Test
    void doesNotEmitSeriesDosesWhenDosesRemainingIsZero()
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters response =
                vmrConversionComponent.convertToParametersResponse(createEvaluationResponseWithSeriesContext("0"),
                        createRequestParameters(), LocalDateTime.now());
        final ImmunizationRecommendation recommendation = firstRecommendation(response);
        final ImmunizationEvaluation evaluation = firstEvaluation(response);
        final var rec = recommendation.recommendation().getFirst();

        assertNull(rec.seriesDoses());
        assertNull(evaluation.seriesDoses());
        assertTrue(rec.description().contains("Series: Zoster Series"));
        assertTrue(rec.description().contains("Season: 2026-2027 Season"));
        assertFalse(rec.description().contains("Recommended number of doses for immunity: 0"));
    }

    @Test
    void emitsSingleRecommendationResourceWithNestedEntries() throws Exception
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
                        && issue.details().text().contains("moduleCanonical '%s'".formatted(MODULE_CANONICAL)) && !issue.details()
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
}
