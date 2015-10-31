package org.opencds.common.exceptions;

public class OpenCDSRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OpenCDSRuntimeException() {
    }

    public OpenCDSRuntimeException(String message) {
        super(message);
    }

    public OpenCDSRuntimeException(Throwable cause) {
        super(cause);
    }

    public OpenCDSRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenCDSRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
