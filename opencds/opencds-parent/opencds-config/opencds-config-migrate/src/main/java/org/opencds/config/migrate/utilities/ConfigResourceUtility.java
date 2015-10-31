package org.opencds.config.migrate.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.migrate.BaseConfigLocationType;
import org.opencds.config.migrate.ConfigResource;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.OpencdsBaseConfig;

public class ConfigResourceUtility {
    private static final Log log = LogFactory.getLog(ConfigResourceUtility.class);

    public String getResourceAsString(OpencdsBaseConfig opencdsBaseConfig, ConfigResourceType configResourceType) {
        ConfigResource configResource = opencdsBaseConfig.getConfigResource(configResourceType);
        if (configResource == null) {
            throw new OpenCDSRuntimeException("config resource type not found: type= " + configResourceType);
        }
        try {
            String path = opencdsBaseConfig.getResourceUtility().renormalizeSeparator(
                    opencdsBaseConfig.getKnowledgeRepositoryLocation().getPath() + configResource.getLocation() + "/"
                            + configResource.getName());
            log.debug("attempting to get resource as string: path= " + path);
            return opencdsBaseConfig.getResourceUtility().getContents(path);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new OpenCDSRuntimeException("Failed to get ConfigResource contents: configResourceType= "
                    + configResourceType);
        }
    }

    public String getResourceAsString(OpencdsBaseConfig opencdsBaseConfig, String resource) {
        // comment copied from SimpleKR
        // the interim notion is that each type of resource will be in a
        // separate directory, whose directoryName == the resourceType name
        // the interim notion will be replaced by the actual knowledgeRepository
        // once it is completed
        try {
            log.debug("attempting to get resource as string: resource= " + resource);
            return opencdsBaseConfig.getResourceUtility().getContents(resource);
        } catch (Exception e) {
            throw new OpenCDSRuntimeException(e.getMessage());
        }
    }

    /**
     * Get a list of all the resource ID values available for a given resource
     * type. Returns null if the resourceType is not recognized. Returns an
     * empty list if no resources found for a valid type.
     * 
     * @param resourceType
     * @return resourceList
     * @throws IOException
     */
    // public synchronized List<String> listResourceNamesByType(
    // OpencdsBaseConfig opencdsBaseConfig, String resourceType)
    // throws OpenCDSRuntimeException {
    // List<String> resourceList = new ArrayList<String>();
    // String[] resourceNameList = getKRResourceTypeListing(opencdsBaseConfig,
    // resourceType);
    // for (String resourceName : resourceNameList) {
    // resourceList.add(resourceName);
    // }
    // return resourceList;
    // }

    /**
     * Get a list of all the resource ID values available for a given resource
     * type. Returns null if the resourceType is not recognized. Returns an
     * empty list if no resources found for a valid type.
     * 
     * @param config
     * @return configResourceType
     */
    public List<String> listResourceNamesByType(OpencdsBaseConfig config, ConfigResourceType configResourceType) {
        List<String> resources = new ArrayList<>();
        ConfigResource configResource = config.getConfigResource(configResourceType);
        
        if (configResource == null) {
            throw new OpenCDSRuntimeException("No configuration found for resource type: " + configResourceType);
        }
        System.err.println("config: " + config);
        System.err.println("config.getResourceUtility: " + config.getResourceUtility());
        String relativeResourceLocation = config.getResourceUtility().renormalizeSeparator(configResource.getLocation());
        log.debug("getting resource type: " + configResourceType + " from path: " + relativeResourceLocation);
        String[] listing = getKRResourceTypeListing(config, relativeResourceLocation);
        String basePath = getBasePath(config, configResource);
        for (String name : listing) {
            resources.add(basePath + name);
        }
        return resources;
    }

    /**
     * Returns the base path depending upon which configlocation type is being used.
     * @param config
     * @param configResource
     * @return
     */
    private String getBasePath(OpencdsBaseConfig config, ConfigResource configResource) {
        String basePath = "";
        if (config.getKnowledgeRepositoryLocation().getType() != BaseConfigLocationType.CLASSPATH) {
            basePath = config.getResourceUtility().renormalizeSeparator(
                    config.getKnowledgeRepositoryLocation().getPath() + configResource.getLocation() + "/");
        }
        return basePath;
    }

    /**
     * Get an input stream for the given resource type and id (name).
     * 
     * @param opencdsBaseConfig
     * @param resourceType
     * @param resourceId
     * @return
     */
    public InputStream getResourceAsInputStream(OpencdsBaseConfig opencdsBaseConfig, ConfigResourceType resourceType,
            String resourceId) {
        ConfigResource configResource = opencdsBaseConfig.getConfigResource(resourceType);
        if (configResource == null) {
            throw new OpenCDSRuntimeException("unable to find config resource, type= " + resourceType);
        }
        String path = opencdsBaseConfig.getResourceUtility().renormalizeSeparator(
                opencdsBaseConfig.getKnowledgeRepositoryLocation().getPath() + configResource.getLocation() + "/"
                        + resourceId);
        log.debug("attempting to create input stream from resource: path=" + path);
        return opencdsBaseConfig.getResourceUtility().getResourceAsInputStream(path);
    }

    /**
     * Lists directory contents for a resource folder. Not recursive. This is
     * basically a brute-force implementation.
     * 
     * @param clazz
     *            Any java class that lives in the same place as the desired
     *            resources.
     * @param path
     *            Should end with "/", but not start with one.
     * @return Name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     */
    private synchronized String[] getKRResourceTypeListing(OpencdsBaseConfig opencdsBaseConfig, String resourceType) {
        String directoryPath = opencdsBaseConfig.getKnowledgeRepositoryLocation().getPath() + resourceType;
        List<String> result = opencdsBaseConfig.getResourceUtility().listMatchingResources(directoryPath, "", "");
        return result.toArray(new String[result.size()]);
    }

}
