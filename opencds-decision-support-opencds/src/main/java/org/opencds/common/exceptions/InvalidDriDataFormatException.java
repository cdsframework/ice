package org.opencds.common.exceptions;

import java.io.Serial;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidDriDataFormatException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidDriDataFormatException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidDriDataFormatException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public InvalidDriDataFormatException(final String message)
    {
        super(message);
    }

    public InvalidDriDataFormatException(final Throwable cause)
    {
        super(cause);
    }
}
