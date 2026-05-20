package org.opencds.config.api.dao;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.util.PathUtil;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SDId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.mapper.util.RestConfigUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SupportingDataDao
{
    private record SDKey(KMId kmId,
                         String identifier) implements SDId
    {
    }

    private final Map<SDKey, SupportingData> cache;

    public SupportingDataDao(final Path path)
    {
        final RestConfigUtil restConfigUtil = new RestConfigUtil();

        cache = PathUtil.findFiles(path, false)
                .stream()
                .peek(resource -> log.debug("Loading Resource: {}", resource))
                .map(PathUtil::getResourceAsStream)
                .map(restConfigUtil::unmarshalSupportingDataList)
                .peek(_ -> log.debug("Loading resource as SupportingDataList (resource was not a SupportingData instance)"))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(sd -> new SDKey(sd.kmId(), sd.identifier()), Function.identity()));
    }

    public SupportingData find(final String identifier)
    {
        return find(null, identifier);
    }

    public SupportingData find(final KMId kmId, final String identifier)
    {
        return cache.get(new SDKey(kmId, identifier));
    }

    public List<SupportingData> find(final KMId kmid)
    {
        return cache.values().stream().filter(sd -> sd.kmId().equals(kmid)).toList();
    }

    public List<SupportingData> getAll()
    {
        return new ArrayList<>(cache.values());
    }
}