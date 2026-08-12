package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.cdsframework.fhir.AdministrativeGender;
import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemContentModeEnum;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Immunization;
import org.cdsframework.fhir.Observation;
import org.cdsframework.fhir.Parameters;
import org.cdsframework.fhir.ParametersParameter;
import org.cdsframework.fhir.Patient;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.fhir.PublicationStatusEnum;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.service.conversion.FhirToVmrInputAdapter;
import org.cdsframework.ice.service.conversion.ScheduleAuthorityExtensionBuilder;
import org.cdsframework.ice.service.conversion.SelectionContextExtensionBuilder;
import org.cdsframework.ice.service.conversion.VaccineGroupRulesArtifactExtensionBuilder;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.server.ResponseStatusException;

class VmrConversionComponentCodeSystemReverseMappingTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String MODULE_CANONICAL = "http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String SCHEDULE_FLAGS_OID = "2.16.840.1.113883.3.795.12.100.502";

    private static CdsEngineProperties createCdsEngineProperties()
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setModuleCanonicalDefinitionMap(Map.of(MODULE_CANONICAL, createModuleCanonicalDefinition()));
        return properties;
    }

    private static IceProperties createIceProperties()
    {
        return createIceProperties(List.of());
    }

    private static IceProperties createIceProperties(final java.util.List<String> scheduleFlags)
    {
        final IceProperties properties = new IceProperties();
        properties.setIceBaseModuleCanonical(MODULE_CANONICAL);
        properties.setKnowledgeModules(Map.of(MODULE_CANONICAL,
                new IceProperties.KnowledgeModuleProperties(true, false, true, false, true, false, false, false,
                        java.util.List.of(),
                        java.util.List.of(), false, IceProperties.SupplementalTextMode.LEGACY,
                        new ByteArrayResource(new byte[0]))));
        properties.setScheduleFlags(scheduleFlags);
        return properties;
    }

    private static IceProperties createIceProperties(final boolean outputNumberOfDosesRemaining,
            @SuppressWarnings("SameParameterValue") final boolean outputSeriesInformation)
    {
        final IceProperties properties = new IceProperties();
        properties.setIceBaseModuleCanonical(MODULE_CANONICAL);
        properties.setKnowledgeModules(Map.of(MODULE_CANONICAL,
                new IceProperties.KnowledgeModuleProperties(true, false, true, outputNumberOfDosesRemaining,
                        outputSeriesInformation, false, false, false, java.util.List.of(), java.util.List.of(), false,
                        IceProperties.SupplementalTextMode.LEGACY, new ByteArrayResource(new byte[0]))));
        return properties;
    }

    private static VmrConversionComponent createVmrConversionComponent(final SupportingDataService supportingDataService,
            final IceProperties iceProperties)
    {
        return new VmrConversionComponent(supportingDataService, iceProperties, new FhirToVmrInputAdapter(supportingDataService),
                new SelectionContextExtensionBuilder(supportingDataService), new VaccineGroupRulesArtifactExtensionBuilder(),
                new ScheduleAuthorityExtensionBuilder());
    }

    private static CdsEngineProperties.ModuleCanonicalDefinition createModuleCanonicalDefinition()
    {
        return new CdsEngineProperties.ModuleCanonicalDefinition(PlanDefinition.builder()
                .identifier(
                        Identifier.builder().system("http://cdsframework.org/identifiers/knowledge-modules").value(KM_ID).build())
                .build(), Map.of(), Map.of(), Map.of("DISEASE_IMMUNITY_SOURCE_CONCEPT", CodeSystem.builder()
                .name("DISEASE_IMMUNITY_SOURCE_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.8").build())
                .url("http://terminology.cdsframework.org/ice/disease-immunity-source")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .concept(CodeSystemConcept.builder().code("DISEASE_DOCUMENTED").display("Disease Documented").build())
                .concept(CodeSystemConcept.builder().code("PROOF_OF_IMMUNITY").display("Proof of Immunity").build())
                .build(), "DISEASE_IMMUNITY_REASON_CONCEPT", CodeSystem.builder()
                .name("DISEASE_IMMUNITY_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.9").build())
                .url("http://terminology.cdsframework.org/ice/disease-immunity-reason")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .concept(CodeSystemConcept.builder().code("IS_IMMUNE").display("Is Immune").build())
                .build(), "RECOMMENDATION_REASON_CONCEPT", CodeSystem.builder()
                .name("RECOMMENDATION_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.6").build())
                .url("http://terminology.cdsframework.org/ice/recommendation-reason")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .build(), "EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                .name("EVALUATION_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.3").build())
                .url("http://terminology.cdsframework.org/ice/evaluation-reason")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .build(), "SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT", CodeSystem.builder()
                .name("SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.50").build())
                .url("http://terminology.cdsframework.org/ice/supplemental-recommendation-reason")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .build(), "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                .name("SUPPLEMENTAL_EVALUATION_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.51").build())
                .url("http://terminology.cdsframework.org/ice/supplemental-evaluation-reason")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .build(), "SUPPORTED_VACCINES", CodeSystem.builder()
                .name("SUPPORTED_VACCINES")
                .identifier(Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.12.292").build())
                .url("http://hl7.org/fhir/sid/cvx")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .concept(CodeSystemConcept.builder().code("10").display("IPV").build())
                .build(), "SUPPORTED_SCHEDULE_FLAGS", CodeSystem.builder()
                .name("SUPPORTED_SCHEDULE_FLAGS")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:%s".formatted(SCHEDULE_FLAGS_OID)).build())
                .url("http://terminology.cdsframework.org/ice/schedule-flags")
                .status(PublicationStatusEnum.ACTIVE)
                .content(CodeSystemContentModeEnum.COMPLETE)
                .concept(CodeSystemConcept.builder().code("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .display("Evaluate Invalid 3rd Hep B Dose as Accepted Extra Dose")
                        .build())
                .concept(CodeSystemConcept.builder().code("POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .display("Evaluate 4th/5th Polio Dose Below Minimum Age as Accepted Extra Dose")
                        .build())
                .build()), Map.of("2.16.840.1.113883.6.96", "http://snomed.info/sct", "2.16.840.1.113883.6.1", "http://loinc.org",
                "2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm", "2.16.840.1.113883.6.90",
                "http://hl7.org/fhir/sid/icd-10-cm", "2.16.840.1.113883.6.3", "http://hl7.org/fhir/sid/icd-10",
                "2.16.840.1.113883.3.795.12.100.4", "http://terminology.cdsframework.org/ice/unknown",
                "2.16.840.1.113883.3.795.12.100.500", "http://terminology.cdsframework.org/ice/series-display-options"));
    }

    private static Parameters createRequest(final String moduleCanonical, final String... scheduleFlags)
    {
        final Parameters.ParametersBuilder builder = Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-04").build())
                .parameter(ParametersParameter.builder().name("module").valueCanonical(moduleCanonical).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("i1")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .occurrenceDateTime("2020-03-15")
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/supplemental-evaluation-reason")
                                                .code("SUPPLEMENTAL_TEXT_COVID")
                                                .display("Supplemental")
                                                .build())
                                        .text("Supplemental")
                                        .build())
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/supplemental-recommendation-reason")
                                                .code("SUPPLEMENTAL_TEXT_REC")
                                                .display("Supplemental Rec")
                                                .build())
                                        .text("Supplemental Rec")
                                        .build())
                                .interpretation(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("urn:oid:2.16.840.1.113883.3.795.12.100.9")
                                                .code("CUSTOM_INTERPRETATION")
                                                .display("Custom Interpretation")
                                                .build())
                                        .text("Custom Interpretation")
                                        .build())
                                .effectiveDateTime("2020-03-15")
                                .build())
                        .build());
        if (scheduleFlags != null)
        {
            for (final String scheduleFlag : scheduleFlags)
                builder.parameter(ParametersParameter.builder().name("scheduleFlag").valueCode(scheduleFlag).build());
        }
        return builder.build();
    }

    @Test
    void convertsInboundCanonicalSystemsBackToOidsForVmrPayload()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = createRequest("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0");

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final byte[] payload = evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.12.292\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.3.795.12.100.51\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.3.795.12.100.50\""));
        assertTrue(xml.contains("code=\"CUSTOM_INTERPRETATION\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.3.795.12.100.9\""));
    }

    @Test
    void excludesUnsupportedCvxCodesFromVmrPayload()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-04").build())
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("supported")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .occurrenceDateTime("2020-03-15")
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("unsupported")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("999")
                                                .display("Unsupported")
                                                .build())
                                        .text("Unsupported")
                                        .build())
                                .occurrenceDateTime("2020-03-16")
                                .build())
                        .build())
                .build();

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final byte[] payload = evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("extension=\"supported\""));
        assertFalse(xml.contains("extension=\"unsupported\""));
        assertFalse(xml.contains("code=\"999\""));
    }

    @Test
    void excludesImmunizationsWithUnsupportedCodeSystemsFromVmrPayload()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-04").build())
                .parameter(ParametersParameter.builder().name("module").valueCanonical(MODULE_CANONICAL).build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("supported")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .occurrenceDateTime("2020-03-15")
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("immunization")
                        .resource(Immunization.builder()
                                .identifier(Identifier.builder()
                                        .system("http://nyc.gov/cir/identifier/immunization-id")
                                        .value("unsupported-system")
                                        .build())
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvxs")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .occurrenceDateTime("2020-03-16")
                                .build())
                        .build())
                .build();

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final byte[] payload = evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("extension=\"supported\""));
        assertFalse(xml.contains("extension=\"unsupported-system\""));
        assertFalse(xml.contains("cvxs"));
    }

    @Test
    void throwsWhenDerivedKmIdIsNotConfigured()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = createRequest("http://example.org/fhir/PlanDefinition/forecast|1.0.0");

        assertThrows(ResponseStatusException.class, () -> vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request));
    }

    @Test
    void mapsDiseaseDocumentedObservationValueToDiseaseImmunityCodeSystem()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-04").build())
                .parameter(ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/icd-9-cm")
                                                .code("070.30")
                                                .display("Hepatitis B")
                                                .build())
                                        .text("Hepatitis B")
                                        .build())
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/recommendation-reason")
                                                .code("DISEASE_DOCUMENTED")
                                                .display("Disease Documented")
                                                .build())
                                        .text("Disease Documented")
                                        .build())
                                .interpretation(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("urn:oid:2.16.840.1.113883.3.795.12.100.9")
                                                .code("IS_IMMUNE")
                                                .display("Is Immune")
                                                .build())
                                        .text("Is Immune")
                                        .build())
                                .effectiveDateTime("2008-08-10")
                                .build())
                        .build())
                .build();

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final byte[] payload = evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("code=\"DISEASE_DOCUMENTED\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.3.795.12.100.8\""));
    }

    @Test
    void mapsIcd9Icd10AndLoincSystemsToOidsForObservationFocus()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-04").build())
                .parameter(ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/icd-9-cm")
                                                .code("055.9")
                                                .display("Measles")
                                                .build())
                                        .text("Measles")
                                        .build())
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/disease-immunity-source")
                                                .code("DISEASE_DOCUMENTED")
                                                .display("Disease Documented")
                                                .build())
                                        .text("Disease Documented")
                                        .build())
                                .effectiveDateTime("2006-12-08")
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/icd-10-cm")
                                                .code("B16.9")
                                                .display("Acute hepatitis B")
                                                .build())
                                        .text("Acute hepatitis B")
                                        .build())
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/disease-immunity-source")
                                                .code("DISEASE_DOCUMENTED")
                                                .display("Disease Documented")
                                                .build())
                                        .text("Disease Documented")
                                        .build())
                                .effectiveDateTime("2006-12-08")
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://loinc.org")
                                                .code("64363-4")
                                                .display("Hepatitis B virus DNA [Units/volume] in Serum")
                                                .build())
                                        .text("Hepatitis B DNA")
                                        .build())
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/disease-immunity-source")
                                                .code("DISEASE_DOCUMENTED")
                                                .display("Disease Documented")
                                                .build())
                                        .text("Disease Documented")
                                        .build())
                                .effectiveDateTime("2006-12-08")
                                .build())
                        .build())
                .build();

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final byte[] payload = evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("code=\"055.9\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.6.103\""));
        assertTrue(xml.contains("code=\"B16.9\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.6.90\""));
        assertTrue(xml.contains("code=\"64363-4\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.6.1\""));
    }

    @Test
    void mapsDiseaseImmunityInterpretationCodeToConfiguredReasonCodeSystem()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-04").build())
                .parameter(ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(ParametersParameter.builder()
                        .name("observation")
                        .resource(Observation.builder()
                                .status("final")
                                .code(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/icd-9-cm")
                                                .code("070.30")
                                                .display("Hepatitis B")
                                                .build())
                                        .text("Hepatitis B")
                                        .build())
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/disease-immunity-source")
                                                .code("DISEASE_DOCUMENTED")
                                                .display("Disease Documented")
                                                .build())
                                        .text("Disease Documented")
                                        .build())
                                .interpretation(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://terminology.cdsframework.org/ice/evaluation-reason")
                                                .code("IS_IMMUNE")
                                                .display("Is Immune")
                                                .build())
                                        .text("Is Immune")
                                        .build())
                                .effectiveDateTime("2008-08-10")
                                .build())
                        .build())
                .build();

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final byte[] payload = evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("interpretation code=\"IS_IMMUNE\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.3.795.12.100.9\""));
    }

    @Test
    void injectsRequestScheduleFlagsIntoVmrPayload()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = createRequest("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0",
                "HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID");

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request);
        final String xml = new String(evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst(), StandardCharsets.UTF_8);

        assertTrue(xml.contains("code=\"HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID\""));
        assertTrue(xml.contains("codeSystem=\"" + SCHEDULE_FLAGS_OID + "\""));
        assertTrue(xml.contains("<observationValue><boolean value=\"true\"/></observationValue>"));
    }

    @Test
    void injectsConfiguredScheduleFlagsIntoVmrPayload()
    {
        final IceProperties iceProperties = createIceProperties(java.util.List.of("POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"));
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final var evaluateAtSpecifiedTime = vmrConversionComponent.convertToEvaluateAtSpecifiedTime(
                createRequest("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0"));
        final String xml = new String(evaluateAtSpecifiedTime.getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst(), StandardCharsets.UTF_8);

        assertTrue(xml.contains("code=\"POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID\""));
        assertTrue(xml.contains("codeSystem=\"" + SCHEDULE_FLAGS_OID + "\""));
        assertTrue(xml.contains("<observationValue><boolean value=\"true\"/></observationValue>"));
    }

    @Test
    void rejectsUnsupportedRequestScheduleFlags()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = createRequest("http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0", "DOES_NOT_EXIST");

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request));

        assertTrue(exception.getMessage().contains("DOES_NOT_EXIST"));
    }

    @Test
    void outputSeriesContextGateUsesPrecomputedKnowledgeModuleFlags() throws Exception
    {
        final IceProperties iceProperties = createIceProperties(false, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Method shouldOutputSeriesContext =
                VmrConversionComponent.class.getDeclaredMethod("shouldOutputSeriesContext", String.class);
        shouldOutputSeriesContext.setAccessible(true);

        assertFalse((Boolean) shouldOutputSeriesContext.invoke(vmrConversionComponent, KM_ID));
    }

    @Test
    void outputSeriesContextIsEnabledWhenNumberOfDosesRemainingIsEnabled() throws Exception
    {
        final IceProperties iceProperties = createIceProperties(true, false);
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Method shouldOutputSeriesContext =
                VmrConversionComponent.class.getDeclaredMethod("shouldOutputSeriesContext", String.class);
        shouldOutputSeriesContext.setAccessible(true);

        assertTrue((Boolean) shouldOutputSeriesContext.invoke(vmrConversionComponent, KM_ID));
    }
}
