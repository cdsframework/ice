package org.opencds.config.api.service;

import java.io.IOException;
import java.io.InputStream;

import org.opencds.config.api.model.SupportingData;

public interface SupportingDataPackageService {
    
    boolean exists(SupportingData supportingData);
    
    /**
     * The implementation of this method needs to know where the knowledge packages exist on the backend.
     * 
     * If the underlying implementation cannot be resolved (e.g., file not found), this method returns null;
     * 
     * @param knowledgeModule
     * @return
     * @throws IOException 
     */
    InputStream getPackageInputStream(SupportingData supportingData);
    
    byte[] getPackageBytes(SupportingData supportingData);
    
    void persistPackageInputStream(SupportingData sd, InputStream supportingDataPackage);

    void deletePackage(SupportingData sd);

}
