package org.opencds.config.api.dao;

import java.nio.file.Path;

import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.StreamCacheElement;
import org.opencds.config.api.dao.util.PathUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record FileDao(Path location)
{
    public CacheElement find(final String pk)
    {
        log.debug("Finding cache element at: {}", location);
        return new StreamCacheElement(pk, PathUtil.getResourceAsStream(location));
    }
}
