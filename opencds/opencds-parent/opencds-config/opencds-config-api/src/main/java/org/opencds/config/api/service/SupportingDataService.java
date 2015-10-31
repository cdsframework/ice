package org.opencds.config.api.service;

import java.io.InputStream;
import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportingData;

public interface SupportingDataService {
    SupportingData find(KMId kmId, String identifier);
    
    List<SupportingData> getAll();
    
    void persist(SupportingData sd);
    
    void delete(KMId kmId, String identifier);
    
    void deleteAll(KMId kmId);
    
    List<SupportingData> find(KMId kmid);
    
    InputStream getSupportingDataPackage(KMId kmId, String supportingDataId);

    boolean packageExists(KMId kmId, String supportingDataId);
    
    void persistSupportingDataPackage(KMId kmId, String identifier, InputStream supportingDataPackage);

    void deleteSupportingDataPackage(KMId kmId, String identifier);

}
