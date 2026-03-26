package org.opencds.config.service;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.opencds.common.cache.CacheRegion;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.impl.KMIdImpl;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.config.api.util.EntityIdentifierUtil;

public class KnowledgeModuleServiceImpl implements KnowledgeModuleService
{
    private static final CacheRegion<KMId, KnowledgeModule> KNOWLEDGE_MODULE =
            CacheRegion.create(KMId.class, KnowledgeModule.class);

    private final KnowledgeModuleDao dao;
    private final KnowledgePackageService knowledgePackageService;
    private final SupportingDataService supportingDataService;
    private final CacheService cacheService;

    public KnowledgeModuleServiceImpl(final KnowledgeModuleDao dao, final KnowledgePackageService knowledgePackageService,
            final SupportingDataService supportingDataService, final CacheService cacheService)
    {
        this.dao = dao;
        this.knowledgePackageService = knowledgePackageService;
        this.supportingDataService = supportingDataService;
        this.cacheService = cacheService;
        cacheService.putAll(KNOWLEDGE_MODULE, buildPairs(this.dao.getAll()));
    }

    @Override
    public KnowledgeModule find(final KMId kmId)
    {
        return cacheService.get(KNOWLEDGE_MODULE, kmId);
    }

    @Override
    public KnowledgeModule find(final String stringKmId)
    {
        return find(KMIdImpl.create(EntityIdentifierUtil.makeEI(stringKmId)));
    }

    @Override
    public KnowledgeModule find(final Predicate<? super KnowledgeModule> predicate)
    {
        return cacheService.getAllValues(KNOWLEDGE_MODULE).stream().filter(predicate).findFirst().orElse(null);
    }

    @Override
    public List<KnowledgeModule> getAll(final Predicate<? super KnowledgeModule> predicate)
    {
        return cacheService.getAllValues(KNOWLEDGE_MODULE).stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeModule> getAll()
    {
        return List.copyOf(cacheService.getAllValues(KNOWLEDGE_MODULE));
    }

    @Override
    public void persist(final KnowledgeModule km)
    {
        dao.persist(km);
        cacheService.put(KNOWLEDGE_MODULE, km.getKMId(), km);
    }

    @Override
    public void persist(final List<KnowledgeModule> internal)
    {
        internal.forEach(this::persist);
    }

    @Override
    public void delete(final KMId kmid)
    {
        final KnowledgeModule km = find(kmid);
        if (km != null)
        {
            dao.delete(km);
            cacheService.evict(KNOWLEDGE_MODULE, km.getKMId());
            knowledgePackageService.deletePackage(km);
            supportingDataService.deleteAll(km.getKMId());
        }
    }

    private Map<KMId, KnowledgeModule> buildPairs(final List<KnowledgeModule> all)
    {
        return all.stream()
                .map(km -> Map.entry(km.getKMId(), km))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
