package org.cdsframework.ice.dto;

import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Singular;

@Builder
public record CodeableConcept(@Singular("coding")
                              List<Coding> coding,
                              String text)
{
    public CodeableConcept
    {
        coding = Objects.requireNonNullElseGet(coding, List::of);
    }
}
