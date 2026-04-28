package org.cdsframework.ice.service;

import java.time.LocalDate;
import java.util.List;
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
import org.cdsframework.ice.dto.Parameters;
import org.cdsframework.ice.dto.ParametersParameter;
import org.cdsframework.ice.dto.Patient;
import org.cdsframework.ice.dto.PublicationStatusEnum;
import org.springframework.core.io.ByteArrayResource;

/**
 * Lightweight micro-benchmark harness for local tuning.
 * Run manually from IDE or terminal.
 */
public class ServiceMicroBenchmark
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";

    private static IceProperties createIceProperties()
    {
        final IceProperties properties = new IceProperties();
        properties.setKnowledgeModules(Map.of(KM_ID,
                new IceProperties.KnowledgeModuleProperties(true, false, true, false, true, false, List.of(), List.of(), false,
                        IceProperties.SupplementalTextMode.LEGACY, new ByteArrayResource(new byte[0]))));
        return properties;
    }

    private static IceSupportingDataProperties createIceSupportingDataProperties()
    {
        final IceSupportingDataProperties properties = new IceSupportingDataProperties();
        properties.setKnowledgeModules(Map.of(KM_ID, new IceSupportingDataProperties.KnowledgeModule(Map.of(),
                Map.of("SUPPORTED_VACCINES", CodeSystem.builder()
                        .name("SUPPORTED_VACCINES")
                        .identifier(
                                Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.12.292").build())
                        .url("http://hl7.org/fhir/sid/cvx")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .concept(CodeSystemConcept.builder().code("10").display("IPV").build())
                        .build(), "RECOMMENDATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("RECOMMENDATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.6")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/recommendation-reason")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .concept(CodeSystemConcept.builder().code("RECOMMENDED").display("Recommended").build())
                        .build(), "VACCINE_GROUP_CONCEPT", CodeSystem.builder()
                        .name("VACCINE_GROUP_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.1")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/vaccine-group")
                        .status(PublicationStatusEnum.ACTIVE)
                        .content(CodeSystemContentModeEnum.COMPLETE)
                        .concept(CodeSystemConcept.builder().code("100").display("Test Vaccine Group").build())
                        .build()),
                Map.of("2.16.840.1.113883.6.96", "http://snomed.info/sct", "2.16.840.1.113883.6.1", "http://loinc.org",
                        "2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm", "2.16.840.1.113883.6.90",
                        "http://hl7.org/fhir/sid/icd-10-cm", "2.16.840.1.113883.6.3", "http://hl7.org/fhir/sid/icd-10",
                        "2.16.840.1.113883.3.795.12.100.4", "http://terminology.cdsframework.org/ice/unknown",
                        "2.16.840.1.113883.3.795.12.100.500", "http://terminology.cdsframework.org/ice/series-display-options"))));
        return properties;
    }

    private static Parameters createBenchmarkRequest()
    {
        return Parameters.builder()
                .resourceType("Parameters")
                .parameter(ParametersParameter.builder().name("assessmentDate").valueDate("2026-04-09").build())
                .parameter(ParametersParameter.builder()
                        .name("module")
                        .valueCanonical("http://nyc.gov/cir/PlanDefinition/ice-forecast|1.0.0")
                        .build())
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
                                        .value("i1")
                                        .build())
                                .occurrenceDateTime("2020-03-15")
                                .vaccineCode(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("http://hl7.org/fhir/sid/cvx")
                                                .code("10")
                                                .display("IPV")
                                                .build())
                                        .text("IPV")
                                        .build())
                                .build())
                        .build())
                .build();
    }

    static void main(final String[] args)
    {
        final IceProperties iceProperties = createIceProperties();
        final SupportingDataService supportingDataService =
                new SupportingDataService(createIceSupportingDataProperties(), iceProperties);
        final VmrConversionComponent vmrConversionComponent =
                new VmrConversionComponent(supportingDataService, iceProperties, new KnowledgeModuleIdResolver(iceProperties));
        final Parameters request = createBenchmarkRequest();

        runBenchmark("SupportingDataService.getCodeableConcept", 10_000, 200_000,
                () -> supportingDataService.getCodeableConcept(KM_ID, "10", "IPV", "2.16.840.1.113883.12.292", null));

        runBenchmark("VmrConversionComponent.convertToEvaluateAtSpecifiedTime", 200, 2_000,
                () -> vmrConversionComponent.convertToEvaluateAtSpecifiedTime(request));
    }

    private static void runBenchmark(final String name, final int warmupIterations, final int measureIterations,
            final Runnable operation)
    {
        for (int i = 0; i < warmupIterations; i++)
            operation.run();

        final long startNs = System.nanoTime();
        for (int i = 0; i < measureIterations; i++)
            operation.run();
        final long elapsedNs = System.nanoTime() - startNs;

        final double nsPerOp = elapsedNs / (double) measureIterations;
        final double opsPerSec = 1_000_000_000.0 / nsPerOp;
        System.out.printf("%s -> %.2f ns/op, %.2f ops/sec%n", name, nsPerOp, opsPerSec);
    }
}
