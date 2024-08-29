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

package org.opencds.hooks.evaluation;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;

public class InputFactListsBuilder implements FactListsBuilder {

	@Override
	public Map<Class<?>, List<?>> buildFactLists(KnowledgeRepository knowledgeRepository,
			KnowledgeModule knowledgeModule, Object payload, Date evalTime) {
		
		return null;
	}

}