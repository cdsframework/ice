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

package org.opencds.config.api.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opencds.config.api.model.PrePostProcessPluginId;

public class PrePostProcessPluginIdImpl implements PrePostProcessPluginId {

	private String scopingEntityId;
	private String businessId;
	private String version;
	private List<String> supportingDataIdentifier;

	private PrePostProcessPluginIdImpl() {
	}

	public static PrePostProcessPluginId create(String scopingEntityId, String businessId, String version,
			List<String> supportingDataIdentifier) {
		PrePostProcessPluginIdImpl id = new PrePostProcessPluginIdImpl();
		id.scopingEntityId = scopingEntityId;
		id.businessId = businessId;
		id.version = version;
		id.supportingDataIdentifier = supportingDataIdentifier;
		return id;
	}

	public static PrePostProcessPluginId create(PrePostProcessPluginId pppid) {
		if (pppid == null) {
			return null;
		}
		return create(pppid.getScopingEntityId(), pppid.getBusinessId(), pppid.getVersion(),
				pppid.getSupportingDataIdentifiers());
	}

	public static List<PrePostProcessPluginId> create(List<PrePostProcessPluginId> prePostProcPlugins) {
		if (prePostProcPlugins == null) {
			return null;
		}
		List<PrePostProcessPluginId> list = new ArrayList<>();
		for (PrePostProcessPluginId id : prePostProcPlugins) {
			list.add(create(id));
		}
		return list;
	}

	@Override
	public String getScopingEntityId() {
		return scopingEntityId;
	}

	@Override
	public String getBusinessId() {
		return businessId;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public List<String> getSupportingDataIdentifiers() {
		return supportingDataIdentifier;
	}

	@Override
	public String toString() {
		return "PrePostProcessPluginIdImpl [scopingEntityId=" + scopingEntityId + ", businessId=" + businessId
				+ ", version=" + version + ", supportingDataIdentifier=" + supportingDataIdentifier + "]";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(9967, 11087).append(scopingEntityId).append(businessId).append(version).toHashCode();
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
		PrePostProcessPluginIdImpl rhs = (PrePostProcessPluginIdImpl) obj;
		return new EqualsBuilder().append(scopingEntityId, rhs.scopingEntityId).append(businessId, rhs.businessId)
				.append(version, rhs.version).isEquals();
	}

}