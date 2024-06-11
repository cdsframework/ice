/*
 * Copyright 2011-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.vmr.v1_0.mappings.mappers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Specimen;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/*
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class SpecimenMapper extends EntityBaseMapper {

	private static Log logger = LogFactory.getLog(SpecimenMapper.class);
	
	/**
	 * @param source
	 * @param target
	 * @param parentId
	 * @param relationshipToParent
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @return
	 * @throws ImproperUsageException
	 * @throws InvalidDataException 
	 * @throws DataFormatException 
	 */
	public static Specimen pullIn( 
			org.opencds.vmr.v1_0.schema.Specimen source, 
			Specimen							target,
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
		if (logger.isTraceEnabled())
			logger.trace(_METHODNAME + source.getClass().getSimpleName() + ", " + source.getId());
		EntityBaseMapper.pullIn(source, target, parentId, relationshipToParent, subjectPersonId, focalPersonId, factLists);
		
		if ( source.getDescription() != null ) target.setDescription(MappingUtility.sT2STInternal(source.getDescription()));
		
		factLists.put(Specimen.class, target);
		
		// pull in nested RelatedEntities
		if ( source.getRelatedEntity() != null ) {
			NestedObjectsMapper.pullInRelatedEntityNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);
		}
		
		return target;
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
	 * 
	 * @param source
	 * @param target
	 * @param organizedResults
	 * @return target, fully populated including any nestedObjects 
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.Specimen pushOut(
			Specimen 										source, 
			org.opencds.vmr.v1_0.schema.Specimen 			target, 
			OrganizedResults								organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return null;

		if (logger.isTraceEnabled())
			logger.trace(_METHODNAME + source.getClass().getSimpleName() + ", " + source.getId());
		EntityBaseMapper.pushOut(source, target);
		
		if ( source.getDescription() != null ) target.setDescription(MappingUtility.sTInternal2ST(source.getDescription()));		
		
		//look for nested related entities
		NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);
		
		return target;
		
	}
}
