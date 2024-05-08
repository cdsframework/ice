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

import java.util.List;

import org.opencds.config.api.model.Prefetch;
import org.opencds.config.api.model.Resource;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class PrefetchJe implements Prefetch {

	private List<Resource> resources;

	private PrefetchJe() {}
	
	public static Prefetch create(List<Resource> resources) {
		PrefetchJe pje = new PrefetchJe();
		pje.resources = ResourceJe.create(resources);
		return pje;
	}
	
	public static Prefetch create(Prefetch prefetch) {
		if (prefetch == null) {
			return null;
		}
		return create(prefetch.getResources());
	}
	
	@Override
	public List<Resource> getResources() {
		return resources;
	}

}
