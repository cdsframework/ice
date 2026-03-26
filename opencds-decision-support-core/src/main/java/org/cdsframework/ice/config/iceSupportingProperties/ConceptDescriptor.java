package org.cdsframework.ice.config.iceSupportingProperties;

import jakarta.validation.constraints.NotEmpty;

public record ConceptDescriptor(@NotEmpty
                                String code,
                                String codeSystem,
                                String codeSystemName,
                                String displayName,
                                String originalText) implements BaseConceptDescriptor
{
    @Override
    public String toString()
    {
        return String.format(
                "CD: \n\t.getCode(): %s\n\t.getCodeSystem(): %s\n\t.geCodeSystemName(): %s\n\t.getDisplayName(): %s\n\t.getOriginalText(): %s",
                code, codeSystem, codeSystemName, displayName, originalText);
    }
}
