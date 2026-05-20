package org.opencds.service.evaluate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
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
import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class CdsOutputResultSetBuilder
{
    private static final Set<String> csNameList =
            Set.of("AdverseEvent", "DeniedAdverseEvent", "AppointmentProposal", "AppointmentRequest", "EncounterEvent",
                    "MissedAppointment", "ScheduledAppointment", "Goal", "GoalProposal", "ObservationOrder", "ObservationProposal",
                    "ObservationResult", "UnconductedObservation", "DeniedProblem", "Problem", "ProcedureEvent", "ProcedureOrder",
                    "ProcedureProposal", "ScheduledProcedure", "UndeliveredProcedure", "SubstanceAdministrationEvent",
                    "SubstanceAdministrationOrder", "SubstanceAdministrationProposal", "SubstanceDispensationEvent",
                    "UndeliveredSubstanceAdministration", "SupplyEvent", "SupplyOrder", "SupplyProposal", "UndeliveredSupply");
    private static final Set<String> entityNameList =
            Set.of("AdministrableSubstance", "Entity", "EvaluatedPerson", "Facility", "Organization", "Person", "Specimen");
    private static final List<String> outputNameList =
            List.of("AdverseEvent", "DeniedAdverseEvent", "AppointmentProposal", "AppointmentRequest", "EncounterEvent",
                    "MissedAppointment", "ScheduledAppointment", "Goal", "GoalProposal", "ObservationOrder", "ObservationProposal",
                    "ObservationResult", "UnconductedObservation", "DeniedProblem", "Problem", "ProcedureEvent", "ProcedureOrder",
                    "ProcedureProposal", "ScheduledProcedure", "UndeliveredProcedure", "SubstanceAdministrationEvent",
                    "SubstanceAdministrationOrder", "SubstanceAdministrationProposal", "SubstanceDispensationEvent",
                    "UndeliveredSubstanceAdministration", "SupplyEvent", "SupplyOrder", "SupplyProposal", "UndeliveredSupply",
                    "AdministrableSubstance", "Entity", "EvaluatedPerson", "Facility", "Organization", "Person", "Specimen",
                    "ClinicalStatementRelationship", "EntityRelationship");

    @SuppressWarnings("unchecked")
    private static <T> List<T> getResults(final Map<String, List<?>> results, final String key)
    {
        return (List<T>) results.get(key);
    }

    public static org.opencds.vmr.v1_0.schema.CDSOutput buildResultSet(final Map<String, List<?>> results)
    {
        final org.opencds.vmr.v1_0.schema.CDSOutput cdsOutput = new org.opencds.vmr.v1_0.schema.CDSOutput();
        final List<FocalPersonId> focalPersonIds = getResults(results, "FocalPersonId");
        final String focalPersonId = ObjectUtils.isEmpty(focalPersonIds) ? null : focalPersonIds.getFirst().id();

        final List<ClinicalStatementRelationship> allClinicalStmtRels = getResults(results, "ClinicalStatementRelationship");
        final Map<String, List<ClinicalStatementRelationship>> clinicalStmtRelsByTargetId = allClinicalStmtRels == null
                                                                                            ? Map.of()
                                                                                            : allClinicalStmtRels.stream()
                                                                                              .collect(Collectors.groupingBy(
                                                                                                      ClinicalStatementRelationship::getTargetId));

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
                            if (!ObjectUtils.isEmpty(oneResultList))
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
                                                        log.warn(
                                                                "WARNING: CdsOutputResultSetBuilder found a Clinical Statement id {} which is flagged as isClinicalStatementToBeRoot() == true, and is also the target of a Clinical Statement Relationship to id {}.",
                                                                oneClinicalStatementRelationship.getTargetId(),
                                                                oneClinicalStatementRelationship.getSourceId());

                                                    if (oneCSResult.getRelationshipToSources() == null)
                                                        oneCSResult.setRelationshipToSources(new ArrayList<>());

                                                    if (oneCSResult.getRelationshipToSources()
                                                            .stream()
                                                            .noneMatch(oneRTS -> oneCSResult.getId().equals(oneRTS.getSourceId())))
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
                                                    csChildren.computeIfAbsent(key, _ -> new ArrayList<>()).add(oneCSResult);
                                                }
                                            }
                                        }

                                    }
                                    else
                                        if (entityNameList.contains(oneResultClassName))
                                        {
                                            final EntityBase oneEBresult = (EntityBase) oneResult;
                                            if (subjectPersonId.equals(oneEBresult.getEvaluatedPersonId()))
                                                entityChildren.put(oneEBresult.getId(), oneEBresult);
                                        }
                                        else
                                            if ("EntityRelationship".equals(oneResultClassName))
                                            {
                                                final EntityRelationship oneERresult = (EntityRelationship) oneResult;

                                                if (oneERresult.getSourceId() != null)
                                                    entityRelationships.computeIfAbsent(oneERresult.getSourceId(),
                                                            _ -> new ArrayList<>()).add(oneERresult);
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
                                            oneResult.getClass().getSuperclass().getSuperclass().getSimpleName()))
                                            && ((ClinicalStatement) oneResult).isClinicalStatementToBeRoot()
                                            && subjectPersonId.equals(((ClinicalStatement) oneResult).getEvaluatedPersonId()))
                                        OneObjectMapper.pushOutRootClinicalStatement(oneResult, new Object(),
                                                new OrganizedResults(outputClinicalStatements, subjectPersonId, focalPersonId,
                                                        results, csChildren, entityChildren, entityRelationships));
                                }
                            }
                        }

                        if (subjectPersonId.equals(focalPersonId))
                            cdsOutput.getVmrOutput().getPatient().setClinicalStatements(outputClinicalStatements);
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
                throw new OpenCDSRuntimeException(
                        "CdsOutputResultSetBuilder received a %s while doing a CDSOutputMapper.pushOut: %s".formatted(
                                e.getClass().getSimpleName(), e.getMessage()), e);
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
}
