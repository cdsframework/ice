package org.opencds.config.api.model.impl;

import org.opencds.config.api.model.EntityIdentifier;

public class EntityIdentifierImpl implements EntityIdentifier {

    private String scopingEntityId;
    private String businessId;
    private String version;

    public static EntityIdentifierImpl create(String scopingEntityId, String businessId, String version) {
        EntityIdentifierImpl ssid = new EntityIdentifierImpl();
        ssid.scopingEntityId = scopingEntityId;
        ssid.businessId = businessId;
        ssid.version = version;
        return ssid;
    }
    
    public static EntityIdentifierImpl create(EntityIdentifier kmid) {
        if (kmid == null) {
            return null;
        }
        if (kmid instanceof EntityIdentifierImpl) {
            return EntityIdentifierImpl.class.cast(kmid);
        }
        return create(kmid.getScopingEntityId(), kmid.getBusinessId(), kmid.getVersion());
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
        return "EntityIdentifierImpl [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + "]";
    }

}
