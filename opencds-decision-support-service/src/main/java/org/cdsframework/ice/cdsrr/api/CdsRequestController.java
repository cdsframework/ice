package org.cdsframework.ice.cdsrr.api;

import java.time.LocalDateTime;

import org.cdsframework.ice.cdsrr.CdsRequestResponseConversionComponent;
import org.cdsframework.ice.cdsrr.dto.CdsRequest;
import org.cdsframework.ice.cdsrr.dto.CdsResponse;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.opencds.dss.evaluate.Evaluation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final Evaluation evaluationService;
    private final CdsRequestResponseConversionComponent cdsRequestResponseConversionComponent;

    @PostMapping("/evaluateAtSpecifiedTime")
    public CdsResponse evaluateAtSpecifiedTime(@RequestBody @Valid @NotNull final CdsRequest cdsRequest)
            throws EvaluationExceptionFault, UnrecognizedScopedEntityExceptionFault, DSSRuntimeExceptionFault
    {
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
