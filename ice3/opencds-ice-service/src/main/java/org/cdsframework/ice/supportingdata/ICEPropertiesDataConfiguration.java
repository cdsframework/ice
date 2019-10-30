/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.supportingdata;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ICEPropertiesDataConfiguration {

	private Properties iceProps;
	
	public static final String _ICE_PROPERTIES_FILENAME = "ice.properties";
	private static Log logger = LogFactory.getLog(ICEPropertiesDataConfiguration.class);
	
	public ICEPropertiesDataConfiguration() {
		
		String _METHODNAME = "load(): ";
		
		String filename = "ice.properties";
		iceProps = new Properties();
		try {
			// lProps.load( new FileInputStream(filename) );
			iceProps.load(this.getClass().getClassLoader().getResourceAsStream(filename));
		} 
		catch(IOException e) {
			String lErrStr = "ICE properties file not found or could not be loaded: " + filename;
			logger.error(_METHODNAME + "Properties file not found: " + filename); 
			throw new RuntimeException(lErrStr); 
		}
	}
	
	
	public Properties getProperties() {

		return iceProps;
	}
	
	
	/**
	 * Return property value associated with property name in the ice.properties file, or null if not found
	 */
	public String getICEPropertyByName(String pPropertyName) {
		
		String _METHODNAME = "getICEPropertByName(): ";
		
		// Get the ICE knowledge repository directory location
		String lPropertyValue = iceProps.getProperty(pPropertyName);
		if (lPropertyValue == null) {
			String lDebugStr = "Property not specified in properties file: " + pPropertyName;
			if (logger.isDebugEnabled()) {
				logger.debug(_METHODNAME + lDebugStr);
			}
			return null;
		}
		else {
			if (logger.isInfoEnabled()) {
				String lInfoStr = "ICE property value specified in properties file: " + pPropertyName + "=" + lPropertyValue;
				logger.info(lInfoStr);
			}
		}
		
		return lPropertyValue;
	}
	
	
	public String getBaseRulesScopingEntityId() { 

		String _METHODNAME = "getBaseRulesScopingEntityId(): ";
		
		// Get the default scoping ID for the base ICE rules
		String baseRulesScopingEntityId = iceProps.getProperty("ice_base_rules_scoping_entity_id");
		if (baseRulesScopingEntityId == null) {
			String lErrStr = "ICE base rules scoping entity ID not specified in the properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isInfoEnabled()) {
				String lErrStr = "ICE base rules scoping entity ID specified in properties file: " + baseRulesScopingEntityId;
				logger.info(lErrStr);
			}
		}
		
		return baseRulesScopingEntityId;
	}
	
	
	public String getBaseRulesVersion() { 

		String _METHODNAME = "getBaseRulesVersion(): ";
		
		// Get the version for the base ICE rules
		String baseRulesVersion = iceProps.getProperty("ice_base_rules_version");
		if (baseRulesVersion == null) {
			String lErrStr = "ICE base rules version not specified in the properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isInfoEnabled()) {
				String lErrStr = "ICE base rules version specified in properties file: " + baseRulesVersion;
				logger.info(lErrStr);
			}
		}
		
		return baseRulesVersion;
	}
	
	
	public File getKnowledgeCommonDirectory() { 

		String _METHODNAME = "getKnowledgeCommonDirectory(): ";
		
		// Get the ICE knowledge repository directory location
		String baseConfigurationLocation = iceProps.getProperty("ice_knowledge_repository_location");
		if (baseConfigurationLocation == null) {
			String lErrStr = "ICE knowledge repository data location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isInfoEnabled()) {
				String lErrStr = "ICE knowledge repository data location specified in properties file: " + baseConfigurationLocation;
				logger.info(lErrStr);
			}
		}
		
		// Get the ICE knowledge modules subdirectory location
		String knowledgeCommonSubDirectory = iceProps.getProperty("ice_knowledge_common_subdirectory");
		if (knowledgeCommonSubDirectory == null) {
			String lErrStr = "ICE knowledge common subdirectory location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isDebugEnabled()) {
				String lInfoStr = "ICE knowledge common data location specified in properties file: " + knowledgeCommonSubDirectory;
				logger.info(lInfoStr);
			}
		}

		File lKnowledgeCommonDirectory = new File(baseConfigurationLocation, knowledgeCommonSubDirectory);
		
		return lKnowledgeCommonDirectory;
	}
	
	
	public File getKnowledgeModulesDirectory() { 

		String _METHODNAME = "getKnowledgeModulesDirectory(): ";
		
		// Get the ICE knowledge repository directory location
		String baseConfigurationLocation = iceProps.getProperty("ice_knowledge_repository_location");
		if (baseConfigurationLocation == null) {
			String lErrStr = "ICE knowledge repository data location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isInfoEnabled()) {
				String lErrStr = "ICE knowledge repository data location specified in properties file: " + baseConfigurationLocation;
				logger.info(lErrStr);
			}
		}
		
		// Get the ICE knowledge modules subdirectory location
		String knowledgeModulesSubDirectory = iceProps.getProperty("ice_knowledge_modules_subdirectory");
		if (knowledgeModulesSubDirectory == null) {
			String lErrStr = "ICE knowledge modules subdirectory location not specified in properties file";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		else {
			if (logger.isDebugEnabled()) {
				String lInfoStr = "ICE knowledge modules data location specified in properties file: " + knowledgeModulesSubDirectory;
				logger.info(lInfoStr);
			}
		}

		File lKnowledgeModulesDirectory = new File(baseConfigurationLocation, knowledgeModulesSubDirectory);
		
		return lKnowledgeModulesDirectory;
	}

}
