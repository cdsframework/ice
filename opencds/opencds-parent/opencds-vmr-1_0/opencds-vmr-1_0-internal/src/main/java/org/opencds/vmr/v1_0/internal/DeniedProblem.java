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
 * An assertion that the subject did not have the problem specified.  For example, if problemCode is diabetes and diagnosticEventTime is 2011-05-01, then an assertion was made on 2011-05-01 that the subject does not have diabetes.
 * 
 * To assert that the subject has no known problems, a DeniedProblem can be asserted with a problemCode that is the root-level code for problems or conditions.  E.g., if for a DeniedProblem, problemCode is the root-level code for problems or conditionsand diagnosticEventTime is 2011-05-01, then an assertion was made on 2011-05-01 that the subject has no known problems as of that date.
 * 
 */
public class DeniedProblem extends ProblemBase
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeniedProblem [toString()=" + super.toString() + "]";
	}

}
