package org.opencds.common.exceptions;

import java.io.Serial;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RequiredDataNotProvidedException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public RequiredDataNotProvidedException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RequiredDataNotProvidedException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public RequiredDataNotProvidedException(final String message)
    {
        super(message);
    }

    public RequiredDataNotProvidedException(final Throwable cause)
    {
        super(cause);
    }
}
