package org.opencds.config.api.dao;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencds.config.api.dao.util.PathUtil;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.mapper.util.RestConfigUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KnowledgeModuleDao
{
    private final Map<KMId, KnowledgeModule> cache = new HashMap<>();

    public KnowledgeModuleDao(final Path path)
    {
        final RestConfigUtil restConfigUtil = new RestConfigUtil();

        log.debug("Loading resource: {}", path);

        for (final KnowledgeModule km : restConfigUtil.unmarshalKnowledgeModules(PathUtil.getResourceAsStream(path)))
        {
            log.debug("Caching KnowledgeModule with KMID: {}", km.kmId());
            cache.put(km.kmId(), km);
        }
    }

    public KnowledgeModule find(final KMId kmId)
    {
        return cache.get(kmId);
    }

    public List<KnowledgeModule> getAll()
    {
        return new ArrayList<>(cache.values());
    }
}

