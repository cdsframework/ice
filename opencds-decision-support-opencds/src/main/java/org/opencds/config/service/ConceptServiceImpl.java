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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import org.opencds.common.cache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptView;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.model.impl.ConceptViewImpl;
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
                    .filter(c -> Strings.CI.equals(c.getCodeSystem(), codeSystem))
                    .filter(c -> Strings.CI.equals(c.getCode(), code))
                    .map(conceptMaps::get)
                    .map(ConceptMaps::conceptMaps)
                    .flatMap(Collection::stream)
                    .map(cm -> ConceptViewImpl.create(cm.toConcept(), cm.cdmCode()))
                    .map(cvi -> (ConceptView) cvi)
                    .toList();
        }

        @Override
        public ConceptService byKM(final KnowledgeModule knowledgeModule)
        {
            return this;
        }
    }

    private static final CacheRegion<KMId, KMConceptService> KM_CONCEPT_SERVICE =
            CacheRegion.create(KMId.class, KMConceptService.class);
    private static final CacheRegion<Concept, ConceptMaps> CONCEPTS = CacheRegion.create(Concept.class, ConceptMaps.class);
    private static final CacheRegion<CDMId, ConceptMaps> CONCEPT_MAP_BY_CDMID = CacheRegion.create(CDMId.class, ConceptMaps.class);

    private final ConceptDeterminationMethodService conceptDeterminationMethodService;
    private final KnowledgeModuleService knowledgeModuleService;
    private final CacheService cacheService;

    public ConceptServiceImpl(final ConceptDeterminationMethodService conceptDeterminationMethodService,
            final KnowledgeModuleService knowledgeModuleService, final CacheService cacheService)
    {
        this.conceptDeterminationMethodService = conceptDeterminationMethodService;
        conceptDeterminationMethodService.addPropertyChangeListener(this);
        log.debug("Added this as observer to service: {}", conceptDeterminationMethodService);
        this.knowledgeModuleService = knowledgeModuleService;
        log.debug("Added this as observer to service: {}", knowledgeModuleService);
        this.cacheService = cacheService;
        loadConceptMaps();
        loadAllConceptServices(knowledgeModuleService.getAll(), false);
    }

    private void loadConceptMaps()
    {
        conceptDeterminationMethodService.getAll().forEach(this::resolveConceptMaps);
    }

    private void reloadConceptMaps()
    {
        cacheService.evictAll(CONCEPTS);
    }

    private void deleteConceptService(final KnowledgeModule knowledgeModule)
    {
        if (cacheService.containsKey(KM_CONCEPT_SERVICE, knowledgeModule.getKMId()))
        {
            log.debug("Evicting cached Concept Service");
            cacheService.evict(KM_CONCEPT_SERVICE, knowledgeModule.getKMId());
        }
    }

    private void reloadAllConceptServices(final List<KnowledgeModule> knowledgeModules)
    {
        loadAllConceptServices(knowledgeModules, true);
    }

    private void reloadConceptServiceForKM(final KnowledgeModule knowledgeModule)
    {
        deleteConceptService(knowledgeModule);
        cacheConceptServiceByKM(knowledgeModule);
        log.debug("Reloaded concept service cache for KM: {}", knowledgeModule.getKMId());
    }

    @Override
    public List<ConceptView> getConceptViews(final String codeSystem, final String code)
    {
        if (log.isDebugEnabled())
            log.debug("Finding concept in conceptMaps: codeSystem= {}, code= {}", codeSystem, code);
        return cacheService.getAllKeys(CONCEPTS)
                .stream()
                .filter(c -> Strings.CI.equals(c.getCodeSystem(), codeSystem))
                .filter(c -> Strings.CI.equals(c.getCode(), code))
                .map(c -> cacheService.get(CONCEPTS, c))
                .map(ConceptMaps::conceptMaps)
                .flatMap(Collection::stream)
                .map(cm -> ConceptViewImpl.create(cm.toConcept(), cm.cdmCode()))
                .map(cvi -> (ConceptView) cvi)
                .toList();
    }

    @Override
    public ConceptService byKM(final KnowledgeModule knowledgeModule)
    {
        return Optional.ofNullable(knowledgeModule)
                .map(KnowledgeModule::getKMId)
                .map(kmId -> cacheService.get(KM_CONCEPT_SERVICE, kmId))
                .orElseGet(() -> cacheConceptServiceByKM(knowledgeModule));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt)
    {
        log.debug("Called by object: {}", evt.getSource());

        reloadConceptMaps();
        reloadAllConceptServices(knowledgeModuleService.getAll());
        log.debug("Reloaded all ConceptServices");
    }

    private void loadAllConceptServices(final List<KnowledgeModule> knowledgeModules, final boolean purge)
    {
        knowledgeModules.forEach(this::cacheConceptServiceByKM);
    }

    private KMConceptService cacheConceptServiceByKM(final KnowledgeModule knowledgeModule)
    {
        final var conceptMaps = buildKMSpecificConceptMaps(knowledgeModule);
        log.debug("KM {} gets conceptMap: {}", knowledgeModule.getKMId(), conceptMaps.hashCode());
        final KMConceptService cs = new KMConceptService(conceptMaps);
        cacheService.put(KM_CONCEPT_SERVICE, knowledgeModule.getKMId(), cs);
        return cs;
    }

    private Map<Concept, ConceptMaps> buildKMSpecificConceptMaps(final KnowledgeModule km)
    {
        return Optional.ofNullable(km)
                .map(KnowledgeModule::getPrimaryCDM)
                .map(primaryCDMId -> applyOperations(conceptMapByCDMId(primaryCDMId), buildOperationsDeque(km)))
                .map(this::buildConceptMaps)
                .orElseGet(() -> cacheService.getAll(CONCEPTS));
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
                .map(KnowledgeModule::getSecondaryCDMs)
                .stream()
                .flatMap(Collection::stream)
                .map(sec -> Pair.of(sec, conceptDeterminationMethodService.find(sec.getCDMId())))
                .filter(pair ->
                {
                    if (pair.getRight() == null)
                    {
                        log.error("secondaryCDM specified by KM does not exist: km='{}', secondaryCDM='{}'", km.getKMId(),
                                pair.getLeft());
                        return false;
                    }
                    return true;
                })
                .map(pair -> Pair.of(pair.getLeft(), conceptMapByCDMId(pair.getRight().getCDMId())))
                .map(pair -> Operation.create(pair.getLeft().getSupportMethod(), pair.getRight()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Map<Concept, Set<ConceptMap>> conceptMapByCDMId(final CDMId primaryCDMId)
    {
        return cacheService.get(CONCEPT_MAP_BY_CDMID, primaryCDMId)
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
        final var op = operations.pollFirst();
        return applyOperations(applyOperation(primaries, op), operations);
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
        return primaries.entrySet().stream().map(entry ->
        {
            if (secondaries.containsKey(entry.getKey()))
            {
                final var secondaryCM = secondaries.get(entry.getKey());
                return Map.entry(entry.getKey(),
                        Stream.concat(entry.getValue().stream(), secondaryCM.stream()).collect(Collectors.toUnmodifiableSet()));
            }
            return entry;
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
        log.debug("Resolving concept for CDM : {}", cdm.getCDMId());
        buildConceptMapsByFromConcept(cdm);
        buildConceptMapsByCDMId(cdm);
    }

    private void buildConceptMapsByFromConcept(final ConceptDeterminationMethod cdm)
    {
        final var cdmCode = cdm.getCDMId().getCode();
        Optional.ofNullable(cdm.getConceptMappings())
                .stream()
                .parallel()
                .flatMap(Collection::stream)
                .flatMap(mapping -> mapping.getFromConcepts()
                        .stream()
                        .map(fromConcept -> ConceptMap.create(mapping.getToConcept(), fromConcept, cdmCode)))
                .map(conceptMap -> Map.entry(conceptMap.fromConcept, conceptMap))
                .collect(Collectors.groupingByConcurrent(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> ConceptMaps.create(e.getValue())))
                .forEach((fromConcept, conceptMaps) -> cacheService.put(CONCEPTS, fromConcept, conceptMaps));
    }

    private void buildConceptMapsByCDMId(final ConceptDeterminationMethod cdm)
    {
        Optional.ofNullable(cacheService.getAll(CONCEPTS))
                .map(Map::entrySet)
                .map(Collection::stream)
                .map(stream -> stream.map(Map.Entry::getValue)
                        .map(ConceptMaps::conceptMaps)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()))
                .map(ConceptMaps::create)
                .ifPresent(conceptMaps -> cacheService.put(CONCEPT_MAP_BY_CDMID, cdm.getCDMId(), conceptMaps));
    }
}
