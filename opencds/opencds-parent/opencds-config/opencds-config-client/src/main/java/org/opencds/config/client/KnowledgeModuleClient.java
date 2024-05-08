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

package org.opencds.config.client;


public interface KnowledgeModuleClient {
    <T> T getKnowledgeModules(Class<T> clazz);
    
    <T> void putKnowledgeModules(T knowledgeModules);

    <T> void postKnowledgeModule(T knowledgeModule);

    <T> T getKnowledgeModule(String kmId, Class<T> clazz);

    <T> void putKnowledgeModule(String kmId, T knowledgeModule);

    void deleteKnowledgeModule(String kmId);

    <T> T getKnowledgePackage(String kmId, Class<T> clazz);

    <T> void putKnowledgePackage(String kmId, T knowledgePackage);

    void deleteKnowledgePackage(String kmId);

    @Deprecated
    <T> T getSupportingData(String kmId, Class<T> clazz);

    @Deprecated
    <T> void postSupportingData(String kmId, T supportingData);

    @Deprecated
    <T> T getSupportingData(String kmId, String supportingDataId, Class<T> clazz);

    @Deprecated
    <T> void putSupportingData(String kmId, String supportingDataId, T supportingData);

    @Deprecated
    void deleteSupportingData(String kmId, String supportingDataId);

    @Deprecated
    <T> T getSupportingDataPackage(String kmId, String supportingDataId, Class<T> clazz);

    @Deprecated
    <T> void putSupportingDataPackage(String kmId, String supportingDataId, T supportingDataPackage);
    
    @Deprecated
    void deleteSupportingDataPackage(String kmId, String supportingDataId);

}
