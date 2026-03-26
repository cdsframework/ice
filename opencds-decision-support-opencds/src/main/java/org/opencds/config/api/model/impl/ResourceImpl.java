package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Resource;

public record ResourceImpl(String name,
                           String query) implements Resource
{
    public static ResourceImpl create(final String name, final String query)
    {
        return new ResourceImpl(name, query);
    }

    public static ResourceImpl create(final Resource resource)
    {
        if (resource == null)
            return null;
        if (resource instanceof final ResourceImpl resourceImpl)
            return resourceImpl;
        return create(resource.getName(), resource.getQuery());
    }

    public static List<Resource> create(final List<Resource> resources)
    {
        if (resources == null)
            return null;
        final var res = new ArrayList<Resource>();
        for (final Resource resource : resources)
            res.add(create(resource));
        return res;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getQuery()
    {
        return query;
    }
}
