package org.opencds.service.evaluate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CdsOutputResultSetBuilder implements ResultSetBuilder<org.opencds.vmr.v1_0.schema.CDSOutput>
{
    private static final String[] clinicalStatementNames =
            { "AdverseEvent", "DeniedAdverseEvent", "AppointmentProposal", "AppointmentRequest", "EncounterEvent",
                    "MissedAppointment", "ScheduledAppointment", "Goal", "GoalProposal", "ObservationOrder", "ObservationProposal",
                    "ObservationResult", "UnconductedObservation", "DeniedProblem", "Problem", "ProcedureEvent", "ProcedureOrder",
                    "ProcedureProposal", "ScheduledProcedure", "UndeliveredProcedure", "SubstanceAdministrationEvent",
                    "SubstanceAdministrationOrder", "SubstanceAdministrationProposal", "SubstanceDispensationEvent",
                    "UndeliveredSubstanceAdministration", "SupplyEvent", "SupplyOrder", "SupplyProposal", "UndeliveredSupply" };
    private static final Set<String> csNameList = new HashSet<>(Arrays.asList(clinicalStatementNames));
    private static final String[] entityNames =
            { "AdministrableSubstance", "Entity", "EvaluatedPerson", "Facility", "Organization", "Person", "Specimen" };
    private static final Set<String> entityNameList = new HashSet<>(Arrays.asList(entityNames));
    private static final String[] outputNames =
            { "AdverseEvent", "DeniedAdverseEvent", "AppointmentProposal", "AppointmentRequest", "EncounterEvent",
                    "MissedAppointment", "ScheduledAppointment", "Goal", "GoalProposal", "ObservationOrder", "ObservationProposal",
                    "ObservationResult", "UnconductedObservation", "DeniedProblem", "Problem", "ProcedureEvent", "ProcedureOrder",
                    "ProcedureProposal", "ScheduledProcedure", "UndeliveredProcedure", "SubstanceAdministrationEvent",
                    "SubstanceAdministrationOrder", "SubstanceAdministrationProposal", "SubstanceDispensationEvent",
                    "UndeliveredSubstanceAdministration", "SupplyEvent", "SupplyOrder", "SupplyProposal", "UndeliveredSupply",
                    "AdministrableSubstance", "Entity", "EvaluatedPerson", "Facility", "Organization", "Person", "Specimen",
                    "ClinicalStatementRelationship", "EntityRelationship" };
    private static final List<String> outputNameList = Arrays.asList(outputNames);

    @Override
    public org.opencds.vmr.v1_0.schema.CDSOutput buildResultSet(final Map<String, List<?>> results,
            final EvaluationRequestKMItem dssRequestKMItem)
    {
        final org.opencds.vmr.v1_0.schema.CDSOutput cdsOutput = new org.opencds.vmr.v1_0.schema.CDSOutput();
        final List<FocalPersonId> focalPersonIds = getResults(results, "FocalPersonId");
        String focalPersonId = null;
        if (focalPersonIds != null && !focalPersonIds.isEmpty())
        {
            focalPersonId = focalPersonIds.getFirst().id();
        }

        final List<ClinicalStatementRelationship> allClinicalStmtRels = getResults(results, "ClinicalStatementRelationship");
        final Map<String, List<ClinicalStatementRelationship>> clinicalStmtRelsByTargetId =
                allClinicalStmtRels != null ? allClinicalStmtRels.stream()
                        .collect(Collectors.groupingBy(ClinicalStatementRelationship::getTargetId)) : new HashMap<>();

        if (getResults(results, "CDSInput") != null)
        {
            final List<CDSInput> internalCdsInputList = getResults(results, "CDSInput");
            try
            {
                for (final CDSInput internalCdsInput : internalCdsInputList)
                {
                    CDSOutputMapper.pushOut(results, internalCdsInput, cdsOutput, focalPersonId);

                    final List<EvaluatedPerson> evalPersons = getResults(results, "EvaluatedPerson");
                    for (final EvaluatedPerson internalEvaluatedPerson : evalPersons)
                    {
                        final org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements outputClinicalStatements =
                                new org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements();
                        final String subjectPersonId = internalEvaluatedPerson.getEvaluatedPersonId();

                        final Map<String, List<ClinicalStatement>> csChildren = new HashMap<>();
                        //key = clinical statement id, value = clinical statement object
                        final Map<String, EntityBase> entityChildren = new HashMap<>();
                        //key = entity ID, value = entity object
                        final Map<String, List<EntityRelationship>> entityRelationships = new HashMap<>();
                        //key = sourceId, value = List of all entity relationship objects for that sourceId

                        for (final String oneFactListName : outputNameList)
                        {
                            final List<?> oneResultList = getResults(results, oneFactListName);
                            if ((oneResultList != null) && (!oneResultList.isEmpty()))
                            {

                                for (final Object oneResult : oneResultList)
                                {

                                    final String oneResultClassName = oneResult.getClass().getSimpleName();
                                    if (csNameList.contains(oneResultClassName))
                                    {

                                        final ClinicalStatement oneCSResult = ((ClinicalStatement) oneResult);
                                        if (oneCSResult.isToBeReturned() && subjectPersonId.equals(
                                                oneCSResult.getEvaluatedPersonId()))
                                        {

                                            final List<ClinicalStatementRelationship> clinicalStmtRels =
                                                    clinicalStmtRelsByTargetId.get(oneCSResult.getId());
                                            if (clinicalStmtRels != null)
                                            {
                                                for (final ClinicalStatementRelationship oneClinicalStatementRelationship : clinicalStmtRels)
                                                {

                                                    if (oneCSResult.isClinicalStatementToBeRoot())
                                                    {
                                                        final String warningMsg =
                                                                "WARNING: CdsOutputResultSetBuilder found a Clinical Statement id "
                                                                        + oneClinicalStatementRelationship.getTargetId()
                                                                        + " which is flagged "
                                                                        + "as isClinicalStatementToBeRoot() == true, and is also the target of "
                                                                        + "a Clinical Statement Relationship to id "
                                                                        + oneClinicalStatementRelationship.getSourceId() + ".";
                                                        log.warn(warningMsg);
                                                    }

                                                    if (oneCSResult.getRelationshipToSources() == null)
                                                    {
                                                        final List<RelationshipToSource> relationshipToSources = new ArrayList<>();
                                                        oneCSResult.setRelationshipToSources(relationshipToSources);
                                                    }
                                                    boolean alreadyThere = false;
                                                    for (final RelationshipToSource oneRTS : oneCSResult.getRelationshipToSources())
                                                    {
                                                        if (oneCSResult.getId().equals(oneRTS.getSourceId()))
                                                        {
                                                            alreadyThere = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!alreadyThere)
                                                    {
                                                        final RelationshipToSource newRelationshipToSource =
                                                                new RelationshipToSource();
                                                        newRelationshipToSource.setSourceId(
                                                                oneClinicalStatementRelationship.getSourceId());
                                                        newRelationshipToSource.setRelationshipToSource(
                                                                oneClinicalStatementRelationship.getTargetRelationshipToSource());
                                                        oneCSResult.getRelationshipToSources().add(newRelationshipToSource);
                                                    }

                                                    final String key = oneClinicalStatementRelationship.getSourceId();
                                                    csChildren.computeIfAbsent(key, k -> new ArrayList<>()).add(oneCSResult);
                                                }
                                            }
                                        }

                                    }
                                    else
                                        if (entityNameList.contains(oneResultClassName))
                                        {
                                            final EntityBase oneEBresult = (EntityBase) oneResult;
                                            if (subjectPersonId.equals(oneEBresult.getEvaluatedPersonId()))
                                            {

                                                final String key = oneEBresult.getId();
                                                entityChildren.put(key, oneEBresult);
                                            }

                                        }
                                        else
                                            if ("EntityRelationship".equals(oneResultClassName))
                                            {
                                                final EntityRelationship oneERresult = (EntityRelationship) oneResult;

                                                if (oneERresult.getSourceId() != null)
                                                {
                                                    final String key = oneERresult.getSourceId();
                                                    entityRelationships.computeIfAbsent(key, k -> new ArrayList<>())
                                                            .add(oneERresult);
                                                }
                                            }
                                }
                            }
                        }

                        for (final String oneName : csNameList)
                        {
                            final List<?> oneResultList = getResults(results, oneName);
                            if ((oneResultList != null) && (!oneResultList.isEmpty()))
                            {

                                for (final Object oneResult : oneResultList)
                                {
                                    if ((oneResult.getClass().getSuperclass() != null) && (
                                            oneResult.getClass().getSuperclass().getSuperclass() != null)
                                            && ("ClinicalStatement".equals(
                                            oneResult.getClass().getSuperclass().getSuperclass().getSimpleName())))
                                    {
                                        if (((ClinicalStatement) oneResult).isClinicalStatementToBeRoot() && subjectPersonId.equals(
                                                ((ClinicalStatement) oneResult).getEvaluatedPersonId()))
                                        {

                                            final Object target = new Object();
                                            final OrganizedResults organizedResults =
                                                    new OrganizedResults(outputClinicalStatements, subjectPersonId, focalPersonId,
                                                            results, csChildren, entityChildren, entityRelationships);
                                            OneObjectMapper.pushOutRootClinicalStatement(oneResult, target, organizedResults);
                                        }
                                    }
                                }
                            }
                        }

                        if (subjectPersonId.equals(focalPersonId))
                        {

                            cdsOutput.getVmrOutput().getPatient().setClinicalStatements(outputClinicalStatements);
                        }
                        else
                        {

                            for (int i = 0;
                                 i < cdsOutput.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().size(); i++)
                            {
                                final org.opencds.vmr.v1_0.schema.EvaluatedPerson oneOtherEvaluatedPersonOut =
                                        cdsOutput.getVmrOutput().getOtherEvaluatedPersons().getEvaluatedPerson().get(i);
                                if (subjectPersonId.equals(MappingUtility.iI2FlatId(oneOtherEvaluatedPersonOut.getId())))
                                {

                                    oneOtherEvaluatedPersonOut.setClinicalStatements(outputClinicalStatements);
                                    cdsOutput.getVmrOutput()
                                            .getOtherEvaluatedPersons()
                                            .getEvaluatedPerson()
                                            .set(i, oneOtherEvaluatedPersonOut);
                                }
                            }
                        }
                    }
                }
            }
            catch (final DataFormatException | InvalidDataException | ImproperUsageException e)
            {
                throw new OpenCDSRuntimeException("CdsOutputResultSetBuilder received a " + e.getClass().getSimpleName()
                        + " while doing a CDSOutputMapper.pushOut: " + e.getMessage(), e);
            }
            catch (final StackOverflowError e)
            {
                throw new OpenCDSRuntimeException(
                        "CdsOutputResultSetBuilder received a StackOverflowError, possibly caused by duplicate ids in the output from the ExecutionEngine",
                        e);
            }
        }
        return cdsOutput;
    }

    private static <T> List<T> getResults(final Map<String, List<?>> results, final String key)
    {
        return (List<T>) results.get(key);
    }
}
