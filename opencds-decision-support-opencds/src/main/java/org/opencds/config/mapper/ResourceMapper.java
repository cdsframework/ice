package org.opencds.config.mapper;

import java.util.Collection;
import java.util.List;

import org.opencds.config.api.model.Resource;

public class ResourceMapper
{
    public static List<Resource> internal(final List<org.opencds.config.schema.CDSHook.Prefetch.Resource> external)
    {
        if (external == null)
            return null;

        return external.stream()
                .map((org.opencds.config.schema.CDSHook.Prefetch.Resource res) -> new Resource(res.getName(), res.getQuery()))
                .toList();
    }

    public static Collection<? extends org.opencds.config.schema.CDSHook.Prefetch.Resource> external(final List<Resource> internal)
    {
        if (internal == null)
            return null;

        return internal.stream().map(res ->
        {
            final org.opencds.config.schema.CDSHook.Prefetch.Resource resource =
                    new org.opencds.config.schema.CDSHook.Prefetch.Resource();
            resource.setName(res.name());
            resource.setQuery(res.query());
            return resource;
        }).toList();
    }
}
