package org.cdsframework.ice.api;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.cdsframework.fhir.CodeSystem;
import org.cdsframework.fhir.PlanDefinition;
import org.cdsframework.ice.config.CdsEngineProperties;
import org.cdsframework.ice.service.SupportingDataService;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/cds/supporting-data")
@Tag(name = "Supporting Data", description = "Read-only endpoints for ICE module plan definitions and supporting data.")
public class SupportingDataController
{
    private static final String MODULE_CANONICAL_EXAMPLE = "http://cdsframework.org/PlanDefinition/ice-forecast";
    private static final String MODULE_VERSION_EXAMPLE = "1.0.0";
    private static final String CODE_SYSTEM_NAME_EXAMPLE = "SUPPORTED_VACCINES";

    private final CdsEngineProperties cdsEngineProperties;
    private final SupportingDataService supportingDataService;

    @Operation(operationId = "getModulePlanDefinitions", summary = "List supported module PlanDefinitions",
               description = "Returns all configured PlanDefinition resources that identify supported ICE modules.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "PlanDefinitions returned successfully.",
                                         content = @Content(mediaType = "application/json", array = @ArraySchema(
                                                 schema = @Schema(implementation = PlanDefinition.class)),
                                                            examples = @ExampleObject(name = "PlanDefinitions",
                                                                                      value = SupportingDataOpenApiExamples.MODULE_PLAN_DEFINITIONS_RESPONSE))) })
    @GetMapping("/module-plan-definitions")
    public Collection<PlanDefinition> getModulePlanDefinitions()
    {
        return cdsEngineProperties.getModuleCanonicalDefinitionMap()
                .values()
                .stream()
                .map(CdsEngineProperties.ModuleCanonicalDefinition::modulePlanDefinition)
                .filter(Objects::nonNull)
                .toList();
    }

    @Operation(operationId = "getDiseasesCodeSystem", summary = "Get diseases code system")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Diseases code system returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = CodeSystem.class),
                                                            examples = @ExampleObject(name = "CodeSystem",
                                                                                      value = SupportingDataOpenApiExamples.CODE_SYSTEM_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module or code system was not found.") })
    @GetMapping("/diseases")
    public CodeSystem diseases(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion)
    {
        return supportingDataService.lookupCodeSystemFromCanonicalUrlVersion(moduleCanonical, moduleVersion,
                ICEConceptType.DISEASE.getIceConceptTypeValue());
    }

    @Operation(operationId = "getVaccineGroupsCodeSystem", summary = "Get vaccine-groups code system")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Vaccine-groups code system returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = CodeSystem.class),
                                                            examples = @ExampleObject(name = "CodeSystem",
                                                                                      value = SupportingDataOpenApiExamples.CODE_SYSTEM_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module or code system was not found.") })
    @GetMapping("/vaccine-groups")
    public CodeSystem vaccineGroups(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion)
    {
        return supportingDataService.lookupCodeSystemFromCanonicalUrlVersion(moduleCanonical, moduleVersion,
                ICEConceptType.VACCINE_GROUP.getIceConceptTypeValue());
    }

    @Operation(operationId = "getVaccinesCodeSystem", summary = "Get vaccines code system")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Vaccines code system returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = CodeSystem.class),
                                                            examples = @ExampleObject(name = "CodeSystem",
                                                                                      value = SupportingDataOpenApiExamples.CODE_SYSTEM_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module or code system was not found.") })
    @GetMapping("/vaccines")
    public CodeSystem vaccines(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion)
    {
        return supportingDataService.lookupCodeSystemFromCanonicalUrlVersion(moduleCanonical, moduleVersion,
                ICEConceptType.VACCINE.getIceConceptTypeValue());
    }

    @Operation(operationId = "getSeasonsCodeSystem", summary = "Get seasons code system")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Seasons code system returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = CodeSystem.class),
                                                            examples = @ExampleObject(name = "CodeSystem",
                                                                                      value = SupportingDataOpenApiExamples.CODE_SYSTEM_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module or code system was not found.") })
    @GetMapping("/seasons")
    public CodeSystem seasons(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion)
    {
        return supportingDataService.lookupCodeSystemFromCanonicalUrlVersion(moduleCanonical, moduleVersion,
                ICEConceptType.SEASON.getIceConceptTypeValue());
    }

    @Operation(operationId = "getSeriesCodeSystem", summary = "Get series code system")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Series code system returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = CodeSystem.class),
                                                            examples = @ExampleObject(name = "CodeSystem",
                                                                                      value = SupportingDataOpenApiExamples.CODE_SYSTEM_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module or code system was not found.") })
    @GetMapping("/series")
    public CodeSystem series(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion)
    {
        return supportingDataService.lookupCodeSystemFromCanonicalUrlVersion(moduleCanonical, moduleVersion,
                ICEConceptType.SERIES.getIceConceptTypeValue());
    }

    @Operation(operationId = "getSeriesPlanDefinitions", summary = "Get series PlanDefinition resources")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Series PlanDefinitions returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = Map.class),
                                                            examples = @ExampleObject(name = "SeriesPlanDefinitions",
                                                                                      value = SupportingDataOpenApiExamples.SERIES_PLAN_DEFINITIONS_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module was not found.") })
    @GetMapping("/series-plan-definition")
    public Collection<PlanDefinition> seriesPlanDefinitions(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion)
    {
        return supportingDataService.getKnowledgeModuleFromCanonicalUrlVersion(moduleCanonical, moduleVersion)
                .planDefinitions()
                .values();
    }

    @Operation(operationId = "getCodeSystemByName", summary = "Get a code system by name")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Code system returned successfully.",
                                         content = @Content(mediaType = "application/json",
                                                            schema = @Schema(implementation = CodeSystem.class),
                                                            examples = @ExampleObject(name = "CodeSystem",
                                                                                      value = SupportingDataOpenApiExamples.CODE_SYSTEM_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Module or code system was not found.") })
    @GetMapping("/code-system")
    public CodeSystem codeSystem(
            @Parameter(description = "PlanDefinition canonical URL without version.", example = MODULE_CANONICAL_EXAMPLE)
            @RequestParam @NotBlank final String moduleCanonical,
            @Parameter(description = "PlanDefinition version.", example = MODULE_VERSION_EXAMPLE) @RequestParam @NotBlank
            final String moduleVersion,
            @Parameter(description = "Code system name.", example = CODE_SYSTEM_NAME_EXAMPLE) @RequestParam @NotBlank
            final String name)
    {
        return supportingDataService.lookupCodeSystemFromCanonicalUrlVersion(moduleCanonical, moduleVersion, name);
    }
}
