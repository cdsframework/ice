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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FileCacheElement implements CacheElement {

    private final String id;
    private final File location;

    private FileCacheElement(String id, File cacheFileLocation) {
        this.id = id;
        this.location = cacheFileLocation;
    }

    public static FileCacheElement create(String id, File cacheFileLocation) {
        return new FileCacheElement(id, cacheFileLocation);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean exists() {
        return location.exists();
    }

    @Override
    public long length() {
        File file = getFile();
        return file.length();
    }

    @Override
    public File getFile() {
        if (location == null) {
            return null;
        }
        File file = Paths.get(location.getAbsolutePath(), id).toFile();
        if (!file.exists()) {
            return null;
        }
        return file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        File file = getFile();
        return new FileInputStream(file);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37, 31).append(id).append(location.getAbsolutePath()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileCacheElement other = (FileCacheElement) obj;
        return new EqualsBuilder().append(id, other.id)
                .append(location.getAbsolutePath(), other.location.getAbsolutePath()).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id: " + id).append(", location: " + location.getAbsolutePath())
                .toString();
    }

}
