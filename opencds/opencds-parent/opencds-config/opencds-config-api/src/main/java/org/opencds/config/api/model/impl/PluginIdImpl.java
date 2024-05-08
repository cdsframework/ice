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

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;

public class PluginIdImpl implements PluginId {

    private String scopingEntityId;
    private String businessId;
    private String version;

    public static PluginIdImpl create(String scopingEntityId, String businessId, String version) {
        PluginIdImpl pid = new PluginIdImpl();
        pid.scopingEntityId = scopingEntityId;
        pid.businessId = businessId;
        pid.version = version;
        return pid;
    }

    public static PluginIdImpl create(PluginId pid) {
        if (pid == null) {
            return null;
        }
        if (pid instanceof PluginIdImpl) {
            return PluginIdImpl.class.cast(pid);
        }
        return create(pid.getScopingEntityId(), pid.getBusinessId(), pid.getVersion());
    }

    public static List<PluginId> create(List<PluginId> pluginIds) {
        if (pluginIds == null) {
            return null;
        }
        List<PluginId> pidis = new ArrayList<>();
        for (PluginId pid : pluginIds) {
            pidis.add(create(pid));
        }
        return pidis;
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
        return "PluginIdImpl [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= "
                + version + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(4733, 2731).append(scopingEntityId).append(businessId).append(version).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        PluginIdImpl rhs = (PluginIdImpl) obj;
        return new EqualsBuilder().append(scopingEntityId, rhs.scopingEntityId).append(businessId, rhs.businessId)
                .append(version, rhs.version).isEquals();
    }

}
