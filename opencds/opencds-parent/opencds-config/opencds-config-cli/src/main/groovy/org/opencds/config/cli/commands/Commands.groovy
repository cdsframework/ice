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
        } else if (resource.type == 'supportingData') {
            return KMCommands.uploadSD.curry(IdUtil.entityid(resource.xml.kmId), resource.xml.identifier.text(), resource.input)
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
