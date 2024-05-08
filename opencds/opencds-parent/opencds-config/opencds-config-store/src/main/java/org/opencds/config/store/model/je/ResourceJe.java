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

package org.opencds.config.store.model.je;

import java.util.ArrayList;
import java.util.List;

import org.opencds.config.api.model.Resource;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class ResourceJe implements Resource {

	private String name;
	private String query;

	private ResourceJe() {
	}

	public static ResourceJe create(String name, String query) {
		ResourceJe res = new ResourceJe();
		res.name = name;
		res.query = query;
		return res;
	}
	
	public static ResourceJe create(Resource resource) {
		return create(resource.getName(), resource.getQuery());
	}
	
	public static List<Resource> create(List<Resource> resources) {
		if (resources == null) {
			return null;
		}
		List<Resource> res = new ArrayList<>();
		for (Resource resource : resources) {
			res.add(create(resource));
		}
		return res;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getQuery() {
		return query;
	}
	
}
