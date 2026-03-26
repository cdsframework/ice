package org.opencds.common.exceptions;

import java.io.Serial;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OpenCDSRuntimeException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public OpenCDSRuntimeException(final String message)
    {
        super(message);
    }

    public OpenCDSRuntimeException(final Throwable cause)
    {
        super(cause);
    }

    public OpenCDSRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
