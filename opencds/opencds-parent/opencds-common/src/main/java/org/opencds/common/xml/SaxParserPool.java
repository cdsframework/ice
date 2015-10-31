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

import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * <p>SaxParserPool contains SaxParser objects.  A SaxParserPool
 * can be specified to contain parsers that require XML validation
 * with XML Schemas, and those in which validation has been turned
 * off.</p>
 * <p> After checking out a SaxParser, the user needs to return the
 * parser back into the pool.</p>
 * <p/>
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class SaxParserPool extends Object
{
    private Hashtable<SAXParser, Boolean> myParsers;        // contains SAXParser objects
    private int myIncrement;            // number of parsers to increase by if run out
    private boolean myShouldValidate;   // whether SAXParser should have validation with XML Schemas turned on

    /**
     * Create a SaxParserPool.
     *
     * @param initialParsers Number of parsers that the pool should initially contain.
     * @param increment      How many parsers should be added if all parsers are occupied.
     * @param validate       Whether XML Schema validation should occur (true) or not occur (false).
     */
    public SaxParserPool(int initialParsers, int increment, boolean validate)
    {
        myParsers = new Hashtable<SAXParser, Boolean>();
        myIncrement = increment;
        myShouldValidate = validate;

        // Put our pool of Connections in the Hashtable
        // The FALSE value indicates they're unused

        for (int i = 0; i < initialParsers; i++)
        {
            addParser();
        }
    }

    /**
     * Add a parser to the pool.  Necessary parameters are in private variables.
     */
    protected void addParser()
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            if (myShouldValidate)
            {
                factory.setNamespaceAware(true);
                factory.setValidating(true);
            }
            else
            {
                factory.setNamespaceAware(false);
                factory.setValidating(false);
            }            
            factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);

            myParsers.put(factory.newSAXParser(), Boolean.FALSE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Retrieve a parser from the pool.
     *
     * @return A SaxParser object from the pool.
     */
    public SAXParser getParser()
    {
        SAXParser parser = null;

        Enumeration<SAXParser> parsers = myParsers.keys();

        synchronized (myParsers) // locks myParsers to one thread access at a time
        {
            while (parsers.hasMoreElements())
            {
                parser = (SAXParser) parsers.nextElement();

                Boolean b = (Boolean) myParsers.get(parser);

                if (b == Boolean.FALSE)
                {
                    // So we found an unused parser

                    // Update the Hashtable to show this one's taken
                    myParsers.put(parser, Boolean.TRUE);
                }
                // Return the parser
                return parser;
            }
        }

        // If we get here, there were no free parsers
        // We've got to make more.

        for (int i = 0; i < myIncrement; i++)
        {
            addParser();
        }

        // Recurse to get one of the new parsers.
        return getParser();
    }

    /**
     * Return the parser back into the pool.
     *
     * @param returnedParser The SAXParser to return to the pool.
     */
    public void returnParser(SAXParser returnedParser)
    {
        SAXParser parser;

        Enumeration<SAXParser> parsers = myParsers.keys();

        while (parsers.hasMoreElements())
        {
            parser = (SAXParser) parsers.nextElement();

            if (parser == returnedParser)
            {
                myParsers.put(parser, Boolean.FALSE);

                break;
            }
        }
    }
}


