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

package org.opencds.config.migrate.terminology;

/**
 * <p>CD is a lightweight implementation of the HL7/ISO CD data type.  It contains a code, display name, code system, and code system name.</p>
 * 
 * @author Kensaku Kawamoto
 * @version 1.00
 */
public class CD 
{
	protected String codeSystem;
	protected String codeSystemName;
	protected String code;
	protected String displayName;

    /* DO NOT REMOVE THIS COMMENT
     * 
     * Note that the generated hashset method must be modified as follows to always ignore displayName and codeSystemName: 
     * 
     * //		result = prime * result
     * //				+ ((displayName == null) ? 0 : displayName.hashCode());
     * //		result = prime * result
     * //				+ ((codeSystemName == null) ? 0 : codeSystemName.hashCode());
     * 
     * 
     * Note that the generated equals method must be modified as follows to always ignore displayName and codeSystemName:
     * 
     * //		if (displayName == null) {
     * //			if (other.displayName != null)
     * //				return false;
     * //		} else if (!displayName.equals(other.displayName))
     * //			return false;
     * //		if (codeSystemName == null) {
     * //			if (other.codeSystemName != null)
     * //				return false;
     * //		} else if (!codeSystemName.equals(other.codeSystemName))
     * //			return false;
     */
    	
	/**
	 * @param codeSystem
	 * @param codeSystemName
	 * @param code
	 * @param displayName
	 */
	public CD(String codeSystem, String codeSystemName, String code, String displayName) {
		super();
		this.codeSystem = codeSystem;
		this.codeSystemName = codeSystemName;
		this.code = code;
		this.displayName = displayName;
	}

	/**
	 * @param codeSystem
	 * @param code
	 */
	public CD(String codeSystem, String code) {
		super();
		this.codeSystem = codeSystem;
		this.code = code;
		this.codeSystemName = "";
		this.displayName = "";
	}

	/**
	 * @return the codeSystem
	 */
	public String getCodeSystem() {
		return codeSystem;
	}

	/**
	 * @param codeSystem the codeSystem to set
	 */
	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}

	/**
	 * @return the codeSystemName
	 */
	public String getCodeSystemName() {
		return codeSystemName;
	}

	/**
	 * @param codeSystemName the codeSystemName to set
	 */
	public void setCodeSystemName(String codeSystemName) {
		this.codeSystemName = codeSystemName;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode().  Considers only code and codeSystem.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((codeSystem == null) ? 0 : codeSystem.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object).  Considers only code and codeSystem.
	 */
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CD [codeSystem=" + codeSystem + ", codeSystemName="
				+ codeSystemName + ", code=" + code + ", displayName="
				+ displayName + "]";
	}
}