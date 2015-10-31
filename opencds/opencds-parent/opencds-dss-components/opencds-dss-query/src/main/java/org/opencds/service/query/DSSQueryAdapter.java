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

package org.opencds.service.query;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.InvalidTraitCriterionDataFormatExceptionFault;
import org.omg.dss.UnrecognizedLanguageExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnrecognizedTraitCriterionExceptionFault;
import org.omg.dss.UnsupportedLanguageExceptionFault;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.knowledgemodule.ExtendedKMDescription;
import org.omg.dss.knowledgemodule.KMDataRequirements;
import org.omg.dss.query.requests.KMSearchCriteria;
import org.omg.dss.query.requests.KMTraitInclusionSpecification;
import org.omg.dss.query.responses.KMEvaluationResultSemanticsList;
import org.omg.dss.query.responses.KMList;
import org.omg.dss.query.responses.RankedKMList;
import org.opencds.common.config.OpencdsConfigurator;
import org.opencds.common.utilities.DateUtility;
import org.opencds.knowledgeRepository.SimpleKnowledgeRepository;


/**
 * <p>Adapter to process the query operation of the DSS web service:
 * 
 * <p/>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: OpenCDS</p>
 *
 * @author David Shields
 * @version 1.0
 * @date 09-24-2010
 * 
 */

public class DSSQueryAdapter extends Object
{
	
    private static DSSQueryAdapter instance;  //singleton instance
	private static 	Log 			log 			= LogFactory.getLog(DSSQueryAdapter.class);
	private 		DateUtility 	dateUtility 	= DateUtility.getInstance();
    
    protected DSSQueryAdapter() throws DSSRuntimeExceptionFault
    {
        initialize();
    }

	protected void initialize() throws DSSRuntimeExceptionFault
    {
		String startTime = dateUtility.getDateAsString(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println();
        System.out.println(startTime + " <<< Initializing OpenCDS DSSQueryAdapter >>>");
        System.out.println();
        log.info(startTime + " Initializing OpenCDS DSSQueryAdapter");
        
        OpencdsConfigurator oConfig = new OpencdsConfigurator();  		// initializes the configurator.
        SimpleKnowledgeRepository.setFullPathToKRData(oConfig.getKrFullPath());	// initializes the KnowledgeRepository        
    }

    // public functions
    public static synchronized DSSQueryAdapter getInstance() throws DSSRuntimeExceptionFault
    {
        if (instance == null)
        {
            instance = new DSSQueryAdapter();
        }
        return instance;
    }
    

    public RankedKMList findKMs(String clientLanguage,
			KMSearchCriteria kmSearchCriteria)
	throws UnrecognizedLanguageExceptionFault,
	UnrecognizedTraitCriterionExceptionFault,
	UnrecognizedScopedEntityExceptionFault,
	InvalidTraitCriterionDataFormatExceptionFault
    {
    	//TODO implement this
    	return null;
    }
    

    public KMDataRequirements getKMDataRequirements(EntityIdentifier kmId)
	throws UnrecognizedScopedEntityExceptionFault
    {
    	//TODO implement this
    	return null;
    }
    

    public KMDataRequirements getKMDataRequirementsForEvaluationAtSpecifiedTime(
			XMLGregorianCalendar specifiedTime, EntityIdentifier kmId)
	throws UnrecognizedScopedEntityExceptionFault
    {
    	//TODO implement this
    	return null;
    }
    

    public ExtendedKMDescription getKMDescription(EntityIdentifier kmId,
			String clientLanguage) throws UnrecognizedLanguageExceptionFault,
			UnsupportedLanguageExceptionFault,
			UnrecognizedScopedEntityExceptionFault
    {
    	//TODO implement this
		return null;
    }
    

    public KMEvaluationResultSemanticsList getKMEvaluationResultSemantics(
			EntityIdentifier kmId)
	throws UnrecognizedScopedEntityExceptionFault
    {
    	//TODO implement this
    	return null;
    }
    

    public KMList listKMs(String clientLanguage,
			KMTraitInclusionSpecification kmTraitInclusionSpecification)
	throws UnrecognizedLanguageExceptionFault,
	UnsupportedLanguageExceptionFault,
	UnrecognizedScopedEntityExceptionFault
    {
    	//TODO implement this
    	return null;
    }
    
    
}
