package org.opencds.config.mapper;

import org.opencds.config.api.model.Prefetch;
import org.opencds.config.api.model.impl.PrefetchImpl;

public class PrefetchMapper
{
    public static Prefetch internal(final org.opencds.config.schema.CDSHook.Prefetch external)
    {
        if (external == null)
            return null;
        return PrefetchImpl.create(ResourceMapper.internal(external.getResource()));
    }

    public static org.opencds.config.schema.CDSHook.Prefetch external(final Prefetch internal)
    {
        if (internal == null)
            return null;
        final org.opencds.config.schema.CDSHook.Prefetch pf = new org.opencds.config.schema.CDSHook.Prefetch();
        pf.getResource().addAll(ResourceMapper.external(internal.getResources()));
        return pf;
    }
}
