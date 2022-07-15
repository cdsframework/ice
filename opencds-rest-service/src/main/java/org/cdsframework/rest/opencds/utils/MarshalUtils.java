/**
 * The cdsframework support client aims at making vMR generation easier.
 *
 * Copyright 2016 HLN Consulting, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information about the this software, see https://www.hln.com/services/open-source/ or send
 * correspondence to scm@cdsframework.org.
 */
package org.cdsframework.rest.opencds.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.omg.dss.evaluation.requestresponse.ObjectFactory;
import org.xml.sax.ContentHandler;

/**
 * A static utility class for dealing with marshaling and un-marshaling objects.
 *
 * @author HLN Consulting, LLC
 */
public class MarshalUtils {

    /**
     * static logger
     */
    private static final Log log = LogFactory.getLog(MarshalUtils.class);
    private static final Map<String, JAXBContext> jaxbContextMap = new HashMap<String, JAXBContext>();

    /**
     * Get the jaxb context for the package name.
     *
     * @param classPackageName
     * @return
     * @throws CdsException
     */
    private static JAXBContext getJAXBContext(final String classPackageName) throws JAXBException {
        JAXBContext jaxbContext;
        if (classPackageName == null) {
            throw new IllegalArgumentException("classPackageName cannot be null.");
        }
        if (!jaxbContextMap.containsKey(classPackageName)) {
            jaxbContext = JAXBContext.newInstance(classPackageName);
            jaxbContextMap.put(classPackageName, jaxbContext);
        } else {
            jaxbContext = jaxbContextMap.get(classPackageName);
        }
        return jaxbContext;
    }

    private static Marshaller getMarshaller(final Object jaxbElement) throws JAXBException {
        if (jaxbElement == null) {
            throw new IllegalArgumentException("jaxbElement cannot be null.");
        }
        return getMarshaller(jaxbElement.getClass());
    }

    private static Marshaller getMarshaller(final Class klass) throws JAXBException {
        Marshaller result;
        if (klass == null) {
            throw new IllegalArgumentException("klass cannot be null.");
        }
        final String classPackageName = getClassPackageName(klass);
        log.debug("classPackageName=" + classPackageName);
        result = getJAXBContext(classPackageName).createMarshaller();
        result.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return result;
    }

    private static Unmarshaller getUnmarshaller(final Class klass) throws JAXBException {
        Unmarshaller result;
        if (klass == null) {
            throw new IllegalArgumentException("klass cannot be null.");
        }
        final String classPackageName = getClassPackageName(klass);
        result = getJAXBContext(classPackageName).createUnmarshaller();
        return result;
    }

    /**
     * Marshal an object to a supplied destination.
     *
     * @param jaxbElement
     * @param dst
     * @param schema
     * @throws javax.xml.bind.JAXBException
     */
    public static void marshal(Object jaxbElement, final Object dst, final Schema schema) throws JAXBException {
        final Marshaller marshaller = getMarshaller(jaxbElement);
        marshaller.setSchema(schema);
        if (jaxbElement instanceof EvaluationResponse) {
            jaxbElement = new ObjectFactory().createEvaluationResponse((EvaluationResponse) jaxbElement);
        }
        if (dst instanceof ContentHandler) {
            marshaller.marshal(jaxbElement, (ContentHandler) dst);
        } else if (dst instanceof OutputStream) {
            marshaller.marshal(jaxbElement, (OutputStream) dst);
        } else {
            throw new IllegalArgumentException("Unsupported dst type: " + dst);
        }
    }

    /**
     * Marshal an object to an OutputStream.
     *
     * @param jaxbElement
     * @param os
     * @throws javax.xml.bind.JAXBException
     */
    public static void marshal(final Object jaxbElement, final OutputStream os) throws JAXBException {
        marshal(jaxbElement, os, null);
    }

    /**
     * Un-marshal an object from an InputStream.
     *
     * @param <S>
     * @param inputStream
     * @param returnType
     * @return
     * @throws javax.xml.bind.JAXBException
     * @throws javax.xml.transform.TransformerException
     */
    public static <S> S unmarshal(final InputStream inputStream, final Class<S> returnType)
            throws JAXBException, TransformerException {
        return unmarshal(inputStream, true, returnType);
    }

    /**
     * Un-marshal an object from an InputStream after transforming the XML with the
     * supplied XSLT.
     *
     * @param <S>
     * @param inputStream
     * @param xslInputStream
     * @param returnType
     * @return
     * @throws javax.xml.bind.JAXBException
     * @throws javax.xml.transform.TransformerException
     */
    public static <S> S unmarshal(final InputStream inputStream, final InputStream xslInputStream,
            final Class<S> returnType) throws JAXBException, TransformerException {
        return unmarshal(inputStream, true, xslInputStream, returnType);
    }

    /**
     * Un-marshal an object from an InputStream with the option to ignore
     * namespaces.
     *
     * @param <S>
     * @param inputStream
     * @param namespaceAware
     * @param returnType
     * @return
     * @throws javax.xml.bind.JAXBException
     * @throws javax.xml.transform.TransformerException
     */
    public static <S> S unmarshal(final InputStream inputStream, final boolean namespaceAware,
            final Class<S> returnType) throws JAXBException, TransformerException {
        return unmarshal(inputStream, namespaceAware, null, returnType);
    }

    /**
     * Un-marshal an object from an InputStream after transforming the XML with the
     * supplied XSLT (optional) with the option to ignore namespaces.
     *
     * @param <S>
     * @param inputStream
     * @param namespaceAware
     * @param xslInputStream
     * @param returnType
     * @return
     * @throws javax.xml.bind.JAXBException
     * @throws javax.xml.transform.TransformerConfigurationException
     */
    public static <S> S unmarshal(final InputStream inputStream, final boolean namespaceAware,
            final InputStream xslInputStream, final Class<S> returnType)
            throws JAXBException, TransformerConfigurationException, TransformerException {
        S result;
        final Unmarshaller unmarshaller = getUnmarshaller(returnType);
        if (namespaceAware && xslInputStream == null) {
            final Object unmarshalledObject = unmarshaller.unmarshal(inputStream);
            if (unmarshalledObject instanceof JAXBElement) {
                result = (S) ((JAXBElement) unmarshalledObject).getValue();
            } else {
                result = (S) unmarshalledObject;
            }
        } else {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            InputStream localTransform = xslInputStream;
            if (!namespaceAware && xslInputStream == null) {
                localTransform = MarshalUtils.class.getClassLoader().getResourceAsStream("nsrm.xsl");
            }
            final StreamSource xslStreamSource = new StreamSource(localTransform);
            final Transformer transformer = transformerFactory.newTransformer(xslStreamSource);
            final StreamSource xmlStreamSource = new StreamSource(inputStream);
            final JAXBContext jc = JAXBContext.newInstance(returnType);
            final JAXBResult jaxbResult = new JAXBResult(jc);
            transformer.transform(xmlStreamSource, jaxbResult);
            result = (S) jaxbResult.getResult();
        }
        return result;
    }

    /**
     * Marshal an object to a byte array.
     *
     * @param dataObject
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    public static byte[] marshalObject(final Object dataObject) throws JAXBException {
        byte[] result;
        if (dataObject == null) {
            throw new IllegalArgumentException("dataObject cannot be null.");
        }
        final Marshaller marshaller = getMarshaller(dataObject.getClass());
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshal(dataObject, stream);
        result = stream.toByteArray();
        return result;
    }

    /**
     * Un-marshal an object from an InputStream to the supplied object type.
     *
     * @param <S>
     * @param inputStream
     * @param cdsObjectClass
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    public static <S> S unmarshalObject(final InputStream inputStream, final Class<S> cdsObjectClass)
            throws JAXBException {
        S result;
        final Unmarshaller unmarshaller = getUnmarshaller(cdsObjectClass);
        result = (S) unmarshaller.unmarshal(inputStream);
        return result;
    }

    /**
     * Returns the package name of a class.
     *
     * @param klass
     * @return
     */
    public static String getClassPackageName(final Class klass) {
        String result;
        if (klass == null) {
            throw new IllegalArgumentException("The class cannot be null.");
        }
        final String canonicalName = klass.getCanonicalName();
        result = canonicalName.substring(0, canonicalName.lastIndexOf("."));
        return result;
    }
}
