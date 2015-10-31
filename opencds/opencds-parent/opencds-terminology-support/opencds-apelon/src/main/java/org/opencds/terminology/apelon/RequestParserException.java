package org.opencds.terminology.apelon;

public class RequestParserException extends Exception {

    private static final long serialVersionUID = 1L;

    public RequestParserException() {
    }

    public RequestParserException(String message) {
        super(message);
    }

    public RequestParserException(Throwable cause) {
        super(cause);
    }

    public RequestParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
