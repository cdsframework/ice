package org.cdsframework.ice.util

import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.xml.transform.StringResult
import org.springframework.xml.transform.StringSource

class IceWsUtil {

    public static String sendToEndpoint(String uri, String request) {

        StringResult result = new StringResult()
        StringSource source = new StringSource(request)
        def wst = new WebServiceTemplate()
        wst.setDefaultUri(uri)
        wst.sendSourceAndReceiveToResult(source, result)
        return result.toString()
		// return ""
    }
}
