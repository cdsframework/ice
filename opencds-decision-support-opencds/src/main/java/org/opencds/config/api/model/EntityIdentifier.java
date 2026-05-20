package org.opencds.config.api.model;

public interface EntityIdentifier
{
    String scopingEntityId();

    String businessId();

    String version();

    default String toEIString()
    {
        return "%s^%s^%s".formatted(scopingEntityId(), businessId(), version());
    }
}
