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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.VMR;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class VMRMapper {

	// private static Log logger = LogFactory.getLog(VMRMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param source external vMR object
	 * @param mu MappingUtility instance
	 * @return populated internal vMR structure; null if provided source parameter is null
	 */
	public static VMR pullIn(org.opencds.vmr.v1_0.schema.VMR source, VMR target, MappingUtility mu) {
		
		// String _METHODNAME = "pullIn(): ";
		if (source == null)
			return null;
		
//		VMR target = new VMR();
//		target.setPatient(new EvaluatedPersonMapper().pullIn(source.getPatient(), mu));
//		for (int i=0; i < source.getTemplateId().size(); i++) {
//			target.getTemplateId().add(MappingUtility.iI2Root("templateId", source.getTemplateId().get(i)));
//		}
		List<String> templateIds = new ArrayList<String>();
		for (int i=0; i < source.getTemplateId().size(); i++) {
			templateIds.add(MappingUtility.iI2Root("templateId", source.getTemplateId().get(i)));
		}
		if (templateIds.size() > 0) target.setTemplateId(templateIds);
		return target;
	}
	
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * @param source internal vMR object
	 * @param mu MappingUtility instance
	 * @return populated external vMR object; null if provided source parameter is null
	 * @throws InvalidDataException 
	 * @throws DataFormatException 
	 */
	public static void pushOut(Map<String, List<?>> results, VMR source, org.opencds.vmr.v1_0.schema.CDSOutput output, String focalPersonId) 
	throws DataFormatException, InvalidDataException, ImproperUsageException {   
		
		// String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return;

		org.opencds.vmr.v1_0.schema.VMR target = new org.opencds.vmr.v1_0.schema.VMR();	
		if (source.getTemplateId() != null) {
			for ( String oneTemplateId : source.getTemplateId() ) {
				target.getTemplateId().add(MappingUtility.iIFlat2II(oneTemplateId));
			}
			target.setPatient(new org.opencds.vmr.v1_0.schema.EvaluatedPerson());
			
//			target.getOtherEvaluatedPersons()//add here if exists...
//			target.getEvaluatedPersonRelationships().add(e)
   		} else {
   			throw new OpenCDSRuntimeException("No templateId(s) for input EvaluatedPerson found by CdsOutputResultSetBuilder.  No result will be returned.");
		}

   		if (target != null) {
			output.setVmrOutput(target);
	   		if (results.get("EvaluatedPerson") != null) {
	   			@SuppressWarnings("unchecked")
				List<EvaluatedPerson> sourceEvaluatedPersonList = (List<EvaluatedPerson>)results.get("EvaluatedPerson");
	   			for (EvaluatedPerson sourceEvaluatedPerson : sourceEvaluatedPersonList ) {
	   				String thisEvaluatedPersonId = sourceEvaluatedPerson.getId();
	   				EvaluatedPersonMapper.pushOut(results, sourceEvaluatedPerson, output, focalPersonId, thisEvaluatedPersonId);
	   			}
	   		} else {
	   			throw new OpenCDSRuntimeException("No output EvaluatedPerson found by CdsOutputResultSetBuilder.  No result will be returned.");
	   		}		
		}
			
		return;
	}
}
