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
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationBase;
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
public abstract class SubstanceAdministrationBaseMapper extends ClinicalStatementMapper {

	private static Log logger = LogFactory.getLog(SubstanceAdministrationBaseMapper.class);
	
	public static void pullIn(
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationBase source, 
			SubstanceAdministrationBase								target, 
			String											subjectPersonId, 
			String											focalPersonId, 
			FactLists 										factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pullIn(): ";
		if (source == null)
			return;
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}

		ClinicalStatementMapper.pullIn(source, target, subjectPersonId, focalPersonId, factLists);
		
		if ( source.getSubstanceAdministrationGeneralPurpose() != null ) target.setSubstanceAdministrationGeneralPurpose(
				MappingUtility.cD2CDInternal(source.getSubstanceAdministrationGeneralPurpose()));
		if ( source.getSubstance() != null ) target.setSubstance(AdministrableSubstanceMapper.pullIn(source.getSubstance(), new AdministrableSubstance(), null, null, subjectPersonId, focalPersonId, factLists));
		if ( source.getDeliveryMethod() != null ) target.setDeliveryMethod(MappingUtility.cD2CDInternal(source.getDeliveryMethod()));
		if ( source.getDoseQuantity() != null ) target.setDoseQuantity(MappingUtility.iVLPQ2IVLPQInternal(source.getDoseQuantity()));
		if ( source.getDeliveryRoute() != null ) target.setDeliveryRoute(MappingUtility.cD2CDInternal(source.getDeliveryRoute()));
		if ( source.getApproachBodySite() != null ) target.setApproachBodySite(BodySiteMapper.pullIn(source.getApproachBodySite()));
		if ( source.getTargetBodySite() != null ) target.setTargetBodySite(BodySiteMapper.pullIn(source.getTargetBodySite()));
		if ( source.getDosingPeriod() != null ) target.setDosingPeriod(MappingUtility.iVLPQ2IVLPQInternal(source.getDosingPeriod()));
		if ( source.getDosingPeriodIntervalIsImportant() != null ) target.setDosingPeriodIntervalIsImportant(MappingUtility.bL2BLInternal(source.getDosingPeriodIntervalIsImportant()));
		if ( source.getDeliveryRate() != null ) target.setDeliveryRate(MappingUtility.iVLPQ2IVLPQInternal(source.getDeliveryRate()));
		if ( source.getDoseType() != null ) target.setDoseType(MappingUtility.cD2CDInternal(source.getDoseType()));

		return;
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
	 * @param source internal vMR object
	 * @param target external vMR object to be populated
	 * @param mu MappingUtility instance
	 * @throws InvalidDataException 
	 * @throws DataFormatException 
	 * @throws RuntimeException if supplied target is null
	 */
	public static void pushOut(
			SubstanceAdministrationBase 							source, 
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationBase target)
		throws ImproperUsageException, DataFormatException, InvalidDataException {
		//shouldn't this use the pushOut method with organizedResults???  I think this method is obsolete...
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return;
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		ClinicalStatementMapper.pushOut(source, target);
		
		if ( source.getSubstanceAdministrationGeneralPurpose() != null ) target.setSubstanceAdministrationGeneralPurpose(MappingUtility.cDInternal2CD(source.getSubstanceAdministrationGeneralPurpose()));
		if ( source.getSubstance() != null ) {
			target.setSubstance(new org.opencds.vmr.v1_0.schema.AdministrableSubstance());
			AdministrableSubstanceMapper.pushOut(source.getSubstance(), target.getSubstance());
		}
		if ( source.getDeliveryMethod() != null ) target.setDeliveryMethod(MappingUtility.cDInternal2CD(source.getDeliveryMethod()));
		if ( source.getDoseQuantity() != null ) target.setDoseQuantity(MappingUtility.iVLPQInternal2IVLPQ(source.getDoseQuantity()));
		if ( source.getDeliveryRoute() != null ) target.setDeliveryRoute(MappingUtility.cDInternal2CD(source.getDeliveryRoute()));
		if ( source.getApproachBodySite() != null ) target.setApproachBodySite(BodySiteMapper.pushOut(source.getApproachBodySite()));
		if ( source.getTargetBodySite() != null ) target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
		if ( source.getDosingPeriod() != null ) target.setDosingPeriod(MappingUtility.iVLPQInternal2IVLPQ(source.getDosingPeriod()));
		if ( source.getDosingPeriodIntervalIsImportant() != null ) target.setDosingPeriodIntervalIsImportant(MappingUtility.bLInternal2BL(source.getDosingPeriodIntervalIsImportant()));
		if ( source.getDeliveryRate() != null ) target.setDeliveryRate(MappingUtility.iVLPQInternal2IVLPQ(source.getDeliveryRate()));
		if ( source.getDoseType() != null ) target.setDoseType(MappingUtility.cDInternal2CD(source.getDoseType()));
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object; if supplied source parameter is null, simply returns
	 * @param source internal vMR object
	 * @param target external vMR object to be populated
	 * @param mu MappingUtility instance
	 * @throws InvalidDataException 
	 * @throws DataFormatException 
	 * @throws RuntimeException if supplied target is null
	 */
	public static void pushOut(
			SubstanceAdministrationBase 							source, 
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationBase target,
			OrganizedResults										organizedResults)
		throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOut(): ";
		
		if (source == null)
			return;
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		ClinicalStatementMapper.pushOut(source, target);
		
		if ( source.getSubstanceAdministrationGeneralPurpose() != null ) target.setSubstanceAdministrationGeneralPurpose(MappingUtility.cDInternal2CD(source.getSubstanceAdministrationGeneralPurpose()));
		if ( source.getSubstance() != null ) {
			target.setSubstance(new org.opencds.vmr.v1_0.schema.AdministrableSubstance());
			AdministrableSubstanceMapper.pushOut(source.getSubstance(), target.getSubstance(), organizedResults);
		}
		if ( source.getDeliveryMethod() != null ) target.setDeliveryMethod(MappingUtility.cDInternal2CD(source.getDeliveryMethod()));
		if ( source.getDoseQuantity() != null ) target.setDoseQuantity(MappingUtility.iVLPQInternal2IVLPQ(source.getDoseQuantity()));
		if ( source.getDeliveryRoute() != null ) target.setDeliveryRoute(MappingUtility.cDInternal2CD(source.getDeliveryRoute()));
		if ( source.getApproachBodySite() != null ) target.setApproachBodySite(BodySiteMapper.pushOut(source.getApproachBodySite()));
		if ( source.getTargetBodySite() != null ) target.setTargetBodySite(BodySiteMapper.pushOut(source.getTargetBodySite()));
		if ( source.getDosingPeriod() != null ) target.setDosingPeriod(MappingUtility.iVLPQInternal2IVLPQ(source.getDosingPeriod()));
		if ( source.getDosingPeriodIntervalIsImportant() != null ) target.setDosingPeriodIntervalIsImportant(MappingUtility.bLInternal2BL(source.getDosingPeriodIntervalIsImportant()));
		if ( source.getDeliveryRate() != null ) target.setDeliveryRate(MappingUtility.iVLPQInternal2IVLPQ(source.getDeliveryRate()));
		if ( source.getDoseType() != null ) target.setDoseType(MappingUtility.cDInternal2CD(source.getDoseType()));
	}
}
