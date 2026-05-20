package org.opencds.config.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.opencds.common.utilities.XMLDateUtility;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.schema.Package;
import org.opencds.config.schema.PluginId;
import org.opencds.config.schema.SupportingDataList;

public abstract class SupportingDataMapper
{
    public static SupportingData internal(final org.opencds.config.schema.SupportingData external)
    {
        if (external == null)
            return null;

        final org.opencds.config.schema.Package pkg = external.getPackage();

        return new SupportingData(external.getIdentifier(), Optional.ofNullable(external.getKmId())
                .map(id -> new KMId(id.getScopingEntityId(), id.getBusinessId(), id.getVersion()))
                .orElse(null), Optional.ofNullable(pkg).map(Package::getPackageType).orElse(null),
                Optional.ofNullable(pkg).map(Package::getPackageId).orElse(null), PluginIdMapper.internal(external.getLoadedBy()),
                LocalDate.now(), external.getUserId());

    }

    public static List<SupportingData> internal(final SupportingDataList external)
    {
        return external.getSupportingData().stream().map(SupportingDataMapper::internal).toList();
    }

    public static org.opencds.config.schema.SupportingData external(final SupportingData internal)
    {
        if (internal == null)
            return null;

        final org.opencds.config.schema.SupportingData external = new org.opencds.config.schema.SupportingData();

        external.setIdentifier(internal.identifier());

        external.setKmId(Optional.ofNullable(internal.kmId()).map(id ->
        {
            final org.opencds.config.schema.KMId externalKMId = new org.opencds.config.schema.KMId();
            externalKMId.setBusinessId(id.businessId());
            externalKMId.setScopingEntityId(id.scopingEntityId());
            externalKMId.setVersion(id.version());
            return externalKMId;
        }).orElse(null));

        final org.opencds.config.schema.Package pkg = new org.opencds.config.schema.Package();
        pkg.setPackageId(internal.packageId());
        pkg.setPackageType(internal.packageType());
        external.setPackage(pkg);

        final PluginId plg = new PluginId();
        plg.setScopingEntityId(internal.loadedBy().scopingEntityId());
        plg.setBusinessId(internal.loadedBy().businessId());
        plg.setVersion(internal.loadedBy().version());
        external.setLoadedBy(plg);

        external.setTimestamp(XMLDateUtility.date2XMLGregorian(internal.timestamp()));
        external.setUserId(internal.userId());

        return external;
    }

    public static SupportingDataList external(final List<SupportingData> supportingDataInternal)
    {
        if (supportingDataInternal == null)
            return null;

        final SupportingDataList externalSDList = new SupportingDataList();

        supportingDataInternal.stream().map(SupportingDataMapper::external).forEach(externalSDList.getSupportingData()::add);

        return externalSDList;
    }
}
