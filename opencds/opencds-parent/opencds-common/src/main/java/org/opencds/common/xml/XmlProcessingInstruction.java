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
 * <p>XmlProcessingInstruction represents an XML processing instruction.
 * e.g. <?mso-application progid="InfoPath.Document"?> -->
 * target = mso-application, data = progid="InfoPath.Document</p>
 * <p/>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class XmlProcessingInstruction extends Object implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -281322108404110616L;
	protected String myTarget;
    protected String myData;

    public XmlProcessingInstruction()
    {

    }

    public XmlProcessingInstruction(String target, String data)
    {
        myTarget = target;
        myData = data;
    }

    public void setTarget(String target)
    {
        this.myTarget = target;
    }

    public void setData(String data)
    {
        this.myData = data;
    }

    public String getTarget()
    {
        return myTarget;
    }

    public String getData()
    {
        return myData;
    }
}