package org.opencds.common.exceptions;

import java.io.Serial;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EvaluationException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public EvaluationException(final String message)
    {
        super(message);
    }

    public EvaluationException(final Throwable cause)
    {
        super(cause);
    }

    public EvaluationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public EvaluationException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
