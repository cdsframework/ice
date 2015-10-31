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

package org.opencds.dss.metadata;


import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.metadata.profile.*;
import org.opencds.service.metadata.DSSMetadataDiscoveryAdapter;


public class GenericListProfilesMetadata extends MetadataListProfiles {
	
	@Override
	public ProfilesByType  getResponse() {
		
		ProfilesByType  response = new ProfilesByType ();
		try {
			
			DSSMetadataDiscoveryAdapter myDSSMetadataDiscoveryAdapter = DSSMetadataDiscoveryAdapter.getInstance();
			response = myDSSMetadataDiscoveryAdapter.listProfiles();  
			
		//TODO convert exceptions below to DSSExceptions to return meaningful error messages
		} catch (DSSRuntimeExceptionFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		
		return response;
	}

}
