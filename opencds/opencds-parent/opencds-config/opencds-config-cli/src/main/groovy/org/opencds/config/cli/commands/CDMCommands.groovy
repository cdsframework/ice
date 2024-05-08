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
