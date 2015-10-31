package org.opencds.config.api.dao.util;

import java.io.InputStream;
import java.util.List;

public interface ResourceUtil {

    List<String> findFiles(String path, boolean traverse);

    InputStream getResourceAsStream(String resource);

    List<String> listMatchingResources(String path, String startsWith, String endsWith);

}
