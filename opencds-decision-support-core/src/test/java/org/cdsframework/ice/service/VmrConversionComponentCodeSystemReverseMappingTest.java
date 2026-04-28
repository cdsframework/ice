package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.dto.AdministrativeGender;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConcept;
import org.cdsframework.ice.dto.CodeSystemContentModeEnum;
import org.cdsframework.ice.dto.CodeableConcept;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.dto.Identifier;
import org.cdsframework.ice.dto.Immunization;
import org.cdsframework.ice.dto.Observation;
import org.cdsframework.ice.dto.Parameters;
import org.cdsframework.ice.dto.Patient;
import org.cdsframework.ice.dto.PublicationStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

class VmrConversionComponentCodeSystemReverseMappingTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";

    private static IceProperties createIceProperties()
    {
        final IceProperties properties = new IceProperties();
        properties.setKnowledgeModules(Map.of(KM_ID,
                new IceProperties.KnowledgeModuleProperties(true, false, true, false, true, false, java.util.List.of(),
                        java.util.List.of(), false, IceProperties.SupplementalTextMode.LEGACY,
                        new ByteArrayResource(new byte[0]))));
        return properties;
    }

    private static IceProperties createIceProperties(final boolean outputNumberOfDosesRemaining,
            final boolean outputSeriesInformation)
    {
        final IceProperties properties = new IceProperties();
        properties.setKnowledgeModules(Map.of(KM_ID,
                new IceProperties.KnowledgeModuleProperties(true, false, true, outputNumberOfDosesRemaining,
                        outputSeriesInformation, false, java.util.List.of(), java.util.List.of(), false,
                        IceProperties.SupplementalTextMode.LEGACY, new ByteArrayResource(new byte[0]))));
        return properties;
    }

    private static VmrConversionComponent createVmrConversionComponent(final SupportingDataService supportingDataService,
            final IceProperties iceProperties)
    {
        return new VmrConversionComponent(supportingDataService, iceProperties, new KnowledgeModuleIdResolver(iceProperties));
    }

    private static IceSupportingDataProperties createIceSupportingDataProperties()
    {
        final IceSupportingDataProperties properties = new IceSupportingDataProperties();
        properties.setKnowledgeModules(Map.of(KM_ID, new IceSupportingDataProperties.KnowledgeModule(Map.of(),
                Map.of("DISEASE_IMMUNITY_SOURCE_CONCEPT", CodeSystem.builder()
                        .name("DISEASE_IMMUNITY_SOURCE_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.8")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/disease-immunity-source")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .concept(CodeSystemConcept.builder().code("DISEASE_DOCUMENTED").display("Disease Documented").build())
                        .concept(CodeSystemConcept.builder().code("PROOF_OF_IMMUNITY").display("Proof of Immunity").build())
                        .build(), "DISEASE_IMMUNITY_REASON_CONCEPT", CodeSystem.builder()
                        .name("DISEASE_IMMUNITY_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.9")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/disease-immunity-reason")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .concept(CodeSystemConcept.builder().code("IS_IMMUNE").display("Is Immune").build())
                        .build(), "RECOMMENDATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("RECOMMENDATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.6")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/recommendation-reason")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .build(), "EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("EVALUATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.3")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/evaluation-reason")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .build(), "SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("SUPPLEMENTAL_RECOMMENDATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.50")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/supplemental-recommendation-reason")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .build(), "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("SUPPLEMENTAL_EVALUATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.51")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/supplemental-evaluation-reason")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .build(), "SUPPORTED_VACCINES", CodeSystem.builder()
                        .name("SUPPORTED_VACCINES")
                        .identifier(
                                Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.12.292").build())
                        .url("http://hl7.org/fhir/sid/cvx")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .build()),
                Map.of("2.16.840.1.113883.6.96", "http://snomed.info/sct", "2.16.840.1.113883.6.1", "http://loinc.org",
                        "2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm", "2.16.840.1.113883.6.90",
                        "http://hl7.org/fhir/sid/icd-10-cm", "2.16.840.1.113883.6.3", "http://hl7.org/fhir/sid/icd-10",
                        "2.16.840.1.113883.3.795.12.100.4", "http://terminology.cdsframework.org/ice/unknown",
                        "2.16.840.1.113883.3.795.12.100.500", "http://terminology.cdsframework.org/ice/series-display-options"))));
        return properties;
    }

    private static Parameters createRequest(final String moduleCanonical)
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("assessmentDate")
                        .valueDate("2026-04-04")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("module")
                        .valueCanonical(moduleCanonical)
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
                        .build())
                .build();
    }

    @Test
    void convertsInboundCanonicalSystemsBackToOidsForVmrPayload()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = createRequest("http://nyc.gov/cir/PlanDefinition/ice-forecast|1.0.0");

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
    void throwsWhenDerivedKmIdIsNotConfigured()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = createRequest("http://example.org/fhir/PlanDefinition/forecast|1.0.0");

        assertThrows(IllegalArgumentException.class, () -> vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request));
    }

    @Test
    void mapsDiseaseDocumentedObservationValueToDiseaseImmunityCodeSystem()
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("assessmentDate")
                        .valueDate("2026-04-04")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://nyc.gov/cir/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("assessmentDate")
                        .valueDate("2026-04-04")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://nyc.gov/cir/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Parameters request = Parameters.builder()
                .resourceType("Parameters")
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("assessmentDate")
                        .valueDate("2026-04-04")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://nyc.gov/cir/PlanDefinition/ice-forecast|1.0.0")
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
                        .name("patient")
                        .resource(Patient.builder()
                                .identifier(
                                        Identifier.builder().system("http://nyc.gov/cir/identifier/patient-id").value("p1").build())
                                .birthDate(LocalDate.parse("2020-01-15"))
                                .gender(AdministrativeGender.FEMALE)
                                .build())
                        .build())
                .parameter(org.cdsframework.ice.dto.ParametersParameter.builder()
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
    void outputSeriesContextGateUsesPrecomputedKnowledgeModuleFlags() throws Exception
    {
        final IceProperties iceProperties = createIceProperties(false, false);
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
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
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent = createVmrConversionComponent(supportingDataService, iceProperties);

        final Method shouldOutputSeriesContext =
                VmrConversionComponent.class.getDeclaredMethod("shouldOutputSeriesContext", String.class);
        shouldOutputSeriesContext.setAccessible(true);

        assertTrue((Boolean) shouldOutputSeriesContext.invoke(vmrConversionComponent, KM_ID));
    }
}
