package org.opencds.config.api.model;

import java.util.List;

public interface CDSHook
{
    String getHook();

    String getId();

    String getTitle();

    String getDescription();

    List<String> getClientIds();

    Prefetch getPrefetch();

    FhirVersion getFhirVersion();
}
