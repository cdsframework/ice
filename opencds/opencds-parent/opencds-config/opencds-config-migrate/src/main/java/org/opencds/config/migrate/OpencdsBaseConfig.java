/**
 * 
 */
package org.opencds.config.migrate;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.migrate.utilities.FileUtility;
import org.opencds.config.migrate.utilities.ResourceUtility;

/**
 * OpencdsConfigurator parses the config file, and returns values within it via
 * getters, and protected setters...
 * 
 * @author David Shields
 * 
 * @date 2012-01-02
 * 
 */
public class OpencdsBaseConfig {

    private static Log log = LogFactory.getLog(OpencdsBaseConfig.class);

    private final BaseConfigLocation baseConfigLocation;

    private final Map<ConfigResourceType, ConfigResource> configResources;

    private FileUtility fileUtility;

    public OpencdsBaseConfig(String knowledgeRepositoryType, String knowledgeRespositoryPath,
            Map<ConfigResourceType, ConfigResource> configResources) {
        if (knowledgeRepositoryType == null) {
            throw new OpenCDSRuntimeException("OpencdsBaseConfig requires knowledge-repository.type.");
        }
        if (knowledgeRespositoryPath == null) {
            throw new OpenCDSRuntimeException("OpencdsBaseConfig requires knowledge-repository.path.");
        }
        if (configResources == null) {
            throw new OpenCDSRuntimeException("OpencdsBaseConfig requires a non-null configurationResources map");
        }
        this.configResources = configResources;

        BaseConfigLocationType krType = BaseConfigLocationType.resolveType(knowledgeRepositoryType);
        if (krType == null) {
            throw new OpenCDSRuntimeException(
                    "OpencdsConfigurator found knowledge-repository-type.type='"
                            + krType
                            + "' in opencds-decision-support-service-config.xml. "
                            + "Only 'SIMPLE_FILE' and 'CLASSPATH' are supported at this time");
        }
        knowledgeRespositoryPath = normalizePath(krType, knowledgeRespositoryPath);
        log.debug("opencds-decision-support-service-config.krPath=" + knowledgeRespositoryPath);
        baseConfigLocation = new BaseConfigLocation(krType, knowledgeRespositoryPath);
    }

    private String normalizePath(BaseConfigLocationType krType, String path) {
    	if (krType == BaseConfigLocationType.CLASSPATH) {
    		if (!path.endsWith("/")) {
    			return path + "/";
    		}
    	}
        if (!path.endsWith(File.separator) && !path.endsWith("/")) {
            return path + File.separator;
        }
        return path;
    }

    public BaseConfigLocation getKnowledgeRepositoryLocation() {
        return baseConfigLocation;
    }

    public ResourceUtility getResourceUtility() {
        return getFileUtility();
    }

    public boolean isReloadable() {
        return baseConfigLocation.getType().isReloadable();
    }

    public ConfigResource getConfigResource(ConfigResourceType type) {
        return configResources.get(type);
    }

    private FileUtility getFileUtility() {
        return this.fileUtility;
    }

    public void setFileUtility(FileUtility fileUtility) {
        this.fileUtility = fileUtility;
    }

}
