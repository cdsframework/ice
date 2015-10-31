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

import javax.xml.datatype.XMLGregorianCalendar;

import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.query.requests.KMSearchCriteria;
import org.omg.dss.query.requests.KMTraitInclusionSpecification;

public class QueryFactory {
	
	public static QueryFindKMs createQueryFindKMs (String clientLanguage,	KMSearchCriteria kmSearchCriteria) 
    {	
		QueryFindKMs querySolver;
		try {
			String requestID = "GenericFindKMsQuery";
			Class<?> c = Class.forName("org.opencds.dss.query." + requestID);
			querySolver = (QueryFindKMs)c.newInstance();
			return querySolver;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		  catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		  catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static QueryGetKMDataRequirements createQueryGetKMDataRequirements (EntityIdentifier kmId) 
    {	
		QueryGetKMDataRequirements querySolver;
		try {
			String requestID = "GenericGetKMDataRequirementsQuery";
			Class<?> c = Class.forName("org.opencds.dss.query." + requestID);
			querySolver = (QueryGetKMDataRequirements)c.newInstance();
			return querySolver;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		  catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		  catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static QueryGetKMDataRequirementsForEvaluationAtSpecifiedTime createQueryGetKMDataRequirementsForEvaluationAtSpecifiedTime (
			XMLGregorianCalendar specifiedTime, EntityIdentifier kmId) 
    {	
		QueryGetKMDataRequirementsForEvaluationAtSpecifiedTime querySolver;
		try {
			String requestID = "GenericGetKMDataRequirementsForEvaluationAtSpecifiedTimeQuery";
			Class<?> c = Class.forName("org.opencds.dss.query." + requestID);
			querySolver = (QueryGetKMDataRequirementsForEvaluationAtSpecifiedTime)c.newInstance();
			return querySolver;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		  catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		  catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static QueryGetKMDescription createQueryExtendedKMDescription (EntityIdentifier kmId, String clientLanguage) 
    {	
		QueryGetKMDescription querySolver;
		try {
			String requestID = "GenericGetKMDescriptionQuery";
			Class<?> c = Class.forName("org.opencds.dss.query." + requestID);
			querySolver = (QueryGetKMDescription)c.newInstance();
			return querySolver;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		  catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		  catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static QueryGetKMEvaluationResultSemantics createQueryGetKMEvaluationResultSemantics (
			EntityIdentifier kmId) 
    {	
		QueryGetKMEvaluationResultSemantics querySolver;
		try {
			String requestID = "GenericGetKMEvaluationResultSemanticsQuery";
			Class<?> c = Class.forName("org.opencds.dss.query." + requestID);
			querySolver = (QueryGetKMEvaluationResultSemantics)c.newInstance();
			return querySolver;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		  catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		  catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static QueryListKMs createQueryListKMs (
			String clientLanguage, KMTraitInclusionSpecification kmTraitInclusionSpecification) 
    {	
		QueryListKMs querySolver;
		try {
			String requestID = "GenericListKMsQuery";
			Class<?> c = Class.forName("org.opencds.dss.query." + requestID);
			querySolver = (QueryListKMs)c.newInstance();
			return querySolver;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		  catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		  catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}
}
