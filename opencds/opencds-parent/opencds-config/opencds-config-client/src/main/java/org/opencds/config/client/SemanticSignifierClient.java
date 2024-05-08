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

public interface SemanticSignifierClient {
    <T> T getSemanticSignifiers(Class<T> clazz);
    
    <T> void putSemanticSignifiers(T semanticSignifiers);
    
    <T> void postSemanticSignifier(T semanticSignifier);
    
    <T> T getSemanticSignifier(String ssid, Class<T> clazz);
    
    <T> void putSemanticSignifier(String ssid, T semanticSignifier);
    
    void deleteSemanticSignifier(String ssid);
}
