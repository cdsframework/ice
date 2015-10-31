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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
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
public class ObservationResultMapper extends ObservationBaseMapper {

	private static Log logger = LogFactory.getLog(ObservationResultMapper.class);

	/**
	 * @param external
	 * @param internal
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static void pullIn(
			org.opencds.vmr.v1_0.schema.ObservationResult 	external, 
			ObservationResult								internal, 
			String											subjectPersonId, 
			String											focalPersonId, 
			FactLists 										factLists
			) throws ImproperUsageException, DataFormatException, InvalidDataException {

		String _METHODNAME = "pullIn(): ";

		if (external == null)
			return;

		// Set ancestor variables
		try {
			ObservationBaseMapper.pullIn(external, internal, subjectPersonId, focalPersonId, factLists);
		}
		catch (ImproperUsageException u) {
			String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
			logger.error(errStr, u);
			throw new RuntimeException(errStr);
		}

		if ( external.getObservationEventTime() != null ) internal.setObservationEventTime(MappingUtility.iVLTS2IVLDateInternal(external.getObservationEventTime()));
		if ( external.getObservationValue() != null ) {
			internal.setObservationValue(MappingUtility.observationValue2ObservationValueInternal(external.getObservationValue()));
		}
		if ( external.getInterpretation() != null ) {
			internal.setInterpretation(new ArrayList<CD>());
			for ( org.opencds.vmr.v1_0.schema.CD oneInterpretation : external.getInterpretation() ) {
				internal.getInterpretation().add(MappingUtility.cD2CDInternal(oneInterpretation));
			}
		}

		factLists.put(ObservationResult.class, internal);
		
		// pull in nested RelatedEntities and RelatedClinicalStatements
		NestedObjectsMapper.pullInClinicalStatementNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId, factLists);
		
		return;
	}
	
		
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * 
	 * @param source
	 * @param target
	 * @param organizedResults
	 * @return
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.ObservationResult pushOut(
			ObservationResult 								source, 
			org.opencds.vmr.v1_0.schema.ObservationResult	target, 
			OrganizedResults								organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {

		String _METHODNAME = "pushOut(): ";
				
		target = new org.opencds.vmr.v1_0.schema.ObservationResult();
			
		// Set ancestor variables
		try {
			ObservationBaseMapper.pushOut(source, target);
		}
		catch (ImproperUsageException u) {
			String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
			logger.error(errStr, u);
			throw new RuntimeException(errStr);
		}
		
		if ( source.getObservationEventTime() != null ) target.setObservationEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getObservationEventTime()));
		if ( source.getObservationValue() != null ) target.setObservationValue(MappingUtility.observationValueInternal2ObservationValue(source.getObservationValue()));	
		if ( source.getInterpretation() != null ) {
			for ( CD oneInterpretation : source.getInterpretation() ) {
				target.getInterpretation().add(MappingUtility.cDInternal2CD(oneInterpretation));
			}
		}
			
		// look for and add any nested RelatedClinicalStatements and nested RelatedEntities
		target = NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);
		
		if (organizedResults.getOutput().getObservationResults() == null) {
			organizedResults.getOutput().setObservationResults(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ObservationResults());
		}
		
		return target;
	}

}
