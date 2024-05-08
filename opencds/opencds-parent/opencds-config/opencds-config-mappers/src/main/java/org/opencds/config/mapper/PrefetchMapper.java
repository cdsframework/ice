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

package org.opencds.config.mapper;

import org.opencds.config.api.model.Prefetch;
import org.opencds.config.api.model.impl.PrefetchImpl;

public class PrefetchMapper {
	
	public static Prefetch internal(org.opencds.config.schema.CDSHook.Prefetch external) {
		if (external == null) {
			return null;
		}
		return PrefetchImpl.create(ResourceMapper.internal(external.getResource()));
	}

	public static org.opencds.config.schema.CDSHook.Prefetch external(Prefetch internal) {
		if (internal == null) {
			return null;
		}
		org.opencds.config.schema.CDSHook.Prefetch pf = new org.opencds.config.schema.CDSHook.Prefetch();
		pf.getResource().addAll(ResourceMapper.external(internal.getResources()));
		return pf;
	}

}
