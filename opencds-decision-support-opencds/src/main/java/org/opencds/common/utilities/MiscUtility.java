package org.opencds.common.utilities;

import java.util.UUID;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class MiscUtility
{
    public static String getIDAsString()
    {
        return UUID.randomUUID().toString();
    }
}
