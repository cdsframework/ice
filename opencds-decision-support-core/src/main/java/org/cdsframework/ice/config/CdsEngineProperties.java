package org.cdsframework.ice.config;

import java.util.Map;
import java.util.Objects;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.supportingdata.SeriesData;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Validated
@Component
@ConfigurationProperties("cds-engine")
public class CdsEngineProperties
{
    public record ExperimentalFeature(boolean enableFhirR6)
    {
    }

    public record ModuleCanonicalDefinition(@Valid
                                            PlanDefinition modulePlanDefinition,
                                            Map<String, @Valid PlanDefinition> planDefinitions,
                                            Map<String, @Valid SeriesData> series,
                                            Map<String, @Valid CodeSystem> codeSystems,
                                            Map<String, @Valid String> outboundCodeSystemMap)
    {
        public ModuleCanonicalDefinition
        {
            planDefinitions = Objects.requireNonNullElseGet(planDefinitions, Map::of);
            series = Objects.requireNonNullElseGet(series, Map::of);
            codeSystems = Objects.requireNonNullElseGet(codeSystems, Map::of);
            outboundCodeSystemMap = Objects.requireNonNullElseGet(outboundCodeSystemMap, Map::of);
        }
    }

    @NotNull
    private ExperimentalFeature experimentalFeatures;

    private Map<@NotBlank String, @Valid ModuleCanonicalDefinition> moduleCanonicalDefinitionMap;
}