package org.opencds.common.utilities;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtil
{
    public static <T> T newInstance(final String className, final Class<T> serviceType)
    {
        try
        {
            return ServiceLoader.load(serviceType, Thread.currentThread().getContextClassLoader())
                    .stream()
                    .filter(provider -> provider.type().getName().equals(className))
                    .findFirst()
                    .map(ServiceLoader.Provider::get)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No registered %s implementation found for class '%s'".formatted(serviceType.getSimpleName(),
                                    className)));
        }
        catch (final ServiceConfigurationError e)
        {
            throw new RuntimeException(
                    "Unable to initialize service provider '%s' for type '%s'".formatted(className, serviceType.getName()), e);
        }
    }
}
