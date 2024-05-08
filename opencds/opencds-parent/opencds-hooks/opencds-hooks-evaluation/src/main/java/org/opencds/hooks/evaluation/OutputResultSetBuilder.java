/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.hooks.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.hooks.model.response.CdsResponse;

public class OutputResultSetBuilder implements ResultSetBuilder<CdsResponse> {

	@Override
	public CdsResponse buildResultSet(Map<String, List<?>> results, EvaluationRequestKMItem dssRequestKMItem) {
		List<CdsResponse> resultList = (List<CdsResponse>) results.getOrDefault("CdsResponse", new ArrayList<>());
		return resultList.stream().findFirst().orElse(new CdsResponse());
	}

}
