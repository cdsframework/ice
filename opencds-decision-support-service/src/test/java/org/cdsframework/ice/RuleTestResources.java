package org.cdsframework.ice;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RuleTestResources
{
    public Path sourceResources()
    {
        return Stream.iterate(Path.of("").toAbsolutePath(), Objects::nonNull, Path::getParent)
                .map((final Path directory) -> directory.resolve("opencds-decision-support-rules/src/main/resources"))
                .filter(Files::isDirectory)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot locate the ICE rule sources from the working directory"));
    }
}
