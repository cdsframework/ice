package org.cdsframework.rest.opencds.pojos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sdn
 */
public class UpdateResponseResult {

    private List<KMUpdateResult> kms = new ArrayList<>();
    private List<CDMUpdateResult> cdms = new ArrayList<>();
    private String environment;
    private String instanceId;

    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * Get the value of kms
     *
     * @return the value of kms
     */
    public List<KMUpdateResult> getKms() {
        return kms;
    }

    /**
     * Set the value of kms
     *
     * @param kms new value of kms
     */
    public void setKms(final List<KMUpdateResult> kms) {
        this.kms = kms;
    }

    /**
     * Get the value of cdms
     *
     * @return the value of cdms
     */
    public List<CDMUpdateResult> getCdms() {
        return cdms;
    }

    /**
     * Set the value of cdms
     *
     * @param cdms new value of cdms
     */
    public void setCdms(final List<CDMUpdateResult> cdms) {
        this.cdms = cdms;
    }

    public boolean hasErrors()
    {
        return hasKmErrors() || hasCdmErrors();
    }

    public boolean hasKmErrors()
    {
        return kms != null && !kms.isEmpty();
    }

    public boolean hasCdmErrors()
    {
        return cdms != null && !cdms.isEmpty();
    }

}
