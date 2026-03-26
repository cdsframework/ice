package org.opencds.common.structures;

import java.util.List;
import java.util.Map;

public record EvaluationResponseKMItem(Map<String, List<?>> resultFactLists,
                                       EvaluationRequestKMItem evaluationRequestKMItem)
{
}
