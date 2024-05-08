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

import org.opencds.config.client.rest.KnowledgeModuleRestClient
import org.opencds.config.client.rest.RestClient

class KMCommands {
    private static KnowledgeModuleRestClient getClient(RestClient restClient) {
        return new KnowledgeModuleRestClient(restClient)
    }
    
    static def getCollection = {RestClient restClient ->
        def client = getClient(restClient)
        return client.getKnowledgeModules(String.class)
    }
    
    static def get = {String kmId, RestClient restClient ->
        def client = getClient(restClient)
        return client.getKnowledgeModule(kmId, String.class)
    }
    
    /**
     * Returns an InputStream
     */
    static def getPackage = {String kmId, RestClient restClient ->
        def client = getClient(restClient)
        return client.getKnowledgePackage(kmId, InputStream.class)
    }
    
    /**
     * Returns an InputStream
     */
    static def uploadCollection = {String coll, RestClient restClient ->
        def client = getClient(restClient)
        return client.putKnowledgeModules(coll)
    }
    
    static def upload = {String kmId, String km, RestClient restClient ->
        def client = getClient(restClient)
        return client.putKnowledgeModule(kmId, km)
    }
    
    static def uploadPackage = {String kmId, Closure<InputStream> input, RestClient restClient ->
        def client = getClient(restClient)
        InputStream inputStream = input()
        try {
            return client.putKnowledgePackage(kmId, inputStream)
        } finally {
            inputStream.close()
        }
    }
    
    static def delete = {String kmId, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteKnowledgeModule(kmId)
    }
    
    static def deletePackage = {String kmId, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteKnowledgePackage(kmId)
    }
    
}
