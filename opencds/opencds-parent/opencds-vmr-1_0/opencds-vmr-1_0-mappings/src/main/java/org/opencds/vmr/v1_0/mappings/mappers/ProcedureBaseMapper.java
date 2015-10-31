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
import org.opencds.vmr.v1_0.internal.ProcedureBase;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public abstract class ProcedureBaseMapper extends ClinicalStatementMapper {

	private static Log logger = LogFactory.getLog(ProcedureBaseMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object. If supplied source parameter is null, simply returns
	 * 
	 * @param source
	 * @param target
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 */
	public static void pullIn(
			org.opencds.vmr.v1_0.schema.ProcedureBase 		source, 
			ProcedureBase	 								target, 
			String											subjectPersonId, 
			String											focalPersonId, 
			FactLists 										factLists
	) throws ImproperUsageException {
		
		String _METHODNAME = "pullIn(): ";
		if (source == null)
			return;
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}

		ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
		
		target.setProcedureCode(MappingUtility.cD2CDInternal(source.getProcedureCode()));
		if ( source.getProcedureCode() != null ) target.setProcedureMethod(MappingUtility.cD2CDInternal(source.getProcedureMethod()));
		if ( source.getApproachBodySite() != null ) target.setApproachBodySite(BodySiteMapper.pullIn(source.getApproachBodySite()));
		if ( source.getTargetBodySite() != null ) target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));
		
		return;
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
	 * @param source internal vMR object
	 * @param target external vMR object to be populated
	 * @throws ImproperUsageException  if supplied target is null
	 */
	public static void pushOut(ProcedureBase source, org.opencds.vmr.v1_0.schema.ProcedureBase target)
		throws ImproperUsageException {
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return;
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		ClinicalStatementMapper.pushOut(source, target);
		
		target.setProcedureCode(MappingUtility.cDInternal2CD(source.getProcedureCode()));
		if ( source.getProcedureCode() != null ) target.setProcedureMethod(MappingUtility.cDInternal2CD(source.getProcedureMethod()));
		if ( source.getApproachBodySite() != null ) target.setApproachBodySite(BodySiteMapper.pushOut(source.getApproachBodySite()));
		if ( source.getTargetBodySite() != null ) target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
	}
}
