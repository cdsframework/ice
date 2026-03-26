package org.cdsframework.ice.supportingdata;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;

import org.cdsframework.ice.config.IceSupportingDataProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { IceSupportingDataProperties.class, org.cdsframework.ice.config.FhirConverterConfig.class }, initializers = ConfigDataApplicationContextInitializer.class)
@TestPropertySource(properties = {
        "spring.config.import=classpath:data/knowledgeCommon/org.cdsframework.ice/ice-supporting-data/iceSupportingData.yml,classpath:data/knowledgeModule/org.nyc.cir.ice/ice-supporting-data/iceSupportingData.yml" })
@EnableConfigurationProperties(IceSupportingDataProperties.class)
public class FullSupportingDataLoadTest
{

    @Autowired
    private IceSupportingDataProperties iceSupportingDataProperties;

    @Test
    public void testLoadFullSupportingData_CommonAndNYC()
    {
        assertNotNull(iceSupportingDataProperties, "IceSupportingDataProperties should be injected");

        assertDoesNotThrow(() ->
        {
            try
            {
                new ICESupportingDataConfiguration("org.cdsframework^ICE^1.0.0", Collections.singletonList("org.nyc.cir^ICE^1.0.0"),
                        iceSupportingDataProperties);
            }
            catch (Exception e)
            {
                // Print detailed error for debugging
                Throwable root = e;
                int depth = 0;
                StringBuilder sb = new StringBuilder();
                sb.append("CAUSE_CHAIN:\n");
                while (root != null && depth < 20) {
                    String line = "Cause[" + depth + "]: " + root.getClass().getName() + ": " + String.valueOf(root.getMessage());
                    System.out.println("[DEBUG_LOG] " + line);
                    sb.append(line).append('\n');
                    root = root.getCause();
                    depth++;
                }
                e.printStackTrace();
                throw new RuntimeException(sb.toString(), e);
            }
        });
    }
}
