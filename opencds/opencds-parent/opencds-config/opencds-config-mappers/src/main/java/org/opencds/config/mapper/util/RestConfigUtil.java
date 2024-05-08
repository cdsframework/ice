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

package org.opencds.config.mapper.util;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
            log.info("Loaded resource as ConceptDeterminationMethods");
        } catch (Exception e) {
            log.warn("Resource is not an instance of ConceptDeterminationMethods");
        }
        return cdms;
    }

    public void marshalCdms(List<ConceptDeterminationMethod> cdms, OutputStream os) {
        try {
        	Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        	marshaller.marshal(ConceptDeterminationMethodMapper.external(cdms), os);
        } catch (Exception e) {
            log.warn("Resource is not an instance of ConceptDeterminationMethods");
        }
    }

    public ConceptDeterminationMethod unmarshalCdm(InputStream is) {
        ConceptDeterminationMethod cdm = null;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod> restCDM = (JAXBElement<org.opencds.config.schema.ConceptDeterminationMethod>) unmarshaller
                    .unmarshal(is);
            cdm = ConceptDeterminationMethodMapper.internal(restCDM.getValue());
            log.info("Loaded resource as ConceptDeterminationMethod");
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
            log.info("Loaded resource as ExecutionEngines");
        } catch (Exception e) {
            log.warn("Resource is not an instance of ExecutionEngines");
        }
        return ees;
    }

    public List<KnowledgeModule> unmarshalKnowledgeModules(InputStream is) {
        List<KnowledgeModule> kms = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            KnowledgeModules restKMs = (KnowledgeModules) unmarshaller.unmarshal(is);
            kms.addAll(KnowledgeModuleMapper.internal(restKMs));
            log.info("Loaded resource as KnowledgeModules");
        } catch (Exception e) {
            log.warn("Resource is not an instance of KnowledgeModules");
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
            log.info("Loaded resource as SemanticSignifiers");
        } catch (Exception e) {
            log.warn("Resource is not an instance of SemanticSignifiers");
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
            log.info("Loaded resource as SupportingData");
        } catch (Exception e) {
            log.warn("Resource is not an instance of SupportingData");
        }
        return sd;
    }

    public List<SupportingData> unmarshalSupportingDataList(InputStream is) {
        List<SupportingData> sds = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SupportingDataList sdList = (SupportingDataList) unmarshaller.unmarshal(is);
            sds.addAll(SupportingDataMapper.internal(sdList));
            log.info("Loaded resource as SupportingDataList");
        } catch (Exception e) {
            log.warn("Resource is not an instance of SupportingDataList");
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
            log.info("Loaded resource as PluginPackage");
        } catch (Exception e) {
            log.warn("Resource is not an instance of PluginPackage");
        }
        return pluginPackage;
    }

    public List<PluginPackage> unmarshalPluginPackages(InputStream is) {
        List<PluginPackage> pluginPackages = new ArrayList<>();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            PluginPackages ppkgs = (PluginPackages) unmarshaller.unmarshal(is);
            pluginPackages.addAll(PluginPackageMapper.internal(ppkgs));
            log.info("Loaded resource as PluginPackages");
        } catch (Exception e) {
            log.warn("Resource is not an instance of PluginPackages");
        }
        return pluginPackages;
    }

}
