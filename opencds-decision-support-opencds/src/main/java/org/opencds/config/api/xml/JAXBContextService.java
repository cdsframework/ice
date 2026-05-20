package org.opencds.config.api.xml;

import org.jspecify.annotations.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class JAXBContextService
{
    private static final LoadingCache<String, JAXBContext> contextCache = CacheBuilder.newBuilder().build(new CacheLoader<>()
    {
        @NonNull
        @Override
        public JAXBContext load(final @NonNull String contextPath)
        {
            try
            {
                return JAXBContext.newInstance(contextPath);
            }
            catch (final JAXBException e)
            {
                log.error("Error creating a JAXBContext for: {}", contextPath, e);
                throw new RuntimeException(e);
            }
        }
    });

    public static JAXBContext getJAXBContext(final Class<?> clazz)
    {
        return getJAXBContext(clazz.getPackageName());
    }

    public static JAXBContext getJAXBContext(final String contextPath)
    {
        return contextCache.getUnchecked(contextPath);
    }
}
