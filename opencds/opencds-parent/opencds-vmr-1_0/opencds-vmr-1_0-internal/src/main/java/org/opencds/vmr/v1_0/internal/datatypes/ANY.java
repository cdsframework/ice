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

package org.opencds.vmr.v1_0.internal.datatypes;


/**
 * <p>Java class for ANY complex type.
 * 
 */
public class ANY {

//	org.opencds.vmr.v1_0.schema.ANY anyObjectAsIs;
	
//	public ANY() {
//		
//	}
//	
//	public ANY(org.opencds.vmr.v1_0.schema.ANY schemaObject) {
//		anyObjectAsIs = schemaObject;
//	}
//
//	public org.opencds.vmr.v1_0.schema.ANY getAnyObjectAsIs() {
//		return anyObjectAsIs;
//	}
//
//	public void setAnyObjectAsIs(org.opencds.vmr.v1_0.schema.ANY anyObjectAsIs) {
//		this.anyObjectAsIs = anyObjectAsIs;
//	}
	
	protected Object any;

	/**
	 * @return the any
	 */
	public Object getAny() {
		return any;
	}

	/**
	 * @param any the any to set
	 */
	public void setAny(Object any) {
		this.any = any;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((any == null) ? 0 : any.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ANY other = (ANY) obj;
		if (any == null) {
			if (other.any != null)
				return false;
		} else if (!any.equals(other.any))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ANY [any=" + any + "]";
	}
	
}
