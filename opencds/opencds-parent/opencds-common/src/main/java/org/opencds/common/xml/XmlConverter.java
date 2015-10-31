/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.common.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParser;

import org.opencds.common.utilities.DateUtility;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * <p>XmlConverter is used to transform an XML document as
 * specified in an InputSource into an XML document as
 * represented by an XmlEntity.  XmlConverter also can
 * be used to transform an XML document as represented
 * by an XmlEntity into an XML document as represented
 * by an input stream.</p>
 * <p> The converter uses SAX for parsing instead of
 * DOM for memory and processor efficiency.
 * <p/>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class XmlConverter extends Object
{
    protected SaxParserPool myValidationSaxParserPool;      // pool of validating SAXParser's
    protected SaxParserPool myNonValidationSaxParserPool;   // pool of non-validating SAXParser's
    protected static XmlConverter myInstance;    //singleton instance

    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    protected DateUtility myDateUtility;

    private final String myXmlEncodingCharSet = "UTF-8"; // NOTE: this should be the same charset specified in XmlEntity.getAsConciseXmlStringBuffer

    /**
     * Get an instance of the XmlConverter.
     *
     * @return An instance of the XmlConverter.
     */
    public static synchronized XmlConverter getInstance()
    {
        if (myInstance == null)
        {
            myInstance = new XmlConverter();
        }
        return myInstance;
    }

    /**
     * Constructs a new XmlConverter.  XmlConverter keeps a pool of SAXParsers on
     * hand (number determined at time of XmlConverter creation; increased as needed).
     * Each SAXParser is locked in when parsing an incoming XML file, because the SAXParser
     * is NOT guaratneed to be thread-proof per Java.  Moreover, in my instantiation,
     * the SAXParser uses a SAXHandler which is also not thread-proof (because it maintains
     * an in-memory object model during parsing). Bottom line: a SAXParser should be used by
     * only one thread at a time.
     */
    protected XmlConverter()
    {
        initialize();
    }

    /**
     * Initialize the XmlConverter.
     */
    protected void initialize()
    {
        int initialPoolSize = 20;
        int incrementSize = 5;

        myValidationSaxParserPool = new SaxParserPool(initialPoolSize, incrementSize, true);
        myNonValidationSaxParserPool = new SaxParserPool(initialPoolSize, incrementSize, false);

        myDateUtility = DateUtility.getInstance();
    }

    /**
     * Transform the XML object, represented by the root XmlEntity, into an InputStream
     * that contains the XML (including the XML header).  Comments are removed, and
     * no excess white spaces are included.  Processing instructions are maintained.
     *
     * @param rootEntity The XmlEntity that is the root of the XML object
     * @return The XML object represented as an InputStream.
     */
    public InputStream marshalXml(XmlEntity rootEntity)
    {
        String xmlAsString = rootEntity.getAsConciseXmlString(false, true);
        InputStream xmlInputStream = null;
        try
        {
            xmlInputStream = new ByteArrayInputStream(xmlAsString.getBytes(myXmlEncodingCharSet));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return xmlInputStream;
    }

    /**
     * Returns the root XmlEntity of the XML, with all children populated.
     * NOT synchronized because a unique pair of saxParser and saxHandler is
     * obtained for conducting an unmarshalling process.
     *
     * @param xmlSource          The source of the XML (eg new InputSource("fileLocation")).
     * @param validateWithSchema Whether XML should be validated against XML Schema (NOTE: cannot validate
     *                           using DTDs at this point).
     * @param xmlSchemaLocation  Place of the XML Schema for validation purposes
     * @return Root XmlEntity of the XML; returns null if unmarshalling process
     *         throws an exception.
     */
    public synchronized XmlEntity unmarshalXml(InputSource xmlSource, boolean validateWithSchema, String xmlSchemaLocation) throws org.xml.sax.SAXParseException
    {
        XmlEntity rootEntity = new XmlEntity();

        // Create document handler
        SaxHandler handler = new SaxHandler();

        SAXParser saxParser = null;

        try
        {
            if (validateWithSchema)
            {
                // get validating SAXParser, and specify where the validation XML Schema is
                saxParser = myValidationSaxParserPool.getParser();
                saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(xmlSchemaLocation));
            }
            else
            {
                // get the non-validating SAXParser
                saxParser = myNonValidationSaxParserPool.getParser();
            }

            saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", handler);

            // parse the XML document
            saxParser.parse(xmlSource, handler);

            // set the return variable
            rootEntity = handler.getRootEntity();

        }
        catch (SAXNotRecognizedException e)
        {
            e.printStackTrace();
            rootEntity = null;
        }
        catch (SAXNotSupportedException e)
        {
            e.printStackTrace();
            rootEntity = null;
        }
        catch (java.io.IOException e)
        {
            System.err.println("XML Parsing encountered following IOException:");
            System.out.println(e.getMessage());
            e.printStackTrace();
            rootEntity = null;
        }
        catch (org.xml.sax.SAXParseException e)
        {
            System.out.println("SAX Parsing encountered following SAXParseException:");
            System.out.println(e.getMessage());
            throw e;
        }
        catch (org.xml.sax.SAXException e)
        {
            e.printStackTrace();
            rootEntity = null;
        }

        // return the parser to the pool
        if (validateWithSchema)
        {
            myValidationSaxParserPool.returnParser(saxParser);
        }
        else
        {
            myNonValidationSaxParserPool.returnParser(saxParser);
        }

        return rootEntity;
    }

    /**
     * Alternate entry point to function of same name.
     *
     * @param xmlAsString        May or may not have the <?xml ...> header tag (e.g. <?xml version="1.0" encoding="UTF-8"?>). If not present, will be added automatically.
     * @param validateWithSchema
     * @param xmlSchemaLocation
     * @return
     */
    public XmlEntity unmarshalXml(String xmlAsString, boolean validateWithSchema, String xmlSchemaLocation) throws org.xml.sax.SAXParseException
    {
        InputStream xmlInputStream = null;
        if (!xmlAsString.startsWith("<?xml"))
        {
            xmlAsString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlAsString;
        }

        try
        {
            xmlInputStream = new ByteArrayInputStream(xmlAsString.getBytes(myXmlEncodingCharSet));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return unmarshalXml(new InputSource(xmlInputStream), validateWithSchema, xmlSchemaLocation);
    }
}
