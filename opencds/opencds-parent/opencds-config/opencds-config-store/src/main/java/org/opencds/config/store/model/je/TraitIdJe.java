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

package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.TraitId;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class TraitIdJe implements TraitId {

    private String scopingEntityId;
    private String businessId;
    private String version;

    private TraitIdJe() {}
    
    public static TraitIdJe create(String scopingEntityId, String businessId, String version) {
        TraitIdJe tij = new TraitIdJe();
        tij.scopingEntityId = scopingEntityId;
        tij.businessId = businessId;
        tij.version = version;
        return tij;
    }
    
    public static TraitIdJe create(TraitId traitId) {
        if (traitId == null) {
            return null;
        }
        if (traitId instanceof TraitIdJe) {
            return TraitIdJe.class.cast(traitId);
        }
        return create(traitId.getScopingEntityId(), traitId.getBusinessId(), traitId.getVersion());
    }
    
    public static List<TraitId> create(List<TraitId> traitIds) {
        if (traitIds == null) {
            return null;
        }
        List<TraitId> tids = new ArrayList<>();
        for (TraitId tid : traitIds) {
            tids.add(create(tid));
        }
        return tids;
    }

    @Override
    public String getScopingEntityId() {
        return scopingEntityId;
    }

    @Override
    public String getBusinessId() {
        return businessId;
    }

    @Override
    public String getVersion() {
        return version;
    }

}
