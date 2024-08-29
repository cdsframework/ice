/*
 * Copyright 2015-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.api.ss;

import java.util.List;
import java.util.Map;

import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;

/**
 * Provides a basic API for the exit point for vMR (or other XML strutures) at the OpenCDS Web Service layer.
 * 
 * Essentially, this provides a hook into building the output XML.
 * 
 * @author phillip
 *
 */
public interface ExitPoint {

    byte[] buildOutput(ResultSetBuilder<?> resultSetBuilder, Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem);

}