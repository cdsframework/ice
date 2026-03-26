package org.opencds.dss.evaluate;

import java.util.Date;
import java.util.List;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.DataRequirementItemData;
import org.omg.dss.EvaluationRequest;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;

public interface RequestProcessor
{
    List<EvaluationRequestKMItem> decodeInput(KnowledgeRepository knowledgeRepository, EvaluationRequest request,
            EvaluationRequestDataItem evaluationRequestDataItem, List<DataRequirementItemData> listDRIData, Date evalTime)
            throws DSSRuntimeExceptionFault;
}
