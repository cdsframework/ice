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

package org.opencds.vmr.v1_0.internal.datatypes;

/**
 * <p>Java class for internal CD complex type.
 */
public class CD extends ANY
{

    protected String displayName;
    protected String code;
    protected String codeSystem;
    protected String codeSystemName;
    protected String originalText;
    
    /*
     * DO NOT REMOVE THIS COMMENT
     * 
     * Note that the generated hashset method must be modified as follows to always ignore displayName and codeSystemName, 
     * 		and to ignore originalText unless the code is null:
     * 
     * //		result = prime * result
     * //				+ ((codeSystemName == null) ? 0 : codeSystemName.hashCode());
     * //		result = prime * result
     * //				+ ((displayName == null) ? 0 : displayName.hashCode());
     * 		result = prime * result
     * 				+ (((originalText == null) || (code != null)) ? 0 : originalText.hashCode());
     * 
     * 
     * Note that the generated equals method must be modified as follows to always ignore displayName and codeSystemName, 
     * 		and to ignore originalText unless one of the codes is null:
     * 
     * //		if (codeSystemName == null) {
     * //			if (other.codeSystemName != null)
     * //				return false;
     * //		} else if (!codeSystemName.equals(other.codeSystemName))
     * //			return false;
     * //		if (displayName == null) {
     * //			if (other.displayName != null)
     * //				return false;
     * //		} else if (!displayName.equals(other.displayName))
     * //			return false;
     * 		// ignore original text unless one of the codes is null
     * 		if ((originalText == null) && (code != null)) {
     * 			if ((other.originalText != null) && (other.code == null))
     * 				return false;
     * 		} else if ((originalText != null) && (code == null)) {
     * 			if ((other.originalText == null) && (other.code != null))
     * 				return false;
     * 		} else if ((code == null) && (other.code == null) && (!originalText.equals(other.originalText)) )
     * 			return false;
     */
    
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCodeSystem() {
		return codeSystem;
	}
	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}
	public String getCodeSystemName() {
		return codeSystemName;
	}
	public void setCodeSystemName(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}
	public String getOriginalText() {
		return originalText;
	}
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((codeSystem == null) ? 0 : codeSystem.hashCode());
//		result = prime * result
//				+ ((codeSystemName == null) ? 0 : codeSystemName.hashCode());
//		result = prime * result
//				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ (((originalText == null) || (code != null)) ? 0 : originalText.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CD other = (CD) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (codeSystem == null) {
			if (other.codeSystem != null)
				return false;
		} else if (!codeSystem.equals(other.codeSystem))
			return false;
//		if (codeSystemName == null) {
//			if (other.codeSystemName != null)
//				return false;
//		} else if (!codeSystemName.equals(other.codeSystemName))
//			return false;
//		if (displayName == null) {
//			if (other.displayName != null)
//				return false;
//		} else if (!displayName.equals(other.displayName))
//			return false;
		// ignore original text unless one of the codes is null
		if ((originalText == null) && (code != null)) {
			if ((other.originalText != null) && (other.code == null))
				return false;
		} else if ((originalText != null) && (code == null)) {
			if ((other.originalText == null) && (other.code != null))
				return false;
		} else if ((code == null) && (other.code == null) && (!originalText.equals(other.originalText)) )
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "CD [displayName=" + displayName 
				+ ", code=" + code
				+ ", codeSystem=" + codeSystem 
				+ ", codeSystemName=" + codeSystemName 
				+ ", originalText=" + originalText 
				+ "]";
	}

}
