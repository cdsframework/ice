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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.datatypes.AD;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.EN;
import org.opencds.vmr.v1_0.internal.datatypes.TEL;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;


/**
 * A person who is the subject of evaluation by a CDS system.  May be the focal patient or some other relevant person (e.g., a relative 
 * or a sexual contact). Includes demographic attributes, clinical statements, and related entities.
 * 			
 * EntityBase-entity, clinical statement-clinical statement, and entity-clinical statement relationships may be represented through direct 
 * nesting of content and/or through the use of the relationship entities directly attached to the EvaluatedPersonMapper.
 */

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class EvaluatedPersonMapper extends EntityBaseMapper {

	private static Log logger = LogFactory.getLog(EntityMapper.class);
	
	/**
	 * @param external
	 * @param internal
	 * @param parentId
	 * @param relationshipToParent
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @return
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static EvaluatedPerson pullIn( 
			org.opencds.vmr.v1_0.schema.EvaluatedPerson external, 
			EvaluatedPerson						internal,
			String								parentId,
			org.opencds.vmr.v1_0.schema.CD		relationshipToParent,
			String								subjectPersonId,
			String								focalPersonId,
			FactLists							factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pullIn(): ";
		
    	if (external == null)
    		return null;
    	
    	EntityBaseMapper.pullIn(external, internal, parentId, relationshipToParent, subjectPersonId, focalPersonId, factLists);
    	
    	internal.setEvaluatedPersonId( subjectPersonId );
    	internal.setFocalPerson(focalPersonId.equals(subjectPersonId));
    	
		logger.trace(_METHODNAME + "class " + external.getClass().getSimpleName() + ", " + external.getId());
    	if (external.getDemographics() != null) {
    		if (internal.getDemographics() == null) {
    			internal.setDemographics(new Demographics()); 
    		}
	    	
	    	if (external.getDemographics().getBirthTime() != null) internal.getDemographics().setBirthTime(MappingUtility.tS2DateInternal(external.getDemographics().getBirthTime()));
	    	if (external.getDemographics().getGender() != null) {
	    		internal.getDemographics().setGender(MappingUtility.cD2CDInternal(external.getDemographics().getGender())); 
	    	} else {
	    		internal.getDemographics().setGender(MappingUtility.OPENCDS_NO_INFORMATION); 
	    		}
	    	if ((external.getDemographics().getRace() != null) && (external.getDemographics().getRace().size() > 0))  {
	        	internal.getDemographics().setRace(new ArrayList<CD>());
	    		for ( org.opencds.vmr.v1_0.schema.CD oneRaceCD : external.getDemographics().getRace() )  {
	    			internal.getDemographics().getRace().add(MappingUtility.cD2CDInternal(oneRaceCD));
	    		} 
	    	}
	    	if ((external.getDemographics().getEthnicity() != null) && (external.getDemographics().getEthnicity().size() > 0)) {
	        	internal.getDemographics().setEthnicity(new ArrayList<CD>());
	    		for ( org.opencds.vmr.v1_0.schema.CD oneEthnicityCD : external.getDemographics().getEthnicity() ) {
	    			internal.getDemographics().getEthnicity().add(MappingUtility.cD2CDInternal(oneEthnicityCD));
	    		}
	    	}
	    	if (external.getDemographics().getPreferredLanguage() != null) {
	    		internal.getDemographics().setPreferredLanguage(MappingUtility.cD2CDInternal(external.getDemographics().getPreferredLanguage()));
	    	}
	    	if (external.getDemographics().getAge() != null) internal.getDemographics().setAge(MappingUtility.pQ2PQInternal(external.getDemographics().getAge()));
	    	if (external.getDemographics().getIsDeceased() != null) internal.getDemographics().setIsDeceased(MappingUtility.bL2BLInternal(external.getDemographics().getIsDeceased()));
	    	if (external.getDemographics().getAgeAtDeath() != null) internal.getDemographics().setAgeAtDeath(MappingUtility.pQ2PQInternal(external.getDemographics().getAgeAtDeath()));
	    	if (external.getDemographics().getName() != null) {
	    		internal.getDemographics().setName(new ArrayList<EN>());
	    		for ( org.opencds.vmr.v1_0.schema.EN oneNamePart : external.getDemographics().getName() ) {
	    			internal.getDemographics().getName().add(MappingUtility.eN2ENInternal(oneNamePart));
	    		}
	    	}
	    	if (external.getDemographics().getAddress() != null) {
	    		internal.getDemographics().setAddress(new ArrayList<AD>());
	    		for ( org.opencds.vmr.v1_0.schema.AD oneAddressPart : external.getDemographics().getAddress() ) {
	    			internal.getDemographics().getAddress().add(MappingUtility.aD2ADInternal(oneAddressPart));
	    		}
	    	}
	    	if (external.getDemographics().getTelecom() != null) {
	    		internal.getDemographics().setTelecom(new ArrayList<TEL>());
	    		for ( org.opencds.vmr.v1_0.schema.TEL oneTelecomPart : external.getDemographics().getTelecom() ) {
	    			internal.getDemographics().getTelecom().add(MappingUtility.tEL2TELInternal(oneTelecomPart));
	    		}
	    	}
	    	
    	}
    	
		// pull in nested RelatedEntities
		if ( external.getRelatedEntity() != null ) {
			NestedObjectsMapper.pullInRelatedEntityNestedObjects(external, internal.getId(), subjectPersonId, focalPersonId, factLists);
		}
		
    	return internal;
	}
	
	
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * 
	 * @param results
	 * @param source
	 * @param output
	 * @param focalPersonId
	 * @param thisEvaluatedPersonId
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 * @throws ImproperUsageException
	 */
	public static void pushOut( Map<String, List<?>> 					results, 
    							EvaluatedPerson 						source, 
    							org.opencds.vmr.v1_0.schema.CDSOutput 	output, 
    							String 									focalPersonId, 
    							String 									thisEvaluatedPersonId ) 
    throws DataFormatException, InvalidDataException, ImproperUsageException {
		String _METHODNAME = "pushOut(): ";
		
    	if (source == null)
    		return;

		org.opencds.vmr.v1_0.schema.EvaluatedPerson target = new org.opencds.vmr.v1_0.schema.EvaluatedPerson();

    	EntityBaseMapper.pushOut(source, target);
    
		logger.trace(_METHODNAME + "children of " + source.getClass().getSimpleName() + ", " + source.getId());
    	if (source.getDemographics() != null) {
    		if (target.getDemographics() == null) {
    			target.setDemographics(new org.opencds.vmr.v1_0.schema.EvaluatedPerson.Demographics());
    		}
    		
	    	if (source.getDemographics().getBirthTime() != null) target.getDemographics().setBirthTime(MappingUtility.dateInternal2TS(source.getDemographics().getBirthTime()));
	    	if (source.getDemographics().getGender() != null) target.getDemographics().setGender(MappingUtility.cDInternal2CD(source.getDemographics().getGender()));
	
	    	if ((source.getDemographics().getRace() != null) && (source.getDemographics().getRace().size() > 0))  {
	    		for ( CD oneRaceCD : source.getDemographics().getRace() )  {
	    			target.getDemographics().getRace().add(MappingUtility.cDInternal2CD(oneRaceCD));
	    		} 
	    	}
	
	    	if ((source.getDemographics().getEthnicity() != null) && (source.getDemographics().getEthnicity().size() > 0)) {
	    		for ( CD oneEthnicityCD : source.getDemographics().getEthnicity() ) {
	    			target.getDemographics().getEthnicity().add(MappingUtility.cDInternal2CD(oneEthnicityCD));
	    		}
	    	}
	    	
	    	if (source.getDemographics().getPreferredLanguage() != null) target.getDemographics().setPreferredLanguage(MappingUtility.cDInternal2CD(source.getDemographics().getPreferredLanguage()));
	    	if (source.getDemographics().getAge() != null) target.getDemographics().setAge(MappingUtility.pQInternal2PQ(source.getDemographics().getAge()));
	    	if (source.getDemographics().getIsDeceased() != null) target.getDemographics().setIsDeceased(MappingUtility.bLInternal2BL(source.getDemographics().getIsDeceased()));
	    	if (source.getDemographics().getAgeAtDeath() != null) target.getDemographics().setAgeAtDeath(MappingUtility.pQInternal2PQ(source.getDemographics().getAgeAtDeath()));
	    	if (source.getDemographics().getName() != null) {   		
	    		//target.getDemographics().setName(new ArrayList<org.opencds.vmr.v1_0.schema.EN>());
	    		for (EN oneName : source.getDemographics().getName()) {
	    			target.getDemographics().getName().add(MappingUtility.eNInternal2EN(oneName));
	    		}    		
	    	}
	    	if (source.getDemographics().getAddress() != null) {   		
	    		//target.getDemographics().setAddress(new ArrayList<org.opencds.vmr.v1_0.schema.AD>());
	    		for (AD oneAddress : source.getDemographics().getAddress()) {
	    			target.getDemographics().getAddress().add(MappingUtility.aDInternal2AD(oneAddress));
	    		}    		
	    	}
	    	if (source.getDemographics().getTelecom() != null) {   		
	    		//target.getDemographics().setTelecom(new ArrayList<org.opencds.vmr.v1_0.schema.TEL>());
	    		for (TEL oneTelecom : source.getDemographics().getTelecom()) {
	    			target.getDemographics().getTelecom().add(MappingUtility.tELInternal2TEL(oneTelecom));
	    		}    		
	    	}
    	}
    	
		if ( focalPersonId.equals(thisEvaluatedPersonId) ) {
			output.getVmrOutput().setPatient(target);
		} else {
			//must be an OtherEvaluatedPerson...
			if (output.getVmrOutput().getOtherEvaluatedPersons() == null) {
				output.getVmrOutput().setOtherEvaluatedPersons(new org.opencds.vmr.v1_0.schema.VMR.OtherEvaluatedPersons());
			}
			output.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().add(target);
		}
    	return;
    }

}
