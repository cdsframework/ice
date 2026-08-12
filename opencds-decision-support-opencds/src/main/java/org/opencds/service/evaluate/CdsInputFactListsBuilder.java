package org.opencds.service.evaluate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.InvalidDriDataFormatException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.AdverseEvent;
import org.opencds.vmr.v1_0.internal.AppointmentProposal;
import org.opencds.vmr.v1_0.internal.AppointmentRequest;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.DeniedAdverseEvent;
import org.opencds.vmr.v1_0.internal.DeniedProblem;
import org.opencds.vmr.v1_0.internal.EncounterEvent;
import org.opencds.vmr.v1_0.internal.Entity;
import org.opencds.vmr.v1_0.internal.EvalTime;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.EvaluatedPersonRelationship;
import org.opencds.vmr.v1_0.internal.Facility;
import org.opencds.vmr.v1_0.internal.FocalPersonId;
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
import org.opencds.vmr.v1_0.internal.VMR;
import org.opencds.vmr.v1_0.mappings.in.BuildOpenCDSConceptLists;
import org.opencds.vmr.v1_0.mappings.in.FactLists;
import org.opencds.vmr.v1_0.mappings.mappers.AdministrableSubstanceMapper;
import org.opencds.vmr.v1_0.mappings.mappers.CDSInputMapper;
import org.opencds.vmr.v1_0.mappings.mappers.ClinicalStatementRelationshipMapper;
import org.opencds.vmr.v1_0.mappings.mappers.EntityMapper;
import org.opencds.vmr.v1_0.mappings.mappers.EntityRelationshipMapper;
import org.opencds.vmr.v1_0.mappings.mappers.EvaluatedPersonMapper;
import org.opencds.vmr.v1_0.mappings.mappers.EvaluatedPersonRelationshipMapper;
import org.opencds.vmr.v1_0.mappings.mappers.FacilityMapper;
import org.opencds.vmr.v1_0.mappings.mappers.OneObjectMapper;
import org.opencds.vmr.v1_0.mappings.mappers.OrganizationMapper;
import org.opencds.vmr.v1_0.mappings.mappers.PersonMapper;
import org.opencds.vmr.v1_0.mappings.mappers.SpecimenMapper;
import org.opencds.vmr.v1_0.mappings.mappers.VMRMapper;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class CdsInputFactListsBuilder
{
    public static Map<Class<?>, List<?>> buildFactLists(final KnowledgeRepository knowledgeRepository,
            final KnowledgeModule knowledgeModule, final Object payload, final LocalDate evalTime)
    {
        log.debug("buildFactLists");
        final long t0 = System.nanoTime();
        final Map<Class<?>, List<?>> allFactLists = new ConcurrentHashMap<>();

        try
        {
            final FactLists factLists = new FactLists();

            final org.opencds.vmr.v1_0.schema.CDSInput cdsInput = (org.opencds.vmr.v1_0.schema.CDSInput) payload;

            final EvalTime evalTimeFact = new EvalTime();
            evalTimeFact.setEvalTimeValue(evalTime);

            factLists.put(EvalTime.class, evalTimeFact);

            if ((cdsInput == null))
                throw new InvalidDriDataFormatException("Error: No payload within the CDSInput.");

            final FocalPersonId focalPersonId;

            final CDSInput internalCDSInput = new CDSInput();
            CDSInputMapper.pullIn(cdsInput, internalCDSInput);
            factLists.put(CDSInput.class, internalCDSInput);

            log.debug("CdsInputFactListsBuilder for focalPersonId={}", (Object) null);
            focalPersonId = new FocalPersonId(internalCDSInput.getFocalPersonId());
            factLists.put(FocalPersonId.class, focalPersonId);

            String subjectPersonId = focalPersonId.id();

            final org.opencds.vmr.v1_0.schema.VMR vmrInput = cdsInput.getVmrInput();

            final VMR internalVMR = new VMR();
            VMRMapper.pullIn(vmrInput, internalVMR);
            factLists.put(VMR.class, internalVMR);

            if (vmrInput.getPatient() != null)
                oneEvaluatedPerson(vmrInput.getPatient(), evalTime, subjectPersonId, focalPersonId.id(), factLists);

            if ((vmrInput.getOtherEvaluatedPersons() != null) && (vmrInput.getOtherEvaluatedPersons().getEvaluatedPerson() != null)
                    && (!vmrInput.getOtherEvaluatedPersons().getEvaluatedPerson().isEmpty()))
            {
                for (final org.opencds.vmr.v1_0.schema.EvaluatedPerson eachOtherEvaluatedPerson : vmrInput.getOtherEvaluatedPersons()
                        .getEvaluatedPerson())
                {
                    subjectPersonId = MappingUtility.iI2FlatId(eachOtherEvaluatedPerson.getId());
                    oneEvaluatedPerson(eachOtherEvaluatedPerson, evalTime, subjectPersonId, focalPersonId.id(), factLists);
                }
            }

            if ((vmrInput.getEvaluatedPersonRelationships() != null) && (
                    vmrInput.getEvaluatedPersonRelationships().getEvaluatedPersonRelationship() != null)
                    && (!vmrInput.getEvaluatedPersonRelationships().getEvaluatedPersonRelationship().isEmpty()))
            {
                for (final org.opencds.vmr.v1_0.schema.EntityRelationship each : vmrInput.getEvaluatedPersonRelationships()
                        .getEvaluatedPersonRelationship())
                    factLists.put(EvaluatedPersonRelationship.class,
                            EvaluatedPersonRelationshipMapper.pullIn(each, factLists.getParsedDatesCache()));
            }

            try
            {
                BuildOpenCDSConceptLists.buildConceptLists(knowledgeRepository.conceptService().byKM(knowledgeModule), factLists,
                        allFactLists);
            }
            catch (final Exception e)
            {
                log.error(e.getMessage(), e);
                throw new InvalidDriDataFormatException("BuildOpenCDSConceptLists threw error: " + e.getMessage(), e);
            }

            factLists.populate(allFactLists);

            log.debug("buildFactLists completed for {}", focalPersonId);
        }
        catch (final ImproperUsageException | DataFormatException | InvalidDataException e)
        {
            log.error(e.getMessage(), e);
            throw new InvalidDriDataFormatException(
                    "%s error in CdsInputFactListsBuilder: %s, therefore unable to complete unmarshalling input Semantic Payload: %s".formatted(
                            e.getClass().getSimpleName(), e.getMessage(), payload));
        }
        catch (final OpenCDSRuntimeException e)
        {
            throw new OpenCDSRuntimeException("RuntimeException in CdsInputFactListsBuilder: " + e.getMessage());
        }
        catch (final Exception e)
        {
            log.error(e.getMessage(), e);
            throw new InvalidDriDataFormatException(
                    "Unknown error initializing CdsInputFactListsBuilder: %s, therefore unable to complete unmarshalling input Semantic Payload: %s".formatted(
                            e.getMessage(), payload));
        }

        log.debug("CdsInputFactListsBuilder time : {} ms", (System.nanoTime() - t0) / 1e6);
        return allFactLists;
    }

    private static void oneEvaluatedPerson(final org.opencds.vmr.v1_0.schema.EvaluatedPerson inputPatient, final LocalDate evalTime,
            final String subjectPersonId, final String focalPersonId, final FactLists factLists)
            throws ImproperUsageException, DataFormatException, InvalidDataException
    {
        final EvaluatedPerson internalPatient = new EvaluatedPerson();
        EvaluatedPersonMapper.pullIn(inputPatient, internalPatient, null, null, subjectPersonId, focalPersonId, factLists);

        factLists.put(EvaluatedPerson.class, internalPatient);

        if (inputPatient.getClinicalStatementRelationships() != null)
        {
            final org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatementRelationships inputClinicalStatementRelationships =
                    inputPatient.getClinicalStatementRelationships();
            for (int i = 0; ((inputClinicalStatementRelationships.getClinicalStatementRelationship() != null) && (i
                    < inputClinicalStatementRelationships.getClinicalStatementRelationship().size())); i++)
            {
                ClinicalStatementRelationshipMapper.pullIn(
                        inputClinicalStatementRelationships.getClinicalStatementRelationship().get(i), factLists);
            }
        }

        final org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatements inputClinicalStatements =
                inputPatient.getClinicalStatements();

        if (inputClinicalStatements != null)
        {
            for (int i = 0; ((inputClinicalStatements.getAdverseEvents() != null) && (i < inputClinicalStatements.getAdverseEvents()
                    .getAdverseEvent()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getAdverseEvents().getAdverseEvent().get(i),
                        new AdverseEvent(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getDeniedAdverseEvents() != null) && (i
                    < inputClinicalStatements.getDeniedAdverseEvents().getDeniedAdverseEvent().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getDeniedAdverseEvents().getDeniedAdverseEvent().get(i), new DeniedAdverseEvent(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getAppointmentProposals() != null) && (i
                    < inputClinicalStatements.getAppointmentProposals().getAppointmentProposal().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getAppointmentProposals().getAppointmentProposal().get(i),
                        new AppointmentProposal(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getAppointmentRequests() != null) && (i
                    < inputClinicalStatements.getAppointmentRequests().getAppointmentRequest().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getAppointmentRequests().getAppointmentRequest().get(i), new AppointmentRequest(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getEncounterEvents() != null) && (i
                    < inputClinicalStatements.getEncounterEvents().getEncounterEvent().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getEncounterEvents().getEncounterEvent().get(i),
                        new EncounterEvent(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getMissedAppointments() != null) && (i
                    < inputClinicalStatements.getMissedAppointments().getMissedAppointment().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getMissedAppointments().getMissedAppointment().get(i), new MissedAppointment(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getScheduledAppointments() != null) && (i
                    < inputClinicalStatements.getScheduledAppointments().getScheduledAppointment().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getScheduledAppointments().getScheduledAppointment().get(i),
                        new ScheduledAppointment(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getGoals() != null) && (i < inputClinicalStatements.getGoals()
                    .getGoal()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getGoals().getGoal().get(i), new Goal(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getGoalProposals() != null) && (i < inputClinicalStatements.getGoalProposals()
                    .getGoalProposal()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getGoalProposals().getGoalProposal().get(i),
                        new GoalProposal(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getObservationOrders() != null) && (i
                    < inputClinicalStatements.getObservationOrders().getObservationOrder().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getObservationOrders().getObservationOrder().get(i),
                        new ObservationOrder(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getObservationProposals() != null) && (i
                    < inputClinicalStatements.getObservationProposals().getObservationProposal().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getObservationProposals().getObservationProposal().get(i),
                        new ObservationProposal(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getObservationResults() != null) && (i
                    < inputClinicalStatements.getObservationResults().getObservationResult().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getObservationResults().getObservationResult().get(i), new ObservationResult(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getUnconductedObservations() != null) && (i
                    < inputClinicalStatements.getUnconductedObservations().getUnconductedObservation().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getUnconductedObservations().getUnconductedObservation().get(i),
                        new UnconductedObservation(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getDeniedProblems() != null) && (i
                    < inputClinicalStatements.getDeniedProblems().getDeniedProblem().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getDeniedProblems().getDeniedProblem().get(i),
                        new DeniedProblem(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getProblems() != null) && (i < inputClinicalStatements.getProblems()
                    .getProblem()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getProblems().getProblem().get(i), new Problem(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getProcedureEvents() != null) && (i
                    < inputClinicalStatements.getProcedureEvents().getProcedureEvent().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getProcedureEvents().getProcedureEvent().get(i),
                        new ProcedureEvent(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getProcedureOrders() != null) && (i
                    < inputClinicalStatements.getProcedureOrders().getProcedureOrder().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getProcedureOrders().getProcedureOrder().get(i),
                        new ProcedureOrder(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getProcedureProposals() != null) && (i
                    < inputClinicalStatements.getProcedureProposals().getProcedureProposal().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getProcedureProposals().getProcedureProposal().get(i), new ProcedureProposal(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getScheduledProcedures() != null) && (i
                    < inputClinicalStatements.getScheduledProcedures().getScheduledProcedure().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getScheduledProcedures().getScheduledProcedure().get(i), new ScheduledProcedure(),
                        subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getUndeliveredProcedures() != null) && (i
                    < inputClinicalStatements.getUndeliveredProcedures().getUndeliveredProcedure().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getUndeliveredProcedures().getUndeliveredProcedure().get(i),
                        new UndeliveredProcedure(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSubstanceAdministrationEvents() != null) && (i
                    < inputClinicalStatements.getSubstanceAdministrationEvents().getSubstanceAdministrationEvent().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getSubstanceAdministrationEvents().getSubstanceAdministrationEvent().get(i),
                        new SubstanceAdministrationEvent(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSubstanceAdministrationOrders() != null) && (i
                    < inputClinicalStatements.getSubstanceAdministrationOrders().getSubstanceAdministrationOrder().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getSubstanceAdministrationOrders().getSubstanceAdministrationOrder().get(i),
                        new SubstanceAdministrationOrder(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSubstanceAdministrationProposals() != null) && (i
                    < inputClinicalStatements.getSubstanceAdministrationProposals()
                    .getSubstanceAdministrationProposal()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getSubstanceAdministrationProposals().getSubstanceAdministrationProposal().get(i),
                        new SubstanceAdministrationProposal(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSubstanceDispensationEvents() != null) && (i
                    < inputClinicalStatements.getSubstanceDispensationEvents().getSubstanceDispensationEvent().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getSubstanceDispensationEvents().getSubstanceDispensationEvent().get(i),
                        new SubstanceDispensationEvent(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getUndeliveredSubstanceAdministrations() != null) && (i
                    < inputClinicalStatements.getUndeliveredSubstanceAdministrations()
                    .getUndeliveredSubstanceAdministration()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getUndeliveredSubstanceAdministrations()
                        .getUndeliveredSubstanceAdministration()
                        .get(i), new UndeliveredSubstanceAdministration(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSupplyEvents() != null) && (i < inputClinicalStatements.getSupplyEvents()
                    .getSupplyEvent()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getSupplyEvents().getSupplyEvent().get(i),
                        new SupplyEvent(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSupplyOrders() != null) && (i < inputClinicalStatements.getSupplyOrders()
                    .getSupplyOrder()
                    .size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getSupplyOrders().getSupplyOrder().get(i),
                        new SupplyOrder(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getSupplyProposals() != null) && (i
                    < inputClinicalStatements.getSupplyProposals().getSupplyProposal().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(inputClinicalStatements.getSupplyProposals().getSupplyProposal().get(i),
                        new SupplyProposal(), subjectPersonId, focalPersonId, factLists);
            }

            for (int i = 0; ((inputClinicalStatements.getUndeliveredSupplies() != null) && (i
                    < inputClinicalStatements.getUndeliveredSupplies().getUndeliveredSupply().size())); i++)
            {
                OneObjectMapper.pullInClinicalStatement(
                        inputClinicalStatements.getUndeliveredSupplies().getUndeliveredSupply().get(i), new UndeliveredSupply(),
                        subjectPersonId, focalPersonId, factLists);
            }
        }

        if (inputPatient.getEntityLists() != null)
        {
            final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists inputEntityLists = inputPatient.getEntityLists();
            if (inputEntityLists.getAdministrableSubstances() != null)
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists.AdministrableSubstances administrableSubstances =
                        inputEntityLists.getAdministrableSubstances();
                for (int i = 0; ((administrableSubstances.getAdministrableSubstance() != null) && (i
                        < administrableSubstances.getAdministrableSubstance().size())); i++)
                {
                    AdministrableSubstanceMapper.pullIn(administrableSubstances.getAdministrableSubstance().get(i),
                            new AdministrableSubstance(), null, null, subjectPersonId, focalPersonId, factLists);
                }
            }
            if (inputEntityLists.getEntities() != null)
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists.Entities oneGroup = inputEntityLists.getEntities();
                for (int i = 0; ((oneGroup.getEntity() != null) && (i < oneGroup.getEntity().size())); i++)
                {
                    EntityMapper.pullIn(oneGroup.getEntity().get(i), new Entity(), null, null, subjectPersonId, focalPersonId,
                            factLists);
                }
            }
            if (inputEntityLists.getFacilities() != null)
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists.Facilities oneGroup =
                        inputEntityLists.getFacilities();
                for (int i = 0; ((oneGroup.getFacility() != null) && (i < oneGroup.getFacility().size())); i++)
                {
                    FacilityMapper.pullIn(oneGroup.getFacility().get(i), new Facility(), null, null, subjectPersonId, focalPersonId,
                            factLists);
                }
            }
            if (inputEntityLists.getOrganizations() != null)
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists.Organizations oneGroup =
                        inputEntityLists.getOrganizations();
                for (int i = 0; ((oneGroup.getOrganization() != null) && (i < oneGroup.getOrganization().size())); i++)
                {
                    OrganizationMapper.pullIn(oneGroup.getOrganization().get(i), new Organization(), null, null, subjectPersonId,
                            focalPersonId, factLists);
                }
            }
            if (inputEntityLists.getPersons() != null)
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists.Persons oneGroup = inputEntityLists.getPersons();
                for (int i = 0; ((oneGroup.getPerson() != null) && (i < oneGroup.getPerson().size())); i++)
                {
                    PersonMapper.pullIn(oneGroup.getPerson().get(i), new Person(), null, null, subjectPersonId, focalPersonId,
                            factLists);
                }
            }
            if (inputEntityLists.getSpecimens() != null)
            {
                final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityLists.Specimens oneGroup = inputEntityLists.getSpecimens();
                for (int i = 0; ((oneGroup.getSpecimen() != null) && (i < oneGroup.getSpecimen().size())); i++)
                {
                    SpecimenMapper.pullIn(oneGroup.getSpecimen().get(i), new Specimen(), null, null, subjectPersonId, focalPersonId,
                            factLists);
                }
            }
        }

        if (inputPatient.getEntityRelationships() != null)
        {
            final org.opencds.vmr.v1_0.schema.EvaluatedPerson.EntityRelationships inputEntityRelationships =
                    inputPatient.getEntityRelationships();
            for (int i = 0; ((inputEntityRelationships.getEntityRelationship() != null) && (i
                    < inputEntityRelationships.getEntityRelationship().size())); i++)
            {
                final org.opencds.vmr.v1_0.schema.EntityRelationship inputEntityRelationship =
                        inputEntityRelationships.getEntityRelationship().get(i);
                EntityRelationshipMapper.pullIn(inputEntityRelationship.getSourceId(), inputEntityRelationship.getTargetEntityId(),
                        inputEntityRelationship.getTargetRole(), inputEntityRelationship.getRelationshipTimeInterval(), factLists);
            }
        }

        if (inputPatient.getClinicalStatementEntityInRoleRelationships() != null)
        {
            final org.opencds.vmr.v1_0.schema.EvaluatedPerson.ClinicalStatementEntityInRoleRelationships
                    inputClinicalStatementEntityInRoleRelationships = inputPatient.getClinicalStatementEntityInRoleRelationships();
            for (int i = 0; (
                    (inputClinicalStatementEntityInRoleRelationships.getClinicalStatementEntityInRoleRelationship() != null) && (i
                            < inputClinicalStatementEntityInRoleRelationships.getClinicalStatementEntityInRoleRelationship()
                            .size())); i++)
            {
                final org.opencds.vmr.v1_0.schema.EntityRelationship inputEntityRelationship =
                        inputClinicalStatementEntityInRoleRelationships.getClinicalStatementEntityInRoleRelationship().get(i);

                EntityRelationshipMapper.pullIn(inputEntityRelationship.getSourceId(), inputEntityRelationship.getTargetEntityId(),
                        inputEntityRelationship.getTargetRole(), inputEntityRelationship.getRelationshipTimeInterval(), factLists);
            }
        }
    }
}
