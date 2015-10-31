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

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.BindingType;
import org.omg.dss.ObjectFactory;

@WebService(endpointInterface = "org.omg.dss.MetadataDiscovery")
@BindingType(value = "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")
@XmlSeeAlso({
    ObjectFactory.class
})


public class MetadataDiscoveryImpl
/* following code needs to be refactored to match dss-java-stub
 *    implements MetadataDiscovery
 */  {
/*
//    Date rightNow = new Date();
//
//    private static XMLGregorianCalendar long2Gregorian(long date) {
//    	DatatypeFactory dataTypeFactory;
//    	try {
//    	dataTypeFactory = DatatypeFactory.newInstance();
//    	} catch (DatatypeConfigurationException e) {
//    	throw new RuntimeException(e);
//    	}
//    	GregorianCalendar gc = new GregorianCalendar();
//    	gc.setTimeInMillis(date);
//    	return dataTypeFactory.newXMLGregorianCalendar(gc);
//    	}
//
//    private static XMLGregorianCalendar date2Gregorian(Date date) {
//    	return long2Gregorian(date.getTime());
//    	} 
//
//    XMLGregorianCalendar rightNowTime = date2Gregorian(rightNow);

	@Override
//	public ServiceProfile describeProfile(EntityIdentifier profileId)
	public ServiceProfile describeProfile(parameters profileId)
			throws UnrecognizedScopedEntityExceptionFault {
		MetadataDescribeProfile metadataDescriber = MetadataDiscoveryFactory.createMetadataDescribeProfile(profileId);
		return metadataDescriber.getResponse(profileId);
	}

	@Override
	public ScopingEntity describeScopingEntity(String scopingEntityId)
			throws UnrecognizedScopingEntityExceptionFault {
		MetadataDescribeScopingEntity metadataDescriber = MetadataDiscoveryFactory.createMetadataDescribeScopingEntity(scopingEntityId);
		return metadataDescriber.getResponse(scopingEntityId);
	}

	@Override
	public ScopingEntity describeScopingEntityHierarchy(
			BigInteger maximumDescendantDepth, String scopingEntityId)
			throws UnrecognizedScopingEntityExceptionFault {
		MetadataDescribeScopingEntityHierarchy metadataDescriber = MetadataDiscoveryFactory.createMetadataDescribeScopingEntityHierarchy(maximumDescendantDepth, scopingEntityId);
		return metadataDescriber.getResponse(maximumDescendantDepth, scopingEntityId);
	}

	@Override
	public SemanticRequirement describeSemanticRequirement(
			EntityIdentifier semanticRequirementId)
			throws UnrecognizedScopedEntityExceptionFault {
		MetadataDescribeSemanticRequirement metadataDescriber 
			= MetadataDiscoveryFactory.createMetadataDescribeSemanticRequirement(semanticRequirementId);
		return metadataDescriber.getResponse(semanticRequirementId);
	}

	@Override
	public SemanticSignifier describeSemanticSignifier(
			EntityIdentifier semanticSignifierId)
			throws UnrecognizedScopedEntityExceptionFault {
		MetadataDescribeSemanticSignifier metadataDescriber 
			= MetadataDiscoveryFactory.createMetadataDescribeSemanticSignifier(semanticSignifierId);
	return metadataDescriber.getResponse(semanticSignifierId);
	}

	@Override
	public Trait describeTrait(EntityIdentifier traitId)
			throws UnrecognizedScopedEntityExceptionFault {
		MetadataDescribeTrait metadataDescriber 
			= MetadataDiscoveryFactory.createMetadataDescribeTrait(traitId);
		return metadataDescriber.getResponse(traitId);
	}

	@Override
	public ProfilesByType listProfiles() {
		MetadataListProfiles metadataDescriber 
			= MetadataDiscoveryFactory.createMetadataListProfiles();
		return metadataDescriber.getResponse();
	}
*/
}
