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

package org.opencds.plugin;

import java.util.Date;

public final class SupportingData {
	private final String identifier;
	private final String kmId;
	private final String loadedByPluginId;
	private final String packageId;
	private final String packageType;
	private final SupportingDataPackageSupplier packageSupplier;
	private final Date timestamp;

	private SupportingData(String identifier, String kmId, String loadedByPluginId, String packageId,
			String packageType, Date timestamp, SupportingDataPackageSupplier supplier) {
		this.identifier = identifier;
		this.kmId = kmId;
		this.loadedByPluginId = loadedByPluginId;
		this.packageId = packageId;
		this.packageType = packageType;
		this.timestamp = timestamp;
		this.packageSupplier = supplier;
	}

	public static SupportingData create(String identifier, String kmId, String loadedByPluginId, String packageId,
			String packageType, Date timestamp, SupportingDataPackageSupplier supplier) {
		return new SupportingData(identifier, kmId, loadedByPluginId, packageId, packageType, timestamp, supplier);
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getKmId() {
		return kmId;
	}

	public String getLoadedByPluginId() {
		return loadedByPluginId;
	}

	public String getPackageId() {
		return packageId;
	}

	public String getPackageType() {
		return packageType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public SupportingDataPackage getSupportingDataPackage() {
		return packageSupplier.get();
	}

	@Override
	public String toString() {
		return "plugin.SupportingData [identifier= " + identifier + ", kmId= " + kmId + ",loadedByPluginId= "
				+ loadedByPluginId + ", packageId= " + packageId + ", packageType= " + packageType + ", timestamp= "
				+ timestamp + "]";
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
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
		SupportingData rhs = (SupportingData) obj;
		return identifier.equals(rhs.identifier);
	}

}
