/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.common.interfaces;

import java.util.List;
import java.util.Map;

import org.opencds.common.structures.EvaluationRequestKMItem;

/**
 * The implementation of this interface accepts the processed fact lists from
 * the inferencing engine, and produces a structured output in the CDSOutput
 * structure. This structure is then further processed by the implementation of
 * the OutboundPayloadProcessor interface to produce the response payload.
 * 
 * @author David Shields
 *
 * @version 1.0
 */
public interface ResultSetBuilder<T> {

    /**
     * @param globals
     * @param evalTime
     * @return
     */
    T buildResultSet(Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem);
}
