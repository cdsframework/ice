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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.AdverseEvent;
import org.opencds.vmr.v1_0.internal.AppointmentProposal;
import org.opencds.vmr.v1_0.internal.AppointmentRequest;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.DeniedAdverseEvent;
import org.opencds.vmr.v1_0.internal.DeniedProblem;
import org.opencds.vmr.v1_0.internal.EncounterEvent;
import org.opencds.vmr.v1_0.internal.Entity;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.internal.Facility;
import org.opencds.vmr.v1_0.internal.Goal;
import org.opencds.vmr.v1_0.internal.GoalProposal;
import org.opencds.vmr.v1_0.internal.MissedAppointment;
import org.opencds.vmr.v1_0.internal.ObservationOrder;
import org.opencds.vmr.v1_0.internal.ObservationProposal;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.Organization;
import org.opencds.vmr.v1_0.internal.Person;
import org.opencds.vmr.v1_0.internal.Problem;
import org.opencds.vmr.v1_0.internal.ProcedureEvent;
import org.opencds.vmr.v1_0.internal.ProcedureOrder;
import org.opencds.vmr.v1_0.internal.ProcedureProposal;
import org.opencds.vmr.v1_0.internal.RelationshipToSource;
import org.opencds.vmr.v1_0.internal.ScheduledAppointment;
import org.opencds.vmr.v1_0.internal.ScheduledProcedure;
import org.opencds.vmr.v1_0.internal.Specimen;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationOrder;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.internal.SubstanceDispensationEvent;
import org.opencds.vmr.v1_0.internal.SupplyEvent;
import org.opencds.vmr.v1_0.internal.SupplyOrder;
import org.opencds.vmr.v1_0.internal.SupplyProposal;
import org.opencds.vmr.v1_0.internal.UnconductedObservation;
import org.opencds.vmr.v1_0.internal.UndeliveredProcedure;
import org.opencds.vmr.v1_0.internal.UndeliveredSubstanceAdministration;
import org.opencds.vmr.v1_0.internal.UndeliveredSupply;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * This specialized mapper routes mapping of generic Clinical Statements, related Clinical Statements,
 * 		and related Entities to the specific mapper class needed.
 * 
 * @author David Shields
 *
 */
public abstract class OneObjectMapper extends Object {

	private static Log logger = LogFactory.getLog(OneObjectMapper.class);
	
	/**
	 * Populate internal vMR object from corresponding external vMR object;
	 * 
	 * @param source external vMR object (an extension of the schema ClinicalStatement class)
	 * @param target internal vMR object to be populated (a descendent of ClinicalStatement)
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static void pullInClinicalStatement(
			Object 											source, 
			Object			 								target, 
			String											subjectPersonId, 
			String											focalPersonId,
			FactLists										factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {

		String _METHODNAME = "pullIn(): ";
		
		if (source == null) {
			String errStr = _METHODNAME + "improper usage: source supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		if ("AdverseEvent".equals(source.getClass().getSimpleName())) {
			AdverseEventMapper.pullIn((org.opencds.vmr.v1_0.schema.AdverseEvent)source, (AdverseEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("DeniedAdverseEvent".equals(source.getClass().getSimpleName())) {
			DeniedAdverseEventMapper.pullIn((org.opencds.vmr.v1_0.schema.DeniedAdverseEvent)source, (DeniedAdverseEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("AppointmentProposal".equals(source.getClass().getSimpleName())) {
			AppointmentProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.AppointmentProposal)source, (AppointmentProposal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("AppointmentRequest".equals(source.getClass().getSimpleName())) {
			AppointmentRequestMapper.pullIn((org.opencds.vmr.v1_0.schema.AppointmentRequest)source, (AppointmentRequest)target, subjectPersonId, focalPersonId, factLists);
		} else if ("EncounterEvent".equals(source.getClass().getSimpleName())) {
			EncounterEventMapper.pullIn((org.opencds.vmr.v1_0.schema.EncounterEvent)source, (EncounterEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("MissedAppointment".equals(source.getClass().getSimpleName())) {
			MissedAppointmentMapper.pullIn((org.opencds.vmr.v1_0.schema.MissedAppointment)source, (MissedAppointment)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ScheduledAppointment".equals(source.getClass().getSimpleName())) {
			ScheduledAppointmentMapper.pullIn((org.opencds.vmr.v1_0.schema.ScheduledAppointment)source, (ScheduledAppointment)target, subjectPersonId, focalPersonId, factLists);
		} else if ("Goal".equals(source.getClass().getSimpleName())) {
			GoalMapper.pullIn((org.opencds.vmr.v1_0.schema.Goal)source, (Goal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("GoalProposal".equals(source.getClass().getSimpleName())) {
			GoalProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.GoalProposal)source, (GoalProposal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ObservationOrder".equals(source.getClass().getSimpleName())) {
			ObservationOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.ObservationOrder)source, (ObservationOrder)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ObservationProposal".equals(source.getClass().getSimpleName())) {
			ObservationProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.ObservationProposal)source, (ObservationProposal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ObservationResult".equals(source.getClass().getSimpleName())) {
			ObservationResultMapper.pullIn((org.opencds.vmr.v1_0.schema.ObservationResult)source, (ObservationResult)target, subjectPersonId, focalPersonId, factLists);
		} else if ("UnconductedObservation".equals(source.getClass().getSimpleName())) {
			UnconductedObservationMapper.pullIn((org.opencds.vmr.v1_0.schema.UnconductedObservation)source, (UnconductedObservation)target, subjectPersonId, focalPersonId, factLists);
		} else if ("DeniedProblem".equals(source.getClass().getSimpleName())) {
			DeniedProblemMapper.pullIn((org.opencds.vmr.v1_0.schema.DeniedProblem)source, (DeniedProblem)target, subjectPersonId, focalPersonId, factLists);
		} else if ("Problem".equals(source.getClass().getSimpleName())) {
			ProblemMapper.pullIn((org.opencds.vmr.v1_0.schema.Problem)source, (Problem)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ProcedureEvent".equals(source.getClass().getSimpleName())) {
			ProcedureEventMapper.pullIn((org.opencds.vmr.v1_0.schema.ProcedureEvent)source, (ProcedureEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ProcedureOrder".equals(source.getClass().getSimpleName())) {
			ProcedureOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.ProcedureOrder)source, (ProcedureOrder)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ProcedureProposal".equals(source.getClass().getSimpleName())) {
			ProcedureProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.ProcedureProposal)source, (ProcedureProposal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("ScheduledProcedure".equals(source.getClass().getSimpleName())) {
			ScheduledProcedureMapper.pullIn((org.opencds.vmr.v1_0.schema.ScheduledProcedure)source, (ScheduledProcedure)target, subjectPersonId, focalPersonId, factLists);
		} else if ("UndeliveredProcedure".equals(source.getClass().getSimpleName())) {
			UndeliveredProcedureMapper.pullIn((org.opencds.vmr.v1_0.schema.UndeliveredProcedure)source, (UndeliveredProcedure)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SubstanceAdministrationEvent".equals(source.getClass().getSimpleName())) {
			SubstanceAdministrationEventMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent)source, (SubstanceAdministrationEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SubstanceAdministrationOrder".equals(source.getClass().getSimpleName())) {
			SubstanceAdministrationOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder)source, (SubstanceAdministrationOrder)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SubstanceAdministrationProposal".equals(source.getClass().getSimpleName())) {
			SubstanceAdministrationProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal)source, (SubstanceAdministrationProposal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SubstanceDispensationEvent".equals(source.getClass().getSimpleName())) {
			SubstanceDispensationEventMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent)source, (SubstanceDispensationEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("UndeliveredSubstanceAdministration".equals(source.getClass().getSimpleName())) {
			UndeliveredSubstanceAdministrationMapper.pullIn((org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration)source, (UndeliveredSubstanceAdministration)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SupplyEvent".equals(source.getClass().getSimpleName())) {
			SupplyEventMapper.pullIn((org.opencds.vmr.v1_0.schema.SupplyEvent)source, (SupplyEvent)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SupplyOrder".equals(source.getClass().getSimpleName())) {
			SupplyOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.SupplyOrder)source, (SupplyOrder)target, subjectPersonId, focalPersonId, factLists);
		} else if ("SupplyProposal".equals(source.getClass().getSimpleName())) {
			SupplyProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.SupplyProposal)source, (SupplyProposal)target, subjectPersonId, focalPersonId, factLists);
		} else if ("UndeliveredSupply".equals(source.getClass().getSimpleName())) {
			UndeliveredSupplyMapper.pullIn((org.opencds.vmr.v1_0.schema.UndeliveredSupply)source, (UndeliveredSupply)target, subjectPersonId, focalPersonId, factLists);
		} else {
			throw new ImproperUsageException("OneObjectMapper failed to pullInRelatedClinicalStatement for: " + source.getClass().getSimpleName());
		}
	}
	
	
	/**
	 * pullInRelatedEntity takes nested RelatedEntity information and flattens it into two lists:
	 * 		entityList for the specific entityType, and the generic EntityRelationship list.
	 * 
	 * @param sourceRelatedEntity
	 * @param parentId
	 * @param relationshipToSource
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static void pullInRelatedEntity
	(
			org.opencds.vmr.v1_0.schema.RelatedEntity		sourceRelatedEntity, 	
			String			 								parentId, 
			org.opencds.vmr.v1_0.schema.CD					relationshipToSource,
			String											subjectPersonId, 
			String											focalPersonId,
			FactLists										factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {

		String _METHODNAME = "pullInRelatedEntity(): ";
		
		if (sourceRelatedEntity == null) {
			String errStr = _METHODNAME + "improper usage: source supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		} 
		if (parentId == null) {
			String errStr = _METHODNAME + "improper usage: parentId supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
//        if (parentId.equals(external.getId())) {
//            throw new OpenCDSRuntimeException(
//                    "root and/or extension of source and target IDs of the (post-processed) relationship may not be the same: source (root^extension)= "
//                            + sourceId + ", target (root^extension)= " + external.getId());
//        }
		
		logger.trace(_METHODNAME + sourceRelatedEntity.getClass().getSimpleName() + ", " + parentId );
		
		if (sourceRelatedEntity.getAdministrableSubstance() != null) {
			AdministrableSubstanceMapper.pullIn(
					sourceRelatedEntity.getAdministrableSubstance(), new AdministrableSubstance(), parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
			EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getAdministrableSubstance().getId(), 
					sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists); 
		} else if (sourceRelatedEntity.getEntity() != null) {
			EntityMapper.pullIn(
					sourceRelatedEntity.getEntity(), new Entity(), parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
			EntityRelationshipMapper.pullIn(
					parentId, sourceRelatedEntity.getEntity().getId(), 
					sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists); 
		} else if (sourceRelatedEntity.getFacility() != null) {
			FacilityMapper.pullIn(
					sourceRelatedEntity.getFacility(), new Facility(), parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
			EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getFacility().getId(), 
					sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists); 
		} else if (sourceRelatedEntity.getOrganization() != null) {
			OrganizationMapper.pullIn(
					sourceRelatedEntity.getOrganization(), new Organization(), parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
			EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getOrganization().getId(), 
					sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists); 
		} else if (sourceRelatedEntity.getPerson() != null) {
			PersonMapper.pullIn(
					sourceRelatedEntity.getPerson(), new Person(), parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
			EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getPerson().getId(), 
					sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists); 
		} else if (sourceRelatedEntity.getSpecimen() != null) {
			SpecimenMapper.pullIn(
					sourceRelatedEntity.getSpecimen(), new Specimen(), parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
			EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getSpecimen().getId(), 
					sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists); 
		} else {
			String errStr = _METHODNAME + "improper usage: source class not recognized: " + sourceRelatedEntity.getClass().getName() + " for sourceId: " + parentId + ".";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
	}
	
	
	/**
	 * Populate internal vMR object from corresponding external vMR object; flattens a relatedClinicalStatement into
	 * two lists:  specific clinical statement lists, and the generic clinicalStatementRelationships, which ties 
	 * two clinical statements together.
	 * 
	 * @param source
	 * @param sourceId
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static void pullInRelatedClinicalStatement(
			org.opencds.vmr.v1_0.schema.RelatedClinicalStatement	source, 	
			String			 								sourceId, 
			String											subjectPersonId, 
			String											focalPersonId,
			FactLists										factLists
	) throws ImproperUsageException, DataFormatException, InvalidDataException {

		String _METHODNAME = "pullInRelatedClinicalStatement(): ";
		
		if (source == null) {
			String errStr = _METHODNAME + "improper usage: source supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		if (sourceId == null) {
			String errStr = _METHODNAME + "improper usage: sourceId supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		CD targetRelationshipToSource = MappingUtility.cD2CDInternal(source.getTargetRelationshipToSource());
		RelationshipToSource relationshipToSource = new RelationshipToSource();
		relationshipToSource.setRelationshipToSource(targetRelationshipToSource);
		relationshipToSource.setSourceId(sourceId);
		List<RelationshipToSource> relationshipToSources = new ArrayList<RelationshipToSource>();
		relationshipToSources.add(relationshipToSource);
		
		if (source.getAdverseEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getAdverseEvent().getId()), targetRelationshipToSource, factLists);
			AdverseEvent target = new AdverseEvent();
			target.setRelationshipToSources(relationshipToSources);
			AdverseEventMapper.pullIn(source.getAdverseEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getDeniedAdverseEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getDeniedAdverseEvent().getId()), targetRelationshipToSource, factLists);
			DeniedAdverseEvent target = new DeniedAdverseEvent();
			target.setRelationshipToSources(relationshipToSources);
			DeniedAdverseEventMapper.pullIn(source.getDeniedAdverseEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getAppointmentProposal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getAppointmentProposal().getId()), targetRelationshipToSource, factLists);
			AppointmentProposal target = new AppointmentProposal();
			target.setRelationshipToSources(relationshipToSources);
			AppointmentProposalMapper.pullIn(source.getAppointmentProposal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getAppointmentRequest() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getAppointmentRequest().getId()), targetRelationshipToSource, factLists);
			AppointmentRequest target = new AppointmentRequest();
			target.setRelationshipToSources(relationshipToSources);
			AppointmentRequestMapper.pullIn(source.getAppointmentRequest(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getEncounterEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getEncounterEvent().getId()), targetRelationshipToSource, factLists);
			EncounterEvent target = new EncounterEvent();
			target.setRelationshipToSources(relationshipToSources);
			EncounterEventMapper.pullIn(source.getEncounterEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getMissedAppointment() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getMissedAppointment().getId()), targetRelationshipToSource, factLists);
			MissedAppointment target = new MissedAppointment();
			target.setRelationshipToSources(relationshipToSources);
			MissedAppointmentMapper.pullIn(source.getMissedAppointment(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getScheduledAppointment() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getScheduledAppointment().getId()), targetRelationshipToSource, factLists);
			ScheduledAppointment target = new ScheduledAppointment();
			target.setRelationshipToSources(relationshipToSources);
			ScheduledAppointmentMapper.pullIn(source.getScheduledAppointment(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getGoal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getGoal().getId()), targetRelationshipToSource, factLists);
			Goal target = new Goal();
			target.setRelationshipToSources(relationshipToSources);
			GoalMapper.pullIn(source.getGoal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getGoalProposal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getGoalProposal().getId()), targetRelationshipToSource, factLists);
			GoalProposal target = new GoalProposal();
			target.setRelationshipToSources(relationshipToSources);
			GoalProposalMapper.pullIn(source.getGoalProposal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getObservationOrder() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getObservationOrder().getId()), targetRelationshipToSource, factLists);
			ObservationOrder target = new ObservationOrder();
			target.setRelationshipToSources(relationshipToSources);
			ObservationOrderMapper.pullIn(source.getObservationOrder(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getObservationProposal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getObservationProposal().getId()), targetRelationshipToSource, factLists);
			ObservationProposal target = new ObservationProposal();
			target.setRelationshipToSources(relationshipToSources);
			ObservationProposalMapper.pullIn(source.getObservationProposal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getObservationResult() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getObservationResult().getId()), targetRelationshipToSource, factLists);
			ObservationResult target = new ObservationResult();
			target.setRelationshipToSources(relationshipToSources);
			ObservationResultMapper.pullIn(source.getObservationResult(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getUnconductedObservation() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getUnconductedObservation().getId()), targetRelationshipToSource, factLists);
			UnconductedObservation target = new UnconductedObservation();
			target.setRelationshipToSources(relationshipToSources);
			UnconductedObservationMapper.pullIn(source.getUnconductedObservation(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getDeniedProblem() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getDeniedProblem().getId()), targetRelationshipToSource, factLists);
			DeniedProblem target = new DeniedProblem();
			target.setRelationshipToSources(relationshipToSources);
			DeniedProblemMapper.pullIn(source.getDeniedProblem(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getProblem() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getProblem().getId()), targetRelationshipToSource, factLists);
			Problem target = new Problem();
			target.setRelationshipToSources(relationshipToSources);
			ProblemMapper.pullIn(source.getProblem(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getProcedureEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getProcedureEvent().getId()), targetRelationshipToSource, factLists);
			ProcedureEvent target = new ProcedureEvent();
			target.setRelationshipToSources(relationshipToSources);
			ProcedureEventMapper.pullIn(source.getProcedureEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getProcedureOrder() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getProcedureOrder().getId()), targetRelationshipToSource, factLists);
			ProcedureOrder target = new ProcedureOrder();
			target.setRelationshipToSources(relationshipToSources);
			ProcedureOrderMapper.pullIn(source.getProcedureOrder(), target, subjectPersonId,focalPersonId, factLists);
		} else if (source.getProcedureProposal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getProcedureProposal().getId()), targetRelationshipToSource, factLists);
			ProcedureProposal target = new ProcedureProposal();
			target.setRelationshipToSources(relationshipToSources);
			ProcedureProposalMapper.pullIn(source.getProcedureProposal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getScheduledProcedure() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getScheduledProcedure().getId()), targetRelationshipToSource, factLists);
			ScheduledProcedure target = new ScheduledProcedure();
			target.setRelationshipToSources(relationshipToSources);
			ScheduledProcedureMapper.pullIn(source.getScheduledProcedure(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getUndeliveredProcedure() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getUndeliveredProcedure().getId()), targetRelationshipToSource, factLists);
			UndeliveredProcedure target = new UndeliveredProcedure();
			target.setRelationshipToSources(relationshipToSources);
			UndeliveredProcedureMapper.pullIn(source.getUndeliveredProcedure(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSubstanceAdministrationEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSubstanceAdministrationEvent().getId()), targetRelationshipToSource, factLists);
			SubstanceAdministrationEvent target = new SubstanceAdministrationEvent();
			target.setRelationshipToSources(relationshipToSources);
			SubstanceAdministrationEventMapper.pullIn(source.getSubstanceAdministrationEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSubstanceAdministrationOrder() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSubstanceAdministrationOrder().getId()), targetRelationshipToSource, factLists);
			SubstanceAdministrationOrder target = new SubstanceAdministrationOrder();
			target.setRelationshipToSources(relationshipToSources);
			SubstanceAdministrationOrderMapper.pullIn(source.getSubstanceAdministrationOrder(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSubstanceAdministrationProposal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSubstanceAdministrationProposal().getId()), targetRelationshipToSource, factLists);
			SubstanceAdministrationProposal target = new SubstanceAdministrationProposal();
			target.setRelationshipToSources(relationshipToSources);
			SubstanceAdministrationProposalMapper.pullIn(source.getSubstanceAdministrationProposal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSubstanceDispensationEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSubstanceDispensationEvent().getId()), targetRelationshipToSource, factLists);
			SubstanceDispensationEvent target = new SubstanceDispensationEvent();
			target.setRelationshipToSources(relationshipToSources);
			SubstanceDispensationEventMapper.pullIn(source.getSubstanceDispensationEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getUndeliveredSubstanceAdministration() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getUndeliveredSubstanceAdministration().getId()), targetRelationshipToSource, factLists);
			UndeliveredSubstanceAdministration target = new UndeliveredSubstanceAdministration();
			target.setRelationshipToSources(relationshipToSources);
			UndeliveredSubstanceAdministrationMapper.pullIn(source.getUndeliveredSubstanceAdministration(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSupplyEvent() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSupplyEvent().getId()), targetRelationshipToSource, factLists);
			SupplyEvent target = new SupplyEvent();
			target.setRelationshipToSources(relationshipToSources);
			SupplyEventMapper.pullIn(source.getSupplyEvent(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSupplyOrder() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSupplyOrder().getId()), targetRelationshipToSource, factLists);
			SupplyOrder target = new SupplyOrder();
			target.setRelationshipToSources(relationshipToSources);
			SupplyOrderMapper.pullIn(source.getSupplyOrder(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getSupplyProposal() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getSupplyProposal().getId()), targetRelationshipToSource, factLists);
			SupplyProposal target = new SupplyProposal();
			target.setRelationshipToSources(relationshipToSources);
			SupplyProposalMapper.pullIn(source.getSupplyProposal(), target, subjectPersonId, focalPersonId, factLists);
		} else if (source.getUndeliveredSupply() != null) {
			ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getUndeliveredSupply().getId()), targetRelationshipToSource, factLists);
			UndeliveredSupply target = new UndeliveredSupply();
			target.setRelationshipToSources(relationshipToSources);
			UndeliveredSupplyMapper.pullIn(source.getUndeliveredSupply(), target, subjectPersonId, focalPersonId, factLists);
		}		
	}
	
	
	/**
	 * This pushOut method handles root clinical statements only.  The "isReturnData" flag for clinical statements
	 * is only checked at this level.  All child clinical statements to the root will be automatically treated in
	 * the same manner as the root.  If the root is flagged as output, then all the children will be attached.  If the
	 * root is not flagged as output, then all of the children will be ignored.
	 * 
	 * @param source
	 * @param target
	 * @param organizedResults
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static void pushOutRootClinicalStatement( 
		Object 											source, 
		Object			 								target, 
		OrganizedResults								organizedResults
	) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOutClinicalStatement(): "; 
		
		if (source == null) {
			String errStr = _METHODNAME + "improper usage: source supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		if (target == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		/*
		 * If this ClinicalStatement is a root clinical statement that is not flagged as output, then ignore it...
		 * We always provide all children of any flagged clinical statement on output.
		 */
		if (( !((ClinicalStatement)source).isToBeReturned() ) 							//if not flagged then ignore it
				|| ( ((ClinicalStatement)source).isClinicalStatementToBeRoot() != true ))	//if not a root clinical statement then ignore it
			return;		
		
		
		logger.trace(_METHODNAME + source.getClass().getSimpleName());
		
		if ("AdverseEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.AdverseEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.AdverseEvent();
			rootClinicalStatement = AdverseEventMapper.pushOut(
					(AdverseEvent)source, new org.opencds.vmr.v1_0.schema.AdverseEvent(), organizedResults);	
			organizedResults.getOutput().getAdverseEvents().getAdverseEvent().add(rootClinicalStatement);
		} else if ("DeniedAdverseEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.DeniedAdverseEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.DeniedAdverseEvent();
			rootClinicalStatement = DeniedAdverseEventMapper.pushOut(
					(DeniedAdverseEvent)source, new org.opencds.vmr.v1_0.schema.DeniedAdverseEvent(), organizedResults);
			organizedResults.getOutput().getDeniedAdverseEvents().getDeniedAdverseEvent().add(rootClinicalStatement);
		} else if ("AppointmentProposal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.AppointmentProposal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.AppointmentProposal();
			rootClinicalStatement = AppointmentProposalMapper.pushOut(
					(AppointmentProposal)source, new org.opencds.vmr.v1_0.schema.AppointmentProposal(), organizedResults);
			organizedResults.getOutput().getAppointmentProposals().getAppointmentProposal().add(rootClinicalStatement);
		} else if ("AppointmentRequest".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.AppointmentRequest rootClinicalStatement = new org.opencds.vmr.v1_0.schema.AppointmentRequest();
			rootClinicalStatement = AppointmentRequestMapper.pushOut(
					(AppointmentRequest)source, new org.opencds.vmr.v1_0.schema.AppointmentRequest(), organizedResults);
			organizedResults.getOutput().getAppointmentRequests().getAppointmentRequest().add(rootClinicalStatement);
		} else if ("EncounterEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.EncounterEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.EncounterEvent();
			rootClinicalStatement = EncounterEventMapper.pushOut(
					(EncounterEvent)source, new org.opencds.vmr.v1_0.schema.EncounterEvent(), organizedResults);
			organizedResults.getOutput().getEncounterEvents().getEncounterEvent().add(rootClinicalStatement);
		} else if ("MissedAppointment".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.MissedAppointment rootClinicalStatement = new org.opencds.vmr.v1_0.schema.MissedAppointment();
			rootClinicalStatement = MissedAppointmentMapper.pushOut(
					(MissedAppointment)source, new org.opencds.vmr.v1_0.schema.MissedAppointment(), organizedResults);
			organizedResults.getOutput().getMissedAppointments().getMissedAppointment().add(rootClinicalStatement);
		} else if ("ScheduledAppointment".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ScheduledAppointment rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ScheduledAppointment();
			rootClinicalStatement = ScheduledAppointmentMapper.pushOut(
					(ScheduledAppointment)source, new org.opencds.vmr.v1_0.schema.ScheduledAppointment(), organizedResults);
			organizedResults.getOutput().getScheduledAppointments().getScheduledAppointment().add(rootClinicalStatement);
		} else if ("Goal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.Goal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.Goal();
			rootClinicalStatement = GoalMapper.pushOut(
					(Goal)source, new org.opencds.vmr.v1_0.schema.Goal(), organizedResults);
			organizedResults.getOutput().getGoals().getGoal().add(rootClinicalStatement);
		} else if ("GoalProposal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.GoalProposal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.GoalProposal();
			rootClinicalStatement = GoalProposalMapper.pushOut(
					(GoalProposal)source, new org.opencds.vmr.v1_0.schema.GoalProposal(), organizedResults);
			organizedResults.getOutput().getGoalProposals().getGoalProposal().add(rootClinicalStatement);
		} else if ("ObservationOrder".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ObservationOrder rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ObservationOrder();
			rootClinicalStatement = ObservationOrderMapper.pushOut(
					(ObservationOrder)source, new org.opencds.vmr.v1_0.schema.ObservationOrder(), organizedResults);
			organizedResults.getOutput().getObservationOrders().getObservationOrder().add(rootClinicalStatement);
		} else if ("ObservationProposal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ObservationProposal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ObservationProposal();
			rootClinicalStatement = ObservationProposalMapper.pushOut(
					(ObservationProposal)source, new org.opencds.vmr.v1_0.schema.ObservationProposal(), organizedResults);
			organizedResults.getOutput().getObservationProposals().getObservationProposal().add(rootClinicalStatement);
		} else if ("ObservationResult".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ObservationResult rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ObservationResult();
			rootClinicalStatement = ObservationResultMapper.pushOut(
					(ObservationResult)source, new org.opencds.vmr.v1_0.schema.ObservationResult(), organizedResults);
			organizedResults.getOutput().getObservationResults().getObservationResult().add(rootClinicalStatement);
		} else if ("UnconductedObservation".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.UnconductedObservation rootClinicalStatement = new org.opencds.vmr.v1_0.schema.UnconductedObservation();
			rootClinicalStatement = UnconductedObservationMapper.pushOut(
					(UnconductedObservation)source, new org.opencds.vmr.v1_0.schema.UnconductedObservation(), organizedResults);
			organizedResults.getOutput().getUnconductedObservations().getUnconductedObservation().add(rootClinicalStatement);
		} else if ("DeniedProblem".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.DeniedProblem rootClinicalStatement = new org.opencds.vmr.v1_0.schema.DeniedProblem();
			rootClinicalStatement = DeniedProblemMapper.pushOut(
					(DeniedProblem)source, new org.opencds.vmr.v1_0.schema.DeniedProblem(), organizedResults);
			organizedResults.getOutput().getDeniedProblems().getDeniedProblem().add(rootClinicalStatement);
		} else if ("Problem".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.Problem rootClinicalStatement = new org.opencds.vmr.v1_0.schema.Problem();
			rootClinicalStatement = ProblemMapper.pushOut(
					(Problem)source, new org.opencds.vmr.v1_0.schema.Problem(), organizedResults);
			organizedResults.getOutput().getProblems().getProblem().add(rootClinicalStatement);
		} else if ("ProcedureEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ProcedureEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ProcedureEvent();
			rootClinicalStatement = ProcedureEventMapper.pushOut(
					(ProcedureEvent)source, new org.opencds.vmr.v1_0.schema.ProcedureEvent(), organizedResults);
			organizedResults.getOutput().getProcedureEvents().getProcedureEvent().add(rootClinicalStatement);
		} else if ("ProcedureOrder".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ProcedureOrder rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ProcedureOrder();
			rootClinicalStatement = ProcedureOrderMapper.pushOut(
					(ProcedureOrder)source, new org.opencds.vmr.v1_0.schema.ProcedureOrder(), organizedResults);
			organizedResults.getOutput().getProcedureOrders().getProcedureOrder().add(rootClinicalStatement);
		} else if ("ProcedureProposal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ProcedureProposal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ProcedureProposal();
			rootClinicalStatement =  ProcedureProposalMapper.pushOut(
					(ProcedureProposal)source, new org.opencds.vmr.v1_0.schema.ProcedureProposal(), organizedResults);
			organizedResults.getOutput().getProcedureProposals().getProcedureProposal().add(rootClinicalStatement);
		} else if ("ScheduledProcedure".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.ScheduledProcedure rootClinicalStatement = new org.opencds.vmr.v1_0.schema.ScheduledProcedure();
			rootClinicalStatement = ScheduledProcedureMapper.pushOut(
					(ScheduledProcedure)source, new org.opencds.vmr.v1_0.schema.ScheduledProcedure(), organizedResults);
			organizedResults.getOutput().getScheduledProcedures().getScheduledProcedure().add(rootClinicalStatement);
		} else if ("UndeliveredProcedure".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.UndeliveredProcedure rootClinicalStatement = new org.opencds.vmr.v1_0.schema.UndeliveredProcedure();
			rootClinicalStatement = UndeliveredProcedureMapper.pushOut(
					(UndeliveredProcedure)source, new org.opencds.vmr.v1_0.schema.UndeliveredProcedure(), organizedResults);
			organizedResults.getOutput().getUndeliveredProcedures().getUndeliveredProcedure().add(rootClinicalStatement);
		} else if ("SubstanceAdministrationEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent();
			rootClinicalStatement = SubstanceAdministrationEventMapper.pushOut(
					(SubstanceAdministrationEvent)source, new org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent(), organizedResults);
			organizedResults.getOutput().getSubstanceAdministrationEvents().getSubstanceAdministrationEvent().add(rootClinicalStatement);
		} else if ("SubstanceAdministrationOrder".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder();
			rootClinicalStatement = SubstanceAdministrationOrderMapper.pushOut(
					(SubstanceAdministrationOrder)source, new org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder(), organizedResults);
			organizedResults.getOutput().getSubstanceAdministrationOrders().getSubstanceAdministrationOrder().add(rootClinicalStatement);
		} else if ("SubstanceAdministrationProposal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal();
			rootClinicalStatement = SubstanceAdministrationProposalMapper.pushOut(
					(SubstanceAdministrationProposal)source, new org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal(), organizedResults);
			organizedResults.getOutput().getSubstanceAdministrationProposals().getSubstanceAdministrationProposal().add(rootClinicalStatement);
		} else if ("SubstanceDispensationEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent();
			rootClinicalStatement = SubstanceDispensationEventMapper.pushOut(
					(SubstanceDispensationEvent)source, new org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent(), organizedResults);
			organizedResults.getOutput().getSubstanceDispensationEvents().getSubstanceDispensationEvent().add(rootClinicalStatement);
		} else if ("UndeliveredSubstanceAdministration".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration rootClinicalStatement = new org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration();
			rootClinicalStatement = UndeliveredSubstanceAdministrationMapper.pushOut(
					(UndeliveredSubstanceAdministration)source, new org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration(), organizedResults);
			organizedResults.getOutput().getUndeliveredSubstanceAdministrations().getUndeliveredSubstanceAdministration().add(rootClinicalStatement);
		} else if ("SupplyEvent".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SupplyEvent rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SupplyEvent();
			rootClinicalStatement = SupplyEventMapper.pushOut(
					(SupplyEvent)source, new org.opencds.vmr.v1_0.schema.SupplyEvent(), organizedResults);
			organizedResults.getOutput().getSupplyEvents().getSupplyEvent().add(rootClinicalStatement);
		} else if ("SupplyOrder".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SupplyOrder rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SupplyOrder();
			rootClinicalStatement = SupplyOrderMapper.pushOut(
					(SupplyOrder)source, new org.opencds.vmr.v1_0.schema.SupplyOrder(), organizedResults);
			organizedResults.getOutput().getSupplyOrders().getSupplyOrder().add(rootClinicalStatement);
		} else if ("SupplyProposal".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.SupplyProposal rootClinicalStatement = new org.opencds.vmr.v1_0.schema.SupplyProposal();
			rootClinicalStatement = SupplyProposalMapper.pushOut(
					(SupplyProposal)source, new org.opencds.vmr.v1_0.schema.SupplyProposal(), organizedResults);
			organizedResults.getOutput().getSupplyProposals().getSupplyProposal().add(rootClinicalStatement);
		} else if ("UndeliveredSupply".equals(source.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.UndeliveredSupply rootClinicalStatement = new org.opencds.vmr.v1_0.schema.UndeliveredSupply();
			rootClinicalStatement = UndeliveredSupplyMapper.pushOut(
					(UndeliveredSupply)source, new org.opencds.vmr.v1_0.schema.UndeliveredSupply(), organizedResults);
			organizedResults.getOutput().getUndeliveredSupplies().getUndeliveredSupply().add(rootClinicalStatement);
		}

		return;
	}
	
	
	/**
	 * pushOutRelatedClinicalStatement builds the nested output of related clinical statements to a "source" clinical statement.
	 * 
	 * This process can recurse to any depth, so you can have a clinical statement with a nested clinical statement, and the
	 * nested clinical statement may also have a nested clinical statement.  In general, nested clinical statements are most
	 * commonly observations, but this code will handle any clinical statement.
	 * 
	 * @param internalVMR
	 * @param externalVMR
	 * @param organizedResults
	 * @return
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.RelatedClinicalStatement pushOutRelatedClinicalStatement(
			ClinicalStatement								internalVMR,
//			org.opencds.vmr.v1_0.schema.ClinicalStatement	externalVMR,  
			OrganizedResults								organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOutRelatedClinicalStatement(): "; 
		
		if (internalVMR == null) {
			String errStr = _METHODNAME + "improper usage: internalVMR ClinicalStatement supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
//		if ( !internalVMR.getClass().getSimpleName().equals( externalVMR.getClass().getSimpleName() ) ) {
//			String errStr = _METHODNAME + "improper usage: internalVMR ClinicalStatement (" + internalVMR.getClass().getSimpleName() 
//										+ ") different than externalVMR ClinicalStatement (" + externalVMR.getClass().getSimpleName() + ").";
//			logger.error(errStr);
//			throw new ImproperUsageException(errStr);
//		}
		
		logger.trace(_METHODNAME + internalVMR.getClass().getSimpleName() + ", " + ((ClinicalStatement)internalVMR).getId() + ": " + ((ClinicalStatement)internalVMR).getEvaluatedPersonId());

		org.opencds.vmr.v1_0.schema.RelatedClinicalStatement relatedClinicalStatement = null; 
		
		if ("AdverseEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.AdverseEvent nestedClinicalStatement = AdverseEventMapper.pushOut(
//					(AdverseEvent)internalVMR, (org.opencds.vmr.v1_0.schema.AdverseEventnew org.opencds.vmr.v1_0.schema, organizedResults);
					(AdverseEvent)internalVMR, new org.opencds.vmr.v1_0.schema.AdverseEvent(), organizedResults);
			relatedClinicalStatement.setAdverseEvent(nestedClinicalStatement);
		} else if ("DeniedAdverseEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.DeniedAdverseEvent nestedClinicalStatement = DeniedAdverseEventMapper.pushOut(
					(DeniedAdverseEvent)internalVMR, new org.opencds.vmr.v1_0.schema.DeniedAdverseEvent(), organizedResults);
			relatedClinicalStatement.setDeniedAdverseEvent(nestedClinicalStatement);
		} else if ("EncounterEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.EncounterEvent nestedClinicalStatement = EncounterEventMapper.pushOut(
					(EncounterEvent)internalVMR, new org.opencds.vmr.v1_0.schema.EncounterEvent(), organizedResults);
			relatedClinicalStatement.setEncounterEvent(nestedClinicalStatement);
		} else if ("AppointmentProposal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.AppointmentProposal nestedClinicalStatement = AppointmentProposalMapper.pushOut(
					(AppointmentProposal)internalVMR, new org.opencds.vmr.v1_0.schema.AppointmentProposal(), organizedResults);
			relatedClinicalStatement.setAppointmentProposal(nestedClinicalStatement);
		} else if ("AppointmentRequest".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.AppointmentRequest nestedClinicalStatement = AppointmentRequestMapper.pushOut(
					(AppointmentRequest)internalVMR, new org.opencds.vmr.v1_0.schema.AppointmentRequest(), organizedResults);
			relatedClinicalStatement.setAppointmentRequest(nestedClinicalStatement);
		} else if ("MissedAppointment".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.MissedAppointment nestedClinicalStatement = MissedAppointmentMapper.pushOut(
					(MissedAppointment)internalVMR, new org.opencds.vmr.v1_0.schema.MissedAppointment(), organizedResults);
			relatedClinicalStatement.setMissedAppointment(nestedClinicalStatement);
		} else if ("ScheduledAppointment".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ScheduledAppointment nestedClinicalStatement = ScheduledAppointmentMapper.pushOut(
					(ScheduledAppointment)internalVMR, new org.opencds.vmr.v1_0.schema.ScheduledAppointment(), organizedResults);
			relatedClinicalStatement.setScheduledAppointment(nestedClinicalStatement);
		} else if ("Goal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.Goal nestedClinicalStatement = GoalMapper.pushOut(
					(Goal)internalVMR, new org.opencds.vmr.v1_0.schema.Goal(), organizedResults);
			relatedClinicalStatement.setGoal(nestedClinicalStatement);
		} else if ("GoalProposal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.GoalProposal nestedClinicalStatement = GoalProposalMapper.pushOut(
					(GoalProposal)internalVMR, new org.opencds.vmr.v1_0.schema.GoalProposal(), organizedResults);
			relatedClinicalStatement.setGoalProposal(nestedClinicalStatement);
		} else if ("ObservationOrder".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ObservationOrder nestedClinicalStatement = ObservationOrderMapper.pushOut(
					(ObservationOrder)internalVMR, new org.opencds.vmr.v1_0.schema.ObservationOrder(), organizedResults);
			relatedClinicalStatement.setObservationOrder(nestedClinicalStatement);
		} else if ("ObservationProposal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ObservationProposal nestedClinicalStatement = ObservationProposalMapper.pushOut(
					(ObservationProposal)internalVMR, new org.opencds.vmr.v1_0.schema.ObservationProposal(), organizedResults);
			relatedClinicalStatement.setObservationProposal(nestedClinicalStatement);
		} else if ("ObservationResult".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ObservationResult nestedClinicalStatement = ObservationResultMapper.pushOut(
					(ObservationResult)internalVMR, new org.opencds.vmr.v1_0.schema.ObservationResult(), organizedResults);
			relatedClinicalStatement.setObservationResult(nestedClinicalStatement);
		} else if ("UnconductedObservation".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.UnconductedObservation nestedClinicalStatement = UnconductedObservationMapper.pushOut(
					(UnconductedObservation)internalVMR, new org.opencds.vmr.v1_0.schema.UnconductedObservation(), organizedResults);
			relatedClinicalStatement.setUnconductedObservation(nestedClinicalStatement);
		} else if ("Problem".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.Problem nestedClinicalStatement = ProblemMapper.pushOut(
					(Problem)internalVMR, new org.opencds.vmr.v1_0.schema.Problem(), organizedResults);
			relatedClinicalStatement.setProblem(nestedClinicalStatement);
		} else if ("ProcedureEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ProcedureEvent nestedClinicalStatement = ProcedureEventMapper.pushOut(
					(ProcedureEvent)internalVMR, new org.opencds.vmr.v1_0.schema.ProcedureEvent(), organizedResults);
			relatedClinicalStatement.setProcedureEvent(nestedClinicalStatement);
		} else if ("ProcedureOrder".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ProcedureOrder nestedClinicalStatement = ProcedureOrderMapper.pushOut(
					(ProcedureOrder)internalVMR, new org.opencds.vmr.v1_0.schema.ProcedureOrder(), organizedResults);
			relatedClinicalStatement.setProcedureOrder(nestedClinicalStatement);
		} else if ("ProcedureProposal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.ProcedureProposal nestedClinicalStatement = ProcedureProposalMapper.pushOut(
					(ProcedureProposal)internalVMR, new org.opencds.vmr.v1_0.schema.ProcedureProposal(), organizedResults);
			relatedClinicalStatement.setProcedureProposal(nestedClinicalStatement);
		} else if ("UndeliveredProcedure".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.UndeliveredProcedure nestedClinicalStatement = UndeliveredProcedureMapper.pushOut(
					(UndeliveredProcedure)internalVMR, new org.opencds.vmr.v1_0.schema.UndeliveredProcedure(), organizedResults);
			relatedClinicalStatement.setUndeliveredProcedure(nestedClinicalStatement);
		} else if ("SubstanceAdministrationEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent nestedClinicalStatement = SubstanceAdministrationEventMapper.pushOut(
					(SubstanceAdministrationEvent)internalVMR, new org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent(), organizedResults);
			relatedClinicalStatement.setSubstanceAdministrationEvent(nestedClinicalStatement);
		} else if ("SubstanceAdministrationOrder".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder nestedClinicalStatement = SubstanceAdministrationOrderMapper.pushOut(
					(SubstanceAdministrationOrder)internalVMR, new org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder(), organizedResults);
			relatedClinicalStatement.setSubstanceAdministrationOrder(nestedClinicalStatement);
		} else if ("SubstanceAdministrationProposal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal nestedClinicalStatement = SubstanceAdministrationProposalMapper.pushOut(
					(SubstanceAdministrationProposal)internalVMR, new org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal(), organizedResults);
			relatedClinicalStatement.setSubstanceAdministrationProposal(nestedClinicalStatement);
		} else if ("UndeliveredSubstanceAdministration".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration nestedClinicalStatement = UndeliveredSubstanceAdministrationMapper.pushOut(
					(UndeliveredSubstanceAdministration)internalVMR, new org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration(), organizedResults);
			relatedClinicalStatement.setUndeliveredSubstanceAdministration(nestedClinicalStatement);
		} else if ("SubstanceDispensationEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent nestedClinicalStatement = SubstanceDispensationEventMapper.pushOut(
					(SubstanceDispensationEvent)internalVMR, new org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent(), organizedResults);
			relatedClinicalStatement.setSubstanceDispensationEvent(nestedClinicalStatement);
		} else if ("SupplyEvent".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SupplyEvent nestedClinicalStatement = SupplyEventMapper.pushOut(
					(SupplyEvent)internalVMR, new org.opencds.vmr.v1_0.schema.SupplyEvent(), organizedResults);
			relatedClinicalStatement.setSupplyEvent(nestedClinicalStatement);
		} else if ("SupplyOrder".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SupplyOrder nestedClinicalStatement = SupplyOrderMapper.pushOut(
					(SupplyOrder)internalVMR, new org.opencds.vmr.v1_0.schema.SupplyOrder(), organizedResults);
			relatedClinicalStatement.setSupplyOrder(nestedClinicalStatement);
		} else if ("SupplyProposal".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.SupplyProposal nestedClinicalStatement = SupplyProposalMapper.pushOut(
					(SupplyProposal)internalVMR, new org.opencds.vmr.v1_0.schema.SupplyProposal(), organizedResults);
			relatedClinicalStatement.setSupplyProposal(nestedClinicalStatement);
		} else if ("UndeliveredSupply".equals(internalVMR.getClass().getSimpleName())) {
			relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
			org.opencds.vmr.v1_0.schema.UndeliveredSupply nestedClinicalStatement = UndeliveredSupplyMapper.pushOut(
					(UndeliveredSupply)internalVMR, new org.opencds.vmr.v1_0.schema.UndeliveredSupply(), organizedResults);
			relatedClinicalStatement.setUndeliveredSupply(nestedClinicalStatement);
		}		
		
		return relatedClinicalStatement;
	}
	
	
	/**
	 * pushOutRelatedEntityToClinicalStatement recursively creates nested entities within output 
	 * clinical statements, which may themselves be nested.
	 * 
	 * This is most commonly used to specify components of a specimen or administrable substance, 
	 * or to associate providers with an organization and/or a facility.
	 * 
	 * Note that this method does not handle EvaluatedPersonRelationships, which are separately handled in BuildVMRSchemaResult
	 * 
	 * @param internal
	 * @param external
	 * @param organizedResults
	 * @return
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static org.opencds.vmr.v1_0.schema.RelatedEntity pushOutRelatedEntityToClinicalStatement(
			EntityRelationship								internal, 
			org.opencds.vmr.v1_0.schema.ClinicalStatement	external,  
			OrganizedResults								organizedResults
		) throws ImproperUsageException, DataFormatException, InvalidDataException {
		
		String _METHODNAME = "pushOutRelatedEntityToClinicalStatement(): "; 
		
		if (internal == null) {
			String errStr = _METHODNAME + "improper usage: source supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		if (external == null) {
			String errStr = _METHODNAME + "improper usage: target supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		Object oneInternalEntityObject = organizedResults.getEntityList().get( ((EntityRelationship)internal).getTargetEntityId() );
		if (oneInternalEntityObject == null) {
			String errStr = _METHODNAME + "improper usage: looking up entity Object for ID=" + ((EntityRelationship)internal).getTargetEntityId() + ", and it does not exist.";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
		}
		
		logger.trace(_METHODNAME + "push out " + oneInternalEntityObject.getClass().getSimpleName() + ", sourceEntity Id " + internal.getSourceId()
				+ ", targetEntityId " + internal.getTargetEntityId() 
				+ ", relationshipTimeInterval " + internal.getRelationshipTimeInterval()
				+ ", with relationship " + internal.getTargetRole().toString());
		
		if (internal.getSourceId().equals(internal.getTargetEntityId())) {
            throw new OpenCDSRuntimeException(
                    "root and/or extension of source and target IDs of the relationship may not be the same: source (root^extension)= " + internal.getSourceId()
                            + ", target (root^extension)= " + internal.getTargetEntityId());
		}
		
		 if ("AdministrableSubstance".equals(oneInternalEntityObject.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance	 	schemaNestedEntity 		= new org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance();
			AdministrableSubstanceMapper.pushOut(
					(AdministrableSubstance)organizedResults.getEntityList().get(internal.getTargetEntityId()), 
					schemaNestedEntity, 
					organizedResults);
			
			org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
			schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));	
			schemaRelatedEntity.setAdministrableSubstance(schemaNestedEntity);
			return schemaRelatedEntity;
				
		} else if ("Entity".equals(oneInternalEntityObject.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.RelatedEntity.Entity	 	schemaNestedEntity 		= new org.opencds.vmr.v1_0.schema.RelatedEntity.Entity();
			EntityMapper.pushOut(
					(Entity)organizedResults.getEntityList().get(internal.getTargetEntityId()), 
					schemaNestedEntity, 
					organizedResults);
			
			org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
			schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));	
			schemaRelatedEntity.setEntity(schemaNestedEntity);
			return schemaRelatedEntity;
				
		} else if ("Facility".equals(oneInternalEntityObject.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.RelatedEntity.Facility	 	schemaNestedEntity 		= new org.opencds.vmr.v1_0.schema.RelatedEntity.Facility();
			FacilityMapper.pushOut(
					(Facility)organizedResults.getEntityList().get(internal.getTargetEntityId()), 
					schemaNestedEntity, 
					organizedResults);
			
			org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
			schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));	
			schemaRelatedEntity.setFacility(schemaNestedEntity);
			return schemaRelatedEntity;
				
		} else if ("Organization".equals(oneInternalEntityObject.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.RelatedEntity.Organization	 	schemaNestedEntity 		= new org.opencds.vmr.v1_0.schema.RelatedEntity.Organization();
			OrganizationMapper.pushOut(
					(Organization)organizedResults.getEntityList().get(internal.getTargetEntityId()), 
					schemaNestedEntity, 
					organizedResults);
			
			org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
			schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));	
			schemaRelatedEntity.setOrganization(schemaNestedEntity);
			return schemaRelatedEntity;
				
		} else if ("Person".equals(oneInternalEntityObject.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.RelatedEntity.Person	 	schemaNestedEntity 		= new org.opencds.vmr.v1_0.schema.RelatedEntity.Person();
			PersonMapper.pushOut(
					(Person)organizedResults.getEntityList().get(internal.getTargetEntityId()), 
					schemaNestedEntity, 
					organizedResults);
			
			org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
			schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));	
			schemaRelatedEntity.setPerson(schemaNestedEntity);
			return schemaRelatedEntity;
				
		} else if ("Specimen".equals(oneInternalEntityObject.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen	 	schemaNestedEntity 		= new org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen();
			SpecimenMapper.pushOut(
					(Specimen)organizedResults.getEntityList().get(internal.getTargetEntityId()), 
					schemaNestedEntity, 
					organizedResults);
			
			org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
			schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));	
			schemaRelatedEntity.setSpecimen(schemaNestedEntity);
			return schemaRelatedEntity;
				
		} else {
			String errStr = _METHODNAME + "improper usage: listedEntity not recognized: " + oneInternalEntityObject.getClass().getSimpleName();
			logger.error(errStr);
			throw new InvalidDataException(errStr);
		}
	}
}
