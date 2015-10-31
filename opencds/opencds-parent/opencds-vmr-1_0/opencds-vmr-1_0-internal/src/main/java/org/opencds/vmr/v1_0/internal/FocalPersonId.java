/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.vmr.v1_0.internal;


/**
 * <p>Java class for FocalPersonId.
 * 
 * Exists separately from VMR.focalPersonId right now for access convenience purposes.
 * 
 * May remove later if deemed unnecessary.
 * 
 */
public class FocalPersonId {
	
	private final String id; // concatenation of id root + extension
	
	public FocalPersonId(String id) {
	    this.id = id;
	}
	
	/*
	public void pullIn( org.opencds.vmr.alpha.schema.VMR external, MappingUtility mu ) {
		//super.pullIn( external, mu );
		this.id 						= mu.iI2FlatId(			external.getFocalPersonId());
		return;
	}

	public void pushOut( org.opencds.vmr.alpha.schema.VMR external, MappingUtility mu ) {
		//super.pushOut( external, mu );
		external.setFocalPersonId(					mu.iIFlat2II(this.id) );
		return;
	}
	*/
	
	/** 
     * generated getters and setters, hash code and equals(), and toString() methods follow 
     */

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		FocalPersonId other = (FocalPersonId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FocalPersonId [id=" + id + "]";
	}
}
