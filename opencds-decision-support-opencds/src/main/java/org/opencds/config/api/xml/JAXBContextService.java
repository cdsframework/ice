package org.opencds.config.api.xml;

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
        @Override
        public JAXBContext load(final String contextPath)
        {
            JAXBContext jaxbContext = null;
            try
            {
                log.info("Creating JAXBContext: {}", contextPath);
                jaxbContext = JAXBContext.newInstance(contextPath);
                log.info("JAXBContext created: {}", contextPath);
            }
            catch (final JAXBException e)
            {
                log.error("Error creating a JAXBContext for: {}", contextPath, e);
            }
            return jaxbContext;
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
