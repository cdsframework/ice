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
