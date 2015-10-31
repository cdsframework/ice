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

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.EvaluatedPersonRelationship;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class EvaluatedPersonRelationshipMapper {

	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param external vMR object related to the EvaluatedPerson
	 * @param ii populated Identifier of the EvaluatedPerson
	 * @param mu MappingUtility instance
	 * @return populated internal vMR EvaluatedPersonRelationship; null if provided source parameter is null
	 */
    public static EvaluatedPersonRelationship pullIn(org.opencds.vmr.v1_0.schema.EntityRelationship external,
            MappingUtility mu) {

        if (external == null)
            return null;

        if (external.getSourceId() == null) {
            throw new OpenCDSRuntimeException("sourceId of EvaluatedPersonRelationship must not be null");
        }
        if (external.getTargetEntityId() == null) {
            throw new OpenCDSRuntimeException("targetEntityId of EvaluatedPersonRelationship must not be null");
        }
        String sourceId = MappingUtility.iI2FlatId(external.getSourceId());
        String targetId = MappingUtility.iI2FlatId(external.getTargetEntityId());
        if (targetId.equals(sourceId)) {
            throw new OpenCDSRuntimeException(
                    "root and extension of source and target ID of EvaluatedPersonRelationship may not be the same: source (root^extension)= "
                            + sourceId + ", target (root^extension)= " + targetId);
        }
        EvaluatedPersonRelationship internal = new EvaluatedPersonRelationship();
        internal.setId(MappingUtility.getUUIDAsII().getValue());
        internal.setSourceEntityId(sourceId);
        internal.setTargetEntityId(targetId);
        internal.setTargetRole(MappingUtility.cD2CDInternal(external.getTargetRole()));
        internal.setRelationshipTimeInterval(MappingUtility.iVLTS2IVLDateInternal(external
                .getRelationshipTimeInterval()));

        return internal;
    }
	
	/**
	 * This method needs to be called once for each instance of related EvaluatedPersons for the same EvaluatedPersons in the internal factList
	 * @param Related source internal vMR EvaluatedPersonRelationship object
	 * @return populated external vMR EntityRelationship object; null if provided source parameter is null
	 */
	public static org.opencds.vmr.v1_0.schema.EntityRelationship pushOut(EvaluatedPersonRelationship source, MappingUtility mu) {
	
		if (source == null)
			return null;
	
		org.opencds.vmr.v1_0.schema.EntityRelationship target = new org.opencds.vmr.v1_0.schema.EntityRelationship();
		target.setSourceId(MappingUtility.iIFlat2II(source.getSourceEntityId()));
		target.setTargetEntityId(MappingUtility.iIFlat2II(source.getTargetEntityId()));
		target.setTargetRole(MappingUtility.cDInternal2CD(source.getTargetRole()));
		target.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(source.getRelationshipTimeInterval()));
		
		return target;
	}
}
