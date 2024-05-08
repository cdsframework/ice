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

package org.opencds.config.api.util;

import org.opencds.config.api.model.PluginId;
import org.opencds.config.api.model.PrePostProcessPluginId;

public class PluginIdTuple {

	private final PluginId pluginId;
	
	private final PrePostProcessPluginId prePostProcessPluginId;

	private PluginIdTuple(PluginId pluginId, PrePostProcessPluginId prePostProcessPluginId) {
		this.pluginId = pluginId;
		this.prePostProcessPluginId = prePostProcessPluginId;
	}

	public static PluginIdTuple create(PluginId left, PrePostProcessPluginId right) {
		return new PluginIdTuple(left, right);
	}
	
	public PluginId getLeft() {
		return pluginId;
	}

	public PrePostProcessPluginId getRight() {
		return prePostProcessPluginId;
	}

	
	
}
