package org.opencds.config.cli.commands

import org.opencds.config.client.rest.ExecutionEngineRestClient
import org.opencds.config.client.rest.RestClient

class EECommands {
    
    private static ExecutionEngineRestClient getClient(RestClient restClient) {
        return new ExecutionEngineRestClient(restClient)
    }
    
    static def getCollection = {RestClient restClient ->
        def client = getClient(restClient)
        return client.getExecutionEngines(String.class)
    }
    
    static def get = {String eeId, RestClient restClient ->
        def client = getClient(restClient)
        return client.getExecutionEngine(eeId, String.class)
    }
    
    static def uploadCollection = {String coll, RestClient restClient ->
        def client = getClient(restClient)
        return client.putExecutionEngines(coll)
    }
    
    static def upload = {String eeid, String ee, RestClient restClient ->
        def client = getClient(restClient)
        return client.putExecutionEngine(eeid, ee)
    }
    
    static def delete = {String eeid, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteExecutionEngine(eeid)
    }
}
