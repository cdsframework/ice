package org.cdsframework.ice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record Extension(String url,
                        @Singular("extension")
                        List<Extension> extension,
                        String valueString,
                        String valueCode,
                        String valueDate,
                        String valueDateTime,
                        Boolean valueBoolean,
                        Integer valueInteger,
                        BigDecimal valueDecimal,
                        CodeableConcept valueCodeableConcept,
                        Coding valueCoding,
                        Identifier valueIdentifier,
                        Reference valueReference)
{
    public Extension
    {
        extension = Objects.requireNonNullElseGet(extension, List::of);
    }
}
