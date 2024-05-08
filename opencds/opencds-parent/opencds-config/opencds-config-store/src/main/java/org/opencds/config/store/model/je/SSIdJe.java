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

import org.opencds.config.api.model.SSId;

import com.sleepycat.persist.model.KeyField;
import com.sleepycat.persist.model.Persistent;

@Persistent
public class SSIdJe implements SSId {
    @KeyField(1)
    String scopingEntityId;

    @KeyField(2)
    String businessId;

    @KeyField(3)
    String version;

    private SSIdJe() {
    }

    public static SSIdJe create(String scopingEntityId, String businessId, String version) {
        SSIdJe ssid = new SSIdJe();
        ssid.scopingEntityId = scopingEntityId;
        ssid.businessId = businessId;
        ssid.version = version;
        return ssid;
    }
    
    public static SSIdJe create(SSId ssid) {
        if (ssid == null) {
            return null;
        }
        if (ssid instanceof SSIdJe) {
            return SSIdJe.class.cast(ssid);
        }
        return create(ssid.getScopingEntityId(), ssid.getBusinessId(), ssid.getVersion());
    }

    public String getScopingEntityId() {
        return scopingEntityId;
    }

    public void setScopingEntityId(String scopingEntityId) {
        this.scopingEntityId = scopingEntityId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
