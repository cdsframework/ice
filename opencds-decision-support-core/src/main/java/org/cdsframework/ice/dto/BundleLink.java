package org.cdsframework.ice.dto;

import lombok.Builder;

@Builder
public record BundleLink(String relation,
                         String url)
{
}
