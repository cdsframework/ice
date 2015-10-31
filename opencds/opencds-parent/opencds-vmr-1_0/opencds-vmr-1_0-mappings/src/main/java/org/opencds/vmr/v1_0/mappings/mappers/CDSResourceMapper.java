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

package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.vmr.v1_0.internal.CDSResource;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class CDSResourceMapper {

	/**
	 * CD cdsResourceType;
	 * Object resourceContents;
	 */
	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param source external vMR object
	 * @param mu MappingUtility instance
	 * @return populated internal vMR structure; null if provided source parameter is null
	 */
	public static void pullIn(org.opencds.vmr.v1_0.schema.CDSResource source, CDSResource target, MappingUtility mu ) {
		
		if (source == null)
			return;
		
//		CDSResource target = new CDSResource();
		target.setCdsResourceType(MappingUtility.cD2CDInternal(source.getCdsResourceType()));
		target.setResourceContents(source.getResourceContents());
		return;
	}
	
//	/**
//	 * Populate external vMR object from corresponding internal vMR object
//	 * @param source internal vMR object
//	 * @param mu MappingUtility instance
//	 * @return populated external vMR object; null if provided source parameter is null
//	 */
//	public org.opencds.vmr.v1_0.schema.CDSResource pushOut(CDSResource source, MappingUtility mu) {
//
//		if (source == null)
//			return null;
//		
//		org.opencds.vmr.v1_0.schema.CDSResource target = new org.opencds.vmr.v1_0.schema.CDSResource();
//		target.setCdsResourceType(MappingUtility.cDInternal2CD(source.getCdsResourceType()));
//		target.setResourceContents(source.getResourceContents());
//		
//		return target;
//	}
}
