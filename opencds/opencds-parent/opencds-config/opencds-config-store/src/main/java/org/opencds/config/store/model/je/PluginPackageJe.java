package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PluginPackage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class PluginPackageJe implements PluginPackage, ConfigEntity<PPIdJe> {
    @PrimaryKey
    private PPIdJe identifier;
    private LoadContext loadContext;
    private String resourceName;
    private List<Plugin> plugins;
    private Date timestamp;
    private String userId;

    private PluginPackageJe() {
    }

    public static PluginPackageJe create(PPId identifier, LoadContext loadContext, String resourceName,
            List<Plugin> plugins, Date timestamp, String userId) {
        PluginPackageJe ppj = new PluginPackageJe();
        ppj.identifier = PPIdJe.create(identifier);
        ppj.loadContext = loadContext;
        ppj.resourceName = resourceName;
        ppj.plugins = PluginJe.create(plugins);
        ppj.timestamp = timestamp;
        ppj.userId = userId;
        return ppj;
    }

    public static PluginPackageJe create(PluginPackage pp) {
        if (pp == null) {
            return null;
        }
        if (pp instanceof PluginPackageJe) {
            return PluginPackageJe.class.cast(pp);
        }
        return create(pp.getIdentifier(), pp.getLoadContext(), pp.getResourceName(), pp.getPlugins(),
                pp.getTimestamp(), pp.getUserId());
    }

    public static List<PluginPackageJe> create(List<PluginPackage> pps) {
        if (pps == null) {
            return null;
        }
        List<PluginPackageJe> ppjs = new ArrayList<>();
        for (PluginPackage pp : pps) {
            ppjs.add(create(pp));
        }
        return ppjs;
    }
    
    @Override
    public PPIdJe getPrimaryKey() {
        return identifier;
    }

    @Override
    public PPId getIdentifier() {
        return identifier;
    }

    @Override
    public LoadContext getLoadContext() {
        return loadContext;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public List<Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public Plugin getPlugin(PluginId pluginId) {
        if (plugins == null) {
            return null;
        }
        for (Plugin p : plugins) {
            if (p.getIdentifier().equals(pluginId)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String getUserId() {
        return userId;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(5683, 1579)
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
        PluginPackageJe rhs = (PluginPackageJe) obj;
        return new EqualsBuilder()
        .append(identifier, rhs.identifier)
        .isEquals();
    }

}
