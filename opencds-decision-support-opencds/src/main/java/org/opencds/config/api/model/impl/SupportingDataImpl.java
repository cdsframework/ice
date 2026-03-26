package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.SupportingData;

public record SupportingDataImpl(String identifier,
                                 KMId kmId,
                                 String packageType,
                                 String packageId,
                                 PluginId loadedBy,
                                 Date timestamp,
                                 String userId) implements SupportingData
{
    public static SupportingDataImpl create(final String identifier, final KMId kmId, final String packageType,
            final String packageId, final PluginId loadedBy, final Date timestamp, final String userId)
    {
        return new SupportingDataImpl(identifier, KMIdImpl.create(kmId), packageType, packageId, loadedBy, timestamp, userId);
    }

    public static SupportingDataImpl create(final SupportingData sd)
    {
        if (sd == null)
            return null;
        if (sd instanceof final SupportingDataImpl supportingDataImpl)
            return supportingDataImpl;
        return create(sd.getIdentifier(), sd.getKMId(), sd.getPackageType(), sd.getPackageId(), sd.getLoadedBy(), sd.getTimestamp(),
                sd.getUserId());
    }

    public static List<SupportingDataImpl> create(final List<SupportingData> sds)
    {
        if (sds == null)
            return null;
        final var sdis = new ArrayList<SupportingDataImpl>();
        for (final var sd : sds)
            sdis.add(create(sd));
        return sdis;
    }

    public SupportingDataImpl
    {
        assert StringUtils.isNotBlank(identifier);
        assert StringUtils.isNotBlank(packageType);
        assert StringUtils.isNotBlank(packageId);
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public KMId getKMId()
    {
        return kmId;
    }

    @Override
    public String getPackageType()
    {
        return packageType;
    }

    @Override
    public String getPackageId()
    {
        return packageId;
    }

    @Override
    public PluginId getLoadedBy()
    {
        return loadedBy;
    }

    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }
}
