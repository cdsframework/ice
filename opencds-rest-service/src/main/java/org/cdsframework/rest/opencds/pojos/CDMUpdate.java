package org.cdsframework.rest.opencds.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.opencds.config.api.model.impl.CDMIdImpl;

/**
 *
 * @author sdn
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CDMUpdate {

    private byte[] cdm;
    private CDMIdImpl cdmId;
    private String error;

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
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
     * Get the value of cdm
     *
     * @return the value of cdm
     */
    public byte[] getCdm() {
        return cdm;
    }

    /**
     * Set the value of cdm
     *
     * @param cdm new value of cdm
     */
    public void setCdm(final byte[] cdm) {
        this.cdm = cdm;
    }

    public boolean isValid()
    {
        return getCdmId() != null
                && getCdmId().getCode() != null
                && getCdmId().getCodeSystem() != null
                && getCdmId().getVersion() != null
                && getCdm() != null
                && getCdm().length > 0;
    }

    @Override
    public String toString()
    {
        return "CDMUpdate{" + cdmId + "; cdm=" + (cdm != null? "byte[" + cdm.length + "]": "null") + "}";
    }

}
