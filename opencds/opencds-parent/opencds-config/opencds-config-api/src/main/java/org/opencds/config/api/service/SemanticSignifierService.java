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

import java.util.List;

import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.ss.EntryPoint;
import org.opencds.config.api.ss.ExitPoint;

public interface SemanticSignifierService {
    SemanticSignifier find(SSId ssId);
    
    List<SemanticSignifier> getAll();
    
    void persist(SemanticSignifier ss);
    
    void persist(List<SemanticSignifier> sses);
    
    void delete(SSId ssId);
    
    <T> EntryPoint<T> getEntryPoint(SSId ssId);
    
    ExitPoint getExitPoint(SSId ssId);
    
    FactListsBuilder getFactListsBuilder(SSId ssId);
    
    <T> ResultSetBuilder<T> getResultSetBuilder(SSId ssId);
}
