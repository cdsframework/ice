package org.cdsframework.ice.config;

import lombok.Builder;

@Builder
public record VersionData(String iceVersion,
                          String gitCommitSha,
                          String buildDate)
{
}
