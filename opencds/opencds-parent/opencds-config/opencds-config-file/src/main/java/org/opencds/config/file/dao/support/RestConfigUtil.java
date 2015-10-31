package org.opencds.config.file.dao.support;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.mapper.ConceptDeterminationMethodMapper;
import org.opencds.config.mapper.ExecutionEngineMapper;
import org.opencds.config.mapper.KnowledgeModuleMapper;
import org.opencds.config.mapper.PluginPackageMapper;
import org.opencds.config.mapper.SemanticSignifierMapper;
import org.opencds.config.mapper.SupportingDataMapper;
import org.opencds.config.schema.ConceptDeterminationMethods;
import org.opencds.config.schema.ExecutionEngines;
import org.opencds.config.schema.KnowledgeModules;
import org.opencds.config.schema.PluginPackages;
import org.opencds.config.schema.SemanticSignifiers;
import org.opencds.config.schema.SupportingDataList;

public class RestConfigUtil {
    private static final Log log = LogFactory.getLog(RestConfigUtil.class);
    private static final String CONFIG_SCHEMA_URL = "org.opencds.config.schema";
    private JAXBContext jaxbContext;

    public RestConfigUtil() {
        try {
            this.jaxbContext = JAXBContext.newInstance(CONFIG_SCHEMA_URL);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<ConceptDeterminationMethod> unmarshalCdms(InputStream is) {
        List<ConceptDeterminationMethod> cdms = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ConceptDeterminationMethods restCdms = (ConceptDeterminationMethods) unmarshaller.unmarshal(is);
            cdms.addAll(ConceptDeterminationMethodMapper.internal(restCdms));
        } catch (Exception e) {
            log.warn("Resource is not an instance of ConceptDeterminationMethods");
            e.printStackTrace();
        }
        return cdms;
    }

    public ConceptDeterminationMethod unmarshalCdm(InputStream is) {
        ConceptDeterminationMethod cdm = null;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod> restCDM = (JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod>) unmarshaller
                    .unmarshal(is);
            cdm = ConceptDeterminationMethodMapper.internal(restCDM.getValue());
        } catch (Exception e) {
            log.warn("Resource is not an instance of ConceptDeterminationMethod");
        }
        return cdm;
    }

    public List<ExecutionEngine> unmarshalExecutionEngines(InputStream is) {
        List<ExecutionEngine> ees = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ExecutionEngines restEEs = (ExecutionEngines) unmarshaller.unmarshal(is);
            ees.addAll(ExecutionEngineMapper.internal(restEEs));
        } catch (Exception e) {
            log.warn("Resource is not an instance of ExecutionEngines");
            e.printStackTrace();
        }
        return ees;
    }

    public List<KnowledgeModule> unmarshalKnowledgeModules(InputStream is) {
        List<KnowledgeModule> kms = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            KnowledgeModules restKMs = (KnowledgeModules) unmarshaller.unmarshal(is);
            kms.addAll(KnowledgeModuleMapper.internal(restKMs));
        } catch (Exception e) {
            log.warn("Resource is not an instance of KnowledgeModule");
            e.printStackTrace();
        }
        return kms;
    }

    public List<SemanticSignifier> unmarshalSemanticSignifiers(InputStream is) {
        List<SemanticSignifier> sss = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SemanticSignifiers restSSs = (SemanticSignifiers) unmarshaller.unmarshal(is);
            sss.addAll(SemanticSignifierMapper.internal(restSSs));
        } catch (Exception e) {
            log.warn("Resource is not an instance of SemanticSignifiers");
            e.printStackTrace();
        }
        return sss;
    }

    public SupportingData unmarshalSupportingData(InputStream is) {
        SupportingData sd = null;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<org.opencds.config.schema.SupportingData> restSD = (JAXBElement<org.opencds.config.schema.SupportingData>) unmarshaller
                    .unmarshal(is);
            sd = SupportingDataMapper.internal(restSD.getValue());
        } catch (Exception e) {
            log.warn("Resource is not an instance of SupportingData");
            e.printStackTrace();
        }
        return sd;
    }

    public List<SupportingData> unmarshalSupportingDataList(InputStream is) {
        List<SupportingData> sds = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SupportingDataList sdList = (SupportingDataList) unmarshaller.unmarshal(is);
            sds.addAll(SupportingDataMapper.internal(sdList));
        } catch (Exception e) {
            log.warn("Resource is not an instance of SupportingDataList");
            e.printStackTrace();
        }
        return sds;
    }
    
    public PluginPackage unmarshalPluginPackage(InputStream is) {
        PluginPackage pluginPackage = null;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<org.opencds.config.schema.PluginPackage> ppkg = (JAXBElement<org.opencds.config.schema.PluginPackage>) unmarshaller
                    .unmarshal(is);
            pluginPackage = PluginPackageMapper.internal(ppkg.getValue());
        } catch (Exception e) {
            log.warn("Resource is not an instance of PluginPackage");
            e.printStackTrace();
        }
        return pluginPackage;
    }

    public List<PluginPackage> unmarshalPluginPackages(InputStream is) {
        List<PluginPackage> pluginPackages = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            PluginPackages ppkgs = (PluginPackages) unmarshaller.unmarshal(is);
            pluginPackages.addAll(PluginPackageMapper.internal(ppkgs));
        } catch (Exception e) {
            log.warn("Resource is not an instance of PluginPackages");
            e.printStackTrace();
        }
        return pluginPackages;
    }

}
