/*
 * Copyright 2013-2020 OpenCDS.org
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

package org.opencds.config.migrate.utilities;

import org.opencds.common.exceptions.OpenCDSRuntimeException;
import org.opencds.common.xml.XmlConverter;
import org.opencds.common.xml.XmlEntity;
import org.xml.sax.SAXParseException;

public class XmlUtility {

	public XmlEntity unmarshalXml(String source) {
		try {
			return XmlConverter.getInstance().unmarshalXml(source, false, null);
		} catch (SAXParseException e) {
			throw new OpenCDSRuntimeException("Failed to unmarshal xml source.");
		}
	}

}
