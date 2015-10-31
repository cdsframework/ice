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
