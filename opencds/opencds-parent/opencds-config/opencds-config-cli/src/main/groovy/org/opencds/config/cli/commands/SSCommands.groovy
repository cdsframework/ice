package org.opencds.config.cli.commands

import org.opencds.config.client.rest.RestClient
import org.opencds.config.client.rest.SemanticSignifierRestClient

class SSCommands {
    
    private static SemanticSignifierRestClient getClient(RestClient restClient) {
        return new SemanticSignifierRestClient(restClient)
    }
    
    static def getCollection = {RestClient restClient ->
        def client = getClient(restClient)
        return client.getSemanticSignifiers(String.class)
    }
    
    static def get = {String ssid, RestClient restClient ->
        def client = getClient(restClient)
        return client.getSemanticSignifier(ssid, String.class)
    }
    
    static def uploadCollection = {String coll, RestClient restClient ->
        def client = getClient(restClient)
        return client.putSemanticSignifiers(coll)
    }
    
    static def upload = {String ssid, String ss, RestClient restClient ->
        def client = getClient(restClient)
        return client.putSemanticSignifier(ssid, ss)
    }
    
    static def delete = {String ssid, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteSemanticSignifier(ssid)
    }
    
}
