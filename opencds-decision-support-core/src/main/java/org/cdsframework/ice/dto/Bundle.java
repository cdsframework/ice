package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Bundle(String resourceType,
                     String id,
                     Meta meta,
                     String implicitRules,
                     String language,
                     Narrative text,
                     Identifier identifier,
                     String type,
                     String timestamp,
                     Integer total,
                     @Singular("link")
                     List<BundleLink> link,
                     @Singular("entry")
                     List<BundleEntry> entry,
                     FhirResource issues) implements FhirResource
{
    public Bundle
    {
        resourceType = resourceType == null ? "Bundle" : resourceType;
        link = Objects.requireNonNullElseGet(link, List::of);
        entry = Objects.requireNonNullElseGet(entry, List::of);
    }
}
