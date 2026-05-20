package org.cdsframework.fhir;

import java.util.List;

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
    public static final String RESOURCE_TYPE = "Bundle";

    public Bundle
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
