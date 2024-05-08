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

package org.opencds.hooks.model.discovery;

import org.opencds.hooks.model.Extension;

import java.util.HashMap;
import java.util.Map;

public class Service {
	private String hook;
	private String title;
	private String description;
	private String id;

	private Map<String, String> prefetch = new HashMap<>();

	private Extension extension;

	public String getHook() {
		return hook;
	}

	public void setHook(String hook) {
		this.hook = hook;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Map<String, String> getPrefetch() {
		return prefetch;
	}
	
	public void setPrefetch(Map<String, String> prefetch) {
		this.prefetch = prefetch;
	}

	public Extension getExtension() {
		return extension;
	}

	public void setExtension(Extension extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		return "Service [" +
				"hook='" + hook + '\'' +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", id='" + id + '\'' +
				", prefetch=" + prefetch +
				", extension=" + extension +
				']';
	}

}
