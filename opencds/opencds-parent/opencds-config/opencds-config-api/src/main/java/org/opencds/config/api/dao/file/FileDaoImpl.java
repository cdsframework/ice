package org.opencds.config.api.dao.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.util.ResourceUtil;

public class FileDaoImpl implements FileDao {
    private static final int BUF = 4 * 1024 * 1024;
    private final File cacheFileLocation;

    public FileDaoImpl(ResourceUtil resourceUtil, File cacheFileLocation) {
        this.cacheFileLocation = cacheFileLocation;
    }

    @Override
    public CacheElement find(String pk) {
        return FileCacheElement.create(pk, cacheFileLocation);
    }

    @Override
    public void persist(CacheElement e) {
        Path targetLocation = Paths.get(cacheFileLocation.toString(), e.getId());
        try (
                FileOutputStream fos = new FileOutputStream(targetLocation.toFile());
                InputStream in = e.getInputStream()) {
            byte[] b = new byte[BUF];
            int offset = 0;
            int bytesRead = 0;
            while ((bytesRead = in.read(b, offset, BUF)) != -1) {
                fos.write(b, offset, bytesRead);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void delete(CacheElement e) {
        Path targetLocation = Paths.get(cacheFileLocation.getAbsolutePath(), e.getId());
        targetLocation.toAbsolutePath().toFile().delete();
    }
    
}
