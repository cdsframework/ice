/**
 * The cdsframework support client aims at making vMR generation easier.
 * <p>
 * Copyright 2016 HLN Consulting, LLC
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For more information about the this software, see https://www.hln.com/services/open-source/ or send
 * correspondence to scm@cdsframework.org.
 */
package org.cdsframework.rest.opencds.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.omg.dss.ObjectFactory;
import org.omg.dss.evaluation.requestresponse.EvaluationResponse;
import org.xml.sax.ContentHandler;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.JAXBResult;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * A static utility class for dealing with marshaling and un-marshaling objects.
 *
 * @author HLN Consulting, LLC
 */
@Slf4j
@UtilityClass
public class MarshalUtils
{
    /**
     * static logger
     */
    private static final ConcurrentHashMap<String, JAXBContext> jaxbContextMap = new ConcurrentHashMap<>();
    private static final String dssObjectFactoryPackageName = ObjectFactory.class.getPackageName();
    private static final String dssObjectFactoryPackageNamePrefix = dssObjectFactoryPackageName + ".";

    /**
     * Get the jaxb context for the supplied context path.
     */
    private static JAXBContext getJAXBContext(final String contextPath) throws JAXBException
    {
        if (contextPath == null)
            throw new IllegalArgumentException("contextPath cannot be null.");

        try
        {
            return jaxbContextMap.computeIfAbsent(contextPath, key ->
            {
                try
                {
                    return JAXBContext.newInstance(contextPath);
                }
                catch (final JAXBException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (final RuntimeException e)
        {
            final Throwable cause = e.getCause();
            if (cause instanceof final JAXBException jaxbException)
                throw jaxbException;

            throw e;
        }
    }

    private static Marshaller getMarshaller(final Object jaxbElement) throws JAXBException
    {
        if (jaxbElement == null)
            throw new IllegalArgumentException("jaxbElement cannot be null.");

        return getMarshaller(jaxbElement.getClass());
    }

    private static Marshaller getMarshaller(final Class<?> klass) throws JAXBException
    {
        final Marshaller result;
        if (klass == null)
            throw new IllegalArgumentException("klass cannot be null.");

        final String contextPath = getContextPath(klass);
        log.debug("contextPath={}", contextPath);
        result = getJAXBContext(contextPath).createMarshaller();
        result.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return result;
    }

    private static Unmarshaller getUnmarshaller(final Class<?> klass) throws JAXBException
    {
        final Unmarshaller result;
        if (klass == null)
            throw new IllegalArgumentException("klass cannot be null.");

        final String contextPath = getContextPath(klass);
        result = getJAXBContext(contextPath).createUnmarshaller();
        return result;
    }

    /**
     * Marshal an object to a supplied destination.
     */
    public static void marshal(Object jaxbElement, final Object dst, final Schema schema) throws JAXBException
    {
        final Marshaller marshaller = getMarshaller(jaxbElement);
        marshaller.setSchema(schema);

        if (jaxbElement instanceof final EvaluationResponse evaluationResponse)
            jaxbElement = new ObjectFactory().createEvaluationResponse(evaluationResponse);

        switch (dst)
        {
            case final ContentHandler contentHandler -> marshaller.marshal(jaxbElement, contentHandler);
            case final OutputStream outputStream -> marshaller.marshal(jaxbElement, outputStream);
            case null, default -> throw new IllegalArgumentException("Unsupported dst type: " + dst);
        }
    }

    /**
     * Marshal an object to an OutputStream.
     */
    public static void marshal(final Object jaxbElement, final OutputStream os) throws JAXBException
    {
        marshal(jaxbElement, os, null);
    }

    /**
     * Un-marshal an object from an InputStream.
     */
    public static <S> S unmarshal(final InputStream inputStream, final Class<S> returnType)
            throws JAXBException, TransformerException
    {
        return unmarshal(inputStream, true, returnType);
    }

    /**
     * Un-marshal an object from an InputStream after transforming the XML with the
     * supplied XSLT.
     */
    public static <S> S unmarshal(final InputStream inputStream, final InputStream xslInputStream, final Class<S> returnType)
            throws JAXBException, TransformerException
    {
        return unmarshal(inputStream, true, xslInputStream, returnType);
    }

    /**
     * Un-marshal an object from an InputStream with the option to ignore
     * namespaces.
     */
    public static <S> S unmarshal(final InputStream inputStream, final boolean namespaceAware, final Class<S> returnType)
            throws JAXBException, TransformerException
    {
        return unmarshal(inputStream, namespaceAware, null, returnType);
    }

    /**
     * Un-marshal an object from an InputStream after transforming the XML with the
     * supplied XSLT (optional) with the option to ignore namespaces.
     */
    @SuppressWarnings("unchecked")
    public static <S> S unmarshal(final InputStream inputStream, final boolean namespaceAware, final InputStream xslInputStream,
            final Class<S> returnType) throws JAXBException, TransformerException
    {
        final S result;
        final Unmarshaller unmarshaller = getUnmarshaller(returnType);
        if (namespaceAware && xslInputStream == null)
        {
            final Object unmarshalledObject = unmarshaller.unmarshal(inputStream);
            if (unmarshalledObject instanceof JAXBElement)
                result = (S) ((JAXBElement<?>) unmarshalledObject).getValue();
            else
                result = (S) unmarshalledObject;
        }
        else
        {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            InputStream localTransform = xslInputStream;
            if (!namespaceAware && xslInputStream == null)
                localTransform = MarshalUtils.class.getClassLoader().getResourceAsStream("nsrm.xsl");
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
     */
    public static byte[] marshalObject(final Object dataObject) throws JAXBException
    {
        if (dataObject == null)
            throw new IllegalArgumentException("dataObject cannot be null.");

        final Marshaller marshaller = getMarshaller(dataObject.getClass());
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshal(dataObject, stream);
        return stream.toByteArray();
    }

    /**
     * Un-marshal an object from an InputStream to the supplied object type.
     */
    @SuppressWarnings("unchecked")
    public static <S> S unmarshalObject(final InputStream inputStream, final Class<S> cdsObjectClass) throws JAXBException
    {
        return (S) getUnmarshaller(cdsObjectClass).unmarshal(inputStream);
    }

    /**
     * Returns the context path to be used for creating the jaxb context.
     */
    public static String getContextPath(final Class<?> klass)
    {
        if (klass == null)
            throw new IllegalArgumentException("The class cannot be null.");

        final String packageName = klass.getPackageName();
        if (packageName.equals(dssObjectFactoryPackageName) || packageName.startsWith(dssObjectFactoryPackageNamePrefix))
            return dssObjectFactoryPackageName;

        return packageName;
    }
}
