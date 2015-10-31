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

package org.opencds.dss.query;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.BindingType;
import org.omg.dss.ObjectFactory;

@WebService(endpointInterface = "org.omg.dss.Query")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
@XmlSeeAlso({
    ObjectFactory.class
})


public class QueryImpl
/* following code needs to be refactored to match dss-java-stub
 * implements Query 
 */ {
 /*

    Date rightNow = new Date();

    private static XMLGregorianCalendar long2Gregorian(long date) {
    	DatatypeFactory dataTypeFactory;
    	try {
    	dataTypeFactory = DatatypeFactory.newInstance();
    	} catch (DatatypeConfigurationException e) {
    	throw new RuntimeException(e);
    	}
    	GregorianCalendar gc = new GregorianCalendar();
    	gc.setTimeInMillis(date);
    	return dataTypeFactory.newXMLGregorianCalendar(gc);
    	}

    private static XMLGregorianCalendar date2Gregorian(Date date) {
    	return long2Gregorian(date.getTime());
    	} 

    XMLGregorianCalendar rightNowTime = date2Gregorian(rightNow);



	@Override
	public RankedKMList findKMs(String clientLanguage,
			KMSearchCriteria kmSearchCriteria)
			throws UnrecognizedLanguageExceptionFault,
			UnrecognizedTraitCriterionExceptionFault,
			UnrecognizedScopedEntityExceptionFault,
			InvalidTraitCriterionDataFormatExceptionFault {
		// TODO Auto-generated method stub
		QueryFindKMs querySolver = QueryFactory.createQueryFindKMs(clientLanguage, kmSearchCriteria);
    	return querySolver.getResponse(clientLanguage, kmSearchCriteria);
	}

	@Override
	public KMDataRequirements getKMDataRequirements(EntityIdentifier kmId)
			throws UnrecognizedScopedEntityExceptionFault {
		// TODO Auto-generated method stub
		QueryGetKMDataRequirements querySolver = QueryFactory.createQueryGetKMDataRequirements(kmId);
    	return querySolver.getResponse(kmId);
	}

	@Override
	public KMDataRequirements getKMDataRequirementsForEvaluationAtSpecifiedTime(
			XMLGregorianCalendar specifiedTime, EntityIdentifier kmId)
			throws UnrecognizedScopedEntityExceptionFault {
		// TODO Auto-generated method stub
		QueryGetKMDataRequirementsForEvaluationAtSpecifiedTime querySolver 
			= QueryFactory.createQueryGetKMDataRequirementsForEvaluationAtSpecifiedTime(specifiedTime, kmId);
    	return querySolver.getResponse(specifiedTime, kmId);
	}

	@Override
	public ExtendedKMDescription getKMDescription(EntityIdentifier kmId,
			String clientLanguage) throws UnrecognizedLanguageExceptionFault,
			UnsupportedLanguageExceptionFault,
			UnrecognizedScopedEntityExceptionFault {
		// TODO Auto-generated method stub
		QueryGetKMDescription querySolver = QueryFactory.createQueryExtendedKMDescription(kmId, clientLanguage);
    	return querySolver.getResponse(kmId, clientLanguage);
	}

	@Override
	public KMEvaluationResultSemanticsList getKMEvaluationResultSemantics(
			EntityIdentifier kmId)
			throws UnrecognizedScopedEntityExceptionFault {
		// TODO Auto-generated method stub
		QueryGetKMEvaluationResultSemantics querySolver = QueryFactory.createQueryGetKMEvaluationResultSemantics(kmId);
    	return querySolver.getResponse(kmId);
	}

	@Override
	public KMList listKMs(String clientLanguage,
			KMTraitInclusionSpecification kmTraitInclusionSpecification)
			throws UnrecognizedLanguageExceptionFault,
			UnsupportedLanguageExceptionFault,
			UnrecognizedScopedEntityExceptionFault {
		// TODO Auto-generated method stub
		QueryListKMs querySolver = QueryFactory.createQueryListKMs(clientLanguage, kmTraitInclusionSpecification);
    	return querySolver.getResponse(clientLanguage, kmTraitInclusionSpecification);
	}
*/
}
