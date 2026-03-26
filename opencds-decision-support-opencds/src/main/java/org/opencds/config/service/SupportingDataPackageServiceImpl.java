package org.opencds.config.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opencds.common.cache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.FileCacheElement;
import org.opencds.config.api.dao.file.StreamCacheElement;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.service.SupportingDataPackageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SupportingDataPackageServiceImpl implements SupportingDataPackageService
{
    private static final CacheRegion<CacheElement, byte[]> SD_PACKAGE_BYTES = CacheRegion.create(CacheElement.class, byte[].class);

    private final FileDao fileDao;

    private final CacheService cacheService;

    @Override
    public boolean exists(final SupportingData supportingData)
    {
        return Optional.ofNullable(supportingData)
                .map(this::packageIdOrSDId)
                .map(fileDao::find)
                .map(CacheElement::exists)
                .orElse(false);
    }

    @Override
    public InputStream getPackageInputStream(final SupportingData supportingData)
    {
        return Optional.ofNullable(supportingData)
                .map(this::packageIdOrSDId)
                .map(fileDao::find)
                .filter(CacheElement::exists)
                .map(cacheElement ->
                {
                    try
                    {
                        return cacheElement.inputStream();
                    }
                    catch (final IOException e)
                    {
                        log.error("Cannot resolve package: {}", supportingData.getPackageId());
                        return null;
                    }
                })
                .orElse(null);
    }

    @Override
    public byte[] getPackageBytes(final SupportingData supportingData)
    {
        final CacheElement cacheElement = fileDao.find(packageIdOrSDId(supportingData));
        final byte[] bytes = cacheService.get(SD_PACKAGE_BYTES, cacheElement);
        if (bytes == null || bytes.length == 0)
        {
            try (final BufferedReader is = new BufferedReader(new InputStreamReader(cacheElement.inputStream())))
            {
                return is.lines().collect(Collectors.joining()).getBytes();
            }
            catch (final IOException e)
            {
                log.error("Error reading CacheElement: {}", cacheElement, e);
            }
        }
        return bytes;
    }

    @Override
    public File getFile(final SupportingData supportingData)
    {
        return Optional.ofNullable(supportingData)
                .map(this::packageIdOrSDId)
                .map(fileDao::find)
                .filter(CacheElement::exists)
                .filter(FileCacheElement.class::isInstance)
                .map(FileCacheElement.class::cast)
                .map(FileCacheElement::getFile)
                .orElse(null);
    }

    @Override
    public void persistPackageInputStream(final SupportingData sd, final InputStream supportingDataPackage)
    {
        final var cacheElement = StreamCacheElement.create(packageIdOrSDId(sd), supportingDataPackage);
        fileDao.persist(cacheElement);
    }

    @Override
    public void deletePackage(final SupportingData sd)
    {
        final String packageId = sd.getPackageId();
        if (packageId != null)
        {
            final CacheElement cacheElement = fileDao.find(packageId);
            if (cacheElement != null)
            {
                fileDao.delete(cacheElement);
                cacheService.evict(SD_PACKAGE_BYTES, cacheElement);
            }
        }
    }

    private String packageIdOrSDId(final SupportingData sd)
    {
        return Optional.ofNullable(sd.getPackageId()).orElseGet(sd::getIdentifier);
    }
}
