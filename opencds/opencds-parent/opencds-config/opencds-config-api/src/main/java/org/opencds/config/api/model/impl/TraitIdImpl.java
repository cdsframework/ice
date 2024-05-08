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

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.TraitId;

public class TraitIdImpl implements TraitId {

    private String scopingEntityId;
    private String businessId;
    private String version;

    private TraitIdImpl() {
    }

    public static TraitIdImpl create(String scopingEntityId, String businessId, String version) {
        TraitIdImpl tii = new TraitIdImpl();
        tii.scopingEntityId = scopingEntityId;
        tii.businessId = businessId;
        tii.version = version;
        return tii;
    }

    public static TraitIdImpl create(TraitId traitId) {
        if (traitId == null) {
            return null;
        }
        if (traitId instanceof TraitIdImpl) {
            return TraitIdImpl.class.cast(traitId);
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

    @Override
    public String toString() {
        return "TraitIdImpl [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= "
                + version + "]";
    }

}
