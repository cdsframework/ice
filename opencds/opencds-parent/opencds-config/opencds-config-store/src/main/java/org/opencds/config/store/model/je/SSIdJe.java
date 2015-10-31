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
