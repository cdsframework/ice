package org.opencds.common.exceptions;

import java.io.Serial;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OpenCDSConfigurationException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public OpenCDSConfigurationException(final String message)
    {
        super(message);
    }

    public OpenCDSConfigurationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
