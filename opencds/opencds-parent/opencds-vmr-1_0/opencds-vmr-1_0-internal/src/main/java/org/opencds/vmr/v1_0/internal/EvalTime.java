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
 * <p>Java class for EvalTime complex type.
 * 
 * 
 */
public class EvalTime {
	
	protected java.util.Date evalTimeValue;
    /** 
     * generated getters and setters, hash code and equals(), and toString() methods follow 
     */
	
	public java.util.Date getEvalTimeValue() {
		return evalTimeValue;
	}

	public void setEvalTimeValue(java.util.Date evalTimeValue) {
		this.evalTimeValue = evalTimeValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((evalTimeValue == null) ? 0 : evalTimeValue.hashCode());
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
		EvalTime other = (EvalTime) obj;
		if (evalTimeValue == null) {
			if (other.evalTimeValue != null)
				return false;
		} else if (!evalTimeValue.equals(other.evalTimeValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EvalTime [evalTimeValue=" + evalTimeValue + "]";
	}
}
