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

package org.opencds.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.EvaluationContext;
import org.opencds.config.api.ExecutionEngineAdapter;
import org.opencds.config.api.ExecutionEngineContext;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.evaluation.service.util.SupportingDataUtil;

public class ExecutionEngineAdapterCallable<I, O, KP> implements Callable<EvaluationResponseKMItem> {
    private static final Log log = LogFactory.getLog(ExecutionEngineAdapterCallable.class);

    private final ExecutionEngineAdapter<I, O, KP> adapter;
    private final ExecutionEngine engine;
    private final KnowledgeRepository knowledgeRepository;
    private final EvaluationRequestKMItem evaluationRequestKMItem;

    public ExecutionEngineAdapterCallable(ExecutionEngineAdapter<I, O, KP> adapter, ExecutionEngine engine,
                                          KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem evaluationRequestKMItem) {
        this.adapter = adapter;
        this.engine = engine;
        this.knowledgeRepository = knowledgeRepository;
        this.evaluationRequestKMItem = evaluationRequestKMItem;
    }

    public static <I, O, KP> Callable<EvaluationResponseKMItem> create(ExecutionEngineAdapter<I, O, KP> adapter,
                                                                       ExecutionEngine engine,
                                                                       KnowledgeRepository knowledgeRepository,
                                                                       EvaluationRequestKMItem evaluationRequestKMItem) {
        return new ExecutionEngineAdapterCallable<>(adapter, engine, knowledgeRepository, evaluationRequestKMItem);
    }

    @Override
    public EvaluationResponseKMItem call() {
        var knowledgeModule = knowledgeRepository.getKnowledgeModuleService()
                .find(evaluationRequestKMItem.getRequestedKmId());

        var evaluationCtx = EvaluationContext.create(evaluationRequestKMItem,
                knowledgeModule.getPrimaryProcess());

        var supportingData =SupportingDataUtil.getSupportingData(knowledgeRepository, knowledgeModule);

        PluginProcessor.preProcess(
                knowledgeRepository,
                knowledgeModule,
                supportingData,
                evaluationCtx);

        ExecutionEngineContext<I, O> eeContext = knowledgeRepository
                .getExecutionEngineService()
                .<I, O, ExecutionEngineContext<I, O>>createContext(engine)
                .setEvaluationContext(evaluationCtx);

        KP knowledgePackage = knowledgeRepository.getKnowledgePackageService()
                .getKnowledgePackage(knowledgeModule);
        log.debug("Package found.");

        try {
            eeContext = evaluate(knowledgePackage, eeContext);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        Map<String, List<?>> resultFactLists = eeContext.getResults();

        /*
         * TODO/FIXME: Put this elsewhere...
         */
        /**
         * now process the returned namedObjects and add them to the
         * resultFactLists
         */
        Map<String, Object> namedObjects = evaluationCtx.getNamedObjects();
        if (namedObjects != null) {
            for (String key : namedObjects.keySet()) {
                Object oneNamedObject = namedObjects.get(key);
                if (oneNamedObject != null) {
                    String className = oneNamedObject.getClass().getSimpleName();
                    @SuppressWarnings("unchecked")
                    List<Object> oneList = (List<Object>) resultFactLists.get(className);
                    if (oneList == null) {
                        oneList = new ArrayList<Object>();
                        oneList.add(oneNamedObject);
                    } else {
                        oneList.add(oneNamedObject);
                    }
                    resultFactLists.put(className, oneList);
                }
            }
        }
        PluginProcessor.postProcess(knowledgeRepository, knowledgeModule, supportingData, evaluationCtx, resultFactLists);

        // return eeContext.getResults();

        return new EvaluationResponseKMItem(resultFactLists, evaluationRequestKMItem);
    }

    private ExecutionEngineContext<I, O> evaluate(KP knowledgePackage, ExecutionEngineContext<I, O> context) {
        try {
            return adapter.execute(knowledgePackage, context);
        } catch (Exception e) {
            throw new OpenCDSRuntimeException(e.getMessage(), e);
        }
    }

}
