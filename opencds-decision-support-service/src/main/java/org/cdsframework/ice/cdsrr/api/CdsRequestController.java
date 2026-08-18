package org.cdsframework.ice.cdsrr.api;

import java.time.LocalDateTime;
import java.util.List;

import org.cdsframework.ice.cdsrr.CdsRequestResponseConversionComponent;
import org.cdsframework.ice.cdsrr.dto.CdsRequest;
import org.cdsframework.ice.cdsrr.dto.CdsResponse;
import org.cdsframework.ice.cdsrr.dto.Observation;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.opencds.dss.evaluate.Evaluation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cds")
public class CdsRequestController
{
    private static void validateObservations(final List<Observation> observations)
    {
        if (observations == null)
            return;

        for (int index = 0; index < observations.size(); index++)
        {
            final Observation observation = observations.get(index);
            if (observation != null && observation.valueDateTime() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "observations[%d] must provide valueDateTime or effectiveDateTime".formatted(index));
        }
    }

    private final Evaluation evaluationService;
    private final CdsRequestResponseConversionComponent cdsRequestResponseConversionComponent;

    @PostMapping("/evaluateAtSpecifiedTime")
    public CdsResponse evaluateAtSpecifiedTime(@RequestBody @Valid @NotNull final CdsRequest cdsRequest)
            throws EvaluationExceptionFault, UnrecognizedScopedEntityExceptionFault, DSSRuntimeExceptionFault
    {
        validateObservations(cdsRequest.observations());
        final LocalDateTime requestDateTime = LocalDateTime.now();
        final String kmId = "org.nyc.cir^ICE^1.0.0";
        final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime =
                cdsRequestResponseConversionComponent.convertToEvaluateAtSpecifiedTime(kmId, cdsRequest);
        return cdsRequestResponseConversionComponent.convertToCdsResponse(kmId,
                evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime.getInteractionId(),
                        evaluateAtSpecifiedTime.getSpecifiedTime(), evaluateAtSpecifiedTime.getEvaluationRequest()), cdsRequest,
                requestDateTime);
    }
}
