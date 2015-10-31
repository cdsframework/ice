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

/**
 * <p>MixedContent represents a mixed content value in an XmlEntity.</p>
 * 
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class MixedContent extends Object implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8732040029145359348L;
	@SuppressWarnings("rawtypes")
	protected ArrayList myMixedContentEntryList; // holds MixedContentEntry objects

    /**
     * Creates MixedContent object.
     */
    @SuppressWarnings("rawtypes")
	public MixedContent()
    {
        myMixedContentEntryList = new ArrayList();
    }

    // adder
    @SuppressWarnings("unchecked")
	public void addMixedContentEntry(String stringMixedContentEntry)
    {
        this.myMixedContentEntryList.add(new MixedContentEntry(stringMixedContentEntry));
    }

    @SuppressWarnings("unchecked")
	public void addMixedContentEntry(XmlEntity xmlEntityMixedContentEntry)
    {
        this.myMixedContentEntryList.add(new MixedContentEntry(xmlEntityMixedContentEntry));
    }

    // getter
    @SuppressWarnings("rawtypes")
	public ArrayList getMixedContentEntryList()
    {
        return myMixedContentEntryList;
    }

    // hash functions
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MixedContent)) return false;

        final MixedContent mixedContent = (MixedContent) o;

        if (myMixedContentEntryList != null ? !myMixedContentEntryList.equals(mixedContent.myMixedContentEntryList) : mixedContent.myMixedContentEntryList != null) return false;

        return true;
    }

    public int hashCode()
    {
        return (myMixedContentEntryList != null ? myMixedContentEntryList.hashCode() : 0);
    }
}

