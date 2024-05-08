/*
 * Copyright 2011-2020 OpenCDS.org
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

package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.II;


/**
 * DosingSig, used in SubstanceAdministrationOrder
 * 
 */
public class DosingSigElement {

    protected II relatedObjectId;			//Note this is the id of the containing Person, Organization, Facility, etc.
    protected II dosingSigElementId;			//Note this is a GUID for this name element
	protected CD dosingSig;

	public CD getDosingSig() {
		return dosingSig;
	}

	public void setDosingSig(CD dosingSig) {
		this.dosingSig = dosingSig;
	}

	public II getRelatedObjectId() {
		return relatedObjectId;
	}

	public void setRelatedObjectId(II relatedObjectId) {
		this.relatedObjectId = relatedObjectId;
	}

	public II getDosingSigElementId() {
		return dosingSigElementId;
	}

	public void setDosingSigElementId(II dosingSigElementId) {
		this.dosingSigElementId = dosingSigElementId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dosingSig == null) ? 0 : dosingSig.hashCode());
		result = prime
				* result
				+ ((dosingSigElementId == null) ? 0 : dosingSigElementId
						.hashCode());
		result = prime * result
				+ ((relatedObjectId == null) ? 0 : relatedObjectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DosingSigElement other = (DosingSigElement) obj;
		if (dosingSig == null) {
			if (other.dosingSig != null)
				return false;
		} else if (!dosingSig.equals(other.dosingSig))
			return false;
		if (dosingSigElementId == null) {
			if (other.dosingSigElementId != null)
				return false;
		} else if (!dosingSigElementId.equals(other.dosingSigElementId))
			return false;
		if (relatedObjectId == null) {
			if (other.relatedObjectId != null)
				return false;
		} else if (!relatedObjectId.equals(other.relatedObjectId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DosingSigElement [relatedObjectId=" + relatedObjectId
				+ ", dosingSigElementId=" + dosingSigElementId + ", dosingSig="
				+ dosingSig + "]";
	}
	
}
