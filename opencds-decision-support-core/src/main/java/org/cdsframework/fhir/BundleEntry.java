package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record BundleEntry(FhirResource resource)
{
}
