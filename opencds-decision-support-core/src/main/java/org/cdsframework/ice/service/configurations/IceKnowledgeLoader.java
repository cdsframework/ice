/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.service.configurations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Function;

import org.cdsframework.ice.service.SupportingDataService;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.drools.model.codegen.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IceKnowledgeLoader implements KnowledgeLoader<InputStream, IceKnowledgePackage>
{
    @Setter
    private static SupportingDataService supportingDataService;

    private static KieBase getKieBase() throws IOException
    {
        final KieServices kieServices = KieServices.Factory.get();

        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        final Resource[] droolsModelResources = resolver.getResources("classpath*:META-INF/kie/**/drools-model");
        if (droolsModelResources.length > 1)
            throw new IllegalStateException(
                    "Found %d instances of drools-model in classpath".formatted(droolsModelResources.length));

        if (droolsModelResources.length == 1 && resolver.getResources("classpath*:drools/**/*.drl").length == 0)
        {
            log.info("Found pre-compiled Drools in classpath");

            final URL url = droolsModelResources[0].getURL();
            if (ResourceUtils.isFileURL(url))
                return kieServices.newKieClasspathContainer(Thread.currentThread().getContextClassLoader()).getKieBase();

            final Resource resource = new UrlResource(ResourceUtils.extractJarFileURL(url));
            log.info("Loading ICE KnowledgeBase from jar: {}", resource);

            try (final InputStream is = resource.getInputStream())
            {
                return kieServices.newKieContainer(kieServices.getRepository()
                        .addKieModule(kieServices.getResources().newInputStreamResource(is))
                        .getReleaseId()).getKieBase();
            }
        }

        log.info("No pre-compiled Drools found in classpath");

        final Resource[] kmoduleResources = resolver.getResources("classpath*:META-INF/kmodule.xml");
        if (kmoduleResources.length != 1)
            throw new IllegalStateException("Found %d instances of kmodule.xml in classpath".formatted(kmoduleResources.length));

        log.info("Loading ICE KnowledgeBase from classpath resources");
        final String droolsPattern = "%s**/*".formatted(kmoduleResources[0].createRelative("../drools/").getURL());
        final KieFileSystem kfs = kieServices.newKieFileSystem();
        for (final Resource resource : resolver.getResources(droolsPattern))
        {
            if (resource.getFilename() == null)
                continue;

            final ResourceType resourceType = ResourceType.determineResourceType(resource.getFilename());
            if (resourceType == null)
                continue;

            final org.kie.api.io.Resource droolsResource =
                    kieServices.getResources().newByteArrayResource(resource.getContentAsByteArray());
            droolsResource.setTargetPath(
                    resolver.getPathMatcher().extractPathWithinPattern(droolsPattern, resource.getURL().toString()));
            droolsResource.setResourceType(resourceType);
            kfs.write(droolsResource);
        }

        final KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR))
            throw new RuntimeException("KieBuilder had errors: " + kieBuilder.getResults().getMessages());

        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId()).getKieBase();
    }

    @Override
    public IceKnowledgePackage loadKnowledgePackage(final KnowledgeModule knowledgeModule,
            final Function<KnowledgeModule, InputStream> knowledgeModuleInputStreamFunction)
    {
        final String _METHODNAME = "loadKnowledgePackage(): ";
        if (knowledgeModule == null)
        {
            final String lErrStr = "KnowledgeModule not supplied";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final KMId lKMId = knowledgeModule.kmId();
        if (lKMId == null)
        {
            final String lErrStr = "KMId not populated";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (lKMId.scopingEntityId() == null || lKMId.businessId() == null || lKMId.version() == null)
        {
            final String errStr = "ScopingID and/or BusinessID and/or Version not specified";
            log.error(_METHODNAME + errStr);
            throw new IllegalArgumentException(errStr);
        }

        final String lRequestedKmId =
                KnowledgeModuleUtils.returnStringRepresentationOfKnowledgeModuleName(lKMId.scopingEntityId(), lKMId.businessId(),
                        lKMId.version());

        log.info("Initializing ICE3 Drools KnowledgeBase - Knowledge Module {}", lRequestedKmId);

        if (supportingDataService == null)
        {
            final String errStr = "SupportingDataService not initialized";
            log.error(_METHODNAME + errStr);
            throw new IllegalStateException(errStr);
        }

        try
        {
            return new IceKnowledgePackage(lKMId, getKieBase());
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
