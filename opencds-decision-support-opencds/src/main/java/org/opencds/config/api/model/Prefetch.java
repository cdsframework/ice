package org.opencds.config.api.model;

import java.util.List;

public record Prefetch(List<Resource> resources)
{
    public static Prefetch empty()
    {
        return new Prefetch(List.of());
    }

    public Prefetch
    {
        resources = resources == null ? List.of() : List.copyOf(resources);
    }
}
