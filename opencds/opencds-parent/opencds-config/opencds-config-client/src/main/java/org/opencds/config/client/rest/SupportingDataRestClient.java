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

package org.opencds.config.client.rest;

import org.opencds.config.client.SupportingDataClient;
import org.opencds.config.client.rest.util.PathUtil;

public class SupportingDataRestClient implements SupportingDataClient {
    private static final String PATH = "supportingdata";
    private static final String PKG_PATH = "package";

    private RestClient restClient;
    
    public SupportingDataRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public <T> T getSupportingDataList(Class<T> clazz) {
        return restClient.get(PATH, clazz);
    }

    @Override
    public <T> T getSupportingData(String supportingDataId, Class<T> clazz) {
        return restClient.get(PathUtil.buildPath(PATH, supportingDataId), clazz);
    }

    @Override
    public <T> void putSupportingDataList(T supportingData) {
        restClient.put(PATH, supportingData);
    }
    
    @Override
    public <T> void putSupportingData(String supportingDataId, T supportingData) {
        restClient.put(PathUtil.buildPath(PATH, supportingDataId), supportingData);
    }

    @Override
    public void deleteSupportingData(String supportingDataId) {
        restClient.delete(PathUtil.buildPath(PATH, supportingDataId));
    }

    @Override
    public <T> T getSupportingDataPackage(String supportingDataId, Class<T> clazz) {
        return restClient.getBinary(PathUtil.buildPath(PATH, supportingDataId, PKG_PATH), clazz);
    }

    @Override
    public <T> void putSupportingDataPackage(String supportingDataId, T supportingDataPackage) {
        restClient.putBinary(PathUtil.buildPath(PATH, supportingDataId, PKG_PATH), supportingDataPackage);
    }

    @Override
    public void deleteSupportingDataPackage(String supportingDataId) {
        restClient.delete(PathUtil.buildPath(PATH, supportingDataId, PKG_PATH));
    }


}
