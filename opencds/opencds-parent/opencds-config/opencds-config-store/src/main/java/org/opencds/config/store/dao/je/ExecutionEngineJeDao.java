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

package org.opencds.config.store.dao.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.impl.ExecutionEngineImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.ExecutionEngineJe;

public class ExecutionEngineJeDao extends GenericJeDao<String, ExecutionEngineJe> implements ExecutionEngineDao {

    public ExecutionEngineJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(String.class, ExecutionEngineJe.class));
    }
    
    @Override
    public ExecutionEngine find(String identifier) {
        return ExecutionEngineImpl.create(findCE(identifier));
    }

    @Override
    public List<ExecutionEngine> getAll() {
        List<ExecutionEngineJe> eejs = getAllCE();
        List<ExecutionEngine> ees = new ArrayList<>();
        for (ExecutionEngineJe eej : eejs) {
            ees.add(ExecutionEngineImpl.create(eej));
        }
        return ees;
    }

    @Override
    public void persist(ExecutionEngine ee) {
        persist(ExecutionEngineJe.create(ee));
    }

    @Override
    public void persist(List<ExecutionEngine> ees) {
        persistAllCE(ExecutionEngineJe.create(ees));
    }
    
    @Override
    public void delete(ExecutionEngine ee) {
        delete(ExecutionEngineJe.create(ee));
    }

}
