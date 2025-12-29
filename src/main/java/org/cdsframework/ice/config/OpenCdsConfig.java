package org.cdsframework.ice.config;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.cdsframework.ice.service.configurations.ICEDecisionEngineDSSEvaluationAdapter;
import org.cdsframework.ice.service.configurations.ICESupportingDataLoaderPlugin;
import org.cdsframework.ice.service.configurations.IceExecutionEngineContext;
import org.cdsframework.ice.service.configurations.IceKnowledgeLoader;
import org.opencds.common.cache.CacheRegion;
import org.opencds.common.exceptions.EvaluationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.common.utilities.MiscUtility;
import org.opencds.config.api.ConfigData;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.config.api.KnowledgeRepositoryService;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.ConceptDeterminationMethodDao;
import org.opencds.config.api.dao.ExecutionEngineDao;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.KnowledgeModuleDao;
import org.opencds.config.api.dao.PluginPackageDao;
import org.opencds.config.api.dao.SemanticSignifierDao;
import org.opencds.config.api.dao.SupportingDataDao;
import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.StreamCacheElement;
import org.opencds.config.api.model.CDMId;
import org.opencds.config.api.model.ConceptDeterminationMethod;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.model.PPId;
import org.opencds.config.api.model.PluginPackage;
import org.opencds.config.api.model.SDId;
import org.opencds.config.api.model.SSId;
import org.opencds.config.api.model.SemanticSignifier;
import org.opencds.config.api.model.SupportingData;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgeModuleService;
import org.opencds.config.api.service.KnowledgePackageService;
import org.opencds.config.api.service.PluginPackageService;
import org.opencds.config.api.service.SemanticSignifierService;
import org.opencds.config.api.service.SupportingDataPackageService;
import org.opencds.config.api.service.SupportingDataService;
import org.opencds.config.api.strategy.AbstractConfigStrategy;
import org.opencds.config.api.strategy.ConfigCapability;
import org.opencds.config.api.strategy.ConfigStrategy;
import org.opencds.config.api.xml.JAXBContextService;
import org.opencds.config.mapper.util.RestConfigUtil;
import org.opencds.config.service.CacheServiceImpl;
import org.opencds.config.service.ConceptDeterminationMethodServiceImpl;
import org.opencds.config.service.ConceptServiceImpl;
import org.opencds.config.service.ExecutionEngineServiceImpl;
import org.opencds.config.service.KnowledgeModuleServiceImpl;
import org.opencds.config.service.PluginPackageServiceImpl;
import org.opencds.config.service.SemanticSignifierServiceImpl;
import org.opencds.config.service.SupportingDataPackageServiceImpl;
import org.opencds.config.service.SupportingDataServiceImpl;
import org.opencds.dss.evaluate.EvaluationSoapService;
import org.opencds.dss.evaluate.impl.DSSEvaluation;
import org.opencds.dss.evaluate.impl.RequestProcessorService;
import org.opencds.dss.evaluation.service.util.DSSCallableUtil;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.evaluation.service.util.CallableUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import jakarta.xml.ws.Endpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class OpenCdsConfig
{
    @Slf4j
    private static class ConceptDeterminationMethodPathDao implements ConceptDeterminationMethodDao
    {
        private final Map<CDMId, ConceptDeterminationMethod> cache = new HashMap<>();

        public ConceptDeterminationMethodPathDao(final Path path)
        {
            final RestConfigUtil restConfigUtil = new RestConfigUtil();

            log.debug("Finding CDM resources in path: {}", path);
            for (final Path input : findFiles(path, true))
            {
                log.debug("Loading resource: {}", input);
                final List<ConceptDeterminationMethod> cdms = restConfigUtil.unmarshalCdms(getResourceAsStream(input));
                if (cdms != null)
                {
                    for (final ConceptDeterminationMethod cdm : cdms)
                    {
                        log.debug("Caching ConceptDeterminationMethod with CDMID: {}", cdm.getCDMId());
                        cache.put(cdm.getCDMId(), cdm);
                    }
                }
                else
                {
                    log.debug(
                            "Loading resource as ConceptDeterminationMethod (resources was not a ConceptDeterminationMethods instance)");
                    final ConceptDeterminationMethod cdm = restConfigUtil.unmarshalCdm(getResourceAsStream(input));
                    cache.put(cdm.getCDMId(), cdm);
                }
            }
        }

        @Override
        public ConceptDeterminationMethod find(final CDMId cdmId)
        {
            return cache.get(cdmId);
        }

        @Override
        public List<ConceptDeterminationMethod> getAll()
        {
            return new ArrayList<>(cache.values());
        }

        @Override
        public void persist(final ConceptDeterminationMethod cdm)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through the dao API");
        }

        @Override
        public void persist(final List<ConceptDeterminationMethod> internal)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through the dao API");
        }

        @Override
        public void delete(final ConceptDeterminationMethod cdm)
        {
            throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
        }
    }

    @Slf4j
    private static class ExecutionEnginePathDao implements ExecutionEngineDao
    {
        private final Map<String, ExecutionEngine> cache = new HashMap<>();

        public ExecutionEnginePathDao(final Path path)
        {
            final RestConfigUtil restConfigUtil = new RestConfigUtil();

            log.debug("Loading resource");
            for (final ExecutionEngine ee : restConfigUtil.unmarshalExecutionEngines(getResourceAsStream(path)))
            {
                log.debug("Caching ExecutionEngine with identifier: {}", ee.getIdentifier());
                cache.put(ee.getIdentifier(), ee);
            }
        }

        @Override
        public ExecutionEngine find(final String identifier)
        {
            return cache.get(identifier);
        }

        @Override
        public List<ExecutionEngine> getAll()
        {
            return new ArrayList<>(cache.values());
        }

        @Override
        public void persist(final ExecutionEngine ee)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void persist(final List<ExecutionEngine> ees)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void delete(final ExecutionEngine ee)
        {
            throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
        }
    }

    @Slf4j
    private record PathDaoImpl(Path location) implements FileDao
    {
        @Override
        public CacheElement find(final String pk)
        {
            log.debug("Finding cache element at: {}", location);
            return StreamCacheElement.create(pk, getResourceAsStream(location));
        }

        @Override
        public void persist(final CacheElement e)
        {
            throw new UnsupportedOperationException("Operation not supported on this type of FileDao");
        }

        @Override
        public void delete(final CacheElement e)
        {
            throw new UnsupportedOperationException("Operation not supported on this type of FileDao");
        }
    }

    @Slf4j
    private static class PluginPackagePathDao implements PluginPackageDao
    {
        private final Map<PPId, PluginPackage> cache = new HashMap<>();

        public PluginPackagePathDao(final Path path)
        {
            final RestConfigUtil restConfigUtil = new RestConfigUtil();

            log.debug("Finding plugin resources in path: {}", path);

            for (final Path resource : findFiles(path, false))
            {
                log.debug("Loading resource: {}", resource);
                log.debug("Loading resource as PluginPackages (resource was not a PluginPackage instance)");
                final List<PluginPackage> ppkgs = restConfigUtil.unmarshalPluginPackages(getResourceAsStream(resource));
                if (ppkgs != null)
                {
                    for (final PluginPackage ppkg : ppkgs)
                        cache.put(ppkg.getIdentifier(), ppkg);
                }
            }
        }

        @Override
        public PluginPackage find(final PPId ppId)
        {
            return cache.get(ppId);
        }

        @Override
        public List<PluginPackage> getAll()
        {
            return new ArrayList<>(cache.values());
        }

        @Override
        public void persist(final PluginPackage pluginPackage)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void persist(final List<PluginPackage> pluginPackages)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void delete(final PluginPackage pluginPackage)
        {
            throw new UnsupportedOperationException("Cannot delete from file store through dao API");
        }
    }

    @Slf4j
    private static class SupportingDataPathDao implements SupportingDataDao
    {
        private record SDKey(KMId kmId,
                             String identifier) implements SDId
        {
            private record KMIdWrapper(KMId kmId) implements KMId
            {
                @Override
                public String getScopingEntityId()
                {
                    return kmId.getScopingEntityId();
                }

                @Override
                public String getBusinessId()
                {
                    return kmId().getBusinessId();
                }

                @Override
                public String getVersion()
                {
                    return kmId().getVersion();
                }

                @Override
                public int hashCode()
                {
                    return Objects.hash(getScopingEntityId(), getBusinessId(), getVersion());
                }

                @Override
                public boolean equals(final Object obj)
                {
                    if (this == obj)
                        return true;

                    if (!(obj instanceof final KMId other))
                        return false;

                    return Objects.equals(this.getScopingEntityId(), other.getScopingEntityId()) && Objects.equals(
                            this.getBusinessId(), other.getBusinessId()) && Objects.equals(this.getVersion(), other.getVersion());
                }
            }

            public SDKey
            {
                kmId = Optional.ofNullable(kmId).map(KMIdWrapper::new).orElse(null);
            }

            @Override
            public KMId getKMId()
            {
                return kmId;
            }

            @Override
            public String getIdentifier()
            {
                return identifier;
            }
        }

        private final Map<SDKey, SupportingData> cache = new HashMap<>();
        private final RestConfigUtil restConfigUtil = new RestConfigUtil();

        public SupportingDataPathDao(final Path path)
        {
            for (final Path resource : findFiles(path, false))
            {
                log.debug("Loading Resource: {}", resource);
                cacheSupportingDataList(() -> getResourceAsStream(resource));
            }
        }

        private void cacheSupportingDataList(final Supplier<InputStream> supplier)
        {
            log.debug("Loading resource as SupportingDataList (resource was not a SupportingData instance)");
            Optional.ofNullable(supplier.get())
                    .map(restConfigUtil::unmarshalSupportingDataList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(sd -> cache.put(new SDKey(sd.getKMId(), sd.getIdentifier()), sd));
        }

        @Override
        public SupportingData find(final String identifier)
        {
            return find(null, identifier);
        }

        @Override
        public SupportingData find(final KMId kmId, final String identifier)
        {
            return cache.get(new SDKey(kmId, identifier));
        }

        @Override
        public List<SupportingData> find(final KMId kmid)
        {
            return cache.values().stream().filter(sd -> sd.getKMId().equals(kmid)).toList();
        }

        @Override
        public List<SupportingData> getAll()
        {
            return new ArrayList<>(cache.values());
        }

        @Override
        public void persist(final SupportingData sd)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void delete(final SupportingData sd)
        {
            throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
        }
    }

    @Slf4j
    private static class KnowledgeModulePathDao implements KnowledgeModuleDao
    {
        private final Map<KMId, KnowledgeModule> cache = new HashMap<>();

        public KnowledgeModulePathDao(final Path path)
        {
            final RestConfigUtil restConfigUtil = new RestConfigUtil();

            log.debug("Loading resource: {}", path);

            for (final KnowledgeModule km : restConfigUtil.unmarshalKnowledgeModules(getResourceAsStream(path)))
            {
                log.debug("Caching KnowledgeModule with KMID: {}", km.getKMId());
                cache.put(km.getKMId(), km);
            }
        }

        @Override
        public KnowledgeModule find(final KMId kmId)
        {
            return cache.get(kmId);
        }

        @Override
        public List<KnowledgeModule> getAll()
        {
            return new ArrayList<>(cache.values());
        }

        @Override
        public void persist(final KnowledgeModule km)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void persist(final List<KnowledgeModule> internal)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void delete(final KnowledgeModule km)
        {
            throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
        }
    }

    @Slf4j
    private static class SemanticSignifierPathDao implements SemanticSignifierDao
    {
        private final Map<SSId, SemanticSignifier> cache = new HashMap<>();

        public SemanticSignifierPathDao(final Path resource)
        {
            final RestConfigUtil restConfigUtil = new RestConfigUtil();

            log.debug("Loading resource: {}", resource);

            for (final SemanticSignifier ss : restConfigUtil.unmarshalSemanticSignifiers(getResourceAsStream(resource)))
            {
                log.debug("Caching SemanticSignifier for SSID: {}", ss.getSSId());
                cache.put(ss.getSSId(), ss);
            }
        }

        @Override
        public SemanticSignifier find(final SSId ssId)
        {
            log.debug("Finding match for SSID: {}", ssId);
            return cache.get(ssId);
        }

        @Override
        public List<SemanticSignifier> getAll()
        {
            return new ArrayList<>(cache.values());
        }

        @Override
        public void persist(final SemanticSignifier ss)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void persist(final List<SemanticSignifier> sses)
        {
            throw new UnsupportedOperationException("Cannot persist to file store through dao API");
        }

        @Override
        public void delete(final SemanticSignifier ss)
        {
            throw new UnsupportedOperationException("Cannot delete from file store through the dao API");
        }
    }

    @Slf4j
    private record ExecutorKnowledgePackageServiceImpl(ExecutorService pool,
                                                       ExecutionEngineService executionEngineService,
                                                       CacheService cacheService) implements KnowledgePackageService
    {
        private static final CacheRegion<KMId, Object> KNOWLEDGE_PACKAGE = CacheRegion.create(KMId.class, Object.class);

        @Override
        public void deletePackage(final KnowledgeModule knowledgeModule)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public <KP> KP getKnowledgePackage(final KnowledgeModule knowledgeModule)
        {
            return Optional.ofNullable(this.<KP>getPackage(knowledgeModule)).orElseGet(() ->
            {
                final Future<?> task = submitLoadTask(knowledgeModule);
                log.debug("Waiting for a KM Package to be loaded...");
                try
                {
                    task.get();
                }
                catch (final Exception e)
                {
                    throw new OpenCDSRuntimeException(
                            e instanceof final ExecutionException executionException ? executionException.getCause() : e);
                }
                log.debug("Loaded KM package");
                return getPackage(knowledgeModule);
            });
        }

        @Override
        public InputStream getPackageInputStream(final KnowledgeModule knowledgeModule)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void persistPackageInputStream(final KnowledgeModule knowledgeModule, final InputStream packageInputStream)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void preloadKnowledgePackages(final List<KnowledgeModule> knowledgeModules)
        {
            final AtomicInteger counter = new AtomicInteger();
            knowledgeModules.parallelStream()
                    .filter(KnowledgeModule::isPreload)
                    .peek(km -> log.debug("Preloading KnowledgePackage: {}", km.getKMId()))
                    .map(this::submitLoadTask)
                    .toList()
                    .forEach(task ->
                    {
                        try
                        {
                            task.get();
                            log.debug("Task : {}", counter.incrementAndGet());
                        }
                        catch (final Exception e)
                        {
                            log.error("Task encountered exception: {}", e.getMessage(), e);
                        }
                    });
            log.debug("Tasks finished.");
        }

        private <I, O> KnowledgeLoader<I, O> getKnowledgeLoader(final KnowledgeModule km)
        {
            final ExecutionEngine ee = executionEngineService.find(km.getExecutionEngine());

            KnowledgeLoader<I, O> kLoader = executionEngineService.getKnowledgeLoader(ee);
            if (kLoader == null)
                kLoader = executionEngineService.getExecutionEngineInstance(ee);

            if (kLoader == null)
                throw new OpenCDSRuntimeException("Unable to load data; KnowledgeLoader is null");

            return kLoader;
        }

        private void evictPackage(final KnowledgeModule knowledgeModule)
        {
            cacheService.evict(KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
        }

        private <KP> KP getPackage(final KnowledgeModule knowledgeModule)
        {
            //noinspection unchecked
            return (KP) cacheService.get(KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
        }

        private <T> void putPackage(final KnowledgeModule knowledgeModule, final T knowledgePackage)
        {
            cacheService.put(KNOWLEDGE_PACKAGE, knowledgeModule.getKMId(), knowledgePackage);
        }

        private Future<?> submitLoadTask(final KnowledgeModule knowledgeModule)
        {
            return pool.submit(loader(knowledgeModule));
        }

        private <O> Runnable loader(final KnowledgeModule knowledgeModule)
        {
            final KnowledgeLoader<InputStream, O> loader = getKnowledgeLoader(knowledgeModule);
            return () -> putPackage(knowledgeModule,
                    processPackageInput(knowledgeModule, this::getPackageInputStream, loader::loadKnowledgePackage));

        }

        private <I extends Closeable, O> O processPackageInput(final KnowledgeModule knowledgeModule,
                final Function<KnowledgeModule, I> inputFunction,
                final BiFunction<KnowledgeModule, Function<KnowledgeModule, I>, O> process)
        {
            log.debug("Getting package input: {}", knowledgeModule.getPackageId());
            return process.apply(knowledgeModule, inputFunction);
        }
    }

    @Slf4j
    private record ExecutorEvaluationServiceImpl(ExecutorService evalPool,
                                                 CallableUtil callableUtil) implements EvaluationService
    {
        public ExecutorEvaluationServiceImpl(final CallableUtil callableUtil)
        {
            this(Executors.newFixedThreadPool(128), callableUtil);
        }

        @PreDestroy
        public void preDestroy()
        {
            if (evalPool != null && !evalPool.isShutdown() && !evalPool.isTerminated())
                evalPool.shutdownNow();
        }

        @Override
        public List<EvaluationResponseKMItem> evaluate(final KnowledgeRepository knowledgeRepository,
                final List<EvaluationRequestKMItem> evaluationRequestKMItems)
        {
            final ExecutionEngineService executionEngineService = knowledgeRepository.getExecutionEngineService();

            final List<Future<EvaluationResponseKMItem>> tasks = evaluationRequestKMItems.stream().map(evaluationRequestKMItem ->
            {
                final Callable<EvaluationResponseKMItem> callable = getCallable(knowledgeRepository, executionEngineService,
                        executionEngineService.find(knowledgeRepository.getKnowledgeModuleService()
                                .find(evaluationRequestKMItem.getRequestedKmId())
                                .getExecutionEngine()), evaluationRequestKMItem);

                log.debug("Starting evaluation of KM: {}", evaluationRequestKMItem.getRequestedKmId());
                return evalPool.submit(callable);
            }).toList();

            final List<EvaluationResponseKMItem> responseItems = new ArrayList<>();
            boolean failing = false;
            Throwable t = null;

            for (final Future<EvaluationResponseKMItem> task : tasks)
            {
                if (!failing)
                {
                    log.debug("Joining on task : {}", task.toString());

                    try
                    {
                        responseItems.add(task.get());
                    }
                    catch (final Exception e)
                    {
                        t = e instanceof ExecutionException ? e.getCause() : e;
                        failing = true;
                    }
                }
                else
                    task.cancel(true);
            }

            if (t != null)
                throw new EvaluationException(t.getMessage(), t);

            return responseItems;
        }

        @Override
        public EvaluationResponseKMItem evaluate(final KnowledgeRepository knowledgeRepository,
                final EvaluationRequestKMItem evaluationRequestKMItem)
        {
            final KnowledgeModule knowledgeModule =
                    knowledgeRepository.getKnowledgeModuleService().find(evaluationRequestKMItem.getRequestedKmId());
            if (knowledgeModule == null)
                throw new OpenCDSRuntimeException("Unable to find requested KM: " + evaluationRequestKMItem.getRequestedKmId());

            final ExecutionEngineService executionEngineService = knowledgeRepository.getExecutionEngineService();
            final ExecutionEngine engine = executionEngineService.find(knowledgeModule.getExecutionEngine());
            final Callable<EvaluationResponseKMItem> callable =
                    getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);
            final EvaluationResponseKMItem evaluationResponseKMItem;
            try
            {
                evaluationResponseKMItem = callable.call();
            }
            catch (final Exception e)
            {
                log.error("==========================================================", e);
                throw new OpenCDSRuntimeException(e);
            }

            return evaluationResponseKMItem;
        }

        @Deprecated
        private Callable<EvaluationResponseKMItem> getCallable(final KnowledgeRepository knowledgeRepository,
                final ExecutionEngineService executionEngineService, final ExecutionEngine engine,
                final EvaluationRequestKMItem evaluationRequestKMItem)
        {
            return callableUtil.getCallable(knowledgeRepository, executionEngineService, engine, evaluationRequestKMItem);
        }
    }

    private static class SpringBootConfigStrategy extends AbstractConfigStrategy
    {
        private static final String configType = "SPRING_BOOT";

        private final ExecutorService evalPool = Executors.newCachedThreadPool();

        public SpringBootConfigStrategy()
        {
            super(Set.of(ConfigCapability.READ_ONCE), configType);
        }

        @PreDestroy
        public void preDestroy()
        {
            if (!evalPool.isShutdown() && !evalPool.isTerminated())
                evalPool.shutdownNow();
        }

        @Override
        public KnowledgeRepository getKnowledgeRepository(final ConfigData configData, final CacheService cacheService)
        {
            final Path path = Path.of(URI.create(configData.configPath()));

            final ConceptDeterminationMethodServiceImpl cdmService = new ConceptDeterminationMethodServiceImpl(
                    new ConceptDeterminationMethodPathDao(path.resolve("conceptDeterminationMethods")), cacheService);

            final ExecutionEngineService eeService =
                    new ExecutionEngineServiceImpl(new ExecutionEnginePathDao(path.resolve("executionEngines.xml")), cacheService);

            final PluginPackageService ppService = new PluginPackageServiceImpl(new PluginPackagePathDao(path.resolve("plugins")),
                    new PathDaoImpl(path.resolve("plugins").resolve("packages")), cacheService);

            final SupportingDataPackageService sdpService =
                    new SupportingDataPackageServiceImpl(new PathDaoImpl(path.resolve("supportingData").resolve("packages")),
                            cacheService);

            final SupportingDataService sdService =
                    new SupportingDataServiceImpl(new SupportingDataPathDao(path.resolve("supportingData")), sdpService,
                            cacheService);

            final KnowledgePackageService kpService = new ExecutorKnowledgePackageServiceImpl(evalPool, eeService, cacheService);

            final KnowledgeModuleService kmService =
                    new KnowledgeModuleServiceImpl(new KnowledgeModulePathDao(path.resolve("knowledgeModules.xml")), kpService,
                            sdService, cacheService);

            final SemanticSignifierService ssService =
                    new SemanticSignifierServiceImpl(new SemanticSignifierPathDao(path.resolve("semanticSignifiers.xml")),
                            cacheService);

            return new KnowledgeRepositoryService(cdmService, new ConceptServiceImpl(cdmService, kmService, cacheService),
                    eeService, kmService, kpService, ppService, ssService, sdService, sdpService, null, cacheService);
        }
    }

    private static List<Path> findFiles(final Path path, final boolean traverse)
    {
        try (final Stream<Path> stream = Files.find(path, traverse ? Integer.MAX_VALUE : 1, (p, a) -> a.isRegularFile()))
        {
            return stream.toList();
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getResourceAsStream(final Path path)
    {
        try
        {
            return Files.newInputStream(path);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public DSSCallableUtil dssCallableUtil()
    {
        return new DSSCallableUtil();
    }

    @Bean
    public EvaluationService evaluationService(final DSSCallableUtil dssCallableUtil)
    {
        return new ExecutorEvaluationServiceImpl(dssCallableUtil);
    }

    @Bean
    public DSSEvaluation dssEvaluation(final EvaluationService evaluationService, final ConfigurationService configurationService)
    {
        return new DSSEvaluation(evaluationService, configurationService, new RequestProcessorService());
    }

    @Bean
    public EvaluationSoapService evaluationSoapService(final DSSEvaluation dssEvaluation)
    {
        return new EvaluationSoapService(dssEvaluation);
    }

    @Bean
    public Endpoint endpoint(final SpringBus springBus, final EvaluationSoapService evaluationSoapService)
    {
        final EndpointImpl endpoint = new EndpointImpl(springBus, evaluationSoapService);
        endpoint.setServiceName(new QName("http://www.omg.org/spec/CDSS/201105/dssWsdl", "DecisionSupportService"));
        endpoint.setEndpointName(new QName("http://www.omg.org/spec/CDSS/201105/dssWsdl", "evaluate"));
        endpoint.setWsdlLocation("wsdl/dss.wsdl");
        endpoint.publish("/");
        return endpoint;
    }

    @Bean
    public ConfigData configData(final Path configPath)
    {
        return ConfigData.create(SpringBootConfigStrategy.configType, configPath.toUri().toString());
    }

    @Bean
    public ConfigStrategy configStrategy()
    {
        return new SpringBootConfigStrategy();
    }

    @Bean
    public ConfigurationService configurationService(final IceProperties iceProperties, final Path configPath,
            final Path droolsPath, final VersionData versionData, final ConfigData configData, final ConfigStrategy configStrategy)
    {
        log.info("Setting fire limit to {}", iceProperties.getFireLimit());
        System.setProperty("org.jbpm.rule.task.firelimit", Integer.toString(iceProperties.getFireLimit()));

        ICEDecisionEngineDSSEvaluationAdapter.setIceProperties(iceProperties);

        IceKnowledgeLoader.setIceProperties(iceProperties);
        IceKnowledgeLoader.setDroolsPath(droolsPath);

        ICESupportingDataLoaderPlugin.setIceProperties(iceProperties);
        ICESupportingDataLoaderPlugin.setConfigPath(configPath);

        IceExecutionEngineContext.setIceVersion(versionData.iceVersion());

        return new ConfigurationService(Set.of(configStrategy), CacheServiceImpl.class, configData);
    }

    @Bean
    public MiscUtility miscUtility()
    {
        return new MiscUtility();
    }

    @Bean
    public JAXBContextService jaxbContextService()
    {
        return JAXBContextService.get();
    }
}
