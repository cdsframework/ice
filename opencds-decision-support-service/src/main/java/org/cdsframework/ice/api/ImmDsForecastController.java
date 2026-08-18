package org.cdsframework.ice.api;

import java.time.LocalDateTime;

import org.cdsframework.fhir.Parameters;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.opencds.dss.evaluate.Evaluation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cds")
@ConditionalOnProperty(prefix = "cds-engine.experimental-features", name = "enable-fhir-r6", havingValue = "true")
@Tag(name = "ImmDS Forecast", description = "FHIR operation endpoint for proposed R6 HLN ImmDS forecast requests.")
public class ImmDsForecastController
{
    private static final String FHIR_JSON_MEDIA_TYPE = "application/fhir+json";

    private final Evaluation evaluationService;
    private final VmrConversionComponent vmrConversionComponent;
    private final CapabilityStatementProvider capabilityStatementProvider;

    @Operation(operationId = "capabilityStatement", summary = "Get service capability statement",
               description = "Returns the CapabilityStatement for the FHIR service base, including the system-level ImmDS forecast operation.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "CapabilityStatement returned successfully.",
                                         content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                              schema = @Schema(implementation = String.class)),
                                                 @Content(mediaType = FHIR_JSON_MEDIA_TYPE,
                                                          schema = @Schema(implementation = String.class)) }) })
    @GetMapping(value = "/metadata", produces = { MediaType.APPLICATION_JSON_VALUE, FHIR_JSON_MEDIA_TYPE })
    public String capabilityStatement()
    {
        return capabilityStatementProvider.capabilityStatement();
    }

    @Operation(operationId = "immDsForecast", summary = "Run ImmDS forecast",
               description = "Accepts a FHIR Parameters request for `/$immds-forecast` and returns forecast output as FHIR Parameters. Request supports `patient`, `assessmentDate`, `knowledgeBase`, repeated `immunization`, repeated `observation`, or a single `data` bundle containing those resources. Response parameters are returned at the top level without an `output.part` wrapper.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                                          description = "FHIR Parameters request for proposed HLN ImmDS forecast processing.",
                                                          content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                               schema = @Schema(implementation = Parameters.class),
                                                                               examples = @ExampleObject(
                                                                                       name = "ImmDsForecastRequest",
                                                                                       summary = "Request with patient, immunization, observation, and knowledge base inputs",
                                                                                       value = ImmDsForecastOpenApiExamples.REQUEST_PARAMETERS)),
                                                                  @Content(mediaType = FHIR_JSON_MEDIA_TYPE,
                                                                           schema = @Schema(implementation = Parameters.class),
                                                                           examples = @ExampleObject(
                                                                                   name = "ImmDsForecastRequestFhirJson",
                                                                                   summary = "Same request using application/fhir+json",
                                                                                   value = ImmDsForecastOpenApiExamples.REQUEST_PARAMETERS)) })
    @ApiResponses(value = { @ApiResponse(responseCode = "200",
                                         description = "Forecast completed. Response always returns FHIR Parameters; operation errors are conveyed in operationOutcome.",
                                         content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                              schema = @Schema(implementation = Parameters.class), examples = {
                                                 @ExampleObject(name = "Success",
                                                                value = ImmDsForecastOpenApiExamples.RESPONSE_SUCCESS),
                                                 @ExampleObject(name = "HandledError",
                                                                value = ImmDsForecastOpenApiExamples.RESPONSE_ERROR) }),
                                                 @Content(mediaType = FHIR_JSON_MEDIA_TYPE,
                                                          schema = @Schema(implementation = Parameters.class), examples = {
                                                         @ExampleObject(name = "SuccessFhirJson",
                                                                        value = ImmDsForecastOpenApiExamples.RESPONSE_SUCCESS),
                                                         @ExampleObject(name = "HandledErrorFhirJson",
                                                                        value = ImmDsForecastOpenApiExamples.RESPONSE_ERROR) }) }),
            @ApiResponse(responseCode = "400",
                         description = "Malformed JSON/FHIR payload or bean validation failure before controller execution.") })
    @PostMapping(value = "/$immds-forecast", consumes = { MediaType.APPLICATION_JSON_VALUE, FHIR_JSON_MEDIA_TYPE },
                 produces = { MediaType.APPLICATION_JSON_VALUE, FHIR_JSON_MEDIA_TYPE })
    public Parameters immDsForecast(@RequestBody @Valid @NotNull final Parameters parameters)
    {
        final LocalDateTime requestDateTime = LocalDateTime.now();
        try
        {
            final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime =
                    vmrConversionComponent.convertImmDsToEvaluateAtSpecifiedTime(parameters);
            return vmrConversionComponent.convertImmDsToParametersResponse(
                    evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime.getInteractionId(),
                            evaluateAtSpecifiedTime.getSpecifiedTime(), evaluateAtSpecifiedTime.getEvaluationRequest()), parameters,
                    requestDateTime);
        }
        catch (final Exception e)
        {
            log.error("FHIR ImmDS forecast request processing failed", e);
            final String detail = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            return vmrConversionComponent.createErrorParametersResponse("exception", detail, requestDateTime);
        }
    }
}
