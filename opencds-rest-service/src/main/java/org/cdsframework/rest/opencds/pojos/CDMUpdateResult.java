package org.cdsframework.rest.opencds.pojos;

import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.impl.CDMIdImpl;

/**
 *
 * @author sdn
 */
public class CDMUpdateResult {

    private UpdateError error;

    private CDMIdImpl cdmId;

    public CDMUpdateResult(final CDMId cdmId, final Integer status, final String message) {
        this.cdmId = CDMIdImpl.create(cdmId);
        this.error = new UpdateError(status, message);
    }

    /**
     * Get the value of cdmId
     *
     * @return the value of cdmId
     */
    public CDMIdImpl getCdmId() {
        return cdmId;
    }

    /**
     * Set the value of cdmId
     *
     * @param cdmId new value of cdmId
     */
    public void setCdmId(final CDMIdImpl cdmId) {
        this.cdmId = cdmId;
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
