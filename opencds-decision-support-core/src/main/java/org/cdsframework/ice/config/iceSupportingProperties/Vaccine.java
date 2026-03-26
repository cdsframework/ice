package org.cdsframework.ice.config.iceSupportingProperties;

import jakarta.validation.constraints.NotEmpty;

public record Vaccine(@NotEmpty
                      String code,
                      @NotEmpty
                      String codeSystem,
                      @NotEmpty
                      String codeSystemName,
                      @NotEmpty
                      String displayName) implements BaseConceptDescriptor
{
    @Override
    public String toString()
    {
        return String.format("CD: \n\t.getCode(): %s\n\t.getCodeSystem(): %s\n\t.geCodeSystemName(): %s\n\t.getDisplayName(): %s",
                code, codeSystem, codeSystemName, displayName);
    }
}
