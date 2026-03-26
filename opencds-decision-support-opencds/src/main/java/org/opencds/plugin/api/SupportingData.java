package org.opencds.plugin.api;

import java.util.Date;
import java.util.function.Supplier;

public record SupportingData(String identifier,
                             String kmId,
                             String loadedByPluginId,
                             String packageId,
                             String packageType,
                             Date timestamp,
                             Supplier<SupportingDataPackage> packageSupplier)
{
    public static SupportingData create(final String identifier, final String kmId, final String loadedByPluginId,
            final String packageId, final String packageType, final Date timestamp, final Supplier<SupportingDataPackage> supplier)
    {
        return new SupportingData(identifier, kmId, loadedByPluginId, packageId, packageType, timestamp, supplier);
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getKmId()
    {
        return kmId;
    }

    public String getLoadedByPluginId()
    {
        return loadedByPluginId;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    @Override
    public int hashCode()
    {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        final SupportingData rhs = (SupportingData) obj;
        return identifier.equals(rhs.identifier);
    }
}
