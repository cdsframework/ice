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

import org.opencds.hooks.engine.api.CdsHooksEvaluationContext;
import org.opencds.hooks.model.request.CdsRequest;

public class CdsHooksInput {
    private final CdsRequest cdsRequest;
    private final CdsHooksEvaluationContext evaluationContext;
    
    public CdsHooksInput(CdsRequest cdsRequest, CdsHooksEvaluationContext evaluationContext) {
        super();
        this.cdsRequest = cdsRequest;
        this.evaluationContext = evaluationContext;
    }

    public CdsRequest getCdsRequest() {
        return cdsRequest;
    }

    public CdsHooksEvaluationContext getEvaluationContext() {
        return evaluationContext;
    }
    
}
