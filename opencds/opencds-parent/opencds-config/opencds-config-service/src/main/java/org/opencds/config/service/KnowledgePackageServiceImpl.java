/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.exceptions.OpenCDSConfigurationException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.StreamCacheElement;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgePackageService;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public class KnowledgePackageServiceImpl implements KnowledgePackageService {
    private static final Log log = LogFactory.getLog(KnowledgePackageServiceImpl.class);
    private final ForkJoinPool pool;

    private final ExecutionEngineService executionEngineService;

    private final FileDao fileDao;

    private final CacheService cacheService;

    public KnowledgePackageServiceImpl(ExecutionEngineService executionEngineService,
                                       FileDao fileDao,
                                       CacheService cacheService) {
        this.executionEngineService = executionEngineService;
        this.fileDao = fileDao;
        this.cacheService = cacheService;
        System.setProperty(
                "java.util.concurrent.ForkJoinPool.common.exceptionHandler",
                KPExceptionHandler.class.getCanonicalName());
        pool = new ForkJoinPool();
    }

    private static class KPExceptionHandler implements UncaughtExceptionHandler {
        private static final Log log = LogFactory.getLog(KPExceptionHandler.class);

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Configuration specified loading of KnowledgeModule but KM was not found.", e);
        }
    }

    @Override
    public void deletePackage(KnowledgeModule knowledgeModule) {
        Optional.ofNullable(knowledgeModule.getPackageId())
                .map(fileDao::find)
                .ifPresentOrElse(
                        cacheElement -> {
                            fileDao.delete(cacheElement);
                            evictPackage(knowledgeModule);
                        },
                        () -> log.warn("Attempted to delete non-existent package for KM: " +
                                knowledgeModule.getKMId()));
    }

    @Override
    public <KP> KP getKnowledgePackage(KnowledgeModule knowledgeModule) {
        return Optional.ofNullable(this.<KP>getPackage(knowledgeModule))
                .orElseGet(() -> {
                    ForkJoinTask<?> task = submitLoadTask(knowledgeModule);
                    log.debug("Waiting for a KM Package to be loaded...");
                    task.quietlyJoin();
                    if (task.getException() != null) {
                        throw new OpenCDSRuntimeException(task.getException().getCause());
                    }
                    log.debug("Loaded KM package");
                    return getPackage(knowledgeModule);
                });
    }

    @Override
    public InputStream getPackageInputStream(KnowledgeModule knowledgeModule) {
        try {
            log.debug("Getting InputStream for package: " + knowledgeModule.getPackageId());
            CacheElement cacheElement = fileDao.find(knowledgeModule.getPackageId());
            log.debug("CacheElement: " + cacheElement);
            if (cacheElement != null) {
                return cacheElement.getInputStream();
            }
            throw new OpenCDSConfigurationException("KnowledgePackage not found");
        } catch (IOException e) {
            String message = "Cannot resolve package: " + knowledgeModule.getPackageId();
            log.error(message, e);
            throw new OpenCDSConfigurationException(message);
        } catch (Exception e) {
            String message = "Exception when attempting to load knowledge package: " + knowledgeModule.getPackageId();
            log.error(message, e);
            throw new OpenCDSConfigurationException(message);
        }
    }

    @Override
    public void persistPackageInputStream(KnowledgeModule knowledgeModule, InputStream packageInputStream) {
        String packageId = Optional.ofNullable(knowledgeModule.getPackageId())
                .orElseGet(() -> UUID.randomUUID().toString());
        fileDao.persist(StreamCacheElement.create(packageId, packageInputStream));
        evictPackage(knowledgeModule);
        if (knowledgeModule.isPreload()) {
            submitLoadTask(knowledgeModule);
        }
    }

    @Override
    public void preloadKnowledgePackages(List<KnowledgeModule> knowledgeModules) {
        AtomicInteger counter = new AtomicInteger();
        knowledgeModules.parallelStream()
                .filter(KnowledgeModule::isPreload)
                .peek(km -> log.info("Preloading KnowledgePackage: " + km.getKMId()))
                .map(this::submitLoadTask)
                .toList()
                .stream()
                .peek(ForkJoinTask::quietlyJoin)
                .forEach(task -> {
                    Throwable t = task.getException();
                    if (t == null) {
                        log.debug("Task : " + counter.incrementAndGet());
                    } else {
                        log.error("Task encountered exception: " + t.getMessage(), t);
                    }
                });
        log.debug("Tasks finished.");
    }

    private <I, O> KnowledgeLoader<I, O> getKnowledgeLoader(KnowledgeModule km) {
        ExecutionEngine ee = executionEngineService.find(km.getExecutionEngine());
        KnowledgeLoader<I, O> kLoader = executionEngineService.getKnowledgeLoader(ee);
        if (kLoader == null) {
            kLoader = executionEngineService.getExecutionEngineInstance(ee);
        }
        if (kLoader == null) {
            throw new OpenCDSRuntimeException("Unable to load data; KnowledgeLoader is null");
        }
        return kLoader;
    }

    private void evictPackage(KnowledgeModule knowledgeModule) {
        cacheService.evict(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
    }

    private <KP> KP getPackage(KnowledgeModule knowledgeModule) {
        return cacheService.get(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
    }

    private <T> void putPackage(KnowledgeModule knowledgeModule, T knowledgePackage) {
        cacheService.put(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId(), knowledgePackage);
    }

    private ForkJoinTask<?> submitLoadTask(KnowledgeModule knowledgeModule) {
        return pool.submit(loader(knowledgeModule));
    }

    private <O> Runnable loader(KnowledgeModule knowledgeModule) {
        KnowledgeLoader<InputStream, O> loader = getKnowledgeLoader(knowledgeModule);
        return () -> putPackage(
                knowledgeModule,
                processPackageInput(
                        knowledgeModule,
                        this::getPackageInputStream,
                        loader::loadKnowledgePackage));

    }

    private <I extends Closeable, O> O processPackageInput(
            KnowledgeModule knowledgeModule,
            Function<KnowledgeModule, I> inputFunction,
            BiFunction<KnowledgeModule, Function<KnowledgeModule, I>, O> process) {
        log.debug("Getting package input: " + knowledgeModule.getPackageId());
        return process.apply(knowledgeModule, inputFunction);
    }

    private enum KPCacheRegion implements CacheRegion {
        KNOWLEDGE_PACKAGE(Object.class);

        private final Class<?> type;

        KPCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }

    }

}
