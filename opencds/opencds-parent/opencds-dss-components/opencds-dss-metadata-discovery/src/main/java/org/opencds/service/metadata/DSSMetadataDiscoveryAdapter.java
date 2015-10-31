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

package org.opencds.service.metadata;

import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.DSSRuntimeExceptionFault;
import org.omg.dss.UnrecognizedScopedEntityExceptionFault;
import org.omg.dss.UnrecognizedScopingEntityExceptionFault;
import org.omg.dss.common.EntityIdentifier;
import org.omg.dss.common.ScopingEntity;
import org.omg.dss.metadata.profile.ProfilesByType;
import org.omg.dss.metadata.profile.ServiceProfile;
import org.omg.dss.metadata.semanticrequirement.SemanticRequirement;
import org.omg.dss.metadata.semanticsignifier.SemanticSignifier;
import org.omg.dss.metadata.trait.Trait;
import org.opencds.common.config.OpencdsConfigurator;
import org.opencds.common.utilities.DateUtility;
import org.opencds.knowledgeRepository.SimpleKnowledgeRepository;


/**
 * <p>Adapter to process the metadata discovery operation of the DSS web service:
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

public class DSSMetadataDiscoveryAdapter extends Object
{
	
    private static 	DSSMetadataDiscoveryAdapter instance;  //singleton instance
	private static 	Log 			log 			= LogFactory.getLog(DSSMetadataDiscoveryAdapter.class);
	private 		DateUtility 	dateUtility 	= DateUtility.getInstance();
    
    protected DSSMetadataDiscoveryAdapter() throws DSSRuntimeExceptionFault
    {
        initialize();
    }

	protected void initialize() throws DSSRuntimeExceptionFault
    {
		String startTime = dateUtility.getDateAsString(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println();
        System.out.println(startTime + " <<< Initializing OpenCDS DSSMetadataDiscoveryAdapter >>>");
        System.out.println();
        log.info(startTime + " Initializing OpenCDS DSSMetadataDiscoveryAdapter");
        
        OpencdsConfigurator oConfig = new OpencdsConfigurator();  		// initializes the configurator.
        SimpleKnowledgeRepository.setFullPathToKRData(oConfig.getKrFullPath());	// initializes the KnowledgeRepository               
    }

    // public functions
    public static synchronized DSSMetadataDiscoveryAdapter getInstance() throws DSSRuntimeExceptionFault
    {
        if (instance == null)
        {
            instance = new DSSMetadataDiscoveryAdapter();
        }
        return instance;
    }
    

	public ServiceProfile describeProfile(EntityIdentifier profileId)
	throws UnrecognizedScopedEntityExceptionFault 
    {
    	//TODO implement this
		return null;
    }
    

	public ScopingEntity describeScopingEntity(String scopingEntityId)
	throws UnrecognizedScopingEntityExceptionFault 
	{
    	//TODO implement this
		return null;
	} 

	public ScopingEntity describeScopingEntityHierarchy(
			BigInteger maximumDescendantDepth, String scopingEntityId)
			throws UnrecognizedScopingEntityExceptionFault 
	{
    	//TODO implement this
		return null;
	} 
  

	public SemanticRequirement describeSemanticRequirement(
			EntityIdentifier semanticRequirementId)
			throws UnrecognizedScopedEntityExceptionFault 
	{
    	//TODO implement this
		return null;
	} 
  

	public SemanticSignifier describeSemanticSignifier(
			EntityIdentifier semanticSignifierId)
			throws UnrecognizedScopedEntityExceptionFault 
	{
    	//TODO implement this
		return null;
	}  
  

	public Trait describeTrait(EntityIdentifier traitId)
	throws UnrecognizedScopedEntityExceptionFault 
	{
    	//TODO implement this
		return null;
	}  
  

	public ProfilesByType listProfiles() 
	throws DSSRuntimeExceptionFault
	{
    	//TODO implement this
		return null;
	}  
  

}
