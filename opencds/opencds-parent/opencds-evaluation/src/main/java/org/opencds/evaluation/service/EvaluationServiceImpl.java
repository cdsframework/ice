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
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.evaluation.service.util.CallableUtil;

public class EvaluationServiceImpl implements EvaluationService {
    private static final Log log = LogFactory.getLog(EvaluationServiceImpl.class);

    // TODO: Make this configurable
    private final ForkJoinPool evalPool = new ForkJoinPool(128, ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            new EvaluationExceptionHandler(), true);

	private CallableUtil callableUtil;

    public EvaluationServiceImpl(CallableUtil callableUtil) {
    	this.callableUtil = callableUtil;
    }

    @Override
    public List<EvaluationResponseKMItem> evaluate(KnowledgeRepository knowledgeRepository,
            List<EvaluationRequestKMItem> evaluationRequestKMItems) {
        List<EvaluationResponseKMItem> responseItems = new ArrayList<>();

        List<ForkJoinTask<EvaluationResponseKMItem>> tasks = new ArrayList<>();
        for (EvaluationRequestKMItem evaluationRequestKMItem : evaluationRequestKMItems) {
            KnowledgeModule knowledgeModule = knowledgeRepository.getKnowledgeModuleService().find(
                    evaluationRequestKMItem.getRequestedKmId());
            ExecutionEngineService executionEngineService = knowledgeRepository.getExecutionEngineService();
            ExecutionEngine engine = executionEngineService.find(knowledgeModule.getExecutionEngine());
            Callable<EvaluationResponseKMItem> callable = getCallable(knowledgeRepository, executionEngineService,
                    engine, evaluationRequestKMItem);

            log.debug("Starting evaluation of KM: " + evaluationRequestKMItem.getRequestedKmId());
            tasks.add(evalPool.submit(callable));
        }

        boolean failing = false;
        Throwable t = null;
        for (ForkJoinTask<EvaluationResponseKMItem> task : tasks) {
            if (!failing) {
                log.debug("Joining on task : " + task.toString());
                task.quietlyJoin();
                t = task.getException();
                if (t == null) {
                    responseItems.add(task.getRawResult());
                } else {
                    failing = true;
                }
            } else {
                task.cancel(true);
            }
        }
        if (t != null) {
            throw new EvaluationException(t.getCause().getMessage(), t.getCause());
        }

        // need a wrapper for Evaluater and/or ExecutionEngineAdapter.

        return responseItems;
    }

    @Override
    public EvaluationResponseKMItem evaluate(KnowledgeRepository knowledgeRepository,
            EvaluationRequestKMItem evaluationRequestKMItem) {
        KnowledgeModule knowledgeModule = knowledgeRepository.getKnowledgeModuleService().find(
                evaluationRequestKMItem.getRequestedKmId());
        if (knowledgeModule == null) {
        	throw new OpenCDSRuntimeException("Unable to find requested KM: " + evaluationRequestKMItem.getRequestedKmId());
        }
        ExecutionEngineService executionEngineService = knowledgeRepository.getExecutionEngineService();
        ExecutionEngine engine = executionEngineService.find(knowledgeModule.getExecutionEngine());
        Callable<EvaluationResponseKMItem> callable = getCallable(knowledgeRepository, executionEngineService, engine,
                evaluationRequestKMItem);
        EvaluationResponseKMItem evaluationResponseKMItem = null;
        try {
            evaluationResponseKMItem = callable.call();
        } catch (Exception e) {
            log.error("==========================================================");
            e.printStackTrace();
            throw new OpenCDSRuntimeException(e);
        }
        return evaluationResponseKMItem;
    }

    @Deprecated
    private Callable<EvaluationResponseKMItem> getCallable(KnowledgeRepository knowledgeRepository,
            ExecutionEngineService executionEngineService, ExecutionEngine engine,
            EvaluationRequestKMItem evaluationRequestKMItem) {
    	return callableUtil.getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);
    }

}
