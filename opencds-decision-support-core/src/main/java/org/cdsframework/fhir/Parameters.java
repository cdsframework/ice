package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Parameters(String resourceType,
                         String id,
                         Meta meta,
                         String implicitRules,
                         String language,
                         @Singular("parameter")
                         List<ParametersParameter> parameter) implements FhirResource
{
    public static final String RESOURCE_TYPE = "Parameters";

    public Parameters
    {
        resourceType = FhirResource.normalizeResourceType(resourceType, RESOURCE_TYPE);
    }
}
