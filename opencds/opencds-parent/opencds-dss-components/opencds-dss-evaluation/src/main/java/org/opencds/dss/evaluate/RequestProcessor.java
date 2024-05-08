/*
 * Copyright 2013-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.dss.evaluate;

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
import org.omg.dss.evaluation.requestresponse.DataRequirementItemData;
import org.omg.dss.evaluation.requestresponse.EvaluationRequest;
import org.opencds.common.structures.EvaluationRequestDataItem;
import org.opencds.common.structures.EvaluationRequestKMItem;
import org.opencds.config.api.KnowledgeRepository;

public interface RequestProcessor {

    List<EvaluationRequestKMItem> decodeInput(KnowledgeRepository knowledgeRepository, EvaluationRequest request,
            EvaluationRequestDataItem evaluationRequestDataItem, List<DataRequirementItemData> listDRIData,
            Date evalTime)
            throws InvalidDriDataFormatExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            UnsupportedLanguageExceptionFault,
            DSSRuntimeExceptionFault;

}
