package org.cdsframework.cds.supportingdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdsframework.cds.CdsConcept;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.supportingdata.ICEConceptType;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.internal.datatypes.CD;


/**
 * Creates and manages CdsLists, CdsListItems, and SupportedCdsConcepts.
 */
public class SupportedCdsLists implements SupportingData {
	
	/**
	 * Representative of a concept and one associated local code, that can be represented by an enumeration as follows (for example):
	 *        _RECOMMENDED_RECOMMENDATION_STATUS("ICE219", "Recommended", "2.16.840.1.113883.3.795.12.100.5", "ICE3 Immunization Recommendation", "RECOMMENDED", "Due Now"),
	 *        private CD supportedRecommendationConcept;
	 *        private CD localRecommendationCodeConcept;
	 * 
	 * Note that references to "cdsListName" below are equivalent to LocallyCodedCdsListItem.getCdsListCode()
	 */
	private List<String> cdsVersions;												// Supported CDS List Versions
	private Map<String, LocallyCodedCdsListItem> cdsListItemNameToCdsListItem;		// LOCAL CODE-RELATED: cdsListCode.cdsListItemKey -> CodedCdsListItem
	private Map<String, String> cdsListNameToCodeSystem;							// LOCAL CODE-RELATED: cdsListCode -> cdsListCodeSystem (to ensure there are no conflicts)	
	private Map<String, String> codeSystemToCdsListName;							// LOCAL CODE-RELATED: cdsListCodeSystem -> cdsListCode (to ensure there are no conflicts)	
	private Map<String, Integer> countOfCdsListItemsPerCdsList;						// LOCAL CODE-RELATED: cdsListCode -> number of cdsListItemCodes for the cdsList
	private Map<String, Set<LocallyCodedCdsListItem>> cdsListNameToCdsListItems; 	// LOCAL CODE-RELATED: cdsListCode -> Set of CodedCdsListItems
	private boolean isSupportingDataConsistent;
	
	// This class provides public methods for other classes to populate the IceConceptType.OPENCDS supported concepts, but other IceConceptType concepts
	// are populated by this class when reading the XML. Concepts are stored in the following structure.
	private SupportedCdsConcepts supportedCdsConcepts;

	private static final Logger logger = LogManager.getLogger();
		
	
	public SupportedCdsLists(List<String> pCdsVersions) {
		
		if (pCdsVersions == null) {
			this.cdsVersions = new ArrayList<String>();
		}
		else {
			this.cdsVersions = pCdsVersions;
		}
		this.cdsListItemNameToCdsListItem = new HashMap<String, LocallyCodedCdsListItem>();
		this.cdsListNameToCodeSystem = new HashMap<String, String>();
		this.codeSystemToCdsListName = new HashMap<String, String>();
		this.countOfCdsListItemsPerCdsList = new HashMap<String, Integer>();
		this.cdsListNameToCdsListItems = new HashMap<String, Set<LocallyCodedCdsListItem>>();
		this.supportedCdsConcepts = new SupportedCdsConcepts();
		this.isSupportingDataConsistent = true;
	}
	
	
	public boolean isEmpty() {		
		if (this.cdsListItemNameToCdsListItem.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public boolean isSupportingDataConsistent() {
		return this.isSupportingDataConsistent;
	}
	
	
	public List<String> getCdsVersions() {
		return this.cdsVersions;
	}
	
	
	/**
	 * Adds an individual CdsListItem to the map of supported list concepts tracked by this class. This is currently a private method as all CdsListItems in the  
	 * CdsListSpecificationFile should ideally be added all at once.
	 * @param pCdsListSpecificationFile
	 * @param pCdsListItem
	 * @throws ImproperUsageException If the caller tries to add a SupportedListConcept that has already been added to the supported list concepts; or if the specified CdsListItem
	 * 	is not an item in the CdsListSpecificationFile, or if not all required elements have been populated 
	 */
	private void addSupportedCdsListItemAndConcept(CdsListSpecificationFile pCdsListSpecificationFile, CdsListItem pCdsListItem) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportedListConcept(): ";
		
		if (pCdsListSpecificationFile == null || pCdsListItem == null) {
			return;
		}
		
		// If adding a code that is not one of the supported cdsVersions, then return
		Collection<String> lCdsVersions = CollectionUtils.intersectionOfStringCollections(pCdsListSpecificationFile.getCdsVersions(), this.cdsVersions);
		if (lCdsVersions == null || lCdsVersions.size() == 0) {
			return;
		}
		
		LocallyCodedCdsListItem slci = new LocallyCodedCdsListItem(pCdsListSpecificationFile, pCdsListItem);
		String lSupportedListItemName = slci.getCdsListItemName();
		if (this.cdsListItemNameToCdsListItem.containsKey(lSupportedListItemName)) {
			String lErrStr = "Attempt to add duplicate SupportedListItem: cannot add a supported list concept that already been added; must first remove the prior SupportedCdsListItem of the same name " + 
				lSupportedListItemName;
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);
		}

		String lSLCCdsListCode = slci.getCdsListCode();
		String lSLCCdsListCodeSystem = slci.getCdsListCodeSystem();
		if (lSLCCdsListCode == null || lSLCCdsListCodeSystem == null) {
			String lErrStr = "Attempt to add an unpopulated CdsList code or CdsList Code System";
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new ImproperUsageException(lErrStr);
		}
		
		// Consistency Check: For Cds List Code -> Code System; if mapping already present, enforce consistency of the previous mapping
		String lPreviousMappedCdsListCodeSystem = cdsListNameToCodeSystem.get(lSLCCdsListCode);
		if (lPreviousMappedCdsListCodeSystem != null && ! lSLCCdsListCodeSystem.equals(lPreviousMappedCdsListCodeSystem)) {
			String lErrStr = "Attempt to add a supported list concept whose code system does not match the code system of a previously previously added concept by the same cdsListCode";
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);				
		}		
		
		// Consistency check: For Cds List Code System -> Cds List Code; if mapping already present, enforce consistency of the previous mapping
		String lPreviousMappedCdsListName = this.codeSystemToCdsListName.get(lSLCCdsListCodeSystem);
		if (lPreviousMappedCdsListName != null && ! lPreviousMappedCdsListName.equals(lPreviousMappedCdsListName)) {
			String lErrStr = "Attempt to add a supported list concept whose cdsList code does not match the cdsList code of a previously previously added concept by the same code system";
			logger.error(_METHODNAME + lErrStr);
			this.isSupportingDataConsistent = false;
			throw new InconsistentConfigurationException(lErrStr);
		}

		//
		// Add the CdsList Name-> Code System and Code System -> CdsList Name mappings
		if (lPreviousMappedCdsListCodeSystem == null) {
			this.cdsListNameToCodeSystem.put(slci.getCdsListCode(), slci.getCdsListCodeSystem());
		}
		if (lPreviousMappedCdsListName == null) {
			this.codeSystemToCdsListName.put(slci.getCdsListCodeSystem(), slci.getCdsListCode());
		}

		///////
		// Add the mapping from the CdsListItem Name to CdsListItem
		///////
		this.cdsListItemNameToCdsListItem.put(lSupportedListItemName, slci);
		
		///////
		// Keep track of the number of mappings of Cds List Items per locally coded cds lists
		///////
		Integer lCountOfCdsListItems = this.countOfCdsListItemsPerCdsList.get(lSLCCdsListCode);
		if (lCountOfCdsListItems == null) {
			this.countOfCdsListItemsPerCdsList.put(lSLCCdsListCode, new Integer(1));
		}
		else {
			this.countOfCdsListItemsPerCdsList.put(lSLCCdsListCode, new Integer(lCountOfCdsListItems.intValue()+1));
		}
		
		///////
		// Keep track of all of the CdsListItems associated with the CdsList
		///////
		Set<LocallyCodedCdsListItem> lcclis = this.cdsListNameToCdsListItems.get(lSLCCdsListCode);
		if (lcclis == null) {
			lcclis = new HashSet<LocallyCodedCdsListItem>();
		}
		lcclis.add(slci);
		this.cdsListNameToCdsListItems.put(lSLCCdsListCode, lcclis);
		
		////////////// Supported Concepts initialization START //////////////
		///////
		// Add the OpenCDS Concepts (if any) to SupportedConcepts, only if the CdsList is of an IceConceptType
		///////
		Collection<CdsConcept> lConcepts = slci.getCdsListItemOpencdsConceptMappings();
		for (CdsConcept lC : lConcepts) {
			lC.setIsOpenCdsSupportedConcept(true);
			this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(ICEConceptType.OPENCDS, lC, slci);
		}
		
		///////
		// Add this CdsListItem as an ICEConcept of the correct type (Disease, Evaluation, Recommendation, etc. (that is, only if the CdsList is of an IceConceptType)
		///////
		ICEConceptType lIceConceptType = ICEConceptType.getSupportedIceConceptType(lSLCCdsListCode);
		if (lIceConceptType != null) {
			CdsConcept lIC = new CdsConcept(lSupportedListItemName);			// Not an OpenCDS concept
			lIC.setIsOpenCdsSupportedConcept(false);
			this.supportedCdsConcepts.addSupportedCdsConceptWithCdsListItem(lIceConceptType, lIC, slci);
		}
		
		////////////// Supported Concepts initialization END //////////////
		
	}

	
	public void addSupportedCdsListItemsAndConceptsFromCdsListSpecificationFile(CdsListSpecificationFile pCdsListSpecificationFile) 
		throws InconsistentConfigurationException {
		
		// String _METHODNAME = "addSupportedCdsListItemsAndConceptsFromCdsListSpecificationFile(): ";
		
		if (pCdsListSpecificationFile == null) {
			return;
		}
		if (pCdsListSpecificationFile != null) {
			List<CdsListItem> lcli = pCdsListSpecificationFile.getCdsListItems();
			try {
				/*
				// Check to make sure that the cdsListCode was never provided by a different file; currently do not support the same cdsListCode across different files
				for (CdsListItem cli : lcli) {
					if (cli != null) {
						LocallyCodedCdsListItem slci = new LocallyCodedCdsListItem(pCdsListSpecificationFile, cli);
						ICEConceptType lIceConceptType = ICEConceptType.getSupportedIceConceptType(slci.getCdsListCode());
						if (lIceConceptType != null) {
							if (this.supportedCdsConcepts.existsSupportedConceptsForSpecifiedICEConceptType(lIceConceptType) == true) {
								String lErrStr = "cdsListSpecificationFile cdsListCode \"" + slci.getCdsListCode() + "\" already specified in a different file; cannot continue";
								logger.error(_METHODNAME + lErrStr);
								throw new InconsistentConfigurationException(lErrStr);
							}
							else {
								break;
							}
						}
						// continue until first non-null entry is found and verified
					}
				}
				*/
				for (CdsListItem cli : lcli) {
					addSupportedCdsListItemAndConcept(pCdsListSpecificationFile, cli);
				}
			}
			catch (ImproperUsageException iue) {
				throw new InconsistentConfigurationException(iue.getMessage());
			}
		}
	}
	
	
	public void removeSupportedCdsListItem(String pSupportedCdsListItemName) {
		
		if (pSupportedCdsListItemName != null) { 
			LocallyCodedCdsListItem lSLCI = cdsListItemNameToCdsListItem.remove(pSupportedCdsListItemName);
			if (lSLCI != null) {
				Integer lCountOfCdsListItems = this.countOfCdsListItemsPerCdsList.get(lSLCI.getCdsListCode());
				if (lCountOfCdsListItems != null) {
					int count = lCountOfCdsListItems.intValue();
					if (count > 0) {
						this.countOfCdsListItemsPerCdsList.put(lSLCI.getCdsListCode(), new Integer(count-1));
					}
				}
			}
		}
	}
	

	/**
	 * Return the associated Supported Cds Concepts; will never be null
	 */
	public SupportedCdsConcepts getSupportedCdsConcepts() {
		
		return this.supportedCdsConcepts;
	}
	
	/**
	 * Obtain the Cds List Code associated with a specified code system. 
	 * @param pCodeSystem Code System
	 * @return cdsListCode value associated with the specified code system
	 */
	public String getCdsListCodeAssociatedWithCodeSystem(String pCodeSystem) {
		if (pCodeSystem == null) {
			return null;
		}
		
		return this.codeSystemToCdsListName.get(pCodeSystem);
	}

	
	/**
	 * Obtain Code System associated with a specified Cds List Code
	 * @param pCdsListCode
	 * @return Code System associated with the Cds List Code, or null if there is no associated code system
	 */
	public String getCodeSystemAssociatedWithCdsListCode(String pCdsListCode) {
		if (pCdsListCode == null) {
			return null;
		}
		
		return this.cdsListNameToCodeSystem.get(pCdsListCode);
	}
	
	
	/**
	 * Get all locally coded cds list items associated with a Cds List code
	 * @param pCdsListCode
	 * @return Set of LocallyCodedCdsItem objects associated with the Cds List, or null if there are no associated cds list items
	 */
	public Set<LocallyCodedCdsListItem> getCdsListItemsAssociatedWithCdsListCode(String pCdsListCode) {
		
		if (pCdsListCode == null) {
			return null;
		}
		
		return this.cdsListNameToCdsListItems.get(pCdsListCode);
	}
	
	
	/**
	 * Check to see (by name) if CdsListItem exists
	 * @return true if CdsListItem is found; false if not 
	 */
	public boolean cdsListItemExists(String pCdsListItemName) {
		
		if (pCdsListItemName == null) {
			return false;
		}
		
		if (this.cdsListItemNameToCdsListItem.containsKey(pCdsListItemName)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Check to see (by CD) if CdsListItem exists
	 * @return true if CdsListItem is found; false if not
	 */
	public boolean cdsListItemExists(CD pCdsListItemCD) {
		
		if (pCdsListItemCD == null) {
			return false;
		}
		
		String lCdsListCode = getCdsListCodeAssociatedWithCodeSystem(pCdsListItemCD.getCodeSystem());
		String lCdsListItemKey = pCdsListItemCD.getCode();
		String lCdsListItemName = lCdsListCode + "." + lCdsListItemKey;
		return cdsListItemExists(lCdsListItemName);
	}

	
	/**
	 * Obtain the CdsListItem associated with a CD
	 * @param pCdsListCD
	 * @return CdsLIstItem, or null if not found
	 */
	public LocallyCodedCdsListItem getCdsListItem(CD pCdsListCD) {
		
		if (pCdsListCD == null || pCdsListCD.getCode() == null || pCdsListCD.getCodeSystem() == null) {
			return null;
		}
		
		String lCdsListCode = getCdsListCodeAssociatedWithCodeSystem(pCdsListCD.getCodeSystem());
		String lCdsListItemKey = pCdsListCD.getCode();
		String lCdsListItemName = lCdsListCode + "." + lCdsListItemKey;
		return getCdsListItem(lCdsListItemName);
	}
	
	
	/**
	 * Obtain the CdsListItem by name (<CdsListCode>.<CdsListItemName>).
	 * @param pCdsListItemName
	 * @return CdsListItem, or null if not found
	 */
	public LocallyCodedCdsListItem getCdsListItem(String pCdsListItemName) {
		
		if (pCdsListItemName == null) {
			return null;
		}
		LocallyCodedCdsListItem lSLCI = cdsListItemNameToCdsListItem.get(pCdsListItemName);
		if (lSLCI == null) {
			return null;
		}
		else {
			return lSLCI;
		}
	}
	
	
	@Override
	public String toString() {
		
		// First build string of the list of LocallyCodedCdsListItems stored in the cdsListItemNameToCdsListItem map.
		Collection<LocallyCodedCdsListItem> slcic = this.cdsListItemNameToCdsListItem.values();
		int i = 1;
		String ltoStringStr = "[ ";
		for (LocallyCodedCdsListItem slci : slcic) {
			ltoStringStr += "\n{" + i + "} " + slci.toString();
			i++;
		}
		
		// Second build string of the associated SupportedConcepts
		ltoStringStr += this.supportedCdsConcepts.toString();
		
		return ltoStringStr;
	}
	
	
	// TODO:
	/**
	 * Concepts ; no local code mapping.
	 * supportedCdsListTypes = { "AD_HOC", "CONCEPT", "CONCEPT_TYPE" }; 
	 * 	e.g.
	 * <ns2:CdsListSpecificationFile xmlns:ns2="org.cdsframework.util.support.data.cds.list" xmlns:ns3="org.cdsframework.util.support.data.cds.version" xmlns:ns4="http://www.omg.org/spec/CDSS/201105/dss">
    	<listAlias>TEST_DURATION_TYPE</ns2:listAlias>
    	<listName>TEST - Duration Type</ns2:listName>
    	<listType>AD_HOC</ns2:listType>
    	<cdsListItem>
        	<cdsListItemKey>DurationType.Days</ns2:cdsListItemKey>
        	<cdsListItemValue>Days</ns2:cdsListItemValue>
    	</cdsListItem>
    	<cdsVersion> </cdsVersion>
    	</cdsListSpecificationFile>
	 *
	 * @author daryl
	 *
	private class SupportedCdsConceptItem {
		
		private SupportedCdsConceptItem() {
			
		}
		
	}
	
	 */
	

	
}
