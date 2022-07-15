package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.CDMId;

public class CdmIdCheck {
    private CDMId cdmId;
    private boolean exists;

    public CdmIdCheck() {
    }

    public CdmIdCheck(final CDMId cdmId, final boolean exists) {
        this.cdmId = cdmId;
        this.exists = exists;
    }

    public CDMId getCdmId() {
        return this.cdmId;
    }

    public void setCdmId(final CDMId cdmId) {
        this.cdmId = cdmId;
    }

    public boolean isExists() {
        return this.exists;
    }

    public boolean getExists() {
        return this.exists;
    }

    public void setExists(final boolean exists) {
        this.exists = exists;
    }

    @Override
    public String toString() {
        return "{" + " cdmId='" + getCdmId() + "'" + ", exists='" + isExists() + "'" + "}";
    }
}