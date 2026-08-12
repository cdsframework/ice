package org.opencds.dss.evaluate;

import org.omg.dss.EvaluationRequest;

public interface EvaluationRequestPreProcessor
{
    record Result(long payloadCount,
                  long augmentedPayloadCount,
                  long addedScheduleFlagCount)
    {
        public static final Result NOOP = new Result(0L, 0L, 0L);
    }

    Result process(EvaluationRequest evaluationRequest);
}
