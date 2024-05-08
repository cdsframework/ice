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

package org.opencds.config.api.pool;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.xml.JAXBContextService;

public class UnmarshallerFactory {
	private static final Log log = LogFactory.getLog(UnmarshallerFactory.class);

	public Unmarshaller create(Class<?> clazz) throws Exception {
		Unmarshaller unmarshaller = null;
		log.debug("Creating instance of unmarshaller: " + clazz.getCanonicalName());
		try {
			unmarshaller = JAXBContextService.get().getJAXBContext(clazz).createUnmarshaller();
		} catch (JAXBException e) {
			throw new OpenCDSRuntimeException("Request for Unmarshaller for class: " + clazz.getCanonicalName()
					+ " created JAXBException: " + e.getMessage());
		}
		if (unmarshaller == null) {
			throw new OpenCDSRuntimeException(
					"Could not resolve Unmarshaller for class: " + clazz.getCanonicalName());
		}
		return unmarshaller;
	}

}
