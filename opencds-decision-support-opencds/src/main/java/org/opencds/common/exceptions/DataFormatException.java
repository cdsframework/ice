package org.opencds.common.exceptions;

import java.io.Serial;

public class DataFormatException extends Exception
{
    @Serial
    private static final long serialVersionUID = 1359205946826865304L;

    public DataFormatException(final String message)
    {
        super(message);
    }
}
