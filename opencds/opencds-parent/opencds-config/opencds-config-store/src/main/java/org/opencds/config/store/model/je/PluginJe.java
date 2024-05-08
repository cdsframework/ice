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
