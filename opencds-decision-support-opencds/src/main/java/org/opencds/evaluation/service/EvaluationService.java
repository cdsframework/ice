package org.opencds.evaluation.service;

import java.util.List;

import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.common.structures.EvaluationResponseKMItem;
import org.opencds.config.api.KnowledgeRepository;

public interface EvaluationService
{
    EvaluationResponseKMItem evaluate(KnowledgeRepository knowledgeRepository, EvaluationRequestKMItem evaluationRequestKMItem);

    List<EvaluationResponseKMItem> evaluate(KnowledgeRepository knowledgeRepository,
            List<EvaluationRequestKMItem> evaluationRequestKMItems);
}
