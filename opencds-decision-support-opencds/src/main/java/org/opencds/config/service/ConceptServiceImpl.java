package org.opencds.config.service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptView;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.service.ConceptDeterminationMethodService;
import org.opencds.config.api.service.ConceptService;
import org.opencds.config.api.service.KnowledgeModuleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConceptServiceImpl implements ConceptService, PropertyChangeListener
{
    private record Operation(SupportMethod supportMethod,
                             Map<Concept, Set<ConceptMap>> secondaries)
    {
        public static Operation create(final SupportMethod supportMethod, final Map<Concept, Set<ConceptMap>> secondaries)
        {
            return new Operation(supportMethod, secondaries);
        }

        private Operation
        {
            assert supportMethod != null;
            secondaries = secondaries == null ? Map.of() : secondaries;
        }
    }

    public record ConceptMaps(Set<ConceptMap> conceptMaps)
    {
        public static ConceptMaps create(final Set<ConceptMap> conceptMapSet)
        {
            return new ConceptMaps(conceptMapSet);
        }

        public ConceptMaps
        {
            conceptMaps = conceptMaps == null ? Set.of() : Collections.unmodifiableSet(conceptMaps);
        }
    }

    public record ConceptMap(Concept toConcept,
                             Concept fromConcept,
                             String cdmCode)
    {
        public static ConceptMap create(final Concept toConcept, final Concept fromConcept, final String cdmCode)
        {
            return new ConceptMap(toConcept, fromConcept, cdmCode);
        }
    }

    @RequiredArgsConstructor
    public static class KMConceptService implements ConceptService
    {
        private final Map<Concept, ConceptMaps> conceptMaps;

        @Override
        public List<ConceptView> getConceptViews(final String codeSystem, final String code)
        {
            if (log.isDebugEnabled())
                log.debug("Finding concept in conceptMaps: codeSystem= {}, code= {}", codeSystem, code);

            return conceptMaps.keySet()
                    .stream()
                    .filter(c -> Strings.CI.equals(c.codeSystem(), codeSystem))
                    .filter(c -> Strings.CI.equals(c.code(), code))
                    .map(conceptMaps::get)
                    .map(ConceptMaps::conceptMaps)
                    .flatMap(Collection::stream)
                    .map(cm -> new ConceptView(cm.toConcept(), cm.cdmCode()))
                    .toList();
        }

        @Override
        public ConceptService byKM(final KnowledgeModule knowledgeModule)
        {
            return this;
        }
    }

    private final ConceptDeterminationMethodService conceptDeterminationMethodService;
    private final KnowledgeModuleService knowledgeModuleService;
    private final Map<Concept, ConceptMaps> conceptMap = new ConcurrentHashMap<>();
    private final Map<CDMId, ConceptMaps> conceptMapByCdmId = new ConcurrentHashMap<>();
    private final Map<KMId, KMConceptService> kmConceptServiceMap = new ConcurrentHashMap<>();

    public ConceptServiceImpl(final ConceptDeterminationMethodService conceptDeterminationMethodService,
            final KnowledgeModuleService knowledgeModuleService)
    {
        this.conceptDeterminationMethodService = conceptDeterminationMethodService;
        conceptDeterminationMethodService.addPropertyChangeListener(this);
        log.debug("Added this as observer to service: {}", conceptDeterminationMethodService);
        this.knowledgeModuleService = knowledgeModuleService;
        log.debug("Added this as observer to service: {}", knowledgeModuleService);

        conceptDeterminationMethodService.getAll().forEach(this::resolveConceptMaps);
        knowledgeModuleService.getAll().forEach(this::cacheConceptServiceByKM);
    }

    @Override
    public List<ConceptView> getConceptViews(final String codeSystem, final String code)
    {
        if (log.isDebugEnabled())
            log.debug("Finding concept in conceptMaps: codeSystem= {}, code= {}", codeSystem, code);

        return conceptMap.entrySet()
                .stream()
                .filter(e -> Strings.CI.equals(e.getKey().codeSystem(), codeSystem))
                .filter(e -> Strings.CI.equals(e.getKey().code(), code))
                .map(Map.Entry::getValue)
                .map(ConceptMaps::conceptMaps)
                .flatMap(Collection::stream)
                .map(cm -> new ConceptView(cm.toConcept(), cm.cdmCode()))
                .toList();
    }

    @Override
    public ConceptService byKM(final KnowledgeModule knowledgeModule)
    {
        return Optional.ofNullable(knowledgeModule)
                .map(KnowledgeModule::kmId)
                .map(kmConceptServiceMap::get)
                .orElseGet(() -> cacheConceptServiceByKM(knowledgeModule));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        log.debug("Called by object: {}", evt.getSource());

        conceptMap.clear();
        knowledgeModuleService.getAll().forEach(this::cacheConceptServiceByKM);
        log.debug("Reloaded all ConceptServices");
    }

    private KMConceptService cacheConceptServiceByKM(final KnowledgeModule knowledgeModule)
    {
        final var conceptMaps = buildKMSpecificConceptMaps(knowledgeModule);
        log.debug("KM {} gets conceptMap: {}", knowledgeModule.kmId(), conceptMaps.hashCode());
        final KMConceptService cs = new KMConceptService(conceptMaps);
        kmConceptServiceMap.put(knowledgeModule.kmId(), cs);
        return cs;
    }

    private Map<Concept, ConceptMaps> buildKMSpecificConceptMaps(final KnowledgeModule km)
    {
        return Optional.ofNullable(km)
                .map(KnowledgeModule::primaryCDM)
                .map(primaryCDMId -> applyOperations(conceptMapByCDMId(primaryCDMId), buildOperationsDeque(km)))
                .map(this::buildConceptMaps)
                .orElse(conceptMap);
    }

    private Map<Concept, ConceptMaps> buildConceptMaps(final Map<Concept, Set<ConceptMap>> conceptSetMap)
    {
        return conceptSetMap.entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), ConceptMaps.create(e.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Deque<Operation> buildOperationsDeque(final KnowledgeModule km)
    {
        return Optional.ofNullable(km)
                .map(KnowledgeModule::secondaryCDMs)
                .stream()
                .flatMap(Collection::stream)
                .map(sec -> Pair.of(sec, conceptDeterminationMethodService.find(sec.cdmId())))
                .filter(pair ->
                {
                    if (pair.getRight() == null)
                    {
                        log.error("secondaryCDM specified by KM does not exist: km='{}', secondaryCDM='{}'", km.kmId(),
                                pair.getLeft());
                        return false;
                    }
                    return true;
                })
                .map(pair -> Pair.of(pair.getLeft(), conceptMapByCDMId(pair.getRight().cdmId())))
                .map(pair -> Operation.create(pair.getLeft().supportMethod(), pair.getRight()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Map<Concept, Set<ConceptMap>> conceptMapByCDMId(final CDMId primaryCDMId)
    {
        return conceptMapByCdmId.get(primaryCDMId)
                .conceptMaps()
                .stream()
                .map(cm -> Map.entry(cm.fromConcept(), cm))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
    }

    private Map<Concept, Set<ConceptMap>> applyOperations(final Map<Concept, Set<ConceptMap>> primaries,
            final Deque<Operation> operations)
    {
        if (operations.isEmpty())
            return primaries;

        return applyOperations(applyOperation(primaries, operations.pollFirst()), operations);
    }

    private Map<Concept, Set<ConceptMap>> applyOperation(final Map<Concept, Set<ConceptMap>> primaries, final Operation operation)
    {
        return switch (operation.supportMethod())
        {
            case ADDITIVE -> add(primaries, operation.secondaries());
            case REPLACEMENT -> replace(primaries, operation.secondaries());
            case RETRACTIVE -> retract(primaries, operation.secondaries());
        };
    }

    private Map<Concept, Set<ConceptMap>> add(final Map<Concept, Set<ConceptMap>> primaries,
            final Map<Concept, Set<ConceptMap>> secondaries)
    {
        return primaries.entrySet()
                .stream()
                .map(entry -> Optional.ofNullable(secondaries.get(entry.getKey()))
                        .map(v -> Map.entry(entry.getKey(),
                                Stream.concat(entry.getValue().stream(), v.stream()).collect(Collectors.toUnmodifiableSet())))
                        .orElse(entry))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Concept, Set<ConceptMap>> replace(final Map<Concept, Set<ConceptMap>> primaries,
            final Map<Concept, Set<ConceptMap>> secondaries)
    {
        return primaries.entrySet()
                .stream()
                .map(entry -> Optional.of(secondaries)
                        .filter(secs -> secs.containsKey(entry.getKey()))
                        .map(secs -> Map.entry(entry.getKey(), secs.get(entry.getKey())))
                        .orElse(entry))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Concept, Set<ConceptMap>> retract(final Map<Concept, Set<ConceptMap>> primaries,
            final Map<Concept, Set<ConceptMap>> secondaries)
    {
        return primaries.entrySet().stream().map(primaryEntry ->
        {
            final var secondaryConceptMaps = secondaries.getOrDefault(primaryEntry.getKey(), Set.of());
            return Map.entry(primaryEntry.getKey(),
                    primaryEntry.getValue().stream().filter(cm -> !secondaryConceptMaps.contains(cm)).collect(Collectors.toSet()));
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void resolveConceptMaps(final ConceptDeterminationMethod cdm)
    {
        log.debug("Resolving concept for CDM : {}", cdm.cdmId());
        buildConceptMapsByFromConcept(cdm);
        buildConceptMapsByCDMId(cdm);
    }

    private void buildConceptMapsByFromConcept(final ConceptDeterminationMethod cdm)
    {
        final var cdmCode = cdm.cdmId().code();
        conceptMap.putAll(Optional.ofNullable(cdm.conceptMappings())
                .stream()
                .parallel()
                .flatMap(Collection::stream)
                .flatMap(mapping -> mapping.fromConcepts()
                        .stream()
                        .map(fromConcept -> ConceptMap.create(mapping.toConcept(), fromConcept, cdmCode)))
                .map(conceptMap -> Map.entry(conceptMap.fromConcept, conceptMap))
                .collect(Collectors.groupingByConcurrent(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ConceptMaps.create(e.getValue()))));
    }

    private void buildConceptMapsByCDMId(final ConceptDeterminationMethod cdm)
    {
        conceptMapByCdmId.put(cdm.cdmId(), ConceptMaps.create(conceptMap.values()
                .stream()
                .map(ConceptMaps::conceptMaps)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())));
    }
}
