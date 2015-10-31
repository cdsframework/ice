package org.opencds.common.exceptions;

public class InvalidDriDataFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidDriDataFormatException() {
        super();
    }

    public InvalidDriDataFormatException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidDriDataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDriDataFormatException(String message) {
        super(message);
    }

    public InvalidDriDataFormatException(Throwable cause) {
        super(cause);
    }

}
