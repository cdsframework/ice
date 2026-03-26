package org.cdsframework.ice.config.iceSupportingProperties;

import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CdsListItemData(@NotEmpty
                              String cdsListItemKey,
                              @NotEmpty
                              String cdsListItemValue,
                              Map<String, @Valid CdsListItemConceptMappingData> cdsListItemConceptMapping,
                              @Valid
                              ConceptDescriptor outboundCoding)
{
}
