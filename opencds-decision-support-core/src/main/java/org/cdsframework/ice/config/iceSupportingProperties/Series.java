package org.cdsframework.ice.config.iceSupportingProperties;

import org.jspecify.annotations.NonNull;

import jakarta.validation.constraints.NotEmpty;

public record Series(@NotEmpty
                     String code,
                     @NotEmpty
                     String codeSystem,
                     @NotEmpty
                     String codeSystemName,
                     @NotEmpty
                     String displayName) implements BaseConceptDescriptor
{
    @NonNull
    @Override
    public String toString()
    {
        return String.format("CD: \n\t.getCode(): %s\n\t.getCodeSystem(): %s\n\t.geCodeSystemName(): %s\n\t.getDisplayName(): %s",
                code, codeSystem, codeSystemName, displayName);
    }
}
