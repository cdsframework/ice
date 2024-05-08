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

public class Decision {
	private List<String> create = new ArrayList<>();
	private List<String> delete = new ArrayList<>();

	public List<String> getCreate() {
		return create;
	}

	public void setCreate(List<String> create) {
		this.create = create;
	}

	public List<String> getDelete() {
		return delete;
	}

	public void setDelete(List<String> delete) {
		this.delete = delete;
	}

	@Override
	public String toString() {
		return "Decision [create=" + create + ", delete=" + delete + "]";
	}
	
}
