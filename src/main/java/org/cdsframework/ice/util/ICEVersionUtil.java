package org.cdsframework.ice.util;

import java.util.Map;

import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ICEVersionUtil
{
    @Getter
    private static String iceVersion = "unknown";

    public static void setVersionData(final Map<String, String> map)
    {
        if (ObjectUtils.isEmpty(map))
        {
            log.warn("Got empty map for version information! shouldn't happen! map={}", map);
            return;
        }

        if (!map.containsKey("iceVersion"))
        {
            log.warn("Version map doesn't contain 'iceVersion'! map={}", map);
            return;
        }

        iceVersion = map.get("iceVersion");
        log.info("ICE Version set to {}", iceVersion);
    }
}
