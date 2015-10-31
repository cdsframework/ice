package org.opencds.config.api.dao.file;

import java.io.IOException;
import java.io.InputStream;

public interface CacheElement {

    String getId();

    boolean exists();
    
    long length();
    
    InputStream getInputStream() throws IOException;

}
