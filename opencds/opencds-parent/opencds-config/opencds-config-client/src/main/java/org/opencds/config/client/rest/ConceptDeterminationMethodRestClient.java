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

package org.opencds.config.client.rest;

import org.opencds.config.client.ConceptDeterminationMethodClient;
import org.opencds.config.client.rest.util.PathUtil;

public class ConceptDeterminationMethodRestClient implements ConceptDeterminationMethodClient {
    private static final String PATH = "conceptdeterminationmethods";

    private RestClient restClient;

    public ConceptDeterminationMethodRestClient(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @Override
    public <T> T getConceptDeterminationMethods(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }
    
    @Override
    public <T> void putConceptDeterminationMethods(T cdms) {
        restClient.put(PATH, cdms);
    }

    @Override
    public <T> void postConceptDeterminationMethod(T cdm) {
        restClient.post(PATH, cdm);
    }

    public <T> T getConceptDeterminationMethod(String cdmId, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, cdmId), clazz);
    }

    @Override
    public <T> void putConceptDeterminationMethod(String cdmId, T cdm) {
        restClient.put(PathUtil.buildPath(PATH, cdmId), cdm);
    }

    @Override
    public void deleteConceptDeterminationMethod(String cdmId) {
        restClient.delete(PathUtil.buildPath(PATH, cdmId));
    }

}
