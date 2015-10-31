package org.opencds.dss.util;

import org.omg.dss.common.EntityIdentifier;

public class DssUtil {
    
    public static EntityIdentifier makeEIFromCommon(org.opencds.config.api.model.EntityIdentifier commonEI) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(commonEI.getScopingEntityId());
        ei.setBusinessId(commonEI.getBusinessId());
        ei.setVersion(commonEI.getVersion());
        return ei;
    }
    
    public static EntityIdentifier makeEI( String eiString ) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId( eiString.substring(0, eiString.indexOf("^")) );
        ei.setBusinessId( eiString.substring(eiString.indexOf("^")+1, eiString.lastIndexOf("^")) );
        ei.setVersion( eiString.substring(eiString.lastIndexOf("^")+1) );
        return ei;
    }
    
    public static EntityIdentifier makeEI(String scopingEntityId, String businessId, String version) {
        EntityIdentifier ei = new EntityIdentifier();
        ei.setScopingEntityId(scopingEntityId);
        ei.setBusinessId(businessId);
        ei.setVersion(version);
        return ei;
    }
    
    public static String makeEIString ( EntityIdentifier ei ) {
        String scopingEntityId = ei.getScopingEntityId();
        String businessId = ei.getBusinessId();
        String version = ei.getVersion();
        return scopingEntityId + "^" + businessId + "^" + version;
    }

}
