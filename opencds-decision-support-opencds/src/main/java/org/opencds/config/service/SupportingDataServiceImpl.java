package org.opencds.config.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opencds.common.cache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.model.impl.SupportingDataImpl;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportingDataServiceImpl implements SupportingDataService
{
    private static final CacheRegion<String, SupportingData> SUPPORTING_DATA =
            CacheRegion.create(String.class, SupportingData.class);

    private final SupportingDataDao dao;
    private final SupportingDataPackageService supportingDataPackageService;
    private final CacheService cacheService;

    public SupportingDataServiceImpl(final SupportingDataDao dao, final SupportingDataPackageService supportingDataPackageService,
            final CacheService cacheService)
    {
        this.dao = dao;
        this.supportingDataPackageService = supportingDataPackageService;
        this.cacheService = cacheService;
        cacheService.putAll(SUPPORTING_DATA, buildPairs(this.dao.getAll()));
    }

    @Override
    public SupportingData find(final String supportingDataId)
    {
        return cacheService.get(SUPPORTING_DATA, supportingDataId);
    }

    @Override
    public SupportingData find(final KMId kmId, final String supportingDataId)
    {
        final var sd = cacheService.get(SUPPORTING_DATA, supportingDataId);
        if (kmId.equals(sd.getKMId()))
            return sd;
        return null;
    }

    @Override
    public List<SupportingData> find(final KMId kmid)
    {
        return cacheService.getAll(SUPPORTING_DATA).values().stream().filter(sd -> kmid.equals(sd.getKMId())).toList();
    }

    @Override
    public List<SupportingData> getAll()
    {
        return cacheService.getAll(SUPPORTING_DATA).values().stream().toList();
    }

    @Override
    public void persist(final SupportingData sd)
    {
        dao.persist(sd);
        cacheService.put(SUPPORTING_DATA, sd.getIdentifier(), sd);
    }

    @Deprecated
    @Override
    public void delete(final KMId kmId, final String identifier)
    {
        final SupportingData sd = dao.find(kmId, identifier);
        if (sd != null)
            deleteInternal(kmId, sd);
    }

    @Override
    public void delete(final String identifier)
    {
        final SupportingData sd = dao.find(identifier);
        if (sd != null)
            deleteInternal(sd);
    }

    @Override
    public void deleteAll(final KMId kmId)
    {
        final List<SupportingData> sds = find(kmId);
        for (final SupportingData sd : sds)
            deleteInternal(sd);
    }

    @Deprecated
    private void deleteInternal(final KMId kmId, final SupportingData sd)
    {
        if (sd != null)
        {
            dao.delete(sd);
            deleteSupportingDataPackageInternal(sd);
            cacheService.evict(SUPPORTING_DATA, sd.getIdentifier());
        }
    }

    private void deleteInternal(final SupportingData sd)
    {
        if (sd != null)
        {
            dao.delete(sd);
            deleteSupportingDataPackageInternal(sd);
            cacheService.evict(SUPPORTING_DATA, sd.getIdentifier());
        }
    }

    @Deprecated
    @Override
    public InputStream getSupportingDataPackage(final KMId kmId, final String supportingDataId)
    {
        final SupportingData sd = find(kmId, supportingDataId);
        if (sd != null)
            return supportingDataPackageService.getPackageInputStream(sd);
        return null;
    }

    @Override
    public InputStream getSupportingDataPackage(final String supportingDataId)
    {
        final SupportingData sd = find(supportingDataId);
        if (sd != null)
            return supportingDataPackageService.getPackageInputStream(sd);
        return null;
    }

    @Deprecated
    @Override
    public boolean packageExists(final KMId kmId, final String supportingDataId)
    {
        final SupportingData sd = find(kmId, supportingDataId);
        if (sd != null)
            return supportingDataPackageService.exists(sd);
        return false;
    }

    @Override
    public boolean packageExists(final String supportingDataId)
    {
        final SupportingData sd = find(supportingDataId);
        if (sd != null)
            return supportingDataPackageService.exists(sd);
        return false;
    }

    @Deprecated
    @Override
    public void persistSupportingDataPackage(final KMId kmId, final String identifier, final InputStream supportingDataPackage)
    {
        SupportingData sd = find(kmId, identifier);
        if (sd != null)
        {
            sd = updateTimestamp(sd);
            supportingDataPackageService.persistPackageInputStream(sd, supportingDataPackage);
        }
    }

    @Override
    public void persistSupportingDataPackage(final String identifier, final InputStream supportingDataPackage)
    {
        SupportingData sd = find(identifier);
        if (sd != null)
        {
            sd = updateTimestamp(sd);
            supportingDataPackageService.persistPackageInputStream(sd, supportingDataPackage);
        }
    }

    @Deprecated
    @Override
    public void deleteSupportingDataPackage(final KMId kmId, final String identifier)
    {
        SupportingData sd = find(kmId, identifier);
        if (sd != null)
        {
            sd = updateTimestamp(sd);
            deleteSupportingDataPackageInternal(sd);
        }
    }

    @Override
    public void deleteSupportingDataPackage(final String identifier)
    {
        SupportingData sd = find(identifier);
        if (sd != null)
        {
            sd = updateTimestamp(sd);
            deleteSupportingDataPackageInternal(sd);
        }
    }

    private void deleteSupportingDataPackageInternal(SupportingData sd)
    {
        if (sd != null)
        {
            sd = updateTimestamp(sd);
            supportingDataPackageService.deletePackage(sd);
        }
    }

    private SupportingData updateTimestamp(final SupportingData sd)
    {
        final var sdNew = SupportingDataImpl.create(sd.getIdentifier(), sd.getKMId(), sd.getPackageType(), sd.getPackageId(),
                sd.getLoadedBy(), new Date(), sd.getUserId());
        persist(sdNew);
        return sdNew;
    }

    private Map<String, SupportingData> buildPairs(final List<SupportingData> sds)
    {
        return sds.stream()
                .map(sd -> Map.entry(sd.getIdentifier(), sd))
                .peek(entry -> log.debug("CACHEABLE SD: {} -> {}", entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
