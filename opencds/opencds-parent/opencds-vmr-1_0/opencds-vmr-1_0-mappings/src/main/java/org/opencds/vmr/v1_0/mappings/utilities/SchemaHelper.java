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

package org.opencds.vmr.v1_0.mappings.utilities;

public class SchemaHelper {


	public static boolean equalII(org.opencds.vmr.v1_0.schema.II i1, org.opencds.vmr.v1_0.schema.II i2) {
		
		if (i1 == null && i2 == null) {
			return true;
		}
		else if (i1 == null) {
			return false;
		}
		else if (i2 == null) {
			return false;
		}
		else {
			// Check for root equality
			boolean equalRoot = false;
			String i1Root = i1.getRoot();
			String i2Root = i2.getRoot();
			if (i1Root != null) {
				if (i1Root.equals(i2Root)) {
					equalRoot = true;
				}
			}
			else if (i2Root == null) {		// i1Root and i2Root are both null
				equalRoot = true;
			}
			
			// Check for extension equality 
			boolean equalExtension = false;
			String i1Extension = i1.getExtension();
			String i2Extension = i2.getExtension();
			if (i1Extension != null) {
				if (i1Extension.equals(i2Extension)) {
					equalExtension = true;
				}
			}
			else if (i2Extension == null) {		// i1Extension and i2Extension are both null
				equalExtension = true;
			}
			
			if (equalRoot && equalExtension)
				return true;
			else
				return false;
		}
	}
}
