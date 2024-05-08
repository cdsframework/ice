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

package org.opencds.config.mapper;

import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.impl.ConceptImpl;
import org.opencds.config.schema.NamespacedConcept;

public abstract class ConceptMapper {

	public static Concept internal(NamespacedConcept external) {
		if (external == null) {
			return null;
		}
		return ConceptImpl.create(external.getCode(), external.getCodeSystem(), external.getCodeSystemName(),
				external.getDisplayName(), external.getComment(), ValueSetMapper.internal(external.getValueSet()));
	}

	public static org.opencds.config.schema.Concept external(Concept internal) {
		if (internal == null) {
			return null;
		}
		org.opencds.config.schema.Concept external = new org.opencds.config.schema.Concept();// =
																								// new
																								// NamespacedConcept();

		external.setCode(internal.getCode());
		// external.setCodeSystem(internal.getCodeSystem());
		// external.setCodeSystemName(internal.getCodeSystemName());
		external.setDisplayName(internal.getDisplayName());
		external.setComment(internal.getComment());

		return external;
	}
}
