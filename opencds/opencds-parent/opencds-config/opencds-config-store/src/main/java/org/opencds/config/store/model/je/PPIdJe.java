package org.opencds.config.store.model.je;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.PPId;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class PPIdJe implements PPId {
    @KeyField(1)
    private String scopingEntityId;
    @KeyField(2)
    private String businessId;
    @KeyField(3)
    private String version;

    private PPIdJe() {
    }

    public static PPIdJe create(String scopingEntityId, String businessId, String version) {
        PPIdJe pluginId = new PPIdJe();
        pluginId.scopingEntityId = scopingEntityId;
        pluginId.businessId = businessId;
        pluginId.version = version;
        return pluginId;
    }

    public static PPIdJe create(PPId pluginPackageId) {
        if (pluginPackageId == null) {
            return null;
        }
        if (pluginPackageId instanceof PPIdJe) {
            return PPIdJe.class.cast(pluginPackageId);
        }
        return create(pluginPackageId.getScopingEntityId(), pluginPackageId.getBusinessId(), pluginPackageId.getVersion());
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
        return "PPIdJe [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1579, 7019)
        .append(scopingEntityId)
        .append(businessId)
        .append(version)
        .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
          return false;
        }
        PPIdJe rhs = (PPIdJe) obj;
        return new EqualsBuilder()
        .append(scopingEntityId, rhs.scopingEntityId)
        .append(businessId, rhs.businessId)
        .append(version, rhs.version)
        .isEquals();
    }

}
