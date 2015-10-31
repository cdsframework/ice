/**
 * Copyright 2011 - 2013 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.dss.evaluate;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.BindingType;

import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.ObjectFactory;
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

/**
 * EvaluationSoapService is the primary endpoint of the SOAP web service.
 * 
 * It leverages an implementation of {@link Evaluation}, which is a copy of
 * {@link org.omg.dss.Evaluation}, but independent of SOAP (and REST, etc.)
 * configuration.
 * 
 * @author Andrew Iskander, mod by David Shields
 * 
 * @version 1.0
 */
@WebService(endpointInterface = "org.omg.dss.Evaluation")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
@XmlSeeAlso({ ObjectFactory.class })
public class EvaluationSoapService implements org.omg.dss.Evaluation {

    private final Evaluation evaluation;

    public EvaluationSoapService(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public EvaluateAtSpecifiedTimeResponse evaluateAtSpecifiedTime(EvaluateAtSpecifiedTime parameters)
            throws EvaluationExceptionFault,
            InvalidDriDataFormatExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault {
        return evaluation.evaluateAtSpecifiedTime(parameters);
    }

    @Override
    public EvaluateResponse evaluate(Evaluate parameters)
            throws EvaluationExceptionFault,
            InvalidDriDataFormatExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault {
        return evaluation.evaluate(parameters);
    }

    @Override
    public EvaluateIterativelyResponse evaluateIteratively(EvaluateIteratively parameters)
            throws EvaluationExceptionFault,
            InvalidDriDataFormatExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault {
        return evaluation.evaluateIteratively(parameters);
    }

    @Override
    public EvaluateIterativelyAtSpecifiedTimeResponse evaluateIterativelyAtSpecifiedTime(
            EvaluateIterativelyAtSpecifiedTime parameters)
            throws EvaluationExceptionFault,
            InvalidDriDataFormatExceptionFault,
            UnrecognizedScopedEntityExceptionFault,
            UnrecognizedLanguageExceptionFault,
            InvalidTimeZoneOffsetExceptionFault,
            DSSRuntimeExceptionFault,
            RequiredDataNotProvidedExceptionFault,
            UnsupportedLanguageExceptionFault {
        return evaluation.evaluateIterativelyAtSpecifiedTime(parameters);
    }

}
