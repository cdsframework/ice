package org.opencds.common.exceptions;

import java.io.Serial;

public class InvalidDataException extends Exception
{
    @Serial
    private static final long serialVersionUID = -240127881030952512L;

    public InvalidDataException(final String message)
    {
        super(message);
    }
}
