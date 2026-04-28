package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.cdsframework.ice.config.IceProperties;
import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.cdsframework.ice.service.SupportingDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { IceProperties.class, IceSupportingDataProperties.class },
                      initializers = ConfigDataApplicationContextInitializer.class)
@TestPropertySource(properties = {
        "spring.config.import=classpath:application.yml,classpath:data/knowledgeCommon/org.cdsframework.ice/ice-supporting-data/iceSupportingData.yml,classpath:data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/iceSupportingData.yml" })
@EnableConfigurationProperties({ IceProperties.class, IceSupportingDataProperties.class })
public class FullSupportingDataLoadTest
{

    @Autowired
    private IceSupportingDataProperties iceSupportingDataProperties;

    @Autowired
    private IceProperties iceProperties;

    @Test
    public void testLoadFullSupportingData_CommonAndNYC()
    {
        assertNotNull(iceSupportingDataProperties, "IceSupportingDataProperties should be injected");
        assertNotNull(iceProperties, "IceProperties should be injected");
        final SupportingDataService supportingDataService = new SupportingDataService(iceSupportingDataProperties, iceProperties);

        assertDoesNotThrow(() ->
        {
            try
            {
                new ICESupportingDataConfiguration("org.cdsframework^ICE^1.0.0", List.of("org.nyc.cir^ICE^1.0.0"),
                        iceSupportingDataProperties, supportingDataService);
            }
            catch (final Exception e)
            {
                // Print detailed error for debugging
                Throwable root = e;
                int depth = 0;
                final StringBuilder sb = new StringBuilder();
                sb.append("CAUSE_CHAIN:\n");
                while (root != null && depth < 20)
                {
                    final String line = "Cause[%d]: %s: %s".formatted(depth, root.getClass().getName(), root.getMessage());
                    log.info("[DEBUG_LOG] {}", line);
                    sb.append(line).append('\n');
                    root = root.getCause();
                    depth++;
                }
                log.error(e.getMessage(), e);
                throw new RuntimeException(sb.toString(), e);
            }
        });
    }
}
