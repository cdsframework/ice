package org.opencds.config.api.model.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.config.api.model.Prefetch;

public record CDSHookImpl(String hook,
                          String id,
                          String title,
                          String description,
                          List<String> clientIds,
                          Prefetch prefetch,
                          FhirVersion fhirVersion) implements CDSHook
{
    public static CDSHook create(final String hook, final String id, final String title, final String description,
            final List<String> clientIds, final Prefetch prefetch, final FhirVersion fhirVersion)
    {
        return new CDSHookImpl(hook, id, title, description, clientIds, PrefetchImpl.create(prefetch), fhirVersion);
    }

    public static CDSHook create(final CDSHook cdsHook)
    {
        if (cdsHook == null)
            return null;
        if (cdsHook instanceof final CDSHookImpl cdsHookImpl)
            return cdsHookImpl;
        return create(cdsHook.getHook(), cdsHook.getId(), cdsHook.getTitle(), cdsHook.getDescription(), cdsHook.getClientIds(),
                cdsHook.getPrefetch(), cdsHook.getFhirVersion());
    }

    public CDSHookImpl
    {
        assert StringUtils.isNotBlank(hook);
        assert StringUtils.isNotBlank(id);
        assert StringUtils.isNotBlank(description);
        assert fhirVersion != null;
        clientIds = clientIds == null ? List.of() : List.copyOf(clientIds);
        prefetch = prefetch == null ? PrefetchImpl.empty() : prefetch;
    }

    @Override
    public String getHook()
    {
        return hook;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public List<String> getClientIds()
    {
        return clientIds;
    }

    @Override
    public Prefetch getPrefetch()
    {
        return prefetch;
    }

    @Override
    public FhirVersion getFhirVersion()
    {
        return fhirVersion;
    }
}
