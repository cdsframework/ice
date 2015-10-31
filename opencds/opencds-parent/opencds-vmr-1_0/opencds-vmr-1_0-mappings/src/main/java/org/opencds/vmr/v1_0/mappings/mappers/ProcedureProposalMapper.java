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
import org.opencds.vmr.v1_0.internal.ProcedureProposal;
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
public class ProcedureProposalMapper extends ProcedureBaseMapper {


	private static Log logger = LogFactory.getLog(ProcedureProposalMapper.class);
	
	/**
	 * @param source
	 * @param target
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static void pullIn(
			org.opencds.vmr.v1_0.schema.ProcedureProposal 	source, 
			ProcedureProposal								target, 
			String											subjectPersonId, 
			String											focalPersonId, 
			FactLists 										factLists
			) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pullIn(): ";
	    
		if (source == null)
			return;
		
		// Set ancestor variables
		try {
			ProcedureBaseMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
		}
		catch (ImproperUsageException u) {
			String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
			logger.error(errStr, u);
			throw new RuntimeException(errStr);
		}
		
		if ( source.getCriticality() != null ) target.setCriticality(MappingUtility.cD2CDInternal(source.getCriticality()));
		if ( source.getProposedProcedureTime() != null ) target.setProposedProcedureTime(MappingUtility.iVLTS2IVLDateInternal(source.getProposedProcedureTime()));
		if ( source.getRepeatNumber() != null ) target.setRepeatNumber(MappingUtility.iNT2INTInternal(source.getRepeatNumber()));
		
		factLists.put(ProcedureProposal.class, target);
		
		// pull in nested RelatedEntities and RelatedClinicalStatements
		NestedObjectsMapper.pullInClinicalStatementNestedObjects(source, target.getId(), subjectPersonId, focalPersonId, factLists);
		
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
	public static org.opencds.vmr.v1_0.schema.ProcedureProposal pushOut(
			ProcedureProposal 								source, 
			org.opencds.vmr.v1_0.schema.ProcedureProposal	target, 
			OrganizedResults								organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOut(): ";
		if (source == null)
			return null;
		
		target = new org.opencds.vmr.v1_0.schema.ProcedureProposal();
		
		// Set ancestor variables
		try {
			ProcedureBaseMapper.pushOut(source, target);
		}
		catch (ImproperUsageException u) {
			String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
			logger.error(errStr, u);
			throw new RuntimeException(errStr);
		}

		if ( source.getCriticality() != null ) target.setCriticality(MappingUtility.cDInternal2CD(source.getCriticality()));
		if ( source.getProposedProcedureTime() != null ) target.setProposedProcedureTime(MappingUtility.iVLDateInternal2IVLTS(source.getProposedProcedureTime()));
		if ( source.getRepeatNumber() != null ) target.setRepeatNumber(MappingUtility.iNTInternal2INT(source.getRepeatNumber()));
		
		// look for and add any nested RelatedClinicalStatements and nested RelatedEntities
		target = NestedObjectsMapper.pushOutClinicalStatementNestedObjects(source, target, organizedResults);
		
		if (organizedResults.getOutput().getProcedureProposals() == null) {
			organizedResults.getOutput().setProcedureProposals(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements.ProcedureProposals());
		}
		
		return target;
	}
}
