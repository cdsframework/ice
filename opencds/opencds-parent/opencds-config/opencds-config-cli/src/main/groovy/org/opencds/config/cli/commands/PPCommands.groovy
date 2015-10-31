package org.opencds.config.cli.commands

import org.opencds.config.client.rest.RestClient
import org.opencds.config.client.rest.PluginPackageRestClient

class PPCommands {
    
    private static PluginPackageRestClient getClient(RestClient restClient) {
        return new PluginPackageRestClient(restClient)
    }
    
    static def getCollection = {RestClient restClient ->
        def client = getClient(restClient)
        return client.getPluginPackages(String.class)
    }
    
    static def get = {String ssid, RestClient restClient ->
        def client = getClient(restClient)
        return client.getPluginPackage(ssid, String.class)
    }
    
    static def uploadCollection = {String coll, RestClient restClient ->
        def client = getClient(restClient)
        return client.putPluginPackages(coll)
    }
    
    static def upload = {String ssid, String ss, RestClient restClient ->
        def client = getClient(restClient)
        return client.putPluginPackage(ssid, ss)
    }
    
    static def delete = {String ssid, RestClient restClient ->
        def client = getClient(restClient)
        return client.deletePluginPackage(ssid)
    }
    
}
