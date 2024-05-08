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

import org.opencds.config.store.dao.Dao;
import org.opencds.config.store.model.je.ConfigEntity;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;

public abstract class GenericJeDao<PK, CE extends ConfigEntity<PK>> implements Dao<PK, CE> {

    private PrimaryIndex<PK, CE> primaryIndex;

    protected GenericJeDao(PrimaryIndex<PK, CE> primaryIndex) {
        this.primaryIndex = primaryIndex;
    }

    @Override
    public CE findCE(PK pk) {
        return primaryIndex.get(pk);
    }

    @Override
    public List<CE> getAllCE() {
        List<CE> ces = new ArrayList<>();
        try (EntityCursor<CE> cursor = primaryIndex.entities()) {
            for (CE ce : cursor) {
                ces.add(ce);
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return ces;
    }

    @Override
    public void persist(CE ce) {
        primaryIndex.put(ce);
    }

    @Override
    public void persistAllCE(List<CE> ces) {
        for (CE ce : ces) {
            primaryIndex.put(ce);
        }
    }

    @Override
    public void delete(CE ce) {
        primaryIndex.delete(ce.getPrimaryKey());
    }

}
