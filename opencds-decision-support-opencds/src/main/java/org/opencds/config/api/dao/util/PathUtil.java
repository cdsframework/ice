package org.opencds.config.api.dao.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtil
{
    public static List<Path> findFiles(final Path path, final boolean traverse)
    {
        try (final Stream<Path> stream = Files.find(path, traverse ? Integer.MAX_VALUE : 1, (_, a) -> a.isRegularFile()))
        {
            return stream.toList();
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static InputStream getResourceAsStream(final Path path)
    {
        try
        {
            return Files.newInputStream(path);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
