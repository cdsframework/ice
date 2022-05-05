package org.cdsframework.rest.opencds.pojos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author sdn
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateResponse {

    private List<KMUpdate> kmUpdates;
    private List<CDMUpdate> cdmUpdates;

    /**
     * Get the value of kmUpdates
     *
     * @return the value of kmUpdates
     */
    public List<KMUpdate> getKmUpdates() {
        return kmUpdates;
    }

    /**
     * Set the value of kmUpdates
     *
     * @param kmUpdates new value of kmUpdates
     */
    public void setKmUpdates(final List<KMUpdate> kmUpdates) {
        this.kmUpdates = kmUpdates;
    }

    /**
     * Get the value of cdmUpdates
     *
     * @return the value of cdmUpdates
     */
    public List<CDMUpdate> getCdmUpdates() {
        return cdmUpdates;
    }

    /**
     * Set the value of cdmUpdates
     *
     * @param cdmUpdates new value of cdmUpdates
     */
    public void setCdmUpdates(final List<CDMUpdate> cdmUpdates) {
        this.cdmUpdates = cdmUpdates;
    }

}
