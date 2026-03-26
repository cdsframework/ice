package org.opencds.config.api.model.impl;

import java.util.Collections;
import java.util.List;

import org.opencds.config.api.model.Prefetch;
import org.opencds.config.api.model.Resource;

public record PrefetchImpl(List<Resource> resources) implements Prefetch
{
    public static Prefetch create(final List<Resource> resources)
    {
        return new PrefetchImpl(resources);
    }

    public static Prefetch create(final Prefetch prefetch)
    {
        if (prefetch == null)
            return null;
        if (prefetch instanceof final PrefetchImpl prefetchImpl)
            return prefetchImpl;
        return create(prefetch.getResources());
    }

    public static Prefetch empty()
    {
        return new PrefetchImpl(Collections.emptyList());
    }

    public PrefetchImpl
    {
        resources = resources == null ? Collections.emptyList() : Collections.unmodifiableList(resources);
    }

    @Override
    public List<Resource> getResources()
    {
        return resources;
    }
}
