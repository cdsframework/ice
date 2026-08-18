package org.cdsframework.ice.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
class CapabilityStatementProvider
{
    private static final String RESOURCE_PATH = "fhir/capability-statement.json";

    private final String capabilityStatement;

    CapabilityStatementProvider()
    {
        final ClassPathResource resource = new ClassPathResource(RESOURCE_PATH);
        try (final InputStream inputStream = resource.getInputStream())
        {
            capabilityStatement = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (final IOException e)
        {
            throw new IllegalStateException("Unable to read the FHIR capability statement resource", e);
        }
    }

    String capabilityStatement()
    {
        return capabilityStatement;
    }
}
