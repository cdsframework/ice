package org.opencds.vmr.v1_0.mappings.mappers;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NestedObjectsMapper
{
    public static <T extends org.opencds.vmr.v1_0.schema.ClinicalStatement> void pullInClinicalStatementNestedObjects(
            final T externalSchemaSourceClinicalStatement, final String parentId, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullInClinicalStatementNestedObjects: ";

        final String statementClassName = externalSchemaSourceClinicalStatement.getClass().getSimpleName();
        switch (statementClassName)
        {
            case "AdverseEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.AdverseEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.AdverseEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "DeniedAdverseEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.DeniedAdverseEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.DeniedAdverseEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "AppointmentProposal" ->
            {
                final org.opencds.vmr.v1_0.schema.AppointmentProposal typedSource =
                        (org.opencds.vmr.v1_0.schema.AppointmentProposal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "AppointmentRequest" ->
            {
                final org.opencds.vmr.v1_0.schema.AppointmentRequest typedSource =
                        (org.opencds.vmr.v1_0.schema.AppointmentRequest) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "EncounterEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.EncounterEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.EncounterEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "MissedAppointment" ->
            {
                final org.opencds.vmr.v1_0.schema.MissedAppointment typedSource =
                        (org.opencds.vmr.v1_0.schema.MissedAppointment) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ScheduledAppointment" ->
            {
                final org.opencds.vmr.v1_0.schema.ScheduledAppointment typedSource =
                        (org.opencds.vmr.v1_0.schema.ScheduledAppointment) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "Goal" ->
            {
                final org.opencds.vmr.v1_0.schema.Goal typedSource =
                        (org.opencds.vmr.v1_0.schema.Goal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "GoalProposal" ->
            {
                final org.opencds.vmr.v1_0.schema.GoalProposal typedSource =
                        (org.opencds.vmr.v1_0.schema.GoalProposal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ObservationOrder" ->
            {
                final org.opencds.vmr.v1_0.schema.ObservationOrder typedSource =
                        (org.opencds.vmr.v1_0.schema.ObservationOrder) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ObservationProposal" ->
            {
                final org.opencds.vmr.v1_0.schema.ObservationProposal typedSource =
                        (org.opencds.vmr.v1_0.schema.ObservationProposal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ObservationResult" ->
            {
                final org.opencds.vmr.v1_0.schema.ObservationResult typedSource =
                        (org.opencds.vmr.v1_0.schema.ObservationResult) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "UnconductedObservation" ->
            {
                final org.opencds.vmr.v1_0.schema.UnconductedObservation typedSource =
                        (org.opencds.vmr.v1_0.schema.UnconductedObservation) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "DeniedProblem" ->
            {
                final org.opencds.vmr.v1_0.schema.DeniedProblem typedSource =
                        (org.opencds.vmr.v1_0.schema.DeniedProblem) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "Problem" ->
            {
                final org.opencds.vmr.v1_0.schema.Problem typedSource =
                        (org.opencds.vmr.v1_0.schema.Problem) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ProcedureEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.ProcedureEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.ProcedureEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ProcedureOrder" ->
            {
                final org.opencds.vmr.v1_0.schema.ProcedureOrder typedSource =
                        (org.opencds.vmr.v1_0.schema.ProcedureOrder) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ProcedureProposal" ->
            {
                final org.opencds.vmr.v1_0.schema.ProcedureProposal typedSource =
                        (org.opencds.vmr.v1_0.schema.ProcedureProposal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "ScheduledProcedure" ->
            {
                final org.opencds.vmr.v1_0.schema.ScheduledProcedure typedSource =
                        (org.opencds.vmr.v1_0.schema.ScheduledProcedure) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "UndeliveredProcedure" ->
            {
                final org.opencds.vmr.v1_0.schema.UndeliveredProcedure typedSource =
                        (org.opencds.vmr.v1_0.schema.UndeliveredProcedure) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SubstanceAdministrationEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SubstanceAdministrationOrder" ->
            {
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder typedSource =
                        (org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SubstanceAdministrationProposal" ->
            {
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal typedSource =
                        (org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SubstanceDispensationEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "UndeliveredSubstanceAdministration" ->
            {
                final org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration typedSource =
                        (org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SupplyEvent" ->
            {
                final org.opencds.vmr.v1_0.schema.SupplyEvent typedSource =
                        (org.opencds.vmr.v1_0.schema.SupplyEvent) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SupplyOrder" ->
            {
                final org.opencds.vmr.v1_0.schema.SupplyOrder typedSource =
                        (org.opencds.vmr.v1_0.schema.SupplyOrder) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "SupplyProposal" ->
            {
                final org.opencds.vmr.v1_0.schema.SupplyProposal typedSource =
                        (org.opencds.vmr.v1_0.schema.SupplyProposal) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            case "UndeliveredSupply" ->
            {
                final org.opencds.vmr.v1_0.schema.UndeliveredSupply typedSource =
                        (org.opencds.vmr.v1_0.schema.UndeliveredSupply) externalSchemaSourceClinicalStatement;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
                if (typedSource.getRelatedClinicalStatement() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement oneRelatedClinicalStatement : typedSource.getRelatedClinicalStatement())
                    {
                        OneObjectMapper.pullInRelatedClinicalStatement(oneRelatedClinicalStatement, parentId, subjectPersonId,
                                focalPersonId, factLists);
                    }
                }
            }
            default -> throw new InvalidDataException(_METHODNAME + "Unrecognized class: " + statementClassName);
        }

    }

    public static <T extends org.opencds.vmr.v1_0.schema.EntityBase> void pullInRelatedEntityNestedObjects(final T external,
            final String parentId, final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullInRelatedEntityNestedObjects: ";
        final String externalClassName = external.getClass().getSimpleName();
        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", externalClassName, parentId);

        switch (externalClassName)
        {
            case "AdministrableSubstance" ->
            {
                final org.opencds.vmr.v1_0.schema.AdministrableSubstance typedSource =
                        (org.opencds.vmr.v1_0.schema.AdministrableSubstance) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            case "Entity" ->
            {
                final org.opencds.vmr.v1_0.schema.Entity typedSource = (org.opencds.vmr.v1_0.schema.Entity) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            case "EvaluatedPerson" ->
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson typedSource =
                        (org.opencds.vmr.v1_0.schema.EvaluatedPerson) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            case "Facility" ->
            {
                final org.opencds.vmr.v1_0.schema.Facility typedSource = (org.opencds.vmr.v1_0.schema.Facility) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            case "Organization" ->
            {
                final org.opencds.vmr.v1_0.schema.Organization typedSource = (org.opencds.vmr.v1_0.schema.Organization) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            case "Person" ->
            {
                final org.opencds.vmr.v1_0.schema.Person typedSource = (org.opencds.vmr.v1_0.schema.Person) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            case "Specimen" ->
            {
                final org.opencds.vmr.v1_0.schema.Specimen typedSource = (org.opencds.vmr.v1_0.schema.Specimen) external;
                if (typedSource.getRelatedEntity() != null)
                {
                    for (final org.opencds.vmr.v1_0.schema.RelatedEntity oneRelatedEntity : typedSource.getRelatedEntity())
                    {
                        OneObjectMapper.pullInRelatedEntity(oneRelatedEntity, parentId, oneRelatedEntity.getTargetRole(),
                                subjectPersonId, focalPersonId, factLists);
                    }
                }
            }
            default -> throw new InvalidDataException(_METHODNAME + "Unrecognized class: " + externalClassName);
        }

    }

    public static <T extends org.opencds.vmr.v1_0.schema.ClinicalStatement> T pushOutClinicalStatementNestedObjects(
            final ClinicalStatement source, final T target, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOutClinicalStatementNestedObjects(): ";
        final String targetClassName = target.getClass().getSimpleName();

        if (organizedResults.getEntityChildren().get(source.getId()) != null)
        {
            if (log.isTraceEnabled())
                log.trace(_METHODNAME + "Entity children of {}", source.getId());
            for (final EntityRelationship oneInternalEntityRelationship : organizedResults.getEntityChildren().get(source.getId()))
            {
                if (log.isTraceEnabled())
                    log.trace(_METHODNAME
                                    + "push out source Entity or Clinical Statement Id {}, targetEntityId {}, with relationship {}",
                            oneInternalEntityRelationship.getSourceId(), oneInternalEntityRelationship.getTargetEntityId(),
                            oneInternalEntityRelationship.getTargetRole().toString());
                final org.opencds.vmr.v1_0.schema.RelatedEntity oneSchemaNestedEntity;

                oneSchemaNestedEntity =
                        OneObjectMapper.pushOutRelatedEntityToClinicalStatement(oneInternalEntityRelationship, target,
                                organizedResults);

                switch (targetClassName)
                {
                    case "AdverseEvent" ->
                            ((org.opencds.vmr.v1_0.schema.AdverseEvent) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "DeniedAdverseEvent" ->
                            ((org.opencds.vmr.v1_0.schema.DeniedAdverseEvent) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "AppointmentProposal" -> ((org.opencds.vmr.v1_0.schema.AppointmentProposal) target).getRelatedEntity()
                            .add(oneSchemaNestedEntity);
                    case "AppointmentRequest" ->
                            ((org.opencds.vmr.v1_0.schema.AppointmentRequest) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "EncounterEvent" ->
                            ((org.opencds.vmr.v1_0.schema.EncounterEvent) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "MissedAppointment" ->
                            ((org.opencds.vmr.v1_0.schema.MissedAppointment) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ScheduledAppointment" -> ((org.opencds.vmr.v1_0.schema.ScheduledAppointment) target).getRelatedEntity()
                            .add(oneSchemaNestedEntity);
                    case "Goal" -> ((org.opencds.vmr.v1_0.schema.Goal) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "GoalProposal" ->
                            ((org.opencds.vmr.v1_0.schema.GoalProposal) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ObservationOrder" ->
                            ((org.opencds.vmr.v1_0.schema.ObservationOrder) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ObservationProposal" -> ((org.opencds.vmr.v1_0.schema.ObservationProposal) target).getRelatedEntity()
                            .add(oneSchemaNestedEntity);
                    case "ObservationResult" ->
                            ((org.opencds.vmr.v1_0.schema.ObservationResult) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "UnconductedObservation" ->
                            ((org.opencds.vmr.v1_0.schema.UnconductedObservation) target).getRelatedEntity()
                                    .add(oneSchemaNestedEntity);
                    case "DeniedProblem" ->
                            ((org.opencds.vmr.v1_0.schema.DeniedProblem) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "Problem" -> ((org.opencds.vmr.v1_0.schema.Problem) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ProcedureEvent" ->
                            ((org.opencds.vmr.v1_0.schema.ProcedureEvent) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ProcedureOrder" ->
                            ((org.opencds.vmr.v1_0.schema.ProcedureOrder) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ProcedureProposal" ->
                            ((org.opencds.vmr.v1_0.schema.ProcedureProposal) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "ScheduledProcedure" ->
                            ((org.opencds.vmr.v1_0.schema.ScheduledProcedure) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "UndeliveredProcedure" -> ((org.opencds.vmr.v1_0.schema.UndeliveredProcedure) target).getRelatedEntity()
                            .add(oneSchemaNestedEntity);
                    case "SubstanceAdministrationEvent" ->
                            ((org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent) target).getRelatedEntity()
                                    .add(oneSchemaNestedEntity);
                    case "SubstanceAdministrationOrder" ->
                            ((org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder) target).getRelatedEntity()
                                    .add(oneSchemaNestedEntity);
                    case "SubstanceAdministrationProposal" ->
                            ((org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal) target).getRelatedEntity()
                                    .add(oneSchemaNestedEntity);
                    case "SubstanceDispensationEvent" ->
                            ((org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent) target).getRelatedEntity()
                                    .add(oneSchemaNestedEntity);
                    case "UndeliveredSubstanceAdministration" ->
                            ((org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration) target).getRelatedEntity()
                                    .add(oneSchemaNestedEntity);
                    case "SupplyEvent" ->
                            ((org.opencds.vmr.v1_0.schema.SupplyEvent) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "SupplyOrder" ->
                            ((org.opencds.vmr.v1_0.schema.SupplyOrder) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "SupplyProposal" ->
                            ((org.opencds.vmr.v1_0.schema.SupplyProposal) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    case "UndeliveredSupply" ->
                            ((org.opencds.vmr.v1_0.schema.UndeliveredSupply) target).getRelatedEntity().add(oneSchemaNestedEntity);
                    default -> throw new InvalidDataException("_METHOD_NAME + Unrecognized class: " + targetClassName);
                }
            }
        }

        if (organizedResults.getCsChildren().get(source.getId()) != null)
        {
            if (log.isTraceEnabled())
                log.trace(_METHODNAME + "Clinical Statement children of {}", source.getId());
            for (final ClinicalStatement oneInternalRelatedClinicalStatement : organizedResults.getCsChildren().get(source.getId()))
            {
                final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement nestedTarget =
                        OneObjectMapper.pushOutRelatedClinicalStatement(oneInternalRelatedClinicalStatement, organizedResults);

                for (final RelationshipToSource oneRelationshipToSource : oneInternalRelatedClinicalStatement.getRelationshipToSources())
                {
                    if (source.getId().equals(oneRelationshipToSource.getSourceId()))
                    {
                        nestedTarget.setTargetRelationshipToSource(
                                MappingUtility.cDInternal2CD(oneRelationshipToSource.getRelationshipToSource()));

                    }
                }
                if (nestedTarget != null)
                {
                    switch (targetClassName)
                    {
                        case "AdverseEvent" ->
                                ((org.opencds.vmr.v1_0.schema.AdverseEvent) target).getRelatedClinicalStatement().add(nestedTarget);
                        case "DeniedAdverseEvent" ->
                                ((org.opencds.vmr.v1_0.schema.DeniedAdverseEvent) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "AppointmentProposal" ->
                                ((org.opencds.vmr.v1_0.schema.AppointmentProposal) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "AppointmentRequest" ->
                                ((org.opencds.vmr.v1_0.schema.AppointmentRequest) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "EncounterEvent" -> ((org.opencds.vmr.v1_0.schema.EncounterEvent) target).getRelatedClinicalStatement()
                                .add(nestedTarget);
                        case "MissedAppointment" ->
                                ((org.opencds.vmr.v1_0.schema.MissedAppointment) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "ScheduledAppointment" ->
                                ((org.opencds.vmr.v1_0.schema.ScheduledAppointment) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "Goal" -> ((org.opencds.vmr.v1_0.schema.Goal) target).getRelatedClinicalStatement().add(nestedTarget);
                        case "GoalProposal" ->
                                ((org.opencds.vmr.v1_0.schema.GoalProposal) target).getRelatedClinicalStatement().add(nestedTarget);
                        case "ObservationOrder" ->
                                ((org.opencds.vmr.v1_0.schema.ObservationOrder) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "ObservationProposal" ->
                                ((org.opencds.vmr.v1_0.schema.ObservationProposal) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "ObservationResult" ->
                                ((org.opencds.vmr.v1_0.schema.ObservationResult) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "UnconductedObservation" ->
                                ((org.opencds.vmr.v1_0.schema.UnconductedObservation) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "DeniedProblem" -> ((org.opencds.vmr.v1_0.schema.DeniedProblem) target).getRelatedClinicalStatement()
                                .add(nestedTarget);
                        case "Problem" ->
                                ((org.opencds.vmr.v1_0.schema.Problem) target).getRelatedClinicalStatement().add(nestedTarget);
                        case "ProcedureEvent" -> ((org.opencds.vmr.v1_0.schema.ProcedureEvent) target).getRelatedClinicalStatement()
                                .add(nestedTarget);
                        case "ProcedureOrder" -> ((org.opencds.vmr.v1_0.schema.ProcedureOrder) target).getRelatedClinicalStatement()
                                .add(nestedTarget);
                        case "ProcedureProposal" ->
                                ((org.opencds.vmr.v1_0.schema.ProcedureProposal) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "ScheduledProcedure" ->
                                ((org.opencds.vmr.v1_0.schema.ScheduledProcedure) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "UndeliveredProcedure" ->
                                ((org.opencds.vmr.v1_0.schema.UndeliveredProcedure) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "SubstanceAdministrationEvent" ->
                                ((org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "SubstanceAdministrationOrder" ->
                                ((org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "SubstanceAdministrationProposal" ->
                                ((org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "SubstanceDispensationEvent" ->
                                ((org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "UndeliveredSubstanceAdministration" ->
                                ((org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        case "SupplyEvent" ->
                                ((org.opencds.vmr.v1_0.schema.SupplyEvent) target).getRelatedClinicalStatement().add(nestedTarget);
                        case "SupplyOrder" ->
                                ((org.opencds.vmr.v1_0.schema.SupplyOrder) target).getRelatedClinicalStatement().add(nestedTarget);
                        case "SupplyProposal" -> ((org.opencds.vmr.v1_0.schema.SupplyProposal) target).getRelatedClinicalStatement()
                                .add(nestedTarget);
                        case "UndeliveredSupply" ->
                                ((org.opencds.vmr.v1_0.schema.UndeliveredSupply) target).getRelatedClinicalStatement()
                                        .add(nestedTarget);
                        default -> throw new InvalidDataException(_METHODNAME + "Unrecognized class: " + targetClassName);
                    }
                }
            }
        }

        return target;
    }

    public static <T extends org.opencds.vmr.v1_0.schema.EntityBase> T pushOutRelatedEntityNestedObjects(final String sourceId,
            final T external, final OrganizedResults organizedResults)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOutRelatedEntityNestedObjects(): ";
        final String externalClassName = external.getClass().getSimpleName();

        if (organizedResults.getEntityChildren().get(sourceId) == null)
            return null;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "Entity children of {}", sourceId);
        for (final EntityRelationship oneInternalEntityRelationship : organizedResults.getEntityChildren().get(sourceId))
        {
            final String targetEntityId = oneInternalEntityRelationship.getTargetEntityId();

            if (log.isTraceEnabled())
                log.trace(_METHODNAME + "push out source Entity Id {}, targetEntityId {}, with relationship {}", sourceId,
                        targetEntityId, oneInternalEntityRelationship.getTargetRole().toString());

            final EntityBase thisInternalNestedEntity = organizedResults.getEntityList().get(targetEntityId);
            final String thisInternalNestedEntityClassName = thisInternalNestedEntity.getClass().getSimpleName();

            switch (thisInternalNestedEntityClassName)
            {
                case "AdministrableSubstance" ->
                {
                    final org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance schemaNestedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance();

                    AdministrableSubstanceMapper.pushOut((AdministrableSubstance) organizedResults.getEntityList()
                            .get(oneInternalEntityRelationship.getTargetEntityId()), schemaNestedEntity, organizedResults);

                    final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity();

                    schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
                    schemaRelatedEntity.setRelationshipTimeInterval(
                            MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
                    schemaRelatedEntity.setAdministrableSubstance(schemaNestedEntity);

                    switch (externalClassName)
                    {
                        case "AdministrableSubstance" ->
                        {
                            if ("org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance".equals(
                                    external.getClass().getName()))
                            {
                                ((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                            }
                            else
                            {
                                ((org.opencds.vmr.v1_0.schema.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                            }
                        }
                        case "Entity" ->
                                ((org.opencds.vmr.v1_0.schema.Entity) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Facility" ->
                                ((org.opencds.vmr.v1_0.schema.Facility) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Organization" ->
                                ((org.opencds.vmr.v1_0.schema.Organization) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Person" ->
                                ((org.opencds.vmr.v1_0.schema.Person) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Specimen" ->
                                ((org.opencds.vmr.v1_0.schema.Specimen) external).getRelatedEntity().add(schemaRelatedEntity);
                        default -> throw new InvalidDataException(
                                "_METHOD_NAME + Unrecognized outerTarget class: " + externalClassName);
                    }

                }
                case "Entity" ->
                {
                    final org.opencds.vmr.v1_0.schema.RelatedEntity.Entity schemaNestedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity.Entity();

                    EntityMapper.pushOut(
                            (Entity) organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()),
                            schemaNestedEntity, organizedResults);

                    final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity();

                    schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
                    schemaRelatedEntity.setRelationshipTimeInterval(
                            MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
                    schemaRelatedEntity.setEntity(schemaNestedEntity);

                    switch (externalClassName)
                    {
                        case "AdministrableSubstance" ->
                                ((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                        case "Entity" ->
                                ((org.opencds.vmr.v1_0.schema.Entity) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Facility" ->
                                ((org.opencds.vmr.v1_0.schema.Facility) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Organization" ->
                                ((org.opencds.vmr.v1_0.schema.Organization) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Person" ->
                                ((org.opencds.vmr.v1_0.schema.Person) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Specimen" ->
                                ((org.opencds.vmr.v1_0.schema.Specimen) external).getRelatedEntity().add(schemaRelatedEntity);
                        default -> throw new InvalidDataException(
                                "_METHOD_NAME + Unrecognized outerTarget class: " + externalClassName);
                    }

                }
                case "Facility" ->
                {
                    final org.opencds.vmr.v1_0.schema.RelatedEntity.Facility schemaNestedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity.Facility();

                    FacilityMapper.pushOut(
                            (Facility) organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()),
                            schemaNestedEntity, organizedResults);

                    final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity();

                    schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
                    schemaRelatedEntity.setRelationshipTimeInterval(
                            MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
                    schemaRelatedEntity.setFacility(schemaNestedEntity);

                    switch (externalClassName)
                    {
                        case "AdministrableSubstance" ->
                                ((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                        case "Entity" ->
                                ((org.opencds.vmr.v1_0.schema.Entity) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Facility" ->
                                ((org.opencds.vmr.v1_0.schema.Facility) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Organization" ->
                                ((org.opencds.vmr.v1_0.schema.Organization) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Person" ->
                                ((org.opencds.vmr.v1_0.schema.Person) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Specimen" ->
                                ((org.opencds.vmr.v1_0.schema.Specimen) external).getRelatedEntity().add(schemaRelatedEntity);
                        default -> throw new InvalidDataException(
                                "_METHOD_NAME + Unrecognized outerTarget class: " + externalClassName);
                    }

                }
                case "Organization" ->
                {
                    final org.opencds.vmr.v1_0.schema.RelatedEntity.Organization schemaNestedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity.Organization();

                    OrganizationMapper.pushOut(
                            (Organization) organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()),
                            schemaNestedEntity, organizedResults);

                    final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity();

                    schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
                    schemaRelatedEntity.setRelationshipTimeInterval(
                            MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
                    schemaRelatedEntity.setOrganization(schemaNestedEntity);

                    switch (externalClassName)
                    {
                        case "AdministrableSubstance" ->
                                ((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                        case "Entity" ->
                                ((org.opencds.vmr.v1_0.schema.Entity) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Facility" ->
                                ((org.opencds.vmr.v1_0.schema.Facility) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Organization" ->
                                ((org.opencds.vmr.v1_0.schema.Organization) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Person" ->
                                ((org.opencds.vmr.v1_0.schema.Person) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Specimen" ->
                                ((org.opencds.vmr.v1_0.schema.Specimen) external).getRelatedEntity().add(schemaRelatedEntity);
                        default -> throw new InvalidDataException(
                                "_METHOD_NAME + Unrecognized outerTarget class: " + externalClassName);
                    }

                }
                case "Person" ->
                {
                    final org.opencds.vmr.v1_0.schema.RelatedEntity.Person schemaNestedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity.Person();

                    PersonMapper.pushOut(
                            (Person) organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()),
                            schemaNestedEntity, organizedResults);

                    final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity();

                    schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
                    schemaRelatedEntity.setRelationshipTimeInterval(
                            MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
                    schemaRelatedEntity.setPerson(schemaNestedEntity);

                    switch (externalClassName)
                    {
                        case "AdministrableSubstance" ->
                                ((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                        case "Entity" ->
                                ((org.opencds.vmr.v1_0.schema.Entity) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Facility" ->
                                ((org.opencds.vmr.v1_0.schema.Facility) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Organization" ->
                                ((org.opencds.vmr.v1_0.schema.Organization) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Person" ->
                                ((org.opencds.vmr.v1_0.schema.Person) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Specimen" ->
                                ((org.opencds.vmr.v1_0.schema.Specimen) external).getRelatedEntity().add(schemaRelatedEntity);
                        default -> throw new InvalidDataException(
                                "_METHOD_NAME + Unrecognized outerTarget class: " + externalClassName);
                    }

                }
                case "Specimen" ->
                {
                    final org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen schemaNestedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen();

                    SpecimenMapper.pushOut(
                            (Specimen) organizedResults.getEntityList().get(oneInternalEntityRelationship.getTargetEntityId()),
                            schemaNestedEntity, organizedResults);

                    final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                            new org.opencds.vmr.v1_0.schema.RelatedEntity();

                    schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(oneInternalEntityRelationship.getTargetRole()));
                    schemaRelatedEntity.setRelationshipTimeInterval(
                            MappingUtility.iVLDateInternal2IVLTS(oneInternalEntityRelationship.getRelationshipTimeInterval()));
                    schemaRelatedEntity.setSpecimen(schemaNestedEntity);

                    switch (externalClassName)
                    {
                        case "AdministrableSubstance" ->
                                ((org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance) external).getRelatedEntity()
                                        .add(schemaRelatedEntity);
                        case "Entity" ->
                                ((org.opencds.vmr.v1_0.schema.Entity) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Facility" ->
                                ((org.opencds.vmr.v1_0.schema.Facility) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Organization" ->
                                ((org.opencds.vmr.v1_0.schema.Organization) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Person" ->
                                ((org.opencds.vmr.v1_0.schema.Person) external).getRelatedEntity().add(schemaRelatedEntity);
                        case "Specimen" ->
                                ((org.opencds.vmr.v1_0.schema.Specimen) external).getRelatedEntity().add(schemaRelatedEntity);
                        default -> throw new InvalidDataException(
                                "_METHOD_NAME + Unrecognized outerTarget class: " + externalClassName);
                    }

                }
                default ->
                        throw new InvalidDataException("_METHOD_NAME + Unrecognized class: " + thisInternalNestedEntityClassName);
            }
        }

        return external;
    }
}
