package org.opencds.webapp.config.base;

import jakarta.annotation.PreDestroy;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.opencds.common.utilities.MiscUtility;
import org.opencds.config.api.ConfigData;
import org.opencds.config.api.ConfigurationService;
import org.opencds.config.api.cache.CacheService;
import org.opencds.config.classpath.ClasspathConfigStrategy;
import org.opencds.config.file.FileConfigStrategy;
import org.opencds.config.service.CacheServiceImpl;
import org.opencds.config.store.strategy.BDBConfigStrategy;
import org.opencds.evaluation.service.EvaluationService;
import org.opencds.evaluation.service.EvaluationServiceImpl;
import org.opencds.evaluation.service.util.CallableUtilImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class AppConfiguration {
    @Value("${knowledge-repository.type}")
    private String knowledgeRepoType;

    @Value("${knowledge-repository.path}")
    private String knowledgeRepoPath;


    @Value("${km.threads}")
    private int kmThreads;

    private MiscUtility miscUtility;
    private BDBConfigStrategy bdbConfigStrategy;

    public AppConfiguration() {
        this.miscUtility = new MiscUtility();
        this.bdbConfigStrategy = new BDBConfigStrategy();
    }

    @PreDestroy
    public void shutdown() {
        miscUtility.shutdown();
        bdbConfigStrategy.shutdown();
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public CacheService cacheService() {
        return new CacheServiceImpl();
    }

    @Bean
    public ConfigData configData() {
        // TODO: static create method
        ConfigData cd = new ConfigData();
        System.err.println("type   : " + knowledgeRepoType);
        System.err.println("path   : " + knowledgeRepoPath);
        System.err.println("threads: " + kmThreads);
        cd.setConfigType(knowledgeRepoType);
        cd.setConfigPath(knowledgeRepoPath);
        cd.setKmThreads(kmThreads);
        return cd;
    }

    @Bean
    public ConfigurationService configurationService() {
        return new ConfigurationService(
                Stream
                        .of(bdbConfigStrategy(),
                                fileConfigStrategy(),
                                classpathConfigStrategy())
                        .collect(Collectors.toSet()),
                cacheService(),
                configData());
    }

    @Bean
    public EvaluationService evaluationService() {
        return new EvaluationServiceImpl(new CallableUtilImpl());
    }

    @Bean
    public MiscUtility miscUtility() {
        return miscUtility;
    }

    private BDBConfigStrategy bdbConfigStrategy() {
        return bdbConfigStrategy;
    }

    private FileConfigStrategy fileConfigStrategy() {
        return new FileConfigStrategy();
    }

    private ClasspathConfigStrategy classpathConfigStrategy() {
        return new ClasspathConfigStrategy();
    }
}
