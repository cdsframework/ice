package org.opencds.config.api.dao.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.util.ResourceUtil;

public class ClasspathResourceDaoImpl implements FileDao {
    private static final Log log = LogFactory.getLog(ClasspathResourceDaoImpl.class);
    private static final String CLASSPATH_SEPARATOR = "/";

    private final ResourceUtil resourceUtil;
    private final String location;

    public ClasspathResourceDaoImpl(ResourceUtil resourceUtil, String location) {
        this.resourceUtil = resourceUtil;
        this.location = location;
    }
    
    @Override
    public CacheElement find(String pk) {
    	String path = location + CLASSPATH_SEPARATOR + pk;
    	log.debug("Finding cache element at: " + path);
        return StreamCacheElement.create(pk, resourceUtil.getResourceAsStream(path));
    }

    @Override
    public void persist(CacheElement e) {
        throw new UnsupportedOperationException("Operation not supported on this type of FileDao");
    }

    @Override
    public void delete(CacheElement e) {
        throw new UnsupportedOperationException("Operation not supported on this type of FileDao");
    }

}
