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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.opencds.config.api.model.Resource;
import org.opencds.config.api.model.impl.ResourceImpl;

public class ResourceMapper {

	public static List<Resource> internal(List<org.opencds.config.schema.CDSHook.Prefetch.Resource> external) {
		if (external == null) {
			return null;
		}
		return external.stream().map((org.opencds.config.schema.CDSHook.Prefetch.Resource res) -> ResourceImpl
				.create(res.getName(), res.getQuery())).collect(Collectors.toList());
	}

	public static Collection<? extends org.opencds.config.schema.CDSHook.Prefetch.Resource> external(
			List<Resource> internal) {
		if (internal == null) {
			return null;
		}
		return internal.stream().map((Resource res) -> {
			org.opencds.config.schema.CDSHook.Prefetch.Resource resource = new org.opencds.config.schema.CDSHook.Prefetch.Resource();
			resource.setName(res.getName());
			resource.setQuery(res.getQuery());
			return resource;
		}).collect(Collectors.toList());
	}

}
