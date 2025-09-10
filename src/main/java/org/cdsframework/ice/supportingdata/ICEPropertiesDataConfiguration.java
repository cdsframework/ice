/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.supportingdata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ICEPropertiesDataConfiguration
{
    private final Properties iceProps;

    public ICEPropertiesDataConfiguration()
    {
        final String _METHODNAME = "load(): ";

        final String filename = "ice.properties";
        iceProps = new Properties();
        try
        {
            // lProps.load( new FileInputStream(filename) );
            iceProps.load(this.getClass().getClassLoader().getResourceAsStream(filename));
        }
        catch (final IOException e)
        {
            final String lErrStr = "ICE properties file not found or could not be loaded: " + filename;
            log.error(_METHODNAME + "Properties file not found: " + filename);
            throw new RuntimeException(lErrStr);
        }
    }

    public Properties getProperties()
    {
        return iceProps;
    }

    /**
     * Return property value associated with property name in the ice.properties file, or null if not found
     */
    public String getICEPropertyByName(final String pPropertyName)
    {
        final String _METHODNAME = "getICEPropertByName(): ";

        // Get the ICE knowledge repository directory location
        final String lPropertyValue = iceProps.getProperty(pPropertyName);
        if (lPropertyValue == null)
        {
            final String lDebugStr = "Property not specified in properties file: " + pPropertyName;
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "{}", lDebugStr);
            return null;
        }

        if (log.isInfoEnabled())
            log.info("ICE property value specified in properties file: {}={}", pPropertyName, lPropertyValue);

        return lPropertyValue;
    }

    public String getBaseRulesScopingEntityId()
    {
        final String _METHODNAME = "getBaseRulesScopingEntityId(): ";

        // Get the default scoping ID for the base ICE rules
        final String baseRulesScopingEntityId = iceProps.getProperty("ice_base_rules_scoping_entity_id");
        if (baseRulesScopingEntityId == null)
        {
            final String lErrStr = "ICE base rules scoping entity ID not specified in the properties file";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isInfoEnabled())
            log.info("ICE base rules scoping entity ID specified in properties file: {}", baseRulesScopingEntityId);

        return baseRulesScopingEntityId;
    }

    public String getBaseRulesVersion()
    {
        final String _METHODNAME = "getBaseRulesVersion(): ";

        // Get the version for the base ICE rules
        final String baseRulesVersion = iceProps.getProperty("ice_base_rules_version");
        if (baseRulesVersion == null)
        {
            final String lErrStr = "ICE base rules version not specified in the properties file";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isInfoEnabled())
            log.info("ICE base rules version specified in properties file: {}", baseRulesVersion);

        return baseRulesVersion;
    }

    public Path getKnowledgeCommonDirectory()
    {
        final String _METHODNAME = "getKnowledgeCommonDirectory(): ";

        // Get the ICE knowledge repository directory location
        final String baseConfigurationLocation = iceProps.getProperty("ice_knowledge_config_location");
        if (baseConfigurationLocation == null)
        {
            final String lErrStr = "ICE knowledge repository data location not specified in properties file";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isInfoEnabled())
            log.info("ICE knowledge repository data location specified in properties file: {}", baseConfigurationLocation);

        // Get the ICE knowledge modules subdirectory location
        final String knowledgeCommonSubDirectory = iceProps.getProperty("ice_knowledge_common_subdirectory");
        if (knowledgeCommonSubDirectory == null)
        {
            final String lErrStr = "ICE knowledge common subdirectory location not specified in properties file";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isDebugEnabled())
            log.info("ICE knowledge common data location specified in properties file: {}", knowledgeCommonSubDirectory);

        return Path.of(baseConfigurationLocation, knowledgeCommonSubDirectory);
    }

    public Path getKnowledgeModulesDirectory()
    {
        final String _METHODNAME = "getKnowledgeModulesDirectory(): ";

        // Get the ICE knowledge repository directory location
        final String baseConfigurationLocation = iceProps.getProperty("ice_knowledge_config_location");
        if (baseConfigurationLocation == null)
        {
            final String lErrStr = "ICE knowledge repository data location not specified in properties file";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isInfoEnabled())
            log.info("ICE knowledge repository data location specified in properties file: {}", baseConfigurationLocation);

        // Get the ICE knowledge modules subdirectory location
        final String knowledgeModulesSubDirectory = iceProps.getProperty("ice_knowledge_modules_subdirectory");
        if (knowledgeModulesSubDirectory == null)
        {
            final String lErrStr = "ICE knowledge modules subdirectory location not specified in properties file";
            log.error(_METHODNAME + lErrStr);
            throw new RuntimeException(lErrStr);
        }

        if (log.isDebugEnabled())
            log.info("ICE knowledge modules data location specified in properties file: {}", knowledgeModulesSubDirectory);

        return Path.of(baseConfigurationLocation, knowledgeModulesSubDirectory);
    }
}
