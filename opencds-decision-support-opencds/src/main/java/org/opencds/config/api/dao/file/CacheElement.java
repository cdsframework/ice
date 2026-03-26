package org.opencds.config.api.dao.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public interface CacheElement
{
    String id();

    boolean exists();

    long length();

    InputStream inputStream() throws FileNotFoundException;

    File getFile();
}
