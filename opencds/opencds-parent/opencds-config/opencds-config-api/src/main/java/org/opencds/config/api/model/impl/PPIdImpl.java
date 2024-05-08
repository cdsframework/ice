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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.PPId;

public class PPIdImpl implements PPId {

    private String scopingEntityId;
    private String businessId;
    private String version;

    public static PPIdImpl create(String scopingEntityId, String businessId, String version) {
        PPIdImpl ssid = new PPIdImpl();
        ssid.scopingEntityId = scopingEntityId;
        ssid.businessId = businessId;
        ssid.version = version;
        return ssid;
    }
    
    public static PPIdImpl create(PPId pluginPackageid) {
        if (pluginPackageid == null) {
            return null;
        }
        if (pluginPackageid instanceof PPIdImpl) {
            return PPIdImpl.class.cast(pluginPackageid);
        }
        return create(pluginPackageid.getScopingEntityId(), pluginPackageid.getBusinessId(), pluginPackageid.getVersion());
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
        return "PPIdImpl [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(4733, 5351)
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
        PPIdImpl rhs = (PPIdImpl) obj;
        return new EqualsBuilder()
        .append(scopingEntityId, rhs.scopingEntityId)
        .append(businessId, rhs.businessId)
        .append(version, rhs.version)
        .isEquals();
    }

}
