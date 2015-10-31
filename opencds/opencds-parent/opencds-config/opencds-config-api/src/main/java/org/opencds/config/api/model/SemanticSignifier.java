package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface SemanticSignifier {
    SSId getSSId();

    String getName();

    String getDescription();

    List<XSDComputableDefinition> getXSDComputableDefinitions();

    String getEntryPoint();

    String getExitPoint();
    
    String getFactListsBuilder();
    
    String getResultSetBuilder();

    Date getTimestamp();

    String getUserId();

}
