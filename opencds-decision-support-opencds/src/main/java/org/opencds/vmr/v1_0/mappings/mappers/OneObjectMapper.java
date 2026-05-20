package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;
import java.util.List;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class OneObjectMapper
{
    public static void pullInClinicalStatement(final Object source, final Object target, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullIn(): ";

        if (source == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        final String sourceClassName = source.getClass().getSimpleName();

        switch (sourceClassName)
        {
            case "AdverseEvent" ->
                    AdverseEventMapper.pullIn((org.opencds.vmr.v1_0.schema.AdverseEvent) source, (AdverseEvent) target,
                            subjectPersonId, focalPersonId, factLists);
            case "DeniedAdverseEvent" -> DeniedAdverseEventMapper.pullIn((org.opencds.vmr.v1_0.schema.DeniedAdverseEvent) source,
                    (DeniedAdverseEvent) target, subjectPersonId, focalPersonId, factLists);
            case "AppointmentProposal" -> AppointmentProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.AppointmentProposal) source,
                    (AppointmentProposal) target, subjectPersonId, focalPersonId, factLists);
            case "AppointmentRequest" -> AppointmentRequestMapper.pullIn((org.opencds.vmr.v1_0.schema.AppointmentRequest) source,
                    (AppointmentRequest) target, subjectPersonId, focalPersonId, factLists);
            case "EncounterEvent" ->
                    EncounterEventMapper.pullIn((org.opencds.vmr.v1_0.schema.EncounterEvent) source, (EncounterEvent) target,
                            subjectPersonId, focalPersonId, factLists);
            case "MissedAppointment" -> MissedAppointmentMapper.pullIn((org.opencds.vmr.v1_0.schema.MissedAppointment) source,
                    (MissedAppointment) target, subjectPersonId, focalPersonId, factLists);
            case "ScheduledAppointment" ->
                    ScheduledAppointmentMapper.pullIn((org.opencds.vmr.v1_0.schema.ScheduledAppointment) source,
                            (ScheduledAppointment) target, subjectPersonId, focalPersonId, factLists);
            case "Goal" ->
                    GoalMapper.pullIn((org.opencds.vmr.v1_0.schema.Goal) source, (Goal) target, subjectPersonId, focalPersonId,
                            factLists);
            case "GoalProposal" ->
                    GoalProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.GoalProposal) source, (GoalProposal) target,
                            subjectPersonId, focalPersonId, factLists);
            case "ObservationOrder" ->
                    ObservationOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.ObservationOrder) source, (ObservationOrder) target,
                            subjectPersonId, focalPersonId, factLists);
            case "ObservationProposal" -> ObservationProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.ObservationProposal) source,
                    (ObservationProposal) target, subjectPersonId, focalPersonId, factLists);
            case "ObservationResult" -> ObservationResultMapper.pullIn((org.opencds.vmr.v1_0.schema.ObservationResult) source,
                    (ObservationResult) target, subjectPersonId, focalPersonId, factLists);
            case "UnconductedObservation" ->
                    UnconductedObservationMapper.pullIn((org.opencds.vmr.v1_0.schema.UnconductedObservation) source,
                            (UnconductedObservation) target, subjectPersonId, focalPersonId, factLists);
            case "DeniedProblem" ->
                    DeniedProblemMapper.pullIn((org.opencds.vmr.v1_0.schema.DeniedProblem) source, (DeniedProblem) target,
                            subjectPersonId, focalPersonId, factLists);
            case "Problem" -> ProblemMapper.pullIn((org.opencds.vmr.v1_0.schema.Problem) source, (Problem) target, subjectPersonId,
                    focalPersonId, factLists);
            case "ProcedureEvent" ->
                    ProcedureEventMapper.pullIn((org.opencds.vmr.v1_0.schema.ProcedureEvent) source, (ProcedureEvent) target,
                            subjectPersonId, focalPersonId, factLists);
            case "ProcedureOrder" ->
                    ProcedureOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.ProcedureOrder) source, (ProcedureOrder) target,
                            subjectPersonId, focalPersonId, factLists);
            case "ProcedureProposal" -> ProcedureProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.ProcedureProposal) source,
                    (ProcedureProposal) target, subjectPersonId, focalPersonId, factLists);
            case "ScheduledProcedure" -> ScheduledProcedureMapper.pullIn((org.opencds.vmr.v1_0.schema.ScheduledProcedure) source,
                    (ScheduledProcedure) target, subjectPersonId, focalPersonId, factLists);
            case "UndeliveredProcedure" ->
                    UndeliveredProcedureMapper.pullIn((org.opencds.vmr.v1_0.schema.UndeliveredProcedure) source,
                            (UndeliveredProcedure) target, subjectPersonId, focalPersonId, factLists);
            case "SubstanceAdministrationEvent" ->
                    SubstanceAdministrationEventMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent) source,
                            (SubstanceAdministrationEvent) target, subjectPersonId, focalPersonId, factLists);
            case "SubstanceAdministrationOrder" ->
                    SubstanceAdministrationOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder) source,
                            (SubstanceAdministrationOrder) target, subjectPersonId, focalPersonId, factLists);
            case "SubstanceAdministrationProposal" -> SubstanceAdministrationProposalMapper.pullIn(
                    (org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal) source, (SubstanceAdministrationProposal) target,
                    subjectPersonId, focalPersonId, factLists);
            case "SubstanceDispensationEvent" ->
                    SubstanceDispensationEventMapper.pullIn((org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent) source,
                            (SubstanceDispensationEvent) target, subjectPersonId, focalPersonId, factLists);
            case "UndeliveredSubstanceAdministration" -> UndeliveredSubstanceAdministrationMapper.pullIn(
                    (org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration) source,
                    (UndeliveredSubstanceAdministration) target, subjectPersonId, focalPersonId, factLists);
            case "SupplyEvent" -> SupplyEventMapper.pullIn((org.opencds.vmr.v1_0.schema.SupplyEvent) source, (SupplyEvent) target,
                    subjectPersonId, focalPersonId, factLists);
            case "SupplyOrder" -> SupplyOrderMapper.pullIn((org.opencds.vmr.v1_0.schema.SupplyOrder) source, (SupplyOrder) target,
                    subjectPersonId, focalPersonId, factLists);
            case "SupplyProposal" ->
                    SupplyProposalMapper.pullIn((org.opencds.vmr.v1_0.schema.SupplyProposal) source, (SupplyProposal) target,
                            subjectPersonId, focalPersonId, factLists);
            case "UndeliveredSupply" -> UndeliveredSupplyMapper.pullIn((org.opencds.vmr.v1_0.schema.UndeliveredSupply) source,
                    (UndeliveredSupply) target, subjectPersonId, focalPersonId, factLists);
            default -> throw new ImproperUsageException(
                    "OneObjectMapper failed to pullInRelatedClinicalStatement for: " + sourceClassName);
        }
    }

    public static void pullInRelatedEntity(final org.opencds.vmr.v1_0.schema.RelatedEntity sourceRelatedEntity,
            final String parentId, final org.opencds.vmr.v1_0.schema.CD relationshipToSource, final String subjectPersonId,
            final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullInRelatedEntity(): ";

        if (sourceRelatedEntity == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        if (parentId == null)
        {
            final String errStr = _METHODNAME + "improper usage: parentId supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}", sourceRelatedEntity.getClass().getSimpleName(), parentId);

        if (sourceRelatedEntity.getAdministrableSubstance() != null)
        {
            AdministrableSubstanceMapper.pullIn(sourceRelatedEntity.getAdministrableSubstance(), new AdministrableSubstance(),
                    parentId, relationshipToSource, subjectPersonId, focalPersonId, factLists);
            EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getAdministrableSubstance().getId(),
                    sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists);
        }
        else
            if (sourceRelatedEntity.getEntity() != null)
            {
                EntityMapper.pullIn(sourceRelatedEntity.getEntity(), new Entity(), parentId, relationshipToSource, subjectPersonId,
                        focalPersonId, factLists);
                EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getEntity().getId(),
                        sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists);
            }
            else
                if (sourceRelatedEntity.getFacility() != null)
                {
                    FacilityMapper.pullIn(sourceRelatedEntity.getFacility(), new Facility(), parentId, relationshipToSource,
                            subjectPersonId, focalPersonId, factLists);
                    EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getFacility().getId(),
                            sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists);
                }
                else
                    if (sourceRelatedEntity.getOrganization() != null)
                    {
                        OrganizationMapper.pullIn(sourceRelatedEntity.getOrganization(), new Organization(), parentId,
                                relationshipToSource, subjectPersonId, focalPersonId, factLists);
                        EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getOrganization().getId(),
                                sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(), factLists);
                    }
                    else
                        if (sourceRelatedEntity.getPerson() != null)
                        {
                            PersonMapper.pullIn(sourceRelatedEntity.getPerson(), new Person(), parentId, relationshipToSource,
                                    subjectPersonId, focalPersonId, factLists);
                            EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getPerson().getId(),
                                    sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(),
                                    factLists);
                        }
                        else
                            if (sourceRelatedEntity.getSpecimen() != null)
                            {
                                SpecimenMapper.pullIn(sourceRelatedEntity.getSpecimen(), new Specimen(), parentId,
                                        relationshipToSource, subjectPersonId, focalPersonId, factLists);
                                EntityRelationshipMapper.pullIn(parentId, sourceRelatedEntity.getSpecimen().getId(),
                                        sourceRelatedEntity.getTargetRole(), sourceRelatedEntity.getRelationshipTimeInterval(),
                                        factLists);
                            }
                            else
                            {
                                final String errStr = _METHODNAME + "improper usage: source class not recognized: "
                                        + sourceRelatedEntity.getClass().getName() + " for sourceId: " + parentId + ".";
                                log.error(errStr);
                                throw new ImproperUsageException(errStr);
                            }
    }

    public static void pullInRelatedClinicalStatement(final org.opencds.vmr.v1_0.schema.RelatedClinicalStatement source,
            final String sourceId, final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pullInRelatedClinicalStatement(): ";

        if (source == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        if (sourceId == null)
        {
            final String errStr = _METHODNAME + "improper usage: sourceId supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        final CD targetRelationshipToSource = MappingUtility.cD2CDInternal(source.getTargetRelationshipToSource());
        final RelationshipToSource relationshipToSource = new RelationshipToSource();
        relationshipToSource.setRelationshipToSource(targetRelationshipToSource);
        relationshipToSource.setSourceId(sourceId);
        final List<RelationshipToSource> relationshipToSources = new ArrayList<>();
        relationshipToSources.add(relationshipToSource);

        if (source.getAdverseEvent() != null)
        {
            ClinicalStatementRelationshipMapper.pullIn(sourceId, MappingUtility.iI2FlatId(source.getAdverseEvent().getId()),
                    targetRelationshipToSource, factLists);
            final AdverseEvent target = new AdverseEvent();
            target.setRelationshipToSources(relationshipToSources);
            AdverseEventMapper.pullIn(source.getAdverseEvent(), target, subjectPersonId, focalPersonId, factLists);
        }
        else
            if (source.getDeniedAdverseEvent() != null)
            {
                ClinicalStatementRelationshipMapper.pullIn(sourceId,
                        MappingUtility.iI2FlatId(source.getDeniedAdverseEvent().getId()), targetRelationshipToSource, factLists);
                final DeniedAdverseEvent target = new DeniedAdverseEvent();
                target.setRelationshipToSources(relationshipToSources);
                DeniedAdverseEventMapper.pullIn(source.getDeniedAdverseEvent(), target, subjectPersonId, focalPersonId, factLists);
            }
            else
                if (source.getAppointmentProposal() != null)
                {
                    ClinicalStatementRelationshipMapper.pullIn(sourceId,
                            MappingUtility.iI2FlatId(source.getAppointmentProposal().getId()), targetRelationshipToSource,
                            factLists);
                    final AppointmentProposal target = new AppointmentProposal();
                    target.setRelationshipToSources(relationshipToSources);
                    AppointmentProposalMapper.pullIn(source.getAppointmentProposal(), target, subjectPersonId, focalPersonId,
                            factLists);
                }
                else
                    if (source.getAppointmentRequest() != null)
                    {
                        ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                MappingUtility.iI2FlatId(source.getAppointmentRequest().getId()), targetRelationshipToSource,
                                factLists);
                        final AppointmentRequest target = new AppointmentRequest();
                        target.setRelationshipToSources(relationshipToSources);
                        AppointmentRequestMapper.pullIn(source.getAppointmentRequest(), target, subjectPersonId, focalPersonId,
                                factLists);
                    }
                    else
                        if (source.getEncounterEvent() != null)
                        {
                            ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                    MappingUtility.iI2FlatId(source.getEncounterEvent().getId()), targetRelationshipToSource,
                                    factLists);
                            final EncounterEvent target = new EncounterEvent();
                            target.setRelationshipToSources(relationshipToSources);
                            EncounterEventMapper.pullIn(source.getEncounterEvent(), target, subjectPersonId, focalPersonId,
                                    factLists);
                        }
                        else
                            if (source.getMissedAppointment() != null)
                            {
                                ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                        MappingUtility.iI2FlatId(source.getMissedAppointment().getId()), targetRelationshipToSource,
                                        factLists);
                                final MissedAppointment target = new MissedAppointment();
                                target.setRelationshipToSources(relationshipToSources);
                                MissedAppointmentMapper.pullIn(source.getMissedAppointment(), target, subjectPersonId,
                                        focalPersonId, factLists);
                            }
                            else
                                if (source.getScheduledAppointment() != null)
                                {
                                    ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                            MappingUtility.iI2FlatId(source.getScheduledAppointment().getId()),
                                            targetRelationshipToSource, factLists);
                                    final ScheduledAppointment target = new ScheduledAppointment();
                                    target.setRelationshipToSources(relationshipToSources);
                                    ScheduledAppointmentMapper.pullIn(source.getScheduledAppointment(), target, subjectPersonId,
                                            focalPersonId, factLists);
                                }
                                else
                                    if (source.getGoal() != null)
                                    {
                                        ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                MappingUtility.iI2FlatId(source.getGoal().getId()), targetRelationshipToSource,
                                                factLists);
                                        final Goal target = new Goal();
                                        target.setRelationshipToSources(relationshipToSources);
                                        GoalMapper.pullIn(source.getGoal(), target, subjectPersonId, focalPersonId, factLists);
                                    }
                                    else
                                        if (source.getGoalProposal() != null)
                                        {
                                            ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                    MappingUtility.iI2FlatId(source.getGoalProposal().getId()),
                                                    targetRelationshipToSource, factLists);
                                            final GoalProposal target = new GoalProposal();
                                            target.setRelationshipToSources(relationshipToSources);
                                            GoalProposalMapper.pullIn(source.getGoalProposal(), target, subjectPersonId,
                                                    focalPersonId, factLists);
                                        }
                                        else
                                            if (source.getObservationOrder() != null)
                                            {
                                                ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                        MappingUtility.iI2FlatId(source.getObservationOrder().getId()),
                                                        targetRelationshipToSource, factLists);
                                                final ObservationOrder target = new ObservationOrder();
                                                target.setRelationshipToSources(relationshipToSources);
                                                ObservationOrderMapper.pullIn(source.getObservationOrder(), target, subjectPersonId,
                                                        focalPersonId, factLists);
                                            }
                                            else
                                                if (source.getObservationProposal() != null)
                                                {
                                                    ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                            MappingUtility.iI2FlatId(source.getObservationProposal().getId()),
                                                            targetRelationshipToSource, factLists);
                                                    final ObservationProposal target = new ObservationProposal();
                                                    target.setRelationshipToSources(relationshipToSources);
                                                    ObservationProposalMapper.pullIn(source.getObservationProposal(), target,
                                                            subjectPersonId, focalPersonId, factLists);
                                                }
                                                else
                                                    if (source.getObservationResult() != null)
                                                    {
                                                        ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                MappingUtility.iI2FlatId(source.getObservationResult().getId()),
                                                                targetRelationshipToSource, factLists);
                                                        final ObservationResult target = new ObservationResult();
                                                        target.setRelationshipToSources(relationshipToSources);
                                                        ObservationResultMapper.pullIn(source.getObservationResult(), target,
                                                                subjectPersonId, focalPersonId, factLists);
                                                    }
                                                    else
                                                        if (source.getUnconductedObservation() != null)
                                                        {
                                                            ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                    MappingUtility.iI2FlatId(
                                                                            source.getUnconductedObservation().getId()),
                                                                    targetRelationshipToSource, factLists);
                                                            final UnconductedObservation target = new UnconductedObservation();
                                                            target.setRelationshipToSources(relationshipToSources);
                                                            UnconductedObservationMapper.pullIn(source.getUnconductedObservation(),
                                                                    target, subjectPersonId, focalPersonId, factLists);
                                                        }
                                                        else
                                                            if (source.getDeniedProblem() != null)
                                                            {
                                                                ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                        MappingUtility.iI2FlatId(source.getDeniedProblem().getId()),
                                                                        targetRelationshipToSource, factLists);
                                                                final DeniedProblem target = new DeniedProblem();
                                                                target.setRelationshipToSources(relationshipToSources);
                                                                DeniedProblemMapper.pullIn(source.getDeniedProblem(), target,
                                                                        subjectPersonId, focalPersonId, factLists);
                                                            }
                                                            else
                                                                if (source.getProblem() != null)
                                                                {
                                                                    ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                            MappingUtility.iI2FlatId(source.getProblem().getId()),
                                                                            targetRelationshipToSource, factLists);
                                                                    final Problem target = new Problem();
                                                                    target.setRelationshipToSources(relationshipToSources);
                                                                    ProblemMapper.pullIn(source.getProblem(), target,
                                                                            subjectPersonId, focalPersonId, factLists);
                                                                }
                                                                else
                                                                    if (source.getProcedureEvent() != null)
                                                                    {
                                                                        ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                                MappingUtility.iI2FlatId(
                                                                                        source.getProcedureEvent().getId()),
                                                                                targetRelationshipToSource, factLists);
                                                                        final ProcedureEvent target = new ProcedureEvent();
                                                                        target.setRelationshipToSources(relationshipToSources);
                                                                        ProcedureEventMapper.pullIn(source.getProcedureEvent(),
                                                                                target, subjectPersonId, focalPersonId, factLists);
                                                                    }
                                                                    else
                                                                        if (source.getProcedureOrder() != null)
                                                                        {
                                                                            ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                                    MappingUtility.iI2FlatId(
                                                                                            source.getProcedureOrder().getId()),
                                                                                    targetRelationshipToSource, factLists);
                                                                            final ProcedureOrder target = new ProcedureOrder();
                                                                            target.setRelationshipToSources(relationshipToSources);
                                                                            ProcedureOrderMapper.pullIn(source.getProcedureOrder(),
                                                                                    target, subjectPersonId, focalPersonId,
                                                                                    factLists);
                                                                        }
                                                                        else
                                                                            if (source.getProcedureProposal() != null)
                                                                            {
                                                                                ClinicalStatementRelationshipMapper.pullIn(sourceId,
                                                                                        MappingUtility.iI2FlatId(
                                                                                                source.getProcedureProposal()
                                                                                                        .getId()),
                                                                                        targetRelationshipToSource, factLists);
                                                                                final ProcedureProposal target =
                                                                                        new ProcedureProposal();
                                                                                target.setRelationshipToSources(
                                                                                        relationshipToSources);
                                                                                ProcedureProposalMapper.pullIn(
                                                                                        source.getProcedureProposal(), target,
                                                                                        subjectPersonId, focalPersonId, factLists);
                                                                            }
                                                                            else
                                                                                if (source.getScheduledProcedure() != null)
                                                                                {
                                                                                    ClinicalStatementRelationshipMapper.pullIn(
                                                                                            sourceId, MappingUtility.iI2FlatId(
                                                                                                    source.getScheduledProcedure()
                                                                                                            .getId()),
                                                                                            targetRelationshipToSource, factLists);
                                                                                    final ScheduledProcedure target =
                                                                                            new ScheduledProcedure();
                                                                                    target.setRelationshipToSources(
                                                                                            relationshipToSources);
                                                                                    ScheduledProcedureMapper.pullIn(
                                                                                            source.getScheduledProcedure(), target,
                                                                                            subjectPersonId, focalPersonId,
                                                                                            factLists);
                                                                                }
                                                                                else
                                                                                    if (source.getUndeliveredProcedure() != null)
                                                                                    {
                                                                                        ClinicalStatementRelationshipMapper.pullIn(
                                                                                                sourceId, MappingUtility.iI2FlatId(
                                                                                                        source.getUndeliveredProcedure()
                                                                                                                .getId()),
                                                                                                targetRelationshipToSource,
                                                                                                factLists);
                                                                                        final UndeliveredProcedure target =
                                                                                                new UndeliveredProcedure();
                                                                                        target.setRelationshipToSources(
                                                                                                relationshipToSources);
                                                                                        UndeliveredProcedureMapper.pullIn(
                                                                                                source.getUndeliveredProcedure(),
                                                                                                target, subjectPersonId,
                                                                                                focalPersonId, factLists);
                                                                                    }
                                                                                    else
                                                                                        if (source.getSubstanceAdministrationEvent()
                                                                                                != null)
                                                                                        {
                                                                                            ClinicalStatementRelationshipMapper.pullIn(
                                                                                                    sourceId,
                                                                                                    MappingUtility.iI2FlatId(
                                                                                                            source.getSubstanceAdministrationEvent()
                                                                                                                    .getId()),
                                                                                                    targetRelationshipToSource,
                                                                                                    factLists);
                                                                                            final SubstanceAdministrationEvent
                                                                                                    target =
                                                                                                    new SubstanceAdministrationEvent();
                                                                                            target.setRelationshipToSources(
                                                                                                    relationshipToSources);
                                                                                            SubstanceAdministrationEventMapper.pullIn(
                                                                                                    source.getSubstanceAdministrationEvent(),
                                                                                                    target, subjectPersonId,
                                                                                                    focalPersonId, factLists);
                                                                                        }
                                                                                        else
                                                                                            if (source.getSubstanceAdministrationOrder()
                                                                                                    != null)
                                                                                            {
                                                                                                ClinicalStatementRelationshipMapper.pullIn(
                                                                                                        sourceId,
                                                                                                        MappingUtility.iI2FlatId(
                                                                                                                source.getSubstanceAdministrationOrder()
                                                                                                                        .getId()),
                                                                                                        targetRelationshipToSource,
                                                                                                        factLists);
                                                                                                final SubstanceAdministrationOrder
                                                                                                        target =
                                                                                                        new SubstanceAdministrationOrder();
                                                                                                target.setRelationshipToSources(
                                                                                                        relationshipToSources);
                                                                                                SubstanceAdministrationOrderMapper.pullIn(
                                                                                                        source.getSubstanceAdministrationOrder(),
                                                                                                        target, subjectPersonId,
                                                                                                        focalPersonId, factLists);
                                                                                            }
                                                                                            else
                                                                                                if (source.getSubstanceAdministrationProposal()
                                                                                                        != null)
                                                                                                {
                                                                                                    ClinicalStatementRelationshipMapper.pullIn(
                                                                                                            sourceId,
                                                                                                            MappingUtility.iI2FlatId(
                                                                                                                    source.getSubstanceAdministrationProposal()
                                                                                                                            .getId()),
                                                                                                            targetRelationshipToSource,
                                                                                                            factLists);
                                                                                                    final SubstanceAdministrationProposal
                                                                                                            target =
                                                                                                            new SubstanceAdministrationProposal();
                                                                                                    target.setRelationshipToSources(
                                                                                                            relationshipToSources);
                                                                                                    SubstanceAdministrationProposalMapper.pullIn(
                                                                                                            source.getSubstanceAdministrationProposal(),
                                                                                                            target, subjectPersonId,
                                                                                                            focalPersonId,
                                                                                                            factLists);
                                                                                                }
                                                                                                else
                                                                                                    if (source.getSubstanceDispensationEvent()
                                                                                                            != null)
                                                                                                    {
                                                                                                        ClinicalStatementRelationshipMapper.pullIn(
                                                                                                                sourceId,
                                                                                                                MappingUtility.iI2FlatId(
                                                                                                                        source.getSubstanceDispensationEvent()
                                                                                                                                .getId()),
                                                                                                                targetRelationshipToSource,
                                                                                                                factLists);
                                                                                                        final SubstanceDispensationEvent
                                                                                                                target =
                                                                                                                new SubstanceDispensationEvent();
                                                                                                        target.setRelationshipToSources(
                                                                                                                relationshipToSources);
                                                                                                        SubstanceDispensationEventMapper.pullIn(
                                                                                                                source.getSubstanceDispensationEvent(),
                                                                                                                target,
                                                                                                                subjectPersonId,
                                                                                                                focalPersonId,
                                                                                                                factLists);
                                                                                                    }
                                                                                                    else
                                                                                                        if (source.getUndeliveredSubstanceAdministration()
                                                                                                                != null)
                                                                                                        {
                                                                                                            ClinicalStatementRelationshipMapper.pullIn(
                                                                                                                    sourceId,
                                                                                                                    MappingUtility.iI2FlatId(
                                                                                                                            source.getUndeliveredSubstanceAdministration()
                                                                                                                                    .getId()),
                                                                                                                    targetRelationshipToSource,
                                                                                                                    factLists);
                                                                                                            final UndeliveredSubstanceAdministration
                                                                                                                    target =
                                                                                                                    new UndeliveredSubstanceAdministration();
                                                                                                            target.setRelationshipToSources(
                                                                                                                    relationshipToSources);
                                                                                                            UndeliveredSubstanceAdministrationMapper.pullIn(
                                                                                                                    source.getUndeliveredSubstanceAdministration(),
                                                                                                                    target,
                                                                                                                    subjectPersonId,
                                                                                                                    focalPersonId,
                                                                                                                    factLists);
                                                                                                        }
                                                                                                        else
                                                                                                            if (source.getSupplyEvent()
                                                                                                                    != null)
                                                                                                            {
                                                                                                                ClinicalStatementRelationshipMapper.pullIn(
                                                                                                                        sourceId,
                                                                                                                        MappingUtility.iI2FlatId(
                                                                                                                                source.getSupplyEvent()
                                                                                                                                        .getId()),
                                                                                                                        targetRelationshipToSource,
                                                                                                                        factLists);
                                                                                                                final SupplyEvent
                                                                                                                        target =
                                                                                                                        new SupplyEvent();
                                                                                                                target.setRelationshipToSources(
                                                                                                                        relationshipToSources);
                                                                                                                SupplyEventMapper.pullIn(
                                                                                                                        source.getSupplyEvent(),
                                                                                                                        target,
                                                                                                                        subjectPersonId,
                                                                                                                        focalPersonId,
                                                                                                                        factLists);
                                                                                                            }
                                                                                                            else
                                                                                                                if (source.getSupplyOrder()
                                                                                                                        != null)
                                                                                                                {
                                                                                                                    ClinicalStatementRelationshipMapper.pullIn(
                                                                                                                            sourceId,
                                                                                                                            MappingUtility.iI2FlatId(
                                                                                                                                    source.getSupplyOrder()
                                                                                                                                            .getId()),
                                                                                                                            targetRelationshipToSource,
                                                                                                                            factLists);
                                                                                                                    final SupplyOrder
                                                                                                                            target =
                                                                                                                            new SupplyOrder();
                                                                                                                    target.setRelationshipToSources(
                                                                                                                            relationshipToSources);
                                                                                                                    SupplyOrderMapper.pullIn(
                                                                                                                            source.getSupplyOrder(),
                                                                                                                            target,
                                                                                                                            subjectPersonId,
                                                                                                                            focalPersonId,
                                                                                                                            factLists);
                                                                                                                }
                                                                                                                else
                                                                                                                    if (source.getSupplyProposal()
                                                                                                                            != null)
                                                                                                                    {
                                                                                                                        ClinicalStatementRelationshipMapper.pullIn(
                                                                                                                                sourceId,
                                                                                                                                MappingUtility.iI2FlatId(
                                                                                                                                        source.getSupplyProposal()
                                                                                                                                                .getId()),
                                                                                                                                targetRelationshipToSource,
                                                                                                                                factLists);
                                                                                                                        final SupplyProposal
                                                                                                                                target =
                                                                                                                                new SupplyProposal();
                                                                                                                        target.setRelationshipToSources(
                                                                                                                                relationshipToSources);
                                                                                                                        SupplyProposalMapper.pullIn(
                                                                                                                                source.getSupplyProposal(),
                                                                                                                                target,
                                                                                                                                subjectPersonId,
                                                                                                                                focalPersonId,
                                                                                                                                factLists);
                                                                                                                    }
                                                                                                                    else
                                                                                                                        if (source.getUndeliveredSupply()
                                                                                                                                != null)
                                                                                                                        {
                                                                                                                            ClinicalStatementRelationshipMapper.pullIn(
                                                                                                                                    sourceId,
                                                                                                                                    MappingUtility.iI2FlatId(
                                                                                                                                            source.getUndeliveredSupply()
                                                                                                                                                    .getId()),
                                                                                                                                    targetRelationshipToSource,
                                                                                                                                    factLists);
                                                                                                                            final UndeliveredSupply
                                                                                                                                    target =
                                                                                                                                    new UndeliveredSupply();
                                                                                                                            target.setRelationshipToSources(
                                                                                                                                    relationshipToSources);
                                                                                                                            UndeliveredSupplyMapper.pullIn(
                                                                                                                                    source.getUndeliveredSupply(),
                                                                                                                                    target,
                                                                                                                                    subjectPersonId,
                                                                                                                                    focalPersonId,
                                                                                                                                    factLists);
                                                                                                                        }
    }

    public static void pushOutRootClinicalStatement(final Object source, final Object target,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOutClinicalStatement(): ";

        if (source == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        if (target == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        final String sourceClassName = source.getClass().getSimpleName();

        if ((!((ClinicalStatement) source).isToBeReturned()) || (!((ClinicalStatement) source).isClinicalStatementToBeRoot()))
            return;

        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}", sourceClassName);

        switch (sourceClassName)
        {
            case "AdverseEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.AdverseEvent();
                final org.opencds.vmr.v1_0.schema.AdverseEvent rootClinicalStatement;
                rootClinicalStatement = AdverseEventMapper.pushOut((AdverseEvent) source, organizedResults);
                organizedResults.output().getAdverseEvents().getAdverseEvent().add(rootClinicalStatement);
            }
            case "DeniedAdverseEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.DeniedAdverseEvent();
                final org.opencds.vmr.v1_0.schema.DeniedAdverseEvent rootClinicalStatement;
                rootClinicalStatement = DeniedAdverseEventMapper.pushOut((DeniedAdverseEvent) source, organizedResults);
                organizedResults.output().getDeniedAdverseEvents().getDeniedAdverseEvent().add(rootClinicalStatement);
            }
            case "AppointmentProposal" ->
            {
                new org.opencds.vmr.v1_0.schema.AppointmentProposal();
                final org.opencds.vmr.v1_0.schema.AppointmentProposal rootClinicalStatement;
                rootClinicalStatement = AppointmentProposalMapper.pushOut((AppointmentProposal) source, organizedResults);
                organizedResults.output().getAppointmentProposals().getAppointmentProposal().add(rootClinicalStatement);
            }
            case "AppointmentRequest" ->
            {
                new org.opencds.vmr.v1_0.schema.AppointmentRequest();
                final org.opencds.vmr.v1_0.schema.AppointmentRequest rootClinicalStatement;
                rootClinicalStatement = AppointmentRequestMapper.pushOut((AppointmentRequest) source, organizedResults);
                organizedResults.output().getAppointmentRequests().getAppointmentRequest().add(rootClinicalStatement);
            }
            case "EncounterEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.EncounterEvent();
                final org.opencds.vmr.v1_0.schema.EncounterEvent rootClinicalStatement;
                rootClinicalStatement = EncounterEventMapper.pushOut((EncounterEvent) source, organizedResults);
                organizedResults.output().getEncounterEvents().getEncounterEvent().add(rootClinicalStatement);
            }
            case "MissedAppointment" ->
            {
                new org.opencds.vmr.v1_0.schema.MissedAppointment();
                final org.opencds.vmr.v1_0.schema.MissedAppointment rootClinicalStatement;
                rootClinicalStatement = MissedAppointmentMapper.pushOut((MissedAppointment) source, organizedResults);
                organizedResults.output().getMissedAppointments().getMissedAppointment().add(rootClinicalStatement);
            }
            case "ScheduledAppointment" ->
            {
                new org.opencds.vmr.v1_0.schema.ScheduledAppointment();
                final org.opencds.vmr.v1_0.schema.ScheduledAppointment rootClinicalStatement;
                rootClinicalStatement = ScheduledAppointmentMapper.pushOut((ScheduledAppointment) source, organizedResults);
                organizedResults.output().getScheduledAppointments().getScheduledAppointment().add(rootClinicalStatement);
            }
            case "Goal" ->
            {
                new org.opencds.vmr.v1_0.schema.Goal();
                final org.opencds.vmr.v1_0.schema.Goal rootClinicalStatement;
                rootClinicalStatement = GoalMapper.pushOut((Goal) source, organizedResults);
                organizedResults.output().getGoals().getGoal().add(rootClinicalStatement);
            }
            case "GoalProposal" ->
            {
                new org.opencds.vmr.v1_0.schema.GoalProposal();
                final org.opencds.vmr.v1_0.schema.GoalProposal rootClinicalStatement;
                rootClinicalStatement = GoalProposalMapper.pushOut((GoalProposal) source, organizedResults);
                organizedResults.output().getGoalProposals().getGoalProposal().add(rootClinicalStatement);
            }
            case "ObservationOrder" ->
            {
                new org.opencds.vmr.v1_0.schema.ObservationOrder();
                final org.opencds.vmr.v1_0.schema.ObservationOrder rootClinicalStatement;
                rootClinicalStatement = ObservationOrderMapper.pushOut((ObservationOrder) source, organizedResults);
                organizedResults.output().getObservationOrders().getObservationOrder().add(rootClinicalStatement);
            }
            case "ObservationProposal" ->
            {
                new org.opencds.vmr.v1_0.schema.ObservationProposal();
                final org.opencds.vmr.v1_0.schema.ObservationProposal rootClinicalStatement;
                rootClinicalStatement = ObservationProposalMapper.pushOut((ObservationProposal) source, organizedResults);
                organizedResults.output().getObservationProposals().getObservationProposal().add(rootClinicalStatement);
            }
            case "ObservationResult" ->
            {
                new org.opencds.vmr.v1_0.schema.ObservationResult();
                final org.opencds.vmr.v1_0.schema.ObservationResult rootClinicalStatement;
                rootClinicalStatement = ObservationResultMapper.pushOut((ObservationResult) source, organizedResults);
                organizedResults.output().getObservationResults().getObservationResult().add(rootClinicalStatement);
            }
            case "UnconductedObservation" ->
            {
                new org.opencds.vmr.v1_0.schema.UnconductedObservation();
                final org.opencds.vmr.v1_0.schema.UnconductedObservation rootClinicalStatement;
                rootClinicalStatement = UnconductedObservationMapper.pushOut((UnconductedObservation) source, organizedResults);
                organizedResults.output().getUnconductedObservations().getUnconductedObservation().add(rootClinicalStatement);
            }
            case "DeniedProblem" ->
            {
                new org.opencds.vmr.v1_0.schema.DeniedProblem();
                final org.opencds.vmr.v1_0.schema.DeniedProblem rootClinicalStatement;
                rootClinicalStatement = DeniedProblemMapper.pushOut((DeniedProblem) source, organizedResults);
                organizedResults.output().getDeniedProblems().getDeniedProblem().add(rootClinicalStatement);
            }
            case "Problem" ->
            {
                new org.opencds.vmr.v1_0.schema.Problem();
                final org.opencds.vmr.v1_0.schema.Problem rootClinicalStatement;
                rootClinicalStatement = ProblemMapper.pushOut((Problem) source, organizedResults);
                organizedResults.output().getProblems().getProblem().add(rootClinicalStatement);
            }
            case "ProcedureEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.ProcedureEvent();
                final org.opencds.vmr.v1_0.schema.ProcedureEvent rootClinicalStatement;
                rootClinicalStatement = ProcedureEventMapper.pushOut((ProcedureEvent) source, organizedResults);
                organizedResults.output().getProcedureEvents().getProcedureEvent().add(rootClinicalStatement);
            }
            case "ProcedureOrder" ->
            {
                new org.opencds.vmr.v1_0.schema.ProcedureOrder();
                final org.opencds.vmr.v1_0.schema.ProcedureOrder rootClinicalStatement;
                rootClinicalStatement = ProcedureOrderMapper.pushOut((ProcedureOrder) source, organizedResults);
                organizedResults.output().getProcedureOrders().getProcedureOrder().add(rootClinicalStatement);
            }
            case "ProcedureProposal" ->
            {
                new org.opencds.vmr.v1_0.schema.ProcedureProposal();
                final org.opencds.vmr.v1_0.schema.ProcedureProposal rootClinicalStatement;
                rootClinicalStatement = ProcedureProposalMapper.pushOut((ProcedureProposal) source, organizedResults);
                organizedResults.output().getProcedureProposals().getProcedureProposal().add(rootClinicalStatement);
            }
            case "ScheduledProcedure" ->
            {
                new org.opencds.vmr.v1_0.schema.ScheduledProcedure();
                final org.opencds.vmr.v1_0.schema.ScheduledProcedure rootClinicalStatement;
                rootClinicalStatement = ScheduledProcedureMapper.pushOut((ScheduledProcedure) source, organizedResults);
                organizedResults.output().getScheduledProcedures().getScheduledProcedure().add(rootClinicalStatement);
            }
            case "UndeliveredProcedure" ->
            {
                new org.opencds.vmr.v1_0.schema.UndeliveredProcedure();
                final org.opencds.vmr.v1_0.schema.UndeliveredProcedure rootClinicalStatement;
                rootClinicalStatement = UndeliveredProcedureMapper.pushOut((UndeliveredProcedure) source, organizedResults);
                organizedResults.output().getUndeliveredProcedures().getUndeliveredProcedure().add(rootClinicalStatement);
            }
            case "SubstanceAdministrationEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent();
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent rootClinicalStatement;
                rootClinicalStatement =
                        SubstanceAdministrationEventMapper.pushOut((SubstanceAdministrationEvent) source, organizedResults);
                organizedResults.output()
                        .getSubstanceAdministrationEvents()
                        .getSubstanceAdministrationEvent()
                        .add(rootClinicalStatement);
            }
            case "SubstanceAdministrationOrder" ->
            {
                new org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder();
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder rootClinicalStatement;
                rootClinicalStatement =
                        SubstanceAdministrationOrderMapper.pushOut((SubstanceAdministrationOrder) source, organizedResults);
                organizedResults.output()
                        .getSubstanceAdministrationOrders()
                        .getSubstanceAdministrationOrder()
                        .add(rootClinicalStatement);
            }
            case "SubstanceAdministrationProposal" ->
            {
                new org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal();
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal rootClinicalStatement;
                rootClinicalStatement =
                        SubstanceAdministrationProposalMapper.pushOut((SubstanceAdministrationProposal) source, organizedResults);
                organizedResults.output()
                        .getSubstanceAdministrationProposals()
                        .getSubstanceAdministrationProposal()
                        .add(rootClinicalStatement);
            }
            case "SubstanceDispensationEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent();
                final org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent rootClinicalStatement;
                rootClinicalStatement =
                        SubstanceDispensationEventMapper.pushOut((SubstanceDispensationEvent) source, organizedResults);
                organizedResults.output()
                        .getSubstanceDispensationEvents()
                        .getSubstanceDispensationEvent()
                        .add(rootClinicalStatement);
            }
            case "UndeliveredSubstanceAdministration" ->
            {
                new org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration();
                final org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration rootClinicalStatement;
                rootClinicalStatement =
                        UndeliveredSubstanceAdministrationMapper.pushOut((UndeliveredSubstanceAdministration) source,
                                organizedResults);
                organizedResults.output()
                        .getUndeliveredSubstanceAdministrations()
                        .getUndeliveredSubstanceAdministration()
                        .add(rootClinicalStatement);
            }
            case "SupplyEvent" ->
            {
                new org.opencds.vmr.v1_0.schema.SupplyEvent();
                final org.opencds.vmr.v1_0.schema.SupplyEvent rootClinicalStatement;
                rootClinicalStatement = SupplyEventMapper.pushOut((SupplyEvent) source, organizedResults);
                organizedResults.output().getSupplyEvents().getSupplyEvent().add(rootClinicalStatement);
            }
            case "SupplyOrder" ->
            {
                new org.opencds.vmr.v1_0.schema.SupplyOrder();
                final org.opencds.vmr.v1_0.schema.SupplyOrder rootClinicalStatement;
                rootClinicalStatement = SupplyOrderMapper.pushOut((SupplyOrder) source, organizedResults);
                organizedResults.output().getSupplyOrders().getSupplyOrder().add(rootClinicalStatement);
            }
            case "SupplyProposal" ->
            {
                new org.opencds.vmr.v1_0.schema.SupplyProposal();
                final org.opencds.vmr.v1_0.schema.SupplyProposal rootClinicalStatement;
                rootClinicalStatement = SupplyProposalMapper.pushOut((SupplyProposal) source, organizedResults);
                organizedResults.output().getSupplyProposals().getSupplyProposal().add(rootClinicalStatement);
            }
            case "UndeliveredSupply" ->
            {
                new org.opencds.vmr.v1_0.schema.UndeliveredSupply();
                final org.opencds.vmr.v1_0.schema.UndeliveredSupply rootClinicalStatement;
                rootClinicalStatement = UndeliveredSupplyMapper.pushOut((UndeliveredSupply) source, organizedResults);
                organizedResults.output().getUndeliveredSupplies().getUndeliveredSupply().add(rootClinicalStatement);
            }
        }

    }

    public static org.opencds.vmr.v1_0.schema.RelatedClinicalStatement pushOutRelatedClinicalStatement(
            final ClinicalStatement internalVMR,

            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOutRelatedClinicalStatement(): ";

        if (internalVMR == null)
        {
            final String errStr = _METHODNAME + "improper usage: internalVMR ClinicalStatement supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        final String internalVMRClassName = internalVMR.getClass().getSimpleName();
        if (log.isTraceEnabled())
            log.trace(_METHODNAME + "{}, {}: {}", internalVMRClassName, internalVMR.getId(), internalVMR.getEvaluatedPersonId());

        org.opencds.vmr.v1_0.schema.RelatedClinicalStatement relatedClinicalStatement = null;

        switch (internalVMRClassName)
        {
            case "AdverseEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.AdverseEvent nestedClinicalStatement = AdverseEventMapper.pushOut(

                        (AdverseEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setAdverseEvent(nestedClinicalStatement);
            }
            case "DeniedAdverseEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.DeniedAdverseEvent nestedClinicalStatement =
                        DeniedAdverseEventMapper.pushOut((DeniedAdverseEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setDeniedAdverseEvent(nestedClinicalStatement);
            }
            case "EncounterEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.EncounterEvent nestedClinicalStatement =
                        EncounterEventMapper.pushOut((EncounterEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setEncounterEvent(nestedClinicalStatement);
            }
            case "AppointmentProposal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.AppointmentProposal nestedClinicalStatement =
                        AppointmentProposalMapper.pushOut((AppointmentProposal) internalVMR, organizedResults);
                relatedClinicalStatement.setAppointmentProposal(nestedClinicalStatement);
            }
            case "AppointmentRequest" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.AppointmentRequest nestedClinicalStatement =
                        AppointmentRequestMapper.pushOut((AppointmentRequest) internalVMR, organizedResults);
                relatedClinicalStatement.setAppointmentRequest(nestedClinicalStatement);
            }
            case "MissedAppointment" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.MissedAppointment nestedClinicalStatement =
                        MissedAppointmentMapper.pushOut((MissedAppointment) internalVMR, organizedResults);
                relatedClinicalStatement.setMissedAppointment(nestedClinicalStatement);
            }
            case "ScheduledAppointment" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ScheduledAppointment nestedClinicalStatement =
                        ScheduledAppointmentMapper.pushOut((ScheduledAppointment) internalVMR, organizedResults);
                relatedClinicalStatement.setScheduledAppointment(nestedClinicalStatement);
            }
            case "Goal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.Goal nestedClinicalStatement =
                        GoalMapper.pushOut((Goal) internalVMR, organizedResults);
                relatedClinicalStatement.setGoal(nestedClinicalStatement);
            }
            case "GoalProposal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.GoalProposal nestedClinicalStatement =
                        GoalProposalMapper.pushOut((GoalProposal) internalVMR, organizedResults);
                relatedClinicalStatement.setGoalProposal(nestedClinicalStatement);
            }
            case "ObservationOrder" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ObservationOrder nestedClinicalStatement =
                        ObservationOrderMapper.pushOut((ObservationOrder) internalVMR, organizedResults);
                relatedClinicalStatement.setObservationOrder(nestedClinicalStatement);
            }
            case "ObservationProposal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ObservationProposal nestedClinicalStatement =
                        ObservationProposalMapper.pushOut((ObservationProposal) internalVMR, organizedResults);
                relatedClinicalStatement.setObservationProposal(nestedClinicalStatement);
            }
            case "ObservationResult" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ObservationResult nestedClinicalStatement =
                        ObservationResultMapper.pushOut((ObservationResult) internalVMR, organizedResults);
                relatedClinicalStatement.setObservationResult(nestedClinicalStatement);
            }
            case "UnconductedObservation" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.UnconductedObservation nestedClinicalStatement =
                        UnconductedObservationMapper.pushOut((UnconductedObservation) internalVMR, organizedResults);
                relatedClinicalStatement.setUnconductedObservation(nestedClinicalStatement);
            }
            case "Problem" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.Problem nestedClinicalStatement =
                        ProblemMapper.pushOut((Problem) internalVMR, organizedResults);
                relatedClinicalStatement.setProblem(nestedClinicalStatement);
            }
            case "ProcedureEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ProcedureEvent nestedClinicalStatement =
                        ProcedureEventMapper.pushOut((ProcedureEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setProcedureEvent(nestedClinicalStatement);
            }
            case "ProcedureOrder" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ProcedureOrder nestedClinicalStatement =
                        ProcedureOrderMapper.pushOut((ProcedureOrder) internalVMR, organizedResults);
                relatedClinicalStatement.setProcedureOrder(nestedClinicalStatement);
            }
            case "ProcedureProposal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.ProcedureProposal nestedClinicalStatement =
                        ProcedureProposalMapper.pushOut((ProcedureProposal) internalVMR, organizedResults);
                relatedClinicalStatement.setProcedureProposal(nestedClinicalStatement);
            }
            case "UndeliveredProcedure" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.UndeliveredProcedure nestedClinicalStatement =
                        UndeliveredProcedureMapper.pushOut((UndeliveredProcedure) internalVMR, organizedResults);
                relatedClinicalStatement.setUndeliveredProcedure(nestedClinicalStatement);
            }
            case "SubstanceAdministrationEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationEvent nestedClinicalStatement =
                        SubstanceAdministrationEventMapper.pushOut((SubstanceAdministrationEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setSubstanceAdministrationEvent(nestedClinicalStatement);
            }
            case "SubstanceAdministrationOrder" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationOrder nestedClinicalStatement =
                        SubstanceAdministrationOrderMapper.pushOut((SubstanceAdministrationOrder) internalVMR, organizedResults);
                relatedClinicalStatement.setSubstanceAdministrationOrder(nestedClinicalStatement);
            }
            case "SubstanceAdministrationProposal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SubstanceAdministrationProposal nestedClinicalStatement =
                        SubstanceAdministrationProposalMapper.pushOut((SubstanceAdministrationProposal) internalVMR,
                                organizedResults);
                relatedClinicalStatement.setSubstanceAdministrationProposal(nestedClinicalStatement);
            }
            case "UndeliveredSubstanceAdministration" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.UndeliveredSubstanceAdministration nestedClinicalStatement =
                        UndeliveredSubstanceAdministrationMapper.pushOut((UndeliveredSubstanceAdministration) internalVMR,
                                organizedResults);
                relatedClinicalStatement.setUndeliveredSubstanceAdministration(nestedClinicalStatement);
            }
            case "SubstanceDispensationEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SubstanceDispensationEvent nestedClinicalStatement =
                        SubstanceDispensationEventMapper.pushOut((SubstanceDispensationEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setSubstanceDispensationEvent(nestedClinicalStatement);
            }
            case "SupplyEvent" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SupplyEvent nestedClinicalStatement =
                        SupplyEventMapper.pushOut((SupplyEvent) internalVMR, organizedResults);
                relatedClinicalStatement.setSupplyEvent(nestedClinicalStatement);
            }
            case "SupplyOrder" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SupplyOrder nestedClinicalStatement =
                        SupplyOrderMapper.pushOut((SupplyOrder) internalVMR, organizedResults);
                relatedClinicalStatement.setSupplyOrder(nestedClinicalStatement);
            }
            case "SupplyProposal" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.SupplyProposal nestedClinicalStatement =
                        SupplyProposalMapper.pushOut((SupplyProposal) internalVMR, organizedResults);
                relatedClinicalStatement.setSupplyProposal(nestedClinicalStatement);
            }
            case "UndeliveredSupply" ->
            {
                relatedClinicalStatement = new org.opencds.vmr.v1_0.schema.RelatedClinicalStatement();
                final org.opencds.vmr.v1_0.schema.UndeliveredSupply nestedClinicalStatement =
                        UndeliveredSupplyMapper.pushOut((UndeliveredSupply) internalVMR, organizedResults);
                relatedClinicalStatement.setUndeliveredSupply(nestedClinicalStatement);
            }
        }

        return relatedClinicalStatement;
    }

    public static org.opencds.vmr.v1_0.schema.RelatedEntity pushOutRelatedEntityToClinicalStatement(
            final EntityRelationship internal, final org.opencds.vmr.v1_0.schema.ClinicalStatement external,
            final OrganizedResults organizedResults) throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final String _METHODNAME = "pushOutRelatedEntityToClinicalStatement(): ";

        if (internal == null)
        {
            final String errStr = _METHODNAME + "improper usage: source supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        if (external == null)
        {
            final String errStr = _METHODNAME + "improper usage: target supplied is null";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }

        final Object oneInternalEntityObject = organizedResults.entityList().get(internal.getTargetEntityId());
        if (oneInternalEntityObject == null)
        {
            final String errStr = _METHODNAME + "improper usage: looking up entity Object for ID=" + internal.getTargetEntityId()
                    + ", and it does not exist.";
            log.error(errStr);
            throw new ImproperUsageException(errStr);
        }
        final String oneInternalEntityObjectClassName = oneInternalEntityObject.getClass().getSimpleName();

        if (log.isTraceEnabled())
            log.trace(_METHODNAME
                            + "push out {}, sourceEntity Id {}, targetEntityId {}, relationshipTimeInterval {}, with relationship {}",
                    oneInternalEntityObjectClassName, internal.getSourceId(), internal.getTargetEntityId(),
                    internal.getRelationshipTimeInterval(), internal.getTargetRole().toString());

        if (internal.getSourceId().equals(internal.getTargetEntityId()))
        {
            throw new OpenCDSRuntimeException(
                    "root and/or extension of source and target IDs of the relationship may not be the same: source (root^extension)= "
                            + internal.getSourceId() + ", target (root^extension)= " + internal.getTargetEntityId());
        }

        switch (oneInternalEntityObjectClassName)
        {
            case "AdministrableSubstance" ->
            {
                final org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance schemaNestedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity.AdministrableSubstance();
                AdministrableSubstanceMapper.pushOut(
                        (AdministrableSubstance) organizedResults.entityList().get(internal.getTargetEntityId()),
                        schemaNestedEntity, organizedResults);

                final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity();

                schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
                schemaRelatedEntity.setRelationshipTimeInterval(
                        MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));
                schemaRelatedEntity.setAdministrableSubstance(schemaNestedEntity);
                return schemaRelatedEntity;

            }
            case "Entity" ->
            {
                final org.opencds.vmr.v1_0.schema.RelatedEntity.Entity schemaNestedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity.Entity();
                EntityMapper.pushOut((Entity) organizedResults.entityList().get(internal.getTargetEntityId()), schemaNestedEntity,
                        organizedResults);

                final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity();

                schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
                schemaRelatedEntity.setRelationshipTimeInterval(
                        MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));
                schemaRelatedEntity.setEntity(schemaNestedEntity);
                return schemaRelatedEntity;

            }
            case "Facility" ->
            {
                final org.opencds.vmr.v1_0.schema.RelatedEntity.Facility schemaNestedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity.Facility();
                FacilityMapper.pushOut((Facility) organizedResults.entityList().get(internal.getTargetEntityId()),
                        schemaNestedEntity, organizedResults);

                final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity();

                schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
                schemaRelatedEntity.setRelationshipTimeInterval(
                        MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));
                schemaRelatedEntity.setFacility(schemaNestedEntity);
                return schemaRelatedEntity;

            }
            case "Organization" ->
            {
                final org.opencds.vmr.v1_0.schema.RelatedEntity.Organization schemaNestedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity.Organization();
                OrganizationMapper.pushOut((Organization) organizedResults.entityList().get(internal.getTargetEntityId()),
                        schemaNestedEntity, organizedResults);

                final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity();

                schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
                schemaRelatedEntity.setRelationshipTimeInterval(
                        MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));
                schemaRelatedEntity.setOrganization(schemaNestedEntity);
                return schemaRelatedEntity;

            }
            case "Person" ->
            {
                final org.opencds.vmr.v1_0.schema.RelatedEntity.Person schemaNestedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity.Person();
                PersonMapper.pushOut((Person) organizedResults.entityList().get(internal.getTargetEntityId()), schemaNestedEntity,
                        organizedResults);

                final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity();

                schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
                schemaRelatedEntity.setRelationshipTimeInterval(
                        MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));
                schemaRelatedEntity.setPerson(schemaNestedEntity);
                return schemaRelatedEntity;

            }
            case "Specimen" ->
            {
                final org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen schemaNestedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity.Specimen();
                SpecimenMapper.pushOut((Specimen) organizedResults.entityList().get(internal.getTargetEntityId()),
                        schemaNestedEntity, organizedResults);

                final org.opencds.vmr.v1_0.schema.RelatedEntity schemaRelatedEntity =
                        new org.opencds.vmr.v1_0.schema.RelatedEntity();

                schemaRelatedEntity.setTargetRole(MappingUtility.cDInternal2CD(internal.getTargetRole()));
                schemaRelatedEntity.setRelationshipTimeInterval(
                        MappingUtility.iVLDateInternal2IVLTS(internal.getRelationshipTimeInterval()));
                schemaRelatedEntity.setSpecimen(schemaNestedEntity);
                return schemaRelatedEntity;

            }
            default ->
            {
                final String errStr =
                        _METHODNAME + "improper usage: listedEntity not recognized: " + oneInternalEntityObjectClassName;
                log.error(errStr);
                throw new InvalidDataException(errStr);
            }
        }
    }
}
