package org.opencds.config.migrate.builder;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.terminology.OpenCDSConceptTypes;

public class SupportedConceptsBuilder {
	private static final Logger log = LogManager.getLogger();

    public void loadSupportedConcepts(OpencdsCache cache) {
        // load codeSystem arrays
        List<String> conceptTypes = OpenCDSConceptTypes.getOpenCdsConceptTypes();// conceptTypesRootEntity.getChildrenWithLabel("ConceptType");
        
        cache.put(ConfigCacheRegion.DATA, ConfigResourceType.SUPPORTED_CONCEPTS, conceptTypes);
        log.trace("OpenCDSConceptTypes: " + conceptTypes);
    }

}
