package org.opencds.config.api.dao.file;

import java.io.File;

import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.util.ResourceUtil;

public class ReadOnlyFileDaoImpl implements FileDao {
    private final File cacheFileLocation;

    public ReadOnlyFileDaoImpl(ResourceUtil resourceUtil, File cacheFileLocation) {
        this.cacheFileLocation = cacheFileLocation;
    }

    @Override
    public CacheElement find(String pk) {
        return FileCacheElement.create(pk, cacheFileLocation);
    }

    @Override
    public void persist(CacheElement e) {
        throw new UnsupportedOperationException("Cannot persist to file store through dao API");
    }

    @Override
    public void delete(CacheElement e) {
        throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
    }
    
}
