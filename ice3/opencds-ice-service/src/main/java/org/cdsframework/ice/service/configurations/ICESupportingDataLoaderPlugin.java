package org.cdsframework.ice.service.configurations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Schedule;
import org.cdsframework.ice.supportingdata.ICEPropertiesDataConfiguration;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.config.api.model.KMId;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.PreProcessPlugin;
import org.opencds.plugin.SupportingData;

public class ICESupportingDataLoaderPlugin implements PreProcessPlugin {
    private static final Log logger = LogFactory.getLog(ICESupportingDataLoaderPlugin.class);

    private static final String SD_ICE = "ice-supporting-data";

    
    public static boolean supportingDataAlreadyLoadedInContext(PreProcessPluginContext context) {

    	String _METHODNAME = "supportingDataAlreadyLoadedForContext(): ";
    	if (context == null) {
    		if (logger.isDebugEnabled()) {
    			logger.debug(_METHODNAME + "context provided is null");
    		}
    		return false;
    	}    	
        SupportingData sd = context.getSupportingData().get(SD_ICE);
        if (sd == null) {
        	if (logger.isDebugEnabled()) {
        		logger.debug(_METHODNAME + "supporting data populated in context for " + SD_ICE);
        	}
        	return false;
        }
        String lKmId = sd.getKmId();
    	if (logger.isDebugEnabled()) {
    		logger.debug(_METHODNAME + "supporting data populated in context for " + SD_ICE + "; KmID is: " + lKmId);
    	}

        PluginDataCache cache = context.getCache();            	
    	if (cache == null || cache.get(lKmId) == null) {
    		if (logger.isDebugEnabled()) {
        		logger.debug(_METHODNAME + "No cache in context; returning false");
        	}
    		return false;
    	}

    	return true;
    }

    
    @Override 
    public void execute(PreProcessPluginContext context) {
    	
    	String _METHODNAME = "execute(): ";
    	Map<String, SupportingData> supportingData = context.getSupportingData();
        PluginDataCache cache = context.getCache();
        SupportingData sd = supportingData.get(SD_ICE);
    	String lKMId = sd.getKmId();
        Schedule schedule = cache.get(lKMId); 
        if (schedule == null) {
        	// Schedule has not been stored in supporting data - load it - This should only happen once.
        	logger.info("Loading immunization schedule for Knowledge Module: " + lKMId);
        	loadImmunizationSchedule(lKMId, cache);
        	logger.info(_METHODNAME + "Immunization schedule loaded for knowledge module: " + lKMId);
        }
        else if (logger.isDebugEnabled()) {
        	logger.debug(_METHODNAME + "Immunization schedule previously loaded");
        }
    }
    
    
    /**
     * Given an ICE knowledge module identifier in the correct format, load its corresponding Schedule into the provided cache
     */
    private synchronized void loadImmunizationSchedule(String pRequestedKMIdStr, PluginDataCache pCache) {
    	
    	String _METHODNAME = "loadImmunizationSchedule(): ";
    	
    	if (pCache == null) {
    		String lErrStr = "PluginDataCache parameter not specified";
    		logger.error(_METHODNAME + lErrStr);
    		throw new RuntimeException(lErrStr);
    	}
    	
    	Schedule s = pCache.get(pRequestedKMIdStr);
    	if (s != null) {
    		if (logger.isDebugEnabled()) {
    			logger.debug(_METHODNAME + "Immunization schedule already loaded");
    		}
    		// Schedule is already loaded
    		return;
    	}
    	
    	// Determine requested KM ID Object
    	KMId lRequestedKMIdObject = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(pRequestedKMIdStr);
    	if (lRequestedKMIdObject == null) {
    		String lErrStr = "Invalid knowledge module provided; cannot continue";
    		logger.error(_METHODNAME + lErrStr);
    		throw new RuntimeException(lErrStr);
    	}
    	
    	// Determine base rules KM ID in String format
    	ICEPropertiesDataConfiguration iceProps = new ICEPropertiesDataConfiguration();
    	String lBaseRulesScopingKmId = KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(iceProps.getBaseRulesScopingEntityId(), lRequestedKMIdObject.getBusinessId(), 
    		lRequestedKMIdObject.getVersion());
    	
		// Initialize schedule 
		logger.info("Initializing Schedule");
		List<String> cdsVersions = new ArrayList<String>();
		cdsVersions.add(lBaseRulesScopingKmId);
		cdsVersions.add(pRequestedKMIdStr);
		try {
			s = new Schedule("requestedKmId", cdsVersions, iceProps.getBaseKnowledgeModulesDirectory());
		}
		catch (ImproperUsageException | InconsistentConfigurationException ii) {
			String lErrStr = "Failed to initialize immunization schedule";
			logger.error(_METHODNAME + lErrStr);
			throw new RuntimeException(lErrStr);
		}
		logger.info("Schedule Initialization complete");

		// Store Schedule into cache
    	pCache.put(pRequestedKMIdStr, s);
    }
    
    
    /*
    @Override
    public void execute(PreProcessPluginContext context) {

    	String _METHODNAME = "execute(): ";
    	Map<String, SupportingData> supportingData = context.getSupportingData();
        PluginDataCache cache = context.getCache();
        SupportingData sd = supportingData.get(SD_ICE);
        if (logger.isDebugEnabled()) {
        	logger.debug(_METHODNAME + "SD: " + sd);
        }
        Properties concepts = new Properties();
        if (sd != null) {
        	String lKmId = sd.getKmId();
        	if (logger.isDebugEnabled()) {
        		logger.debug(_METHODNAME + "supportingData KmID: " + lKmId);
        	}
            concepts = cache.get(lKmId);
            if (concepts == null) {
            	if (logger.isDebugEnabled()) {
            		logger.debug(_METHODNAME + "supporting data not previously stored in cache. Loading it in now");
            	}
                Map<String, SupportingData> sdMap = context.getSupportingData();
                sd = sdMap.get(SD_ICE);
                byte[] data = sd.getData();
                try {
                    concepts = new Properties();
                    concepts.load(new ByteArrayInputStream(data));
                    cache.put(lKmId, concepts);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // how do we know which supporting data is the one?
            }
            else {
            	if (logger.isDebugEnabled()) {
            		logger.debug(_METHODNAME + "supporting data found in cache.");
            	}

            }
        }
        
        concepts.list(System.out);
    }
	*/
}
