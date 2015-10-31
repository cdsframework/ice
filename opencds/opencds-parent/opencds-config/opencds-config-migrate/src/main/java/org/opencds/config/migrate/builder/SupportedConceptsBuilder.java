package org.opencds.config.migrate.builder;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.terminology.OpenCDSConceptTypes;

public class SupportedConceptsBuilder {
    private static final Log log = LogFactory.getLog(SupportedConceptsBuilder.class);

    public void loadSupportedConcepts(OpencdsCache cache) {
        // load codeSystem arrays
        List<String> conceptTypes = OpenCDSConceptTypes.getOpenCdsConceptTypes();// conceptTypesRootEntity.getChildrenWithLabel("ConceptType");
        
        cache.put(ConfigCacheRegion.DATA, ConfigResourceType.SUPPORTED_CONCEPTS, conceptTypes);
        log.trace("OpenCDSConceptTypes: " + conceptTypes);
    }

}
