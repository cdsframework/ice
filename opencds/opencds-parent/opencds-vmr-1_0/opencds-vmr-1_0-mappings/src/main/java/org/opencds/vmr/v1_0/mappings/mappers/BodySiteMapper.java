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

import org.opencds.vmr.v1_0.internal.BodySite;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class BodySiteMapper {

	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param source external vMR object
	 * @param mu MappingUtility instance
	 * @return populated internal vMR structure; null if provided source parameter is null
	 */
	public static BodySite pullIn(org.opencds.vmr.v1_0.schema.BodySite source, MappingUtility mu) {
		
		if (source == null)
			return null;
		
		BodySite target = new BodySite();
		if (source.getBodySiteCode() != null) target.setBodySiteCode(MappingUtility.cD2CDInternal(source.getBodySiteCode()));
		if (source.getLaterality() != null) target.setLaterality(MappingUtility.cD2CDInternal(source.getLaterality()));
		
		return target;
	}
	
	/**
	 * Populate internal vMR object from corresponding external vMR object
	 * @param source external vMR object
	 * @param mu MappingUtility instance
	 * @return populated internal vMR structure; null if provided source parameter is null
	 */
	public static BodySite pullIn(org.opencds.vmr.v1_0.schema.BodySite source) {
		
		if (source == null)
			return null;
		
		BodySite target = new BodySite();
		if (source.getBodySiteCode() != null) target.setBodySiteCode(MappingUtility.cD2CDInternal(source.getBodySiteCode()));
		if (source.getLaterality() != null) target.setLaterality(MappingUtility.cD2CDInternal(source.getLaterality()));
		
		return target;
	}
	
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * @param source internal vMR object
	 * @param mu MappingUtility instance
	 * @return populated external vMR object; null if provided source parameter is null
	 */
	public static org.opencds.vmr.v1_0.schema.BodySite pushOut(BodySite source, MappingUtility mu) {
		if (source == null)
			return null;
		org.opencds.vmr.v1_0.schema.BodySite target = new org.opencds.vmr.v1_0.schema.BodySite();
		if (source.getBodySiteCode() != null) target.setBodySiteCode(MappingUtility.cDInternal2CD(source.getBodySiteCode()));
		if (source.getLaterality() != null) target.setLaterality(MappingUtility.cDInternal2CD(source.getLaterality()));

		return target;
	}
	public static org.opencds.vmr.v1_0.schema.BodySite pushOut(BodySite source) {
		if (source == null)
			return null;
		org.opencds.vmr.v1_0.schema.BodySite target = new org.opencds.vmr.v1_0.schema.BodySite();
		if (source.getBodySiteCode() != null) target.setBodySiteCode(MappingUtility.cDInternal2CD(source.getBodySiteCode()));
		if (source.getLaterality() != null) target.setLaterality(MappingUtility.cDInternal2CD(source.getLaterality()));

		return target;
	}
}
