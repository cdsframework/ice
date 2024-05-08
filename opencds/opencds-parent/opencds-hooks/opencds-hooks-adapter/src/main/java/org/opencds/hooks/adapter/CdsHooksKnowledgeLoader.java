/*
 * Copyright 2020 OpenCDS.org
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

package org.opencds.hooks.adapter;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.hooks.engine.api.CdsHooksExecutionEngine;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class CdsHooksKnowledgeLoader implements KnowledgeLoader<Void, CdsHooksExecutionEngine> {

    private static final String CLASSPATH = "classpath";

    @Override
    public CdsHooksExecutionEngine loadKnowledgePackage(KnowledgeModule knowledgeModule,
                                                        Function<KnowledgeModule, Void> inputFunction) {
        String packageId = knowledgeModule.getPackageId();

        String pkgType = knowledgeModule.getPackageType();

        if (CLASSPATH.equalsIgnoreCase(pkgType)) {
            try {
                return (CdsHooksExecutionEngine) Class.forName(packageId).getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new OpenCDSRuntimeException(e.getMessage(), e);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new OpenCDSRuntimeException("Unsupported package type for KM: " + knowledgeModule.getPackageId());
        }
    }
}
