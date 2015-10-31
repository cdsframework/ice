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

import org.opencds.common.utilities.StringUtility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;
import java.util.ArrayList;


/**
 * <p>SaxHandler is used to handle SAX events.</p>
 * <p>The SaxHandler builds up a model of the XML document using
 * XmlEntity objects, in a format similar to DOM.  It also
 * throws an exception if XML Schema validation is turned on
 * and the incoming XML file does not meet the criteria of
 * the Schema.</p>
 * <p>Note that a given SaxHandler instance SHOULD ONLY BE
 * USED BY ONE SAXParser, as it is not thread-safe, due to
 * the maintenance of an internal memory representation of
 * the XML document.</P>
 * <p/>
 * 
 * @author Kensaku Kawamoto
 * @version 1.01
 */
public class SaxHandler extends DefaultHandler implements LexicalHandler
        // LexicalHandler implementation required for CDATA listening
{
    protected XmlEntity myRootEntity;        // the root XmlEntity, to be returned after completion of parsing
    protected StringBuffer myValueBuffer;    // buffer to hold values, as the parser divides text in values sections into multiple pieces
    protected ArrayList<String> myCommentsToAdd;     // String comments to be added to following entity
    protected ArrayList<XmlProcessingInstruction> myProcessingInstructionsToAdd; // ProcessingInstruction objects to be added to the following entity
    protected Stack<XmlEntity> myEntityStack;           // stack holding entities; used as temporary holding place of XML entities not placed into the custom object model
    protected boolean myIsInCDATA;           // identifies whether currently in CDATA section

    /**
     * Creates a new SaxHandler.
     */
    public SaxHandler()
    {
        myRootEntity = new XmlEntity();
        myValueBuffer = new StringBuffer(200); // initial capacity of 200 characters
        myCommentsToAdd = new ArrayList<String>();
        myProcessingInstructionsToAdd = new ArrayList<XmlProcessingInstruction>();
        myEntityStack = new Stack<XmlEntity>();
        myIsInCDATA = false;
    }

    /**
     * Gets the parsed XML document represented as nested XmlEntity objects.
     *
     * @return The root XmlEntity object.
     */
    public XmlEntity getRootEntity()
    {
        return myRootEntity;
    }

    /**
     * Processing when document starts; does nothing.
     *
     * @throws SAXException
     */
    public void startDocument() throws SAXException
    {
        // do nothing
    }

    /**
     * Processing when new element starts; see actual code for details of processing.
     *
     * @param uri       Uri.
     * @param localpart The local part of the name.
     * @param name      The full name.
     * @param amap      The map of attributes.
     */
    public void startElement(String uri, String localpart,
                             String name, Attributes amap)
    {
        // If a new element is being started, and there are characters in the value buffer, this is
        // a mixed content entry.  Add the value buffer into the mixed content.
        if (! StringUtility.stringBufferContainsOnlyWhiteSpace(myValueBuffer))
        {
            addStringValueToXmlEntity((XmlEntity) myEntityStack.peek(), myValueBuffer.toString());
        }

        // reset the value buffer
        myValueBuffer.setLength(0);

        // create entity
        XmlEntity entity = new XmlEntity();

        // set label
        entity.setLabel(name);

        // add attributes
        for (int i = 0; i < amap.getLength(); i++)
        {
            String attName = amap.getQName(i);
            //String type    = amap.getType(i);
            String attValue = amap.getValue(i);

            entity.addAttribute(attName, attValue);
        }
        
        // add comments if available
        for (int j = 0; j < myCommentsToAdd.size(); j++)
        {
            String comment = (String) myCommentsToAdd.get(j);
            entity.addComment(comment.trim());
        }
        // reset buffer list
        myCommentsToAdd.clear();

        // add processing instructions if available
        for (int k = 0; k < myProcessingInstructionsToAdd.size(); k++)
        {
            XmlProcessingInstruction processingInstruction = (XmlProcessingInstruction) myProcessingInstructionsToAdd.get(k);
            entity.addProcessingInstruction(processingInstruction);
        }
        // reset buffer list
        myProcessingInstructionsToAdd.clear();

        // push to stack
        myEntityStack.push(entity);
    }

    /**
     * Processing when non-ignorable characters are seen.
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        myValueBuffer.append(ch, start, length);
    }

    public void processingInstruction(String target, String data)
    {
        myProcessingInstructionsToAdd.add(new XmlProcessingInstruction(target, data));
    }

    /**
     * Gets contents of myValueBuffer without leading and trailing white spaces
     * (defined as ' ', '\n', '\f', '\t').  Note: should only be called when buffer is complete.
     *
     * @return Contents of myValueBuffer without leading and trailing white spaces.
     */
    protected String getValueBufferMinusWhiteSpace()
            // returns contents of myValueBuffer without leading and trailing white spaces
    {
        String stringToReturn;

        // set these to safe values to begin
        int firstNonWsIndex = 0;
        int lastNonWsIndex = myValueBuffer.length() - 1;

        boolean foundFirstNonWs = false;
        boolean foundLastNonWs = false;

        int index = 0;

        // look for the first non-white space, and establish that index
        while ((!foundFirstNonWs) && (index < myValueBuffer.length()))
        {
            if ((myValueBuffer.charAt(index) == ' ') || (myValueBuffer.charAt(index) == '\n') ||
                    (myValueBuffer.charAt(index) == '\f') || (myValueBuffer.charAt(index) == '\r') ||
                    (myValueBuffer.charAt(index) == '\t'))
            {
                // keep going, this is white space
            }
            else
            {
                firstNonWsIndex = index;

                // terminate loop
                foundFirstNonWs = true;
            }
            // increment index
            index++;
        }

        // if there is only white space, then go no further
        if (!foundFirstNonWs)
        {
            stringToReturn = "";
        }
        else // if there is some non-white space in this buffer
        {
            // set index to last character's index
            index = myValueBuffer.length() - 1;

            // look for the last non-white space, and establish that index
            while ((!foundLastNonWs) && (index >= 0))
            {
                if ((myValueBuffer.charAt(index) == ' ') || (myValueBuffer.charAt(index) == '\n') ||
                        (myValueBuffer.charAt(index) == '\f') || (myValueBuffer.charAt(index) == '\r') ||
                        (myValueBuffer.charAt(index) == '\t'))
                {
                    // keep going, this is white space
                }
                else
                {
                    lastNonWsIndex = index;

                    // terminate loop
                    foundLastNonWs = true;
                }
                // decrement index
                index--;
            }

            // now, truncate the buffer
            stringToReturn = myValueBuffer.substring(firstNonWsIndex, lastNonWsIndex + 1);
        }

        return stringToReturn;
    }

    /**
     * Processing when the end of an element is reached.  See code for details.
     *
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        //String entityValue = getValueBufferMinusWhiteSpace(); // get myValueBuffer with leading and trailing white space removed (note: should only be called when buffer is complete)
        String entityValue = myValueBuffer.toString();
        if (StringUtility.stringContainsOnlyWhiteSpace(entityValue))
        {
            entityValue = "";
        }

        // reset value buffer
        myValueBuffer.setLength(0);

        if (myEntityStack.size() == 1)
        // if this is the end of the root element (i.e. there is only one
        // element left on the stack, and it has ended
        {
            // it is the end of the document; get this root, give it
            // its value, and make it available for retrieval

            myRootEntity = (XmlEntity) myEntityStack.pop();
            myRootEntity.setValueIsCDATA(myIsInCDATA);

            addStringValueToXmlEntity(myRootEntity, entityValue);
        }
        else
        {
            // pop the most recent entity
            XmlEntity mostRecentEntity = (XmlEntity) myEntityStack.pop();

            // give this entity its value
            addStringValueToXmlEntity(mostRecentEntity, entityValue);
            mostRecentEntity.setValueIsCDATA(myIsInCDATA);

            XmlEntity secondMostRecentEntity = (XmlEntity) myEntityStack.peek();
            // add this entity to its parent entity
            addXmlEntityToParentEntity(mostRecentEntity, secondMostRecentEntity);
        }

        // reset the CDATA flag to not in CDATA
        myIsInCDATA = false;
    }


    /**
     * Processing when CDATA section starts.
     */
    public void startCDATA()
    {
        myIsInCDATA = true;
    }

    /**
     * Processing when CDATA section ends.
     */
    public void endCDATA()
    {
        //myIsInCDATA = false;
        // resetting CATA status is done in the endElement section,
        // since need to know whether in CDATA at that point
    }

    /**
     * Processing when comment is found.
     *
     * @param ch
     * @param start
     * @param length
     */
    public void comment(char[] ch, int start, int length)
    {
        myCommentsToAdd.add(String.valueOf(ch, start, length));
    }

    /**
     * Processing when DTD is found.  Currently, does nothing.
     *
     * @param name
     * @param publicId
     * @param systemId
     */
    public void startDTD(String name, String publicId, String systemId)
    {
    }

    /**
     * Processing when DTD section ends.  Currently, does nothing.
     */
    public void endDTD()
    {
    }

    /**
     * Processing when entity begins.  Currently, does nothing.
     *
     * @param name
     */
    public void startEntity(String name)
    {
    }

    /**
     * Processing when entity ends.  Currently, does nothing.
     *
     * @param name
     */
    public void endEntity(String name)
    {
    }

    /**
     * Processing when end of document reached.  Currently, does nothing.
     *
     * @throws SAXException
     */
    public void endDocument() throws SAXException
    {
        // nothing left to do; end of document taken care of by
        // last endElement call
    }

    /**
     * Processing when a parsing error is found.  Currently, throws an exception.
     *
     * @param e
     * @throws SAXParseException
     */
    public void error(SAXParseException e) throws SAXParseException
    {
        throw e;
    }

    // helper functions

    /**
     * Adds String value to xmlEntity.
     *
     * @param xmlEntity
     * @param stringValue
     */
    protected void addStringValueToXmlEntity(XmlEntity xmlEntity, String stringValue)
    {
        if (xmlEntity.isValueMixedContent())
        {
            xmlEntity.addMixedContentEntry(stringValue);
        }
        else
        {
            if ((stringValue == null) || (stringValue.equals("")))
            {
                xmlEntity.setValue(stringValue);
            }
            else
            {
                ArrayList<XmlEntity> childrenList = xmlEntity.getChildren();
                if (childrenList.size() > 0)
                {
                    // we are trying to add a non-whitespace value to an entity that already has children;
                    // this must become a mixed value content
                    xmlEntity.setValueIsMixedContent(true);

                    // copy all previous children into the mixedContent realm
                    for (int a = 0; a < childrenList.size(); a++)
                    {
                        XmlEntity child = (XmlEntity) childrenList.get(a);
                        xmlEntity.addMixedContentEntry(child);
                    }

                    // remove the children
                    childrenList.clear();

                    // add the String value
                    xmlEntity.addMixedContentEntry(stringValue);
                }
                else
                {
                    xmlEntity.setValue(stringValue);
                }
            }
        }
    }

    /**
     * Adds childEntity to parentEntity, as appropriate.
     *
     * @param childEntity
     * @param parentEntity
     */
    protected void addXmlEntityToParentEntity(XmlEntity childEntity, XmlEntity parentEntity)
    {
        if (parentEntity.isValueMixedContent())
        {
            parentEntity.addMixedContentEntry(childEntity);
        }
        else
        {
            String parentStringValue = parentEntity.getValue();
            if ((parentStringValue == null) || (parentStringValue.equals("")))
            {
                parentEntity.addChild(childEntity);
            }
            else
            {
                // we are trying to add a child to an entity that already has a non-whitespace value;
                // this must become a mixed value content
                parentEntity.setValueIsMixedContent(true);

                // move the String into the MixedContent area
                parentEntity.addMixedContentEntry(parentStringValue);

                // remove the String from the parent entity's regular String area
                parentEntity.setValue("");

                // add the child
                parentEntity.addMixedContentEntry(childEntity);
            }
        }
    }
}
