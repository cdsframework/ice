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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache;
import org.opencds.common.xml.XmlEntity;
import org.opencds.config.migrate.ConfigResourceType;
import org.opencds.config.migrate.OpencdsBaseConfig;
import org.opencds.config.migrate.cache.ConfigCacheRegion;
import org.opencds.config.migrate.model.CodeSystem;
import org.opencds.config.migrate.utilities.ConfigResourceUtility;
import org.opencds.config.migrate.utilities.XmlUtility;

public class CodeSystemsConfigBuilder {
    private static final Log log = LogFactory.getLog(CodeSystemsConfigBuilder.class);

    private static final String CODE_SYSTEM = "codeSystem";
    private static final String CODE_SYSTEM_OID = "codeSystemOID";
    private static final String CODE_SYSTEM_DISPLAY_NAME = "codeSystemDisplayName";
    private static final String APELON_NAMESPACE_NAME = "apelonNamespaceName";
    private static final String IS_APELON_ONTOLOGY = "isApelonOntylog";

    private XmlUtility xmlUtility = new XmlUtility();

    private ConfigResourceUtility configResourceUtility = new ConfigResourceUtility();

    public void loadCodeSystems(OpencdsBaseConfig opencdsBaseConfig, OpencdsCache cache) {
        // read codeSystem
        XmlEntity codeSystemsRootEntity = xmlUtility.unmarshalXml(configResourceUtility.getResourceAsString(
                opencdsBaseConfig, ConfigResourceType.CODE_SYSTEMS));

        // load codeSystem arrays
        List<XmlEntity> codeSystemsList = codeSystemsRootEntity.getChildrenWithLabel(CODE_SYSTEM);
        List<CodeSystem> codeSystems = new ArrayList<>();
        for (XmlEntity codeSystem : codeSystemsList) {
            // load oid to codeSystemName map
            String oid = codeSystem.getAttributeValue(CODE_SYSTEM_OID);
            String name = codeSystem.getAttributeValue(CODE_SYSTEM_DISPLAY_NAME);
            String apelonNamespace = codeSystem.getAttributeValue(APELON_NAMESPACE_NAME);
            boolean isOntylog = "true".equals(codeSystem.getAttributeValue(IS_APELON_ONTOLOGY));
            log.debug("OpenCDSCodeSystems: " + oid + ", name: " + name);
            codeSystems.add(new CodeSystem(oid, name, apelonNamespace, isOntylog));
        }
        cache.put(ConfigCacheRegion.DATA, ConfigResourceType.CODE_SYSTEMS, codeSystems);
    }

    public void setConfigResourceUtility(ConfigResourceUtility configResourceUtility) {
        this.configResourceUtility = configResourceUtility;
    }
}
