package org.opencds.config.mapper;

import java.util.List;
import java.util.Optional;

import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.config.schema.CDSHook.CdsHookClientIds;

public class CDSHookMapper
{
    public static CDSHook internal(final org.opencds.config.schema.CDSHook external)
    {
        if (external == null)
            return null;

        return new CDSHook(external.getHook(), external.getId(), external.getTitle(), external.getDescription(),
                Optional.ofNullable(external.getCdsHookClientIds())
                        .map(CdsHookClientIds::getClientId)
                        .map(List::copyOf)
                        .orElseGet(List::of), PrefetchMapper.internal(external.getPrefetch()),
                FhirVersion.valueOf(external.getFhirVersion()));
    }

    public static org.opencds.config.schema.CDSHook external(final CDSHook internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.CDSHook cdsHook = new org.opencds.config.schema.CDSHook();
        cdsHook.setHook(internal.hook());
        cdsHook.setTitle(internal.title());
        cdsHook.setDescription(internal.description());
        cdsHook.setId(internal.id());
        cdsHook.setCdsHookClientIds(new CdsHookClientIds());
        internal.clientIds().forEach(cdsHook.getCdsHookClientIds().getClientId()::add);
        cdsHook.setPrefetch(PrefetchMapper.external(internal.prefetch()));
        cdsHook.setFhirVersion(internal.fhirVersion().toString());
        return cdsHook;
    }
}
