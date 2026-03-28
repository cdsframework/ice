package org.cdsframework.ice.config;

import java.util.Map;

import org.cdsframework.ice.config.iceSupportingProperties.SeriesData;
import org.cdsframework.ice.dto.CodeSystem;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("ice-supporting-data")
public class IceSupportingDataProperties
{
    public record KnowledgeModule(Map<String, @Valid SeriesData> series,
                                  Map<String, CodeSystem> codeSystems)
    {
    }

    private Map<String, @Valid KnowledgeModule> knowledgeModules;
}