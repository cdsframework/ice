package org.opencds.service.evaluate;

import java.util.Date;
import java.util.List;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;
import org.opencds.dss.evaluate.KMEvalRequest;

public interface RequestProcessor {

    List<EvaluationRequestKMItem> decodeInput(KnowledgeRepository knowledgeRepository, KMEvalRequest request,
            EvaluationRequestDataItem evaluationRequestDataItem)
            throws InvalidDriDataFormatExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            UnsupportedLanguageExceptionFault,
            DSSRuntimeExceptionFault;

}
