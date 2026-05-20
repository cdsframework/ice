package org.opencds.config.api.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.util.StringUtils;

public record FileCacheElement(String id,
                               Path location) implements CacheElement
{
    public FileCacheElement
    {
        assert StringUtils.hasText(id);
        assert location != null;
    }

    @Override
    public boolean exists()
    {
        return getFile().exists();
    }

    @Override
    public long length()
    {
        return getFile().length();
    }

    @Override
    public File getFile()
    {
        return Paths.get(location.toString(), id).toFile();
    }

    @Override
    public InputStream inputStream() throws FileNotFoundException
    {
        return new FileInputStream(getFile());
    }
}
