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
import jakarta.xml.bind.Marshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.config.api.xml.JAXBContextService;

public class MarshallerFactory {
	private static final Log log = LogFactory.getLog(MarshallerFactory.class);

	public Marshaller create(Class<?> clazz) throws Exception {
		Marshaller marshaller = null;
		log.debug("Creating instance of marshaller: " + clazz.getCanonicalName());
		try {
			marshaller = JAXBContextService.get().getJAXBContext(clazz).createMarshaller();
		} catch (JAXBException e) {
			throw new OpenCDSRuntimeException("Request for Marshaller for class: " + clazz.getCanonicalName()
					+ " created JAXBException: " + e.getMessage(), e);
		}
		if (marshaller == null) {
			throw new OpenCDSRuntimeException("Could not resolve Marshaller for class: " + clazz.getCanonicalName());
		}
		return marshaller;
	}

}
