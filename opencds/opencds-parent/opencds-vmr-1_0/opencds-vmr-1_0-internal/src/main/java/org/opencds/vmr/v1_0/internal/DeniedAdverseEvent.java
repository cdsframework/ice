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


/**
 * A denial that the subject has or had the specified adverse event.  E.g., if adverseEventCode is hives, adverse event agent is penicillin, 
 * and documentation time is 2011-05-01, an assertion was made on 2011-05-01 that the subject does not get hives as a reaction to penicillin.
 * 
 * Common denials of adverse events to a class of agents can be expressed as follows:
 * 
 * 	- No known allergies --> DeniedAdverseEvent with adverseEventAgentCode that is the generic root-level code for substances and 
 * adverseEventCode that its the generic root-level code for adverse events.
 * 
 * 
 * 	- No known drug allergies --> DeniedAdverseEvent with adverseEventAgentCode that is the root-level code for medications and 
 * adverseEventCode that its the generic root-level code for adverse events.
 * 
 * 
 * 	- No known food allergies --> DeniedAdverseEvent with adverseEventAgentCode that is the root-level code for food and adverseEventCode 
 * that its the generic root-level code for adverse events.
 */

public class DeniedAdverseEvent extends AdverseEventBase
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeniedAdverseEvent [toString()=" + super.toString() + "]";
	}


}
