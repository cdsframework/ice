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

package org.opencds.config.api.strategy;

import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.cache.CacheService;

/**
 * <tt>ConfigStrategy</tt> is responsible for providing services with access to
 * the underying configuration.
 *
 * Support likely includes injecting the appropriate DAO into the service in
 * order for the service to interact with the configuration backend.
 *
 * @author phillip
 *
 */
public interface ConfigStrategy {

    boolean supports(String configType);

    boolean isReloadable();

    boolean isUpdatable();

    /**
     * Builds a new {@link KnowledgeRepository} that has access to the underlying configuration.
     *
     * @param configPath
     * @return
     */
    KnowledgeRepository getKnowledgeRepository(ConfigData configData, CacheService cacheService);

}
