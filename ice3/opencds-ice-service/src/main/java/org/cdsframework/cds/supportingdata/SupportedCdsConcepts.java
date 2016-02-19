package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.ICEConceptType;

/**
 * SupportedCdsConcepts is used to track the CAT-originated concepts defined in its supporting data files. All concepts, if defined in the supporting data, must be associated with 
 * an ICEConceptType, or may not be tracked by this class. Furthermore, no CdsConcept defined in the supporting data can be associated with more than one CdsListItem per 
 * ICEConceptType. This latter requirement is so that, on output for a request, the ICE response may be output using the locally coded values required by the client.
 */
public class SupportedCdsConcepts {

	private Map<ICEConceptType, Map<CdsConcept, LocallyCodedCdsListItem>> conceptTypeToConceptCdsListItemMap;
	private Map<LocallyCodedCdsListItem, Set<CdsConcept>> cdsListItemToConceptList;
	
	private static Log logger = LogFactory.getLog(SupportedCdsConcepts.class);
	
	
	public SupportedCdsConcepts() {
		
		conceptTypeToConceptCdsListItemMap = new EnumMap<ICEConceptType, Map<CdsConcept, LocallyCodedCdsListItem>>(ICEConceptType.class);
		cdsListItemToConceptList = new HashMap<LocallyCodedCdsListItem, Set<CdsConcept>>();
	}
	
	
	/**
	 * Add the supported ICE concept for the specified IceConceptType
	 * @param pICT The ICEConceptType this concept will be associated with
	 * @param PIC the concept to add
	 * @throws InconsistentConfigurationException if the supporting data supplied is inconsistent with prior supporting data that has already been provided
	 */
	public void addSupportedCdsConcept(ICEConceptType pICT, CdsConcept pIC) {
		
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
	 * 		if ICEConcept is already associated with a LocallyCodedCdsListItem for the specified IceConceptType. (So, although at the OpenCDS level, concepts may map to multiple 
	 * 		codes and code sets, at the supporting data level, only one code may be mapped to an ICEConcept [which may or may not also be an OpenCDS concept]).
	 */
	public void addSupportedCdsConceptWithCdsListItem(ICEConceptType pICT, CdsConcept pIC, LocallyCodedCdsListItem pLCCLI) 
		throws InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportedCdsConceptWithCdsListItem(): ";
		
		if (pICT == null || pIC == null) {
			return;
		}

		Map<CdsConcept, LocallyCodedCdsListItem> lIceConceptEntry = this.conceptTypeToConceptCdsListItemMap.get(pICT);
		if (lIceConceptEntry == null) {
			lIceConceptEntry = new HashMap<CdsConcept, LocallyCodedCdsListItem>();
		}
		if (pLCCLI != null) {
			Set<CdsConcept> lICEConceptListAssocWCdsListItem = this.cdsListItemToConceptList.get(pLCCLI);
			LocallyCodedCdsListItem priorAssociatedLCCLI = lIceConceptEntry.get(pIC);
			if (priorAssociatedLCCLI == null) {
				lIceConceptEntry.put(pIC, pLCCLI);
				// Record the Map<ICEConceptType, Map<ICEConcept, LocallyCodedCdsListItem> entry
				this.conceptTypeToConceptCdsListItemMap.put(pICT, lIceConceptEntry);
				// Record the Map<LocallyCodedCdsListItem, List<ICEConcept>> entry
				if (lICEConceptListAssocWCdsListItem == null || ! lICEConceptListAssocWCdsListItem.contains(pIC)) {
					if (lICEConceptListAssocWCdsListItem == null) {
						lICEConceptListAssocWCdsListItem = new HashSet<CdsConcept>();
					}
					lICEConceptListAssocWCdsListItem.add(pIC);
					this.cdsListItemToConceptList.put(pLCCLI, lICEConceptListAssocWCdsListItem);
				}
				if (logger.isDebugEnabled()) {
					String lDebugStr = "SupportedCdsConcept with LocallyCodedCdsListItem ADDED. ICEConceptType: " + pICT + "; ICEConcept: " + pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
					logger.debug(_METHODNAME + lDebugStr);
				}
			}
			else {
				if (! priorAssociatedLCCLI.equals(pLCCLI)) {
					String lErrStr = "Attempt to map different LocallyCodedCdsListItem with ICEConcept previously mapped: ICEConceptType: " + pICT + "; ICEConcept: " + 
						pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
					logger.warn(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
				else {
					if (logger.isDebugEnabled()) {
						String lDebugStr = "Encountered SupportedCdsConcept with _duplicate_ LocallyCodedCdsListItem (ignored): ICEConceptType: " + pICT + "; ICEConcept: " + pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
						logger.debug(_METHODNAME + lDebugStr);
					}
				}
			}
		}
		else {
			if (lIceConceptEntry.containsKey(pIC)) {
				if (lIceConceptEntry.get(pICT) != null) {
					String lErrStr = "Attempt to map different LocallyCodedCdsListItem with ICEConcept previously mapped: IceConceptType: " + pICT + "; ICEConcept: " + 
							pIC + "; LocallyCodedCdsListItem: " + pLCCLI;
					logger.warn(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);					
				}
			}
			else {
				this.conceptTypeToConceptCdsListItemMap.put(pICT, null);
			}
		}
	}

	
	/**
	 * Return map of supported concepts for the given ICEConceptType. Returns null if not found.
	 */
	public Map<CdsConcept, LocallyCodedCdsListItem> getCdsConceptsAssociatedWithICEConceptType(ICEConceptType pICT) {

		if (pICT == null) {
			return null;
		}
		
		return this.conceptTypeToConceptCdsListItemMap.get(pICT);
	}

	
	/**
	 * Return true if any supported concepts for the given ICEConceptType exist, false if not.
	 */
	public boolean existsSupportedConceptsForSpecifiedICEConceptType(ICEConceptType pICT) {
		
		if (pICT == null) {
			return false;
		}
		
		if (this.conceptTypeToConceptCdsListItemMap.containsKey(pICT)) {
			Map<CdsConcept, LocallyCodedCdsListItem> lConceptCdsList = this.conceptTypeToConceptCdsListItemMap.get(pICT);
			if (lConceptCdsList != null && lConceptCdsList.size() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Return list of ICEConcepts that is also an OpenCDS concept for the given LocallyCodedCdsListItem. Returns empty list if none found 
	 */
	public Collection<CdsConcept> getOpenCDSICEConceptsAssociatedWithCdsListItem(LocallyCodedCdsListItem pLCCLI) {
		
		if (pLCCLI == null) {
			return new ArrayList<CdsConcept>();
		}
		
		Set<CdsConcept> lSetOfICEConceptsForLC = this.cdsListItemToConceptList.get(pLCCLI);
		if (lSetOfICEConceptsForLC == null) {
			return new ArrayList<CdsConcept>();
		}
		List<CdsConcept> lApplicableOpenCDSConcepts = new ArrayList<CdsConcept>();
		for (CdsConcept lIC : lSetOfICEConceptsForLC) {
			if (lIC != null && lIC.isOpenCdsSupportedConcept()) {
				lApplicableOpenCDSConcepts.add(lIC);
			}
		}
		return lApplicableOpenCDSConcepts;
		
	}
	
	/**
	 * Return list of ICEConcepts for the given LocallyCodedCdsListItem. Returns empty list if none found 
	 */
	public Collection<CdsConcept> getICEConceptsAssociatedWithCdsListItem(LocallyCodedCdsListItem pLCCLI) {
		
		if (pLCCLI == null) {
			return new ArrayList<CdsConcept>();
		}
		
		Set<CdsConcept> lSetOfICEConceptsForLC = this.cdsListItemToConceptList.get(pLCCLI);
		if (lSetOfICEConceptsForLC == null) {
			return new ArrayList<CdsConcept>();
		}
		else {
			return lSetOfICEConceptsForLC;
		}
	}
	
	
	/**
	 * Return LocallyCodedCdsListItem for the given IceConceptType and ICEConcept. Returns null if not found.
	 */
	public LocallyCodedCdsListItem getCdsListItemAssociatedWithICEConceptTypeAndICEConcept(ICEConceptType pICT, CdsConcept pIC) {
		
		if (pICT == null || pIC == null) {
			return null;
		}
		
		Map<CdsConcept, LocallyCodedCdsListItem> lMap = this.conceptTypeToConceptCdsListItemMap.get(pICT);
		if (lMap != null) {
			return lMap.get(pIC);
		}
		else {
			return null;
		}
	}
}
