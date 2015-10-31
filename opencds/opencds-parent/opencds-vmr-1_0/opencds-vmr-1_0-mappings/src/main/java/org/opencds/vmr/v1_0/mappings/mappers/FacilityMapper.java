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

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Facility;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * @author u0762232
 *
 */
/**
 * @author u0762232
 *
 */
public class FacilityMapper extends EntityBaseMapper {

	private static Log logger = LogFactory.getLog(FacilityMapper.class);
	
	public static Facility pullIn( 
			org.opencds.vmr.v1_0.schema.Facility source, 
			Facility							target,
			String								parentId,
			org.opencds.vmr.v1_0.schema.CD		relationshipToParent,
			String								subjectPersonId,
			String								focalPersonId,
			FactLists							factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {	
		
		String _METHODNAME = "pullIn(): ";
		
		if (source == null) {
			return null;
		}
		
		logger.trace(_METHODNAME + source.getClass().getSimpleName() + ", " + source.getId());
		EntityBaseMapper.pullIn(source, target, parentId, relationshipToParent, subjectPersonId, focalPersonId, factLists);
		
    	if (source.getName() != null) {
    		target.setName(new ArrayList<EN>());
    		for ( org.opencds.vmr.v1_0.schema.EN oneNamePart : source.getName() ) {
    			target.getName().add(MappingUtility.eN2ENInternal(oneNamePart));
    		}
    	}
    	if (source.getAddress() != null) {
    		target.setAddress(new ArrayList<AD>());
    		for ( org.opencds.vmr.v1_0.schema.AD oneAddressPart : source.getAddress() ) {
    			target.getAddress().add(MappingUtility.aD2ADInternal(oneAddressPart));
    		}
    	}
    	if (source.getTelecom() != null) {
    		target.setTelecom(new ArrayList<TEL>());
    		for ( org.opencds.vmr.v1_0.schema.TEL oneTelecomPart : source.getTelecom() ) {
    			target.getTelecom().add(MappingUtility.tEL2TELInternal(oneTelecomPart));
    		}
    	}
    	
    	factLists.put(Facility.class, target);
    	
		// pull in nested RelatedEntities
		if ( source.getRelatedEntity() != null ) {
			NestedObjectsMapper.pullInRelatedEntityNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);
		}
		
		return target;
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, returns null
	 * 
	 * @param source
	 * @param target
	 * @param organizedResults
	 * @return target, fully populated including any nestedObjects
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.Facility pushOut(
			Facility 										source, 
			org.opencds.vmr.v1_0.schema.Facility			target, 
			OrganizedResults								organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return null;
		
		logger.trace(_METHODNAME + source.getClass().getSimpleName() + ", " + source.getId());
		EntityBaseMapper.pushOut(source, target);
		
    	if (source.getName() != null) {   		
    		for (EN oneName : source.getName()) {
    			target.getName().add(MappingUtility.eNInternal2EN(oneName));
    		}    		
    	}
    	if (source.getAddress() != null) {   		
    		for (AD oneAddress : source.getAddress()) {
    			target.getAddress().add(MappingUtility.aDInternal2AD(oneAddress));
    		}    		
    	}
    	if (source.getTelecom() != null) {   		
    		for (TEL oneTelecom : source.getTelecom()) {
    			target.getTelecom().add(MappingUtility.tELInternal2TEL(oneTelecom));
    		}    		
    	}
		
		//look for nested related entities
		NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);
		
		return target;
		
	}
}
