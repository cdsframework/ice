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

package org.opencds.vmr.v1_0.mappings.mappers;


import java.util.ArrayList;
import java.util.List;

import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.utilities.MiscUtility;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.RelationshipToSource;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * A physical thing, group of physical things or an organization. 
 * 
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class EntityBaseMapper {
	
	// private static Log logger = LogFactory.getLog(EntityBaseMapper.class);
	
	public static void pullIn(org.opencds.vmr.v1_0.schema.EntityBase	source, 	
			EntityBase										target, 
			String											parentId, 
			org.opencds.vmr.v1_0.schema.CD					relationshipToParent,
			String											subjectPersonId, 
			String											focalPersonId,
			FactLists										factLists
	) throws ImproperUsageException {

		
		// String _METHODNAME = "pullIn(): ";
		if (source == null)
			return;
		
		if (source.getEntityType() != null) target.setEntityType( MappingUtility.cD2CDInternal(source.getEntityType()));
		
		if (source.getId() == null) {
			//generate and store a GUID in target.id if source.id is null, because the ID is required internally
			target.setId( 		MiscUtility.getIDAsString());  
		} else {
			target.setId( 		MappingUtility.iI2FlatId(source.getId()));  
		}
		if (source.getTemplateId() != null) {
			target.setTemplateId(MappingUtility.iIList2FlatIdList(source.getTemplateId()));
		}
		
		target.setEvaluatedPersonId(subjectPersonId);
//		target.setIsFocalPerson((subjectPersonId.equals(focalPersonId)));
		// should following two items be present in EntityBase???  
		//answer: yes, so where do they come from?   they are not in source...
		//answer: They have to be passed in separately.
		if ((relationshipToParent != null) && (parentId != null)) {
			RelationshipToSource relationshipToSource = new RelationshipToSource();
			List<RelationshipToSource> relationshipToSources = new ArrayList<RelationshipToSource>();
			relationshipToSource.setRelationshipToSource(MappingUtility.cD2CDInternal(relationshipToParent));
			relationshipToSource.setSourceId(parentId);
			relationshipToSources.add(relationshipToSource);
			target.setRelationshipToSources(relationshipToSources);
		}
		
		// isReturnData is always false when factList is created, gets set true manually by rules, as required...
		if ( "EvaluatedPerson".equals(source.getClass().getSimpleName()) ) {
			target.setToBeReturned(true);	//always return evaluated person (may be modified by rules, if required)
		} else {
			target.setToBeReturned(false);
		}
		
		return;
	}
	

	public static void pushOut(
			EntityBase 								source, 
			org.opencds.vmr.v1_0.schema.EntityBase	target) {

		// String _METHODNAME = "pushOut(): ";
		
		/*
		 * Note that we always return the Entity info (id plus demographics) for all Patient / OtherEvaluatedPersons
		 */
		if ( (source == null) 
				|| (( source.getRelationshipToSources() == null ) && ( !source.isToBeReturned() ) ) ) //always return entity if flagged as ReturnData
			return;

		if (source.getEntityType() != null) target.setEntityType(MappingUtility.cDInternal2CD(source.getEntityType()));
		if (source.getId() != null) target.setId(MappingUtility.iIFlat2II(source.getId()));
		if ((source.getTemplateId() != null) && (source.getTemplateId().length != 0)) {
			target.getTemplateId().addAll(MappingUtility.iIFlatList2IIList(source.getTemplateId()));
		}
		
		return ;
	}
}
