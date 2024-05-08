package org.cdsframework.ice.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ICEVersionUtil
{
    private static final Logger logger = LogManager.getLogger();
    private static String iceVersion = "unknown";

    public static void setVersionData(Map<String, String> map)
    {
        if (map == null || map.isEmpty())
        {
            logger.warn("Got empty map for version information! shouldn't happen! map={}", map);
            return;
        }

        if (!map.containsKey("iceVersion"))
        {
            logger.warn("Version map doesn't contain 'iceVersion'! map={}", map);
            return;
        }
        iceVersion = map.get("iceVersion");
        logger.info("ICE Version set to {}", iceVersion);
    }

    public static String getIceVersion()
    {
        return iceVersion;
    }
}
