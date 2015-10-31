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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.DataFormatException;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.common.exceptions.InvalidDataException;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.vmr.v1_0.internal.CDSInput;
import org.opencds.vmr.v1_0.internal.VMR;
import org.opencds.vmr.v1_0.internal.datatypes.ANY;
import org.opencds.vmr.v1_0.mappings.utilities.MappingUtility;

/**
 * Mapper classes provide mapping in both directions between the external schema structure of the vMR
 * 		and the internal javabeans used by the rules.
 * 
 * @author Daryl Chertcoff
 *
 */
public class CDSOutputMapper {

	private static Log logger = LogFactory.getLog(CDSOutputMapper.class);
	
//NOTE: there is no pullIn method for CDSOutput, because it is an output-only structure.  Data is pushed out to it.
    
	/**
	 * Populate external vMR object from corresponding internal vMR object
	 * @param source internal vMR object
	 * @param mu MappingUtility instance
	 * @return populated external vMR object; null if provided source parameter is null
	 * @throws InvalidDataException
	 * @throws ImproperUsageException 
	 * @throws DataFormatException 
	 */
    public static void pushOut(Map<String, List<?>> results, CDSInput source, org.opencds.vmr.v1_0.schema.CDSOutput target, String focalPersonId ) 
    throws DataFormatException, ImproperUsageException, InvalidDataException {
    	
		String _METHODNAME = "pushOut(): "; 
		
		if (source == null) {
			String errStr = _METHODNAME + "improper usage: source supplied is null";
			logger.error(errStr);
			throw new ImproperUsageException(errStr);
    		
    	} else {
       		
       		if (results.get("VMR") != null) {
       			@SuppressWarnings("unchecked")
				List<VMR> sourceVmrList = (List<VMR>)results.get("VMR");
       			for ( VMR sourceVmr : sourceVmrList ) {
       				//should only be one, but it is stored in a list structure
       				VMRMapper.pushOut(results, sourceVmr, target, focalPersonId);
       			}
       			
       		} else if (results.get("SimpleOutput") != null) {
       			target.setSimpleOutput(MappingUtility.aNYInternal2ANY((ANY)results.get("SimpleOutput").get(0)));  
       			
       		} else {
       			throw new OpenCDSRuntimeException("No outputVMR or simpleOutput found by CdsOutputResultSetBuilder.  No result will be returned");
       		}
       	}
    	
    	return;
    }
}
