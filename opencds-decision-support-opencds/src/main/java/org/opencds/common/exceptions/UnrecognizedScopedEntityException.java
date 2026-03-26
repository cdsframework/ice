package org.opencds.common.exceptions;

import java.io.Serial;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnrecognizedScopedEntityException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public UnrecognizedScopedEntityException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnrecognizedScopedEntityException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public UnrecognizedScopedEntityException(final String message)
    {
        super(message);
    }

    public UnrecognizedScopedEntityException(final Throwable cause)
    {
        super(cause);
    }
}
