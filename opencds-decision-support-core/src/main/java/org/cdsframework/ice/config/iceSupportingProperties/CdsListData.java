package org.cdsframework.ice.config.iceSupportingProperties;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CdsListData(@NotEmpty
                          String listId,
                          @NotEmpty
                          String code,
                          @NotEmpty
                          String name,
                          @NotEmpty
                          String listType,
                          String description,
                          String enumClass,
                          String codeSystem,
                          String codeSystemName,
                          String valueSet,
                          String openCdsConceptType,
                          Map<String, @Valid CdsListItemData> cdsListItem,
                          @NotEmpty
                          Map<String, @NotEmpty String> cdsVersion)
{
}
