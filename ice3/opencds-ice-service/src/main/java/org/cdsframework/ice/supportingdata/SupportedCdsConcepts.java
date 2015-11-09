package org.cdsframework.ice.supportingdata;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.service.ICEConcept;
import org.cdsframework.ice.service.InconsistentConfigurationException;

/**
 * SupportedCdsConcepts is used to track the concepts defined in the ICE supporting data files. All concepts, if defined in the ICE supporting data, must be associated with 
 * an ICEConceptType, or may not be tracked by this class. Furthermore, no ICEConcept defined in the supporting data can be associated with more than one CdsListItem per 
 * ICEConceptType. This latter requirement is so that, on output for a request, the ICE response may be output using the locally coded values required by the client.
 */
public class SupportedCdsConcepts {

	private Map<ICEConceptType, Map<ICEConcept, LocallyCodedCdsListItem>> concepts;
	
	private static Log logger = LogFactory.getLog(SupportedCdsConcepts.class);
	
	
	protected SupportedCdsConcepts() {
		
		concepts = new EnumMap<ICEConceptType, Map<ICEConcept, LocallyCodedCdsListItem>>(ICEConceptType.class);
	}
	
	
	/**
	 * Add the supported ICE concept for the specified IceConceptType
	 * @param pICT The ICEConceptType this concept will be associated with
	 * @param PIC the concept to add
	 * @throws InconsistentConfigurationException if the supporting data supplied is inconsistent with prior supporting data that has already been provided
	 */
	protected void addSupportedCdsConcept(ICEConceptType pICT, ICEConcept pIC) {
		
		addSupportedCdsConceptWithCdsListItem(pICT, pIC, null);
	}
	
	
	/**
	 * Add the supported concept for the specified IceConceptType and associate the specified LocallyCodedCdsListItem with the concept. If either IceConceptType or ICEConcept 
	 * arguments are null, this method simply returns. If the LocallyCodedCdsListItem is null, the ICEConcept is simply added to the list of concepts being tracked without 
	 * a LocallyCodedCdsListItem 
	 * @param pICT The ICEConceptType this concept will be associated with
	 * @param pIC The concept to add
	 * @param pLCCLI The LocallyCodedCdsListItem (a.k.a. locally coded CdsListItem) with which to associate the ICEConcept with.
	 * @throws InconsistentConfigurationException if the supporting data supplied is inconsistent with prior supporting data that has already been provided. This will happen
	 * 		if ICEConcept is already associated with a LocallyCodedCdsListItem for the specified IceConceptType
	 */
	protected void addSupportedCdsConceptWithCdsListItem(ICEConceptType pICT, ICEConcept pIC, LocallyCodedCdsListItem pLCCLI) 
		throws InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportedCdsConceptWithCdsListItem(): ";
		
		if (pICT == null || pIC == null) {
			return;
		}

		Map<ICEConcept, LocallyCodedCdsListItem> lIceConceptEntry = this.concepts.get(pICT);
		boolean lIceConceptEntryForConceptTypeAdded = false;
		if (lIceConceptEntry == null) {
			lIceConceptEntry = new HashMap<ICEConcept, LocallyCodedCdsListItem>();
		}

		if (pLCCLI != null) {
			LocallyCodedCdsListItem priorAssociatedLCCLI = lIceConceptEntry.get(pIC);
			if (priorAssociatedLCCLI == null) {
				lIceConceptEntryForConceptTypeAdded = true;
				lIceConceptEntry.put(pIC, pLCCLI);
			}
			else {
				if (! priorAssociatedLCCLI.equals(pLCCLI)) {
					String lErrStr = "Attempt to map different LocallyCodedCdsListItem with ICEConcept previously mapped: IceConceptType: " + pICT + "; ICEConcept: " + 
						pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
					logger.warn(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
			}
		}
		
		if (lIceConceptEntryForConceptTypeAdded == true) {
			this.concepts.put(pICT, lIceConceptEntry);
			if (logger.isDebugEnabled()) {
				String lDebugStr = "SupportedCdsConcept ADDED. ICEConceptType: " + pICT + "; ICEConcept: " + pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
				logger.debug(_METHODNAME + lDebugStr);
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				String lDebugStr = "SupportedCdsConcept _NOT_ ADDED. ICEConceptType: " + pICT + "; ICEConcept: " + pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
				logger.debug(_METHODNAME + lDebugStr);
			}
		}
	}

	
	/**
	 * Return map of supported concepts for the given IceConceptType. Returns null if not found.
	 */
	protected Map<ICEConcept, LocallyCodedCdsListItem> getMapOfSupportedCdsConceptsForICEConceptType(ICEConceptType pICT) {

		if (pICT == null) {
			return null;
		}
		
		return this.concepts.get(pICT);
	}

	
	/**
	 * Return LocallyCodedCdsListItem for the given IceConceptType and ICEConcept. Returns null if not found.
	 */
	protected LocallyCodedCdsListItem getLocallyCodedCdsListItemForIceConceptTypeAndICEConcept(ICEConceptType pICT, ICEConcept pIC) {
		
		if (pICT == null || pIC == null) {
			return null;
		}
		
		Map<ICEConcept, LocallyCodedCdsListItem> lMap = this.concepts.get(pICT);
		if (lMap != null) {
			return lMap.get(pIC);
		}
		else {
			return null;
		}
	}
}
