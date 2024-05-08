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

package org.opencds.hooks.model.response;

public class Action {
	private ActionType type;
	private String description;
	/**
	 * Resource or some other kind of object
	 */
	private Object resource;

	public static Action create(ActionType actionType, String description, Object resource) {
		Action action = new Action();
		action.type = actionType;
		action.description = description;
		action.resource = resource;
		return action;
	}

	public ActionType getType() {
		return type;
	}

	public void setType(ActionType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getResource() {
		return resource;
	}

	public void setResource(Object resource) {
		this.resource = resource;
	}

	public Class<?> getResourceType() {
	    if (resource != null) {
	        return resource.getClass();
	    }
	    return null;
	}

	@Override
	public String toString() {
		return "Action [type=" + type + ", description=" + description + ", resource=" + resource + "]";
	}
}
