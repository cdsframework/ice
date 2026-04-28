package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Meta(String versionId,
                   String lastUpdated,
                   String source,
                   @Singular("profile")
                   List<String> profile,
                   @Singular("security")
                   List<Coding> security,
                   @Singular("tag")
                   List<Coding> tag)
{
    public Meta
    {
        profile = Objects.requireNonNullElseGet(profile, List::of);
        security = Objects.requireNonNullElseGet(security, List::of);
        tag = Objects.requireNonNullElseGet(tag, List::of);
    }
}
