package org.opencds.config.service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.cache.OpencdsCache.CacheRegion;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.ConfigData;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.api.dao.FileDao;
import org.opencds.config.api.dao.file.CacheElement;
import org.opencds.config.api.dao.file.StreamCacheElement;
import org.opencds.config.api.model.ExecutionEngine;
import org.opencds.config.api.model.KnowledgeModule;
import org.opencds.config.api.service.ExecutionEngineService;
import org.opencds.config.api.service.KnowledgePackageService;

public class KnowledgePackageServiceImpl implements KnowledgePackageService {
    private static final Log log = LogFactory.getLog(KnowledgePackageServiceImpl.class);
    private static final int KP_LOAD_SCALE_FACTOR = 8; // magic number
    private ForkJoinPool pool;

    private final ConfigData configData;

    private final ExecutionEngineService executionEngineService;

    private final FileDao fileDao;

    private CacheService cacheService;

    public KnowledgePackageServiceImpl(ConfigData configData, ExecutionEngineService executionEngineService,
            FileDao fileDao, CacheService cacheService) {
        this.configData = configData;
        this.executionEngineService = executionEngineService;
        this.fileDao = fileDao;
        this.cacheService = cacheService;
        pool = new ForkJoinPool(configData.getKmThreads() * KP_LOAD_SCALE_FACTOR,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory, new KPExceptionHandler(), false);
    }

    private static class KPExceptionHandler implements UncaughtExceptionHandler {
        private static final Log log = LogFactory.getLog(KPExceptionHandler.class);

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Configuration specified loading of KnowledgeModule but KM was not found.", e);
        }
    }

    @Override
    public InputStream getPackageInputStream(KnowledgeModule knowledgeModule) {
        log.debug("Getting inputstream for package: " + knowledgeModule.getPackageId());
        CacheElement cacheElement = fileDao.find(knowledgeModule.getPackageId());
        log.debug("CacheElement: " + cacheElement);
        if (cacheElement == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = cacheElement.getInputStream();
        } catch (IOException e) {
            log.error("Cannot resolve package: " + knowledgeModule.getPackageId(), e);
        }
        return is;
    }

    /**
     * Evicts any cached package after the persist succeeds.
     */
    @Override
    public void persistPackageInputStream(KnowledgeModule knowledgeModule, InputStream packageInputStream) {
        String packageId = knowledgeModule.getPackageId();
        if (packageId == null) {
            packageId = UUID.randomUUID().toString(); // TODO: Is this what I
                                                      // want?
        }
        CacheElement cacheElement = StreamCacheElement.create(packageId, packageInputStream);
        fileDao.persist(cacheElement);
        evictPackagesFromQueue(knowledgeModule);
        if (knowledgeModule.isPreload()) {
            loadKnowledgePackage(knowledgeModule);
        }
    }

    private void evictPackagesFromQueue(KnowledgeModule knowledgeModule) {
        BlockingQueue<KnowledgeModule> q = cacheService.get(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
        if (q != null) {
            q.clear();
            log.debug("Cleared KP Queue for KM: " + knowledgeModule.getKMId());
        }
    }

    @Override
    public void deletePackage(KnowledgeModule knowledgeModule) {
        String packageId = knowledgeModule.getPackageId();
        // TODO: What should happen if it's null?
        if (packageId != null) {
            CacheElement cacheElement = fileDao.find(packageId);
            fileDao.delete(cacheElement);
            evictPackagesFromQueue(knowledgeModule);
        }
    }

    @Override
    public <T> T getPackage(KnowledgeModule knowledgeModule) {
        BlockingQueue<T> q = cacheService.get(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
        if (q != null) {
            return q.peek();
        }
        return null;
    }

    public <T> void putPackage(KnowledgeModule knowledgeModule, T knowledgePackage) {
        BlockingQueue<T> q = cacheService.get(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
        if (q == null) {
            q = new LinkedBlockingQueue<T>();
        }
        q.add(knowledgePackage);
        cacheService.put(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId(), q);
    }

    public <T> T takePackage(KnowledgeModule knowledgeModule) {
        BlockingQueue<T> q = cacheService.get(KPCacheRegion.KNOWLEDGE_PACKAGE, knowledgeModule.getKMId());
        if (q != null) {
            try {
                return q.take();
            } catch (InterruptedException e) {
                log.error("InterruptedException when taking a KnowledgePackage...", e);
            }
        }
        return null;
    }

    @Override
    public <KP> KP borrowKnowledgePackage(KnowledgeModule knowledgeModule) {
        log.debug("Taking package from queue...");
        KP kp = takePackage(knowledgeModule);
        if (kp == null) {
            List<ForkJoinTask<?>> tasks = loadKnowledgePackage(knowledgeModule);
            log.debug("Waiting for a KM Package to be loaded...");
            ForkJoinTask<?> task = tasks.get(0);
            task.quietlyJoin();
            if (task.getException() != null) {
                throw new OpenCDSRuntimeException(task.getException().getCause());
            }
            log.debug("Loaded...  Taking package");
            kp = takePackage(knowledgeModule);
        }
        return kp;
    }

    @Override
    public <KP> void returnKnowledgePackage(KnowledgeModule knowledgeModule, KP knowledgePackage) {
        putPackage(knowledgeModule, knowledgePackage);
    }

    private List<ForkJoinTask<?>> loadKnowledgePackage(KnowledgeModule knowledgeModule) {
        List<ForkJoinTask<?>> taskList = new LinkedList<>();
        for (int i = 0; i < configData.getKmThreads(); i++) {
            taskList.add(pool.submit(new KPLoader(getKnowledgeLoader(knowledgeModule), this, knowledgeModule)));
        }
        return taskList;
    }

    @Override
    public void preloadKnowledgePackages(List<KnowledgeModule> knowledgeModules) {
        List<ForkJoinTask<?>> taskList = new LinkedList<>();
        for (KnowledgeModule km : knowledgeModules) {
            if (km.isPreload()) {
                try {
                    log.info("Preloading KnowledgePackage: " + km.getKMId());
                    taskList.addAll(loadKnowledgePackage(km));
                } catch (Exception e) {
                    log.error("Unable to load KnowledgePackage: " + km.getKMId(), e);
                }
            }
        }
        log.debug("Joining (" + taskList.size() + ") KnowledgePackage preloader tasks...");
        int counter = 0;
        for (ForkJoinTask<?> task : taskList) {
            task.quietlyJoin();
            Throwable t = task.getException();
            if (t == null) {
                counter++;
                log.debug("Task : " + counter);
            } else {
                log.error("Task encountered exception: " + t.getMessage(), t);
            }
        }
        log.debug("Tasks finished.");
    }

    private KnowledgeLoader<?> getKnowledgeLoader(KnowledgeModule km) {
        ExecutionEngine ee = executionEngineService.find(km.getExecutionEngine());
        return executionEngineService.getExecutionEngineInstance(ee);
    }

    private static class KPLoader implements Runnable {

        private final KnowledgeLoader<?> loader;
        private final KnowledgeModule knowledgeModule;
        private final KnowledgePackageService knowledgePackageService;

        public KPLoader(KnowledgeLoader<?> loader, KnowledgePackageService knowledgePackageService,
                KnowledgeModule knowledgeModule) {
            this.loader = loader;
            this.knowledgePackageService = knowledgePackageService;
            this.knowledgeModule = knowledgeModule;
        }

        @Override
        public void run() {
            loader.loadKnowledgePackage(knowledgePackageService, knowledgeModule);
        }

    }

    private enum KPCacheRegion implements CacheRegion {
        KNOWLEDGE_PACKAGE(Queue.class);

        private Class<?> type;

        KPCacheRegion(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean supports(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }

    }

}
