package org.opencds.config.api.service;

import org.opencds.config.api.model.KnowledgeModule;

public interface KnowledgeBaseService {

    <KB> KB getKnowledgeBase(KnowledgeModule knowledgeModule);

}
