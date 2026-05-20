package org.opencds.plugin.api;

import java.time.LocalDate;
import java.util.function.Supplier;

public record SupportingData(String identifier,
                             String kmId,
                             String loadedByPluginId,
                             String packageId,
                             String packageType,
                             LocalDate timestamp,
                             Supplier<SupportingDataPackage> packageSupplier)
{
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

        if (!(obj instanceof final SupportingData rhs))
            return false;

        return identifier.equals(rhs.identifier);
    }
}
