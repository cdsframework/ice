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

import java.util.List;
import java.util.stream.Collectors;

import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.hooks.model.discovery.Services;

public class ServicesBuilder {

	public static Services build(List<KnowledgeModule> kms) {
		Services services = new Services();
		services.setServices(
				kms.stream().map(km -> ServiceBuilder.build(km.getCDSHook())).collect(Collectors.toList()));
		return services;
	}
}
