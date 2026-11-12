package org.cdsframework.ice.service.configurations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.cdsframework.ice.service.SupportingDataService;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.model.codegen.ExecutableModelProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;

class IceKnowledgeLoaderTest
{
    private static final String RULE_PACKAGE = "knowledgeCommon.test";

    private static String ruleSource(final String ruleName)
    {
        return """
               package %s;
               rule "%s"
               when
               then
               end""".formatted(RULE_PACKAGE, ruleName);
    }

    @TempDir
    private Path outputDirectory;

    @Test
    void compilesCurrentSourcesInsteadOfPrecompiledModelInOutputDirectory() throws IOException
    {
        unpackPrecompiledModule(ruleSource("stale rule"));
        write("drools/knowledgeCommon/test/rules.drl", ruleSource("current rule").getBytes(StandardCharsets.UTF_8));

        final KieBase kieBase = loadKieBase();

        assertNotNull(kieBase.getRule(RULE_PACKAGE, "current rule"));
        assertNull(kieBase.getRule(RULE_PACKAGE, "stale rule"));
    }

    /**
     * Leave an executable-model build of the given rules in the output directory, as a precompiling Maven build would.
     */
    private void unpackPrecompiledModule(final String drl) throws IOException
    {
        final KieServices kieServices = KieServices.Factory.get();
        final KieFileSystem kieFileSystem = kieServices.newKieFileSystem().write("src/main/resources/rules.drl", drl);
        final KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll(ExecutableModelProject.class);
        assertFalse(kieBuilder.getResults().hasMessages(Message.Level.ERROR), kieBuilder.getResults().toString());

        try (final ZipInputStream module = new ZipInputStream(
                new ByteArrayInputStream(((InternalKieModule) kieBuilder.getKieModule()).getBytes())))
        {
            for (ZipEntry entry = module.getNextEntry(); entry != null; entry = module.getNextEntry())
                if (!entry.isDirectory() && !entry.getName().endsWith(".drl"))
                    write(entry.getName(), module.readAllBytes());
        }
        assertTrue(Files.exists(outputDirectory.resolve("META-INF/kie")), "precompiled model fixture missing");
    }

    private KieBase loadKieBase() throws IOException
    {
        final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try (final URLClassLoader classLoader = new URLClassLoader(new URL[] { outputDirectory.toUri().toURL() },
                originalClassLoader))
        {
            Thread.currentThread().setContextClassLoader(classLoader);
            IceKnowledgeLoader.setSupportingDataService(mock(SupportingDataService.class));

            final KnowledgeModule knowledgeModule = mock(KnowledgeModule.class);
            when(knowledgeModule.kmId()).thenReturn(new KMId("test", UUID.randomUUID().toString(), "1"));
            return new IceKnowledgeLoader().loadKnowledgePackage(knowledgeModule, null).kieBase();
        }
        finally
        {
            IceKnowledgeLoader.setSupportingDataService(null);
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private void write(final String relativePath, final byte[] content) throws IOException
    {
        final Path file = outputDirectory.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.write(file, content);
    }
}
