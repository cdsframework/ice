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
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class AdministrableSubstanceMapper extends EntityBaseMapper {
	
	private static Log logger = LogFactory.getLog(AdministrableSubstanceMapper.class); 

	public static AdministrableSubstance pullIn( 
			org.opencds.vmr.v1_0.schema.AdministrableSubstance external, 
			AdministrableSubstance				internal,
			String								parentId, 
			org.opencds.vmr.v1_0.schema.CD		relationshipToParent,
			String								subjectPersonId,
			String								focalPersonId,
			FactLists							factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {	
		
		String _METHODNAME = "pullIn(): ";		
		logger.trace(_METHODNAME + external.getClass().getSimpleName() + ", " + MappingUtility.iI2FlatId(external.getId()) + ", " + parentId);
		
		EntityBaseMapper.pullIn(external, internal, parentId, relationshipToParent, subjectPersonId, focalPersonId, factLists);
		
		if ( external.getSubstanceCode() != null ) internal.setSubstanceCode(MappingUtility.cD2CDInternal(external.getSubstanceCode()));
		if ( external.getStrength() != null ) internal.setStrength(MappingUtility.rTO2RTOInternal(external.getStrength()));
		if ( external.getForm() != null ) internal.setForm(MappingUtility.cD2CDInternal(external.getForm()));
		if ( external.getSubstanceBrandCode() != null ) internal.setSubstanceBrandCode(MappingUtility.cD2CDInternal(external.getSubstanceBrandCode()));
		if ( external.getSubstanceGenericCode() != null ) internal.setSubstanceGenericCode(MappingUtility.cD2CDInternal(external.getSubstanceGenericCode()));
		if ( external.getManufacturer() != null ) internal.setManufacturer(MappingUtility.cD2CDInternal(external.getManufacturer()));
		if ( external.getLotNo() != null ) internal.setLotNo(MappingUtility.sT2STInternal(external.getLotNo()));
		
		factLists.put(AdministrableSubstance.class, internal);
		
		// pull in nested RelatedEntities
		if ( external.getRelatedEntity() != null ) {
			NestedObjectsMapper.pullInRelatedEntityNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId, factLists);
		}
		
		return internal;
	}
	
		
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * 
	 * @param source
	 * @param target
	 * @param organizedResults
	 * @return target, fully populated including any nestedObjects
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.AdministrableSubstance pushOut(
			AdministrableSubstance 								source, 
			org.opencds.vmr.v1_0.schema.AdministrableSubstance 	target, 
			OrganizedResults									organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return null;
		
		logger.trace(_METHODNAME + source.getClass().getSimpleName() + ", " + source.getId());		
		EntityBaseMapper.pushOut(source, target);
		
		if ( source.getSubstanceCode() != null ) target.setSubstanceCode(MappingUtility.cDInternal2CD(source.getSubstanceCode()));
		if ( source.getStrength() != null ) target.setStrength(MappingUtility.rTOInternal2RTO(source.getStrength()));
		if ( source.getForm() != null ) target.setForm(MappingUtility.cDInternal2CD(source.getForm()));
		if ( source.getSubstanceBrandCode() != null ) target.setSubstanceBrandCode(MappingUtility.cDInternal2CD(source.getSubstanceBrandCode()));
		if ( source.getSubstanceGenericCode() != null ) target.setSubstanceGenericCode(MappingUtility.cDInternal2CD(source.getSubstanceGenericCode()));
		if ( source.getManufacturer() != null ) target.setManufacturer(MappingUtility.cDInternal2CD(source.getManufacturer()));
		if ( source.getLotNo() != null ) target.setLotNo(MappingUtility.sTInternal2ST(source.getLotNo()));
		
		//look for nested related entities
		NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);
		
		return target;
		
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * 
	 * @param source
	 * @param target
	 * @return target, fully populated including any nestedObjects
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.AdministrableSubstance pushOut(
			AdministrableSubstance 								source, 
			org.opencds.vmr.v1_0.schema.AdministrableSubstance 	target
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return null;
		
		logger.trace(_METHODNAME + source.getClass().getSimpleName() + ", " + source.getId());		
		EntityBaseMapper.pushOut(source, target);
		
		if ( source.getSubstanceCode() != null ) target.setSubstanceCode(MappingUtility.cDInternal2CD(source.getSubstanceCode()));
		if ( source.getStrength() != null ) target.setStrength(MappingUtility.rTOInternal2RTO(source.getStrength()));
		if ( source.getForm() != null ) target.setForm(MappingUtility.cDInternal2CD(source.getForm()));
		if ( source.getSubstanceBrandCode() != null ) target.setSubstanceBrandCode(MappingUtility.cDInternal2CD(source.getSubstanceBrandCode()));
		if ( source.getSubstanceGenericCode() != null ) target.setSubstanceGenericCode(MappingUtility.cDInternal2CD(source.getSubstanceGenericCode()));
		if ( source.getManufacturer() != null ) target.setManufacturer(MappingUtility.cDInternal2CD(source.getManufacturer()));
		if ( source.getLotNo() != null ) target.setLotNo(MappingUtility.sTInternal2ST(source.getLotNo()));
		
//		//look for nested related entities
//		NestedObjectsMapper.pushOutRelatedEntityNestedObjects(source.getId(), target, organizedResults);
		
		return target;
		
	}
}
