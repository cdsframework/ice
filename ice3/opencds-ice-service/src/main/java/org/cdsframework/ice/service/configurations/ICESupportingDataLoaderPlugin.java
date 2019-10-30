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

    // AI: determine supportingData.identifier dynamically (fix upon OpenCDS upgrade)
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
    	if (context == null) {
    		String lErrStr = "PreProcessPluginContext not specified";
    		logger.error(_METHODNAME + lErrStr);
    		throw new IllegalArgumentException(lErrStr);
    	}
    	Map<String, SupportingData> supportingData = context.getSupportingData();
        PluginDataCache cache = context.getCache();
        SupportingData sd = supportingData.get(SD_ICE);
        if (sd == null) {
        	String lErrStr = "SupportingData not found";
    		logger.error(_METHODNAME + lErrStr);
    		throw new RuntimeException(lErrStr);
        }
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
    
    public void execute(PreProcessPluginContext context, String pKMId) {
    	
    	String _METHODNAME = "execute(PreProcessPluginContext, String): ";
    	if (context == null || pKMId == null) {
    		String lErrStr = "PreProcessPluginContext or Knowledge Module ID not specified";
    		logger.error(_METHODNAME + lErrStr);
    		throw new IllegalArgumentException(lErrStr);
    	}
        PluginDataCache cache = context.getCache();
        Schedule schedule = cache.get(pKMId); 
        if (schedule == null) {
        	// Schedule has not been stored in supporting data - load it - This should only happen once.
        	logger.info("Loading immunization schedule for Knowledge Module: " + pKMId);
        	loadImmunizationSchedule(pKMId, cache);
        	logger.info(_METHODNAME + "Immunization schedule loaded for knowledge module: " + pKMId);
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
    		iceProps.getBaseRulesVersion());
    	
		// Initialize schedule 
		logger.info("Initializing Schedule");
		List<String> cdsVersions = new ArrayList<String>();
		String lRequestedKmIdStr = (pRequestedKMIdStr != null && pRequestedKMIdStr.equals("org.nyc.cir^ICE^1.0.0")) ? "gov.nyc.cir^ICE^1.0.0" : pRequestedKMIdStr;
		cdsVersions.add(lRequestedKmIdStr);
		try {
			s = new Schedule("requestedKmId", lBaseRulesScopingKmId, iceProps.getKnowledgeCommonDirectory(), cdsVersions, iceProps.getKnowledgeModulesDirectory());
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
