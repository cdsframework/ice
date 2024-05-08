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
