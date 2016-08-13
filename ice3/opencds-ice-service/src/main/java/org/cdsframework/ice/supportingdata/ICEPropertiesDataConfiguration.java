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
	
	
	public File getBaseKnowledgeModulesDirectory() { 

		String _METHODNAME = "getKnowledgeModulesDirectoryAsFile(): ";
		
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
