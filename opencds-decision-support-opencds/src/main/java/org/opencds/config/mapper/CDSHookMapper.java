package org.opencds.config.mapper;

import java.util.List;
import java.util.Optional;

import org.opencds.config.api.model.CDSHook;
import org.opencds.config.api.model.FhirVersion;
import org.opencds.config.api.model.impl.CDSHookImpl;
import org.opencds.config.schema.CDSHook.CdsHookClientIds;

public class CDSHookMapper
{
    public static CDSHook internal(final org.opencds.config.schema.CDSHook external)
    {
        if (external == null)
            return null;
        return CDSHookImpl.create(external.getHook(), external.getId(), external.getTitle(), external.getDescription(),
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
        cdsHook.setHook(internal.getHook());
        cdsHook.setTitle(internal.getTitle());
        cdsHook.setDescription(internal.getDescription());
        cdsHook.setId(internal.getId());
        cdsHook.setCdsHookClientIds(new CdsHookClientIds());
        internal.getClientIds().forEach(cdsHook.getCdsHookClientIds().getClientId()::add);
        cdsHook.setPrefetch(PrefetchMapper.external(internal.getPrefetch()));
        cdsHook.setFhirVersion(internal.getFhirVersion().toString());
        return cdsHook;
    }
}
