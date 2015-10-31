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
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.AdverseEventBase;
import org.opencds.vmr.v1_0.internal.BodySite;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public abstract class AdverseEventBaseMapper extends ClinicalStatementMapper {

	private static Log logger = LogFactory.getLog(AdverseEventBaseMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object. If supplied source parameter is null, simply returns
	 * @param source external vMR object
	 * @param target internal vMR object that will be populated 
	 * @param mu MappingUtility instance
	 * @throws RuntimeException if supplied target is null
	 */
	public static void pullIn(
			org.opencds.vmr.v1_0.schema.AdverseEventBase 	source, 
			AdverseEventBase 								target, 
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


		try {
			ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
		} catch (ImproperUsageException u) {
			String errStr = _METHODNAME + "Caught unexpected ImproperUsageException: " + u.getMessage();
			logger.error(errStr, u);
			throw new ImproperUsageException(errStr);
		}
		
		target.setAdverseEventCode(MappingUtility.cD2CDInternal(source.getAdverseEventCode()));	//required
		if (source.getAdverseEventAgent() != null ) target.setAdverseEventAgent(MappingUtility.cD2CDInternal(source.getAdverseEventAgent()));
		if (source.getAdverseEventTime() != null ) target.setAdverseEventTime(MappingUtility.iVLTS2IVLDateInternal(source.getAdverseEventTime()));
		if (source.getDocumentationTime() != null ) target.setDocumentationTime(MappingUtility.iVLTS2IVLDateInternal(source.getDocumentationTime()));
		if (source.getAffectedBodySite() != null) {
			target.setAffectedBodySite(new ArrayList<BodySite>());
			for ( org.opencds.vmr.v1_0.schema.BodySite oneBodySite : source.getAffectedBodySite() ) {
				if ( oneBodySite != null ) target.getAffectedBodySite().add(BodySiteMapper.pullIn(oneBodySite));
			}
		}
		
		return;
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
	 * @param source internal vMR object
	 * @param target external vMR object to be populated
	 * @throws ImproperUsageException if supplied target is null
	 */
	public static void pushOut(AdverseEventBase source, org.opencds.vmr.v1_0.schema.AdverseEventBase target)
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
		
		target.setAdverseEventCode(MappingUtility.cDInternal2CD(source.getAdverseEventCode()));
		if (source.getAdverseEventAgent() != null ) target.setAdverseEventAgent(MappingUtility.cDInternal2CD(source.getAdverseEventAgent()));
		if (source.getAdverseEventTime() != null ) target.setAdverseEventTime(MappingUtility.iVLDateInternal2IVLTS(source.getAdverseEventTime()));
		if (source.getDocumentationTime() != null ) target.setDocumentationTime(MappingUtility.iVLDateInternal2IVLTS(source.getDocumentationTime()));
		if (source.getAffectedBodySite() != null) {
			for ( BodySite oneBodySite : source.getAffectedBodySite() ) {
				if ( oneBodySite != null ) target.getAffectedBodySite().add(BodySiteMapper.pushOut(oneBodySite));
			}
		}
				

	}
}
