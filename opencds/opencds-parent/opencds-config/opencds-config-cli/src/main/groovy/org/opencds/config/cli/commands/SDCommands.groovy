/*
 * Copyright 2015-2020 OpenCDS.org
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
import org.opencds.config.client.rest.SupportingDataRestClient

class SDCommands {
    private static SupportingDataRestClient getClient(RestClient restClient) {
        return new SupportingDataRestClient(restClient)
    }
    
    static def getCollection = {RestClient restClient ->
        def client = getClient(restClient)
        return client.getSupportingDataList(String.class)
    }
    
    static def get = {String sdId, RestClient restClient ->
        def client = getClient(restClient)
        return client.getSupportingData(sdId, String.class)
    }
    
    /**
     * Returns an InputStream
     */
    static def getPackage = {String sdId, RestClient restClient ->
        def client = getClient(restClient)
        return client.getSupportingDataPackage(sdId, InputStream.class)
    }
    
    static def uploadCollection = {String coll, RestClient restClient ->
        def client = getClient(restClient)
        return client.putSupportingDataList(coll)
    }
    
    static def upload = {String sdId, String sd, RestClient restClient ->
        def client = getClient(restClient)
        return client.putSupportingData(sdId, sd)
    }
    
    static def uploadPackage = {String sdId, Closure<InputStream> input, RestClient restClient ->
        def client = getClient(restClient)
        InputStream inputStream = input()
        try {
            return client.putSupportingDataPackage(sdId, inputStream)
        } finally {
            inputStream.close()
        }
    }
    
    static def delete = {String sdId, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteSupportingData(sdId)
    }
    
    static def deletePackage = {String sdId, RestClient restClient ->
        def client = getClient(restClient)
        return client.deleteSupportingDataPackage(sdId)
    }
    
}
