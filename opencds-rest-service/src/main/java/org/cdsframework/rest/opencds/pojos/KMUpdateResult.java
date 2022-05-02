package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.impl.KMIdImpl;

/**
 *
 * @author sdn
 */
public class KMUpdateResult {

    private KMIdImpl kmId;
    private UpdateError error;

    public KMUpdateResult(final KMId kmId, final Integer status, final String message) {
        this.kmId = KMIdImpl.create(kmId);
        this.error = new UpdateError(status, message);
    }

    /**
     * Get the value of kmId
     *
     * @return the value of kmId
     */
    public KMIdImpl getKmId() {
        return kmId;
    }

    /**
     * Set the value of kmId
     *
     * @param kmId new value of kmId
     */
    public void setKmId(final KMIdImpl kmId) {
        this.kmId = kmId;
    }

    /**
     * Get the value of error
     *
     * @return the value of error
     */
    public UpdateError getError() {
        return error;
    }

    /**
     * Set the value of error
     *
     * @param error new value of error
     */
    public void setError(final UpdateError error) {
        this.error = error;
    }

}
