package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Extension;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.Meta;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

class SupportingDataServiceSeriesDisplayNameTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String KNOWLEDGE_BASE = "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String SERIES_CODE = "ZOSTER_SERIES";
    private static final String SERIES_PLAN_DEFINITION_PROFILE_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-plan-definition";
    private static final String SERIES_METADATA_EXTENSION_URL =
            "https://terminology.cdsframework.org/ice/StructureDefinition/ice-series-metadata";

    private static CdsEngineProperties createCdsEngineProperties(final Map<String, PlanDefinition> planDefinitions)
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setKnowledgeBaseDefinitionMap(Map.of(KNOWLEDGE_BASE, new CdsEngineProperties.KnowledgeBaseDefinition(
                PlanDefinition.builder()
                        .identifier(Identifier.builder()
                                .system("https://terminology.cdsframework.org/ice/identifiers/knowledge-bases")
                                .value(KM_ID)
                                .build())
                        .build(), planDefinitions, Map.of(), Map.of("SUPPORTED_SERIES", CodeSystem.builder()
                .name("SUPPORTED_SERIES")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.10").build())
                .url("https://terminology.cdsframework.org/ice/series")
                .concept(CodeSystemConcept.builder().code(SERIES_CODE).display("Code System Display").build())
                .build()), Map.of())));
        return properties;
    }

    private static PlanDefinition createSeriesPlanDefinition()
    {
        return PlanDefinition.builder()
                .id(SERIES_CODE)
                .name(SERIES_CODE)
                .meta(Meta.builder().profile(SERIES_PLAN_DEFINITION_PROFILE_URL).build())
                .extension(Extension.builder()
                        .url(SERIES_METADATA_EXTENSION_URL)
                        .extension(Extension.builder().url("numberOfDosesInSeries").valueInteger(1).build())
                        .extension(Extension.builder()
                                .url("series")
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("urn:oid:2.16.840.1.113883.3.795.12.100.10")
                                                .code(SERIES_CODE)
                                                .display("Series Data Display")
                                                .build())
                                        .text("Series Data Display")
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url("vaccineGroup")
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("urn:oid:2.16.840.1.113883.3.795.12.100.1")
                                                .code("ZOSTER")
                                                .display("Zoster")
                                                .build())
                                        .text("Zoster")
                                        .build())
                                .build())
                        .build())
                .action(PlanDefinition.Action.builder()
                        .id("dose-1")
                        .extension(Extension.builder()
                                .url("https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-number")
                                .valueInteger(1)
                                .build())
                        .extension(Extension.builder()
                                .url("https://terminology.cdsframework.org/ice/StructureDefinition/ice-dose-vaccine")
                                .extension(Extension.builder()
                                        .url("vaccine")
                                        .valueCodeableConcept(CodeableConcept.builder()
                                                .coding(Coding.builder()
                                                        .system("urn:oid:2.16.840.1.113883.12.292")
                                                        .code("121")
                                                        .display("Zoster vaccine")
                                                        .build())
                                                .text("Zoster vaccine")
                                                .build())
                                        .build())
                                .extension(Extension.builder().url("preferred").valueBoolean(true).build())
                                .build())
                        .build())
                .build();
    }

    private static IceProperties createIceProperties()
    {
        final IceProperties properties = new IceProperties();
        properties.setIceBaseKnowledgeBase(KNOWLEDGE_BASE);
        properties.setKnowledgeBases(Map.of(KNOWLEDGE_BASE,
                new IceProperties.KnowledgeBaseProperties(true, false, true, false, true, false, false, false, List.of(), List.of(),
                        false, IceProperties.SupplementalTextMode.LEGACY, new ByteArrayResource(new byte[0]))));
        return properties;
    }

    @Test
    void resolvesSeriesDisplayFromSeriesDataForDirectAndPrefixedCodes()
    {
        final SupportingDataService supportingDataService =
                new SupportingDataService(createCdsEngineProperties(Map.of(SERIES_CODE, createSeriesPlanDefinition())),
                        createIceProperties());

        assertEquals("Series Data Display", supportingDataService.getSupportedSeriesDisplayName(KM_ID, SERIES_CODE));
        assertEquals("Series Data Display",
                supportingDataService.getSupportedSeriesDisplayName(KM_ID, "VACCINE_SERIES_" + SERIES_CODE));
        assertEquals("Series Data Display",
                supportingDataService.getSupportedSeriesDisplayName(KM_ID, "SUPPORTED_SERIES." + SERIES_CODE));
    }

    @Test
    void fallsBackToSupportedSeriesCodeSystemDisplayWhenSeriesDataMissing()
    {
        final SupportingDataService supportingDataService = new SupportingDataService(createCdsEngineProperties(
                Map.of("NOT_A_SERIES_PLAN_DEFINITION", PlanDefinition.builder()
                        .id("NOT_A_SERIES_PLAN_DEFINITION")
                        .meta(Meta.builder().profile("http://example.org/non-series").build())
                        .build())), createIceProperties());

        assertEquals("Code System Display",
                supportingDataService.getSupportedSeriesDisplayName(KM_ID, "VACCINE_SERIES_" + SERIES_CODE));
    }
}
