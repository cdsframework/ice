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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class EntityRelationshipMapper {

	 private static Log logger = LogFactory.getLog(EntityRelationshipMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object, and adds it to list
	 *
	 * @param sourceId
	 * @param targetId
	 * @param targetRelationshipToSource
	 * @param relationshipTimeInterval
	 * @param factLists
	 */
	public static void pullIn(org.opencds.vmr.v1_0.schema.II sourceId, org.opencds.vmr.v1_0.schema.II targetId, org.opencds.vmr.v1_0.schema.CD targetRelationshipToSource, 
			org.opencds.vmr.v1_0.schema.IVLTS relationshipTimeInterval, FactLists factLists) {
		
		//flatten the sourceId, and call the other pullIn method below.
		pullIn(MappingUtility.iI2FlatId(sourceId), targetId, targetRelationshipToSource, relationshipTimeInterval, factLists);
		return;
	}
	

	/**
	 * Populate internal vMR object from corresponding external vMR object, and adds it to list
	 *
	 * @param sourceId
	 * @param targetId
	 * @param targetRelationshipToSource
	 * @param relationshipTimeInterval
	 * @param factLists
	 */
    public static void pullIn(String sourceId, org.opencds.vmr.v1_0.schema.II targetId,
            org.opencds.vmr.v1_0.schema.CD targetRelationshipToSource,
            org.opencds.vmr.v1_0.schema.IVLTS relationshipTimeInterval, FactLists factLists) {
        if (sourceId == null) {
            throw new OpenCDSRuntimeException("sourceId of EntityRelationship must not be null");
        }
        if (targetId == null) {
            throw new OpenCDSRuntimeException("targetEntityId of EntityRelationship must not be null");
        }
        String targetIdString = MappingUtility.iI2FlatId(targetId);
        //logger.debug("sourceId: " + sourceId + ", targetId= " + targetIdString);
        if (targetIdString.equals(sourceId)) {
            throw new OpenCDSRuntimeException(
                    "root and/or extension of source and target IDs of EntityRelationship may not be the same: source (root^extension)= "
                            + sourceId + ", target (root^extension)= " + targetIdString);
        }
        EntityRelationship target = new EntityRelationship();
        target.setId(MappingUtility.getUUIDAsII().getValue());
        target.setSourceId(sourceId);
        target.setTargetEntityId(targetIdString);
        target.setTargetRole(MappingUtility.cD2CDInternal(targetRelationshipToSource));
        target.setRelationshipTimeInterval(MappingUtility.iVLTS2IVLDateInternal(relationshipTimeInterval));
        factLists.put(EntityRelationship.class, target);
        return;
    }	

	/**
	 * Populate external vMR object from corresponding internal vMR object.  In this case,
	 * 		the external object is a RelatedEntity, so it has to be created from the internal
	 * 		EntityRelationship...
	 * 
	 * @param source internal vMR object
	 * @param mu MappingUtility instance
	 * @return populated external vMR object; null if provided source parameter is null
	 */
	public static org.opencds.vmr.v1_0.schema.RelatedEntity pushOut(EntityRelationship source) {
		
		// String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return null;

		org.opencds.vmr.v1_0.schema.RelatedEntity target = new org.opencds.vmr.v1_0.schema.RelatedEntity();
		
		target.setTargetRole(MappingUtility.cDInternal2CD(source.getTargetRole()));
		target.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(source.getRelationshipTimeInterval()));
		return target;
	}
}
