package org.cdsframework.fhir;

import java.util.List;

import lombok.Builder;
import lombok.Singular;

@Builder
public record CodeSystemConcept(String code,
                                String display,
                                @Singular("property")
                                List<CodeSystemConceptProperty> property)
{
}
