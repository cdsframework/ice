package org.opencds.common.exceptions;

public class UnrecognizedScopedEntityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnrecognizedScopedEntityException() {
        super();
    }

    public UnrecognizedScopedEntityException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnrecognizedScopedEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnrecognizedScopedEntityException(String message) {
        super(message);
    }

    public UnrecognizedScopedEntityException(Throwable cause) {
        super(cause);
    }

}
