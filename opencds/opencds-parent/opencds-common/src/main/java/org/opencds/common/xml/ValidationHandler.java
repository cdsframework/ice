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

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * <p>ValidationHandler is used to throw an exception if there
 * is an error reported during parsing.  It is intended to be
 * used SOLELY for validating an XML document with XML Schemas.</p>
 * <p/>
 * 
 * @author Kensaku Kawamoto
 * @version 1.0
 */
public class ValidationHandler extends DefaultHandler
{
    /**
     * Creates a new ValidationHandler.
     */
    public ValidationHandler()
    {
    }

    /**
     * Processing when a parsing error is found.  Currently, throws an exception.
     *
     * @param e
     * @throws org.xml.sax.SAXParseException
     */
    public void error(SAXParseException e) throws SAXParseException
    {
        throw e;
    }
}
