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

package org.opencds.dss.metadata;

import java.math.BigInteger;

import org.omg.dss.common.EntityIdentifier;


public class MetadataDiscoveryFactory {

	public static MetadataDescribeProfile createMetadataDescribeProfile (EntityIdentifier profileId) 
    {	
		MetadataDescribeProfile metadataDescriber;
		try {
			String requestID = "GenericDescribeProfileMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataDescribeProfile)c.newInstance();
			return metadataDescriber;
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

	public static MetadataDescribeScopingEntity createMetadataDescribeScopingEntity(
			String scopingEntityId) 
	{
		MetadataDescribeScopingEntity metadataDescriber;
		try {
			String requestID = "GenericDescribeScopingEntityMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataDescribeScopingEntity)c.newInstance();
			return metadataDescriber;
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

	public static MetadataDescribeScopingEntityHierarchy createMetadataDescribeScopingEntityHierarchy(
			BigInteger maximumDescendantDepth, String scopingEntityId) 
	{
		MetadataDescribeScopingEntityHierarchy metadataDescriber;
		try {
			String requestID = "GenericDescribeScopingEntityHierarchyMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataDescribeScopingEntityHierarchy)c.newInstance();
			return metadataDescriber;
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

	public static MetadataDescribeSemanticRequirement createMetadataDescribeSemanticRequirement(
			EntityIdentifier semanticRequirementId) 
	{
		MetadataDescribeSemanticRequirement metadataDescriber;
		try {
			String requestID = "GenericDescribeSemanticRequirementMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataDescribeSemanticRequirement)c.newInstance();
			return metadataDescriber;
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

	public static MetadataDescribeSemanticSignifier createMetadataDescribeSemanticSignifier(
			EntityIdentifier semanticSignifierId) 
	{
		MetadataDescribeSemanticSignifier metadataDescriber;
		try {
			String requestID = "GenericDescribeSemanticSignifierMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataDescribeSemanticSignifier)c.newInstance();
			return metadataDescriber;
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

	public static MetadataDescribeTrait createMetadataDescribeTrait(
			EntityIdentifier traitId) 
	{
		MetadataDescribeTrait metadataDescriber;
		try {
			String requestID = "GenericDescribeTraitMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataDescribeTrait)c.newInstance();
			return metadataDescriber;
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

	public static MetadataListProfiles createMetadataListProfiles() 
	{
		MetadataListProfiles metadataDescriber;
		try {
			String requestID = "GenericListProfilesMetadata";
			Class<?> c = Class.forName("org.opencds.dss.metadata." + requestID);
			metadataDescriber = (MetadataListProfiles)c.newInstance();
			return metadataDescriber;
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
