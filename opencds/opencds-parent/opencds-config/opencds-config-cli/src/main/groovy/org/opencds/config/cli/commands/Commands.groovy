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

package org.opencds.config.cli.commands

import org.opencds.config.cli.util.IdUtil

class Commands {
    static Closure resolve(Map resource) {
        if (resource.type == 'conceptDeterminationMethods') {
            return CDMCommands.uploadCollection.curry(resource.input)
        } else if (resource.type == 'conceptDeterminationMethod') {
            return CDMCommands.upload.curry(IdUtil.cdmid(resource.xml), resource.input)
        } else if (resource.type == 'executionEngines') {
            return EECommands.uploadCollection.curry(resource.input)
        } else if (resource.type == 'executionEngine') {
            return EECommands.upload.curry(resource.xml.identifier.text())
        } else if (resource.type == 'knowledgeModules') {
            return KMCommands.uploadCollection.curry(resource.input)
        } else if (resource.type == 'knowledgeModule') {
            return KMCommands.upload.curry(IdUtil.entityid(resource.xml.identifier), resource.input)
        } else if (resource.type == 'supportingDataList') {
            return SDCommands.upload.curry(resource.input)
        } else if (resource.type == 'supportingData') {
            return SDCommands.upload.curry(resource.xml.identifier.text(), resource.input)
        } else if (resource.type == 'semanticSignifiers') {
            return SSCommands.uploadCollection.curry(resource.input)
        } else if (resource.type == 'semanticSignifier') {
            return SSCommands.upload.curry(IdUtil.entityid(resource.xml.identifier), resource.input)
        } else if (resource.type == 'pluginPackages') {
            return PPCommands.uploadCollection.curry(resource.input)
        } else if (resource.type == 'pluginPackage') {
            return PPCommands.upload.curry(IdUtil.entityid(resource.xml.identifier), resource.input)
        }
    }
}
