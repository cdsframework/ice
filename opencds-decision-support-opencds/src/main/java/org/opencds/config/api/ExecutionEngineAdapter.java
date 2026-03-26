package org.opencds.config.api;

public interface ExecutionEngineAdapter<Input, Output, KnowledgePackage>
{
    ExecutionEngineContext<Input, Output> execute(KnowledgePackage knowledgePackage, ExecutionEngineContext<Input, Output> context)
            throws Exception;
}
