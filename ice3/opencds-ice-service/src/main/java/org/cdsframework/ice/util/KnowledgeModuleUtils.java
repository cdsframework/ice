package org.cdsframework.ice.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.impl.KMIdImpl;

/**
 * Some of these knowledge module routines are ICE-specific, as noted in the documentation for each method below 
 */
public class KnowledgeModuleUtils {

	private static Log logger = LogFactory.getLog(KnowledgeModuleUtils.class);
	
	
	public static String returnStringRepresentationOfKnowledgeModuleName(String scopingEntityId, String businessId, String version) {
		
		String _METHODNAME = "returnStringRepresentationOfKnowledgeModuleName(): ";
		
		if (StringUtils.isNullOrEmpty(scopingEntityId) || StringUtils.isNullOrEmpty(businessId) || StringUtils.isNullOrEmpty(version)) {
			String lErrStr = "One or more parameters not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new IllegalArgumentException(lErrStr);
		}
		
		return scopingEntityId + "^" + businessId + "^" + version;
	}
	
	
	public static KMId returnKMIdRepresentationOfKnowledgeModule(String pStringRepresentationOfKnowledgeModuleName) {
		
		if (pStringRepresentationOfKnowledgeModuleName == null) {
			return null;
		}
		
		String[] lKmIdParts = pStringRepresentationOfKnowledgeModuleName.split("\\^");
		if (lKmIdParts.length != 3) {
			return null;
		}
		else {
			return KMIdImpl.create(lKmIdParts[0], lKmIdParts[1], lKmIdParts[2]);
		}
	}
}
