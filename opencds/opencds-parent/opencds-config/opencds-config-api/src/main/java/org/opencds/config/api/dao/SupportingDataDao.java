package org.opencds.config.api.dao;

import java.util.List;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;

public interface SupportingDataDao {

    SupportingData find(KMId kmId, String identifier);
    
    List<SupportingData> find(KMId kmid);
    
    List<SupportingData> getAll();

    void persist(SupportingData sd);

    void delete(SupportingData sd);

}
