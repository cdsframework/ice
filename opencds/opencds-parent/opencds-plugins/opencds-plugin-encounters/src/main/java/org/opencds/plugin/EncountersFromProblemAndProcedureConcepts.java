package org.opencds.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.plugin.PluginContext.PreProcessPluginContext;
import org.opencds.vmr.v1_0.internal.ClinicalStatementRelationship;
import org.opencds.vmr.v1_0.internal.EncounterEvent;
import org.opencds.vmr.v1_0.internal.concepts.ClinicalStatementRelationshipConcept;
import org.opencds.vmr.v1_0.internal.concepts.EncounterTypeConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProblemConcept;
import org.opencds.vmr.v1_0.internal.concepts.ProcedureConcept;

public class EncountersFromProblemAndProcedureConcepts implements OpencdsPlugin<PreProcessPluginContext> {
    private static final Log log = LogFactory.getLog(EncountersFromProblemAndProcedureConcepts.class);

    private <T> List<T> get(Map<Class<?>, List<?>> allFactLists, Class<T> clazz) {
        List<T> list = (List<T>) allFactLists.get(clazz);
        if (list == null) {
            list = new ArrayList<>();
            allFactLists.put(clazz, list);
        }
        return list;
    }
    
    @Override
    public void execute(PreProcessPluginContext context) {
        Map<Class<?>, List<?>> allFactLists = context.getAllFactLists();
        
        List<EncounterEvent> encounterEvents = get(allFactLists, EncounterEvent.class);
        List<EncounterTypeConcept> encounterTypeConcepts = get(allFactLists, EncounterTypeConcept.class);
        List<ClinicalStatementRelationship> clinicalStatementRelationships = get(allFactLists,
                ClinicalStatementRelationship.class);
        List<ClinicalStatementRelationshipConcept> clinicalStatementRelationshipConcepts = get(allFactLists,
                ClinicalStatementRelationshipConcept.class);
        List<ProblemConcept> problemConcepts = get(allFactLists, ProblemConcept.class);
        List<ProcedureConcept> procedureConcepts = get(allFactLists, ProcedureConcept.class);
        /*
         *(  Input requirements:
         *      encounterTypeConcepts are a set  of EncounterTypeConcepts in memory
         *      clincialStatementRelationships are a set of ClinicalStatementRelationships where the source is an encounter and the relationship is as specified
         *      problemConcepts are concepts which are related to encounters in the specified manner        
         *  Adds encounterTypeConcepts as follows:
         *      1. Rejects any clinicalStatementRelationship whose code is NOT C405
         *      2. Rejects any clinicalStatementRelationship whose sourceId is not in the Set of EncounterEvent Ids
         *      For each remaining clinicalStatementRelationship
         *          3. See if the targetId is a conceptTargetId for a ProblemConcept
         *          4. If the encounter does not already have an encounter type concept which is the same concept as the problem concept, 
         *              add the encounterConcept (with the appropriate encounterId) to encounterEventConcepts           
         *          5. See if the targetId is a conceptTargetId for a ProcedureConcept
         *          6. If the encounter does not already have an encounter type concept which is the same concept as the procedure concept, 
         *              add the encounterConcept (with the appropriate encounterId) to encounterEventConcepts           
         *  May returns an empty list if no matches made.
         */  
        Set<String> etcSet = new HashSet<String>(); // set String = Encounter
                                                    // Type Concept code + "|" +
                                                    // concept target Id
        Set<String> eeSet = new HashSet<String>(); // set String = Encounter
                                                   // Event Id
        // Map<String, String> csrTargetIdToSourceIdMap = new HashMap<String,
        // String>(); // key = targetId, target = sourceId associated with the
        // targetId

        Map<String, List<ProblemConcept>> problemConceptTargetIdMap = new HashMap<String, List<ProblemConcept>>();
        // key = conceptTargetId, target = ProblemConcept associated with the
        // conceptTargetId
        Map<String, List<ProcedureConcept>> procedureConceptTargetIdMap = new HashMap<String, List<ProcedureConcept>>();
        // key = conceptTargetId, target = ProcedureConcept associated with the
        // conceptTargetId
        Map<String, List<ClinicalStatementRelationshipConcept>> clinicalStatementRelationshipConceptTargetIdMap = new HashMap<String, List<ClinicalStatementRelationshipConcept>>();
        // key = conceptTargetId, target = ClinicalStatementRelationshipConcept
        // associated with the conceptTargetId

        if ((clinicalStatementRelationships == null) || (clinicalStatementRelationships.size() == 0)) {
            return;
        }

        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts found clinicalStatementRelationships="
                + clinicalStatementRelationships.size());

        if (encounterEvents != null) {
            for (EncounterEvent ee : encounterEvents) {
                eeSet.add(ee.getId());
            }
        }
        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts found EncounterEvents="
                + encounterEvents.size());

        if (encounterTypeConcepts != null) {
            for (EncounterTypeConcept etc : encounterTypeConcepts) {
                String etcHash = etc.getOpenCdsConceptCode() + "|" + etc.getConceptTargetId();
                etcSet.add(etcHash);
            }
        }
        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts started with EncTypeConcepts.size="
                + encounterTypeConcepts.size());

        if (problemConcepts != null) {
            for (ProblemConcept probC : problemConcepts) {
                if (problemConceptTargetIdMap.get(probC.getConceptTargetId()) != null) {
                    List<ProblemConcept> probCs = problemConceptTargetIdMap.get(probC.getConceptTargetId());
                    probCs.add(probC);
                    problemConceptTargetIdMap.put(probC.getConceptTargetId(), probCs);
                } else {
                    List<ProblemConcept> probCs = new ArrayList<ProblemConcept>();
                    probCs.add(probC);
                    problemConceptTargetIdMap.put(probC.getConceptTargetId(), probCs);
                }
            }
        }
        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts found problemConcepts="
                + problemConcepts.size());

        if (procedureConcepts != null) {
            for (ProcedureConcept procC : procedureConcepts) {
                if (procedureConceptTargetIdMap.get(procC.getConceptTargetId()) != null) {
                    List<ProcedureConcept> procCs = procedureConceptTargetIdMap.get(procC.getConceptTargetId());
                    procCs.add(procC);
                    procedureConceptTargetIdMap.put(procC.getConceptTargetId(), procCs);
                } else {
                    List<ProcedureConcept> procCs = new ArrayList<ProcedureConcept>();
                    procCs.add(procC);
                    procedureConceptTargetIdMap.put(procC.getConceptTargetId(), procCs);
                }
            }
        }
        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts found procedureConcepts="
                + procedureConcepts.size());

        if (clinicalStatementRelationshipConcepts != null) {
            for (ClinicalStatementRelationshipConcept csrC : clinicalStatementRelationshipConcepts) {
                if (clinicalStatementRelationshipConceptTargetIdMap.get(csrC.getConceptTargetId()) != null) {
                    List<ClinicalStatementRelationshipConcept> csrCs = clinicalStatementRelationshipConceptTargetIdMap
                            .get(csrC.getConceptTargetId());
                    csrCs.add(csrC);
                    clinicalStatementRelationshipConceptTargetIdMap.put(csrC.getConceptTargetId(), csrCs);
                } else {
                    List<ClinicalStatementRelationshipConcept> csrCs = new ArrayList<ClinicalStatementRelationshipConcept>();
                    csrCs.add(csrC);
                    clinicalStatementRelationshipConceptTargetIdMap.put(csrC.getConceptTargetId(), csrCs);
                }
            }
        }
        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts found clinicalStatementRelationshipConcepts="
                + clinicalStatementRelationshipConcepts.size());

        // For each ClinicalStatementRelationship
        for (ClinicalStatementRelationship csr : clinicalStatementRelationships) {
            // 1. Skip a CSR whose relationshipCode is not C405 = "PartOf"
            if (clinicalStatementRelationshipConceptTargetIdMap.get(csr.getId()) != null) {
                List<ClinicalStatementRelationshipConcept> csrCs = clinicalStatementRelationshipConceptTargetIdMap
                        .get(csr.getId());
                boolean foundC405 = false;
                for (ClinicalStatementRelationshipConcept csrC : csrCs) {
                    if ("C405".equals(csrC.getOpenCdsConceptCode())) {
                        foundC405 = true;
                        break;
                    }
                    continue; // keep looking...
                }
                if (!foundC405)
                    continue; // skip if not C405 = "PartOf"
            }

            // 2. Skip a CSR that does not point to an encounter
            if (!eeSet.contains(csr.getSourceId()))
                continue; // skip if sourceId is not in the set of
                          // EncounterEvents

            // 3. See if the targetId is a conceptTargetId for a ProblemConcept
            if (problemConceptTargetIdMap.get(csr.getTargetId()) != null) {
                List<ProblemConcept> probCs = problemConceptTargetIdMap.get(csr.getTargetId());
                for (ProblemConcept probC : probCs) {
                    // 4. If the encounter does not already have an encounter
                    // type concept which is the same concept as the problem
                    // concept,
                    // add the encounterConcept (with the appropriate
                    // encounterId) to encounterEventConcepts
                    String candidateEncounterTypeConceptHash = probC.getOpenCdsConceptCode() + "|" + csr.getSourceId();
                    if (!etcSet.contains(candidateEncounterTypeConceptHash)) {
                        EncounterTypeConcept newEtc = new EncounterTypeConcept();
                        newEtc.setConceptTargetId(csr.getSourceId());
                        newEtc.setOpenCdsConceptCode(probC.getOpenCdsConceptCode());
                        newEtc.setDeterminationMethodCode(probC.getDeterminationMethodCode());
                        // newEtc.setDisplayName(probC.getDisplayName());
                        newEtc.setId(probC.getId() + "^generated");
                        encounterTypeConcepts.add(newEtc);

                        // now add it to hashset, so we don't try to create it
                        // again...
                        etcSet.add(candidateEncounterTypeConceptHash);
                    }
                }
            }

            // 5. See if the targetId is a conceptTargetId for a
            // ProcedureConcept
            if (procedureConceptTargetIdMap.get(csr.getTargetId()) != null) {
                List<ProcedureConcept> procCs = procedureConceptTargetIdMap.get(csr.getTargetId());
                for (ProcedureConcept procC : procCs) {
                    // 6. If the encounter does not already have an encounter
                    // type concept which is the same concept as the procedure
                    // concept,
                    // add the encounterConcept (with the appropriate
                    // encounterId) to encounterEventConcepts
                    String candidateEncounterTypeConceptHash = procC.getOpenCdsConceptCode() + "|" + csr.getSourceId();
                    if (!etcSet.contains(candidateEncounterTypeConceptHash)) {
                        EncounterTypeConcept newEtc = new EncounterTypeConcept();
                        newEtc.setConceptTargetId(csr.getSourceId());
                        newEtc.setOpenCdsConceptCode(procC.getOpenCdsConceptCode());
                        newEtc.setDeterminationMethodCode(procC.getDeterminationMethodCode());
                        // newEtc.setDisplayName(procC.getDisplayName());
                        newEtc.setId(procC.getId() + "^generated");
                        encounterTypeConcepts.add(newEtc);

                        // now add it to hashset, so we don't try to create it
                        // again...
                        etcSet.add(candidateEncounterTypeConceptHash);
                    }
                }
            }
        }

        log.debug("addToEncounterConceptListFromProblemAndProcedureConcepts finished with EncounterTypeConcepts.size()="
                + encounterTypeConcepts.size());
        return;
    }

}
