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

/**
 * <p>MixedContentEntry represents a mixed content entry in a MixedContent.</p>
 * 
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class MixedContentEntry extends Object implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5914755579450113612L;
	protected boolean myIsXmlEntity;  // whether this is an XmlEntity or a String
    protected String myStringEntry; // populated if isXmlEntity == false
    protected XmlEntity myXmlEntityEntry; // populated if isXmlEntity == true

    /**
     * Creates MixedContentEntry object using String
     */
    public MixedContentEntry(String stringEntry)
    {
        myIsXmlEntity = false;
        myStringEntry = stringEntry;
    }

    /**
     * Creates MixedContentEntry object using XmlEntity
     */
    public MixedContentEntry(XmlEntity xmlEntityEntry)
    {
        myIsXmlEntity = true;
        myXmlEntityEntry = xmlEntityEntry;
    }

    // setters
    public void setStringEntry(String stringEntry)
    {
        myIsXmlEntity = false;
        myStringEntry = stringEntry;
    }
    
    // getters
    public boolean isXmlEntity()
    {
        return myIsXmlEntity;
    }

    public String getStringEntry()
    {
        return myStringEntry;
    }

    public XmlEntity getXmlEntityEntry()
    {
        return myXmlEntityEntry;
    }

    // hash functions
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MixedContentEntry)) return false;

        final MixedContentEntry mixedContentEntry = (MixedContentEntry) o;

        if (myIsXmlEntity != mixedContentEntry.myIsXmlEntity) return false;
        if (myStringEntry != null ? !myStringEntry.equals(mixedContentEntry.myStringEntry) : mixedContentEntry.myStringEntry != null) return false;
        if (myXmlEntityEntry != null ? !myXmlEntityEntry.equals(mixedContentEntry.myXmlEntityEntry) : mixedContentEntry.myXmlEntityEntry != null) return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        result = (myIsXmlEntity ? 1 : 0);
        result = 29 * result + (myStringEntry != null ? myStringEntry.hashCode() : 0);
        result = 29 * result + (myXmlEntityEntry != null ? myXmlEntityEntry.hashCode() : 0);
        return result;
    }
}

