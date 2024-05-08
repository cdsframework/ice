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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.mapper.util.RestConfigUtil;

public class KnowledgeModuleFileDao implements KnowledgeModuleDao {
    private static final Log log = LogFactory.getLog(KnowledgeModuleFileDao.class);
    private final Map<KMId, KnowledgeModule> cache;
    private RestConfigUtil restConfigUtil;

    public KnowledgeModuleFileDao(ResourceUtil resourceUtil, String resource) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected version
        log.info("Loading resource: " + resource);
        List<KnowledgeModule> kms = restConfigUtil.unmarshalKnowledgeModules(resourceUtil.getResourceAsStream(resource));
        for (KnowledgeModule km : kms) {
            log.debug("Caching KnowledgeModule with KMID: " + km.getKMId());
            cache.put(km.getKMId(), km);
        }
    }
    
    @Override
    public KnowledgeModule find(KMId kmId) {
        return cache.get(kmId);
    }

    @Override
    public List<KnowledgeModule> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(KnowledgeModule km) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void persist(List<KnowledgeModule> internal) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }
    
    @Override
    public void delete(KnowledgeModule km) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");

    }

}
