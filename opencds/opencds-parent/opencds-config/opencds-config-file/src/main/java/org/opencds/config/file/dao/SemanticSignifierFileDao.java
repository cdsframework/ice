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
import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.mapper.util.RestConfigUtil;

public class SemanticSignifierFileDao implements SemanticSignifierDao {
    private static final Log log = LogFactory.getLog(SemanticSignifierFileDao.class);
    private final Map<SSId, SemanticSignifier> cache;
    private RestConfigUtil restConfigUtil;

    public SemanticSignifierFileDao(ResourceUtil resourceUtil, String resource) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected version
        log.info("Loading resource: " + resource);
        List<SemanticSignifier> sss = restConfigUtil.unmarshalSemanticSignifiers(resourceUtil.getResourceAsStream(resource));
        for (SemanticSignifier ss : sss) {
            log.debug("Caching SemanticSignifier for SSID: " + ss.getSSId());
            cache.put(ss.getSSId(), ss);
        }
    }
    
    @Override
    public SemanticSignifier find(SSId ssId) {
        log.debug("Finding match for SSID: " + ssId);
        return cache.get(ssId);
    }

    @Override
    public List<SemanticSignifier> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(SemanticSignifier ss) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");

    }

    @Override
    public void persist(List<SemanticSignifier> sses) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }
    
    @Override
    public void delete(SemanticSignifier ss) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }

}
