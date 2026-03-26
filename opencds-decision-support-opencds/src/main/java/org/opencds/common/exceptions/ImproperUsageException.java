package org.opencds.common.exceptions;

import java.io.Serial;

public class ImproperUsageException extends Exception
{
    @Serial
    private static final long serialVersionUID = 2934351913995892619L;

    public ImproperUsageException(final String message)
    {
        super(message);
    }
}
