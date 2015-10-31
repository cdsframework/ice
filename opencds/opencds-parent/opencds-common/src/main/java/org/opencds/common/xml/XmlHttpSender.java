package org.opencds.common.xml;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.utilities.EncoderUtility;

/**
 * <p>XmlHttpSender is used to send a Xml request to a servlet and receive a Xml
 * response back as the response.  Also, can send a XML response from a client servlet
 * as well as a request.</p>
 *
 * @author Kensaku Kawamoto
 * @version 1.0
 */
public class XmlHttpSender extends Object
{
    private static final Log log = LogFactory.getLog(XmlHttpSender.class);

    /**
     * Create the XmlHttpSender; need to specify where the certificate Trust Store
     * is, and what the password into that Trust Store is, if the XmlHttpSender
     * is to be used for transmitting with SSL turned on.
     *
     * @param trustStoreLocation Place of Trust Store (e.g. "C:\\Program Files\\Apache Group\\Tomcat 4.1\\stores\\centralServer.keystore")
     * @param trustStorePassword Password to access Trust Store.
     */
    public XmlHttpSender(String trustStoreLocation, String trustStorePassword)
    {
        System.setProperty("java.protocol.handler.pkgs", "javax.net.ssl");
        System.setProperty("javax.net.ssl.trustStore", trustStoreLocation);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    /**
     * Sends response to client servlet; protected because not meant for direct access outside of this class.
     *
     * @param response                The response object obtained by this servlet.
     * @param responseContentType     Expected content types include: "test/xml; charset=utf-8", "application/pdf",
     *                                "text/html; charset=utf-8"
     * @param responseContentAsStream The content to be returned to the client servlet.
     */
    protected void sendResponse(HttpServletResponse response, String responseContentType, InputStream responseContentAsStream) throws IOException
    {
        try
        {
            ServletOutputStream ouputStream = response.getOutputStream();

            // set content type of response
            response.setContentType(responseContentType);

            int c = 0;
            byte data[] = new byte[4096];

            while ((c = responseContentAsStream.read(data)) != -1)
            {
                ouputStream.write(data, 0, c);
            }
            ouputStream.flush();
            ouputStream.close();
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * Calls sendRequestGetResponse with ignoreSslCertificateWarnings == true
     *
     * @param targetServletUrl
     * @param requestContentAsStream
     * @param requestPropertyContainer
     * @return
     * @throws IOException
     */
    public InputStream sendRequestGetResponse(String targetServletUrl, InputStream requestContentAsStream, Map<String, String> requestPropertyContainer) throws IOException
    {
        return sendRequestGetResponse(targetServletUrl, requestContentAsStream, requestPropertyContainer, true);
    }

    /**
     * Sends a request to the service servlet located at targetServletUrl
     * (e.g. "http://arabian:8080/clientServlet/ClientServlet").  Returns the response
     * of the servlet call as an InputStream.  Protected because not meant for direct
     * access outside of this class.  Uses HTTPS if required, does not if not required.
     *
     * @param targetServletUrl             Url of servlet to which request is being sent (e.g. "http://arabian:8080/clientServlet/ClientServlet").
     * @param requestContentAsStream       The content to be request delivered to the service servlet.
     * @param requestProperties     The request properties to be set; potential parameters include:
     *                                     "Content-Type" --> "text/html; charset=utf-8"; "text/xml; charset=utf-8"; "application/pdf"
     *                                     "Content-Length" --> length of content; can get by calling string.getBytes() --> then bytes.length
     *                                     "SOAPAction" --> "http://tempuri.org/CBRS1/CBRSRequests/Procedures"
     *                                     "Host" --> "cdgdev.duhs.duke.edu"
     * @param ignoreSslCertificateWarnings If set to true, when making an SSL connection to an https host, ignores any warnings regarding
     *                                     the SSL certificate (e.g. whether its name is no longer valid), instead of throwing an exception.
     * @return The reponse from the service servlet as an InputStream.
     * @throws IOException
     */
    public InputStream sendRequestGetResponse(String targetServletUrl, InputStream requestContentAsStream, Map<String, String> requestProperties, boolean ignoreSslCertificateWarnings) throws IOException
    {
        boolean isSecureTransmission = false;

        if (targetServletUrl.startsWith("https"))
        {
            isSecureTransmission = true;
        }

        InputStream reponseInputStream = null;

        try
        {
            URL objURL = new URL(targetServletUrl);

            HttpURLConnection con = null;

            if (isSecureTransmission)
            {
                if (ignoreSslCertificateWarnings)
                {
                    // code from http://www.tek-tips.com/viewthread.cfm?qid=1041523&page=1
                    try
                    {
                        SSLContext sc = SSLContext.getInstance("SSL");

                        // Create empty HostnameVerifier
                        HostnameVerifier hv = new HostnameVerifier()
                        {
                            public boolean verify(String urlHostName, SSLSession session)
                            {
                                return true;
                            }
                        };

                        // Create a trust manager that does not validate certificate chains
                        TrustManager[] trustAllCerts = new TrustManager[]
                        {
                            new X509TrustManager()
                            {
                                public java.security.cert.X509Certificate[] getAcceptedIssuers()
                                {
                                    return null;
                                }

                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                                {
                                }

                                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                                {
                                }
                            }
                        };
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        SSLSocketFactory sslSocketFactory = sc.getSocketFactory();

                        HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                        HttpsURLConnection.setDefaultHostnameVerifier(hv);

                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                con = (javax.net.ssl.HttpsURLConnection) objURL.openConnection();
            }
            else
            {
                con = (HttpURLConnection) objURL.openConnection();
            }

            // Sets HTTP method
            con.setRequestMethod("POST");

            // Sets contents
            for (Entry<String, String> entry : requestProperties.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }

            con.setDoOutput(true);
            // default for setDoInput is true

            // Sends a request to the server
            OutputStream out = new BufferedOutputStream((con.getOutputStream()));

            byte[] buf = new byte[2048];
            int length;
            while ((length = requestContentAsStream.read(buf)) != -1)
            {
                out.write(buf, 0, length);
            }
            out.flush();
            out.close();

            reponseInputStream = con.getInputStream();
        }
        catch (IOException e)
        {
            throw e;
        }

        return reponseInputStream;
    }

    /**
     * Sends a SOAP XML request to the service servlet located at targetServletUrl
     * (e.g. "http://arabian:8080/clientServlet/ClientServlet").  Returns the response of
     * the servlet as an InputStream.  Returned as an InputStrem rather than as an XmlEntity
     * to allow for validation to be specified using XmlConverter.
     *
     * @param targetServletUrl       Url of servlet to which request is being sent (e.g. "http://arabian:8080/clientServlet/ClientServlet").
     * @param soapRequestAsXmlEntity The request as reprsented by the root XmlEntity.
     * @param soapAction             e.g. "http://tempuri.org/CBRS1/CBRSRequests/Procedures"
     * @param isWholRequestRatherThanBodyPortionOnly
     *                               If true, expect <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body>...
     *                               If false, expect <soap:Body>...
     * @return Response of the service servlet returned as an InputStream.
     * @throws IOException
     */
    public InputStream sendSoapRequestGetResponse(String targetServletUrl, XmlEntity soapRequestAsXmlEntity, String soapAction, boolean isWholRequestRatherThanBodyPortionOnly) throws IOException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "text/xml; charset=utf-8");
        requestProperties.put("SOAPAction", soapAction);

        XmlEntity fullSoapRequestEntity = null;
        if (isWholRequestRatherThanBodyPortionOnly)
        {
            fullSoapRequestEntity = soapRequestAsXmlEntity;
        }
        else
        {
            fullSoapRequestEntity = new XmlEntity("soap:Envelope");
            fullSoapRequestEntity.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            fullSoapRequestEntity.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            fullSoapRequestEntity.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
            fullSoapRequestEntity.addChild(soapRequestAsXmlEntity);
        }

        InputStream responseInputStream = null;
        try
        {
            responseInputStream = sendRequestGetResponse(targetServletUrl, XmlConverter.getInstance().marshalXml(fullSoapRequestEntity), requestProperties);
        }
        catch (IOException e)
        {
            throw e;
        }

        return responseInputStream;
    }

    /**
     * Sends an XML request to the service servlet located at targetServletUrl
     * (e.g. "http://arabian:8080/clientServlet/ClientServlet").  Returns the response of
     * the servlet as an InputStream.  Returned as an InputStrem rather than as an XmlEntity
     * to allow for validation to be specified using XmlConverter.
     *
     * @param targetServletUrl   Url of servlet to which request is being sent (e.g. "http://arabian:8080/clientServlet/ClientServlet").
     * @param requestAsXmlEntity The request as reprsented by the root XmlEntity.
     * @return Response of the service servlet returned as an InputStream.
     * @throws IOException
     */
    public InputStream sendXmlRequestGetResponse(String targetServletUrl, XmlEntity requestAsXmlEntity) throws IOException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "text/xml; charset=utf-8");
        InputStream responseInputStream = null;
        try
        {
            responseInputStream = sendRequestGetResponse(targetServletUrl, XmlConverter.getInstance().marshalXml(requestAsXmlEntity), requestProperties);
        }
        catch (IOException e)
        {
            throw e;
        }

        return responseInputStream;
    }

    /**
     * Send XML request as string, get XML response back as string.
     *
     * @param targetServletUrl
     * @param xmlRequest
     * @return
     */
    public String sendXmlRequestGetResponse(String targetServletUrl, String xmlRequest) throws IOException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "text/xml; charset=utf-8");

        InputStream responseInputStream = null;
        try
        {
            responseInputStream = sendRequestGetResponse(targetServletUrl, StreamUtility.getInstance().getInputStreamFromString(xmlRequest), requestProperties);
        }
        catch (IOException e)
        {
            throw e;
        }
        return StreamUtility.getInstance().getStringFromInputStream_depleteStream(responseInputStream);
    }

    /**
     * Send HTTP POST request, get HTML response back as string.
     * <p/>
     * NOTE: THE ACCURACY/FUNCTIONALITY OF THIS METHOD HAS NOT YET BEEN VERIFIED.
     * <p/>
     * Modeled after information from http://www.javaworld.com/javaworld/javatips/jw-javatip34.html.
     *
     * @param targetServletUrl
     * @param parameters    Contains ArrayList of Parameter objects to post to targetServletUrl
     * @return
     */
    public String sendPostRequestGetResponse(String targetServletUrl, Map<String, String> parameters) throws IOException
    {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/x-www-form-urlencoded");

        StringBuilder postParams = new StringBuilder("");

        int count = 0;
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            if (count > 0) {
                postParams.append("&");
            }
            postParams.append(EncoderUtility.encodeStringAsUrlString(param.getKey()));
            postParams.append("=");
            postParams.append(EncoderUtility.encodeStringAsUrlString(param.getValue()));
        }

        log.debug(postParams);

        InputStream responseInputStream = null;
        try
        {
            responseInputStream = sendRequestGetResponse(targetServletUrl, StreamUtility.getInstance().getInputStreamFromString(postParams.toString()), requestProperties);
        }
        catch (IOException e)
        {
            throw e;
        }
        return StreamUtility.getInstance().getStringFromInputStream_depleteStream(responseInputStream);
    }

    /**
     * Sends an XML response, where the response is encapsulated in the XmlEntity object.
     *
     * @param response                   The response object available in this servlet.
     * @param responseContentAsXmlEntity The reponse content represented as the root XmlEntity.
     */
    public void sendXmlResponse(HttpServletResponse response, XmlEntity responseContentAsXmlEntity) throws IOException
    {
        try
        {
            sendResponse(response, "text/xml; charset=utf-8", XmlConverter.getInstance().marshalXml(responseContentAsXmlEntity));
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * Sends a PDF response, where the response PDF is contained in the inputStream
     *
     * @param response         The response object available in this servlet.
     * @param pdfAsInputStream The PDF as an InputStream.
     */
    public void sendPdfResponse(HttpServletResponse response, InputStream pdfAsInputStream) throws IOException
    {
        try
        {
            sendResponse(response, "application/pdf", pdfAsInputStream);
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    public void sendPdfResponse(HttpServletResponse response, ByteArrayOutputStream pdfAsByteArrayOutputStream) throws IOException
    {
        InputStream pdfAsInputStream = new ByteArrayInputStream(pdfAsByteArrayOutputStream.toByteArray());
        try
        {
            sendResponse(response, "application/pdf", pdfAsInputStream);
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * Sends an HTML response.
     *
     * @param response     The reponse object available in the servlet.
     * @param htmlAsString The HTML response as one long string.
     */
    public void sendHtmlResponse(HttpServletResponse response, String htmlAsString) throws IOException
    {
        InputStream htmlAsInputStream = null;
        htmlAsInputStream = new ByteArrayInputStream(htmlAsString.getBytes());
        try
        {
            sendResponse(response, "text/html", htmlAsInputStream);
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    public void sendHtmlResponse(HttpServletResponse response, ByteArrayOutputStream htmlAsByteArrayOutputStream) throws IOException
    {
        InputStream htmlAsInputStream = new ByteArrayInputStream(htmlAsByteArrayOutputStream.toByteArray());
        try
        {
            sendResponse(response, "text/html", htmlAsInputStream);
        }
        catch (IOException e)
        {
            throw e;
        }
    }
}