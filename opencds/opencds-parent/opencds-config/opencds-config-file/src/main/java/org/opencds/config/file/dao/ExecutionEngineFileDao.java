package org.opencds.config.file.dao;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.file.dao.support.RestConfigUtil;

public class ExecutionEngineFileDao implements ExecutionEngineDao {
	private static final Logger log = LogManager.getLogger();
    private final Map<String, ExecutionEngine> cache;
    private RestConfigUtil restConfigUtil;
    
    public ExecutionEngineFileDao(ResourceUtil resourceUtil, String resource) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected version
        InputStream input = resourceUtil.getResourceAsStream(resource);
        log.info("Loading resource: " + input);
        List<ExecutionEngine> ees = restConfigUtil.unmarshalExecutionEngines(input);
        for (ExecutionEngine ee : ees) {
            log.debug("Caching ExecutionEngine with identifier: " + ee.getIdentifier());
            cache.put(ee.getIdentifier(), ee);
        }
    }
    
    @Override
    public ExecutionEngine find(String identifier) {
        return cache.get(identifier);
    }

    @Override
    public List<ExecutionEngine> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(ExecutionEngine ee) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void persist(List<ExecutionEngine> ees) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }
    
    @Override
    public void delete(ExecutionEngine ee) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }

}
