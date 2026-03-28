package org.cdsframework.ice.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeableConcept
{
    private List<Coding> coding;
    private String text;
}
