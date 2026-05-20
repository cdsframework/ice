package org.opencds.vmr.v1_0.mappings.in;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.terminology.CodeSystems;
import org.opencds.common.utilities.MiscUtility;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptView;
import org.opencds.config.api.service.ConceptService;
import org.opencds.vmr.v1_0.internal.AdverseEvent;
import org.opencds.vmr.v1_0.internal.AdverseEventBase;
import org.opencds.vmr.v1_0.internal.AppointmentProposal;
import org.opencds.vmr.v1_0.internal.AppointmentRequest;
import org.opencds.vmr.v1_0.internal.BodySite;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.Demographics;
import org.opencds.vmr.v1_0.internal.DeniedAdverseEvent;
import org.opencds.vmr.v1_0.internal.DeniedProblem;
import org.opencds.vmr.v1_0.internal.EncounterBase;
import org.opencds.vmr.v1_0.internal.EncounterEvent;
import org.opencds.vmr.v1_0.internal.Entity;
import org.opencds.vmr.v1_0.internal.EntityBase;
import org.opencds.vmr.v1_0.internal.EntityRelationship;
import org.opencds.vmr.v1_0.internal.EvaluatedPerson;
import org.opencds.vmr.v1_0.internal.EvaluatedPersonRelationship;
import org.opencds.vmr.v1_0.internal.Facility;
import org.opencds.vmr.v1_0.internal.Goal;
import org.opencds.vmr.v1_0.internal.GoalBase;
import org.opencds.vmr.v1_0.internal.GoalProposal;
import org.opencds.vmr.v1_0.internal.MissedAppointment;
import org.opencds.vmr.v1_0.internal.ObservationBase;
import org.opencds.vmr.v1_0.internal.ObservationOrder;
import org.opencds.vmr.v1_0.internal.ObservationProposal;
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.Organization;
import org.opencds.vmr.v1_0.internal.Person;
import org.opencds.vmr.v1_0.internal.Problem;
import org.opencds.vmr.v1_0.internal.ProblemBase;
import org.opencds.vmr.v1_0.internal.ProcedureBase;
import org.opencds.vmr.v1_0.internal.ProcedureEvent;
import org.opencds.vmr.v1_0.internal.ProcedureOrder;
import org.opencds.vmr.v1_0.internal.ProcedureProposal;
import org.opencds.vmr.v1_0.internal.ScheduledAppointment;
import org.opencds.vmr.v1_0.internal.ScheduledProcedure;
import org.opencds.vmr.v1_0.internal.Specimen;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationBase;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationOrder;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationProposal;
import org.opencds.vmr.v1_0.internal.SubstanceDispensationEvent;
import org.opencds.vmr.v1_0.internal.SupplyBase;
import org.opencds.vmr.v1_0.internal.SupplyEvent;
import org.opencds.vmr.v1_0.internal.SupplyOrder;
import org.opencds.vmr.v1_0.internal.SupplyProposal;
import org.opencds.vmr.v1_0.internal.UnconductedObservation;
import org.opencds.vmr.v1_0.internal.UndeliveredProcedure;
import org.opencds.vmr.v1_0.internal.UndeliveredSubstanceAdministration;
import org.opencds.vmr.v1_0.internal.UndeliveredSupply;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventAffectedBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventAffectedBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventAgentConcept;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventConcept;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventSeverityConcept;
import org.opencds.vmr.v1_0.internal.concepts.AdverseEventStatusConcept;
import org.opencds.vmr.v1_0.internal.concepts.BrandedMedicationConcept;
import org.opencds.vmr.v1_0.internal.concepts.ClinicalStatementRelationshipConcept;
import org.opencds.vmr.v1_0.internal.concepts.ClinicalStatementTemplateConcept;
import org.opencds.vmr.v1_0.internal.concepts.DataSourceTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.DoseTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.DosingSigConcept;
import org.opencds.vmr.v1_0.internal.concepts.EncounterCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.EncounterTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.EntityRelationshipConcept;
import org.opencds.vmr.v1_0.internal.concepts.EntityTemplateConcept;
import org.opencds.vmr.v1_0.internal.concepts.EntityTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.EthnicityConcept;
import org.opencds.vmr.v1_0.internal.concepts.EvaluatedPersonRelationshipConcept;
import org.opencds.vmr.v1_0.internal.concepts.GenderConcept;
import org.opencds.vmr.v1_0.internal.concepts.GenericMedicationConcept;
import org.opencds.vmr.v1_0.internal.concepts.GoalCodedValueConcept;
import org.opencds.vmr.v1_0.internal.concepts.GoalCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.GoalStatusConcept;
import org.opencds.vmr.v1_0.internal.concepts.GoalTargetBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.GoalTargetBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;
import org.opencds.vmr.v1_0.internal.concepts.InformationAttestationTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.ManufacturerConcept;
import org.opencds.vmr.v1_0.internal.concepts.MedicationClassConcept;
import org.opencds.vmr.v1_0.internal.concepts.MedicationConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationCodedValueConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationFocusConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationInterpretationConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationMethodConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationTargetBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationTargetBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ObservationUnconductedReasonConcept;
import org.opencds.vmr.v1_0.internal.concepts.PreferredLanguageConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemAffectedBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemAffectedBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemImportanceConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemSeverityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemStatusConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureApproachBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureApproachBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureMethodConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureTargetBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureTargetBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.RaceConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceAdministrationApproachBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceAdministrationApproachBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceAdministrationCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceAdministrationGeneralPurposeConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceAdministrationTargetBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceAdministrationTargetBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceDeliveryMethodConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceDeliveryRouteConcept;
import org.opencds.vmr.v1_0.internal.concepts.SubstanceFormConcept;
import org.opencds.vmr.v1_0.internal.concepts.SupplyConcept;
import org.opencds.vmr.v1_0.internal.concepts.SupplyCriticalityConcept;
import org.opencds.vmr.v1_0.internal.concepts.SupplyTargetBodySiteConcept;
import org.opencds.vmr.v1_0.internal.concepts.SupplyTargetBodySiteLateralityConcept;
import org.opencds.vmr.v1_0.internal.concepts.SupplyUndeliveredReasonConcept;
import org.opencds.vmr.v1_0.internal.concepts.UndeliveredProcedureReasonConcept;
import org.opencds.vmr.v1_0.internal.concepts.UndeliveredSubstanceAdministrationReasonConcept;
import org.opencds.vmr.v1_0.internal.concepts.VmrOpenCdsConcept;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class BuildOpenCDSConceptLists
{
    public static void buildConceptLists(final ConceptService conceptService, final FactLists factLists,
            final Map<Class<?>, List<?>> allFactLists)
    {
        final long t0 = System.nanoTime();
        final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists = new ConcurrentHashMap<>();

        processEvaluatedPersons(conceptService, factLists.get(EvaluatedPerson.class), conceptLists);
        processEntities(conceptService, factLists.get(Entity.class), conceptLists);
        processEntities(conceptService, factLists.get(Facility.class), conceptLists);
        processEntities(conceptService, factLists.get(Organization.class), conceptLists);
        processEntities(conceptService, factLists.get(Person.class), conceptLists);
        processEntities(conceptService, factLists.get(Specimen.class), conceptLists);

        processClinicalStatementRelationships(conceptService, factLists.get(ClinicalStatementRelationship.class), conceptLists);
        processEntityRelationships(conceptService, factLists.get(EntityRelationship.class), conceptLists);
        processEvaluatedPersonRelationShips(conceptService, factLists.get(EvaluatedPersonRelationship.class), conceptLists);

        processAdverseEvents(conceptService, factLists.get(AdverseEvent.class), conceptLists);
        processDeniedAdverseEvents(conceptService, factLists.get(DeniedAdverseEvent.class), conceptLists);

        processAppointmentProposals(conceptService, factLists.get(AppointmentProposal.class), conceptLists);
        processAppointmentRequests(conceptService, factLists.get(AppointmentRequest.class), conceptLists);

        processEncounterEvents(conceptService, factLists.get(EncounterEvent.class), conceptLists);

        processMissedAppointments(conceptService, factLists.get(MissedAppointment.class), conceptLists);
        processScheduledAppointments(conceptService, factLists.get(ScheduledAppointment.class), conceptLists);

        processGoals(conceptService, factLists.get(Goal.class), conceptLists);
        processGoalProposals(conceptService, factLists.get(GoalProposal.class), conceptLists);

        processObservationOrders(conceptService, factLists.get(ObservationOrder.class), conceptLists);
        processObservationProposals(conceptService, factLists.get(ObservationProposal.class), conceptLists);
        processObservationResults(conceptService, factLists.get(ObservationResult.class), conceptLists);
        processUnconductedObservations(conceptService, factLists.get(UnconductedObservation.class), conceptLists);

        processDeniedProblems(conceptService, factLists.get(DeniedProblem.class), conceptLists);
        processProblems(conceptService, factLists.get(Problem.class), conceptLists);

        processProcedureEvents(conceptService, factLists.get(ProcedureEvent.class), conceptLists);
        processProcedureOrders(conceptService, factLists.get(ProcedureOrder.class), conceptLists);
        processProcedureProposals(conceptService, factLists.get(ProcedureProposal.class), conceptLists);
        processScheduledProcedures(conceptService, factLists.get(ScheduledProcedure.class), conceptLists);
        processUndeliveredProcedures(conceptService, factLists.get(UndeliveredProcedure.class), conceptLists);

        processSubstanceAdministrationEvents(conceptService, factLists.get(SubstanceAdministrationEvent.class), conceptLists);
        processSubstanceAdministrationOrder(conceptService, factLists.get(SubstanceAdministrationOrder.class), conceptLists);
        processSubstanceAdministrationProposal(conceptService, factLists.get(SubstanceAdministrationProposal.class), conceptLists);
        processSubstanceDispensationEvent(conceptService, factLists.get(SubstanceDispensationEvent.class), conceptLists);
        processUndeliveredSubstanceAdministration(conceptService, factLists.get(UndeliveredSubstanceAdministration.class),
                conceptLists);

        processSupplyEvent(conceptService, factLists.get(SupplyEvent.class), conceptLists);
        processSupplyOrder(conceptService, factLists.get(SupplyOrder.class), conceptLists);
        processSupplyProposal(conceptService, factLists.get(SupplyProposal.class), conceptLists);
        processUndeliveredSupply(conceptService, factLists.get(UndeliveredSupply.class), conceptLists);

        allFactLists.putAll(conceptLists);

        log.debug("BuildOpenCDSConceptLists time : {} ms", (System.nanoTime() - t0) / 1e6);
    }

    private static <EB extends EntityBase> void processEntities(final ConceptService conceptService, final List<EB> entities,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (entities == null)
            return;

        for (final EB entity : entities)
            processEntity(conceptService, entity, conceptLists);
    }

    private static void processEntity(final ConceptService conceptService, final EntityBase entity,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, entity.getId(), entity.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, EntityTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, entity.getId(), entity.getEntityType(), EntityTypeConcept.class, conceptLists);
    }

    private static void processEvaluatedPersons(final ConceptService conceptService, final List<EvaluatedPerson> evaluatedPersons,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (evaluatedPersons == null)
            return;

        for (final EvaluatedPerson evaluatedPerson : evaluatedPersons)
        {
            processEntity(conceptService, evaluatedPerson, conceptLists);

            if (evaluatedPerson.getDemographics() != null)
            {
                final Demographics demographics = evaluatedPerson.getDemographics();

                if (demographics.getGender() != null)
                {
                    populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getGender(),
                            GenderConcept.class, conceptLists);
                }

                if (demographics.getRace() != null)
                {
                    populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getRace(), RaceConcept.class,
                            conceptLists);
                }

                if (demographics.getEthnicity() != null)
                {
                    populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getEthnicity(),
                            EthnicityConcept.class, conceptLists);
                }

                if (demographics.getPreferredLanguage() != null)
                {
                    populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getPreferredLanguage(),
                            PreferredLanguageConcept.class, conceptLists);
                }
            }
        }
    }

    private static void processEvaluatedPersonRelationShips(final ConceptService conceptService,
            final List<EvaluatedPersonRelationship> eprs,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (eprs == null)
            return;

        for (final EvaluatedPersonRelationship epr : eprs)
        {
            populateVmrOpenCdsConcept(conceptService, epr.getId(), epr.getTargetRole(), EvaluatedPersonRelationshipConcept.class,
                    conceptLists);
        }
    }

    private static void processEntityRelationships(final ConceptService conceptService, final List<EntityRelationship> ers,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (ers == null)
            return;

        for (final EntityRelationship er : ers)
        {
            populateVmrOpenCdsConcept(conceptService, er.getId(), er.getTargetRole(), EntityRelationshipConcept.class,
                    conceptLists);
        }
    }

    private static void processClinicalStatementRelationships(final ConceptService conceptService,
            final List<ClinicalStatementRelationship> csrs,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (csrs == null)
            return;

        for (final ClinicalStatementRelationship csr : csrs)
        {
            populateVmrOpenCdsConcept(conceptService, csr.getId(), csr.getTargetRelationshipToSource(),
                    ClinicalStatementRelationshipConcept.class, conceptLists);
        }
    }

    private static <AEB extends AdverseEventBase> void processAdverseEventBase(final ConceptService conceptService,
            final List<AEB> aebs, final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (aebs == null)
            return;

        for (final AEB aeb : aebs)
            processAdverseEventBase(conceptService, aeb, conceptLists);
    }

    private static <AEB extends AdverseEventBase> void processAdverseEventBase(final ConceptService conceptService, final AEB aeb,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getAdverseEventCode(), AdverseEventConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getAdverseEventAgent(), AdverseEventAgentConcept.class,
                conceptLists);

        if (aeb.getAffectedBodySite() != null)
        {
            for (final BodySite bodySite : aeb.getAffectedBodySite())
            {
                if (bodySite != null)
                {
                    if (bodySite.getBodySiteCode() != null)
                    {
                        populateVmrOpenCdsConcept(conceptService, aeb.getId(), bodySite.getBodySiteCode(),
                                AdverseEventAffectedBodySiteConcept.class, conceptLists);

                    }
                    if (bodySite.getLaterality() != null)
                    {
                        populateVmrOpenCdsConcept(conceptService, aeb.getId(), bodySite.getLaterality(),
                                AdverseEventAffectedBodySiteLateralityConcept.class, conceptLists);
                    }
                }
            }
        }
    }

    private static void processDeniedAdverseEvents(final ConceptService conceptService, final List<DeniedAdverseEvent> daes,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processAdverseEventBase(conceptService, daes, conceptLists);
    }

    private static void processAdverseEvents(final ConceptService conceptService, final List<AdverseEvent> aes,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (aes == null)
            return;

        for (final AdverseEvent ae : aes)
        {
            processAdverseEventBase(conceptService, ae, conceptLists);

            populateVmrOpenCdsConcept(conceptService, ae.getId(), ae.getCriticality(), AdverseEventCriticalityConcept.class,
                    conceptLists);

            populateVmrOpenCdsConcept(conceptService, ae.getId(), ae.getSeverity(), AdverseEventSeverityConcept.class,
                    conceptLists);

            populateVmrOpenCdsConcept(conceptService, ae.getId(), ae.getAdverseEventStatus(), AdverseEventStatusConcept.class,
                    conceptLists);
        }
    }

    private static void processAppointmentRequests(final ConceptService conceptService, final List<AppointmentRequest> ars,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (ars == null)
            return;

        for (final AppointmentRequest ar : ars)
        {
            processEncounterBase(conceptService, ar, conceptLists);

            populateVmrOpenCdsConcept(conceptService, ar.getId(), ar.getCriticality(), EncounterCriticalityConcept.class,
                    conceptLists);
        }
    }

    private static void processAppointmentProposals(final ConceptService conceptService, final List<AppointmentProposal> aps,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (aps == null)
            return;

        for (final AppointmentProposal ap : aps)
        {
            processEncounterBase(conceptService, ap, conceptLists);

            populateVmrOpenCdsConcept(conceptService, ap.getId(), ap.getCriticality(), EncounterCriticalityConcept.class,
                    conceptLists);
        }
    }

    private static <EB extends EncounterBase> void processEncounterBase(final ConceptService conceptService, final List<EB> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final EB eb : list)
            processEncounterBase(conceptService, eb, conceptLists);
    }

    private static <EB extends EncounterBase> void processEncounterBase(final ConceptService conceptService, final EB eb,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, eb.getId(), eb.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, eb.getId(), eb.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, eb.getId(), eb.getEncounterType(), EncounterTypeConcept.class, conceptLists);
    }

    private static void processScheduledAppointments(final ConceptService conceptService, final List<ScheduledAppointment> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processEncounterBase(conceptService, list, conceptLists);
    }

    private static void processMissedAppointments(final ConceptService conceptService, final List<MissedAppointment> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processEncounterBase(conceptService, list, conceptLists);
    }

    private static void processEncounterEvents(final ConceptService conceptService, final List<EncounterEvent> ees,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processEncounterBase(conceptService, ees, conceptLists);
    }

    private static <GB extends GoalBase> void processGoalBase(final ConceptService conceptService, final GB gb,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getGoalFocus(), ObservationFocusConcept.class, conceptLists);

        if (gb.getTargetBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTargetBodySite().getBodySiteCode(),
                    GoalTargetBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTargetBodySite().getLaterality(),
                    GoalTargetBodySiteLateralityConcept.class, conceptLists);
        }

        if (gb.getTargetGoalValue() != null)
        {
            populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTargetGoalValue().getConcept(), GoalCodedValueConcept.class,
                    conceptLists);
        }

        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getCriticality(), GoalCriticalityConcept.class, conceptLists);
    }

    private static void processGoalProposals(final ConceptService conceptService, final List<GoalProposal> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final GoalProposal goalProposal : list)
            processGoalBase(conceptService, goalProposal, conceptLists);
    }

    private static void processGoals(final ConceptService conceptService, final List<Goal> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final Goal goal : list)
        {
            processGoalBase(conceptService, goal, conceptLists);

            populateVmrOpenCdsConcept(conceptService, goal.getId(), goal.getGoalStatus(), GoalStatusConcept.class, conceptLists);
        }
    }

    private static <OB extends ObservationBase> void processObservationBase(final ConceptService conceptService, final OB ob,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getObservationFocus(), ObservationFocusConcept.class,
                conceptLists);

        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getObservationMethod(), ObservationMethodConcept.class,
                conceptLists);

        if (ob.getTargetBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getTargetBodySite().getBodySiteCode(),
                    ObservationTargetBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getTargetBodySite().getLaterality(),
                    ObservationTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private static void processUnconductedObservations(final ConceptService conceptService, final List<UnconductedObservation> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final UnconductedObservation unconductedObservation : list)
        {
            processObservationBase(conceptService, unconductedObservation, conceptLists);

            populateVmrOpenCdsConcept(conceptService, unconductedObservation.getId(), unconductedObservation.getReason(),
                    ObservationUnconductedReasonConcept.class, conceptLists);
        }
    }

    private static void processObservationResults(final ConceptService conceptService, final List<ObservationResult> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final ObservationResult observationResult : list)
        {
            processObservationBase(conceptService, observationResult, conceptLists);

            if (observationResult.getObservationValue() != null)
            {
                populateVmrOpenCdsConcept(conceptService, observationResult.getId(),
                        observationResult.getObservationValue().getConcept(), ObservationCodedValueConcept.class, conceptLists);
            }

            populateVmrOpenCdsConcept(conceptService, observationResult.getId(), observationResult.getInterpretation(),
                    ObservationInterpretationConcept.class, conceptLists);
        }
    }

    private static void processObservationProposals(final ConceptService conceptService, final List<ObservationProposal> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final ObservationProposal observationProposal : list)
        {
            processObservationBase(conceptService, observationProposal, conceptLists);

            populateVmrOpenCdsConcept(conceptService, observationProposal.getId(), observationProposal.getCriticality(),
                    ObservationCriticalityConcept.class, conceptLists);
        }
    }

    private static void processObservationOrders(final ConceptService conceptService, final List<ObservationOrder> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final ObservationOrder observationOrder : list)
        {
            processObservationBase(conceptService, observationOrder, conceptLists);

            populateVmrOpenCdsConcept(conceptService, observationOrder.getId(), observationOrder.getCriticality(),
                    ObservationCriticalityConcept.class, conceptLists);
        }
    }

    private static <PB extends ProblemBase> void processProblemBase(final ConceptService conceptService, final List<PB> pbs,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (pbs == null)
            return;

        for (final PB pb : pbs)
            processProblemBase(conceptService, pb, conceptLists);
    }

    private static <PB extends ProblemBase> void processProblemBase(final ConceptService conceptService, final PB pb,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getProblemCode(), ProblemConcept.class, conceptLists);

        if (pb.getAffectedBodySite() != null)
        {
            for (final BodySite affectedBodySite : pb.getAffectedBodySite())
            {
                if (affectedBodySite != null)
                {
                    if (affectedBodySite.getBodySiteCode() != null)
                    {
                        populateVmrOpenCdsConcept(conceptService, pb.getId(), affectedBodySite.getBodySiteCode(),
                                ProblemAffectedBodySiteConcept.class, conceptLists);
                    }
                    if (affectedBodySite.getLaterality() != null)
                    {
                        populateVmrOpenCdsConcept(conceptService, pb.getId(), affectedBodySite.getLaterality(),
                                ProblemAffectedBodySiteLateralityConcept.class, conceptLists);
                    }
                }
            }
        }
    }

    private static void processProblems(final ConceptService conceptService, final List<Problem> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final Problem problem : list)
        {
            processProblemBase(conceptService, problem, conceptLists);

            populateVmrOpenCdsConcept(conceptService, problem.getId(), problem.getProblemStatus(), ProblemStatusConcept.class,
                    conceptLists);

            populateVmrOpenCdsConcept(conceptService, problem.getId(), problem.getImportance(), ProblemImportanceConcept.class,
                    conceptLists);

            populateVmrOpenCdsConcept(conceptService, problem.getId(), problem.getSeverity(), ProblemSeverityConcept.class,
                    conceptLists);
        }
    }

    private static void processDeniedProblems(final ConceptService conceptService, final List<DeniedProblem> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processProblemBase(conceptService, list, conceptLists);
    }

    private static void processUndeliveredProcedures(final ConceptService conceptService, final List<UndeliveredProcedure> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final UndeliveredProcedure undeliveredProcedure : list)
        {
            processProcedureBase(conceptService, undeliveredProcedure, conceptLists);

            populateVmrOpenCdsConcept(conceptService, undeliveredProcedure.getId(), undeliveredProcedure.getReason(),
                    UndeliveredProcedureReasonConcept.class, conceptLists);
        }
    }

    private static <PB extends ProcedureBase> void processProcedureBase(final ConceptService conceptService, final List<PB> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final PB pb : list)
            processProcedureBase(conceptService, pb, conceptLists);
    }

    private static <PB extends ProcedureBase> void processProcedureBase(final ConceptService conceptService, final PB pb,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getProcedureCode(), ProcedureConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getProcedureMethod(), ProcedureMethodConcept.class, conceptLists);

        if (pb.getApproachBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getApproachBodySite().getBodySiteCode(),
                    ProcedureApproachBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getApproachBodySite().getLaterality(),
                    ProcedureApproachBodySiteLateralityConcept.class, conceptLists);
        }

        if (pb.getTargetBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTargetBodySite().getBodySiteCode(),
                    ProcedureTargetBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTargetBodySite().getLaterality(),
                    ProcedureTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private static void processScheduledProcedures(final ConceptService conceptService, final List<ScheduledProcedure> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processProcedureBase(conceptService, list, conceptLists);
    }

    private static void processProcedureProposals(final ConceptService conceptService, final List<ProcedureProposal> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final ProcedureProposal procedureProposal : list)
        {
            processProcedureBase(conceptService, procedureProposal, conceptLists);

            populateVmrOpenCdsConcept(conceptService, procedureProposal.getId(), procedureProposal.getCriticality(),
                    ProcedureCriticalityConcept.class, conceptLists);
        }
    }

    private static void processProcedureOrders(final ConceptService conceptService, final List<ProcedureOrder> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final ProcedureOrder procedureOrder : list)
        {
            processProcedureBase(conceptService, procedureOrder, conceptLists);

            populateVmrOpenCdsConcept(conceptService, procedureOrder.getId(), procedureOrder.getCriticality(),
                    ProcedureCriticalityConcept.class, conceptLists);
        }
    }

    private static void processProcedureEvents(final ConceptService conceptService, final List<ProcedureEvent> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processProcedureBase(conceptService, list, conceptLists);
    }

    private static <SAB extends SubstanceAdministrationBase> void processSubstanceAdministrationBase(
            final ConceptService conceptService, final SAB sab,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstanceAdministrationGeneralPurpose(),
                SubstanceAdministrationGeneralPurposeConcept.class, conceptLists);

        if (sab.getApproachBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getApproachBodySite().getBodySiteCode(),
                    SubstanceAdministrationApproachBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getApproachBodySite().getLaterality(),
                    SubstanceAdministrationApproachBodySiteLateralityConcept.class, conceptLists);
        }

        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDeliveryMethod(), SubstanceDeliveryMethodConcept.class,
                conceptLists);

        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDeliveryRoute(), SubstanceDeliveryRouteConcept.class,
                conceptLists);

        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDoseType(), DoseTypeConcept.class, conceptLists);

        if (sab.getSubstance() != null)
        {
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceCode(), ImmunizationConcept.class,
                    conceptLists);
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceCode(), MedicationConcept.class,
                    conceptLists);
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceCode(),
                    MedicationClassConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getForm(), SubstanceFormConcept.class,
                    conceptLists);

            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getManufacturer(), ManufacturerConcept.class,
                    conceptLists);

            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceBrandCode(),
                    BrandedMedicationConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceGenericCode(),
                    GenericMedicationConcept.class, conceptLists);
        }

        if (sab.getTargetBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getTargetBodySite().getBodySiteCode(),
                    SubstanceAdministrationTargetBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getTargetBodySite().getLaterality(),
                    SubstanceAdministrationTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private static <SAB extends SubstanceAdministrationBase> void processSubstanceAdministrationBase(
            final ConceptService conceptService, final List<SAB> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SAB sab : list)
            processSubstanceAdministrationBase(conceptService, sab, conceptLists);
    }

    private static void processUndeliveredSubstanceAdministration(final ConceptService conceptService,
            final List<UndeliveredSubstanceAdministration> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final UndeliveredSubstanceAdministration undeliveredSubstanceAdministration : list)
        {
            processSubstanceAdministrationBase(conceptService, undeliveredSubstanceAdministration, conceptLists);

            populateVmrOpenCdsConcept(conceptService, undeliveredSubstanceAdministration.getId(),
                    undeliveredSubstanceAdministration.getReason(), UndeliveredSubstanceAdministrationReasonConcept.class,
                    conceptLists);
        }
    }

    private static void processSubstanceDispensationEvent(final ConceptService conceptService,
            final List<SubstanceDispensationEvent> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processSubstanceAdministrationBase(conceptService, list, conceptLists);
    }

    private static void processSubstanceAdministrationProposal(final ConceptService conceptService,
            final List<SubstanceAdministrationProposal> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SubstanceAdministrationProposal substanceAdministrationProposal : list)
        {
            processSubstanceAdministrationBase(conceptService, substanceAdministrationProposal, conceptLists);

            populateVmrOpenCdsConcept(conceptService, substanceAdministrationProposal.getId(),
                    substanceAdministrationProposal.getCriticality(), SubstanceAdministrationCriticalityConcept.class,
                    conceptLists);
        }
    }

    private static void processSubstanceAdministrationOrder(final ConceptService conceptService,
            final List<SubstanceAdministrationOrder> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SubstanceAdministrationOrder substanceAdministrationOrder : list)
        {
            processSubstanceAdministrationBase(conceptService, substanceAdministrationOrder, conceptLists);

            populateVmrOpenCdsConcept(conceptService, substanceAdministrationOrder.getId(),
                    substanceAdministrationOrder.getCriticality(), SubstanceAdministrationCriticalityConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, substanceAdministrationOrder.getId(),
                    substanceAdministrationOrder.getDosingSig(), DosingSigConcept.class, conceptLists);
        }
    }

    private static void processSubstanceAdministrationEvents(final ConceptService conceptService,
            final List<SubstanceAdministrationEvent> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SubstanceAdministrationEvent substanceAdministrationEvent : list)
        {
            processSubstanceAdministrationBase(conceptService, substanceAdministrationEvent, conceptLists);

            populateVmrOpenCdsConcept(conceptService, substanceAdministrationEvent.getId(),
                    substanceAdministrationEvent.getInformationAttestationType(), InformationAttestationTypeConcept.class,
                    conceptLists);
        }
    }

    private static <SB extends SupplyBase> void processSupplyBase(final ConceptService conceptService, final SB sb,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getTemplateId(), CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES,
                ClinicalStatementTemplateConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getDataSourceType(), DataSourceTypeConcept.class, conceptLists);

        populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getSupplyCode(), SupplyConcept.class, conceptLists);

        if (sb.getTargetBodySite() != null)
        {
            populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getTargetBodySite().getBodySiteCode(),
                    SupplyTargetBodySiteConcept.class, conceptLists);

            populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getTargetBodySite().getLaterality(),
                    SupplyTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private static <SB extends SupplyBase> void processSupplyBase(final ConceptService conceptService, final List<SB> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SB sb : list)
            processSupplyBase(conceptService, sb, conceptLists);
    }

    private static void processUndeliveredSupply(final ConceptService conceptService, final List<UndeliveredSupply> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final UndeliveredSupply undeliveredSupply : list)
        {
            processSupplyBase(conceptService, undeliveredSupply, conceptLists);

            populateVmrOpenCdsConcept(conceptService, undeliveredSupply.getId(), undeliveredSupply.getReason(),
                    SupplyUndeliveredReasonConcept.class, conceptLists);
        }
    }

    private static void processSupplyProposal(final ConceptService conceptService, final List<SupplyProposal> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SupplyProposal supplyProposal : list)
        {
            processSupplyBase(conceptService, supplyProposal, conceptLists);

            populateVmrOpenCdsConcept(conceptService, supplyProposal.getId(), supplyProposal.getCriticality(),
                    SupplyCriticalityConcept.class, conceptLists);
        }
    }

    private static void processSupplyOrder(final ConceptService conceptService, final List<SupplyOrder> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (list == null)
            return;

        for (final SupplyOrder supplyOrder : list)
        {
            processSupplyBase(conceptService, supplyOrder, conceptLists);

            populateVmrOpenCdsConcept(conceptService, supplyOrder.getId(), supplyOrder.getCriticality(),
                    SupplyCriticalityConcept.class, conceptLists);
        }
    }

    private static void processSupplyEvent(final ConceptService conceptService, final List<SupplyEvent> list,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        processSupplyBase(conceptService, list, conceptLists);
    }

    private static <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(final ConceptService conceptService,
            final String id, final String[] codes, final String codeSystem, final Class<C> conceptClass,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (codes == null)
            return;

        for (final String code : codes)
            populateVmrOpenCdsConcept(conceptService, id, code, codeSystem, conceptClass, conceptLists);
    }

    private static <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(final ConceptService conceptService,
            final String id, final List<CD> cds, final Class<C> conceptClass,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (cds == null)
            return;

        for (final CD cd : cds)
            populateVmrOpenCdsConcept(conceptService, id, cd, conceptClass, conceptLists);
    }

    private static <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(final ConceptService conceptService,
            final String id, final CD cd, final Class<C> conceptClass,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (cd == null)
            return;

        populateVmrOpenCdsConcept(conceptService, id, cd.getCode(), cd.getCodeSystem(), conceptClass, conceptLists);
    }

    private static <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(final ConceptService conceptService,
            final String id, final String code, final String codeSystem, final Class<C> conceptClass,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists)
    {
        if (code == null)
            return;

        populateVmrOpenCdsConcept(id, conceptClass, conceptLists, conceptService.getConceptViews(codeSystem, code));
    }

    private static <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(final String conceptTargetId,
            final Class<C> conceptClass,
            final Map<Class<? extends VmrOpenCdsConcept>, List<? extends VmrOpenCdsConcept>> conceptLists,
            final List<ConceptView> conceptViews)
    {
        if (conceptViews == null)
            return;

        for (final ConceptView conceptView : conceptViews)
        {
            final C vocc;
            try
            {
                vocc = conceptClass.getDeclaredConstructor().newInstance();
            }
            catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
            {
                log.error(e.getMessage(), e);
                throw new OpenCDSRuntimeException(e.getMessage(), e);
            }

            final Concept toConcept = conceptView.toConcept();
            vocc.setId(MiscUtility.getIDAsString());
            vocc.setConceptTargetId(conceptTargetId);
            vocc.setOpenCdsConceptCode(toConcept.code());
            vocc.setDeterminationMethodCode(conceptView.cdmCode());
            vocc.setDisplayName(toConcept.displayName());

            //noinspection unchecked
            ((List<C>) conceptLists.computeIfAbsent(conceptClass, _ -> new ArrayList<>())).add(vocc);
        }
    }
}
