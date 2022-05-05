package org.cdsframework.rest.opencds.pojos;

/**
 *
 * @author sdn
 */
public class UpdateError {

    private Integer status;
    private String message;

    public UpdateError(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Get the value of message
     *
     * @return the value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the value of message
     *
     * @param message new value of message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the value of status
     *
     * @return the value of status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

}
