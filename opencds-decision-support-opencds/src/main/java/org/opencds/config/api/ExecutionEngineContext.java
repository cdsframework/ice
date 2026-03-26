package org.opencds.config.api;

import java.util.List;
import java.util.Map;

public interface ExecutionEngineContext<Input, Output>
{
    Input getInput();

    Map<String, List<?>> getResults();

    ExecutionEngineContext<Input, Output> setResults(Output results);

    ExecutionEngineContext<Input, Output> setEvaluationContext(EvaluationContext evaluationContext);
}
