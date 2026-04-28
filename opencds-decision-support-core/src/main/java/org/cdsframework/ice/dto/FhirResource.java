package org.cdsframework.ice.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "resourceType", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = Parameters.class, name = "Parameters"),
        @JsonSubTypes.Type(value = Patient.class, name = "Patient"),
        @JsonSubTypes.Type(value = Immunization.class, name = "Immunization"),
        @JsonSubTypes.Type(value = Observation.class, name = "Observation"),
        @JsonSubTypes.Type(value = Bundle.class, name = "Bundle"),
        @JsonSubTypes.Type(value = GuidanceResponse.class, name = "GuidanceResponse"),
        @JsonSubTypes.Type(value = ImmunizationEvaluation.class, name = "ImmunizationEvaluation"),
        @JsonSubTypes.Type(value = ImmunizationRecommendation.class, name = "ImmunizationRecommendation"),
        @JsonSubTypes.Type(value = OperationOutcome.class, name = "OperationOutcome") })
public interface FhirResource
{
    String resourceType();
}
