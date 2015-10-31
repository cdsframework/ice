package org.opencds.config.migrate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.model.KMId;
import org.opencds.config.schema.ConceptDeterminationMethod;
import org.opencds.config.schema.ConceptDeterminationMethods;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.KnowledgeModules;
import org.opencds.config.schema.SemanticSignifiers;
import org.opencds.config.util.URIUtil;

public class NewConfigWriter {
    private static final Log log = LogFactory.getLog(NewConfigWriter.class);

    public void writeExecutionEngineConfig(ConfigHandler configHandler, ExecutionEngines ees) throws Exception {
        configHandler.write(ees);
    }

    public void writeCDMConfig(ConfigHandler configHandler, ConceptDeterminationMethods cdms, boolean splitCDM)
            throws Exception {
        if (splitCDM) {
            for (ConceptDeterminationMethod cdm : cdms.getConceptDeterminationMethod()) {
                configHandler.write(cdm, URIUtil.buildCDMIdString(cdm));
            }
        } else {
            configHandler.write(cdms);
        }
    }

    public void writeKnowledgeModuleConfig(ConfigHandler configHandler, KnowledgeModules kms) throws Exception {
        configHandler.write(kms);
    }

    public void writeSemanticSignifierConfig(ConfigHandler configHandler, SemanticSignifiers sses) throws Exception {
        configHandler.write(sses);
    }

    public void writeKnowledgePackages(Map<Pair<KMId, String>, ConfigHandler> configHandlers,
            List<Pair<KMId, String>> kpList) {
        if (kpList != null) {
            log.debug(kpList);
            log.debug(configHandlers.entrySet());
            for (Pair<KMId, String> kp : kpList) {
                Path kpPath = Paths.get(kp.getRight());
                log.debug("Getting config handler for KMID: " + kp.getLeft());
                ConfigHandler configHandler = configHandlers.get(kp);
                if (configHandler == null) {
                    throw new RuntimeException("Could not resolve configHandler for file: " + kp.getRight());
                }
                try (InputStream in = new FileInputStream(kpPath.toFile());) {
                    log.debug("Writing: " + kpPath.toString());
                    configHandler.write(in);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error reading file: kp", e);
                }
            }
        }
    }

}
