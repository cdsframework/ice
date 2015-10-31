package org.opencds.config.migrate.model;

import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.util.EntityIdentifierUtil;

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
