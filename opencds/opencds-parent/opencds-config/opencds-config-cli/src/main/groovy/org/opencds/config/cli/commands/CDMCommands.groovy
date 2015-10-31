package org.opencds.config.cli.commands

import org.opencds.config.client.rest.ConceptDeterminationMethodRestClient
import org.opencds.config.client.rest.RestClient

class CDMCommands {
    
    private static ConceptDeterminationMethodRestClient getClient(RestClient restClient) {
        return new ConceptDeterminationMethodRestClient(restClient)
    }
    
    static def getCollection = {RestClient restClient ->
        def client = getClient(restClient)
        return client.getConceptDeterminationMethods(String)
    }
    
    static def get = {String cdmId, RestClient restClient ->
        def client = getClient(restClient)
        return client.getConceptDeterminationMethod(cdmId, String.class)
    }
    
    static def uploadCollection = {String coll, RestClient restClient ->
        def client = getClient(restClient)
        return client.putConceptDeterminationMethods(coll)
    }
    
    static def upload = {String cdmId, String cdm, RestClient restClient ->
        def client = getClient(restClient)
        return client.putConceptDeterminationMethod(cdmId, cdm)
    }
    
    static def delete = {String cdmId, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteConceptDeterminationMethod(cdmId)
    }
}
