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

import java.util.ArrayList;
import java.util.List;

public class Suggestion {
	private String label;
	private String uuid;
	private boolean isRecommended;
	private List<Action> actions = new ArrayList<>();

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isRecommended() {
		return isRecommended;
	}

	public void setRecommended(boolean recommended) {
		isRecommended = recommended;
	}

	public boolean addAction(Action action) {
	    return actions.add(action);
	}
	
	public List<Action> getActions() {
		return actions;
	}

	public void setCreate(List<Action> actions) {
		this.actions = actions;
	}

	@Override
	public String toString() {
		return "Suggestion [" +
				"label='" + label + '\'' +
				", uuid='" + uuid + '\'' +
				", isRecommended=" + isRecommended +
				", actions=" + actions +
				']';
	}
}
