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

package org.opencds.hooks.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.hooks.engine.api.CdsHooksEvaluationContext;
import org.opencds.hooks.model.request.CdsRequest;
import org.opencds.hooks.model.response.CdsResponse;

public class CdsHooksExecutionEngineContext implements ExecutionEngineContext<CdsHooksInput, CdsResponse> {

    private Map<String, List<?>> resultFactLists = new ConcurrentHashMap<>();
    private EvaluationContext evaluationContext;

    @Override
    public CdsHooksInput getInput() {
        return new CdsHooksInput(
                (CdsRequest) evaluationContext.getAllFactLists().get(CdsRequest.class).stream().findFirst()
                        .orElse(null),
                CdsHooksEvaluationContext.create(evaluationContext.getEvalTime(), evaluationContext.getServerBaseUri(),
                        evaluationContext.getClientLanguage(), evaluationContext.getClientTimeZoneOffset(),
                        evaluationContext.getNamedObjects(), evaluationContext.getAllFactLists(),
                        evaluationContext.getGlobals()));
    }

    @Override
    public ExecutionEngineContext<CdsHooksInput, CdsResponse> setResults(CdsResponse response) {
        resultFactLists.put(CdsResponse.class.getSimpleName(), Collections.singletonList(response));
        return this;
    }

    @Override
    public Map<String, List<?>> getResults() {
        return resultFactLists;
    }

    @Override
    public ExecutionEngineContext<CdsHooksInput, CdsResponse> setEvaluationContext(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
        return this;
    }

}
