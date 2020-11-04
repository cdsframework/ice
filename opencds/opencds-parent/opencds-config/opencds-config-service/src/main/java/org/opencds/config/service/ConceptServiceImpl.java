package org.opencds.config.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.Concept;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ConceptMapping;
import org.opencds.config.api.model.ConceptView;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.SecondaryCDM;
import org.opencds.config.api.model.SupportMethod;
import org.opencds.config.api.model.impl.ConceptImpl;
import org.opencds.config.api.model.impl.ConceptViewImpl;
import org.opencds.config.api.service.ConceptDeterminationMethodService;
import org.opencds.config.api.service.ConceptService;
import org.opencds.config.api.service.KnowledgeModuleService;

public class ConceptServiceImpl implements ConceptService, Observer {
	private static final Logger log = LogManager.getLogger();
    private static final String ALL_CONCEPTS = "allConcepts";

    private ConceptDeterminationMethodService conceptDeterminationMethodService;
    private KnowledgeModuleService knowledgeModuleService;
    private CacheService cacheService;
    // cachable
    private Map<Concept, List<ConceptMap>> conceptMaps;

    public ConceptServiceImpl(ConceptDeterminationMethodService conceptDeterminationMethodService,
            KnowledgeModuleService knowledgeModuleService, CacheService cacheService) {
        this.conceptDeterminationMethodService = conceptDeterminationMethodService;
        ((Observable) conceptDeterminationMethodService).addObserver(this);
        log.debug("Added this as observer to service: " + conceptDeterminationMethodService);
        this.knowledgeModuleService = knowledgeModuleService;
        ((Observable) knowledgeModuleService).addObserver(this);
        log.debug("Added this as observer to service: " + knowledgeModuleService);
        this.cacheService = cacheService;
        // initially load all of them.
        loadAllConceptServices(knowledgeModuleService.getAll(), false);
    }

    private ConceptServiceImpl(ConceptServiceImpl conceptService, Map<Concept, List<ConceptMap>> conceptMaps) {
        this.cacheService = conceptService.cacheService;
        this.conceptMaps = conceptMaps;
    }

    private void deleteConceptService(KnowledgeModule knowledgeModule) {
        if (cacheService.get(ConceptServiceCacheRegion.CS_BY_KM, knowledgeModule.getKMId()) != null) {
            log.debug("Evicting cached Concept Service");
            cacheService.evict(ConceptServiceCacheRegion.CS_BY_KM, knowledgeModule.getKMId());
        }
    }

    private void reloadAllConceptServices(List<KnowledgeModule> knowledgeModules) {
        loadAllConceptServices(knowledgeModules, true);
    }

    private void reloadConceptServiceForKM(KnowledgeModule knowledgeModule) {
        deleteConceptService(knowledgeModule);
        cacheConceptServiceByKM(knowledgeModule);
        log.debug("Reloaded concept service cache for KM: " + knowledgeModule.getKMId());
    }

    @Override
    public List<ConceptView> getConceptViews(String codeSystem, String code) {
        List<ConceptView> conceptViews = new ArrayList<>();
        if (conceptMaps == null) {
            throw new OpenCDSRuntimeException("Unsupported ... <what?>");
        }

        log.debug("Finding concept in conceptMaps: codeSystem= " + codeSystem + ", code= " + code);
        List<ConceptMap> concepts = conceptMaps.get(ConceptImpl.create(code, codeSystem, null, null));
        log.debug("Concepts found in conceptMaps: " + concepts);
        if (concepts != null) {
            for (ConceptMap cm : concepts) {
                conceptViews.add(new ConceptViewImpl(cm.getToConcept(), cm.getCdmCode()));
                log.debug("Adding conceptView: to= " + cm.getToConcept() + ", cdm= " + cm.getCdmCode());
            }
        }

        return conceptViews;
    }

    @Override
    public ConceptService byKM(KnowledgeModule knowledgeModule) {
        ConceptService cs = cacheService.get(ConceptServiceCacheRegion.CS_BY_KM, knowledgeModule.getKMId());
        if (cs == null) {
            cs = cacheConceptServiceByKM(knowledgeModule);
        }
        return cs;
    }

    @Override
    public void update(Observable o, Object arg) {
        log.debug("Called by object: " + o);
        if (arg == null) {
            reloadAllConceptServices(knowledgeModuleService.getAll());
            log.debug("Reloaded all ConceptServices");
        } else if (arg instanceof KnowledgeModule) {
            KnowledgeModule km = KnowledgeModule.class.cast(arg);
            // we check whether the KM was removed
            if (knowledgeModuleService.find(km.getKMId()) == null) {
                deleteConceptService(km);
                log.debug("Deleted concept service for KM: " + km.getKMId());
            } else {
                reloadConceptServiceForKM(km);
                log.debug("Reloaded concept service for KM: " + km.getKMId());
            }
        }
    }

    // privates

    private void loadAllConceptServices(List<KnowledgeModule> knowledgeModules, boolean purge) {
        // load ConceptServices for each KMs
        for (KnowledgeModule km : knowledgeModules) {
            loadConceptService(km, purge);
        }

        // in case this instance is used directly, set the default conceptMaps.
        Map<Concept, List<ConceptMap>> cms = cacheService.get(ConceptServiceCacheRegion.CONCEPTS, ALL_CONCEPTS);
        if (cms == null || cms.isEmpty()) {
            cms = new HashMap<>();
            resolveAllConcepts(cms);
            log.debug("All concepts? " + cms.size());
            cacheService.put(ConceptServiceCacheRegion.CONCEPTS, ALL_CONCEPTS, cms);
        }
        log.debug("Concepts cached? " + cms.size());
        conceptMaps = cms;
        log.debug("All concepts: " + conceptMaps.size());
    }

    private void loadConceptService(KnowledgeModule knowledgeModule, boolean purge) {
        log.debug("Caching concepts for KM: " + knowledgeModule.getKMId());
        cacheConceptServiceByKM(knowledgeModule);
    }

    private ConceptService cacheConceptServiceByKM(KnowledgeModule knowledgeModule) {
        Map<Concept, List<ConceptMap>> conceptMaps = buildConceptMaps(knowledgeModule);
        log.debug("KM " + knowledgeModule.getKMId() + " gets conceptMap: " + conceptMaps.hashCode());
        ConceptService cs = new ConceptServiceImpl(this, conceptMaps);
        cacheService.put(ConceptServiceCacheRegion.CS_BY_KM, knowledgeModule.getKMId(), cs);
        return cs;
    }

    /**
     * Build the concept maps.
     * <p>
     * If the KM doesn't have an associated Primary CDM:
     * <ul>
     * <li>the cache is checked for the ALL_CONCEPTS conceptMaps</li>
     * <ul>
     * <li>if this exists, then the ALL_CONCEPTS conceptMaps are returned.</li>
     * <li>if not, then the ALL_CONCEPTS is created and cached, and then
     * returned.</li>
     * </ul>
     * <li>any secondary CDMs are ignored in this case</li> </ul>
     * <p>
     * If the KM has an associated Primary CDM, a new conceptMaps is built and
     * returned.
     * <p>
     * The returned value is an unmodifiable map (from Collections).
     */
    private Map<Concept, List<ConceptMap>> buildConceptMaps(KnowledgeModule km) {
        Map<Concept, List<ConceptMap>> cms = new HashMap<>();
        CDMId primaryCDMId = km.getPrimaryCDM();
        log.debug("has primary CDMId? : " + primaryCDMId);
        /*
         * If there is no primaryCDM, check that we have the ALL_CONCEPTS
         * Concepts service... if we don't, then we create one and cache it. The
         * default is for the KM to see all of the concepts.
         */
        if (primaryCDMId == null) {
            Map<Concept, List<ConceptMap>> cached = cacheService.get(ConceptServiceCacheRegion.CONCEPTS, ALL_CONCEPTS);
            if (cached == null) {
                cached = new HashMap<>();
                resolveAllConcepts(cached);
                cacheService.put(ConceptServiceCacheRegion.CONCEPTS, ALL_CONCEPTS, cached);
            }
            cms = cached;
        } else {
            ConceptDeterminationMethod primaryCDM = conceptDeterminationMethodService.find(primaryCDMId);
            log.debug("Found primaryCDM: " + primaryCDM);
            if (primaryCDM == null) {
                log.error("primaryCDM does not exist for KM: " + km.getKMId());
            } else {
                addAllConceptMaps(cms, resolveConceptMaps(primaryCDM));
            }

            // we are concerned with local codes (from concepts) and how they
            // map to concepts
            // Secondary CDM concept mappings will override (or add to)
            // primary......
            List<SecondaryCDM> secondaryCDMs = km.getSecondaryCDMs();
            if (secondaryCDMs != null) {
                for (SecondaryCDM sec : secondaryCDMs) {
                    ConceptDeterminationMethod secondaryCDM = conceptDeterminationMethodService.find(sec.getCDMId());
                    if (secondaryCDM == null) {
                        log.error("secondaryCDM does not exist for KM: " + km.getKMId());
                    } else {
                        List<ConceptMap> secondaries = resolveConceptMaps(secondaryCDM);
                        if (sec.getSupportMethod() == SupportMethod.ADDITIVE) {
                            addAllConceptMaps(cms, secondaries);
                        } else if (sec.getSupportMethod() == SupportMethod.REPLACEMENT) {
                            replaceConceptMaps(cms, secondaries);
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableMap(cms);
    }

    private void resolveAllConcepts(Map<Concept, List<ConceptMap>> cms) {
        for (ConceptDeterminationMethod cdm : conceptDeterminationMethodService.getAll()) {
            addAllConceptMaps(cms, resolveConceptMaps(cdm));
        }
    }

    /*
     * Extract the concepts from the mappings; flatten out the hierarchy for the
     * given CDM Once the ConceptMappings are extracted, we no longer need the
     * CDM.
     */
    private List<ConceptMap> resolveConceptMaps(ConceptDeterminationMethod cdm) {
        log.debug("Resolving concept for CDM : " + cdm.getCDMId());
        List<ConceptMap> conceptMaps = new ArrayList<>();
        if (cdm.getConceptMappings() != null) {
            for (ConceptMapping cm : cdm.getConceptMappings()) {
                Concept toConcept = cm.getToConcept();
                for (Concept fromConcept : cm.getFromConcepts()) {
                    conceptMaps.add(new ConceptMap(toConcept, fromConcept, cdm.getCDMId().getCode()));
                    // log.debug("Adding concept: to= " + toConcept + ", from= "
                    // + fromConcept + ", cdm= " + cdm.getCDMId());
                }
            }
        }
        return conceptMaps;
    }

    private void addAllConceptMaps(Map<Concept, List<ConceptMap>> cmMap, List<ConceptMap> cms) {
        for (ConceptMap cm : cms) {
            addConceptMap(cmMap, cm);
        }
    }

    private void addConceptMap(Map<Concept, List<ConceptMap>> cms, ConceptMap cm) {
        if (cms.containsKey(cm.getFromConcept())) {
            cms.get(cm.getFromConcept()).add(cm);
        } else {
            List<ConceptMap> cmlist = new ArrayList<>();
            cmlist.add(cm);
            cms.put(cm.getFromConcept(), cmlist);
        }
    }

    private void replaceConceptMaps(Map<Concept, List<ConceptMap>> cms, List<ConceptMap> replacements) {
        List<Concept> fromConcepts = getFromConcepts(replacements);
        for (Concept c : fromConcepts) {
            cms.put(c, new ArrayList<ConceptMap>());
        }
        for (ConceptMap cm : replacements) {
            cms.get(cm.getFromConcept()).add(cm);
        }

    }

    private List<Concept> getFromConcepts(List<ConceptMap> concepts) {
        List<Concept> fromConcepts = new ArrayList<>();
        for (ConceptMap cm : concepts) {
            fromConcepts.add(cm.getFromConcept());
        }
        return fromConcepts;
    }

    private static class ConceptMap {
        private Concept toConcept;
        private Concept fromConcept;
        private String cdmCode;

        public ConceptMap(Concept toConcept, Concept fromConcept, String cdmCode) {
            this.toConcept = toConcept;
            this.fromConcept = fromConcept;
            this.cdmCode = cdmCode;
        }

        public Concept getToConcept() {
            return toConcept;
        }

        public Concept getFromConcept() {
            return fromConcept;
        }

        public String getCdmCode() {
            return cdmCode;
        }
    }

    private static enum ConceptServiceCacheRegion implements CacheRegion {
        CS_BY_KM(ConceptService.class),
        CONCEPTS(Map.class);

        private Class<?> type;

        ConceptServiceCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }

    }

}
