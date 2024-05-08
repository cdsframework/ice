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

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateIteratively;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTime;
import org.omg.dss.evaluation.EvaluateIterativelyAtSpecifiedTimeResponse;
import org.omg.dss.evaluation.EvaluateIterativelyResponse;
import org.omg.dss.evaluation.EvaluateResponse;

public interface Evaluation {
    EvaluateResponse evaluate(Evaluate parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault;

    EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTime(EvaluateAtSpecifiedTime parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault;

    EvaluateIterativelyResponse evaluateIteratively(EvaluateIteratively parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault;

    EvaluateIterativelyAtSpecifiedTimeResponse evaluateIterativelyAtSpecifiedTime(
            EvaluateIterativelyAtSpecifiedTime parameters)
            throws InvalidDriDataFormatExceptionFault,
            UnrecognizedLanguageExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            EvaluationExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault;
}
