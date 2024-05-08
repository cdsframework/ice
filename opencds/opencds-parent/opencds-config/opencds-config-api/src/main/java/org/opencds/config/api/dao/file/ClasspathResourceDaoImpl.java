/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
