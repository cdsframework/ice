package org.cdsframework.fhir;

import lombok.Builder;

@Builder
public record Reference(String reference,
                        String type,
                        Identifier identifier,
                        String display)
{
}
