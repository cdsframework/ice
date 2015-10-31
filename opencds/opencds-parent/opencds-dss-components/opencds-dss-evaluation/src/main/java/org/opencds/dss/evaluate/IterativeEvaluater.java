/**
 * Copyright 2011 OpenCDS.org
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

import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.EvaluationExceptionFault;
import org.omg.dss.InvalidDriDataFormatExceptionFault;
import org.omg.dss.InvalidTimeZoneOffsetExceptionFault;
import org.omg.dss.RequiredDataNotProvidedExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.omg.dss.common.InteractionIdentifier;
//import org.omg.dss.evaluation.Evaluate;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationRequest;
import org.omg.dss.evaluation.requestresponse.IterativeEvaluationResponse;

/**
 * 
 * @author Andrew Iskander, mod by David Shields
 * 
 * @version 1.0
 */
public abstract class IterativeEvaluater {

	/**
	 * 
	 * @param ii = the interaction identifier, contains unique ID plus a timestamp identifying when it was initially submitted
	 * @param et = evalTime, is the point in time to evaluate the submitted data.  May be future, present, or past
	 * @param e  = evaluation request, which is the list of requested rules to evaluate, and the semantic payload which includes the data to be evaluated
	 * @return = IterativeEvaluationResponse is the structured response, containing identifiers and the response semantic payload,
	 * 				 it may be an interim response or a final response
	 * @throws InvalidDriDataFormatExceptionFault
	 * @throws UnrecognizedLanguageExceptionFault
	 * @throws RequiredDataNotProvidedExceptionFault
	 * @throws UnsupportedLanguageExceptionFault
	 * @throws UnrecognizedScopedEntityExceptionFault
	 * @throws EvaluationExceptionFault
	 * @throws InvalidTimeZoneOffsetExceptionFault
	 */
	public abstract IterativeEvaluationResponse getResponse(InteractionIdentifier ii, XMLGregorianCalendar et, IterativeEvaluationRequest e)
			throws InvalidDriDataFormatExceptionFault, 
			UnrecognizedLanguageExceptionFault, 
			RequiredDataNotProvidedExceptionFault, 
			UnsupportedLanguageExceptionFault, 
			UnrecognizedScopedEntityExceptionFault, 
			EvaluationExceptionFault, 
			InvalidTimeZoneOffsetExceptionFault;

}
