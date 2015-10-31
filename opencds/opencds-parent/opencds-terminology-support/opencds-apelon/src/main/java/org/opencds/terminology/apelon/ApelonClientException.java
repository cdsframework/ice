package org.opencds.terminology.apelon;

public class ApelonClientException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApelonClientException() {
    }

    public ApelonClientException(String message) {
        super(message);
    }

    public ApelonClientException(Throwable cause) {
        super(cause);
    }

    public ApelonClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApelonClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
