package org.opencds.config.mapper;

import java.util.List;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.LoadContext;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.schema.PluginLoadContext;
import org.opencds.config.schema.PluginPackageId;
import org.opencds.config.schema.PluginPackages;

public abstract class PluginPackageMapper
{
    public static PluginPackage internal(final org.opencds.config.schema.PluginPackage external)
    {
        if (external == null)
            return null;

        return new PluginPackage(new PPId(external.getIdentifier().getScopingEntityId(), external.getIdentifier().getBusinessId(),
                external.getIdentifier().getVersion()), LoadContext.resolve(external.getLoadContext().name()),
                external.getResourceName(), PluginMapper.internal(external.getPlugins()),
                external.getTimestamp().toGregorianCalendar().toZonedDateTime().toLocalDate(), external.getUserId());
    }

    public static List<PluginPackage> internal(final PluginPackages external)
    {
        if (external == null)
            return null;

        return external.getPluginPackage().stream().map(PluginPackageMapper::internal).toList();
    }

    public static org.opencds.config.schema.PluginPackage external(final PluginPackage internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.PluginPackage external = new org.opencds.config.schema.PluginPackage();

        final PluginPackageId ppid = new PluginPackageId();
        ppid.setScopingEntityId(internal.identifier().scopingEntityId());
        ppid.setBusinessId(internal.identifier().businessId());
        ppid.setVersion(internal.identifier().version());
        external.setIdentifier(ppid);

        external.setLoadContext(PluginLoadContext.fromValue(internal.loadContext().name()));
        external.setResourceName(internal.resourceName());
        external.setPlugins(PluginMapper.external(internal.plugins()));
        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.timestamp()));
        external.setUserId(internal.userId());

        return external;
    }

    public static PluginPackages external(final List<PluginPackage> internal)
    {
        if (internal == null)
            return null;
        final PluginPackages external = new PluginPackages();
        for (final PluginPackage pp : internal)
            external.getPluginPackage().add(external(pp));
        return external;
    }
}
