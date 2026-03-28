package org.cdsframework.ice.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeSystemConcept
{
    private String code;
    private String display;
    private List<CodeSystemConceptProperty> properties;
}
