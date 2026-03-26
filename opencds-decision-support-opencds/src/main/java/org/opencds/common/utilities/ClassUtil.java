package org.opencds.common.utilities;

import java.lang.reflect.InvocationTargetException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil
{
    public static <T> T newInstance(final String className)
    {
        try
        {
            return newInstance((Class<T>) Class.forName(className));
        }
        catch (final ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(final Class<T> cls)
    {
        try
        {
            return cls.getDeclaredConstructor().newInstance();
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }
}
