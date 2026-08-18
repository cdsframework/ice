package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

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

class SupportingDataServicePlanDefinitionSeriesRoutingTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String KNOWLEDGE_BASE = "https://terminology.cdsframework.org/PlanDefinition/ice-forecast|1.0.0";
    private static final String SERIES_CODE = "SERIES_PROFILED";

    private static PlanDefinition createModulePlanDefinition()
    {
        return PlanDefinition.builder()
                .identifier(Identifier.builder()
                        .system("https://terminology.cdsframework.org/ice/identifiers/knowledge-bases")
                        .value(KM_ID)
                        .build())
                .build();
    }

    private static PlanDefinition createSeriesPlanDefinition()
    {
        return PlanDefinition.builder()
                .id(SERIES_CODE)
                .meta(Meta.builder().profile(PlanDefinitionSeriesDataConsumer.SERIES_PLAN_DEFINITION_PROFILE_URL).build())
                .extension(Extension.builder()
                        .url(PlanDefinitionSeriesDataConsumer.SERIES_METADATA_EXTENSION_URL)
                        .extension(Extension.builder().url("numberOfDosesInSeries").valueInteger(1).build())
                        .extension(Extension.builder()
                                .url("series")
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("urn:oid:2.16.840.1.113883.3.795.12.100.10")
                                                .code(SERIES_CODE)
                                                .display("Series Profiled")
                                                .build())
                                        .text("Series Profiled")
                                        .build())
                                .build())
                        .extension(Extension.builder()
                                .url("vaccineGroup")
                                .valueCodeableConcept(CodeableConcept.builder()
                                        .coding(Coding.builder()
                                                .system("urn:oid:2.16.840.1.113883.3.795.12.100.1")
                                                .code("VARICELLA")
                                                .display("Varicella")
                                                .build())
                                        .text("Varicella")
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
                                                        .code("21")
                                                        .display("varicella")
                                                        .build())
                                                .text("varicella")
                                                .build())
                                        .build())
                                .extension(Extension.builder().url("preferred").valueBoolean(true).build())
                                .build())
                        .build())
                .build();
    }

    private static PlanDefinition createNonSeriesPlanDefinition()
    {
        return PlanDefinition.builder()
                .id("NON_SERIES_PLAN_DEFINITION")
                .meta(Meta.builder().profile("http://example.org/fhir/StructureDefinition/non-series").build())
                .action(PlanDefinition.Action.builder().id("non-series-actions").build())
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
    void routesOnlySeriesProfiledPlanDefinitionsToSeriesDataPrroutesOnlySeriesProfiledPlanDefinitionsToSeriesDataProcessingocessing()
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setKnowledgeBaseDefinitionMap(Map.of(KNOWLEDGE_BASE,
                new CdsEngineProperties.KnowledgeBaseDefinition(createModulePlanDefinition(),
                        Map.of(SERIES_CODE, createSeriesPlanDefinition(), "NON_SERIES_PLAN_DEFINITION",
                                createNonSeriesPlanDefinition()), Map.of(), Map.of(), Map.of())));

        final SupportingDataService supportingDataService = new SupportingDataService(properties, createIceProperties());
        final CdsEngineProperties.KnowledgeBaseDefinition module = supportingDataService.getSupportingKnowledgeBaseByKmId(KM_ID);

        assertEquals(1, module.series().size());
        assertTrue(module.series().containsKey(SERIES_CODE));
        assertFalse(module.series().containsKey("NON_SERIES_PLAN_DEFINITION"));
        assertEquals(SERIES_CODE, module.series().get(SERIES_CODE).series().code());
    }
}
