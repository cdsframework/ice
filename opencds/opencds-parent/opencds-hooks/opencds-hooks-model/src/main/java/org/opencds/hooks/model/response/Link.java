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

import java.net.URL;

public class Link {
	private String label;
	private URL url;
	private LinkType type;
	private String appContext;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public LinkType getType() {
		return type;
	}

	public void setType(LinkType type) {
		this.type = type;
	}

	public String getAppContext() {
		return appContext;
	}

	public void setAppContext(String appContext) {
		this.appContext = appContext;
	}

	@Override
	public String toString() {
		return "Link [label=" + label + ", url=" + url + ", type=" + type + ", appContext=" + appContext + "]";
	}

}
