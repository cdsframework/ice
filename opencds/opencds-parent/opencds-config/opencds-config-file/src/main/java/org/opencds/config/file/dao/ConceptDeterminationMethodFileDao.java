package org.opencds.config.file.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.dao.util.ResourceUtil;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.file.dao.support.RestConfigUtil;

public class ConceptDeterminationMethodFileDao implements ConceptDeterminationMethodDao {
	private static final Logger log = LogManager.getLogger();
    private final Map<CDMId, ConceptDeterminationMethod> cache;
    private final RestConfigUtil restConfigUtil;

    public ConceptDeterminationMethodFileDao(ResourceUtil resourceUtil, String path) {
        cache = new HashMap<>();
        restConfigUtil = new RestConfigUtil(); // TODO replace with an injected
                                               // version
        log.info("Finding CDM resources in path: " + path);
        List<String> resources = resourceUtil.findFiles(path, true);
        for (String input : resources) {
            log.info("Loading resource: " + input);
            List<ConceptDeterminationMethod> cdms = restConfigUtil.unmarshalCdms(resourceUtil
                    .getResourceAsStream(input));
            if (cdms != null) {
                for (ConceptDeterminationMethod cdm : cdms) {
                    log.debug("Caching ConceptDeterminationMethod with CDMID: " + cdm.getCDMId());
                    cache.put(cdm.getCDMId(), cdm);
                }
            } else {
                log.info("Loading resource as ConceptDeterminationMethod (resources was not a ConceptDeterminationMethods instance)");
                ConceptDeterminationMethod cdm = restConfigUtil.unmarshalCdm(resourceUtil.getResourceAsStream(input));
                cache.put(cdm.getCDMId(), cdm);
            }
        }
    }

    @Override
    public ConceptDeterminationMethod find(CDMId cdmId) {
        return cache.get(cdmId);
    }

    @Override
    public List<ConceptDeterminationMethod> getAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void persist(ConceptDeterminationMethod cdm) {
        throw new UnsupportedOperationException("Cannot persist to file store through the dao API");
    }

    @Override
    public void persist(List<ConceptDeterminationMethod> internal) {
        throw new UnsupportedOperationException("Cannot persist to file store through the dao API");
    }

    @Override
    public void delete(ConceptDeterminationMethod cdm) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }

}
