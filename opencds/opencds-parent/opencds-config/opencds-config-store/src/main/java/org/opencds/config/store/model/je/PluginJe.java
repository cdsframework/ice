package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.Plugin;
import org.opencds.config.api.model.PluginId;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class PluginJe implements Plugin, ConfigEntity<PluginIdJe> {
    private PluginIdJe identifier;
    private String className;

    private PluginJe() {
    }

    public static PluginJe create(PluginId identifier, String className) {
        PluginJe ppi = new PluginJe();
        ppi.identifier = PluginIdJe.create(identifier);
        ppi.className = className;
        return ppi;
    }

    public static PluginJe create(Plugin pp) {
        if (pp == null) {
            return null;
        }
        if (pp instanceof PluginJe) {
            return PluginJe.class.cast(pp);
        }
        return create(pp.getIdentifier(), pp.getClassName());
    }

    public static List<Plugin> create(List<Plugin> plugins) {
        if (plugins == null) {
            return null;
        }
        List<Plugin> pjs = new ArrayList<>();
        for (Plugin p : plugins) {
            pjs.add(create(p));
        }
        return pjs;
    }
    
    @Override
    public PluginIdJe getPrimaryKey() {
        return getIdentifier();
    }
    
    @Override
    public PluginIdJe getIdentifier() {
        return identifier;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(5479, 1579)
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
        PluginJe rhs = (PluginJe) obj;
        return new EqualsBuilder()
        .append(identifier, rhs.identifier)
        .isEquals();
    }
}
