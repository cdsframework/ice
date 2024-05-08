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

import org.opencds.hooks.model.Extension;

import java.util.ArrayList;
import java.util.List;

public class Card {
	private String uuid;
	private String summary;
	private String detail;
	private Indicator indicator;
	private Source source;
	private List<Suggestion> suggestions;
	private String selectionBehavior;
	private List<OverrideReason> overrideReasons;
	private List<Link> links;
    private Extension extension;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}
	
	public boolean addSuggestion(Suggestion suggestion) {
	    if (suggestion == null) {
	        return false;
	    }
	    if (suggestions == null) {
	        suggestions = new ArrayList<>();
	    }
	    return suggestions.add(suggestion);
	}

	public List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(List<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public String getSelectionBehavior() {
		return selectionBehavior;
	}

	public void setSelectionBehavior(String selectionBehavior) {
		this.selectionBehavior = selectionBehavior;
	}

	public boolean addOverrideReason(OverrideReason overrideReason) {
		if (overrideReason == null) {
			return false;
		}
		if (overrideReasons == null) {
			overrideReasons = new ArrayList<>();
		}
		return overrideReasons.add(overrideReason);
	}

	public List<OverrideReason> getOverrideReasons() {
		return overrideReasons;
	}

	public void setOverrideReasons(List<OverrideReason> overrideReasons) {
		this.overrideReasons = overrideReasons;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	/**
	 * Currently only supports FHIR Extension
	 * 
	 * @return
	 */
	public Object getExtension() {
	    return this.extension;
	}
	
	/**
     * Currently only supports FHIR Extension
     * 
	 * @param extension
	 */
	public void setExtension(Object extension) {
	    this.extension = new Extension(extension);
	}

	@Override
	public String toString() {
		return "Card [" +
				"uuid='" + uuid + '\'' +
				", summary='" + summary + '\'' +
				", detail='" + detail + '\'' +
				", indicator=" + indicator +
				", source=" + source +
				", suggestions=" + suggestions +
				", selectionBehavior='" + selectionBehavior + '\'' +
				", overrideReasons=" + overrideReasons +
				", links=" + links +
				", extension=" + extension +
				"]";
	}
}
