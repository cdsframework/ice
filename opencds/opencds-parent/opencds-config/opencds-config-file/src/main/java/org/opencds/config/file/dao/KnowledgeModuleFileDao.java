package org.opencds.config.file.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.file.dao.support.RestConfigUtil;

public class KnowledgeModuleFileDao implements KnowledgeModuleDao {
	private static final Logger log = LogManager.getLogger();
    private final Map<KMId, KnowledgeModule> cache;
    private RestConfigUtil restConfigUtil;

    public KnowledgeModuleFileDao(ResourceUtil resourceUtil, String resource) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected version
        log.info("Loading resource: " + resource);
        List<KnowledgeModule> kms = restConfigUtil.unmarshalKnowledgeModules(resourceUtil.getResourceAsStream(resource));
        for (KnowledgeModule km : kms) {
            log.debug("Caching KnowledgeModule with KMID: " + km.getKMId());
            cache.put(km.getKMId(), km);
        }
    }
    
    @Override
    public KnowledgeModule find(KMId kmId) {
        return cache.get(kmId);
    }

    @Override
    public List<KnowledgeModule> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(KnowledgeModule km) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void persist(List<KnowledgeModule> internal) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }
    
    @Override
    public void delete(KnowledgeModule km) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");

    }

}
