package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.fhir.Identifier;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.config.IceProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

class SupportingDataServiceCodeSystemMappingTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";
    private static final String MODULE_CANONICAL = "http://cdsframework.org/PlanDefinition/ice-forecast|1.0.0";

    private static CdsEngineProperties createCdsEngineProperties()
    {
        final CdsEngineProperties properties = new CdsEngineProperties();
        properties.setModuleCanonicalDefinitionMap(Map.of(MODULE_CANONICAL, new CdsEngineProperties.ModuleCanonicalDefinition(
                PlanDefinition.builder()
                        .identifier(Identifier.builder()
                                .system("http://cdsframework.org/identifiers/knowledge-modules")
                                .value(KM_ID)
                                .build())
                        .build(), Map.of(), Map.of(), Map.of("SUPPORTED_VACCINES", CodeSystem.builder()
                .name("SUPPORTED_VACCINES")
                .identifier(Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.12.292").build())
                .url("http://hl7.org/fhir/sid/cvx")
                .concept(CodeSystemConcept.builder().code("10").display("IPV").build())
                .concept(CodeSystemConcept.builder()
                        .code("24")
                        .display("Anthrax")
                        .property(CodeSystemConceptProperty.builder().code("supported").valueBoolean(false).build())
                        .build())
                .build(), "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                .name("SUPPLEMENTAL_EVALUATION_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.51").build())
                .url("http://terminology.cdsframework.org/ice/supplemental-evaluation-reason")
                .build(), "EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                .name("EVALUATION_REASON_CONCEPT")
                .identifier(
                        Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.3.795.12.100.3").build())
                .url("http://terminology.cdsframework.org/ice/evaluation-reason")
                .build(), "TEST_DISPLAY_CONCEPT", CodeSystem.builder()
                .name("TEST_DISPLAY_CONCEPT")
                .identifier(Identifier.builder()
                        .system("urn:ietf:rfc:3986")
                        .value("urn:oid:2.16.840.1.113883.3.795.12.100.700")
                        .build())
                .url("http://terminology.cdsframework.org/ice/test-display")
                .concept(CodeSystemConcept.builder()
                        .code("PRIMARY")
                        .display("Primary Display")
                        .property(CodeSystemConceptProperty.builder()
                                .code("conceptMapping")
                                .valueCoding(Coding.builder().code("ALIAS").build())
                                .build())
                        .build())
                .build(), "SUPPORTED_SCHEDULE_FLAGS", CodeSystem.builder()
                .name("SUPPORTED_SCHEDULE_FLAGS")
                .identifier(Identifier.builder()
                        .system("urn:ietf:rfc:3986")
                        .value("urn:oid:2.16.840.1.113883.3.795.12.100.502")
                        .build())
                .url("http://terminology.cdsframework.org/ice/schedule-flags")
                .concept(CodeSystemConcept.builder().code("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .display("Evaluate Invalid 3rd Hep B Dose as Accepted Extra Dose")
                        .build())
                .concept(CodeSystemConcept.builder().code("POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID")
                        .display("Evaluate 4th/5th Polio Dose Below Minimum Age as Accepted Extra Dose")
                        .build())
                .build()), Map.of("2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm"))));
        return properties;
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

    private final SupportingDataService supportingDataService =
            new SupportingDataService(createCdsEngineProperties(), createIceProperties(List.of()));

    @Test
    void resolvesKnowledgeModuleIdFromCanonicalPlanDefinitionMapping()
    {
        assertEquals(KM_ID, supportingDataService.getKmIdFromModuleCanonicalUrl(MODULE_CANONICAL));
    }

    @Test
    void mapsCvxOidToFhirSidUrl()
    {
        assertEquals("http://hl7.org/fhir/sid/cvx",
                supportingDataService.getCodeableConcept(KM_ID, "10", "IPV", "2.16.840.1.113883.12.292", null)
                        .coding()
                        .getFirst()
                        .system());
    }

    @Test
    void mapsConfiguredIceOidToCanonicalUrl()
    {
        assertEquals("http://terminology.cdsframework.org/ice/evaluation-reason",
                supportingDataService.getCodeableConcept(KM_ID, "SUPPLEMENTAL_TEXT", "Supplemental",
                        "2.16.840.1.113883.3.795.12.100.3", null).coding().getFirst().system());
    }

    @Test
    void leavesHttpUrlUnchanged()
    {
        assertEquals("http://example.org/fhir/CodeSystem/custom",
                supportingDataService.getCodeableConcept(KM_ID, "code", "display", "http://example.org/fhir/CodeSystem/custom",
                        null).coding().getFirst().system());
    }

    @Test
    void mapsConfiguredCanonicalUrlBackToOid()
    {
        assertEquals("2.16.840.1.113883.3.795.12.100.51", supportingDataService.toRequiredInternalCodeSystemOid(KM_ID,
                "http://terminology.cdsframework.org/ice/supplemental-evaluation-reason"));
    }

    @Test
    void resolvesDisplayFromConceptMappingAliasCode()
    {
        final CodeableConcept concept =
                supportingDataService.getCodeableConcept(KM_ID, "ALIAS", null, "2.16.840.1.113883.3.795.12.100.700", null);

        assertEquals("Primary Display", concept.coding().getFirst().display());
        assertEquals("Primary Display", concept.text());
    }

    @Test
    void considersCodeSupportedWhenSupportedPropertyIsMissing()
    {
        assertTrue(supportingDataService.isCodeSupportedInCodeSystem(KM_ID, "SUPPORTED_VACCINES", "10"));
    }

    @Test
    void considersCodeUnsupportedWhenSupportedPropertyIsFalse()
    {
        assertFalse(supportingDataService.isCodeSupportedInCodeSystem(KM_ID, "SUPPORTED_VACCINES", "24"));
    }

    @Test
    void acceptsConfiguredScheduleFlagsWhenAllCodesExist()
    {
        assertEquals(KM_ID, new SupportingDataService(createCdsEngineProperties(), createIceProperties(
                java.util.List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID",
                        "POLIO_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID"))).getKmIdFromModuleCanonicalUrl(
                MODULE_CANONICAL));
    }

    @Test
    void rejectsConfiguredScheduleFlagsWhenCodeMissing()
    {
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> new SupportingDataService(createCdsEngineProperties(),
                        createIceProperties(java.util.List.of("HEP_B_EXTRA_DOSE_ACCEPTED_INSTEAD_OF_VALID", "DOES_NOT_EXIST"))));

        assertTrue(exception.getMessage().contains("DOES_NOT_EXIST"));
    }
}
