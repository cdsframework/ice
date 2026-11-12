package org.opencds.config.api.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.model.KMId;
import org.opencds.config.api.model.KnowledgeModule;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

class KnowledgePackageServiceTest
{
    @SuppressWarnings("unchecked")
    private static KnowledgeLoader<Object, Object> loader()
    {
        return mock(KnowledgeLoader.class);
    }

    private static KnowledgeModule knowledgeModule(final boolean preload)
    {
        final KnowledgeModule knowledgeModule = mock(KnowledgeModule.class);
        when(knowledgeModule.kmId()).thenReturn(new KMId("test", UUID.randomUUID().toString(), "1"));
        when(knowledgeModule.preload()).thenReturn(preload);
        return knowledgeModule;
    }

    private static KnowledgePackageService service(final KnowledgeLoader<Object, Object> loader)
    {
        final ExecutionEngineService executionEngineService = mock(ExecutionEngineService.class);
        doReturn(loader).when(executionEngineService).getKnowledgeLoader(null);
        return new KnowledgePackageService(new TaskExecutorAdapter(new SyncTaskExecutor()), executionEngineService);
    }

    @Test
    void preloadFailurePropagates()
    {
        final IllegalStateException failure = new IllegalStateException("rules unavailable");
        final KnowledgeModule knowledgeModule = knowledgeModule(true);
        final KnowledgeLoader<Object, Object> loader = loader();
        when(loader.loadKnowledgePackage(any(), any())).thenThrow(failure);

        final CompletionException error =
                assertThrows(CompletionException.class, () -> service(loader).preloadKnowledgePackages(List.of(knowledgeModule)));

        assertSame(failure, error.getCause());
    }

    @Test
    void preloadSkipsModulesNotMarkedForPreload()
    {
        final KnowledgeLoader<Object, Object> loader = loader();

        service(loader).preloadKnowledgePackages(List.of(knowledgeModule(false)));

        verifyNoInteractions(loader);
    }
}
