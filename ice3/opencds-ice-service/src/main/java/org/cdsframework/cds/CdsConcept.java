/**
 * Copyright (C) 2023 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 *
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 *
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.cds;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencds.vmr.v1_0.internal.concepts.VmrOpenCdsConcept;

/**
 * A CDS concept that originates from CAT. It may or may not also be an instantiated OpenCDS concept during invocation of OpenCDS.
 */
public class CdsConcept extends VmrOpenCdsConcept {

	private String displayName;
	private boolean isOpenCdsSupportedConcept;
	
	private static final Logger logger = LogManager.getLogger();

	
	private CdsConcept() {
		super();
	}
	
	
	/**
	 * Instantiate an OpenCdsConceptCode object. It does not set the conceptTargetId, displayName or determinationMethodCode attributes of the VmrOpenCdsConcept
	 * @param openCdsConceptCode Concept Code; mandatory
	 * @throws IllegalArgumentException if concept code is null
	 */
 	public CdsConcept(String pOpenCdsConceptCode) {
		super();
		
		if (pOpenCdsConceptCode == null) {
			String _METHODNAME = "OpenCdsICEConcept(): ";
			String errStr = "concept code not supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		setOpenCdsConceptCode(pOpenCdsConceptCode);
	}
 	
 	
	/**
	 * Instantiate an OpenCdsConceptCode object. It does not set the conceptTargetId, displayName or determinationMethodCode attributes of the VmrOpenCdsConcept
	 * @param openCdsConceptCode Concept Code; mandatory
	 * @throws IllegalArgumentException if concept code is null
	 */
 	private CdsConcept(String pOpenCdsConceptCode, boolean pIsOpenCdsSupportedConcept) {
		super();
		
		if (pOpenCdsConceptCode == null) {
			String _METHODNAME = "OpenCdsICEConcept(): ";
			String errStr = "concept code not supplied";
			logger.warn(_METHODNAME + errStr);
			throw new IllegalArgumentException(errStr);
		}
		setOpenCdsConceptCode(pOpenCdsConceptCode);
		this.isOpenCdsSupportedConcept = pIsOpenCdsSupportedConcept;
	}
 	
	/**
	 * Instantiate an CdsConcept object. It does not set the conceptTargetId or determinationMethodCode attributes of the VmrOpenCdsConcept
	 * @param pOpenCdsConceptCode Concept Code; mandatory
	 * @param pDisplayName Display Name
	 * @throws IllegalArgumentException if concept code is null
	 */
	private CdsConcept(String pOpenCdsConceptCode, boolean pIsOpenCdsSupportedConcept, String pDisplayName) {
		this(pOpenCdsConceptCode, pIsOpenCdsSupportedConcept);
		setDisplayName(pDisplayName);
	}
	
	
	/**
	 * Instantiate an CdsConcept object. It does not set the conceptTargetId or determinationMethodCode attributes of the VmrOpenCdsConcept
	 * @param pOpenCdsConceptCode Concept Code; mandatory
	 * @param pDisplayName Display Name
	 * @throws IllegalArgumentException if concept code is null
	 */
	public CdsConcept(String pOpenCdsConceptCode, String pDisplayName) {
		this(pOpenCdsConceptCode);
		setDisplayName(pDisplayName);
	}
	

	/**
	 * Construct a CdsConcept from a VmrOpenCdsConcept
	 * @param pVOCC
	 * @return
	 */
	public static CdsConcept constructCdsConceptFromVmrOpenCdsConcept(VmrOpenCdsConcept pVOCC) {
		
		if (pVOCC == null) {
			return null;
		}
		
		CdsConcept lIC = new CdsConcept(pVOCC.getOpenCdsConceptCode(), true, pVOCC.getDisplayName());
		lIC.conceptTargetId = pVOCC.getConceptTargetId();
		lIC.determinationMethodCode = pVOCC.getDeterminationMethodCode();
		return lIC;
	}
	
	
	public static CdsConcept constructDeepCopyOfCdsConceptObject(CdsConcept pIC) {
		
		if (pIC == null) {
			return pIC;
		}
		
		CdsConcept lIC = new CdsConcept();
		lIC.conceptTargetId = pIC.conceptTargetId;
		lIC.determinationMethodCode = pIC.determinationMethodCode;
		lIC.openCdsConceptCode = pIC.openCdsConceptCode;
		lIC.displayName = pIC.displayName;
		lIC.isOpenCdsSupportedConcept = pIC.isOpenCdsSupportedConcept;
		return lIC;
	}
	
	
	public String getDisplayName() {
		return displayName;
	}

	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	
	public boolean isOpenCdsSupportedConcept() {
		return this.isOpenCdsSupportedConcept;		
	}
	
	
	public void setIsOpenCdsSupportedConcept(boolean pIsOpenCdsSupportedConcept) {
		this.isOpenCdsSupportedConcept = pIsOpenCdsSupportedConcept;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getOpenCdsConceptCode() == null) ? 0 : getOpenCdsConceptCode().hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CdsConcept other = (CdsConcept) obj;
		if (getOpenCdsConceptCode() == null) {
			if (other.getOpenCdsConceptCode() != null)
				return false;
		} 
		else if (!getOpenCdsConceptCode().equals(other.getOpenCdsConceptCode())) {
			return false;
		}
		//else if (getOpenCdsConceptCode().equals(other.getOpenCdsConceptCode()) && isOpenCdsSupportedConcept() != other.isOpenCdsSupportedConcept()) {
		//	return false;
		//}
		
		return true;
	}

	
	@Override
	public String toString() {
		return "CdsConcept [displayName=" + displayName + ", isOpenCdsSupportedConcept=" + isOpenCdsSupportedConcept + ", toString()=" + super.toString() + "]";
	}
	
	
}
