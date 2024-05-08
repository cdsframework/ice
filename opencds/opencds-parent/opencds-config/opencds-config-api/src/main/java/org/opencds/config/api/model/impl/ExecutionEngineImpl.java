/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.opencds.config.api.model.DssOperation;
import org.opencds.config.api.model.ExecutionEngine;

public class ExecutionEngineImpl implements ExecutionEngine {

    private String identifier;
    private String adapter;
    private String context;
    private String knowledgeLoader;
    private String description;
    private Date timestamp;
    private String userId;
    private List<DssOperation> supportedOperations;

    private ExecutionEngineImpl() {
    }

    public static ExecutionEngineImpl create(String identifier, String adapter, String context, String knowledgeLoader,
            String description, Date timestamp, String userId, List<DssOperation> supportedOperations) {
        ExecutionEngineImpl ee = new ExecutionEngineImpl();
        ee.identifier = identifier;
        ee.adapter = adapter;
        ee.context = context;
        ee.knowledgeLoader = knowledgeLoader;
        ee.description = description;
        ee.timestamp = timestamp;
        ee.userId = userId;
        ee.supportedOperations = new ArrayList<>(supportedOperations);
        return ee;
    }

    public static ExecutionEngineImpl create(ExecutionEngine ee) {
        if (ee == null) {
            return null;
        }
        if (ee instanceof ExecutionEngineImpl) {
            return ExecutionEngineImpl.class.cast(ee);
        }
        return create(ee.getIdentifier(), ee.getAdapter(), ee.getContext(), ee.getKnowledgeLoader(),
                ee.getDescription(), ee.getTimestamp(), ee.getUserId(), ee.getSupportedOperations());
    }

    public static List<ExecutionEngineImpl> create(List<ExecutionEngine> ees) {
        if (ees == null) {
            return null;
        }
        List<ExecutionEngineImpl> eeis = new ArrayList<>();
        for (ExecutionEngine ee : ees) {
            eeis.add(create(ee));
        }
        return eeis;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getAdapter() {
        return adapter;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public String getKnowledgeLoader() {
        return knowledgeLoader;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public List<DssOperation> getSupportedOperations() {
        return Collections.unmodifiableList(supportedOperations);
    }

}
