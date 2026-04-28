package org.opencds.config.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.opencds.common.cache.CacheRegion;
import org.opencds.common.interfaces.ResultSetBuilder;
import org.opencds.common.utilities.ClassUtil;
import org.opencds.config.api.FactListsBuilder;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.ss.EntryPoint;
import org.opencds.config.api.ss.ExitPoint;

public class SemanticSignifierServiceImpl implements SemanticSignifierService
{
    private static final CacheRegion<SSId, SemanticSignifier> SEMANTIC_SIGNIFIER =
            CacheRegion.create(SSId.class, SemanticSignifier.class);
    private static final CacheRegion<SSId, EntryPoint> ENTRY_POINT = CacheRegion.create(SSId.class, EntryPoint.class);
    private static final CacheRegion<SSId, ExitPoint> EXIT_POINT = CacheRegion.create(SSId.class, ExitPoint.class);
    private static final CacheRegion<SSId, FactListsBuilder> FACT_LISTS_BUILDER =
            CacheRegion.create(SSId.class, FactListsBuilder.class);
    private static final CacheRegion<SSId, ResultSetBuilder> RESULT_SET_BUILDER =
            CacheRegion.create(SSId.class, ResultSetBuilder.class);

    private final SemanticSignifierDao dao;
    private final CacheService cacheService;

    public SemanticSignifierServiceImpl(final SemanticSignifierDao dao, final CacheService cacheService)
    {
        this.dao = dao;
        this.cacheService = cacheService;
        init(dao.getAll());
    }

    private void init(final List<SemanticSignifier> allSS)
    {
        cacheService.putAll(SEMANTIC_SIGNIFIER, buildPairs(allSS));
        cacheService.putAll(ENTRY_POINT, buildEntryPointPairs(allSS));
        cacheService.putAll(EXIT_POINT, buildExitPointPairs(allSS));
        cacheService.putAll(FACT_LISTS_BUILDER, buildFactListsBuilderPairs(allSS));
        cacheService.putAll(RESULT_SET_BUILDER, buildResultSetBuilderPairs(allSS));
    }

    @Override
    public SemanticSignifier find(final SSId ssId)
    {
        return cacheService.get(SEMANTIC_SIGNIFIER, ssId);
    }

    @Override
    public List<SemanticSignifier> getAll()
    {
        return List.copyOf(cacheService.getAllValues(SEMANTIC_SIGNIFIER));
    }

    @Override
    public void persist(final SemanticSignifier ss)
    {
        dao.persist(ss);
        cacheService.put(SEMANTIC_SIGNIFIER, ss.getSSId(), ss);
        init(this.dao.getAll());
    }

    @Override
    public void persist(final List<SemanticSignifier> sses)
    {
        dao.persist(sses);
        cacheService.putAll(SEMANTIC_SIGNIFIER, buildPairs(sses));
        init(this.dao.getAll());
    }

    @Override
    public void delete(final SSId ssId)
    {
        final SemanticSignifier ss = find(ssId);
        if (ss != null)
        {
            dao.delete(ss);
            cacheService.evict(SEMANTIC_SIGNIFIER, ss.getSSId());
            cacheService.evict(FACT_LISTS_BUILDER, ss.getSSId());
            cacheService.evict(RESULT_SET_BUILDER, ss.getSSId());
        }
    }

    @Override
    public <T, EP extends EntryPoint<T>> EP getEntryPoint(final SSId ssId)
    {
        return (EP) cacheService.get(ENTRY_POINT, ssId);
    }

    @Override
    public ExitPoint getExitPoint(final SSId ssId)
    {
        return cacheService.get(EXIT_POINT, ssId);
    }

    @Override
    public FactListsBuilder getFactListsBuilder(final SSId ssId)
    {
        return cacheService.get(FACT_LISTS_BUILDER, ssId);
    }

    @Override
    public <T, RSB extends ResultSetBuilder<T>> RSB getResultSetBuilder(final SSId ssId)
    {
        return (RSB) cacheService.get(RESULT_SET_BUILDER, ssId);
    }

    private Map<SSId, SemanticSignifier> buildPairs(final List<SemanticSignifier> all)
    {
        return all.stream()
                .map(ss -> Map.entry(ss.getSSId(), ss))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <MDL, EP extends EntryPoint<MDL>> Map<SSId, EP> buildEntryPointPairs(final List<SemanticSignifier> allSS)
    {
        return allSS.stream()
                .map(ss -> Map.entry(ss.getSSId(), ClassUtil.<EP>newInstance(ss.getEntryPoint())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<SSId, ExitPoint> buildExitPointPairs(final List<SemanticSignifier> allSS)
    {
        return allSS.stream()
                .map(ss -> Map.entry(ss.getSSId(), ClassUtil.<ExitPoint>newInstance(ss.getExitPoint())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<SSId, FactListsBuilder> buildFactListsBuilderPairs(final List<SemanticSignifier> all)
    {
        return all.stream()
                .map(ss -> Map.entry(ss.getSSId(), ClassUtil.<FactListsBuilder>newInstance(ss.getFactListsBuilder())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private <T, RSB extends ResultSetBuilder<T>> Map<SSId, RSB> buildResultSetBuilderPairs(final List<SemanticSignifier> all)
    {
        return all.stream()
                .map(ss -> Map.entry(ss.getSSId(), ClassUtil.<RSB>newInstance(ss.getResultSetBuilder())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
