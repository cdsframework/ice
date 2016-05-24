package org.cdsframework.ice.service.configurations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.plugin.PluginDataCache;
import org.opencds.plugin.PreProcessPlugin;
import org.opencds.plugin.SupportingData;

public class SupportingDataPlugin implements PreProcessPlugin {
    private static final Log log = LogFactory.getLog(SupportingDataPlugin.class);

    private static final String SD_CONCEPTS = "concepts";

    @Override
    public void execute(PreProcessPluginContext context) {
        Map<String, SupportingData> supportingData = context.getSupportingData();
        PluginDataCache cache = context.getCache();

        SupportingData sd = supportingData.get(SD_CONCEPTS);
        log.debug("SD: " + sd);
        Properties concepts = new Properties();
        if (sd != null) {
            concepts = cache.get(sd);
            if (concepts == null) {
                Map<String, SupportingData> sdMap = context.getSupportingData();
                sd = sdMap.get(SD_CONCEPTS);
                byte[] data = sd.getData();
                try {
                    concepts = new Properties();
                    concepts.load(new ByteArrayInputStream(data));
                    cache.put(sd, concepts);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // how do we know which supporting data is the one?
            }
        }

    }

}
