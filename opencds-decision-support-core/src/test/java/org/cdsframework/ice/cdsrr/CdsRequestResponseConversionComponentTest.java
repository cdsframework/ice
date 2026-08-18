package org.cdsframework.ice.cdsrr;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.cdsframework.fhir.CodeableConcept;
import org.cdsframework.fhir.Coding;
import org.cdsframework.ice.cdsrr.dto.CdsRequest;
import org.cdsframework.ice.cdsrr.dto.Observation;
import org.cdsframework.ice.cdsrr.dto.Patient;
import org.cdsframework.ice.service.SupportingDataService;
import org.junit.jupiter.api.Test;
import org.omg.dss.EntityIdentifier;

class CdsRequestResponseConversionComponentTest
{
    private static final String KM_ID = "org.nyc.cir^ICE^1.0.0";

    private static CodeableConcept codeableConcept(final String system, final String code, final String display)
    {
        return CodeableConcept.builder().coding(Coding.builder().system(system).code(code).display(display).build()).build();
    }

    private static EntityIdentifier entityIdentifier()
    {
        final EntityIdentifier entityIdentifier = new EntityIdentifier();
        entityIdentifier.setScopingEntityId("org.nyc.cir");
        entityIdentifier.setBusinessId("ICE");
        entityIdentifier.setVersion("1.0.0");
        return entityIdentifier;
    }

    @Test
    void preservesLegacyObservationCodingsInVmrPayload()
    {
        final SupportingDataService supportingDataService = mock(SupportingDataService.class);
        when(supportingDataService.parseKmEntityIdentifier(KM_ID)).thenReturn(entityIdentifier());
        final CdsRequestResponseConversionComponent conversionComponent =
                new CdsRequestResponseConversionComponent(supportingDataService);
        final CdsRequest request = new CdsRequest(Patient.builder().id("patient-123").birthDate(LocalDate.of(1990, 5, 15)).build(),
                LocalDate.of(2025, 8, 7), null, List.of(), List.of(Observation.builder()
                .valueDateTime(LocalDate.of(2010, 6, 15))
                .code(codeableConcept("2.16.840.1.113883.6.103", "070.30", "Viral hepatitis B without mention of hepatic coma"))
                .valueCodeableConcept(
                        codeableConcept("2.16.840.1.113883.3.795.12.100.8", "DISEASE_DOCUMENTED", "Disease Documented"))
                .build()));

        final byte[] payload = conversionComponent.convertToEvaluateAtSpecifiedTime(KM_ID, request)
                .getEvaluationRequest()
                .getDataRequirementItemData()
                .getFirst()
                .getData()
                .getBase64EncodedPayload()
                .getFirst();
        final String xml = new String(payload, StandardCharsets.UTF_8);

        assertTrue(xml.contains("code=\"070.30\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.6.103\""));
        assertTrue(xml.contains("code=\"DISEASE_DOCUMENTED\""));
        assertTrue(xml.contains("codeSystem=\"2.16.840.1.113883.3.795.12.100.8\""));
        assertTrue(xml.contains("low=\"20100615\"") && xml.contains("high=\"20100615\""));
    }
}
