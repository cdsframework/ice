package org.opencds.plugin.api;

import java.io.File;
import java.util.function.Supplier;

public record SupportingDataPackage(Supplier<File> fileSupplier,
                                    Supplier<byte[]> bytesSupplier)
{
    public static SupportingDataPackage create(final Supplier<File> fileSupplier, final Supplier<byte[]> bytesSupplier)
    {
        return new SupportingDataPackage(fileSupplier, bytesSupplier);
    }

    public SupportingDataPackage
    {
        assert fileSupplier != null;
        assert bytesSupplier != null;
    }

    public File getFile()
    {
        return fileSupplier.get();
    }
}
