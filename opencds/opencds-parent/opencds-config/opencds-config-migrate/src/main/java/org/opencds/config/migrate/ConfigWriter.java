/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.config.migrate;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.mapper.ConceptDeterminationMethodMapper;
import org.opencds.config.mapper.ExecutionEngineMapper;
import org.opencds.config.mapper.KnowledgeModuleMapper;
import org.opencds.config.mapper.SemanticSignifierMapper;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.schema.ConceptDeterminationMethods;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.KnowledgeModules;
import org.opencds.config.schema.SemanticSignifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConfigWriter {
    private static final Log log = LogFactory.getLog(ConfigWriter.class);

    private static final String CONFIG_SCHEMA_URL = "org.opencds.config.schema";
    private static final String KM_FILE = "knowledgeModules.xml";
    private static final String EE_FILE = "executionEngines.xml";
    private static final String SS_FILE = "semanticSignifiers.xml";
    private static final String CDM_FILE = "cdm.xml";
    private static final String KP_DIR = "knowledgePackages";
    private static final String CDM_DIR = "conceptDeterminationMethods";

    public void write(Path targetPath, OpencdsCache cache) throws Exception {
        JAXBContext context = JAXBContext.newInstance(CONFIG_SCHEMA_URL);
        writeExecutionEngineConfig(context, targetPath, cache.<List<ExecutionEngine>> get(ConfigCacheRegion.METADATA, ConfigResourceType.EXECUTION_ENGINES));
        writeKnowledgeModuleConfig(context, targetPath, cache.<List<KnowledgeModule>> get(ConfigCacheRegion.METADATA, ConfigResourceType.KNOWLEDGE_MODULES));
        writeKnowledgePackages(targetPath, cache.<List<String>> get(ConfigCacheRegion.DATA, ConfigResourceType.KNOWLEDGE_PACKAGE));
        writeSemanticSignifierConfig(context, targetPath, cache.<List<SemanticSignifier>> get(ConfigCacheRegion.METADATA, ConfigResourceType.SEMANTIC_SIGNIFIERS));
        writeCDMConfig(context, targetPath, cache.<List<ConceptDeterminationMethod>> get(ConfigCacheRegion.METADATA, ConfigResourceType.CONCEPT_DETERMINATION_METHOD));
    }

    private void writeKnowledgeModuleConfig(JAXBContext context, Path targetPath, List<KnowledgeModule> list)
            throws Exception {
        KnowledgeModules kms = KnowledgeModuleMapper.external(list);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(kms, Paths.get(targetPath.toString(), KM_FILE).toFile());
    }

    private void writeKnowledgePackages(Path targetPath, List<String> kpList) {
        if (kpList != null) {
            for (String kp : kpList) {
                Path kpPath = Paths.get(kp);
                String filename = kpPath.toFile().getName();
                File source = kpPath.toFile();
                File targetDir = Paths.get(targetPath.toString(), KP_DIR).toFile();
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                    log.info("Created target dir: " + targetDir.getAbsolutePath());
                }
                File target = Paths.get(targetPath.toString(), KP_DIR, filename).toFile();
                log.info("Transferring file: " + kp + " to target: " + target.getAbsolutePath());
                try (InputStream in = new FileInputStream(source);
                        OutputStream out = new FileOutputStream(target)) {
                    int size = 2*1024;
                    byte[] buffer = new byte[size];
                    int bytesRead = 0;
                    int offset = 0;
                    while ((bytesRead = in.read(buffer, offset, size)) != -1) {
                        out.write(buffer, offset, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error reading file: kp", e);
                }
            }
        }
    }

    private void writeExecutionEngineConfig(JAXBContext context, Path targetPath, List<ExecutionEngine> eeList)
            throws Exception {
        ExecutionEngines ees = ExecutionEngineMapper.external(eeList);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(ees, Paths.get(targetPath.toString(), EE_FILE).toFile());
    }

    private void writeSemanticSignifierConfig(JAXBContext context, Path targetPath, List<SemanticSignifier> list)
            throws Exception {
        SemanticSignifiers sses = SemanticSignifierMapper.external(list);
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(sses, Paths.get(targetPath.toString(), SS_FILE).toFile());
    }

    private void writeCDMConfig(JAXBContext context, Path targetPath, List<ConceptDeterminationMethod> list)
            throws Exception {
        ConceptDeterminationMethods cdms = ConceptDeterminationMethodMapper.external(list);
        Marshaller marshaller = context.createMarshaller();
        File targetDir = Paths.get(targetPath.toString(), CDM_DIR).toFile();
        targetDir.mkdirs();
        log.info("Created target dir: " + targetDir.getAbsolutePath());
        marshaller.marshal(cdms, Paths.get(targetPath.toString(), CDM_DIR, CDM_FILE).toFile());
    }

}
