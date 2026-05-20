package org.opencds.config.api.dao;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencds.config.api.dao.util.PathUtil;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.mapper.util.RestConfigUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutionEngineDao
{
    private final Map<String, ExecutionEngine> cache = new HashMap<>();

    public ExecutionEngineDao(final Path path)
    {
        final RestConfigUtil restConfigUtil = new RestConfigUtil();

        log.debug("Loading resource");
        for (final ExecutionEngine ee : restConfigUtil.unmarshalExecutionEngines(PathUtil.getResourceAsStream(path)))
        {
            log.debug("Caching ExecutionEngine with identifier: {}", ee.identifier());
            cache.put(ee.identifier(), ee);
        }
    }

    public ExecutionEngine find(final String identifier)
    {
        return cache.get(identifier);
    }

    public List<ExecutionEngine> getAll()
    {
        return new ArrayList<>(cache.values());
    }
}
