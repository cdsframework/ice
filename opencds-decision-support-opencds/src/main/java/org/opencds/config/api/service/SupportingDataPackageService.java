package org.opencds.config.api.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.FileCacheElement;
import org.opencds.config.api.model.SupportingData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SupportingDataPackageService
{
    private final FileDao fileDao;
    private final Map<CacheElement, byte[]> sdPackageBytesMap = new ConcurrentHashMap<>();

    public boolean exists(final SupportingData supportingData)
    {
        return Optional.ofNullable(supportingData)
                .map(this::packageIdOrSDId)
                .map(fileDao::find)
                .map(CacheElement::exists)
                .orElse(false);
    }

    public byte[] getPackageBytes(final SupportingData supportingData)
    {
        return sdPackageBytesMap.computeIfAbsent(fileDao.find(packageIdOrSDId(supportingData)), k ->
        {
            try (final BufferedReader is = new BufferedReader(new InputStreamReader(k.inputStream())))
            {
                return is.lines().collect(Collectors.joining()).getBytes();
            }
            catch (final IOException e)
            {
                log.error("Error reading CacheElement: {}", k, e);
            }

            return null;
        });
    }

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

    private String packageIdOrSDId(final SupportingData sd)
    {
        return Optional.ofNullable(sd.packageId()).orElseGet(sd::identifier);
    }
}
