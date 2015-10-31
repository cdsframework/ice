package org.opencds.config.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.config.util.EntityIdentifierUtil;

import com.google.common.collect.ImmutableList;

public class KnowledgeModuleServiceImpl extends Observable implements KnowledgeModuleService {
    private final KnowledgeModuleDao dao;
    private final KnowledgePackageService knowledgePackageService;
    private final SupportingDataService supportingDataService;
    private CacheService cacheService;

    public KnowledgeModuleServiceImpl(KnowledgeModuleDao dao, KnowledgePackageService knowledgePackageService,
            SupportingDataService supportingDataService, CacheService cacheService) {
        this.dao = dao;
        this.knowledgePackageService = knowledgePackageService;
        this.supportingDataService = supportingDataService;
        this.cacheService = cacheService;
        Map<KMId, KnowledgeModule> knowledgeModules = buildPairs(this.dao.getAll());
        cacheService.putAll(KMCacheRegion.KNOWLEDGE_MODULE, knowledgeModules);
    }

    @Override
    public KnowledgeModule find(KMId kmId) {
        return cacheService.get(KMCacheRegion.KNOWLEDGE_MODULE, kmId);
    }

    @Override
    public KnowledgeModule find(String stringKmId) {
        EntityIdentifier ei = EntityIdentifierUtil.makeEI(stringKmId);
        return find(KMIdImpl.create(ei.getScopingEntityId(), ei.getBusinessId(), ei.getVersion()));
    }

    @Override
    public List<KnowledgeModule> getAll() {
        Set<KnowledgeModule> kms = cacheService.getAll(KMCacheRegion.KNOWLEDGE_MODULE);
        return ImmutableList.<KnowledgeModule> builder().addAll(kms).build();
    }

    @Override
    public void persist(KnowledgeModule km) {
        dao.persist(km);
        cacheService.put(KMCacheRegion.KNOWLEDGE_MODULE, km.getKMId(), km);
        // will create or update the ConceptService for this KM
        tellObservers(km);
    }

    @Override
    public void persist(List<KnowledgeModule> internal) {
        for (KnowledgeModule km : internal) {
            persist(km);
        }
    }

    @Override
    public void delete(KMId kmid) {
        KnowledgeModule km = find(kmid);
        if (km != null) {
            dao.delete(km);
            cacheService.evict(KMCacheRegion.KNOWLEDGE_MODULE, km.getKMId());
            knowledgePackageService.deletePackage(km);
            supportingDataService.deleteAll(km.getKMId());
            // will delete the cached ConceptService associated with this KM
            tellObservers(km);
        }
    }

    @Override
    public InputStream getKnowledgePackage(KMId kmId) {
        KnowledgeModule km = find(kmId);
        if (km != null) {
            return knowledgePackageService.getPackageInputStream(km);
        }
        return null;
    }

    @Override
    public void deleteKnowledgePackage(KMId kmId) {
        KnowledgeModule km = find(kmId);
        if (km != null) {
            knowledgePackageService.deletePackage(km);
        }
    }

    @Override
    public void persistKnowledgePackage(KMId kmId, InputStream knowledgePackage) {
        KnowledgeModule km = find(kmId);
        if (km != null) {
            knowledgePackageService.persistPackageInputStream(km, knowledgePackage);
        }
    }
    
    private void tellObservers(KnowledgeModule km) {
        setChanged();
        notifyObservers(km);
    }

    private Map<KMId, KnowledgeModule> buildPairs(List<KnowledgeModule> all) {
        Map<KMId, KnowledgeModule> cachables = new HashMap<>();
        for (KnowledgeModule km : all) {
            cachables.put(km.getKMId(), km);
        }
        return cachables;
    }

    private enum KMCacheRegion implements CacheRegion {
        KNOWLEDGE_MODULE(KnowledgeModule.class);

        private Class<?> type;

        KMCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

}
