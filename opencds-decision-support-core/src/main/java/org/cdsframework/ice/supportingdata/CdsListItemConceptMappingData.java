package org.cdsframework.ice.supportingdata;

import jakarta.validation.constraints.NotEmpty;

public record CdsListItemConceptMappingData(@NotEmpty
                                            String code,
                                            @NotEmpty
                                            String displayName,
                                            String codeSystem,
                                            String codeSystemName,
                                            String conceptDeterminationMethod)
{
}
