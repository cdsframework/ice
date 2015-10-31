package org.opencds.config.api.model;

import java.util.List;

public interface XSDComputableDefinition {
    String getRootGlobalElementName();
    
    String getUrl();
    
    List<String> getSchematronUrls();
    
    String getNarrativeModelRestrictionGuideUrl();
}
