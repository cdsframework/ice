package org.cdsframework.ice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Identifier
{
    private String use;
    private CodeableConcept type;
    private String system;
    private String value;
}
