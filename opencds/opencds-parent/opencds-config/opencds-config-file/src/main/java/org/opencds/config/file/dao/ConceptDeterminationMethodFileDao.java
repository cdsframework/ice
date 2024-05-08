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
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.mapper.util.RestConfigUtil;

public class ConceptDeterminationMethodFileDao implements ConceptDeterminationMethodDao {
    private static final Log log = LogFactory.getLog(ConceptDeterminationMethodFileDao.class);
    private final Map<CDMId, ConceptDeterminationMethod> cache;
    private final RestConfigUtil restConfigUtil;

    public ConceptDeterminationMethodFileDao(ResourceUtil resourceUtil, String path) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected
                                               // version
        log.info("Finding CDM resources in path: " + path);
        List<String> resources = resourceUtil.findFiles(path, true);
        for (String input : resources) {
            log.info("Loading resource: " + input);
            List<ConceptDeterminationMethod> cdms = restConfigUtil.unmarshalCdms(resourceUtil
                    .getResourceAsStream(input));
            if (cdms != null) {
                for (ConceptDeterminationMethod cdm : cdms) {
                    log.debug("Caching ConceptDeterminationMethod with CDMID: " + cdm.getCDMId());
                    cache.put(cdm.getCDMId(), cdm);
                }
            } else {
                log.info("Loading resource as ConceptDeterminationMethod (resources was not a ConceptDeterminationMethods instance)");
                ConceptDeterminationMethod cdm = restConfigUtil.unmarshalCdm(resourceUtil.getResourceAsStream(input));
                cache.put(cdm.getCDMId(), cdm);
            }
        }
    }

    @Override
    public ConceptDeterminationMethod find(CDMId cdmId) {
        return cache.get(cdmId);
    }

    @Override
    public List<ConceptDeterminationMethod> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(ConceptDeterminationMethod cdm) {
        throw new UnsupportedOperationException("Cannot persist to file store through the dao API");
    }

    @Override
    public void persist(List<ConceptDeterminationMethod> internal) {
        throw new UnsupportedOperationException("Cannot persist to file store through the dao API");
    }

    @Override
    public void delete(ConceptDeterminationMethod cdm) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }

}
