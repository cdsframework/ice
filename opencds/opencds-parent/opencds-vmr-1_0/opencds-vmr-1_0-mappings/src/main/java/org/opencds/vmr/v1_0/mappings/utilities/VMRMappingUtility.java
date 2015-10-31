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

package org.opencds.vmr.v1_0.mappings.utilities;

import javax.xml.datatype.XMLGregorianCalendar;
import org.opencds.common.utilities.XMLDateUtility;

/**
 * <p>VMRMappingUtility is used to perform mapping functions between external and internal
 * data structures.  It is thread-safe,
 * in that new VMRMappingUtility objects are created for performing an operation or
 * evaluation if one is needed.
 * </p>
 * 
 *
 * <p>Initial Create Date: 2010-04-15</p>
 * <p>Last Update Date: 2011-02-09</p>
 * <p>Version history: </p>
 * <p>v1.00 (2010-04-15).  Initial creation.</p>
 * <p>v2.00 (2011-02-09).  Revision to 2nd major version of vMR schema and internal classes.  Released as vmr-alpha.</p>
 * <p>v3.00 (2011-08-14).  Revision to 3rd major version of vMR schema and internal classes.  Release as vmr-1.0,</p>
 *
 * @author Kensaku Kawamoto, mod by David Shields
 * @version 3.00
 */

public class VMRMappingUtility extends java.lang.Object
{
	    private static VMRMappingUtility instance = new VMRMappingUtility();  //singleton instance

	    private VMRMappingUtility()
	    {
	    }

	    public static VMRMappingUtility getInstance()
	    {
	        return instance;
	    }

		public XMLGregorianCalendar dateFlat2XMLGregorianDate( java.util.Date date )
		{
			if ( date == null ) {
				//nothing to work with
				return null;
			}
			try {
				XMLGregorianCalendar tsOut = XMLDateUtility.date2XMLGregorian( date );
				return tsOut;
			} catch (Exception e) {
	            e.printStackTrace();  
				throw new RuntimeException( "dateFlat2XMLGregorianDate(" + date.toString() + ") had errors: " + e.getMessage() );
			}
		}
		
		public java.util.Date xMLGregorianDate2Date( XMLGregorianCalendar ts )
		{
			if ( ts == null ) {
				//nothing to work with
				return null;
			}
			try {
				java.util.Date date = XMLDateUtility.xmlGregorian2Date( ts );
				return date;
			} catch (Exception e) {
	            e.printStackTrace();  
				throw new RuntimeException( "xMLGregorianDate2Date(" + ts.toString() + ") had errors: " + e.getMessage() );
			}
		}
		
}
