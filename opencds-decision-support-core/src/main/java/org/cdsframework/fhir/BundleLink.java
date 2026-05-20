package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record BundleLink(String relation,
                         String url)
{
}
