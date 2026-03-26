package org.cdsframework.ice.service;

import lombok.Builder;

@Builder
public record VersionData(String iceVersion,
                          String gitCommitSha,
                          String buildDate)
{
}
