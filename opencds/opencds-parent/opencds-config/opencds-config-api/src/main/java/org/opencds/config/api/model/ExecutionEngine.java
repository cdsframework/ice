package org.opencds.config.api.model;

import java.util.Date;
import java.util.List;

public interface ExecutionEngine {

    String getIdentifier();

    String getDescription();

    Date getTimestamp();

    String getUserId();

    List<DssOperation> getSupportedOperations();

}
