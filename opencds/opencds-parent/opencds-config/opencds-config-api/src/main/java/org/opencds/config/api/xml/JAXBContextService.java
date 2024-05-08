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

package org.opencds.config.api.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JAXBContextService {
    private static final Log log = LogFactory.getLog(JAXBContextService.class);
    private static final JAXBContextService INSTANCE = new JAXBContextService();

    private final LoadingCache<String, JAXBContext> contextCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<>()
                    {
                        @Override
                        public JAXBContext load(String contextPath)
                        {
                            JAXBContext jaxbContext = null;
                            try {
                                log.info("Creating JAXBContext: " + contextPath);
                                jaxbContext = JAXBContext.newInstance(contextPath);
                                log.info("JAXBContext created: " + contextPath);
                            } catch (JAXBException e) {
                                log.error("Error creating a JAXBContext for: " + contextPath, e);
                            }
                            return jaxbContext;
                        }
                    }
            );

    private JAXBContextService() {
    }

    public static JAXBContextService get() {
        return INSTANCE;
    }

    public JAXBContext getJAXBContext(Class<?> clazz) {
        return getJAXBContext(clazz.getPackageName());
    }

    public JAXBContext getJAXBContext(String contextPath) {
        return contextCache.getUnchecked(contextPath);
    }

}
