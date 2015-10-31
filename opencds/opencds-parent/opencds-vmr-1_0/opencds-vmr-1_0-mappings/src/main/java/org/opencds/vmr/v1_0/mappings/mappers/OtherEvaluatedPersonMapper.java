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

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.mappings.in.FactLists;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class OtherEvaluatedPersonMapper extends EvaluatedPersonMapper {

//	private static Log logger = LogFactory.getLog(OtherEvaluatedPersonMapper.class);
	
	/**
	 * @param source
	 * @param target
	 * @param parentId
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @return
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static EvaluatedPerson pullIn( 
			org.opencds.vmr.v1_0.schema.EvaluatedPerson source, 
			EvaluatedPerson						target,
			String								parentId,
			org.opencds.vmr.v1_0.schema.CD		relationshipToParent,
			String								subjectPersonId,
			String								focalPersonId,
			FactLists							factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		// String _METHODNAME = "pullIn(): ";
		
		if (source == null) {
			return null;
		}
		
		EvaluatedPersonMapper.pullIn(source, target, parentId, relationshipToParent, subjectPersonId, focalPersonId, factLists);
		
		return target;
	}
	

	//there is no use for pushOut, since it is the same as EvaluatedPerson.pushOut() 
//	/**
//	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
//	 * @param source internal vMR object
//	 * @param mu MappingUtility instance
//	 * @return populated external vMR object; null if provided source parameter is null
//	 * @throws InvalidDataException 
//	 * @throws DataFormatException 
//	 */
////	public org.opencds.vmr.v1_0.schema.EvaluatedPerson pushOut(OtherEvaluatedPerson source, MappingUtility mu ) {
//	public static void pushOut(OtherEvaluatedPerson source, org.opencds.vmr.v1_0.schema.EvaluatedPerson target, MappingUtility mu ) 
//		throws DataFormatException, InvalidDataException {
//		
//		// String _METHODNAME = "pushOut(): ";
//		
//		if (source == null)
//			return;
////		EvaluatedPersonMapper.pushOut(source, target, mu);
////		return (org.opencds.vmr.v1_0.schema.EvaluatedPerson) super.pushOut(source,mu);
//		return;
//	}
}
