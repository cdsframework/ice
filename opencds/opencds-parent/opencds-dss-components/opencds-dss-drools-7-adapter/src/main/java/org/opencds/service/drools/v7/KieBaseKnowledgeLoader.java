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
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KnowledgeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.function.Function;

public class KieBaseKnowledgeLoader implements KnowledgeLoader<InputStream, KieBase> {
    private static final Log log = LogFactory.getLog(KieBaseKnowledgeLoader.class);
    private static final String KIEBASE = "KIEBASE";

    @Override
    public KieBase loadKnowledgePackage(KnowledgeModule knowledgeModule,
                                        Function<KnowledgeModule, InputStream> inputFunction) {
        KieServices ks = KieServices.Factory.get();

        String pkgType = knowledgeModule.getPackageType();

        if (KIEBASE.equalsIgnoreCase(pkgType)) {
            try (ObjectInputStream ois = new ObjectInputStream(inputFunction.apply(knowledgeModule))) {
                ks.getResources().newInputStreamResource(ois);
                KieBase kieBase = (KieBase) ois.readObject();
                log.debug("Loaded KnowledgeModule package; kmId= " + knowledgeModule.getKMId());
                return kieBase;
            } catch (IOException | ClassNotFoundException e) {
                throw new OpenCDSRuntimeException(
                        "KnowledgeModule package cannot be found or loaded (possibly due to misconfiguration?); packageId= "
                                + knowledgeModule.getPackageId() + ", packageType= " + knowledgeModule.getPackageType());
            }
        } else {
            throw new OpenCDSRuntimeException("Unsupported package type for KM: " + knowledgeModule.getPackageId());
        }
    }
}
