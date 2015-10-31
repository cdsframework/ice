package org.opencds.config.strategy;

import java.util.Set;

public abstract class AbstractConfigStrategy implements ConfigStrategy {
    protected static final String KNOWLEDGE_PACKAGE_DIR = "knowledgePackages";
    protected static final String CDMS = "conceptDeterminationMethods";
    protected static final String EXECUTION_ENGINES = "executionEngines.xml";
    protected static final String KNOWLEDGE_MODULES = "knowledgeModules.xml";
    protected static final String SEMANTIC_SIGNIFIERS = "semanticSignifiers.xml";
    
    protected static final String PLUGIN_DIR = "plugins";
    protected static final String SUPPORTING_DATA_DIR = "supportingData";
    // for use in file-based or classpath-based configurations
    protected static final String PACKAGES = "packages";

    private final Set<ConfigCapability> capabilities;
    private final String supportedConfigType;

    /**
     * Extensions must provide a set of {@link ConfigCapability} and a <tt>supportedConfigType</tt>, which will be
     * provided in the externalized OpenCDS Configuration properties.
     * 
     * @param capabilities
     * @param supportedConfigType
     */
    protected AbstractConfigStrategy(Set<ConfigCapability> capabilities, String supportedConfigType) {
        this.capabilities = capabilities;
        this.supportedConfigType = supportedConfigType;
    }
    
    @Override
    public boolean supports(String configType) {
        return supportedConfigType.equalsIgnoreCase(configType);
    }
    
    @Override
    public boolean isReloadable() {
        return capabilities.contains(ConfigCapability.RELOAD);
    }

    @Override
    public boolean isUpdatable() {
        return capabilities.contains(ConfigCapability.UPDATE);
    }
    
}
