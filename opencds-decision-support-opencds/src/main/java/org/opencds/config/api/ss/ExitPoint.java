package org.opencds.config.api.ss;

import java.util.List;
import java.util.Map;

import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;

public interface ExitPoint
{
    byte[] buildOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results,
            EvaluationRequestKMItem dssRequestKMItem);
}
