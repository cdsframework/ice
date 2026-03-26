package org.opencds.dss.evaluation.service;

import java.util.List;
import java.util.Map;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeLoader;
import org.opencds.config.api.KnowledgeRepository;

@Deprecated(forRemoval = true)
public interface Evaluater<KPInput, KP> extends KnowledgeLoader<KPInput, KP>
{
    Map<String, List<?>> getOneResponse(KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem dssRequestKMItem);
}
