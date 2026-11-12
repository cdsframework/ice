package org.cdsframework.ice.service.configurations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.commands.runtime.rule.InsertElementsCommand;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.ExecutionResults;
import org.opencds.config.api.EvaluationContext;
import org.opencds.vmr.v1_0.internal.AdministrableSubstance;
import org.opencds.vmr.v1_0.internal.SubstanceAdministrationEvent;
import org.opencds.vmr.v1_0.internal.concepts.ImmunizationConcept;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.opencds.vmr.v1_0.internal.datatypes.IVLDate;

class IceExecutionEngineContextTest
{
    private static SubstanceAdministrationEvent event(final String evaluatedPersonId, final String id, final String vaccineCode,
            final LocalDate administrationDate, final boolean highOnlyDate)
    {
        final IVLDate administrationTimeInterval = new IVLDate();
        if (highOnlyDate)
            administrationTimeInterval.setHigh(administrationDate);
        else
            administrationTimeInterval.setLow(administrationDate);

        final AdministrableSubstance substance = new AdministrableSubstance();
        substance.setSubstanceCode(CD.builder().code(vaccineCode).build());

        final SubstanceAdministrationEvent event = new SubstanceAdministrationEvent();
        event.setEvaluatedPersonId(evaluatedPersonId);
        event.setId(id);
        event.setSubstance(substance);
        event.setAdministrationTimeInterval(administrationTimeInterval);
        return event;
    }

    private static ImmunizationConcept concept(final String conceptTargetId)
    {
        final ImmunizationConcept concept = new ImmunizationConcept();
        concept.setConceptTargetId(conceptTargetId);
        return concept;
    }

    @Test
    void ordersAdministrationEventsAndTheirConceptsIntoMutableCopiesWithoutMutatingMappedFacts()
    {
        final List<SubstanceAdministrationEvent> events =
                List.of(event("other-person", "other-person-event", "1", LocalDate.of(2021, 1, 1), false),
                        event("patient", "nonnumeric-z", "XYZ", LocalDate.of(2020, 1, 1), false),
                        event("patient", "cvx-119", "119", LocalDate.of(2020, 1, 1), false),
                        event("patient", "cvx-74-b", "74", LocalDate.of(2020, 1, 1), false),
                        event("patient", "high-only-date", "999", LocalDate.of(2019, 12, 30), true),
                        event("patient", "cvx-7", "7", LocalDate.of(2020, 1, 1), false),
                        event("patient", "nonnumeric-a", "ABC", LocalDate.of(2020, 1, 1), false),
                        event("patient", "cvx-74-a", "74", LocalDate.of(2020, 1, 1), false));
        final List<String> expectedEventOrder =
                List.of("other-person-event", "high-only-date", "cvx-7", "cvx-74-a", "cvx-74-b", "cvx-119", "nonnumeric-a",
                        "nonnumeric-z");
        final List<SubstanceAdministrationEvent> reversedEvents = new ArrayList<>(events);
        Collections.reverse(reversedEvents);
        final List<ImmunizationConcept> concepts =
                new ArrayList<>(reversedEvents.stream().map(event -> concept(event.getId())).toList());
        concepts.add(0, concept("order-before"));
        concepts.add(4, concept("order-middle"));
        concepts.add(concept("order-after"));
        final Map<Class<?>, List<?>> mappedFacts = new HashMap<>();
        mappedFacts.put(SubstanceAdministrationEvent.class, reversedEvents);
        mappedFacts.put(ImmunizationConcept.class, concepts);

        final LocalDate evaluationDate = LocalDate.of(2021, 1, 1);
        final EvaluationContext evaluationContext =
                new EvaluationContext("patient", evaluationDate, null, "en", null, Set.of(), Map.of(), Map.of(), mappedFacts, null);
        final Map<String, Collection<Object>> insertedFacts =
                new IceExecutionEngineContext().setEvaluationContext(evaluationContext)
                        .getInput()
                        .stream()
                        .filter(InsertElementsCommand.class::isInstance)
                        .map(InsertElementsCommand.class::cast)
                        .collect(Collectors.toMap(InsertElementsCommand::getOutIdentifier, InsertElementsCommand::getObjects));

        final List<SubstanceAdministrationEvent> orderedEvents =
                insertedFacts.get("SubstanceAdministrationEvent").stream().map(SubstanceAdministrationEvent.class::cast).toList();
        final List<ImmunizationConcept> orderedConcepts =
                insertedFacts.get("ImmunizationConcept").stream().map(ImmunizationConcept.class::cast).toList();
        assertEquals(expectedEventOrder, orderedEvents.stream().map(SubstanceAdministrationEvent::getId).toList());
        assertEquals(
                List.of("order-before", "other-person-event", "high-only-date", "cvx-7", "order-middle", "cvx-74-a", "cvx-74-b",
                        "cvx-119", "nonnumeric-a", "nonnumeric-z", "order-after"),
                orderedConcepts.stream().map(ImmunizationConcept::getConceptTargetId).toList());
        assertEquals("cvx-74-a", reversedEvents.getFirst().getId());
        assertNotSame(reversedEvents, insertedFacts.get("SubstanceAdministrationEvent"));
        assertNotSame(concepts, insertedFacts.get("ImmunizationConcept"));
        assertDoesNotThrow(() -> insertedFacts.get("SubstanceAdministrationEvent").clear());
        assertDoesNotThrow(() -> insertedFacts.get("ImmunizationConcept").clear());
    }

    @Test
    @SuppressWarnings("unchecked")
    void copiesEngineFactsIntoMutableListsAndKeepsGlobalWrappersUnmodifiable()
    {
        final List<SubstanceAdministrationEvent> engineEvents = List.of(new SubstanceAdministrationEvent());
        final ExecutionResults results = mock(ExecutionResults.class);
        when(results.getIdentifiers()).thenReturn(List.of("SubstanceAdministrationEvent", "evalTime"));
        when(results.getValue("SubstanceAdministrationEvent")).thenReturn(engineEvents);
        final Map<String, Object> namedObjects = new HashMap<>();
        final EvaluationContext evaluationContext = mock(EvaluationContext.class);
        when(evaluationContext.namedObjects()).thenReturn(namedObjects);

        final Map<String, List<?>> resultFactLists =
                new IceExecutionEngineContext().setEvaluationContext(evaluationContext).setResults(results).getResults();

        assertEquals(List.of("SubstanceAdministrationEvent", "assertions", "namedObjects"), List.copyOf(resultFactLists.keySet()));
        assertNotSame(engineEvents, resultFactLists.get("SubstanceAdministrationEvent"));
        assertDoesNotThrow(
                () -> ((List<Object>) resultFactLists.get("SubstanceAdministrationEvent")).add(new SubstanceAdministrationEvent()));
        assertEquals(1, engineEvents.size());
        assertEquals(2, resultFactLists.get("SubstanceAdministrationEvent").size());
        assertSame(namedObjects, resultFactLists.get("assertions").getFirst());
        assertSame(namedObjects, resultFactLists.get("namedObjects").getFirst());
        assertThrows(UnsupportedOperationException.class, () -> resultFactLists.get("assertions").clear());
        assertThrows(UnsupportedOperationException.class, () -> resultFactLists.get("namedObjects").clear());
    }
}
