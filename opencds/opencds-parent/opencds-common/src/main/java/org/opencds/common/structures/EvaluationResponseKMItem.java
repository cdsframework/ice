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

package org.opencds.common.structures;

import java.util.List;
import java.util.Map;

public class EvaluationResponseKMItem {

    private final Map<String, List<?>> resultFactLists;
    private final EvaluationRequestKMItem evaluationRequestKMItem;

    public EvaluationResponseKMItem(Map<String, List<?>> resultFactLists, EvaluationRequestKMItem request) {
        this.resultFactLists = resultFactLists;
        this.evaluationRequestKMItem = request;
    }

    public Map<String, List<?>> getResultFactLists() {
        return resultFactLists;
    }

    public EvaluationRequestKMItem getEvaluationRequestKMItem() {
        return evaluationRequestKMItem;
    }

}
