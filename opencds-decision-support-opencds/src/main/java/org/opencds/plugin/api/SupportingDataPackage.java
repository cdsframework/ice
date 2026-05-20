package org.opencds.plugin.api;

import java.io.File;
import java.util.function.Supplier;

public record SupportingDataPackage(Supplier<File> fileSupplier,
                                    Supplier<byte[]> bytesSupplier)
{
    public SupportingDataPackage
    {
        assert fileSupplier != null;
        assert bytesSupplier != null;
    }
}
