package org.opencds.config.api.service;

import java.util.List;

import org.opencds.config.api.model.ConceptView;
import org.opencds.config.api.model.KnowledgeModule;

public interface ConceptService {

    List<ConceptView> getConceptViews(String codeSystem, String code);

    ConceptService byKM(KnowledgeModule knowledgeModule);

}
