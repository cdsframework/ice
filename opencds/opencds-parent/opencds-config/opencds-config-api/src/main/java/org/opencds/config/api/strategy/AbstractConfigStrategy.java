/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.api.strategy;

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
