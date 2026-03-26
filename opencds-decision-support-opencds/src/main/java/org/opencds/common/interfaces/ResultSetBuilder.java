package org.opencds.common.interfaces;

import java.util.List;
import java.util.Map;

import org.opencds.common.structures.EvaluationRequestKMItem;

public interface ResultSetBuilder<T>
{
    T buildResultSet(Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem);
}
