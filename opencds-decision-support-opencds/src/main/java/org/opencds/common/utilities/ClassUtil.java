package org.opencds.common.utilities;

import java.lang.reflect.InvocationTargetException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil
{
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(final String className)
    {
        try
        {
            return (T) Class.forName(className).getDeclaredConstructor().newInstance();
        }
        catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }
}
