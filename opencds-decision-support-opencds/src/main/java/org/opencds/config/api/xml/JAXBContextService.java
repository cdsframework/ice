package org.opencds.config.api.xml;

import org.jspecify.annotations.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JAXBContextService
{
    @Getter
    private static final JAXBContextService instance = new JAXBContextService();

    private final LoadingCache<String, JAXBContext> contextCache = CacheBuilder.newBuilder().build(new CacheLoader<>()
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

    public JAXBContext getJAXBContext(final Class<?> clazz)
    {
        return getJAXBContext(clazz.getPackageName());
    }

    public JAXBContext getJAXBContext(final String contextPath)
    {
        return contextCache.getUnchecked(contextPath);
    }
}
