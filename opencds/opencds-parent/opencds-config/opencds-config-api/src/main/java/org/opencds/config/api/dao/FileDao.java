package org.opencds.config.api.dao;

import org.opencds.config.api.dao.file.CacheElement;

public interface FileDao {

    CacheElement find(String pk);

    void persist(CacheElement e);

    void delete(CacheElement e);

}
