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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SDId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.mapper.util.RestConfigUtil;

public class SupportingDataFileDao implements SupportingDataDao {
    private static final Log log = LogFactory.getLog(SupportingDataFileDao.class);
    private final Map<SDKey, SupportingData> cache;
    private RestConfigUtil restConfigUtil;

    public SupportingDataFileDao(ResourceUtil resourceUtil, String path) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected
        List<String> resources = resourceUtil.findFiles(path, false);
        for (String resource : resources) {
            log.info("Loading Resource: " + resource);
            SupportingData sd = restConfigUtil.unmarshalSupportingData(resourceUtil.getResourceAsStream(resource));
            if (sd != null) {
                cache.put(new SDKey(sd.getKMId(), sd.getIdentifier()), sd);
            } else {
                log.info("Loading resource as SupportingDataList (resource was not a SupportingData instance)");
                List<SupportingData> sds = restConfigUtil.unmarshalSupportingDataList(resourceUtil.getResourceAsStream(resource));
                for (SupportingData essdee : sds) {
                    cache.put(new SDKey(essdee.getKMId(), essdee.getIdentifier()), essdee);
                }
            }
        }
    }

    @Override
    public SupportingData find(String identifier) {
        return find(null, identifier);
    }

    @Override
    public SupportingData find(KMId kmId, String identifier) {
        return cache.get(new SDKey(kmId, identifier));
    }

    @Override
    public List<SupportingData> find(KMId kmid) {
        List<SupportingData> sds = new ArrayList<>();
        for (SupportingData sd : cache.values()) {
            if (sd.getKMId().equals(kmid)) {
                sds.add(sd);
            }
        }
        return sds;
    }

    @Override
    public List<SupportingData> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(SupportingData sd) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");

    }

    @Override
    public void delete(SupportingData sd) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }

    private static class SDKey implements SDId {
        private final KMId kmId;
        private final String identifier;

        public SDKey(KMId kmId, String identifier) {
            this.kmId = kmId;
            this.identifier = identifier;
        }

        @Override
        public KMId getKMId() {
            return kmId;
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public int hashCode() {
            HashCodeBuilder hcb = new HashCodeBuilder();
            if (kmId != null) {
                hcb.append(kmId.getScopingEntityId()).append(kmId.getBusinessId()).append(kmId.getVersion());
            }
            hcb.append(identifier);
            return hcb.toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            SDKey rhs = (SDKey) obj;
            EqualsBuilder eb = new EqualsBuilder();
            if (kmId != null) {
                eb.append(kmId.getScopingEntityId(), rhs.kmId.getScopingEntityId())
                        .append(kmId.getBusinessId(), rhs.kmId.getBusinessId())
                        .append(kmId.getVersion(), rhs.kmId.getVersion());
            }
            eb.append(identifier, rhs.identifier);
            return eb.isEquals();
        }
    }

}
