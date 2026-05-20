package org.opencds.config.api.service;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.springframework.core.task.AsyncTaskExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record KnowledgePackageService(AsyncTaskExecutor pool,
                                      ExecutionEngineService executionEngineService)
{
    private static final Map<KMId, Object> knowledgePackageMap = new ConcurrentHashMap<>();

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

    public InputStream getPackageInputStream(final KnowledgeModule knowledgeModule)
    {
        throw new UnsupportedOperationException();
    }

    public void preloadKnowledgePackages(final List<KnowledgeModule> knowledgeModules)
    {
        final AtomicInteger counter = new AtomicInteger();
        knowledgeModules.parallelStream()
                .filter(KnowledgeModule::preload)
                .peek(km -> log.debug("Preloading KnowledgePackage: {}", km.kmId()))
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
        final KnowledgeLoader<I, O> kLoader =
                executionEngineService.getKnowledgeLoader(executionEngineService.find(km.executionEngine()));
        if (kLoader == null)
            throw new OpenCDSRuntimeException("Unable to load data; KnowledgeLoader is null");

        return kLoader;
    }

    @SuppressWarnings("unchecked")
    private <T> T getPackage(final KnowledgeModule knowledgeModule)
    {
        return (T) knowledgePackageMap.get(knowledgeModule.kmId());
    }

    private <T> void putPackage(final KnowledgeModule knowledgeModule, final T knowledgePackage)
    {
        knowledgePackageMap.put(knowledgeModule.kmId(), knowledgePackage);
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
        log.debug("Getting package input: {}", knowledgeModule.packageId());
        return process.apply(knowledgeModule, inputFunction);
    }
}

