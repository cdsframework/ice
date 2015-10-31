package org.opencds.config.cli.util

import org.opencds.config.client.rest.ConceptDeterminationMethodRestClient
import org.opencds.config.client.rest.ExecutionEngineRestClient
import org.opencds.config.client.rest.KnowledgeModuleRestClient
import org.opencds.config.client.rest.RestClient
import org.opencds.config.schema.ConceptDeterminationMethod
import org.opencds.config.schema.ConceptDeterminationMethods
import org.opencds.config.schema.ExecutionEngine
import org.opencds.config.schema.ExecutionEngines
import org.opencds.config.schema.KnowledgeModule
import org.opencds.config.schema.KnowledgeModules
import org.opencds.config.util.URIUtil;

class ClientUtil {

    def conceptDeterminationMethod = { RestClient restClient ->
        return {Map params ->
            def cdmrc = new ConceptDeterminationMethodRestClient(restClient)
            if (params.data) {
                // method is PUT
                cdmrc.putConceptDeterminationMethod(params.id, params.data)
                return true
            } else if (params.delete) {
                cdmrc.deleteConceptDeterminationMethod(params.id)
                return true
            } else if (params.id) {
                return cdmrc.getConceptDeterminationMethod(params.id, ConceptDeterminationMethod)
            }
        }
    }
    def conceptDeterminationMethods = { RestClient restClient ->
        return {Map params ->
            def cdmrc = new ConceptDeterminationMethodRestClient(restClient)
            if (params.data) {
                // method is PUT (collection)
                cdmrc.putConceptDeterminationMethods(params.data)
                return true
            } else {
                return cdmrc.getConceptDeterminationMethods(ConceptDeterminationMethods)
            }
        }
    }
    def executionEngine = { RestClient restClient ->
        def clazz = ExecutionEngine
        return {Map params ->
            def eerc = new ExecutionEngineRestClient(restClient)
            if (params.data) {
                // method is PUT
//                def eeid = 
//                eerc.putExecutionEngine(, params.data)
            }
        }
    }
    def executionEngines = { RestClient restClient ->
        def clazz = ExecutionEngines
        return new ExecutionEngineRestClient(restClient)
    }
    def knowledgeModule = { RestClient restClient ->
        def clazz = KnowledgeModule
        return new KnowledgeModuleRestClient(restClient)
    }
    def knowledgeModules = { RestClient restClient ->
        def clazz = KnowledgeModules
        return new KnowledgeModuleRestClient(restClient)
    }
    def knowledgePackage = { RestClient restClient ->
        def clazz = KnowledgeModule
        return new KnowledgeModuleRestClient(restClient)
    }
    def supportingData = { RestClient restClient ->
        return new KnowledgeModuleRestClient(restClient)
    }
    def supportingDataPackage = { RestClient restClient ->
        // supports KM Package and SD Package
        return new KnowledgeModuleRestClient(restClient)
    }
    
    /*
     * 1. Create a new client, based on the type
     * 2. curry the appropriate function
     */
    def getClient(String type) {
        this."$type"()
    }
}
