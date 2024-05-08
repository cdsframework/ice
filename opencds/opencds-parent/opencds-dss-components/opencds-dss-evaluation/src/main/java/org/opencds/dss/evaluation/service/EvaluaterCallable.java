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

package org.opencds.dss.evaluation.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;

@Deprecated(forRemoval = true)
public class EvaluaterCallable implements Callable<EvaluationResponseKMItem> {
    private static final Log log = LogFactory.getLog(EvaluaterCallable.class);

    private final Evaluater evaluater;
    private final KnowledgeRepository knowledgeRepository;
    private final EvaluationRequestKMItem evaluationRequestKMItem;

    public EvaluaterCallable(Evaluater evaluater, KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem evaluationRequestKMItem) {
        this.evaluater = evaluater;
        this.knowledgeRepository = knowledgeRepository;
        this.evaluationRequestKMItem = evaluationRequestKMItem;
    }

    @Override
    public EvaluationResponseKMItem call() throws Exception {
        Map<String, List<?>> resultFactLists = evaluater.getOneResponse(knowledgeRepository, evaluationRequestKMItem);
        return new EvaluationResponseKMItem(resultFactLists, evaluationRequestKMItem);
    }

}
