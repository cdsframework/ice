package org.opencds.config.api.model;

import java.util.Date;

public interface SemanticSignifier
{
    SSId getSSId();

    String getName();

    String getDescription();

    String getEntryPoint();

    String getExitPoint();

    String getFactListsBuilder();

    String getResultSetBuilder();

    Date getTimestamp();

    String getUserId();
}
