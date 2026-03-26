package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface ExecutionEngine
{
    String getIdentifier();

    String getAdapter();

    String getContext();

    String getKnowledgeLoader();

    String getDescription();

    Date getTimestamp();

    String getUserId();

    List<DssOperation> getSupportedOperations();
}
