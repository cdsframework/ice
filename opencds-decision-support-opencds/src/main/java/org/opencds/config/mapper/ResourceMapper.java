package org.opencds.config.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.opencds.config.api.model.Resource;
import org.opencds.config.api.model.impl.ResourceImpl;

public class ResourceMapper
{
    public static List<Resource> internal(final List<org.opencds.config.schema.CDSHook.Prefetch.Resource> external)
    {
        if (external == null)
            return null;
        return external.stream()
                .map((org.opencds.config.schema.CDSHook.Prefetch.Resource res) -> ResourceImpl.create(res.getName(),
                        res.getQuery()))
                .collect(Collectors.toList());
    }

    public static Collection<? extends org.opencds.config.schema.CDSHook.Prefetch.Resource> external(final List<Resource> internal)
    {
        if (internal == null)
            return null;
        return internal.stream().map((final Resource res) ->
        {
            final org.opencds.config.schema.CDSHook.Prefetch.Resource resource =
                    new org.opencds.config.schema.CDSHook.Prefetch.Resource();
            resource.setName(res.getName());
            resource.setQuery(res.getQuery());
            return resource;
        }).collect(Collectors.toList());
    }
}
