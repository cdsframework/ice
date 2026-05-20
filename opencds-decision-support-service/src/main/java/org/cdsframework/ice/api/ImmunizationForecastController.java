package org.cdsframework.ice.api;

import java.time.LocalDateTime;

import org.cdsframework.fhir.Parameters;
import org.cdsframework.ice.service.conversion.VmrConversionComponent;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.opencds.dss.evaluate.Evaluation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
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
@Tag(name = "Immunization Forecast", description = "FHIR operation endpoint for immunization forecast requests.")
public class ImmunizationForecastController
{
    private static final String FHIR_JSON_MEDIA_TYPE = "application/fhir+json";
    private final Evaluation evaluationService;
    private final VmrConversionComponent vmrConversionComponent;

    @Operation(operationId = "immunizationForecast", summary = "Run immunization forecast",
               description = "Accepts a FHIR Parameters request and returns forecast output as FHIR Parameters. Request supports `patient`, `assessmentDate`, `module`, repeated `immunization`, repeated `observation`, or a single `data` bundle containing those resources.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                                                          description = "FHIR Parameters request for immunization forecast processing.",
                                                          content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                                               schema = @Schema(implementation = Parameters.class),
                                                                               examples = @ExampleObject(name = "ForecastRequest",
                                                                                                         summary = "Request with patient, immunization and observation inputs",
                                                                                                         value = ImmunizationForecastOpenApiExamples.REQUEST_PARAMETERS)),
                                                                  @Content(mediaType = FHIR_JSON_MEDIA_TYPE,
                                                                           schema = @Schema(implementation = Parameters.class),
                                                                           examples = @ExampleObject(
                                                                                   name = "ForecastRequestFhirJson",
                                                                                   summary = "Same request using application/fhir+json",
                                                                                   value = ImmunizationForecastOpenApiExamples.REQUEST_PARAMETERS)) })
    @ApiResponses(value = { @ApiResponse(responseCode = "200",
                                         description = "Forecast completed. Response always returns FHIR Parameters; operation errors are conveyed in operationOutcome.",
                                         content = { @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                              schema = @Schema(implementation = Parameters.class), examples = {
                                                 @ExampleObject(name = "Success",
                                                                value = ImmunizationForecastOpenApiExamples.RESPONSE_SUCCESS),
                                                 @ExampleObject(name = "HandledError",
                                                                value = ImmunizationForecastOpenApiExamples.RESPONSE_ERROR) }),
                                                 @Content(mediaType = FHIR_JSON_MEDIA_TYPE,
                                                          schema = @Schema(implementation = Parameters.class), examples = {
                                                         @ExampleObject(name = "SuccessFhirJson",
                                                                        value = ImmunizationForecastOpenApiExamples.RESPONSE_SUCCESS),
                                                         @ExampleObject(name = "HandledErrorFhirJson",
                                                                        value = ImmunizationForecastOpenApiExamples.RESPONSE_ERROR) }) }),
            @ApiResponse(responseCode = "400",
                         description = "Malformed JSON/FHIR payload or bean validation failure before controller execution.") })
    @PostMapping(value = "/$immunization-forecast", consumes = { MediaType.APPLICATION_JSON_VALUE, FHIR_JSON_MEDIA_TYPE },
                 produces = { MediaType.APPLICATION_JSON_VALUE, FHIR_JSON_MEDIA_TYPE })
    public Parameters immunizationForecast(@RequestBody @Valid @NotNull final Parameters parameters)
    {
        final LocalDateTime requestDateTime = LocalDateTime.now();
        try
        {
            final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime =
                    vmrConversionComponent.convertToEvaluateAtSpecifiedTime(parameters);
            return vmrConversionComponent.convertToParametersResponse(
                    evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime.getInteractionId(),
                            evaluateAtSpecifiedTime.getSpecifiedTime(), evaluateAtSpecifiedTime.getEvaluationRequest()), parameters,
                    requestDateTime);
        }
        catch (final Exception e)
        {
            log.error("FHIR forecast request processing failed", e);
            final String detail = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            return vmrConversionComponent.createErrorParametersResponse("exception", detail, requestDateTime);
        }
    }
}
