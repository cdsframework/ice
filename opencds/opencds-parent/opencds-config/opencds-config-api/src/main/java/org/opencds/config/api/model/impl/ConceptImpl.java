/*
 * Copyright 2014-2020 OpenCDS.org
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

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ValueSet;

public class ConceptImpl implements Concept {
	private String code;
	private String codeSystem;
	private String codeSystemName;
	private String displayName;
	private String comment;
	private ValueSetImpl valueSet;

	private ConceptImpl() {
	}

	public static ConceptImpl create(String code, String codeSystem, String codeSystemName, String displayName,
			String comment, ValueSet valueSet) {
		ConceptImpl ci = new ConceptImpl();
		ci.code = code;
		ci.codeSystem = codeSystem;
		ci.codeSystemName = codeSystemName;
		ci.displayName = displayName;
		ci.comment = comment;
		ci.valueSet = ValueSetImpl.create(valueSet);
		return ci;
	}

	public static ConceptImpl create(Concept concept) {
		if (concept == null) {
			return null;
		}
		if (concept instanceof ConceptImpl) {
			return ConceptImpl.class.cast(concept);
		}
		return create(concept.getCode(), concept.getCodeSystem(), concept.getCodeSystemName(), concept.getDisplayName(),
				concept.getComment(), concept.getValueSet());
	}

	public static List<Concept> create(List<Concept> fromConcepts) {
		if (fromConcepts == null) {
			return null;
		}
		List<Concept> cis = new ArrayList<>();
		for (Concept c : fromConcepts) {
			cis.add(create(c));
		}
		return cis;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getCodeSystem() {
		return codeSystem;
	}

	@Override
	public String getCodeSystemName() {
		return codeSystemName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public ValueSet getValueSet() {
		return valueSet;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(401, 677).append(code).append(codeSystem).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		ConceptImpl rhs = (ConceptImpl) obj;
		return new EqualsBuilder().append(code, rhs.code).append(codeSystem, rhs.codeSystem).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("code: " + code).append("codeSystem: " + codeSystem)
				.append("codeSystemName: " + codeSystemName).append("displayName: " + displayName).toString();
	}

}
