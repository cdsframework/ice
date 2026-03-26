package org.opencds.config.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FhirVersion
{
    DSTU2("org.hl7.fhir.dstu2.model", "DSTU2_HL7ORG"),
    STU3("org.hl7.fhir.dstu3.model", "DSTU3"),
    R4("org.hl7.fhir.r4.model", "R4"),
    R5("org.hl7.fhir.r5.model", "R5");

    private final String resourcePackageName;
    private final String hapiFhirVersionEnumValue;
}
