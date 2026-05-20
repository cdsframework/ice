package org.cdsframework.ice.cdsrr.dto;

public record ConceptDescriptor(String displayName,
                                String code,
                                String codeSystem,
                                String codeSystemName,
                                String originalText)
{
}
