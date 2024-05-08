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

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgePackageService {

    void deletePackage(KnowledgeModule km);

    <KP> KP getKnowledgePackage(KnowledgeModule knowledgeModule);

    /**
     * Used when a {@link org.opencds.config.api.KnowledgeLoader} requires an InputStream.
     *
     * @param knowledgeModule the {@link KnowledgeModule}
     * @return inputStream the {@link InputStream}
     */
    InputStream getPackageInputStream(KnowledgeModule knowledgeModule);

    void persistPackageInputStream(KnowledgeModule km, InputStream knowledgePackage);

    /**
     * This method is used to load all knowledge packages (where preload == true) into the cache.
     */
    void preloadKnowledgePackages(List<KnowledgeModule> knowledgeModules);

}
