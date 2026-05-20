package org.cdsframework.fhir;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "resourceType", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = Parameters.class, name = Parameters.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = Patient.class, name = Patient.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = Immunization.class, name = Immunization.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = Observation.class, name = Observation.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = Bundle.class, name = Bundle.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = GuidanceResponse.class, name = GuidanceResponse.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = ImmunizationEvaluation.class, name = ImmunizationEvaluation.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = ImmunizationRecommendation.class, name = ImmunizationRecommendation.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = PlanDefinition.class, name = PlanDefinition.RESOURCE_TYPE),
        @JsonSubTypes.Type(value = OperationOutcome.class, name = OperationOutcome.RESOURCE_TYPE) })
public interface FhirResource
{
    static String normalizeResourceType(final String actual, final String expected)
    {
        if (!StringUtils.hasText(actual))
            return expected;

        if (!expected.equals(actual))
            throw new IllegalArgumentException("Resource type mismatch. Expected '%s' but got '%s'.".formatted(expected, actual));

        return expected;
    }

    String resourceType();

    String id();
}
