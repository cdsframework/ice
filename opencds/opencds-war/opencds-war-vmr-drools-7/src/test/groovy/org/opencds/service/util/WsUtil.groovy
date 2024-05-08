package org.opencds.service.util

import java.security.KeyStore

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory

import org.springframework.ws.client.core.WebServiceTemplate
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender
import org.springframework.xml.transform.StringResult
import org.springframework.xml.transform.StringSource

class WsUtil {
	
	private static final String KEYSTORE = "src/test/resources/keystore/.keystore"
	private static final String PASSWORD = "opencds"

    public static String sendToEndpoint(String uri, String request) {
        StringResult result = new StringResult()
        StringSource source = new StringSource(request)
        def wst = new WebServiceTemplate()
		if (uri.startsWith("https")) 
			wst.setMessageSender(getHttpsMessageSender())
		wst.setDefaultUri(uri)
        wst.sendSourceAndReceiveToResult(source, result)
        return result.toString()
    }
	
	private static HttpsUrlConnectionMessageSender getHttpsMessageSender(){
		
		HttpsUrlConnectionMessageSender msgSender = new HttpsUrlConnectionMessageSender()
		
		KeyStore keyStore = KeyStore.getInstance("JKS")
		keyStore.load(new FileInputStream(KEYSTORE), PASSWORD.toCharArray() )
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
		keyManagerFactory.init(keyStore, PASSWORD.toCharArray())
		msgSender.setKeyManagers(keyManagerFactory.getKeyManagers())

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
		trustManagerFactory.init(keyStore)
		msgSender.setTrustManagers(trustManagerFactory.getTrustManagers())
		
		return msgSender
	}

}
