/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.config.api;

import java.util.List;
import java.util.Map;

import org.opencds.config.api.model.KnowledgeModule;

/**
 * Interface to a class that will accept the payload from the DSS message as a
 * base64 string, and convert it into a structured JaxB element for the VMR. The
 * output of this class can then be further processed by another method to
 * produce the internal VMR structure for processing by a rules engine.
 * 
 * @author David Shields
 *
 * @version 1.0
 */
public interface FactListsBuilder {

    /**
     * @param payloadJaxB
     * @param evalTime
     * @return allFactLists
     */
    public Map<Class<?>, List<?>> buildFactLists(KnowledgeRepository knowledgeRepository,
            KnowledgeModule knowledgeModule, Object payload, java.util.Date evalTime);
}
