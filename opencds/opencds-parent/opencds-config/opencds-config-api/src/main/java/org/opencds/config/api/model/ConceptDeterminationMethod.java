package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface ConceptDeterminationMethod {
    
    CDMId getCDMId();

    String getDisplayName();
    
    Date getTimestamp();
    
    String getDescription();
    
    String getUserId();
    
    List<ConceptMapping> getConceptMappings();
    
}
