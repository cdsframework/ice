package org.cdsframework.ice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coding
{
    private String system;
    private String version;
    private String code;
    private String display;
    private Boolean userSelected;
}
