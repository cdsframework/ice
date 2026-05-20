package org.cdsframework.ice.supportingdata;

import org.jspecify.annotations.NonNull;

import jakarta.validation.constraints.NotEmpty;

public record VaccineGroup(@NotEmpty
                           String code,
                           String codeSystem,
                           String codeSystemName,
                           String displayName) implements BaseConceptDescriptor
{
    @NonNull
    @Override
    public String toString()
    {
        return String.format(
                "CD: \n\t.getCode(): %s\n\t.getCodeSystem(): %s\n\t.geCodeSystemName(): %s\n\t.getDisplayName(): %s\n\t.getOriginalText(): null",
                code, codeSystem, codeSystemName, displayName);
    }
}
