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

package org.opencds.hooks.services.util;

import java.util.Map;
import java.util.stream.Collectors;

import org.opencds.config.api.model.Prefetch;
import org.opencds.config.api.model.Resource;

public class PrefetchBuilder {

	public static Map<String, String> build(Prefetch prefetch) {
		return prefetch.getResources().stream().collect(
				Collectors.<Resource, String, String>toMap((Resource r) -> r.getName(), (Resource r) -> r.getQuery()));
	}

}
