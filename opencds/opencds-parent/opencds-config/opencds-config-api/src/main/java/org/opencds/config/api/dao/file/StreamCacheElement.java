package org.opencds.config.api.dao.file;

import java.io.IOException;
import java.io.InputStream;

public class StreamCacheElement implements CacheElement {

    private String id;
    private InputStream inputStream;

    private StreamCacheElement(String id, InputStream inputStream) {
        this.id = id;
        this.inputStream = inputStream;
    }
    
    public static CacheElement create(String id, InputStream inputStream) {
        return new StreamCacheElement(id, inputStream);
    }
    
    @Override
    public long length() {
        return -1;
    }
    
    @Override
    public boolean exists() {
        return inputStream != null;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

}
