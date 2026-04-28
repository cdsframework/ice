package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record BundleEntry(FhirResource resource)
{
}
