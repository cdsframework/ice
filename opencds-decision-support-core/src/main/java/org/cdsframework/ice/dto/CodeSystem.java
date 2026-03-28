package org.cdsframework.ice.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeSystem
{
    private String name;
    private List<Identifier> identifiers;
    private String description;
    private String url;
    private String title;
    private String version;
    private PublicationStatusEnum status;
    private CodeSystemContentModeEnum content;
    private List<CodeSystemResourceProperty> properties;
    private List<CodeSystemConcept> concepts;
}
