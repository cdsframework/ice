package org.opencds.config.api.model;

import java.util.List;

public interface CdsHooksClient
{
    String id();

    String description();

    List<Issuer> issuers();

    String tenant();
}
