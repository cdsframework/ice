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

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.DoseRestriction;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class DoseRestrictionMapper {

	// private static Log logger = LogFactory.getLog(DoseRestrictionMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param source external vMR object
	 * @param mu MappingUtility instance
	 * @return populated internal vMR structure; null if provided source parameter is null
	 */
	public static DoseRestriction pullIn(
			org.opencds.vmr.v1_0.schema.DoseRestriction source, 
			DoseRestriction								target,
			String								parentId,
			String								subjectPersonId,
			String								focalPersonId,
			FactLists							factLists
	) throws ImproperUsageException {	
		
		if (source == null)
			return null;
		
//		DoseRestriction target = new DoseRestriction();
		
		target.setMaxDoseForInterval(MappingUtility.pQ2PQInternal(source.getMaxDoseForInterval()));
		target.setTimeInterval(MappingUtility.pQ2PQInternal(source.getTimeInterval()));
		return target;
	}
	
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * @param source internal vMR object
	 * @param target schema vMR object
	 * @return populated external vMR object; null if provided source parameter is null
	 */
	public static org.opencds.vmr.v1_0.schema.DoseRestriction pushOut(DoseRestriction source) {
		
		if (source == null)
			return null;
		
		org.opencds.vmr.v1_0.schema.DoseRestriction target = new org.opencds.vmr.v1_0.schema.DoseRestriction();
		target.setMaxDoseForInterval(MappingUtility.pQInternal2PQ(source.getMaxDoseForInterval()));
		target.setTimeInterval(MappingUtility.pQInternal2PQ(source.getTimeInterval()));
		
		return target;
	}
	
}
