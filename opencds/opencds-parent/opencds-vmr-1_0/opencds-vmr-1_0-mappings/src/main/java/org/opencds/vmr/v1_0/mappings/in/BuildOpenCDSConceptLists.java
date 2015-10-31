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

package org.opencds.vmr.v1_0.mappings.in;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * Build the Collection of ConceptLists for the populated fact lists for input
 * to Drools, moved to this class from CdsInputFactListsBuilder.java
 * 
 * @author David Shields
 *
 */
public class BuildOpenCDSConceptLists implements Cloneable {
    private static Log log = LogFactory.getLog(BuildOpenCDSConceptLists.class);

    public <C extends VmrOpenCdsConcept> void buildConceptLists(ConceptService conceptService, FactLists factLists,
            Map<Class<?>, List<?>> allFactLists) {
        long t0 = System.nanoTime();
        ConceptLists conceptLists = new ConceptLists();

        processEvaluatedPersons(conceptService, factLists.get(EvaluatedPerson.class), conceptLists);
        processEntities(conceptService, factLists.get(Entity.class), conceptLists);
        processEntities(conceptService, factLists.get(Facility.class), conceptLists);
        processEntities(conceptService, factLists.get(Organization.class), conceptLists);
        processEntities(conceptService, factLists.get(Person.class), conceptLists);
        processEntities(conceptService, factLists.get(Specimen.class), conceptLists);

        processClinicalStatementRelationships(conceptService, factLists.get(ClinicalStatementRelationship.class),
                conceptLists);
        processEntityRelationships(conceptService, factLists.get(EntityRelationship.class), conceptLists);
        processEvaluatedPersonRelationShips(conceptService, factLists.get(EvaluatedPersonRelationship.class),
                conceptLists);

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

        processSubstanceAdministrationEvents(conceptService, factLists.get(SubstanceAdministrationEvent.class),
                conceptLists);
        processSubstanceAdministrationOrder(conceptService, factLists.get(SubstanceAdministrationOrder.class),
                conceptLists);
        processSubstanceAdministrationProposal(conceptService, factLists.get(SubstanceAdministrationProposal.class),
                conceptLists);
        processSubstanceDispensationEvent(conceptService, factLists.get(SubstanceDispensationEvent.class), conceptLists);
        processUndeliveredSubstanceAdministration(conceptService,
                factLists.get(UndeliveredSubstanceAdministration.class), conceptLists);

        processSupplyEvent(conceptService, factLists.get(SupplyEvent.class), conceptLists);
        processSupplyOrder(conceptService, factLists.get(SupplyOrder.class), conceptLists);
        processSupplyProposal(conceptService, factLists.get(SupplyProposal.class), conceptLists);
        processUndeliveredSupply(conceptService, factLists.get(UndeliveredSupply.class), conceptLists);

        /* Concepts */
        for (Entry<Class<?>, List<?>> list : conceptLists.iterable()) {
            allFactLists.put(list.getKey(), list.getValue());
        }
        log.debug("BuildOpenCDSConceptLists time : " + (System.nanoTime() - t0) / 1e6 + " ms");
    }

    /**
     * Method to process of list of abstract EntityBase
     * 
     * @param entities
     * @param conceptLists
     */
    private <EB extends EntityBase> void processEntities(ConceptService conceptService, List<EB> entities,
            ConceptLists conceptLists) {
        if (entities != null) {
            for (EB entity : entities) {
                processEntity(conceptService, entity, conceptLists);
            }
        }
    }

    /**
     * Method to process any class that is an EntityBase class.
     * 
     * @param entity
     * @param conceptLists
     */
    private void processEntity(ConceptService conceptService, EntityBase entity, ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, entity.getId(), entity.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, EntityTemplateConcept.class, conceptLists);

        // entity type
        populateVmrOpenCdsConcept(conceptService, entity.getId(), entity.getEntityType(), EntityTypeConcept.class,
                conceptLists);
    }

    private void processEvaluatedPersons(ConceptService conceptService, List<EvaluatedPerson> evaluatedPersons,
            ConceptLists conceptLists) {
        // evaluated person
        if (evaluatedPersons != null) {
            for (EvaluatedPerson evaluatedPerson : evaluatedPersons) {
                // process the entity
                processEntity(conceptService, evaluatedPerson, conceptLists);

                // demographics
                if (evaluatedPerson.getDemographics() != null) {
                    Demographics demographics = evaluatedPerson.getDemographics();
                    // gender
                    if (demographics.getGender() != null) {
                        populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getGender(),
                                GenderConcept.class, conceptLists);
                    }

                    // race
                    if (demographics.getRace() != null) {
                        populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getRace(),
                                RaceConcept.class, conceptLists);
                    }

                    // ethnicity
                    if (demographics.getEthnicity() != null) {
                        populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(), demographics.getEthnicity(),
                                EthnicityConcept.class, conceptLists);
                    }

                    // preferred language
                    if (demographics.getPreferredLanguage() != null) {
                        populateVmrOpenCdsConcept(conceptService, evaluatedPerson.getId(),
                                demographics.getPreferredLanguage(), PreferredLanguageConcept.class, conceptLists);
                    }
                }
            }
        }
    }

    private void processEvaluatedPersonRelationShips(ConceptService conceptService,
            List<EvaluatedPersonRelationship> eprs, ConceptLists conceptLists) {
        // evaluated person relationship
        if (eprs != null) {
            for (EvaluatedPersonRelationship epr : eprs) {
                populateVmrOpenCdsConcept(conceptService, epr.getId(), epr.getTargetRole(),
                        EvaluatedPersonRelationshipConcept.class, conceptLists);
            }
        }
    }

    private void processEntityRelationships(ConceptService conceptService, List<EntityRelationship> ers,
            ConceptLists conceptLists) {
        // entity relationship
        if (ers != null) {
            for (EntityRelationship er : ers) {
                populateVmrOpenCdsConcept(conceptService, er.getId(), er.getTargetRole(),
                        EntityRelationshipConcept.class, conceptLists);
            }
        }
    }

    private void processClinicalStatementRelationships(ConceptService conceptService,
            List<ClinicalStatementRelationship> csrs, ConceptLists conceptLists) {
        // clinical statement relationship
        if (csrs != null) {
            for (ClinicalStatementRelationship csr : csrs) {
                populateVmrOpenCdsConcept(conceptService, csr.getId(), csr.getTargetRelationshipToSource(),
                        ClinicalStatementRelationshipConcept.class, conceptLists);
            }
        }
    }

    /**
     * Processes instances (subclasses) of AdverseEventBase
     * 
     * @param aebs
     * @param conceptLists
     */
    private <AEB extends AdverseEventBase> void processAdverseEventBase(ConceptService conceptService, List<AEB> aebs,
            ConceptLists conceptLists) {
        if (aebs != null) {
            for (AEB aeb : aebs) {
                processAdverseEventBase(conceptService, aeb, conceptLists);
            }
        }
    }

    /**
     * Processes an instance (subclass) of AdverseEventBase
     * 
     * @param aeb
     * @param conceptLists
     */
    private <AEB extends AdverseEventBase> void processAdverseEventBase(ConceptService conceptService, AEB aeb,
            ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // adverse event code
        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getAdverseEventCode(), AdverseEventConcept.class,
                conceptLists);

        // adverse event agent
        populateVmrOpenCdsConcept(conceptService, aeb.getId(), aeb.getAdverseEventAgent(),
                AdverseEventAgentConcept.class, conceptLists);

        // affected body site
        if (aeb.getAffectedBodySite() != null) {
            for (BodySite bodySite : aeb.getAffectedBodySite()) {
                if (bodySite != null) {
                    if (bodySite.getBodySiteCode() != null) {
                        populateVmrOpenCdsConcept(conceptService, aeb.getId(), bodySite.getBodySiteCode(),
                                AdverseEventAffectedBodySiteConcept.class, conceptLists);

                    }
                    if (bodySite.getLaterality() != null) {
                        populateVmrOpenCdsConcept(conceptService, aeb.getId(), bodySite.getLaterality(),
                                AdverseEventAffectedBodySiteLateralityConcept.class, conceptLists);
                    }
                }
            }
        }
    }

    private void processDeniedAdverseEvents(ConceptService conceptService, List<DeniedAdverseEvent> daes,
            ConceptLists conceptLists) {
        // denied adverse event
        processAdverseEventBase(conceptService, daes, conceptLists);
    }

    private void processAdverseEvents(ConceptService conceptService, List<AdverseEvent> aes, ConceptLists conceptLists) {
        // adverse event
        if (aes != null) {
            for (AdverseEvent ae : aes) {
                // process the AEB
                processAdverseEventBase(conceptService, ae, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, ae.getId(), ae.getCriticality(),
                        AdverseEventCriticalityConcept.class, conceptLists);

                // severity
                populateVmrOpenCdsConcept(conceptService, ae.getId(), ae.getSeverity(),
                        AdverseEventSeverityConcept.class, conceptLists);

                // status
                populateVmrOpenCdsConcept(conceptService, ae.getId(), ae.getAdverseEventStatus(),
                        AdverseEventStatusConcept.class, conceptLists);
            }
        }
    }

    private void processAppointmentRequests(ConceptService conceptService, List<AppointmentRequest> ars,
            ConceptLists conceptLists) {
        // appointment request
        if (ars != null) {
            for (AppointmentRequest ar : ars) {
                processEncounterBase(conceptService, ar, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, ar.getId(), ar.getCriticality(),
                        EncounterCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processAppointmentProposals(ConceptService conceptService, List<AppointmentProposal> aps,
            ConceptLists conceptLists) {
        // appointment proposal
        if (aps != null) {
            for (AppointmentProposal ap : aps) {
                processEncounterBase(conceptService, ap, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, ap.getId(), ap.getCriticality(),
                        EncounterCriticalityConcept.class, conceptLists);
            }
        }
    }

    /**
     * Processes instances (subclasses) of EncounterBase
     * 
     * @param list
     * @param conceptLists
     */
    private <EB extends EncounterBase> void processEncounterBase(ConceptService conceptService, List<EB> list,
            ConceptLists conceptLists) {
        if (list != null) {
            for (EB eb : list) {
                processEncounterBase(conceptService, eb, conceptLists);
            }
        }
    }

    /**
     * Processes an instance (subclass) of EncounterBase
     * 
     * @param eb
     * @param conceptLists
     */
    private <EB extends EncounterBase> void processEncounterBase(ConceptService conceptService, EB eb,
            ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, eb.getId(), eb.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, eb.getId(), eb.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // encounter type
        populateVmrOpenCdsConcept(conceptService, eb.getId(), eb.getEncounterType(), EncounterTypeConcept.class,
                conceptLists);
    }

    private void processScheduledAppointments(ConceptService conceptService, List<ScheduledAppointment> list,
            ConceptLists conceptLists) {
        processEncounterBase(conceptService, list, conceptLists);
    }

    private void processMissedAppointments(ConceptService conceptService, List<MissedAppointment> list,
            ConceptLists conceptLists) {
        processEncounterBase(conceptService, list, conceptLists);
    }

    private void processEncounterEvents(ConceptService conceptService, List<EncounterEvent> ees,
            ConceptLists conceptLists) {
        processEncounterBase(conceptService, ees, conceptLists);
    }

    /**
     * Processes an instance (subclass) of GoalBase.
     * 
     * @param gb
     * @param conceptLists
     */
    private <GB extends GoalBase> void processGoalBase(ConceptService conceptService, GB gb, ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // goal focus
        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getGoalFocus(), ObservationFocusConcept.class,
                conceptLists);

        // target body site
        if (gb.getTargetBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTargetBodySite().getBodySiteCode(),
                    GoalTargetBodySiteConcept.class, conceptLists);

            // target body site laterality
            populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTargetBodySite().getLaterality(),
                    GoalTargetBodySiteLateralityConcept.class, conceptLists);
        }

        // goal value (= ANY -- just processing CD here... anything other than
        // CD?)
        // TODO can we create concepts out of any other types???
        if (gb.getTargetGoalValue() != null) {
            populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getTargetGoalValue().getConcept(),
                    GoalCodedValueConcept.class, conceptLists);
        }

        // goal criticality
        populateVmrOpenCdsConcept(conceptService, gb.getId(), gb.getCriticality(), GoalCriticalityConcept.class,
                conceptLists);
    }

    private void processGoalProposals(ConceptService conceptService, List<GoalProposal> list, ConceptLists conceptLists) {
        // goal proposal
        if (list != null) {
            for (GoalProposal goalProposal : list) {
                processGoalBase(conceptService, goalProposal, conceptLists);
            }
        }
    }

    private void processGoals(ConceptService conceptService, List<Goal> list, ConceptLists conceptLists) {
        // goal
        if (list != null) {
            for (Goal goal : list) {
                processGoalBase(conceptService, goal, conceptLists);

                // goal status
                populateVmrOpenCdsConcept(conceptService, goal.getId(), goal.getGoalStatus(), GoalStatusConcept.class,
                        conceptLists);
            }
        }
    }

    /**
     * Processes an instance (subclass) of ObservationBase.
     * 
     * @param ob
     * @param conceptLists
     */
    private <OB extends ObservationBase> void processObservationBase(ConceptService conceptService, OB ob,
            ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // observation focus
        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getObservationFocus(), ObservationFocusConcept.class,
                conceptLists);

        // observation method
        populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getObservationMethod(),
                ObservationMethodConcept.class, conceptLists);

        // target body site
        if (ob.getTargetBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getTargetBodySite().getBodySiteCode(),
                    ObservationTargetBodySiteConcept.class, conceptLists);

            // target body site laterality
            populateVmrOpenCdsConcept(conceptService, ob.getId(), ob.getTargetBodySite().getLaterality(),
                    ObservationTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private void processUnconductedObservations(ConceptService conceptService, List<UnconductedObservation> list,
            ConceptLists conceptLists) {
        // unconducted observation
        if (list != null) {
            for (UnconductedObservation unconductedObservation : list) {
                processObservationBase(conceptService, unconductedObservation, conceptLists);
                // reason
                populateVmrOpenCdsConcept(conceptService, unconductedObservation.getId(),
                        unconductedObservation.getReason(), ObservationUnconductedReasonConcept.class, conceptLists);
            }
        }
    }

    private void processObservationResults(ConceptService conceptService, List<ObservationResult> list,
            ConceptLists conceptLists) {
        // observation result
        if (list != null) {
            for (ObservationResult observationResult : list) {
                processObservationBase(conceptService, observationResult, conceptLists);

                // observation value (= ANY -- just processing CD here...)
                // TODO can we create concepts out of any other types???
                if (observationResult.getObservationValue() != null) {
                    populateVmrOpenCdsConcept(conceptService, observationResult.getId(), observationResult
                            .getObservationValue().getConcept(), ObservationCodedValueConcept.class, conceptLists);
                }

                // interpretation
                populateVmrOpenCdsConcept(conceptService, observationResult.getId(),
                        observationResult.getInterpretation(), ObservationInterpretationConcept.class, conceptLists);
            }
        }
    }

    private void processObservationProposals(ConceptService conceptService, List<ObservationProposal> list,
            ConceptLists conceptLists) {
        // observation proposal
        if (list != null) {
            for (ObservationProposal observationProposal : list) {
                processObservationBase(conceptService, observationProposal, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, observationProposal.getId(),
                        observationProposal.getCriticality(), ObservationCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processObservationOrders(ConceptService conceptService, List<ObservationOrder> list,
            ConceptLists conceptLists) {
        // observation order
        if (list != null) {
            for (ObservationOrder observationOrder : list) {
                processObservationBase(conceptService, observationOrder, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, observationOrder.getId(), observationOrder.getCriticality(),
                        ObservationCriticalityConcept.class, conceptLists);
            }
        }
    }

    /**
     * Processes instances (subclasses) of ProblemBase.
     * 
     * @param pbs
     * @param conceptLists
     */
    private <PB extends ProblemBase> void processProblemBase(ConceptService conceptService, List<PB> pbs,
            ConceptLists conceptLists) {
        if (pbs != null) {
            for (PB pb : pbs) {
                processProblemBase(conceptService, pb, conceptLists);
            }
        }
    }

    /**
     * Processes an instance (a subclass) of ProblemBase.
     * 
     * @param pb
     * @param conceptLists
     */
    private <PB extends ProblemBase> void processProblemBase(ConceptService conceptService, PB pb,
            ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // problem code
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getProblemCode(), ProblemConcept.class, conceptLists);

        // affected body site
        if (pb.getAffectedBodySite() != null) {
            for (BodySite affectedBodySite : pb.getAffectedBodySite()) {
                // body site code
                if (affectedBodySite != null) {
                    if (affectedBodySite.getBodySiteCode() != null) {
                        populateVmrOpenCdsConcept(conceptService, pb.getId(), affectedBodySite.getBodySiteCode(),
                                ProblemAffectedBodySiteConcept.class, conceptLists);
                    }
                    if (affectedBodySite.getLaterality() != null) {
                        populateVmrOpenCdsConcept(conceptService, pb.getId(), affectedBodySite.getLaterality(),
                                ProblemAffectedBodySiteLateralityConcept.class, conceptLists);
                    }
                }
            }
        }
    }

    private void processProblems(ConceptService conceptService, List<Problem> list, ConceptLists conceptLists) {
        // problem
        if (list != null) {
            for (Problem problem : list) {
                processProblemBase(conceptService, problem, conceptLists);

                // problem status
                populateVmrOpenCdsConcept(conceptService, problem.getId(), problem.getProblemStatus(),
                        ProblemStatusConcept.class, conceptLists);

                // problem importance
                populateVmrOpenCdsConcept(conceptService, problem.getId(), problem.getImportance(),
                        ProblemImportanceConcept.class, conceptLists);

                // severity
                populateVmrOpenCdsConcept(conceptService, problem.getId(), problem.getSeverity(),
                        ProblemSeverityConcept.class, conceptLists);
            }
        }
    }

    private void processDeniedProblems(ConceptService conceptService, List<DeniedProblem> list,
            ConceptLists conceptLists) {
        processProblemBase(conceptService, list, conceptLists);
    }

    private void processUndeliveredProcedures(ConceptService conceptService, List<UndeliveredProcedure> list,
            ConceptLists conceptLists) {
        // undelivered procedure
        if (list != null) {
            for (UndeliveredProcedure undeliveredProcedure : list) {
                processProcedureBase(conceptService, undeliveredProcedure, conceptLists);

                // undelivered procedure reason
                populateVmrOpenCdsConcept(conceptService, undeliveredProcedure.getId(),
                        undeliveredProcedure.getReason(), UndeliveredProcedureReasonConcept.class, conceptLists);
            }
        }
    }

    /**
     * Processes instances (subclasses) of ProcedureBase.
     * 
     * @param list
     * @param conceptLists
     */
    private <PB extends ProcedureBase> void processProcedureBase(ConceptService conceptService, List<PB> list,
            ConceptLists conceptLists) {
        if (list != null) {
            for (PB pb : list) {
                processProcedureBase(conceptService, pb, conceptLists);
            }
        }
    }

    /**
     * Processes an instance (a subclass) of ProcedureBase.
     * 
     * @param pb
     * @param conceptLists
     */
    private <PB extends ProcedureBase> void processProcedureBase(ConceptService conceptService, PB pb,
            ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // procedure code
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getProcedureCode(), ProcedureConcept.class,
                conceptLists);

        // procedure method
        populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getProcedureMethod(), ProcedureMethodConcept.class,
                conceptLists);

        // approach body site
        if (pb.getApproachBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getApproachBodySite().getBodySiteCode(),
                    ProcedureApproachBodySiteConcept.class, conceptLists);

            // approach body site laterality
            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getApproachBodySite().getLaterality(),
                    ProcedureApproachBodySiteLateralityConcept.class, conceptLists);
        }

        // target body site
        if (pb.getTargetBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTargetBodySite().getBodySiteCode(),
                    ProcedureTargetBodySiteConcept.class, conceptLists);

            // target body site laterality
            populateVmrOpenCdsConcept(conceptService, pb.getId(), pb.getTargetBodySite().getLaterality(),
                    ProcedureTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private void processScheduledProcedures(ConceptService conceptService, List<ScheduledProcedure> list,
            ConceptLists conceptLists) {
        processProcedureBase(conceptService, list, conceptLists);
    }

    private void processProcedureProposals(ConceptService conceptService, List<ProcedureProposal> list,
            ConceptLists conceptLists) {
        // procedure proposal
        if (list != null) {
            for (ProcedureProposal procedureProposal : list) {
                processProcedureBase(conceptService, procedureProposal, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, procedureProposal.getId(),
                        procedureProposal.getCriticality(), ProcedureCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processProcedureOrders(ConceptService conceptService, List<ProcedureOrder> list,
            ConceptLists conceptLists) {
        // procedure order
        if (list != null) {
            for (ProcedureOrder procedureOrder : list) {
                processProcedureBase(conceptService, procedureOrder, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, procedureOrder.getId(), procedureOrder.getCriticality(),
                        ProcedureCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processProcedureEvents(ConceptService conceptService, List<ProcedureEvent> list,
            ConceptLists conceptLists) {
        processProcedureBase(conceptService, list, conceptLists);
    }

    /**
     * Processes an instance (a subclass) of SubstanceAdministrationBase
     * 
     * @param sab
     * @param conceptLists
     */
    private <SAB extends SubstanceAdministrationBase> void processSubstanceAdministrationBase(
            ConceptService conceptService, SAB sab, ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // substance administration general purpose
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstanceAdministrationGeneralPurpose(),
                SubstanceAdministrationGeneralPurposeConcept.class, conceptLists);

        // approach body site
        if (sab.getApproachBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getApproachBodySite().getBodySiteCode(),
                    SubstanceAdministrationApproachBodySiteConcept.class, conceptLists);

            // approach body site laterality
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getApproachBodySite().getLaterality(),
                    SubstanceAdministrationApproachBodySiteLateralityConcept.class, conceptLists);
        }

        // delivery method
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDeliveryMethod(),
                SubstanceDeliveryMethodConcept.class, conceptLists);

        // delivery route
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDeliveryRoute(),
                SubstanceDeliveryRouteConcept.class, conceptLists);

        // dose type
        populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getDoseType(), DoseTypeConcept.class, conceptLists);

        // substance
        if (sab.getSubstance() != null) {
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceCode(),
                    ImmunizationConcept.class, conceptLists);
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceCode(),
                    MedicationConcept.class, conceptLists);
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceCode(),
                    MedicationClassConcept.class, conceptLists);

            // form
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getForm(),
                    SubstanceFormConcept.class, conceptLists);

            // manufacturer
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getManufacturer(),
                    ManufacturerConcept.class, conceptLists);

            // substance brand code
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceBrandCode(),
                    BrandedMedicationConcept.class, conceptLists);

            // substance generic code
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getSubstance().getSubstanceGenericCode(),
                    GenericMedicationConcept.class, conceptLists);
        }

        // target body site
        if (sab.getTargetBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getTargetBodySite().getBodySiteCode(),
                    SubstanceAdministrationTargetBodySiteConcept.class, conceptLists);

            // target body site laterality
            populateVmrOpenCdsConcept(conceptService, sab.getId(), sab.getTargetBodySite().getLaterality(),
                    SubstanceAdministrationTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    /**
     * Processes instances (subclasses) of SubstanceAdministrationBase
     * 
     * @param list
     * @param conceptLists
     */
    private <SAB extends SubstanceAdministrationBase> void processSubstanceAdministrationBase(
            ConceptService conceptService, List<SAB> list, ConceptLists conceptLists) {
        // substance dispensation event
        if (list != null) {
            for (SAB sab : list) {
                processSubstanceAdministrationBase(conceptService, sab, conceptLists);
            }
        }
    }

    private void processUndeliveredSubstanceAdministration(ConceptService conceptService,
            List<UndeliveredSubstanceAdministration> list, ConceptLists conceptLists) {
        // undelivered substance administration
        if (list != null) {
            for (UndeliveredSubstanceAdministration undeliveredSubstanceAdministration : list) {
                processSubstanceAdministrationBase(conceptService, undeliveredSubstanceAdministration, conceptLists);

                // reason
                populateVmrOpenCdsConcept(conceptService, undeliveredSubstanceAdministration.getId(),
                        undeliveredSubstanceAdministration.getReason(),
                        UndeliveredSubstanceAdministrationReasonConcept.class, conceptLists);
            }
        }
    }

    private void processSubstanceDispensationEvent(ConceptService conceptService,
            List<SubstanceDispensationEvent> list, ConceptLists conceptLists) {
        processSubstanceAdministrationBase(conceptService, list, conceptLists);
    }

    private void processSubstanceAdministrationProposal(ConceptService conceptService,
            List<SubstanceAdministrationProposal> list, ConceptLists conceptLists) {
        // substance administration proposal
        if (list != null) {
            for (SubstanceAdministrationProposal substanceAdministrationProposal : list) {
                processSubstanceAdministrationBase(conceptService, substanceAdministrationProposal, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, substanceAdministrationProposal.getId(),
                        substanceAdministrationProposal.getCriticality(),
                        SubstanceAdministrationCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processSubstanceAdministrationOrder(ConceptService conceptService,
            List<SubstanceAdministrationOrder> list, ConceptLists conceptLists) {
        // substance administration order
        if (list != null) {
            for (SubstanceAdministrationOrder substanceAdministrationOrder : list) {
                processSubstanceAdministrationBase(conceptService, substanceAdministrationOrder, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, substanceAdministrationOrder.getId(),
                        substanceAdministrationOrder.getCriticality(), SubstanceAdministrationCriticalityConcept.class,
                        conceptLists);

                // dosing sig
                populateVmrOpenCdsConcept(conceptService, substanceAdministrationOrder.getId(),
                        substanceAdministrationOrder.getDosingSig(), DosingSigConcept.class, conceptLists);
            }
        }
    }

    private void processSubstanceAdministrationEvents(ConceptService conceptService,
            List<SubstanceAdministrationEvent> list, ConceptLists conceptLists) {
        // substance administration event
        if (list != null) {
            for (SubstanceAdministrationEvent substanceAdministrationEvent : list) {
                processSubstanceAdministrationBase(conceptService, substanceAdministrationEvent, conceptLists);

                // information attestation type
                populateVmrOpenCdsConcept(conceptService, substanceAdministrationEvent.getId(),
                        substanceAdministrationEvent.getInformationAttestationType(),
                        InformationAttestationTypeConcept.class, conceptLists);
            }
        }
    }

    private <SB extends SupplyBase> void processSupplyBase(ConceptService conceptService, SB sb,
            ConceptLists conceptLists) {
        // templateId
        populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getTemplateId(),
                CodeSystems.CODE_SYSTEM_OID_OPENCDS_TEMPLATES, ClinicalStatementTemplateConcept.class, conceptLists);

        // data source type
        populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getDataSourceType(), DataSourceTypeConcept.class,
                conceptLists);

        // supply
        populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getSupplyCode(), SupplyConcept.class, conceptLists);

        // target body site
        if (sb.getTargetBodySite() != null) {
            populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getTargetBodySite().getBodySiteCode(),
                    SupplyTargetBodySiteConcept.class, conceptLists);

            // target body site laterality
            populateVmrOpenCdsConcept(conceptService, sb.getId(), sb.getTargetBodySite().getLaterality(),
                    SupplyTargetBodySiteLateralityConcept.class, conceptLists);
        }
    }

    private <SB extends SupplyBase> void processSupplyBase(ConceptService conceptService, List<SB> list,
            ConceptLists conceptLists) {
        if (list != null) {
            for (SB sb : list) {
                processSupplyBase(conceptService, sb, conceptLists);
            }
        }
    }

    private void processUndeliveredSupply(ConceptService conceptService, List<UndeliveredSupply> list,
            ConceptLists conceptLists) {
        // undelivered supply
        if (list != null) {
            for (UndeliveredSupply undeliveredSupply : list) {
                processSupplyBase(conceptService, undeliveredSupply, conceptLists);

                // reason
                populateVmrOpenCdsConcept(conceptService, undeliveredSupply.getId(), undeliveredSupply.getReason(),
                        SupplyUndeliveredReasonConcept.class, conceptLists);
            }
        }
    }

    private void processSupplyProposal(ConceptService conceptService, List<SupplyProposal> list,
            ConceptLists conceptLists) {
        // supply proposal
        if (list != null) {
            for (SupplyProposal supplyProposal : list) {
                processSupplyBase(conceptService, supplyProposal, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, supplyProposal.getId(), supplyProposal.getCriticality(),
                        SupplyCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processSupplyOrder(ConceptService conceptService, List<SupplyOrder> list, ConceptLists conceptLists) {
        // supply order
        if (list != null) {
            for (SupplyOrder supplyOrder : list) {
                processSupplyBase(conceptService, supplyOrder, conceptLists);

                // criticality
                populateVmrOpenCdsConcept(conceptService, supplyOrder.getId(), supplyOrder.getCriticality(),
                        SupplyCriticalityConcept.class, conceptLists);
            }
        }
    }

    private void processSupplyEvent(ConceptService conceptService, List<SupplyEvent> list, ConceptLists conceptLists) {
        processSupplyBase(conceptService, list, conceptLists);
    }

    /**
     * For template support.
     * 
     * @param id
     * @param codes
     * @param codeSystem
     * @param conceptClass
     * @param conceptList
     */
    private <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(ConceptService conceptService, String id,
            String[] codes, String codeSystem, Class<C> conceptClass, ConceptLists conceptList) {
        if (codes != null) {
            for (String code : codes) {
                populateVmrOpenCdsConcept(conceptService, id, code, codeSystem, conceptClass, conceptList);
            }
        }
    }

    private <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(ConceptService conceptService, String id,
            List<CD> cds, Class<C> conceptClass, ConceptLists conceptLists) {
        if (cds != null) {
            for (CD cd : cds) {
                populateVmrOpenCdsConcept(conceptService, id, cd, conceptClass, conceptLists);
            }
        }
    }

    private <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(ConceptService conceptService, String id,
            CD cd, Class<C> conceptClass, ConceptLists conceptLists) {
        if (cd != null) {
            populateVmrOpenCdsConcept(conceptService, id, cd.getCode(), cd.getCodeSystem(), conceptClass, conceptLists);
        }
    }

    /**
     * For template support.
     * 
     * @param id
     * @param code
     * @param codeSystem
     * @param conceptClass
     * @param conceptLists
     */
    private <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(ConceptService conceptService, String id,
            String code, String codeSystem, Class<C> conceptClass, ConceptLists conceptLists) {
        if (code != null) {
            List<ConceptView> conceptViews = conceptService.getConceptViews(codeSystem, code);
            populateVmrOpenCdsConcept(id, conceptClass, conceptLists, conceptViews);
        }
    }

    /**
     * @param conceptTargetId
     * @param vmrOpenCdsConceptToAddAfterPopulating
     * @param vmrOpenCdsConceptList
     * @param matchingOpenCdsConcept
     */
    private <C extends VmrOpenCdsConcept> void populateVmrOpenCdsConcept(String conceptTargetId, Class<C> conceptClass,
            ConceptLists conceptLists, List<ConceptView> conceptViews) {
        if (conceptViews != null) {
            for (ConceptView conceptView : conceptViews) {
                C vocc = null;
                try {
                    vocc = conceptClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new OpenCDSRuntimeException(e.getMessage(), e);
                }
                Concept toConcept = conceptView.getToConcept();
                vocc.setId(MiscUtility.getIDAsString());
                vocc.setConceptTargetId(conceptTargetId);
                vocc.setOpenCdsConceptCode(toConcept.getCode());
                vocc.setDeterminationMethodCode(conceptView.getCdmCode());
                vocc.setDisplayName(toConcept.getDisplayName());
                conceptLists.put(conceptClass, vocc);
            }
        }

    }

}
