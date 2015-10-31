package org.opencds.terminology.apelon;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.xml.XmlConverter;
import org.opencds.common.xml.XmlEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class XmlRequestParser implements RequestParser {
    private static Log log = LogFactory.getLog(XmlRequestParser.class);
    
    @Override
    public Map<String, String> getParameters(InputStream inputStream) throws HttpMessageConversionException {
        // Get XML content as input stream
        try {
            Map<String, String> map = new HashMap<>();
            // Convert input stream into InputSource
            InputSource xmlInputSource = new InputSource(inputStream);
            // Get XML content as XmlEntity root, with XmlSchema validation
            XmlEntity requestEntity = XmlConverter.getInstance().unmarshalXml(xmlInputSource, false, null);
            for(XmlEntity child : requestEntity.getChildren()) {
                map.put(child.getLabel(), child.getValue());
            }        
            return map;
        } catch (SAXParseException e) {
            log.error(e,e);
            throw new HttpMessageConversionException("Error reading input message: " + e.getMessage());
        }
    }    
}
