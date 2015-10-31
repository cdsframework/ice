package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.PluginId;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class PluginIdJe implements PluginId {
    @KeyField(1)
    private String scopingEntityId;
    
    @KeyField(2)
    private String businessId;
    
    @KeyField(3)
    private String version;

    private PluginIdJe() {
    }

    public static PluginIdJe create(String scopingEntityId, String businessId, String version) {
        PluginIdJe pluginId = new PluginIdJe();
        pluginId.scopingEntityId = scopingEntityId;
        pluginId.businessId = businessId;
        pluginId.version = version;
        return pluginId;
    }

    public static PluginIdJe create(PluginId pid) {
        if (pid == null) {
            return null;
        }
        if (pid instanceof PluginIdJe) {
            return PluginIdJe.class.cast(pid);
        }
        return create(pid.getScopingEntityId(), pid.getBusinessId(), pid.getVersion());
    }

    public static List<PluginId> create(List<PluginId> pluginIds) {
        if (pluginIds == null) {
            return null;
        }
        List<PluginId> pidjs = new ArrayList<>();
        for (PluginId pid : pluginIds) {
            pidjs.add(create(pid));
        }
        return pidjs;
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
        return "PluginIdJe [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(6113, 2731)
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
        PluginIdJe rhs = (PluginIdJe) obj;
        return new EqualsBuilder()
        .append(scopingEntityId, rhs.scopingEntityId)
        .append(businessId, rhs.businessId)
        .append(version, rhs.version)
        .isEquals();
    }

}
