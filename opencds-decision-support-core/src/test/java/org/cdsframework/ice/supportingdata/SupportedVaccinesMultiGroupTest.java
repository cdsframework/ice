package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.CodeSystemConcept;
import org.cdsframework.fhir.CodeSystemConceptProperty;
import org.cdsframework.fhir.Coding;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Vaccine;
import org.junit.jupiter.api.Test;

class SupportedVaccinesMultiGroupTest
{
    private static final String CODE_SYSTEM_OID = "2.16.840.1.113883.12.292";

    private static CodeSystemConceptProperty conceptMapping(final String code)
    {
        return CodeSystemConceptProperty.builder()
                .code("conceptMapping")
                .valueCoding(Coding.builder().code("VACCINE_CVX_%s".formatted(code)).build())
                .build();
    }

    private static CodeSystemConceptProperty vaccineComponent(final String code)
    {
        return CodeSystemConceptProperty.builder()
                .code("vaccineComponent")
                .valueCoding(Coding.builder().code(code).system(CODE_SYSTEM_OID).build())
                .build();
    }

    private static CodeSystemConcept vaccineConcept(final String code, final boolean validInMultipleVaccineGroups)
    {
        return CodeSystemConcept.builder()
                .code(code)
                .display("CVX %s".formatted(code))
                .property(conceptMapping(code))
                .property(vaccineComponent(code))
                .property(CodeSystemConceptProperty.builder().code("supported").valueBoolean(true).build())
                .property(CodeSystemConceptProperty.builder()
                        .code("validInMultipleVaccineGroups")
                        .valueBoolean(validInMultipleVaccineGroups)
                        .build())
                .build();
    }

    private static CodeSystemConcept vaccineConceptWithoutMultiGroupProperty(final String code)
    {
        return CodeSystemConcept.builder()
                .code(code)
                .display("CVX %s".formatted(code))
                .property(conceptMapping(code))
                .property(vaccineComponent(code))
                .property(CodeSystemConceptProperty.builder().code("supported").valueBoolean(true).build())
                .build();
    }

    @Test
    void parsesMultiGroupPropertyWhenTrueFalseOrAbsent() throws InconsistentConfigurationException
    {
        final SupportedCdsLists supportedCdsLists = new SupportedCdsLists(List.of());
        supportedCdsLists.addSupportedCodeSystem(CodeSystem.builder()
                .name("SUPPORTED_VACCINES")
                .concept(vaccineConcept("TRUE", true))
                .concept(vaccineConcept("FALSE", false))
                .concept(vaccineConceptWithoutMultiGroupProperty("ABSENT"))
                .build(), CODE_SYSTEM_OID);
        final ICESupportingDataConfiguration configuration = mock(ICESupportingDataConfiguration.class);
        when(configuration.getSupportedCdsLists()).thenReturn(supportedCdsLists);

        final SupportedVaccines supportedVaccines = new SupportedVaccines(configuration);
        supportedVaccines.initializeFromCdsLists();

        assertTrue(supportedVaccines.getVaccineItem("SUPPORTED_VACCINES.TRUE").getVaccine().isValidInMultipleVaccineGroups());
        assertFalse(supportedVaccines.getVaccineItem("SUPPORTED_VACCINES.FALSE").getVaccine().isValidInMultipleVaccineGroups());
        assertFalse(supportedVaccines.getVaccineItem("SUPPORTED_VACCINES.ABSENT").getVaccine().isValidInMultipleVaccineGroups());
    }

    @Test
    void deepCopyPreservesMultiGroupProperty()
    {
        final Vaccine vaccine = new Vaccine(new CdsConcept("VACCINE_CVX_121"));
        vaccine.setValidInMultipleVaccineGroups(true);

        final Vaccine copy = Vaccine.constructDeepCopyOfVaccineObject(vaccine);

        assertNotNull(copy);
        assertTrue(copy.isValidInMultipleVaccineGroups());
    }
}
