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

package org.opencds.config.api.service;

import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgeModuleService {
    KnowledgeModule find(KMId identifier);
    
    KnowledgeModule find(String requestedKmId);
    
    KnowledgeModule find(Predicate<? super KnowledgeModule> predicate);
    
    List<KnowledgeModule> getAll();
    
    List<KnowledgeModule> getAll(Predicate<? super KnowledgeModule> predicate);
    
    void persist(KnowledgeModule km);
    
    void persist(List<KnowledgeModule> kms);
    
    void delete(KMId identifier);

    InputStream getKnowledgePackage(KMId kmId);

    void persistKnowledgePackage(KMId kmId, InputStream knowledgePackage);

    void deleteKnowledgePackage(KMId kmId);
}
