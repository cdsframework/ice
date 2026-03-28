package org.cdsframework.ice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeSystemConceptProperty
{
    private String code;
    private Coding valueCoding;
    private Boolean valueBoolean;
    private Integer valueInteger;
    private String valueString;

    public Boolean isValueBoolean()
    {
        return valueBoolean;
    }
}
