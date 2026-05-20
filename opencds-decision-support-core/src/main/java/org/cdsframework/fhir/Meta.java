package org.cdsframework.fhir;

import java.util.List;

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
}
