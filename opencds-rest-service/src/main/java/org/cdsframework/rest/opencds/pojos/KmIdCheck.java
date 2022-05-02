package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.KMId;

public class KmIdCheck {
    private KMId kmId;
    private boolean exists = false;

    public KmIdCheck() {
    }

    public KmIdCheck(final KMId kmId, final boolean exists) {
        this.kmId = kmId;
        this.exists = exists;
    }

    public KMId getKmId() {
        return this.kmId;
    }

    public void setKmId(final KMId kmId) {
        this.kmId = kmId;
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
        return "{" + " kmId='" + getKmId() + "'" + ", exists='" + isExists() + "'" + "}";
    }

}