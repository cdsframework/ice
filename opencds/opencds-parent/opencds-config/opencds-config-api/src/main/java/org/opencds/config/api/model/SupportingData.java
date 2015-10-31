package org.opencds.config.api.model;

import java.util.Date;

public interface SupportingData {
    String getIdentifier();
    
    KMId getKMId();
    
    String getPackageType();
    
    String getPackageId();
    
    PluginId getLoadedBy();
    
    Date getTimestamp();
    
    String getUserId();
}
