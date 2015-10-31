package org.cdsframework.ice.util;

import org.opencds.vmr.v1_0.internal.datatypes.CD;

public class ConceptUtils {

	
	/**
	 * Checks to make sure that the schema CD element is populated with at least the code and codeSystem attributes.
	 * @param CD CD element
	 * @return true if so, false if not.
	 */
	public static boolean requiredAttributesForCDSpecified(org.opencds.vmr.v1_0.schema.CD pCD) {
		
		if (pCD == null || pCD.getCode() == null || pCD.getCodeSystem() == null || pCD.getCode().isEmpty() || pCD.getCodeSystem().isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}

	
	/**
	 * Checks to make sure that the schema CD element is populated with at least the code and codeSystem attributes.
	 * @param CD CD element
	 * @return true if so, false if not.
	 */
	public static boolean requiredAttributesForCDSpecified(org.opencds.vmr.v1_0.internal.datatypes.CD pCD) {
		
		if (pCD == null || pCD.getCode() == null || pCD.getCodeSystem() == null || pCD.getCode().isEmpty() || pCD.getCodeSystem().isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}

	
	/**
	 * Check to see if two CD elements are equal; two null elements are not equals
	 */
	public static boolean cDElementsAreEqual(CD pCD1, CD pCD2) {
	
		if (pCD1 == null || pCD2 == null) {
			return false;
		}
		
		if (pCD1.equals(pCD2)) {
			return true;
		}

		return false;
	}
	
	
	/**
	 * Check to see that two CD elements are equal and minimally populated; two null CD elements are not equal
	 */
	public static boolean cDElementsArePopulatedAndEqual(CD pCD1, CD pCD2) {
		
		if (pCD1 == null || pCD2 == null) {
			return false;
		}
		
		if (requiredAttributesForCDSpecified(pCD1) && requiredAttributesForCDSpecified(pCD2)) {
			if (pCD1.equals(pCD2)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Returns a string representation of the schema CD element
	 * @param lCD
	 * @return
	 */
	public static String toStringCD(org.opencds.vmr.v1_0.schema.CD lCD) {
		
		if (lCD == null) {
			return "No CD information supplied";
		}		
		String lDebugStrb = "CD: ";
		lDebugStrb += "\n\t.getCode(): " + lCD.getCode();
		lDebugStrb += "\n\t.getCodeSystem(): " + lCD.getCodeSystem();
		lDebugStrb += "\n\t.geCodeSystemName(): " + lCD.getCodeSystemName();
		lDebugStrb += "\n\t.getDisplayName(): " + lCD.getDisplayName();
		lDebugStrb += "\n\t.getOriginalText(): " + lCD.getOriginalText();
		return lDebugStrb;
	}

	
	public static org.opencds.vmr.v1_0.internal.datatypes.CD toInternalCD(org.opencds.vmr.v1_0.schema.CD pCD) {
		
		if (pCD == null) {
			return null;
		}
		org.opencds.vmr.v1_0.internal.datatypes.CD lInternalCD = new CD();
		lInternalCD.setCode(pCD.getCode());
		lInternalCD.setCodeSystem(pCD.getCodeSystem());
		lInternalCD.setCodeSystemName(pCD.getCodeSystemName());
		lInternalCD.setDisplayName(pCD.getDisplayName());
		lInternalCD.setOriginalText(pCD.getOriginalText());

		return lInternalCD;
	}
	
}
