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

package org.opencds.config.file.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.mapper.util.RestConfigUtil;

public class ExecutionEngineFileDao implements ExecutionEngineDao {
    private static final Log log = LogFactory.getLog(ExecutionEngineFileDao.class);
    private final Map<String, ExecutionEngine> cache;
    private RestConfigUtil restConfigUtil;
    
    public ExecutionEngineFileDao(ResourceUtil resourceUtil, String resource) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected version
        InputStream input = resourceUtil.getResourceAsStream(resource);
        log.info("Loading resource: " + input);
        List<ExecutionEngine> ees = restConfigUtil.unmarshalExecutionEngines(input);
        for (ExecutionEngine ee : ees) {
            log.debug("Caching ExecutionEngine with identifier: " + ee.getIdentifier());
            cache.put(ee.getIdentifier(), ee);
        }
    }
    
    @Override
    public ExecutionEngine find(String identifier) {
        return cache.get(identifier);
    }

    @Override
    public List<ExecutionEngine> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(ExecutionEngine ee) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void persist(List<ExecutionEngine> ees) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }
    
    @Override
    public void delete(ExecutionEngine ee) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }

}
