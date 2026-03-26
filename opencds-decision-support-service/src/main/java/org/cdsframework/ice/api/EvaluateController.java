package org.cdsframework.ice.api;

import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.Evaluate;
import org.omg.dss.EvaluateAtSpecifiedTime;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.EvaluationRequest;
import org.omg.dss.EvaluationResponse;
import org.omg.dss.KMEvaluationRequestBase;
import org.omg.dss.ServiceRequestBase;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.opencds.dss.evaluate.impl.DSSEvaluation;
import org.opencds.dss.evaluate.util.DssUtil;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Web Service
 *
 * @author sdn
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resources")
public class EvaluateController
{
    private static String getEvalInfo(final EvaluateAtSpecifiedTime eval)
    {
        return getEvalInfo(eval, eval.getEvaluationRequest());
    }

    private static String getEvalInfo(final Evaluate eval)
    {
        return getEvalInfo(eval, eval.getEvaluationRequest());
    }

    private static String getEvalInfo(final ServiceRequestBase reqBase, final EvaluationRequest evalReq)
    {
        return "%s:%s".formatted(reqBase.getInteractionId().getInteractionId(), evalReq.getKmEvaluationRequest()
                .stream()
                .map(KMEvaluationRequestBase::getKmId)
                .filter(Objects::nonNull)
                .map(DssUtil::makeEIString)
                .collect(Collectors.joining(",")));
    }

    private final DSSEvaluation evaluationService;

    @GetMapping(value = "/tz", produces = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    public String tz()
    {
        return TimeZone.getDefault().getID();
    }

    /**
     * Retrieves representation of an instance of
     * org.cdsframework.ice.api.EvaluateResource
     */
    @PostMapping(value = "/evaluate", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
                 produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public EvaluationResponse evaluate(@RequestBody final Evaluate evaluate)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        final String METHODNAME = "evaluate ";

        long evalTime = -1;
        boolean success = false;

        final StopWatch timer = new StopWatch();

        try
        {
            timer.start("evaluate");
            final EvaluationResponse evaluateResponse =
                    evaluationService.evaluate(evaluate.getInteractionId(), evaluate.getEvaluationRequest());
            timer.stop();
            evalTime = timer.lastTaskInfo().getTimeMillis();

            success = true;
            return evaluateResponse;
        }
        finally
        {
            log.debug("{} eval={}; success={}; evalTime={}; totalTime={}", METHODNAME, getEvalInfo(evaluate), success, evalTime,
                    timer.getTotalTimeMillis());
        }
    }

    /**
     * Retrieves representation of an instance of
     * org.cdsframework.ice.api.EvaluateResource
     */
    @PostMapping(value = "/evaluateAtSpecifiedTime",
                 consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
                 produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public EvaluationResponse evaluateAtSpecifiedTime(@RequestBody final EvaluateAtSpecifiedTime evaluateAtSpecifiedTime)
            throws UnrecognizedScopedEntityExceptionFault, EvaluationExceptionFault, DSSRuntimeExceptionFault
    {
        final String METHODNAME = "evaluateAtSpecifiedTime ";

        long evalTime = -1;
        boolean success = false;

        final StopWatch timer = new StopWatch();

        try
        {
            timer.start("evaluate");
            final EvaluationResponse evaluateAtSpecifiedTimeResponse =
                    evaluationService.evaluateAtSpecifiedTime(evaluateAtSpecifiedTime.getInteractionId(),
                            evaluateAtSpecifiedTime.getSpecifiedTime(), evaluateAtSpecifiedTime.getEvaluationRequest());
            timer.stop();
            evalTime = timer.lastTaskInfo().getTimeMillis();

            success = true;
            return evaluateAtSpecifiedTimeResponse;
        }
        finally
        {
            log.debug("{} eval={}; success={}; evalTime={}; totalTime={}", METHODNAME, getEvalInfo(evaluateAtSpecifiedTime),
                    success, evalTime, timer.getTotalTimeMillis());
        }
    }
}
