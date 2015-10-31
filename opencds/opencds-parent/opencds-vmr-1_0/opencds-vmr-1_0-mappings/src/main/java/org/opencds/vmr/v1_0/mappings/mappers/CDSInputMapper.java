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

package org.opencds.vmr.v1_0.mappings.mappers;

import java.util.ArrayList;
import java.util.List;

import org.opencds.vmr.v1_0.internal.CDSContext;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.CDSResource;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class CDSInputMapper {
	/**
	 * External  vmr CDSInput variables
     * List<II> templateId;
     * List<CDSResource> cdsResource;
     * //protected VMR vmrInput;  -- just using the focalPersonId to substitute for the VMR
     * String focalPersonId;		//in place of the full VMR
     * CDSContext cdsContext;
	 */
	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param source external vMR object
	 * @param mu MappingUtility instance
	 * @return populated internal vMR structure; null if provided source parameter is null
	 */
	public static void pullIn(org.opencds.vmr.v1_0.schema.CDSInput source, CDSInput target, MappingUtility mu ) {

		if (source == null)
			return;
//		CDSInput target = new CDSInput();
		List<String> templateIds = new ArrayList<String>();
		for (int i=0; i < source.getTemplateId().size(); i++) {
			templateIds.add(MappingUtility.iI2Root("templateId", source.getTemplateId().get(i)));
		}
		if (templateIds.size() > 0) target.setTemplateId(templateIds);
		List<CDSResource> CdsResources = new ArrayList<CDSResource>();
		for (org.opencds.vmr.v1_0.schema.CDSResource thisCdsResource : (List<org.opencds.vmr.v1_0.schema.CDSResource>)source.getCdsResource()) {
			if (thisCdsResource != null) {
				CDSResource targetCDSResource = new CDSResource();
//				CDSResourceMapper.pullIn(thisCdsResource, targetCDSResource, mu);
				if (thisCdsResource.getCdsResourceType() != null) targetCDSResource.setCdsResourceType(MappingUtility.cD2CDInternal(thisCdsResource.getCdsResourceType()));
				if (thisCdsResource.getResourceContents() != null) targetCDSResource.setResourceContents(thisCdsResource.getResourceContents());
				CdsResources.add(targetCDSResource);
			}
		}
		target.setFocalPersonId(MappingUtility.iI2FlatId(source.getVmrInput().getPatient().getId()));
		if (source.getCdsContext() != null) {
			CDSContext internalCDSContext = new CDSContext();
			if (source.getCdsContext().getCdsSystemUserType() != null) 
				internalCDSContext.setCdsSystemUserType(MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserType()));
			if (source.getCdsContext().getCdsSystemUserPreferredLanguage() != null) 
				internalCDSContext.setCdsSystemUserPreferredLanguage(MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserPreferredLanguage()));
			if (source.getCdsContext().getCdsInformationRecipientType() != null) 
				internalCDSContext.setCdsInformationRecipientType(MappingUtility.cD2CDInternal(source.getCdsContext().getCdsInformationRecipientType()));
			if (source.getCdsContext().getCdsInformationRecipientType() != null) 
				internalCDSContext.setCdsInformationRecipientPreferredLanguage(MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserPreferredLanguage()));
			if (source.getCdsContext().getCdsSystemUserTaskContext() != null) 
				internalCDSContext.setCdsSystemUserTaskContext(MappingUtility.cD2CDInternal(source.getCdsContext().getCdsSystemUserTaskContext()));
			if (internalCDSContext != null) target.setCdsContext(internalCDSContext);
		}

		return;
	}
	
	//NOTE there is no pushOut from CDSInput, use CDSOutput for output
//	/**
//	 * Populate external vMR object from corresponding internal vMR object
//	 * @param source internal vMR object
//	 * @param mu MappingUtility instance
//	 * @return populated external vMR object; null if provided source parameter is null
//	 */
//	public static org.opencds.vmr.v1_0.schema.CDSContext pushOut(CDSContext source, MappingUtility mu) {
//		
//		if (source == null)
//			return null;
//		org.opencds.vmr.v1_0.schema.CDSContext target = new org.opencds.vmr.v1_0.schema.CDSContext();
//		target.setCdsSystemUserType(mu.cDInternal2CD(source.getCdsSystemUserType()));
//		target.setCdsSystemUserPreferredLanguage(mu.cDInternal2CD(source.getCdsSystemUserPreferredLanguage()));
//		target.setCdsInformationRecipientType(mu.cDInternal2CD(source.getCdsInformationRecipientType()));
//		target.setCdsInformationRecipientPreferredLanguage(mu.cDInternal2CD(source.getCdsSystemUserPreferredLanguage()));
//		target.setCdsSystemUserTaskContext(mu.cDInternal2CD(source.getCdsSystemUserTaskContext()));		
//		
//		return target;
//	}

}
