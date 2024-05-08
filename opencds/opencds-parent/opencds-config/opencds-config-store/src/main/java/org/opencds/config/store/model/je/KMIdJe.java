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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.KMId;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class KMIdJe implements KMId {
    @KeyField(1)
    private String scopingEntityId;

    @KeyField(2)
    private String businessId;

    @KeyField(3)
    private String version;

    private KMIdJe() {
    }

    public static KMIdJe create(String scopingEntityId, String businessId, String version) {
        KMIdJe kmid = new KMIdJe();
        kmid.scopingEntityId = scopingEntityId;
        kmid.businessId = businessId;
        kmid.version = version;
        return kmid;
    }

    public static KMIdJe create(KMId kmId) {
        if (kmId == null) {
            return null;
        }
        if (kmId instanceof KMIdJe) {
            return KMIdJe.class.cast(kmId);
        }
        return create(kmId.getScopingEntityId(), kmId.getBusinessId(), kmId.getVersion());
    }

    public String getScopingEntityId() {
        return scopingEntityId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "KMIdJe [scopingEntityId= " + scopingEntityId + ", businessId= " + businessId + ", version= " + version
                + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1579, 2731)
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
        KMIdJe rhs = (KMIdJe) obj;
        return new EqualsBuilder()
        .append(scopingEntityId, rhs.scopingEntityId)
        .append(businessId, rhs.businessId)
        .append(version, rhs.version)
        .isEquals();
    }
}
