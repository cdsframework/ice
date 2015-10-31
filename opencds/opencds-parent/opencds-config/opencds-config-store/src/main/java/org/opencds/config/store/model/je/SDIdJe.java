package org.opencds.config.store.model.je;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SDId;
import org.opencds.config.api.model.impl.KMIdImpl;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class SDIdJe implements SDId {
    @KeyField(1)
    private String scopingEntityId;

    @KeyField(2)
    private String businessId;

    @KeyField(3)
    private String version;

    @KeyField(4)
    private String identifier;

    private SDIdJe() {
    }
    
    public static SDIdJe create(KMId kmId, String identifier) {
        SDIdJe sdid = new SDIdJe();
        sdid.scopingEntityId = kmId.getScopingEntityId();
        sdid.businessId = kmId.getBusinessId();
        sdid.version = kmId.getVersion();
        sdid.identifier = identifier;
        return sdid;
    }

    @Override
    public KMId getKMId() {
        return KMIdImpl.create(scopingEntityId, businessId, version);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public String toString() {
        return "SDIdJe [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + ", identifier= " + identifier + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1579, 2731)
        .append(scopingEntityId)
        .append(businessId)
        .append(version)
        .append(identifier)
        .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
          return false;
        }
        SDIdJe rhs = (SDIdJe) obj;
        return new EqualsBuilder()
        .append(scopingEntityId, rhs.scopingEntityId)
        .append(businessId, rhs.businessId)
        .append(version, rhs.version)
        .append(identifier, rhs.identifier)
        .isEquals();
    }

}
