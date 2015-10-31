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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.opencds.common.utilities.StringUtility;

/**
 * <p>XmlElement represents an XML entity.</p>
 * <p/>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class XmlEntity extends Object implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4531869042716473060L;
	protected String myLabel; // The label that identifies the XmlEntity (e.g. "FirstName")
    protected String myValue; // The value of the XmlEntity (e.g. "Bob")
    protected boolean myValueIsCDATA; // Whether the value is CDATA (meaning shouldn't be parsed by XML parser)
    protected MixedContent myValue_mixedContent; // will be populated if entity holds mixed content
    protected boolean myValueIsMixedContent;
    protected ArrayList<String> myComments;                 // Comments associated with (preceding) the XmlEntity.
    protected ArrayList<XmlProcessingInstruction> myProcessingInstructions;   // XmlProcessingInstruction's associated with (preceding) the XmlEntity.

    protected final String myXmlHeaderString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    // The XML header to be added to the outputs of the getAs[Concise/Verbose]XmlStringBuffer methods.
    // NOTE: if changing from "UTF-8", also change XmlConverter.myXmlEncodingCharSet

    // note: attributes are in HashMap because, by definition,
    // XML attributes are orderless

    protected ArrayList<XmlEntity> myChildren; // contains XMLEntity's
    protected HashMap<String, String> myAttributes; // key = String label, target = String value

    /**
     * Creates XmlEntity object with the specified parameters set to indicated values.
     *
     * @param label        The label of the XmlEntity.
     * @param value        The value of the XmlEntity.
     * @param valueIsCDATA Whether the value is a CDATA entry.
     */
    public XmlEntity(String label, String value, boolean valueIsCDATA)
    {
        initialize();
        myLabel = label;
        myValue = value;
        myValueIsCDATA = valueIsCDATA;
    }

    /**
     * Creates XmlEntity object with the specified parameters set to indicated values.
     *
     * @param label The label of the XmlEntity.
     */
    public XmlEntity(String label)
    {
        initialize();
        myLabel = label;
    }

    /**
     * Creates new XmlEntity where label == null, value == null, CDATA == false, comments == null.
     */
    public XmlEntity()
    {
        initialize();
    }

    protected void initialize()
    {
        myValueIsCDATA = false;
        myComments = new ArrayList<String>();
        myProcessingInstructions = new ArrayList<XmlProcessingInstruction>();

        myChildren = new ArrayList<XmlEntity>();
        myAttributes = new HashMap<String, String>();

        myValue_mixedContent = new MixedContent();
        myValueIsMixedContent = false;
    }

    /**
     * Sets the label of the XmlEntity to the specified label.
     *
     * @param label The label of the XmlEntity.
     */
    public void setLabel(String label)
    {
        myLabel = label;
    }

    /**
     * Sets the value of the XmlEntity to the specified value.
     *
     * @param value The value of the XmlEntity.
     */
    public void setValue(String value)
    {
        myValue = value;
    }

    /**
     * Sets whether the value of the XmlEntity is a CDATA item.
     *
     * @param valueIsCDATA Whether the value of the XmlEntity is a CDATA item.
     */
    public void setValueIsCDATA(boolean valueIsCDATA)
    {
        myValueIsCDATA = valueIsCDATA;
    }

    public void setValue_MixedContent(MixedContent mixedContent)
    {
        this.myValue_mixedContent = mixedContent;
        this.myValueIsMixedContent = true;
    }

    public void addMixedContentEntry(String stringMixedContentEntry)
    {
        this.myValue_mixedContent.addMixedContentEntry(stringMixedContentEntry);
    }

    public void addMixedContentEntry(XmlEntity xmlEntityMixedContentEntry)
    {
        this.myValue_mixedContent.addMixedContentEntry(xmlEntityMixedContentEntry);
    }

    public void setValueIsMixedContent(boolean valueIsMixedContent)
    {
        this.myValueIsMixedContent = valueIsMixedContent;
    }

    /**
     * Adds comment associated with (preceding) the XmlEntity.
     *
     * @param comment Comment associated with the XmlEntity.
     */
    public void addComment(String comment)
    {
        myComments.add(comment);
    }

    /**
     * Adds processing instruction associated with (preceding) the XmlEntity.
     *
     * @param processingInstruction
     */
    public void addProcessingInstruction(XmlProcessingInstruction processingInstruction)
    {
        myProcessingInstructions.add(processingInstruction);
    }

    /**
     * Add to this XmlEntity the specified child XmlEntity.
     *
     * @param childXmlEntity An XmlEntity which is the child of this XmlEntity.
     */
    public void addChild(XmlEntity childXmlEntity)
    {
        myChildren.add(childXmlEntity);
    }

    /**
     * Adds child at the index specified (use position 0 if want to add as first
     * in list).
     *
     * @param childXmlEntity
     * @param index
     */
    public void addChildAtIndex(XmlEntity childXmlEntity, int index) throws IndexOutOfBoundsException
    {
        try
        {
            myChildren.add(index, childXmlEntity);
        }
        catch (IndexOutOfBoundsException e)
        {
            throw e;
        }
    }

    /**
     * Adds an attribute to this XmlEntity.  If attribute already exists, overwrites existing value.
     *
     * @param attributeLabel The label of the attribute to be added.
     * @param attributeValue The value of the attribute to be added.
     */
    public void addAttribute(String attributeLabel, String attributeValue)
    {
        myAttributes.put(attributeLabel, attributeValue);
    }

    /**
     * Sets attribute for this XmlEntity.  If attribute does not exist, it is created.
     *
     * @param attributeLabel The label of the attribute to be added.
     * @param attributeValue The value of the attribute to be added.
     */
    public void setAttribute(String attributeLabel, String attributeValue)
    {
        addAttribute(attributeLabel, attributeValue);
    }

    /**
     * Returns the label of the XmlEntity.
     *
     * @return The label of the XmlEntity; may be <code>null</code> if not set.
     */
    public String getLabel()
    {
        return myLabel;
    }

    /**
     * Returns the value of the XmlEntity.
     *
     * @return The value of the XmlEntity; may be <code>null</code> if not set.
     */
    public String getValue()
    {
        return myValue;
    }

    /**
     * Returns whether the value of the XmlEntity is of the CDATA type.
     *
     * @return <code>true</code> if XmlEntity's value is of the CDATA type, <code>false</code> otherwise.
     */
    public boolean getValueIsCDATA()
    {
        return myValueIsCDATA;
    }

    public MixedContent getValue_MixedContent()
    {
        return myValue_mixedContent;
    }

    public boolean isValueMixedContent()
    {
        return myValueIsMixedContent;
    }

    /**
     * Returns any comments associated with the XmlEntity; should be null if no comments.
     *
     * @return Comments associated with the XmlEntity; should be <code>null</code> if no comments.
     */
    public ArrayList<String> getComments()
    {
        return myComments;
    }

    /**
     * Returns array containing XmlProcessingInstruction objects.
     *
     * @return
     */
    public ArrayList<XmlProcessingInstruction> getProcessingInstructions()
    {
        return myProcessingInstructions;
    }

    /**
     * Returns the ArrayList of XmlEntity objects that are this XmlEntity's children.
     *
     * @return ArrayList containing XmlEntity objects that are this XmlEntity's children.
     */
    public ArrayList<XmlEntity> getChildren()
    {
        return myChildren;
    }

    /**
     * Returns the ArrayList of XmlEntity objects that are this XmlEntity's children AND have the specified label.
     * Returns an empty array list if no matches exist.
     *
     * @param label The label required among this XmlEntity's children in order for that child to be included in the returned ArrayList.
     * @return An ArrayList containing the children XmlEntity objects that have the specified label.
     */
    public ArrayList<XmlEntity> getChildrenWithLabel(String label)
    {
        ArrayList<XmlEntity> arrayListToReturn = new ArrayList<XmlEntity>();

        for (int k = 0; k < myChildren.size(); k++)
        {
            XmlEntity child = (XmlEntity) myChildren.get(k);

            if ((child.getLabel()).equals(label))
            {
                arrayListToReturn.add(child);
            }
        }
        return arrayListToReturn;
    }

    /**
     * Returns XmlEntity which is the first child with the label.  Returns null if no such entity exists.
     *
     * @param label
     * @return
     */
    public XmlEntity getFirstChildWithLabel(String label)
    {
        for (int k = 0; k < myChildren.size(); k++)
        {
            XmlEntity child = (XmlEntity) myChildren.get(k);

            if (child != null)
            {
                if ((child.getLabel()).equals(label))
                {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Returns the ArrayList of XmlEntity objects that are this XmlEntity's descendants AND have the specified label.
     * Returns an empty array list if no matches exist.
     *
     * @param label The label required among this XmlEntity's descendants in order for that descendant to be included in the returned ArrayList.
     * @return An ArrayList containing the descendant XmlEntity objects that have the specified label.
     */
    public ArrayList<XmlEntity> getDescendantsWithLabel(String label)
    {
        ArrayList<XmlEntity> arrayListToReturn = new ArrayList<XmlEntity>();

        ArrayList<XmlEntity> descendants = getDescendants();
        for (int a = 0; a < descendants.size(); a++)
        {
            XmlEntity descendant = (XmlEntity) descendants.get(a);
            if (descendant.getLabel().equals(label))
            {
                arrayListToReturn.add(descendant);
            }
        }

        return arrayListToReturn;
    }

    /**
     * Returns the ArrayList of XmlEntity objects that are this XmlEntity's descendants.
     * Returns an empty array list if no descendants exists.
     *
     * @return An ArrayList containing the descendant XmlEntity objects.
     */
    public ArrayList<XmlEntity> getDescendants()
    {
        ArrayList<XmlEntity> arrayListToReturn = new ArrayList<XmlEntity>();

        if (!myValueIsMixedContent)
        {
            for (int a = 0; a < myChildren.size(); a++)
            {
                XmlEntity child = (XmlEntity) myChildren.get(a);
                arrayListToReturn.add(child);

                // recursively add descendants with label
                arrayListToReturn.addAll(child.getDescendants());
            }
        }
        else
        {
            ArrayList<?> mixedContentEntryList = myValue_mixedContent.getMixedContentEntryList();
            for (int b = 0; b < mixedContentEntryList.size(); b++)
            {
                MixedContentEntry mixedContentEntry = (MixedContentEntry) mixedContentEntryList.get(b);
                if (mixedContentEntry.isXmlEntity())
                {
                    XmlEntity mixedContentXmlEntry = mixedContentEntry.getXmlEntityEntry();
                    arrayListToReturn.add(mixedContentXmlEntry);

                    // recursively add descendants with label
                    arrayListToReturn.addAll(mixedContentXmlEntry.getDescendants());
                }
            }
        }

        return arrayListToReturn;
    }

    /**
     * Returns XmlEntity which is the first descendant with the label.  Returns null if no such entity exists.
     *
     * @param label
     * @return
     */
    public XmlEntity getFirstDescendantWithLabel(String label)
    {
        XmlEntity entityToReturn = null;
        ArrayList<XmlEntity> descendantsWithLabel = getDescendantsWithLabel(label);
        if (descendantsWithLabel.size() > 0)
        {
            return (XmlEntity) descendantsWithLabel.get(0);
        }
        return entityToReturn;
    }

    /**
     * Returns XmlEntity which is the first child with label1.  Returns null if no such entity exists, or
     * if a child with label2 is encountered.  Introduced to avoid iteration over entire children list if
     * not necessary.
     *
     * @param label1
     * @param label2
     * @return
     */
    public XmlEntity getFirstChildWithLabel1BeforeLabel2(String label1, String label2)
    {
        for (int k = 0; k < myChildren.size(); k++)
        {
            XmlEntity child = (XmlEntity) myChildren.get(k);

            String currentLabel = child.getLabel();

            if (currentLabel.equals(label1))
            {
                return child;
            }
            else if (currentLabel.equals(label2))
            {
                return null;
            }
        }
        return null;
    }

    /**
     * Returns first child.
     *
     * @return
     */
    public XmlEntity getFirstChild()
    {
        return (XmlEntity) myChildren.get(0);
    }

    /**
     * Returns the n'th child of this XmlEntity; returns null if n is not valid.
     *
     * @param n The number of the child XmlEntity; must be 1 or greater, where 1 (not 0) signifies the first child.
     * @return The XmlEntity corresponding to the n'th child if n is valid, null otherwise.
     */
    public XmlEntity getNthChild(int n)
    {
        // if n is invalid
        if ((n < 1) || (n > myChildren.size()))
        {
            return null;
        }
        else
        {
            return (XmlEntity) myChildren.get(n - 1);
        }
    }

    /**
     * Gets the labels of all the XmlEntity's attributes.
     *
     * @return A Set (implemented as a TreeSet) of attributeLabels (ie keys to myAttributes).
     */
    public Set<String> getAttributeLabels()
    {
        //Set setToReturn = new TreeSet();
        Set<String> setToReturn = new TreeSet<String>(myAttributes.keySet());
        return setToReturn;
    }

    /**
     * Gets the value of this XmlEntity's attribute with the specified attribute label.
     *
     * @param attributeLabel The label of the attribute for which the value is requested.
     * @return The value of the specified label; null if no such attribute exists.
     */
    public String getAttributeValue(String attributeLabel)
    {
        return (String) myAttributes.get(attributeLabel);
    }

    /**
     * Get the contents of the XmlEntity represented as a string, with white space and formatting added
     * for easier reading by a human, as when printed to screen or saved to a text file.  Note that
     * the XML declaration IS now part of the XML string returned.
     *
     * @param indentSpacing   Number of spaces to add for each level of indentation in the XML.
     * @param includeComments Whether comments should be inlcuded in the output.
     * @return The contents of the XmlEntity represented as a string.
     */
    public String getAsVerboseXmlString(int indentSpacing, boolean includeComments, boolean includeProcessingInstructions)
    {
        StringBuilder buffer = new StringBuilder(10000);

        int numberUnclosedElements = 0;
        boolean isInCDATA = false;
        boolean isInComments = false;

        int indexOfMostRecentStartTag = 0;
        int indexOfMostRecentEndTag = 0;

        // get the concise version
        StringBuffer xmlBuffer = getAsConciseXmlStringBuffer(includeComments, includeProcessingInstructions, true, 0); // 0 indicates start recursion

        // now format it to be more human readable, by adding in
        // carriage lines and white space as appropriate

        // algorithm:
        // 0) if in a comment section (started by "<!-" and ending with "-->"), then
        //    don't make any formatting changes
        //
        // 1) every time there is a "<[not / or !]", add to the number of unclosed elements
        // Then, add a '\n + indentSpacing * x' before the <, where x
        // is the number of unclosed elements.
        //
        // 2) every time there is a "</", decrease number of unclosed elements by 1
        //
        // 3) if there is a "<!", don't change unclosed elements number
        //
        // 4) if "<![CDATA[" is encountered (deemed to be the case with the presence
        // of "<![" ), then don't do anything until "]]>" is encountered.
        //
        // 5) if this is the last three characters, add all three characters to the copy buffer
        //
        // 6) except for case 5, add the first character being read to the copy buffer
        //
        // 7) also, if there are 2 endElements in a row (as determined by lack
        //    of new element following endElement, go to new line after end of first
        //    endElement

        for (int index = 0; index < (xmlBuffer.length()) - 2; index++)
        {
            String triad = (String) xmlBuffer.substring(index, index + 3);

            String firstCharacter = triad.substring(0, 1);
            String secondCharacter = triad.substring(1, 2);
            String thirdCharacter = triad.substring(2, 3);

            if (index == 0)
            // take care of special case of first character
            {
                buffer.append(firstCharacter);
            }
            else if (index == (xmlBuffer.length()) - 3)
            // take care of special case of this being the last three characters
            {
                // if this is the last triad, add all of the characters to the document

                buffer.append(firstCharacter);
                buffer.append(secondCharacter);
                buffer.append(thirdCharacter);
            }
            else if (isInComments)
            {
                // just append the first character
                buffer.append(firstCharacter);

                // check to see if should move out of CDATA
                if ((firstCharacter.equals("-")) && (secondCharacter.equals("-"))
                        && (thirdCharacter.equals(">")))
                {
                    isInComments = false;
                }
            }
            else if (isInCDATA)
            // next, take care of special case of being inside a CDATA section
            {
                // just append the first character
                buffer.append(firstCharacter);

                // check to see if should move out of CDATA
                if ((firstCharacter.equals("]")) && (secondCharacter.equals("]"))
                        && (thirdCharacter.equals(">")))
                {
                    isInCDATA = false;
                }
            }
            else
            {
                if ((!firstCharacter.equals("<")) && (!firstCharacter.equals("]")))
                {
                    // just append the first character and move on
                    buffer.append(firstCharacter);

                }
                else if (firstCharacter.equals("<")) // if this is a <XX
                {
                    if (secondCharacter.equals("!")) // if this is a comment or CDATA start
                    {
                        // don't change numberUnclosedElements, but indent as if this were the case

                        if (thirdCharacter.equals("-"))
                        {
                            // assume this is a comment section beginning
                            isInComments = true;
                        }

                        if (thirdCharacter.equals("["))
                        {
                            // assume this is a CDATA section beginning
                            isInCDATA = true;
                        }

                        // new line
                        buffer.append("\n");

                        // indent
                        for (int k = 0; k < (numberUnclosedElements + 1) * indentSpacing; k++)
                        {
                            buffer.append(" ");
                        }
                    }
                    else if (secondCharacter.equals("/")) // if this is an end tag
                    {
                        numberUnclosedElements--;

                        // add a new line if this is a second end tag in a row
                        if (indexOfMostRecentEndTag > indexOfMostRecentStartTag)
                        {
                            // new line
                            buffer.append("\n");

                            // indent
                            for (int k = 0; k < (numberUnclosedElements + 1) * indentSpacing; k++)
                            {
                                buffer.append(" ");
                            }
                        }

                        // mark the index of end tag
                        indexOfMostRecentEndTag = index;
                    }
                    else
                    {
                        numberUnclosedElements++;

                        // mark the index of start tag
                        indexOfMostRecentStartTag = index;

                        // new line
                        buffer.append("\n");

                        // indent
                        for (int k = 0; k < numberUnclosedElements * indentSpacing; k++)
                        {
                            buffer.append(" ");
                        }
                    }

                    // in all cases, add first character
                    buffer.append(firstCharacter);
                }
                else
                {
                    // case of end of CDATA section
                    buffer.append(firstCharacter);
                }
            }
        }
        return new String(buffer);
    }

    /**
     * Get the contents of the XmlEntity represented as a string, WITHOUT any white space and formatting added.
     * This method should be invoked rather than getAsVerboseXmlStringBuffer when the size of the
     * XML representation should be constrained, as when using the output of this method for
     * system-system communication over HTTP.  Note that the XML declaration IS now part
     * of the XML string returned.
     *
     * @param includeComments               Whether comments should be inlcuded in the output.
     * @param includeProcessingInstructions
     * @param includeXmlHeader              Whether to include the <?xml ... > XML header
     * @return The contents of the XmlEntity represented as a string.
     */
    public String getAsConciseXmlString(boolean includeComments, boolean includeProcessingInstructions, boolean includeXmlHeader)
    {
        return new String(getAsConciseXmlStringBuffer(includeComments, includeProcessingInstructions, includeXmlHeader, 0));
    }

    /**
     * Same as above function, but includeXmlHeader set to true.
     *
     * @param includeComments
     * @param includeProcessingInstructions
     * @return
     */
    public String getAsConciseXmlString(boolean includeComments, boolean includeProcessingInstructions)
    {
        return new String(getAsConciseXmlStringBuffer(includeComments, includeProcessingInstructions, true, 0));
    }

    /**
     * Get the contents of the XmlEntity represented as a string, WITHOUT any white space and formatting added.
     * This method is protected because it is meant to act as a helper class to the public function
     * of the same name.  This method has been sequestered into a separate class in order to enable
     * construction of the string using recursion, where the XML header is added only
     * at the very beginning of the XML document.
     * NOTE: illegal XML characters in attributes and in non-CDATA element values are replaced with escape characters.
     *
     * @param includeComments Whether comments should be inlcuded in the output.
     * @param recursionNumber The current depth in the recursion; initial call should indicate a value of 0.
     * @return The contents of the XmlEntity represented as a string.
     */
    protected StringBuffer getAsConciseXmlStringBuffer(boolean includeComments, boolean includeProcessingInstructions, boolean includeXmlHeader, int recursionNumber)
    {
        StringBuffer buffer = new StringBuffer(10000);

        if (recursionNumber == 0)
        {
            if (includeXmlHeader)
            {
                buffer.append(myXmlHeaderString);
            }
        }
        recursionNumber++;

        if (includeComments)
        {
            for (int k = 0; k < myComments.size(); k++)
            {
                buffer.append("<!-- ");
                buffer.append((String) myComments.get(k));
                buffer.append(" -->");
            }
        }

        if (includeProcessingInstructions)
        {
            for (int k = 0; k < myProcessingInstructions.size(); k++)
            {
                XmlProcessingInstruction processingInstruction = (XmlProcessingInstruction) myProcessingInstructions.get(k);
                buffer.append("<?");
                buffer.append(processingInstruction.getTarget());
                buffer.append(" ");
                buffer.append(processingInstruction.getData());
                buffer.append("?>");
            }
        }

        buffer.append("<");
        buffer.append(myLabel);

        Set<String> attributeLabels = getAttributeLabels();
        Iterator<String> allLabels = attributeLabels.iterator();

        while (allLabels.hasNext())
        {
            String attributeLabel = (String) allLabels.next();
            String attributeValue = (String) myAttributes.get(attributeLabel);
            buffer.append(" ");
            buffer.append(attributeLabel);
            buffer.append("=\"");
            if (attributeValue != null)
            {
                buffer.append(StringUtility.getStringWithLegalXmlCharacters(attributeValue));
            }
            buffer.append("\"");
        }

        buffer.append(">");

        if (isValueMixedContent() == false)
        {
            if (myValueIsCDATA)
            {
                buffer.append("<![CDATA[");
            }

            if (myValue != null)
            {
                if (myValueIsCDATA)
                {
                    buffer.append(myValue);
                }
                else
                {
                    buffer.append(StringUtility.getStringWithLegalXmlCharacters(myValue));
                }
            }

            if (myValueIsCDATA)
            {
                buffer.append("]]>");
            }

            for (int j = 0; j < myChildren.size(); j++)
            {
                XmlEntity child = (XmlEntity) myChildren.get(j);

                buffer.append(child.getAsConciseXmlStringBuffer(includeComments, includeProcessingInstructions, true, recursionNumber));
            }
        }
        else
        {
            MixedContent mixedContent = getValue_MixedContent();
            ArrayList<?> mixedContentEntryList = mixedContent.getMixedContentEntryList();
            for (int k = 0; k < mixedContentEntryList.size(); k++)
            {
                MixedContentEntry entry = (MixedContentEntry) mixedContentEntryList.get(k);
                if (entry.isXmlEntity())
                {
                    XmlEntity xmlEntityEntry = entry.getXmlEntityEntry();
                    buffer.append(xmlEntityEntry.getAsConciseXmlStringBuffer(includeComments, includeProcessingInstructions, true, recursionNumber));
                }
                else
                {
                    buffer.append(StringUtility.getStringWithLegalXmlCharacters(entry.getStringEntry()));
                }
            }
        }

        buffer.append("</");
        buffer.append(myLabel);
        buffer.append(">");

        return buffer;
    }

    // removal functions
    /**
     * Re-initializes XmlEntity.
     */
    public void reInitialize()
    {
        initialize();
    }

    public void removeAttributes()
    {
        this.myAttributes.clear();
    }

    public void removeChildren()
    {
        myChildren = null; // get rid of previous children list from memory
        myChildren = new ArrayList<XmlEntity>();
    }

    public void removeChildrenWithLabel(String label)
    {
        ArrayList<XmlEntity> childrenWithoutLabel = new ArrayList<XmlEntity>();

        for (int k = 0; k < myChildren.size(); k++)
        {
            XmlEntity child = (XmlEntity) myChildren.get(k);
            if (!(child.getLabel().equals(label)))
            {
                childrenWithoutLabel.add(child);
            }
        }
        myChildren = null; // get rid of previous children list from memory
        myChildren = childrenWithoutLabel;
    }

    public void removeProcessingInstructions()
    {
        myProcessingInstructions = null;
        myProcessingInstructions = new ArrayList<XmlProcessingInstruction>();
    }

    // hash functions
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof XmlEntity)) return false;

        final XmlEntity xmlEntity = (XmlEntity) o;

        if (myAttributes != null ? !myAttributes.equals(xmlEntity.myAttributes) : xmlEntity.myAttributes != null) return false;
        if (myChildren != null ? !myChildren.equals(xmlEntity.myChildren) : xmlEntity.myChildren != null) return false;
        if (myLabel != null ? !myLabel.equals(xmlEntity.myLabel) : xmlEntity.myLabel != null) return false;
        if (myValue != null ? !myValue.equals(xmlEntity.myValue) : xmlEntity.myValue != null) return false;
        if (myValue_mixedContent != null ? !myValue_mixedContent.equals(xmlEntity.myValue_mixedContent) : xmlEntity.myValue_mixedContent != null) return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (myLabel != null ? myLabel.hashCode() : 0);
        result = 29 * result + (myValue != null ? myValue.hashCode() : 0);
        result = 29 * result + (myValue_mixedContent != null ? myValue_mixedContent.hashCode() : 0);
        result = 29 * result + (myChildren != null ? myChildren.hashCode() : 0);
        result = 29 * result + (myAttributes != null ? myAttributes.hashCode() : 0);
        return result;
    }
}

