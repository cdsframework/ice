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
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.ClinicalStatement;
import org.opencds.vmr.v1_0.internal.Entity;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.internal.Facility;
import org.opencds.vmr.v1_0.internal.Organization;
import org.opencds.vmr.v1_0.internal.Person;
import org.opencds.vmr.v1_0.internal.RelationshipToSource;
import org.opencds.vmr.v1_0.internal.Specimen;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.out.structures.OrganizedResults;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * @author David Shields
 * 
 * @date
 *
 */
public abstract class NestedObjectsMapper extends Object {

	private static Log logger = LogFactory.getLog(NestedObjectsMapper.class);
	
	/**
	 * Pull in the lists of RelatedEntities and RelatedClinicalStatements found in each source 
	 * 		ClinicalStatement.  Note that any found relatedEntity and relatedClinicalStatements 
	 * 		are stored in factLists.
	 * This routine is recursively invoked for every further clinical statement found nested within
	 * 		this clinical statement.  Any related entities found nested within this clinical
	 * 		statement will recursively invoke the method pullInRelatedEntityNestedObjects.
	 * 
	 * @param <T>
	 * @param externalSchemaSourceClinicalStatement
	 * @param parentId
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static <T extends org.opencds.vmr.v1_0.schema.ClinicalStatement> void pullInClinicalStatementNestedObjects(
			T						externalSchemaSourceClinicalStatement, 
			String					parentId, 
			String					subjectPersonId, 
			String					focalPersonId, 
			FactLists 				factLists) 
		throws ImproperUsageException, DataFormatException, InvalidDataException
	{
		String _METHODNAME = "pullInClinicalStatementNestedObjects: ";
		
		if ( "AdverseEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.AdverseEvent typedSource = (org.opencds.vmr.v1_0.schema.AdverseEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "DeniedAdverseEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.DeniedAdverseEvent typedSource = (org.opencds.vmr.v1_0.schema.DeniedAdverseEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "AppointmentProposal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.AppointmentProposal typedSource = (org.opencds.vmr.v1_0.schema.AppointmentProposal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "AppointmentRequest".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.AppointmentRequest typedSource = (org.opencds.vmr.v1_0.schema.AppointmentRequest)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "EncounterEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.EncounterEvent typedSource = (org.opencds.vmr.v1_0.schema.EncounterEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "MissedAppointment".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.MissedAppointment typedSource = (org.opencds.vmr.v1_0.schema.MissedAppointment)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ScheduledAppointment".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ScheduledAppointment typedSource = (org.opencds.vmr.v1_0.schema.ScheduledAppointment)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "Goal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Goal typedSource = (org.opencds.vmr.v1_0.schema.Goal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "GoalProposal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.GoalProposal typedSource = (org.opencds.vmr.v1_0.schema.GoalProposal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ObservationOrder".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ObservationOrder typedSource = (org.opencds.vmr.v1_0.schema.ObservationOrder)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ObservationProposal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ObservationProposal typedSource = (org.opencds.vmr.v1_0.schema.ObservationProposal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ObservationResult".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ObservationResult typedSource = (org.opencds.vmr.v1_0.schema.ObservationResult)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "UnconductedObservation".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.UnconductedObservation typedSource = (org.opencds.vmr.v1_0.schema.UnconductedObservation)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "DeniedProblem".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.DeniedProblem typedSource = (org.opencds.vmr.v1_0.schema.DeniedProblem)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "Problem".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Problem typedSource = (org.opencds.vmr.v1_0.schema.Problem)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ProcedureEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ProcedureEvent typedSource = (org.opencds.vmr.v1_0.schema.ProcedureEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ProcedureOrder".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ProcedureOrder typedSource = (org.opencds.vmr.v1_0.schema.ProcedureOrder)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ProcedureProposal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ProcedureProposal typedSource = (org.opencds.vmr.v1_0.schema.ProcedureProposal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "ScheduledProcedure".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.ScheduledProcedure typedSource = (org.opencds.vmr.v1_0.schema.ScheduledProcedure)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "UndeliveredProcedure".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.UndeliveredProcedure typedSource = (org.opencds.vmr.v1_0.schema.UndeliveredProcedure)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SubstanceAdministrationEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent typedSource = (org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SubstanceAdministrationOrder".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder typedSource = (org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SubstanceAdministrationProposal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal typedSource = (org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SubstanceDispensationEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent typedSource = (org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "UndeliveredSubstanceAdministration".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration typedSource = (org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SupplyEvent".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SupplyEvent typedSource = (org.opencds.vmr.v1_0.schema.SupplyEvent)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SupplyOrder".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SupplyOrder typedSource = (org.opencds.vmr.v1_0.schema.SupplyOrder)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "SupplyProposal".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.SupplyProposal typedSource = (org.opencds.vmr.v1_0.schema.SupplyProposal)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if( "UndeliveredSupply".equals(externalSchemaSourceClinicalStatement.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.UndeliveredSupply typedSource = (org.opencds.vmr.v1_0.schema.UndeliveredSupply)externalSchemaSourceClinicalStatement;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
			if (typedSource.getRelatedClinicalStatement() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement()) {
					OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId, focalPersonId, factLists);
				}
			}
		} else {
			throw new InvalidDataException(_METHODNAME + "Unrecognized class: " + externalSchemaSourceClinicalStatement.getClass().getSimpleName());
		}
		
		return;
	}
		
	
	/**
	 * Pull in the list of RelatedEntities possibly found in each source entity.  Note that any 
	 * 		found related Entities are stored in the factLists.  Note also that a related entity 
	 * 		may have been originally imbedded directly in an evaluatedPerson (including the patient), 
	 * 		directly in a clinical statement, or as a further nested component of a related entity
	 * 		in either of the above locations.
	 * This routine is recursively invoked for every further related entity found nested within
	 * 		this related entity.  
	 * 
	 * @param <T>
	 * @param external
	 * @param parentId
	 * @param subjectPersonId
	 * @param focalPersonId
	 * @param factLists
	 * @throws ImproperUsageException
	 * @throws DataFormatException
	 * @throws InvalidDataException
	 */
	public static <T extends org.opencds.vmr.v1_0.schema.EntityBase> void pullInRelatedEntityNestedObjects(
			T						external, 
			String					parentId, 
			String					subjectPersonId, 
			String					focalPersonId, 
			FactLists 				factLists) 
		throws ImproperUsageException, DataFormatException, InvalidDataException
	{
		String _METHODNAME = "pullInRelatedEntityNestedObjects: ";		
        logger.trace(_METHODNAME + external.getClass().getSimpleName() + ", " + parentId);

        if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
			org.opencds.vmr.v1_0.schema.AdministrableSubstance typedSource = (org.opencds.vmr.v1_0.schema.AdministrableSubstance)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if ( "Entity".equals(external.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Entity typedSource = (org.opencds.vmr.v1_0.schema.Entity)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if ( "EvaluatedPerson".equals(external.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.EvaluatedPerson typedSource = (org.opencds.vmr.v1_0.schema.EvaluatedPerson)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if ( "Facility".equals(external.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Facility typedSource = (org.opencds.vmr.v1_0.schema.Facility)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if ( "Organization".equals(external.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Organization typedSource = (org.opencds.vmr.v1_0.schema.Organization)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if ( "Person".equals(external.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Person typedSource = (org.opencds.vmr.v1_0.schema.Person)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else if ( "Specimen".equals(external.getClass().getSimpleName()) ) {
			org.opencds.vmr.v1_0.schema.Specimen typedSource = (org.opencds.vmr.v1_0.schema.Specimen)external;
			if (typedSource.getRelatedEntity() != null) {
				for (org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity()) {
					OneObjectMapper.pullInRelatedEntity(
							oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(), subjectPersonId, focalPersonId, factLists);
				}
			}
		} else {
			throw new InvalidDataException(_METHODNAME + "Unrecognized class: " + external.getClass().getSimpleName());
		}
		
		return;
	}
		
	
	public static <T extends org.opencds.vmr.v1_0.schema.ClinicalStatement> T pushOutClinicalStatementNestedObjects(
			ClinicalStatement 	source, 
			T 					target, 
			OrganizedResults 	organizedResults) 
		throws ImproperUsageException, DataFormatException, InvalidDataException
	{
		
		String _METHODNAME = "pushOutClinicalStatementNestedObjects(): ";
		
		//look for related entities to this clinical statement
		if ( organizedResults.getEntityChildren().get(source.getId()) != null ) 
		{
			logger.trace(_METHODNAME + "Entity children of " + source.getId());
			for ( EntityRelationship oneInternalEntityRelationship : organizedResults.getEntityChildren().get(source.getId())) {
				/*
				 * oneInternalEntityRelationship is a member of the List of EntityRelationships 
				 * 		that are nested in this one source clinicalStatement.  Note that the nested 
				 * 		entity may contain its own list of nested related entities as well...
				 *  The following line of code populates the nested entity, and will recursively 
				 *  	populate any further nested entities within the oneInternalEntityRelationship.
				 */
				logger.trace(_METHODNAME + "push out source Entity or Clinical Statement Id " + oneInternalEntityRelationship.getSourceId()
						+ ", targetEntityId " + oneInternalEntityRelationship.getTargetEntityId() 
						+ ", with relationship " + oneInternalEntityRelationship.getTargetRole().toString());
				org.opencds.vmr.v1_0.schema.RelatedEntity oneSchemaNestedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				oneSchemaNestedEntity = OneObjectMapper.pushOutRelatedEntityToClinicalStatement(oneInternalEntityRelationship, target, organizedResults);
				
				if (oneSchemaNestedEntity != null) {
					/*
					 * Store the completed nested related entity tree back into 
					 * 		the source clinical statement as a relatedClinicalStatement.
					 */
					if ( "AdverseEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.AdverseEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "DeniedAdverseEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.DeniedAdverseEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "AppointmentProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.AppointmentProposal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "AppointmentRequest".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.AppointmentRequest)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "EncounterEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.EncounterEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "MissedAppointment".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.MissedAppointment)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ScheduledAppointment".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ScheduledAppointment)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "Goal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.Goal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "GoalProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.GoalProposal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ObservationOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ObservationOrder)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ObservationProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ObservationProposal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ObservationResult".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ObservationResult)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "UnconductedObservation".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UnconductedObservation)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "DeniedProblem".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.DeniedProblem)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "Problem".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.Problem)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ProcedureEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ProcedureEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ProcedureOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ProcedureOrder)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ProcedureProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ProcedureProposal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "ScheduledProcedure".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ScheduledProcedure)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "UndeliveredProcedure".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UndeliveredProcedure)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SubstanceAdministrationEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SubstanceAdministrationOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SubstanceAdministrationProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SubstanceDispensationEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "UndeliveredSubstanceAdministration".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SupplyEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SupplyEvent)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SupplyOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SupplyOrder)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "SupplyProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SupplyProposal)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else if( "UndeliveredSupply".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UndeliveredSupply)target).getRelatedEntity().add(oneSchemaNestedEntity);
					} else {
						throw new InvalidDataException("_METHOD_NAME + Unrecognized class: " + target.getClass().getSimpleName());
					}
				}
			}
		}
		
		//look for related clinical statements
		if ( organizedResults.getCsChildren().get(source.getId()) != null ) 
		{
			logger.trace(_METHODNAME + "Clinical Statement children of " + source.getId());
			for ( ClinicalStatement oneInternalRelatedClinicalStatement : organizedResults.getCsChildren().get(source.getId())) 
			{
				/*
				 * oneRelatedClinicalStatement is one of a possible list of nested clinical statements
				 * 		within the source clinical statement.  Note that the nested clinical statement
				 *  	may contain nested related entities as well...
				 *  The following line of code populates the nested clinical statement, and will
				 *  	recursively populate any further nested clinical statements and entities within
				 *  	the oneRelatedClinicalStatement.
				 */
				org.opencds.vmr.v1_0.schema.RelatedClinicalStatement nestedTarget = 				
					OneObjectMapper.pushOutRelatedClinicalStatement(oneInternalRelatedClinicalStatement, organizedResults);
				
				for ( RelationshipToSource oneRelationshipToSource : ((ClinicalStatement)oneInternalRelatedClinicalStatement).getRelationshipToSources() ) 
				{
					if (source.getId().equals(oneRelationshipToSource.getSourceId())) 
					{
						/*
						 * Update the nestedTarget relationship to the source clinical statement from the list of 
						 * 		imbedded targetRelationshipToSource codes
						 */
						nestedTarget.setTargetRelationshipToSource( MappingUtility.cDInternal2CD(oneRelationshipToSource.getRelationshipToSource()) );
						// TODO DC: Commenting this logger trace out - causes NullPointerException 
						// logger.trace(_METHODNAME + "push out Clinical Statement Id " + ((ClinicalStatement)oneInternalRelatedClinicalStatement).getId()
						//		+ ", class " + oneInternalRelatedClinicalStatement.getClass().getSimpleName() 
						//		+ ", with relationship " + oneRelationshipToSource.getRelationshipToSource().toString());
					}
				}
				if (nestedTarget != null) {
					/*
					 * Store the completed nested clinical statement tree back into 
					 * 		the source clinical statement as a relatedClinicalStatement.
					 */
					if ( "AdverseEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.AdverseEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "DeniedAdverseEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.DeniedAdverseEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "AppointmentProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.AppointmentProposal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "AppointmentRequest".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.AppointmentRequest)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "EncounterEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.EncounterEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "MissedAppointment".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.MissedAppointment)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ScheduledAppointment".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ScheduledAppointment)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "Goal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.Goal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "GoalProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.GoalProposal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ObservationOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ObservationOrder)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ObservationProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ObservationProposal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ObservationResult".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ObservationResult)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "UnconductedObservation".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UnconductedObservation)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "DeniedProblem".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.DeniedProblem)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "Problem".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.Problem)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ProcedureEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ProcedureEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ProcedureOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ProcedureOrder)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ProcedureProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ProcedureProposal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "ScheduledProcedure".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.ScheduledProcedure)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "UndeliveredProcedure".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UndeliveredProcedure)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SubstanceAdministrationEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SubstanceAdministrationOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SubstanceAdministrationProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SubstanceDispensationEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "UndeliveredSubstanceAdministration".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SupplyEvent".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SupplyEvent)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SupplyOrder".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SupplyOrder)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "SupplyProposal".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.SupplyProposal)target).getRelatedClinicalStatement().add(nestedTarget);
					} else if( "UndeliveredSupply".equals(target.getClass().getSimpleName()) ) {
						((org.opencds.vmr.v1_0.schema.UndeliveredSupply)target).getRelatedClinicalStatement().add(nestedTarget);
					} else {
						throw new InvalidDataException(_METHODNAME + "Unrecognized class: " + target.getClass().getSimpleName());
					}
				}
			}
		}
		
		return target;
	}
		
	
	public static <T extends org.opencds.vmr.v1_0.schema.EntityBase> T pushOutRelatedEntityNestedObjects(
			String										sourceId,
			T										    external,
			OrganizedResults 							organizedResults) 
		throws ImproperUsageException, DataFormatException, InvalidDataException
	{
		
		String _METHODNAME = "pushOutRelatedEntityNestedObjects(): ";
		
		//look for related entities to this entity
		if ( organizedResults.getEntityChildren().get(sourceId) == null ) {
			return null;
		} 
			
		logger.trace(_METHODNAME + "Entity children of " + sourceId);	
		for ( EntityRelationship oneInternalEntityRelationship : organizedResults.getEntityChildren().get(sourceId)) {
			/*
			 * At this point oneInternalEntityRelationship is one member of the List 
			 * 		of EntityRelationships that are nested in this one entity specified by sourceId
			 */
			String targetEntityId = oneInternalEntityRelationship.getTargetEntityId();
			
			logger.trace(_METHODNAME + "push out source Entity Id " + sourceId
					+ ", targetEntityId " + targetEntityId 
					+ ", with relationship " + oneInternalEntityRelationship.getTargetRole().toString());
			
			org.opencds.vmr.v1_0.schema.RelatedEntity oneSchemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
			
			oneSchemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
			oneSchemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
			
            EntityBase thisInternalNestedEntity = organizedResults.getEntityList().get(targetEntityId);

            if ("AdministrableSubstance".equals(thisInternalNestedEntity.getClass().getSimpleName())) {
				org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance schemaNestedEntity = 
					new org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance();
				
				AdministrableSubstanceMapper.pushOut(
						(AdministrableSubstance)organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()), 
						schemaNestedEntity, 
						organizedResults);
				
				org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
				schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));	
				schemaRelatedEntity.setAdministrableSubstance(schemaNestedEntity);
				
				if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
					if ("org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance".equals(external.getClass().getName())) {
						((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
					} else {
						((org.opencds.vmr.v1_0.schema.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
					}
				} else if ("Entity".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Entity)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Facility".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Facility)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Organization".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Organization)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Person".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Person)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Specimen".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Specimen)external).getRelatedEntity().add(schemaRelatedEntity);
				} else {
					throw new InvalidDataException("_METHOD_NAME + Unrecognized outerTarget class: " + external.getClass().getSimpleName());
				}
				
			} else if ( "Entity".equals(thisInternalNestedEntity.getClass().getSimpleName()) ) {
				org.opencds.vmr.v1_0.schema.RelatedEntity.Entity schemaNestedEntity = 
					new org.opencds.vmr.v1_0.schema.RelatedEntity.Entity();
				
				EntityMapper.pushOut(
						(Entity)organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()), 
						schemaNestedEntity, 
						organizedResults);
				
				org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
				schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));	
				schemaRelatedEntity.setEntity(schemaNestedEntity);
				
				if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Entity".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Entity)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Facility".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Facility)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Organization".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Organization)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Person".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Person)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Specimen".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Specimen)external).getRelatedEntity().add(schemaRelatedEntity);
				} else {
					throw new InvalidDataException("_METHOD_NAME + Unrecognized outerTarget class: " + external.getClass().getSimpleName());
				}
				
			} else if ( "Facility".equals(thisInternalNestedEntity.getClass().getSimpleName()) ) {
				org.opencds.vmr.v1_0.schema.RelatedEntity.Facility schemaNestedEntity = 
					new org.opencds.vmr.v1_0.schema.RelatedEntity.Facility();
				
				FacilityMapper.pushOut(
						(Facility)organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()), 
						schemaNestedEntity, 
						organizedResults);
				
				org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
				schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));	
				schemaRelatedEntity.setFacility(schemaNestedEntity);
				
				if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Entity".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Entity)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Facility".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Facility)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Organization".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Organization)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Person".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Person)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Specimen".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Specimen)external).getRelatedEntity().add(schemaRelatedEntity);
				} else {
					throw new InvalidDataException("_METHOD_NAME + Unrecognized outerTarget class: " + external.getClass().getSimpleName());
				}
				
			} else if ( "Organization".equals(thisInternalNestedEntity.getClass().getSimpleName()) ) {
				org.opencds.vmr.v1_0.schema.RelatedEntity.Organization schemaNestedEntity = 
					new org.opencds.vmr.v1_0.schema.RelatedEntity.Organization();
				
				OrganizationMapper.pushOut(
						(Organization)organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()), 
						schemaNestedEntity, 
						organizedResults);
				
				org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
				schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));	
				schemaRelatedEntity.setOrganization(schemaNestedEntity);
				
				if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Entity".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Entity)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Facility".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Facility)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Organization".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Organization)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Person".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Person)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Specimen".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Specimen)external).getRelatedEntity().add(schemaRelatedEntity);
				} else {
					throw new InvalidDataException("_METHOD_NAME + Unrecognized outerTarget class: " + external.getClass().getSimpleName());
				}
				
			} else if ( "Person".equals(thisInternalNestedEntity.getClass().getSimpleName()) ) {
				org.opencds.vmr.v1_0.schema.RelatedEntity.Person schemaNestedEntity = 
					new org.opencds.vmr.v1_0.schema.RelatedEntity.Person();
				
				PersonMapper.pushOut(
						(Person)organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()), 
						schemaNestedEntity, 
						organizedResults);
				
				org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
				schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));	
				schemaRelatedEntity.setPerson(schemaNestedEntity);
				
				if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Entity".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Entity)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Facility".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Facility)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Organization".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Organization)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Person".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Person)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Specimen".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Specimen)external).getRelatedEntity().add(schemaRelatedEntity);
				} else {
					throw new InvalidDataException("_METHOD_NAME + Unrecognized outerTarget class: " + external.getClass().getSimpleName());
				}
				
			} else if ( "Specimen".equals(thisInternalNestedEntity.getClass().getSimpleName()) ) {
				org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen schemaNestedEntity = 
					new org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen();
				
				SpecimenMapper.pushOut(
						(Specimen)organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()), 
						schemaNestedEntity, 
						organizedResults);
				
				org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity = new org.opencds.vmr.v1_0.schema.RelatedEntity();
				
				schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
				schemaRelatedEntity.setRelationshipTimeInterval(MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));	
				schemaRelatedEntity.setSpecimen(schemaNestedEntity);
				
				if ("AdministrableSubstance".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Entity".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Entity)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Facility".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Facility)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Organization".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Organization)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Person".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Person)external).getRelatedEntity().add(schemaRelatedEntity);
				} else if ("Specimen".equals(external.getClass().getSimpleName())) {
					((org.opencds.vmr.v1_0.schema.Specimen)external).getRelatedEntity().add(schemaRelatedEntity);
				} else {
					throw new InvalidDataException("_METHOD_NAME + Unrecognized outerTarget class: " + external.getClass().getSimpleName());
				}
								
			} else {
				throw new InvalidDataException("_METHOD_NAME + Unrecognized class: " + thisInternalNestedEntity.getClass().getSimpleName());
			}
		}
		
		return external;

	}
}
