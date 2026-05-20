package org.opencds.config.api;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;

public record EvaluationContext(String focalPersonId,
                                LocalDate evalTime,
                                URI serverBaseUri,
                                String clientLanguage,
                                String clientTimeZoneOffset,
                                Set<String> assertions,
                                Map<String, Object> namedObjects,
                                Map<String, Object> globals,
                                Map<Class<?>, List<?>> allFactLists,
                                String primaryProcess)
{
    public static EvaluationContext create(final EvaluationRequestKMItem evaluationRequestKMItem, final String primaryProcess)
    {
        final EvaluationRequestDataItem evalRequestDataItem = evaluationRequestKMItem.evaluationRequestDataItem();
        return new EvaluationContext(evalRequestDataItem.focalPersonId(), evalRequestDataItem.evalTime(),
                evalRequestDataItem.serverUri(), evalRequestDataItem.clientLanguage(), evalRequestDataItem.clientTimeZoneOffset(),
                ConcurrentHashMap.newKeySet(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(),
                evaluationRequestKMItem.allFactLists(), primaryProcess);
    }
}
