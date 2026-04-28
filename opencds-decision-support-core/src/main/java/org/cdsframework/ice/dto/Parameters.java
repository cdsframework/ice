package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

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
    public Parameters
    {
        resourceType = resourceType == null ? "Parameters" : resourceType;
        parameter = Objects.requireNonNullElseGet(parameter, List::of);
    }
}
