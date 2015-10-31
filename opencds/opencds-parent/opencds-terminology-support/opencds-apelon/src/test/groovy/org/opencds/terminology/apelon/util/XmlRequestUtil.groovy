package org.opencds.terminology.apelon.util

import org.opencds.common.xml.XmlConverter
import org.opencds.common.xml.XmlEntity
import org.opencds.common.xml.XmlHttpSender
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException


/**
 * For use with the functional tests.
 * 
 * @author phillip
 *
 */
class XmlRequestUtil {

    static String sendRequestGetResponse(String webServiceHostUrl, XmlEntity requestEntity)
    throws SAXParseException, IOException {
        InputStream responseStream = new XmlHttpSender("", "").sendXmlRequestGetResponse(webServiceHostUrl, requestEntity)
        return responseStream.getText()
    }
    
    static createRequest(Map params) {
        XmlEntity requestEntity = new XmlEntity("ApelonDtsServiceRequest")
        params.each {k, v ->
            requestEntity.addChild(new XmlEntity(k, v, false))
        }
        return requestEntity
    }
}
