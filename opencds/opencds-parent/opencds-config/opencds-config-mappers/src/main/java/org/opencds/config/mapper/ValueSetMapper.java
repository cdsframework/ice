/*
 * Copyright 2015-2020 OpenCDS.org
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

import org.opencds.config.api.model.ValueSet;
import org.opencds.config.api.model.impl.ValueSetImpl;

public abstract class ValueSetMapper {

	public static ValueSet internal(org.opencds.config.schema.ValueSet external) {
		if (external == null) {
			return null;
		}
		return ValueSetImpl.create(external.getOid(), external.getName());
	}
	
	public static org.opencds.config.schema.ValueSet external(ValueSet internal) {
		if (internal == null) {
			return null;
		}
		
		org.opencds.config.schema.ValueSet external = new org.opencds.config.schema.ValueSet();
		external.setOid(internal.getOid());
		external.setName(internal.getName());
		
		return external;
	}
}
