package org.cdsframework.ice.service.configurations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    
    
    // byte[] data = knowledgeRepository.getSupportingDataPackageService().getPackageBytes(sd);
    //   org.opencds.plugin.SupportingData lSD = org.opencds.plugin.SupportingData.create(sd.getIdentifier(), EntityIdentifierUtil.makeEIString(sd.getKMId()),
    //         EntityIdentifierUtil.makeEIString(sd.getLoadedBy()), sd.getPackageId(), sd.getPackageType(), new byte[0]);
}
