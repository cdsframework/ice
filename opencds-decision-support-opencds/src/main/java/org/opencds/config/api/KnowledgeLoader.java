package org.opencds.config.api;

import java.util.function.Function;

import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgeLoader<KPInput, KP>
{
    KP loadKnowledgePackage(KnowledgeModule knowledgeModule, Function<KnowledgeModule, KPInput> inputFunction);
}
