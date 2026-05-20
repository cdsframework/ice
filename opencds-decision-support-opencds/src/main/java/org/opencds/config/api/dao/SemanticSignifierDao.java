package org.opencds.config.api.dao;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.opencds.config.api.dao.util.PathUtil;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.mapper.util.RestConfigUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SemanticSignifierDao
{
    private final Map<SSId, SemanticSignifier> cache;

    public SemanticSignifierDao(final Path resource)
    {
        final RestConfigUtil restConfigUtil = new RestConfigUtil();

        log.debug("Loading resource: {}", resource);

        cache = restConfigUtil.unmarshalSemanticSignifiers(PathUtil.getResourceAsStream(resource))
                .stream()
                .peek(ss -> log.debug("Caching SemanticSignifier for SSID: {}", ss.ssId()))
                .collect(Collectors.toMap(SemanticSignifier::ssId, Function.identity()));
    }

    public SemanticSignifier find(final SSId ssId)
    {
        log.debug("Finding match for SSID: {}", ssId);
        return cache.get(ssId);
    }

    public List<SemanticSignifier> getAll()
    {
        return List.copyOf(cache.values());
    }
}

