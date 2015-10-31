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

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.SAXParser;
import java.io.File;


/**
 * <p>XmlValidator is used to validate an XML document
 * using XML Schemas.  It can be called multiple times
 * to validate the same XML document multiple times.</p>
 * <p> The input XML document can be represented
 * as an InputStream, InputSource, or XmlEntity root.
 * Note that if the XML document is represented as an
 * XmlEntity root, that the XML must be unmarshalled first
 * into an InputStream; thus, validating an XmlEntity root
 * is not as efficient as validating an InpuStream or
 * InputSource.</p>
 * <p/>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class XmlValidator extends Object
{
    protected SaxParserPool myValidationSaxParserPool;      // pool of validating SAXParser's
    protected static XmlValidator myInstance = new XmlValidator();    //singleton instance

    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /**
     * Get an instance of the XmlValidator.
     *
     * @return An instance of the XmlValidator.
     */
    public static synchronized XmlValidator getInstance()
    {
        return myInstance;
    }

    /**
     * Constructs a new XmlValidator.  XmlValidator keeps a pool of SAXParsers on
     * hand (number determined at time of XmlValidator creation; increased as needed).
     * Each SAXParser is locked in when parsing an incoming XML file, because the SAXParser
     * is NOT guaratneed to be thread-proof per Java.
     */
    protected XmlValidator()
    {
        initialize();
    }

    /**
     * Initialize the XmlValidator.
     */
    protected void initialize()
    {
        int initialPoolSize = 20;
        int incrementSize = 5;

        myValidationSaxParserPool = new SaxParserPool(initialPoolSize, incrementSize, true);
    }


    /**
     * Determines whether the XML document is valid according to the XML Schema specified.
     * @param xmlSource             The XML document being validated.
     * @param xmlSchemaLocation     The location of the XML Schema.
     * @return      True if valid; false if invalid or if error encountered during processing.
     */
    public boolean isValidPerSchema(InputSource xmlSource, String xmlSchemaLocation)
    {
        // Create validation handler
        ValidationHandler handler = new ValidationHandler();

        SAXParser saxParser = null;

        try
        {
            // get validating SAXParser, and specify where the validation XML Schema is
            saxParser = myValidationSaxParserPool.getParser();
            saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(xmlSchemaLocation));

            // parse the XML document
            saxParser.parse(xmlSource, handler);

        }
        catch (SAXNotRecognizedException e)
        {
            myValidationSaxParserPool.returnParser(saxParser);
            return false;
        }
        catch (SAXNotSupportedException e)
        {
            myValidationSaxParserPool.returnParser(saxParser);
            return false;
        }
        catch (java.io.IOException e)
        {
            myValidationSaxParserPool.returnParser(saxParser);
            return false;
        }
        catch (org.xml.sax.SAXParseException e)
        {
            myValidationSaxParserPool.returnParser(saxParser);
            return false;
        }
        catch (org.xml.sax.SAXException e)
        {
            myValidationSaxParserPool.returnParser(saxParser);
            return false;
        }

        // return the parser
        myValidationSaxParserPool.returnParser(saxParser);

        // if got here, no exceptions encountered, so return true
        return true;
    }

    /**
     * Determines whether the XML document is valid according to the XML Schema specified.  Note
     * that validating in this manner is inefficient, because the rootEntity is unmarshalled
     * into an InputStream during the process.
     * @param rootEntity            The root of the XML document being validated.
     * @param xmlSchemaLocation     The location of the XML Schema.
     * @return      True if valid; false if invalid or if error encountered during processing.
     */
    public boolean isValidPerSchema(XmlEntity rootEntity, String xmlSchemaLocation)
    {
        InputSource xmlAsInputSource = new InputSource(XmlConverter.getInstance().marshalXml(rootEntity));

        return isValidPerSchema(xmlAsInputSource, xmlSchemaLocation);
    }

}
