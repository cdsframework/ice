package org.opencds.common.interfaces;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.opencds.common.structures.EvaluationRequestKMItem;

/**
 * Provides a basic API for the exit point for vMR (or other XML strutures) at the OpenCDS Web Service layer.
 * 
 * Essentially, this provides a hook into building the output XML.
 * 
 * @author phillip
 *
 * @param <T>
 */
public interface ModelExitPoint<T> {

    JAXBElement<T> createOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem);

}
