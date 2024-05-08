/*
 * Copyright 2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.service.drools.v7;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KnowledgeModule;

import java.io.InputStream;
import java.util.function.Function;


public class DroolsKnowledgeLoader implements KnowledgeLoader<InputStream, InternalKnowledgeBase> {
    private static final Log log = LogFactory.getLog(DroolsKnowledgeLoader.class);

    @Override
    public InternalKnowledgeBase loadKnowledgePackage(KnowledgeModule knowledgeModule,
                                                      Function<KnowledgeModule, InputStream> inputFunction) {
        InputStream inputStream = inputFunction.apply(knowledgeModule);
        if (inputStream == null) {
            throw new OpenCDSRuntimeException(
                    "KnowledgeModule package cannot be found (possibly due to misconfiguration?); packageId= "
                            + knowledgeModule.getPackageId() + ", packageType= " + knowledgeModule.getPackageType());
        }
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(
                KnowledgeBuilderFactory
                        .newKnowledgeBuilderConfiguration(null, this.getClass().getClassLoader()));
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(
                KnowledgeBaseFactory
                        .newKnowledgeBaseConfiguration(null, this.getClass().getClassLoader())
        );
        knowledgeBase.setContainerId(knowledgeModule.getKMId().toString());

        knowledgeBuilder.add(ResourceFactory.newInputStreamResource(inputStream),
                ResourceType.getResourceType(knowledgeModule.getPackageType()));

        if (knowledgeBuilder.hasErrors()) {
            throw new OpenCDSRuntimeException("KnowledgeBuilder had errors on build of: '"
                    + knowledgeModule.getKMId() + "', " + knowledgeBuilder.getErrors().toString());
        }
        if (knowledgeBuilder.getKnowledgePackages().size() == 0) {
            throw new OpenCDSRuntimeException(
                    "KnowledgeBuilder did not find any VALID '.drl', '.bpmn' or '.pkg' files for: '"
                            + knowledgeModule.getKMId() + "', " + knowledgeBuilder.getErrors().toString());
        }
        knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());
        log.debug("Loaded KnowledgeModule package; kmId= " + knowledgeModule.getKMId());
        return knowledgeBase;
    }

}
