/*
 * Copyright 2016-2020 OpenCDS.org
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

package org.opencds.dss.evaluation.service.util;

import java.util.concurrent.Callable;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.dss.evaluation.service.Evaluater;
import org.opencds.dss.evaluation.service.EvaluaterCallable;
import org.opencds.evaluation.service.ExecutionEngineAdapterCallable;
import org.opencds.evaluation.service.util.CallableUtil;

public class DSSCallableUtil implements CallableUtil {

	@Override
	public Callable<EvaluationResponseKMItem> getCallable(KnowledgeRepository knowledgeRepository,
			ExecutionEngineService executionEngineService, ExecutionEngine engine,
			EvaluationRequestKMItem evaluationRequestKMItem) {
        ExecutionEngineAdapter<?, ?, ?> adapter = executionEngineService.getExecutionEngineAdapter(engine);
        if (adapter != null) {
            return ExecutionEngineAdapterCallable.create(adapter, engine, knowledgeRepository, evaluationRequestKMItem);
        }
        // FIXME: deprecated flow
        adapter = executionEngineService.getExecutionEngineInstance(engine);
        if (adapter != null && adapter instanceof Evaluater) {
            return new EvaluaterCallable((Evaluater) adapter, knowledgeRepository, evaluationRequestKMItem);
        }
        return null;
	}

}
