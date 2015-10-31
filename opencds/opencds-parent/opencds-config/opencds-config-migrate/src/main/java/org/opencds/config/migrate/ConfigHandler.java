package org.opencds.config.migrate;

import java.io.OutputStream;

import javax.xml.transform.Result;

public interface ConfigHandler {

    OutputStream getOutputStream() throws Exception;

    OutputStream getOutputStream(String baseName) throws Exception;
    
    <T> void write(T t) throws Exception;

    <T> void write(T t, String baseName) throws Exception;

}
