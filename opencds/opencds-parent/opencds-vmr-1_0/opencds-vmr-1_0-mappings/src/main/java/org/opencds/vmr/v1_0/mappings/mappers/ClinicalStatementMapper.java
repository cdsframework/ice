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
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public abstract class ClinicalStatementMapper extends Object {

	private static Log logger = LogFactory.getLog(ClinicalStatementMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object; if supplied source parameter is null, simply returns
	 * 
	 * @param external
	 * @param internal
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 */
	public static void pullIn(
			org.opencds.vmr.v1_0.schema.ClinicalStatement	external, 
			ClinicalStatement								internal, 
			String											subjectPersonId, 
			String											focalPersonId, 
			FactLists 										factLists
	) throws ImproperUsageException {
		
		String _METHODNAME = "pullIn(): ";
		
		if (external == null)
			return;
		if (internal == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
        if (external.getId() == null) {
            throw new OpenCDSRuntimeException("source ID of ClinicalStatement must not be null");
        }
		if (external.getTemplateId() != null) internal.setTemplateId(MappingUtility.iIList2FlatIdList(external.getTemplateId()));
		internal.setId( MappingUtility.iI2FlatId(external.getId()));
		if ( external.getDataSourceType() != null ) internal.setDataSourceType(MappingUtility.cD2CDInternal(external.getDataSourceType()));
		internal.setEvaluatedPersonId(subjectPersonId);
		internal.setSubjectIsFocalPerson((subjectPersonId.equals(focalPersonId)));
		internal.setClinicalStatementToBeRoot(true);  //over-ridden to false by ObservationResults that are nested.
		
		// isReturnData is always false when factList is created, gets set true manually by rules, as required...
		internal.setToBeReturned(false);
		
	}
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
	 * @param source internal vMR object
	 * @param target external vMR object to be populated
	 * @throws ImproperUsageException if supplied target is null
	 */
	public static void pushOut(ClinicalStatement source, org.opencds.vmr.v1_0.schema.ClinicalStatement target)
		throws ImproperUsageException {	

		String _METHODNAME = "pushOut(): "; 
		
		if ((source == null) || ( !source.isToBeReturned() ))
			return;
		
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		if ((source.getTemplateId() != null) && (source.getTemplateId().length != 0)) {
			target.getTemplateId().addAll(MappingUtility.iIFlatList2IIList(source.getTemplateId()));
		}
		target.setId(MappingUtility.iIFlat2II(source.getId()));
		if ( source.getDataSourceType() != null) target.setDataSourceType(MappingUtility.cDInternal2CD(source.getDataSourceType()));
		if ( source.getId() != null) target.setId(MappingUtility.iIFlat2II(source.getId())); 

		// Question: How/what to do with evaluatedPersonId, isFocalPerson and IsReturnData vars?
		//			Answer:  they are all dropped, their only purpose is to make it easier to write rules.
		//			IsReturnData flags output CS that have to be mapped into the outputPayload (CDSOutput...), but is not part of the output payload.
		return;
	}
}
