package org.cdsframework.rest.opencds.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.opencds.config.api.model.impl.KMIdImpl;

/**
 *
 * @author sdn
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KMUpdate {

    private KMIdImpl kmId;
    private byte[] kmPackage;
    private String error;

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
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
     * Get the value of kmPackage
     *
     * @return the value of kmPackage
     */
    public byte[] getKmPackage() {
        return kmPackage;
    }

    /**
     * Set the value of kmPackage
     *
     * @param kmPackage new value of kmPackage
     */
    public void setKmPackage(final byte[] kmPackage) {
        this.kmPackage = kmPackage;
    }

    public boolean isValid()
    {
        return getKmId() != null
                && getKmId().getScopingEntityId() != null
                && getKmId().getBusinessId() != null
                && getKmId().getVersion() != null
                && getKmPackage() != null
                && getKmPackage().length > 0;
    }

    @Override
    public String toString()
    {
        return "KMUpdate{" + kmId + "; kmPackage=" + (kmPackage != null? "byte[" + kmPackage.length + "]": "null") + "}";
    }

}
