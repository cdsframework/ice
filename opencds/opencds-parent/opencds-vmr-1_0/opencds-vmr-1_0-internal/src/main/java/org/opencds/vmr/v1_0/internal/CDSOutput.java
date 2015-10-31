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
 */

package org.opencds.vmr.v1_0.internal;

import org.opencds.vmr.v1_0.internal.datatypes.ANY;


/**
 * The parent class containing the data used by a CDS system to communicate inferences.  Can use the vMR data structure or a 
 * base data type to communicate the results.
 */

public class CDSOutput {

    protected VMR vmrOutput;
    protected ANY simpleOutput;

	/**
	 * @return the vmrOutput
	 */
	public VMR getVmrOutput() {
		return vmrOutput;
	}

	/**
	 * @param vmrOutput the vmrOutput to set
	 */
	public void setVmrOutput(VMR vmrOutput) {
		this.vmrOutput = vmrOutput;
	}

	/**
	 * @return the simpleOutput
	 */
	public ANY getSimpleOutput() {
		return simpleOutput;
	}

	/**
	 * @param simpleOutput the simpleOutput to set
	 */
	public void setSimpleOutput(ANY simpleOutput) {
		this.simpleOutput = simpleOutput;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((simpleOutput == null) ? 0 : simpleOutput.hashCode());
		result = prime * result
				+ ((vmrOutput == null) ? 0 : vmrOutput.hashCode());
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
		CDSOutput other = (CDSOutput) obj;
		if (simpleOutput == null) {
			if (other.simpleOutput != null)
				return false;
		} else if (!simpleOutput.equals(other.simpleOutput))
			return false;
		if (vmrOutput == null) {
			if (other.vmrOutput != null)
				return false;
		} else if (!vmrOutput.equals(other.vmrOutput))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CDSOutput [vmrOutput=" + vmrOutput + ", simpleOutput="
				+ simpleOutput + "]";
	}

}
