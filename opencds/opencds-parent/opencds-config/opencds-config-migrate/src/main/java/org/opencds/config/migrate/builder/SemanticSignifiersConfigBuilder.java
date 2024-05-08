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

package org.opencds.config.migrate.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.xml.XmlEntity;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.XSDComputableDefinition;
import org.opencds.config.api.model.impl.SSIdImpl;
import org.opencds.config.api.model.impl.SemanticSignifierImpl;
import org.opencds.config.api.model.impl.XSDComputableDefinitionImpl;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.OpencdsBaseConfig;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.model.DataModel;
import org.opencds.config.migrate.utilities.ConfigResourceUtility;
import org.opencds.config.migrate.utilities.XmlUtility;

public class SemanticSignifiersConfigBuilder {
    private static final Log log = LogFactory.getLog(SemanticSignifiersConfigBuilder.class);
    public XmlUtility xmlUtility = new XmlUtility();

    private ConfigResourceUtility configResourceUtility = new ConfigResourceUtility();

    public void loadSemanticSignifiers(OpencdsBaseConfig opencdsBaseConfig, OpencdsCache cache) {
        // read semanticSignifiers resource
        XmlEntity semanticSignifiersRootEntity = xmlUtility.unmarshalXml(configResourceUtility.getResourceAsString(
                opencdsBaseConfig, ConfigResourceType.SEMANTIC_SIGNIFIERS));

        // load SSID arrays
        List<XmlEntity> semanticSignifierList = semanticSignifiersRootEntity.getChildrenWithLabel("semanticSignifier");
        List<SemanticSignifier> signifiers = new ArrayList<>();
        for (XmlEntity semanticSignifier : semanticSignifierList) {
            // load ssid supported operations
            XmlEntity dataModelElement = semanticSignifier.getFirstChildWithLabel("dataModel");

            DataModel dataModel = DataModel.resolve(dataModelElement.getValue());

            XSDComputableDefinitionImpl computableDefinition = XSDComputableDefinitionImpl.create("CDSInput", "org.opencds.vmr.v1_0.schema", null, null);
            List<XSDComputableDefinition> computableDefinitions = new ArrayList<>();
            computableDefinitions.add(computableDefinition);

            signifiers.add(SemanticSignifierImpl.create(
                    SSIdImpl.create(dataModel.getEntityId().getScopingEntityId(), dataModel.getEntityId().getBusinessId(), dataModel.getEntityId().getVersion()),
                    dataModel.getEntityIdString(),
                    dataModel.getEntityIdString(),
                    computableDefinitions,
                    "org.opencds.vmr.v1_0.schema.CDSInput",
                    "org.opencds.vmr.v1_0.mappings.out.CdsOutputModelExitPoint",
                    "org.opencds.vmr.v1_0.mappings.in.CdsInputFactListsBuilder",
                    "org.opencds.vmr.v1_0.mappings.out.VMRSchemaResultSetBuilder",
                    new Date(),
                    System.getenv("USER")));

            log.debug("SSID(new): " + dataModel.getEntityIdString());
        }
        cache.put(ConfigCacheRegion.METADATA, ConfigResourceType.SEMANTIC_SIGNIFIERS, signifiers);
    }

}
