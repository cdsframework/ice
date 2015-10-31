/**
 * Copyright 2011, 2012 OpenCDS.org
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

package org.opencds.vmr.v1_0.mappings.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.FocalPersonId;
import org.opencds.vmr.v1_0.internal.RelationshipToSource;
import org.opencds.vmr.v1_0.mappings.mappers.CDSOutputMapper;
import org.opencds.vmr.v1_0.mappings.mappers.OneObjectMapper;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;


/**
 * <p>mapper to go from flat form based on javaBeans in the project opencds-vmr-internal
 * to external XML data described by vmr.schema in project opencds-vmr-external.
 * 
 * <p/>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: OpenCDS</p>
 *
 * @author David Shields
 * @version 2.0
 * @date 03-03-2011
 * 
 */
public class CdsOutputResultSetBuilder implements ResultSetBuilder<org.opencds.vmr.v1_0.schema.CDSOutput>
{
	private static Log logger = LogFactory.getLog(CdsOutputResultSetBuilder.class);

    private static final String[] clinicalStatementNames = {
    		"AdverseEvent", "DeniedAdverseEvent",
    		"AppointmentProposal", "AppointmentRequest", "EncounterEvent", "MissedAppointment", "ScheduledAppointment",
    		"Goal", "GoalProposal",
    		"ObservationOrder", "ObservationProposal", "ObservationResult", "UnconductedObservation",
    		"DeniedProblem", "Problem",
    		"ProcedureEvent", "ProcedureOrder", "ProcedureProposal", "ScheduledProcedure", "UndeliveredProcedure", 
    		"SubstanceAdministrationEvent", "SubstanceAdministrationOrder", "SubstanceAdministrationProposal", "SubstanceDispensationEvent", "UndeliveredSubstanceAdministration",
    		"SupplyEvent", "SupplyOrder", "SupplyProposal", "UndeliveredSupply"};  
    private static final Set<String> csNameList = new HashSet<>(Arrays.asList(clinicalStatementNames));
    private static final String[] entityNames = {"AdministrableSubstance","Entity","EvaluatedPerson","Facility","Organization","Person","Specimen"};
    private static final Set<String> entityNameList = new HashSet<>(Arrays.asList(entityNames));
//    private String[] relationshipNames = {"ClinicalStatementRelationship", "EntityRelationship"};
//    private List<String> relationshipNameList = Arrays.asList(relationshipNames);
    private static final String[] outputNames = {
    		"AdverseEvent", "DeniedAdverseEvent",
    		"AppointmentProposal", "AppointmentRequest", "EncounterEvent", "MissedAppointment", "ScheduledAppointment",
    		"Goal", "GoalProposal",
    		"ObservationOrder", "ObservationProposal", "ObservationResult", "UnconductedObservation",
    		"DeniedProblem", "Problem",
    		"ProcedureEvent", "ProcedureOrder", "ProcedureProposal", "ScheduledProcedure", "UndeliveredProcedure", 
    		"SubstanceAdministrationEvent", "SubstanceAdministrationOrder", "SubstanceAdministrationProposal", "SubstanceDispensationEvent", "UndeliveredSubstanceAdministration",
    		"SupplyEvent", "SupplyOrder", "SupplyProposal", "UndeliveredSupply",
    		"AdministrableSubstance","Entity","EvaluatedPerson","Facility","Organization","Person","Specimen",
    		"ClinicalStatementRelationship", "EntityRelationship"/*, "ClinicalStatementEntityInRoleRelationship"*/};
    private static final List<String> outputNameList = Arrays.asList(outputNames); 
    
	/* (non-Javadoc)
	 * @see org.opencds.vmr.v1_0.mappings.out.IBuildResultSet#buildResultSet(java.util.Map, java.util.Date, java.lang.String, java.lang.String, java.util.List)
	 */
    @Override
	@SuppressWarnings({ "unchecked" })
	public org.opencds.vmr.v1_0.schema.CDSOutput buildResultSet( 
			Map<String,List<?>>		results,
			EvaluationRequestKMItem		dssRequestKMItem
	) {
		org.opencds.vmr.v1_0.schema.CDSOutput 	cdsOutput 			= new org.opencds.vmr.v1_0.schema.CDSOutput();
		List<FocalPersonId> focalPersonIds = (List<FocalPersonId>) results.get("FocalPersonId");
        String focalPersonId = null;
        if (focalPersonIds != null && focalPersonIds.size() > 0) {
            focalPersonId = focalPersonIds.get(0).getId();
        }
		
       	if (results.get("CDSInput") != null) {
       		List<CDSInput> internalCdsInputList = (List<CDSInput>)results.get("CDSInput");
       		try {
       			for (CDSInput internalCdsInput : internalCdsInputList)  
       			{
       				/**
       				 * There should only be one CDSInput element, but it is stored in a list structure
       				 * 
       				 * The following code builds the cdsOutput structure using the cdsInput as a seed. 
       				 * It populates VMR tag level, and EvaluatedPerson.Demographics level
       				 * 
       				 */
       				CDSOutputMapper.pushOut(results, internalCdsInput, cdsOutput, focalPersonId);
       				
       				/*
       				 * Populate the nested clinical statement lists
       				 */
       				for ( EvaluatedPerson internalEvaluatedPerson : (List<EvaluatedPerson>)results.get("EvaluatedPerson") ) 
       				{
       					org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements outputClinicalStatements = new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements();
   						String subjectPersonId = internalEvaluatedPerson.getEvaluatedPersonId();
   						
   						/*
   						 * nested child clinical statements:
   						 * key = sourceId
   						 * payload - list of all RelatedClinicalStatements for that sourceId
   						 */
   						Map<String,List<ClinicalStatement>> csChildren = new ConcurrentHashMap<String,List<ClinicalStatement>>();
   							//key = clinical statement id, value = clinical statement object
   						Map<String,EntityBase> entityChildren = new ConcurrentHashMap<String,EntityBase>();
   							//key = entity ID, value = entity object
   						Map<String,List<EntityRelationship>> entityRelationships = new ConcurrentHashMap<String,List<EntityRelationship>>();
   							//key = sourceId, value = List of all entity relationship objects for that sourceId
   						
   						/*
   						 * Outer Loop A: build the map of child clinical statements in first pass
   						 */
	       				for ( Object oneFactListName : outputNameList ) 
	       				{
	       					List<?> oneResultList = results.get(oneFactListName);		       					
	       					if ((oneResultList != null) && (oneResultList.size() > 0))
	       					{
	       						/*
	       						 * results for this fact list are not empty
	       						 * 
	       						 * oneResult represents an object found in either a ClinicalStatement
	       						 * fact list or in an EntityBase fact list
	       						 */
	       						for ( Object oneResult : oneResultList ) 
	       						{	
	       							/*
	       							 * Accumulate Related Entities and Clinical Statements in separate lists...
	       							 * 
	       							 * First look for Clinical Statements
	       							 */
	       							if ( csNameList.contains( oneResult.getClass().getSimpleName()) ) {	 
	       								
	       								ClinicalStatement oneCSResult = ((ClinicalStatement)oneResult);
		       							if ( subjectPersonId.equals(((ClinicalStatement)oneResult).getEvaluatedPersonId()) ) {
			       							/*
			       							 * Accumulate map of child clinical statements in one list, so they can be reassembled later...
			       							 *  If oneResult is a child record, this loop should populate both the csChildren HashMap, and 
		       								 *  	it will also populate the TargetRelationshipToSources array in oneResult. 
		       								 *  
		       								 * des 2012-01-27: replaced original code with global search through 
		       								 * 		all clinicalStatementRelationships to look for nested CS, and to force
		       								 * 		populating the TargetRelationshipToSources in nested CS.
		       								 */  
				       						if (results.get("ClinicalStatementRelationship") != null) {
		       									for ( ClinicalStatementRelationship oneClinicalStatementRelationship : 
		       											(List<ClinicalStatementRelationship>)results.get("ClinicalStatementRelationship")) {
		       										if (( oneClinicalStatementRelationship.getTargetId().equals( oneCSResult.getId() ) ) 
		       												&& ( oneCSResult.isToBeReturned() )) {
			       										/*
			       										 * Note that the following code searches the CSR targetId against the current CS id 
			       										 * to find the id of child CS, then adds matching CS as an Object to csChildren
			       										 */
		       											if ( ((ClinicalStatement)oneResult).isClinicalStatementToBeRoot() == true ) {
		       												String warningMsg = "WARNING: CdsOutputResultSetBuilder found a Clinical Statement id "
	       														+ oneClinicalStatementRelationship.getTargetId() + " which is flagged "
	       														+ "as isClinicalStatementToBeRoot() == true, and is also the target of "
	       														+ "a Clinical Statement Relationship to id " 
	       														+ oneClinicalStatementRelationship.getSourceId()
	       														+ ".";
		       												logger.warn(warningMsg);
		       											}
						       							
		       											/* 
		       											 * Add the relationship to the RelationshipToSources() in target record if it is not already there...
		       											 * The target record at this point is oneResult
		       											 */
		       											if ( ((ClinicalStatement)oneResult).getRelationshipToSources() == null ) {
		       												List<RelationshipToSource> relationshipToSources = new ArrayList<RelationshipToSource>();
		       												((ClinicalStatement)oneResult).setRelationshipToSources(relationshipToSources);
		       											}
		       											boolean alreadyThere = false;
		       											for ( RelationshipToSource oneRTS : ((ClinicalStatement)oneResult).getRelationshipToSources() ) {
		       												if ( oneCSResult.getId().equals(oneRTS.getSourceId() ) ) {
		       													alreadyThere = true;
		       												}
		       											}
		       											if ( !alreadyThere ) {
			       											RelationshipToSource newRelationshipToSource = new RelationshipToSource();
			       											newRelationshipToSource.setSourceId(oneClinicalStatementRelationship.getSourceId());
			       											newRelationshipToSource.setRelationshipToSource(oneClinicalStatementRelationship.getTargetRelationshipToSource());
			       											((ClinicalStatement)oneResult).getRelationshipToSources().add(newRelationshipToSource);		
		       											}
		       											
		       											/*
		       											 * Add the child (target) ClinicalStatement to csChildren as well.
		       											 */
			       										String key = oneClinicalStatementRelationship.getSourceId();
						       							if ( csChildren.get(key) == null)
						       							{
						       								csChildren.put(key, new ArrayList<ClinicalStatement>());
						       							}
						       							List<ClinicalStatement> nestedClinicalStatementList = csChildren.get(key);
						       							nestedClinicalStatementList.add((ClinicalStatement)oneResult);
						       							csChildren.put( key, nestedClinicalStatementList );
		       										}
		       									}
		       								}
		       							}	       								
	       							/*
	       							 * Second look for Entities
	       							 */
	       							} else if ( entityNameList.contains(oneResult.getClass().getSimpleName() ) ) {
	       								EntityBase oneEBresult = (EntityBase)oneResult;
		       							if ( subjectPersonId.equals(oneEBresult.getEvaluatedPersonId()) ) {
			       							/*
			       							 * Accumulate map of listed entities in one list, so they can be reassembled later...
			       							 */
			       							String key = oneEBresult.getId();
			       							entityChildren.put( key, oneEBresult );
		       							}
	       							/*
	       							 * Third look for EntityRelationships
	       							 */
	       							} else if ( "EntityRelationship".equals(oneResult.getClass().getSimpleName() ) ) {
	       								EntityRelationship oneERresult = (EntityRelationship)oneResult;
		       							/*
		       							 * Accumulate map of relationship of child entities to another entity in one list, 
		       							 * so they can be reassembled later...
		       							 * 
		       							 * Note that this list may include relationships between entities that are not related
		       							 * to the current evaluated person...  
		       							 * This is not a problem, because they won't be used in that case...
		       							 * 
		       							 * Note also that the sourceId may point to either a clinical statement or an entity
		       							 */
		       							if ( oneERresult.getSourceId() != null )   
		       							{
			       							String key = oneERresult.getSourceId();
			       							if ( entityRelationships.get(key) == null)
			       							{
			       								entityRelationships.put(key, new ArrayList<EntityRelationship>());
			       							}
			       							List<EntityRelationship> nestedEntityList = entityRelationships.get(key);
			       							nestedEntityList.add(oneERresult);
			       							entityRelationships.put( key, nestedEntityList );
		       							}
	       							}
	       						}
	       					}
	       				}
   						
   						/*
   						 * Outer Loop B: iterate through the list of names of clinical statements, 
   						 * 		and push out the results
   						 */
	       				for ( String oneName : csNameList)
	       				{
	       					List<?> oneResultList = results.get(oneName);		       					
	       					if ((oneResultList != null) && (oneResultList.size() > 0))
	       					{
	       						/*
	       						 * results for this clinical statement are not empty
	       						 */
	       						for ( Object oneResult : oneResultList ) 
	       						{	
	       							if (( oneResult.getClass().getSuperclass() != null )   
	       									&& ( oneResult.getClass().getSuperclass().getSuperclass() != null )   
	       									&& ( "ClinicalStatement".equals(oneResult.getClass().getSuperclass().getSuperclass().getSimpleName() ) )) {
		       							if ( subjectPersonId.equals(((ClinicalStatement)oneResult).getEvaluatedPersonId()) ) {
			       							/*
			       							 * Process "root" clinical statements, based on the isClinicalStatementToBeRoot flag, 
			       							 * 		for the current subjectPersonId
			       							 * 
			       							 * Note:  all nested content (both clinical statements and entities) belonging to a root
			       							 * 		clinical statement will be always be included regardless of any flags on the nested content
			       							 */
			       							if ( ((ClinicalStatement)oneResult).isClinicalStatementToBeRoot() == true )
			       							{
				       							Object target = new Object();
				       							OrganizedResults organizedResults = new OrganizedResults(	outputClinicalStatements, 
				       																						subjectPersonId, 
				       																						focalPersonId, 
				       																						results, 
				       																						csChildren, 
				       																						entityChildren, 
				       																						entityRelationships);
				       							OneObjectMapper.pushOutRootClinicalStatement(oneResult, target, organizedResults);
			       							}
		       							}
	       							}
	       						}
	       					}
	       				}
	       				
       					/*
       					 * Place a populated ClinicalStatements element into either 
       					 * 		VMR.Patient.ClinicalStatements 
       					 * or into 
       					 * 		VMR.OtherEvaluatedPersons.OtherEvaluatedPerson.ClinicalStatements
       					 */
	       				if ( subjectPersonId.equals(focalPersonId) ) 
	       				{
	       					/*
	       					 * Set the clinicalStatements into the Patient element
	       					 */
	       					cdsOutput.getVmrOutput().getPatient().setClinicalStatements(outputClinicalStatements);
	       				} 
	       				else 
	       				{
	       					/*
	       					 * Set the clinical statements into the OtherEvaluatedPersons.EvaluatedPerson element
	       					 */
	       					for ( int i = 0; i < cdsOutput.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().size(); i++ ) 
	       					{
	       						org.opencds.vmr.v1_0.schema.EvaluatedPerson oneOtherEvaluatedPersonOut = cdsOutput.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().get(i);
	       						if ( subjectPersonId.equals(MappingUtility.iI2FlatId(oneOtherEvaluatedPersonOut.getId())) ) 
	       						{
	       							/*
	       							 * If this is the right person, replace this element with a new one having all the clinicalStatements in it
	       							 */
	       							oneOtherEvaluatedPersonOut.setClinicalStatements(outputClinicalStatements);
	       							cdsOutput.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().set(i, oneOtherEvaluatedPersonOut);
	       						}	       						
	       					}
	       				}
       				}       				
       			}
			} catch (DataFormatException | InvalidDataException | ImproperUsageException e) {
				e.printStackTrace();
				throw new OpenCDSRuntimeException("BuildVMRSchemaResult received a " + e.getClass().getSimpleName() + " while doing a CDSOutputMapper.pushOut: " + e.getMessage());
			}
       	}
		return cdsOutput;
	}

}


			
		


