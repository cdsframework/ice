package org.cdsframework.ice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.dto.CodeSystem;
import org.cdsframework.ice.dto.CodeSystemConcept;
import org.cdsframework.ice.dto.CodeSystemConceptProperty;
import org.cdsframework.ice.dto.CodeableConcept;
import org.cdsframework.ice.dto.Coding;
import org.cdsframework.ice.dto.Identifier;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

class SupportingDataServiceCodeSystemMappingTest
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

    private static IceSupportingDataProperties createIceSupportingDataProperties()
    {
        final IceSupportingDataProperties properties = new IceSupportingDataProperties();
        properties.setKnowledgeModules(Map.of(KM_ID, new IceSupportingDataProperties.KnowledgeModule(Map.of(),
                Map.of("SUPPORTED_VACCINES", CodeSystem.builder()
                        .name("SUPPORTED_VACCINES")
                        .identifier(
                                Identifier.builder().system("urn:ietf:rfc:3986").value("urn:oid:2.16.840.1.113883.12.292").build())
                        .url("http://hl7.org/fhir/sid/cvx")
                        .build(), "SUPPLEMENTAL_EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("SUPPLEMENTAL_EVALUATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.51")
                                .build())
                        .url("http://terminology.cdsframework.org/ice/supplemental-evaluation-reason")
                        .build(), "EVALUATION_REASON_CONCEPT", CodeSystem.builder()
                        .name("EVALUATION_REASON_CONCEPT")
                        .identifier(Identifier.builder()
                                .system("urn:ietf:rfc:3986")
                                .value("urn:oid:2.16.840.1.113883.3.795.12.100.3")
                                .build())
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
                        .build()), Map.of("2.16.840.1.113883.6.103", "http://hl7.org/fhir/sid/icd-9-cm"))));
        return properties;
    }

    private final SupportingDataService supportingDataService =
            new SupportingDataService(createIceSupportingDataProperties(), createIceProperties());

    @Test
    void mapsCvxOidToFhirSidUrl()
    {
        final CodeableConcept concept =
                supportingDataService.getCodeableConcept(KM_ID, "10", "IPV", "2.16.840.1.113883.12.292", null);

        assertEquals("http://hl7.org/fhir/sid/cvx", concept.coding().getFirst().system());
    }

    @Test
    void mapsConfiguredIceOidToCanonicalUrl()
    {
        final CodeableConcept concept = supportingDataService.getCodeableConcept(KM_ID, "SUPPLEMENTAL_TEXT", "Supplemental",
                "2.16.840.1.113883.3.795.12.100.3", null);

        assertEquals("http://terminology.cdsframework.org/ice/evaluation-reason", concept.coding().getFirst().system());
    }

    @Test
    void leavesHttpUrlUnchanged()
    {
        final CodeableConcept concept =
                supportingDataService.getCodeableConcept(KM_ID, "code", "display", "http://example.org/fhir/CodeSystem/custom",
                        null);

        assertEquals("http://example.org/fhir/CodeSystem/custom", concept.coding().getFirst().system());
    }

    @Test
    void mapsConfiguredCanonicalUrlBackToOid()
    {
        final String oid = supportingDataService.toRequiredInternalCodeSystemOid(KM_ID,
                "http://terminology.cdsframework.org/ice/supplemental-evaluation-reason");

        assertEquals("2.16.840.1.113883.3.795.12.100.51", oid);
    }

    @Test
    void resolvesDisplayFromConceptMappingAliasCode()
    {
        final CodeableConcept concept =
                supportingDataService.getCodeableConcept(KM_ID, "ALIAS", null, "2.16.840.1.113883.3.795.12.100.700", null);

        assertEquals("Primary Display", concept.coding().getFirst().display());
        assertEquals("Primary Display", concept.text());
    }
}
