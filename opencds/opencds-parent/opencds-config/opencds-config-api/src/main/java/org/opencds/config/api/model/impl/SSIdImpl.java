package org.opencds.config.api.model.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.SSId;

public class SSIdImpl implements SSId {

    private String scopingEntityId;
    private String businessId;
    private String version;

    public static SSIdImpl create(String scopingEntityId, String businessId, String version) {
        SSIdImpl ssid = new SSIdImpl();
        ssid.scopingEntityId = scopingEntityId;
        ssid.businessId = businessId;
        ssid.version = version;
        return ssid;
    }
    
    public static SSIdImpl create(SSId ssid) {
        if (ssid == null) {
            return null;
        }
        if (ssid instanceof SSIdImpl) {
            return SSIdImpl.class.cast(ssid);
        }
        return create(ssid.getScopingEntityId(), ssid.getBusinessId(), ssid.getVersion());
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
        return "SSIdImpl [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(571,587)
        .append(scopingEntityId)
        .append(businessId)
        .append(version).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
          return false;
        }
        SSIdImpl rhs = (SSIdImpl) obj;
        return new EqualsBuilder()
        .append(scopingEntityId, rhs.scopingEntityId)
        .append(businessId, rhs.businessId)
        .append(version, rhs.version)
        .isEquals();
    }

}
