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

package org.opencds.config.migrate.model;

import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.util.EntityIdentifierUtil;

public enum DataModel {
    V1_0(
            "org.opencds.vmr^VMR^1.0",
            "org.opencds.vmr.v1_0.schema",
            "org.opencds.vmr.v1_0.schema.CDSInput",
            "org.opencds.vmr.v1_0.mappings.out.VMRV10ModelExitPoint");
    
    private String entityIdString;
    private EntityIdentifier entityId;
    private String schema;
    private String entryPoint;
    private String exitPoint;

    DataModel(String entityIdString, String schema, String entryPoint, String exitPoint) {
        this.entityIdString = entityIdString;
        this.entityId = EntityIdentifierUtil.makeEI(entityIdString);
        this.schema = schema;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
    }

    public String getEntityIdString() {
        return entityIdString;
    }

    public EntityIdentifier getEntityId() {
        return entityId;
    }

    public String getSchema() {
        return schema;
    }
    
    public String getEntryPoint() {
        return entryPoint;
    }
    
    public String getExitPoint() {
        return exitPoint;
    }
    
    public static DataModel resolve(String entityIdString) {
        DataModel dataModel = null;
        for (DataModel dm : DataModel.values()) {
            if (dm.entityIdString.equals(entityIdString)) {
                dataModel = dm;
                break;
            }
        }
        return dataModel;
    }
}
