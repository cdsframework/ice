/*
 * Copyright 2014-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.config.api.util;

import org.opencds.config.api.model.EntityIdentifier;
import org.opencds.config.api.model.impl.EntityIdentifierImpl;

public class EntityIdentifierUtil {

	public static EntityIdentifier makeEI( String scopingEntityId, String businessId, String version ) {
		return EntityIdentifierImpl.create(scopingEntityId, businessId, version);
	}

	public static EntityIdentifier makeEI( String eiString ) {
		return makeEI(
		        eiString.substring(0, eiString.indexOf("^")),
		        eiString.substring(eiString.indexOf("^")+1, eiString.lastIndexOf("^")),
		        eiString.substring(eiString.lastIndexOf("^")+1));
	}

	public static String makeEIString ( EntityIdentifier ei ) {
	    if (ei == null) {
	        return null;
	    }
		return ei.getScopingEntityId() + "^" + ei.getBusinessId() + "^" + ei.getVersion();
	}


}
