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

import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KnowledgeModuleImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.KMIdJe;
import org.opencds.config.store.model.je.KnowledgeModuleJe;

public class KnowledgeModuleJeDao extends GenericJeDao<KMIdJe, KnowledgeModuleJe> implements KnowledgeModuleDao {

    public KnowledgeModuleJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(KMIdJe.class, KnowledgeModuleJe.class));
    }

    @Override
    public KnowledgeModule find(KMId kmId) {
        return KnowledgeModuleImpl.create(findCE(KMIdJe.create(kmId)));
    }

    @Override
    public List<KnowledgeModule> getAll() {
        List<KnowledgeModuleJe> kmjs = getAllCE();
        List<KnowledgeModule> kms = new ArrayList<>();
        for (KnowledgeModuleJe kmj : kmjs) {
            kms.add(KnowledgeModuleImpl.create(kmj));
        }
        return kms;
    }

    @Override
    public void persist(KnowledgeModule km) {
        persist(KnowledgeModuleJe.create(km));
    }

    @Override
    public void persist(List<KnowledgeModule> internal) {
        persistAllCE(KnowledgeModuleJe.create(internal));
    }
    
    @Override
    public void delete(KnowledgeModule km) {
        delete(KnowledgeModuleJe.create(km));
    }

}
