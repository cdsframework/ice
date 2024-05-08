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
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.util.URIUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class KieScannerKnowledgeLoader implements KnowledgeLoader<InputStream, KieContainer> {
    private static final Log log = LogFactory.getLog(KieScannerKnowledgeLoader.class);
    private static final String MAVEN = "MAVEN";
    private static final String MAVEN_SELF_CONTAINED_JAR = "MAVEN_SELF_CONTAINED_JAR";

    // TODO remove hardcoded values
    private final String repositoryLocation = "local"; // remote or local
    private final String url = "";
    private final String user = "";
    private final String pass = "";

    @Override
    public KieContainer loadKnowledgePackage(KnowledgeModule knowledgeModule,
                                             Function<KnowledgeModule, InputStream> inputFunction) {
        String kmidString = knowledgeModule.getPackageId();
        KMId kmId = URIUtil.getKMId(kmidString);
        KieServices ks = KieServices.Factory.get();
        String pkgType = knowledgeModule.getPackageType();

        if (MAVEN.equalsIgnoreCase(pkgType)) {
            if (repositoryLocation.equals("remote")) {
                KieRepository kr = ks.getRepository();
                UrlResource urlResource = (UrlResource) ks.getResources().newUrlResource(url);
                urlResource.setUsername(user);
                urlResource.setPassword(pass);
                urlResource.setBasicAuthentication("enabled");
                try (InputStream is = urlResource.getInputStream()) {
                    KieModule kModule = kr.addKieModule(ks.getResources().newInputStreamResource(is));
                    log.info("KieModule retrieved from remote KIE WorkBench: " + kModule);
                    KieContainer kieContainer = ks
                            .newKieContainer(new ReleaseIdImpl(kmId.getScopingEntityId(), kmId.getBusinessId(), "LATEST"));
                    KieScanner kScan = ks.newKieScanner(kieContainer);
                    kScan.scanNow();
                    return kieContainer;
                } catch (IOException e) {
                    throw new OpenCDSRuntimeException("Could not retrieve KieModule from remote KIE WorkBench; URL= " + url
                            + ", scopingEntity= " + kmId.getScopingEntityId() + ", businessId= " + kmId.getBusinessId()
                            + ", version= " + kmId.getVersion());
                }
            } else if (repositoryLocation.equals("local")) {
                ReleaseId releaseId = ks.newReleaseId(kmId.getScopingEntityId(), kmId.getBusinessId(), kmId.getVersion());
                KieContainer kieContainer = ks.newKieContainer(releaseId);
                KieScanner kScan = ks.newKieScanner(kieContainer);
                kScan.scanNow();
                return kieContainer;
            } else {
                throw new OpenCDSRuntimeException("Unsupported repositoryLocation for KM: " + knowledgeModule.getPackageId());
            }
        } else if (MAVEN_SELF_CONTAINED_JAR.equalsIgnoreCase(pkgType)) {
            InputStream inputStream = inputFunction.apply(knowledgeModule);
            if (inputStream != null) {
                Resource resource = ks.getResources().newInputStreamResource(inputStream);
                KieRepository kr = ks.getRepository();
                KieModule kieModule = kr.addKieModule(resource); //add to the KIE repo
                KieContainer kieContainer = ks.newKieContainer(kieModule.getReleaseId());
                log.debug("Loaded KnowledgeModule package; kmId= " + knowledgeModule.getKMId());
                return kieContainer;
            } else {
                throw new OpenCDSRuntimeException(
                        "KnowledgeModule package cannot be found (possibly due to misconfiguration?); packageId= "
                                + knowledgeModule.getPackageId() + ", packageType= " + knowledgeModule.getPackageType());
            }
        } else {
            throw new OpenCDSRuntimeException("Unsupported package type for KM: " + knowledgeModule.getPackageId());
        }
    }
}
