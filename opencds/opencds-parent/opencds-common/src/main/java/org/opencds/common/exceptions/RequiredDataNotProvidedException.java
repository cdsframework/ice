package org.opencds.common.exceptions;

public class RequiredDataNotProvidedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequiredDataNotProvidedException() {
        super();
    }

    public RequiredDataNotProvidedException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RequiredDataNotProvidedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequiredDataNotProvidedException(String message) {
        super(message);
    }

    public RequiredDataNotProvidedException(Throwable cause) {
        super(cause);
    }

}
