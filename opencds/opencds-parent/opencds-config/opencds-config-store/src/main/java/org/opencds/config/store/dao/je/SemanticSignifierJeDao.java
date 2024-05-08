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

import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.impl.SemanticSignifierImpl;
import org.opencds.config.store.je.OpenCDSConfigStore;
import org.opencds.config.store.model.je.SSIdJe;
import org.opencds.config.store.model.je.SemanticSignifierJe;

public class SemanticSignifierJeDao extends GenericJeDao<SSIdJe, SemanticSignifierJe> implements SemanticSignifierDao {

    public SemanticSignifierJeDao(OpenCDSConfigStore openCDSConfigStore) {
        super(openCDSConfigStore.getEntityStore().getPrimaryIndex(SSIdJe.class, SemanticSignifierJe.class));
    }

    @Override
    public SemanticSignifier find(SSId ssId) {
        return SemanticSignifierImpl.create(findCE(SSIdJe.create(ssId)));
    }

    @Override
    public List<SemanticSignifier> getAll() {
        List<SemanticSignifierJe> ssjs = getAllCE();
        List<SemanticSignifier> sss = new ArrayList<>();
        for (SemanticSignifierJe ssj : ssjs) {
            sss.add(SemanticSignifierImpl.create(ssj));
        }
        return sss;
    }

    @Override
    public void persist(SemanticSignifier ss) {
        persist(SemanticSignifierJe.create(ss));
    }

    @Override
    public void persist(List<SemanticSignifier> sses) {
        persistAllCE(SemanticSignifierJe.create(sses));
    }
    
    @Override
    public void delete(SemanticSignifier ss) {
        delete(SemanticSignifierJe.create(ss));
    }

}
