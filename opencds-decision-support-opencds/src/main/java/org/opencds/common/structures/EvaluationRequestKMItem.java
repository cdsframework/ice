package org.opencds.common.structures;

import java.util.List;
import java.util.Map;

public record EvaluationRequestKMItem(String requestedKmId,
                                      EvaluationRequestDataItem evaluationRequestDataItem,
                                      Map<Class<?>, List<?>> allFactLists)
{
}
