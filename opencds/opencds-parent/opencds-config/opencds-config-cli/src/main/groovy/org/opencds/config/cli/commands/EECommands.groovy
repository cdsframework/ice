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
