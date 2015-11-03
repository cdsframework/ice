/**
 * Copyright (C) 2015 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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

package org.cdsframework.ice.supportingdata;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.ice.util.ConceptUtils;
import org.cdsframework.ice.util.XMLSupportingDataFilenameFilterImpl;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListItemConceptMapping;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.cdsframework.util.support.data.ice.vaccine.IceVaccineSpecificationFile;
import org.cdsframework.util.support.data.ice.vaccinegroup.IceVaccineGroupSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.schema.CD;


public class ICESupportingDataHelper {

	/**
	 * e.g.
	 * 	_RECOMMENDED_IN_FUTURE_REASON("ICE225", "Immunization Recommended in Future (Due In Future)", "2.16.840.1.113883.3.795.12.100.6", "ICE3 Immunization Recommendation Reason", 
	 *		"DUE_IN_FUTURE", "Immunization Recommended in Future (Due In Future)");
	 */
	private List<String> supportedCdsVersions;
	private List<File> supportingDataDirectories;

	private static final String _ICE_CDS_LIST_SPECIFICATION_FILE_XML_NAMESPACE = "org.cdsframework.util.support.data.cds.list";
	private static final String _ICE_VACCINE_GROUP_SPECIFICATION_FILE_XML_NAMESPACE = "org.cdsframework.util.support.data.ice.vaccinegroup";
	private static final String _ICE_VACCINE_SPECIFICATION_FILE_XML_NAMESPACE = "org.cdsframework.util.support.data.ice.vaccine";
	
	private static Log logger = LogFactory.getLog(ICESupportingDataHelper.class);
	
	
	public ICESupportingDataHelper(List<String> pSupportedCdsVersions, List<File> pSupportingDataDirectories) 
		throws ImproperUsageException {
		
		String _METHODNAME = "ICESupportingDataHelper(): ";
		if (pSupportingDataDirectories == null) {
			String lErrStr = "Supporting data directory(ies) not specified; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		for (File lSupportingDataDirectory : pSupportingDataDirectories) {
			if (! isSupportingDirectoryPresent(lSupportingDataDirectory)) {
				String lErrStr = "Specified supporting data directory " + (lSupportingDataDirectory == null ? "" : lSupportingDataDirectory.getAbsolutePath()) + " does not exist";
				logger.error(_METHODNAME + lErrStr);
				throw new ImproperUsageException(lErrStr);
			}
		}
		if (pSupportedCdsVersions == null || pSupportedCdsVersions.isEmpty()) {
			String lErrStr = "Applicable CDS versions for this set of supporting data not specified; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}

		this.supportingDataDirectories = pSupportingDataDirectories;
	}
	
	
	/**
	 * Given a parent directory in File and sub-directory represented as a string, returns true, if the directory is present (and accessible), and false is not.
	 * @param pParentDirectory
	 * @param pChildDirectory
	 * @return
	 */
	public static boolean isSupportingDirectoryAndSubDirectoryPresent(File pParentDirectory, String pChildDirectory) {
		
		if (pParentDirectory == null) {
			return false;
		}
		File lSD = null;
		if (pChildDirectory == null || pChildDirectory.length() == 0) {
			lSD = pParentDirectory;
		}
		else {
			lSD = new File(pParentDirectory, pChildDirectory);
		}
		if (lSD.isDirectory() == false) {
			return false;
		}
		else {
			return true;
		}
	}
	

	/**
	 * Given a directory represented as a File, returns true if the directory is present (and accessible), false if not
	 * @param pDirectory
	 * @return
	 */
	public static boolean isSupportingDirectoryPresent(File pDirectory) {
		
		return isSupportingDirectoryAndSubDirectoryPresent(pDirectory, null);
	}
	
	
	/**
	 * Initialize the Coded Concepts from CdsListSpecificationFile XML supporting data files
	 * @param pSupportingDataDirectory
	 * @param pSupportingDataChildDirectory
	 * @return
	 * @throws ImproperUsageException
	 */
	public SupportedCdsLists initializeCodeConcepts(String pSupportingDataChildDirectory) 
		throws ImproperUsageException {
		
		String _METHODNAME = "initializeCodeConcepts(): ";

		SupportedCdsLists lSLC = new SupportedCdsLists(this.supportedCdsVersions);
		for (File lSupportingDatadirectory : this.supportingDataDirectories) {
			File lCodedConceptsDirectory = new File(lSupportingDatadirectory, pSupportingDataChildDirectory);
			try {
				// TODO: Make configurable
				JAXBContext jc = JAXBContext.newInstance(_ICE_CDS_LIST_SPECIFICATION_FILE_XML_NAMESPACE);
				Unmarshaller lUnmarshaller = jc.createUnmarshaller();
				FilenameFilter lFF = new XMLSupportingDataFilenameFilterImpl();
				String[] lSDFiles = lCodedConceptsDirectory.list(lFF);
				if (lSDFiles == null) {
					String lErrStr = "An error occurred obtaining list of supporting data files";
					logger.error(_METHODNAME + lErrStr);
					throw new RuntimeException(lErrStr);
				}

				for (String lSDFile : lSDFiles) {
					if (logger.isDebugEnabled()) {
						logger.debug("Parsing supporting data file: \"" + lSDFile + "\"");
					}
					////// JAXBElement<?> cdslsfElement = (JAXBElement<?>) u.nmarshall(new File(lCodedConceptsDirectory, lSDFile));
					File lCdsListFile = new File(lCodedConceptsDirectory, lSDFile);
					CdsListSpecificationFile cdslsf = (CdsListSpecificationFile) lUnmarshaller.unmarshal(lCdsListFile);
					addSupportingListConceptsFromCdsListSpecificationFile(cdslsf, lCdsListFile, lSLC);
				}
			}
			catch (JAXBException jaxbe) {
				String lErrStr = "encountered an exception processing supporting data file";
				logger.error(_METHODNAME + lErrStr, jaxbe);
				throw new RuntimeException(lErrStr);
			}
		}
		return lSLC;
	}
	
	
	/**
	 * Initialize the Vaccine Group Concepts from supporting iceVaccineGroupSpecificationFile XML data files  
	 * @param pSupportingDataDirectory
	 * @param pChildDirectory
	 * @return
	 * @throws ImproperUsageException
	 */
	public SupportedCdsVaccineGroups initializeVaccineGroupSupportingData(String pChildDirectory, SupportedCdsLists pSupportedCdsLists) 
		throws ImproperUsageException {
		
		String _METHODNAME = "initializeVaccineGroupSupportingData(): ";

		SupportedCdsVaccineGroups lSVGC = new SupportedCdsVaccineGroups(this.supportedCdsVersions);
		for (File lSupportingDataDirectory : this.supportingDataDirectories) {
			File lVaccineGroupsDirectory = new File(lSupportingDataDirectory, pChildDirectory);
			try {
				// TODO: 
				JAXBContext jc = JAXBContext.newInstance(_ICE_VACCINE_GROUP_SPECIFICATION_FILE_XML_NAMESPACE);
				Unmarshaller lUnmarshaller = jc.createUnmarshaller();
				FilenameFilter lFF = new XMLSupportingDataFilenameFilterImpl();
				String[] lSDFiles = lVaccineGroupsDirectory.list(lFF);
				if (lSDFiles == null) {
					String lErrStr = "An error occurred obtaining list of supporting data files";
					logger.error(_METHODNAME + lErrStr);
					throw new RuntimeException(lErrStr);
				}

				for (String lSDFile : lSDFiles) {
					if (logger.isDebugEnabled()) {
						logger.debug("Parsing supporting data file: \"" + lSDFile + "\"");
					}
					File lIceVGFile = new File(lVaccineGroupsDirectory, lSDFile);
					IceVaccineGroupSpecificationFile icevgf = (IceVaccineGroupSpecificationFile) lUnmarshaller.unmarshal(lIceVGFile);
					addSupportingVaccineGroupConceptsFromIceVaccineGroupSpecificationFile(icevgf, lIceVGFile, lSVGC, pSupportedCdsLists);
				}
			}
			catch (JAXBException jaxbe) {
				String lErrStr = "encountered an exception processing supporting data file";
				logger.error(_METHODNAME + lErrStr, jaxbe);
				throw new RuntimeException(lErrStr);
			}
		}
		return lSVGC;
	}


	/**
	 * Initialize the Vaccine Group Concepts from supporting iceVaccineGroupSpecificationFile XML data files  
	 * @param pSupportingDataDirectory
	 * @param pChildDirectory
	 * @return
	 * @throws ImproperUsageException
	 */
	public SupportedCdsVaccines initializeVaccineSupportingData(String pChildDirectory, SupportedCdsLists pSupportedCdsLists) 
		throws ImproperUsageException {	
	
		String _METHODNAME = "initializeVaccineSupportingData(): ";

		SupportedCdsVaccines lSVC = new SupportedCdsVaccines(this.supportedCdsVersions);
		for (File lSupportingDataDirectory : this.supportingDataDirectories) {
			File lVaccineDirectory = new File(lSupportingDataDirectory, pChildDirectory);
			try {
				// TODO: 
				JAXBContext jc = JAXBContext.newInstance(_ICE_VACCINE_SPECIFICATION_FILE_XML_NAMESPACE);
				Unmarshaller lUnmarshaller = jc.createUnmarshaller();
				FilenameFilter lFF = new XMLSupportingDataFilenameFilterImpl();
				String[] lSDFiles = lVaccineDirectory.list(lFF);
				if (lSDFiles == null) {
					String lErrStr = "An error occurred obtaining list of supporting data files";
					logger.error(_METHODNAME + lErrStr);
					throw new RuntimeException(lErrStr);
				}

				for (String lSDFile : lSDFiles) {
					if (logger.isDebugEnabled()) {
						logger.debug("Parsing supporting data file: \"" + lSDFile + "\"");
					}
					File lIceVFile = new File(lVaccineDirectory, lSDFile);
					IceVaccineSpecificationFile icevf = (IceVaccineSpecificationFile) lUnmarshaller.unmarshal(lIceVFile);
					addSupportingVaccineConceptsFromIceVaccineSpecificationFile(icevf, lIceVFile, lSVC, pSupportedCdsLists);
				}
			}
			catch (JAXBException jaxbe) {
				String lErrStr = "encountered an exception processing supporting data file";
				logger.error(_METHODNAME + lErrStr, jaxbe);
				throw new RuntimeException(lErrStr);
			}
		}
		
		return lSVC;		
	}
	
	
	private static void addSupportingVaccineConceptsFromIceVaccineSpecificationFile(IceVaccineSpecificationFile pIceVaccineSpecification, File pVaccineFile,
			SupportedCdsVaccines pSVs, SupportedCdsLists pSupportedCdsLists) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportingVaccineConceptsFromIceVaccineSpecificationFile(): ";
		if (pVaccineFile == null) {
			String lErrStr = "Vaccine supporting data file not specified; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		if (pIceVaccineSpecification == null || pSVs == null) {
			logger.warn(_METHODNAME + "IceVaccineSpecificationFile and/or SupportedCdsVaccineConcepts object not specified; read of supporting data file skipped");
			return;
		}
		
		CD lVaccineCD = pIceVaccineSpecification.getVaccine();
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineCD)) {
			String lErrStr = "Required supporting data item vaccine element not provided in IceVaccineSpecificationFile: " + pVaccineFile.getAbsolutePath();
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		String lDebugStrb = "";
		if (logger.isDebugEnabled()) { 
			lDebugStrb += _METHODNAME + pIceVaccineSpecification.getClass().getName();

			// Vaccine
			lDebugStrb += "\ngetVaccine(): " + ConceptUtils.toStringCD(lVaccineCD);;
			
			// CdsVersions
			List<String> lCdsVersions = pIceVaccineSpecification.getCdsVersions();
			lDebugStrb += "\ngetCdsVersions(): ";
			int i=1;
			if (lCdsVersions != null) {
				for (String lCdsVersion : lCdsVersions) {
					lDebugStrb += "\n\t(" + i + "): " + lCdsVersion;
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo CdsVersion information supplied";
			}
			
			// Tradename
			lDebugStrb += "\nVaccine Trade Name: " + pIceVaccineSpecification.getTradeName();

			// Manufacturer
			lDebugStrb += "\nManufacturer: " + ConceptUtils.toStringCD(pIceVaccineSpecification.getManufacturerCode());
			
			// Live virus vaccine
			lDebugStrb += "\nLive Virus: " + pIceVaccineSpecification.isLiveVirusVaccine().toString();
			
			// age restrictions
			lDebugStrb += "\nValid Minimum Age: " + pIceVaccineSpecification.getValidMinimumAgeForUse();
			lDebugStrb += "\nValid Maximum Age: " + pIceVaccineSpecification.getValidMaximumAgeForUse();
			lDebugStrb += "\nLicensed Minimum Age: " + pIceVaccineSpecification.getLicensedMinimumAgeForUse();
			lDebugStrb += "\nLicensed Maximum Age: " + pIceVaccineSpecification.getLicensedMaximumAgeForUse();
			
			// Targeted diseases
			lDebugStrb += "\ngetDiseaseImmunities(): ";
			List<CD> lRelatedDiseases = pIceVaccineSpecification.getDiseaseImmunities();
			i=1;
			if (lRelatedDiseases != null) {
				for (CD lRelatedDisease : lRelatedDiseases) {
					lDebugStrb += "\n\t(" + i + "): " + ConceptUtils.toStringCD(lRelatedDisease);
				}
			}
			else {
				lDebugStrb += "\n\tNo target disease information supplied";
			}
			
			// Vaccine components
			lDebugStrb += "\ngetVaccineComponents(): ";
			List<CD> lVaccineComponents = pIceVaccineSpecification.getVaccineComponents();
			i=1;
			if (lVaccineComponents != null) {
				for (CD lVaccineComponent : lVaccineComponents) {
					lDebugStrb += "\n\t(" + i + "): " + ConceptUtils.toStringCD(lVaccineComponent);
				}
			}
			else {
				lDebugStrb += "\n\tNo vaccine component information supplied";
			}
			
			// Conflicting vaccines
			lDebugStrb += "\ngetConflictingVaccines(): ";
			List<CD> lConflictingVaccines = pIceVaccineSpecification.getConflictingVaccines();
			i=1;
			if (lConflictingVaccines != null) {
				for (CD lConflictingVaccine : lConflictingVaccines) {
					lDebugStrb += "\n\t(" + i + "): " + ConceptUtils.toStringCD(lConflictingVaccine);
				}
			}
			else {
				lDebugStrb += "\n\tNo conflict vaccines supplied";
			}
			
			// OpenCDS memberships
			lDebugStrb += "\ngetOpenCdsMemberships(): ";
			List<CD> lOpenCdsMemberships = pIceVaccineSpecification.getOpenCdsMemberships();
			i=1;
			if (lOpenCdsMemberships != null) {
				for (CD lOpenCdsMembership : lOpenCdsMemberships) {
					lDebugStrb += "\n\t(" + i + "): " + ConceptUtils.toStringCD(lOpenCdsMembership);
				}
			}
			else {
				lDebugStrb += "\n\tNo OpenCDS memberships supplied";
			}
			
			// Primary OpenCDS membership
			CD lPrimaryOpenCdsMembership = pIceVaccineSpecification.getPrimaryOpenCdsConcept();
			lDebugStrb += "\ngetPrimaryOpenCdsConcept(): " + ConceptUtils.toStringCD(lPrimaryOpenCdsMembership);
		}
		
		pSVs.addSupportedVaccineItemFromIceVaccineSpecificationFile(pIceVaccineSpecification, pSupportedCdsLists);
	}
	
	
	/**
	 * 
	 * @param pIceVaccineGroupSpecification
	 * @param pVaccineGroupFile
	 * @param pSVGs
	 * @param pSupportedCdsLists
	 * @throws ImproperUsageException
	 */	
	private static void addSupportingVaccineGroupConceptsFromIceVaccineGroupSpecificationFile(IceVaccineGroupSpecificationFile pIceVaccineGroupSpecification, 
			File pVaccineGroupFile, SupportedCdsVaccineGroups pSVGs, SupportedCdsLists pSupportedCdsLists) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportingVaccineGroupConceptsFromIceVaccineGroupSpecificationFile(): ";
		if (pVaccineGroupFile == null) {
			String lErrStr = "Vaccine group supporting data file not specified; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);			
		}
		if (pIceVaccineGroupSpecification == null || pSVGs == null) {
			logger.warn(_METHODNAME + "IceVaccineGroupSpecificationFile and/or SupportedCdsVaccineGroupConcepts object not specified; read of supporting data file skipped");
			return;
		}
		CD lVaccineGroupCD = pIceVaccineGroupSpecification.getVaccineGroup();
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD)) {
			String lErrStr = "Required supporting data item vaccineGroup element not provided in IceVaccineGroupSpecificationFile: " + pVaccineGroupFile.getAbsolutePath();
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		String lDebugStrb = "";
		if (logger.isDebugEnabled()) {
			lDebugStrb += _METHODNAME + pIceVaccineGroupSpecification.getClass().getName();
			// VaccineGroup Code
			// CD lVaccineGroupCD = pIceVaccineGroupSpecification.getVaccineGroup();
			lDebugStrb += "\ngetVaccineGroup():";
			if (lVaccineGroupCD != null) {
				lDebugStrb += "\n" + ConceptUtils.toStringCD(lVaccineGroupCD);
			}
			else {
				lDebugStrb += "\nNo vaccine group CD information supplied";
			}

			// CdsVersions
			List<String> lCdsVersions = pIceVaccineGroupSpecification.getCdsVersions();
			lDebugStrb += "\ngetCdsVersions(): ";
			int i=1;
			if (lCdsVersions != null) {
				for (String lCdsVersion : lCdsVersions) {
					lDebugStrb += "\n\t(" + i + "): " + lCdsVersion;
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo Cds Versions information supplied";
			}

			// Priority
			List<CD> lRelatedVaccines = pIceVaccineGroupSpecification.getRelatedVaccines();
			lDebugStrb += "\ngetPriority(): " + pIceVaccineGroupSpecification.getPriority();

			// Related Vaccines
			lDebugStrb += "\ngetRelatedVaccines(): ";
			if (lRelatedVaccines != null) {
				i=1;
				for (CD lRelatedVaccine : lRelatedVaccines) {
					lDebugStrb += "\n" + i + ") " + ConceptUtils.toStringCD(lRelatedVaccine);
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo related vaccines specified";
			}

			// Disease Immunities
			List<CD> lDiseaseImmunities = pIceVaccineGroupSpecification.getDiseaseImmunities();
			lDebugStrb += "\ngetDiseaseImmunities(): ";
			if (lDiseaseImmunities != null) {
				i=1;
				for (CD lDiseaseImmunity : lDiseaseImmunities) {
					lDebugStrb += "\n\t" + i + ") " + ConceptUtils.toStringCD(lDiseaseImmunity);
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo disease immunities specified";
			}

			// OpenCDS memberships
			List<CD> lOpenCdsMemberships = pIceVaccineGroupSpecification.getOpenCdsMemberships();
			lDebugStrb += "\ngetOpenCdsMemberships(): ";
			if (lOpenCdsMemberships != null) {
				i=1;
				for (CD lOpenCdsMembership : lOpenCdsMemberships) {
					lDebugStrb += "\n\t" + i + "): " + ConceptUtils.toStringCD(lOpenCdsMembership);
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo OpenCDS memberships specified";
			}

			// Primary OpenCDS membership
			CD lPrimaryOpenCdsConcept = pIceVaccineGroupSpecification.getPrimaryOpenCdsConcept();
			lDebugStrb += "\ngetPrimaryOpenCdsConcept(): ";
			if (lPrimaryOpenCdsConcept != null) {
				lDebugStrb += "\n" + ConceptUtils.toStringCD(lPrimaryOpenCdsConcept);
			}
			else {
				lDebugStrb += "\tNo Primary OpenCDS concept CD information supplied";
			}

			logger.debug(lDebugStrb);
		}
		
		pSVGs.addSupportedVaccineGroupItemFromIceVaccineGroupSpecificationFile(pIceVaccineGroupSpecification, pSupportedCdsLists);
	}

	
	private static void addSupportingListConceptsFromCdsListSpecificationFile(CdsListSpecificationFile pCdsListSpecification, File pCdsListFile, SupportedCdsLists pSLC) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportingListConceptsForCdsListSpecificationFile(): ";
		
		if (pCdsListFile == null) {
			String lErrStr = "File not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		if (pCdsListSpecification == null || pSLC == null) {
			logger.warn(_METHODNAME + "CdsListSpecification and/or SupportedListConcepts object not specified");
			return;
		}

		String lDebugStrb = null;
		if (logger.isDebugEnabled()) {
			lDebugStrb = _METHODNAME + pCdsListSpecification.getClass().getName();
			lDebugStrb += "\ngetListId(): " + pCdsListSpecification.getListId();
			lDebugStrb += "\ngetCode(): " + pCdsListSpecification.getCode();
			lDebugStrb += "\ngetName(): " + pCdsListSpecification.getName();
			lDebugStrb += "\ngetListType(): " + pCdsListSpecification.getListType();
			lDebugStrb += "\ngetDescription(): " + pCdsListSpecification.getDescription();
			lDebugStrb += "\ngetEnumClass(): " + pCdsListSpecification.getEnumClass();
			lDebugStrb += "\ngetCodeSystem(): " + pCdsListSpecification.getCodeSystem();
			lDebugStrb += "\ngetCodeSystemName(): " + pCdsListSpecification.getCodeSystemName();
			lDebugStrb += "\ngetValueSet(): " + pCdsListSpecification.getValueSet();
			lDebugStrb += "\ngetOpenCdsConceptType(): " + pCdsListSpecification.getOpenCdsConceptType();
		}

		String lCdsListCode = pCdsListSpecification.getCode();
		if (lCdsListCode == null) {
			String lErrStr = "Required supporting data item code element not provided in cdsListSpecificationFile: " + pCdsListFile.getAbsolutePath();
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		List<CdsListItem> lcli = pCdsListSpecification.getCdsListItems();
		if (lcli != null) {
			int i=1;
			for (CdsListItem cli : lcli) {
				if (logger.isDebugEnabled()) {
					int j=1;
					lDebugStrb += "\n\tCdsListItem " + i;
					lDebugStrb += "\n\tgetCdsListItemKey(): " + cli.getCdsListItemKey();
					lDebugStrb += "\n\tgetCdsListItemValue(): " + cli.getCdsListItemValue();
					List<CdsListItemConceptMapping> clicm = cli.getCdsListItemConceptMappings();
					lDebugStrb += "\n\tCdsListItemConceptMapping " + j;
					for (CdsListItemConceptMapping clic : clicm) {
						lDebugStrb += "\n\t\tgetCode(): " + clic.getCode();
						lDebugStrb += "\n\t\tgetCodeSystem(): " + clic.getCodeSystem();
						lDebugStrb += "\n\t\tgetCodeSystemName(): " + clic.getCodeSystemName();
						lDebugStrb += "\n\t\tgetConceptDeterminationMethod(): " + clic.getConceptDeterminationMethod();
						lDebugStrb += "\n\t\tgetDisplayName(): " + clic.getDisplayName();
					}
				}
				i++;
				String lCdsListItemCode = cli.getCdsListItemKey();
				if (lCdsListItemCode == null) {
					String lErrStr = "Required supporting data item cdsListItem.code element not provided in cdsListSpecificationFile: " + pCdsListFile.getAbsolutePath();
					logger.error(_METHODNAME + lErrStr);
					throw new ImproperUsageException(lErrStr);
				}
			}
		
			if (logger.isDebugEnabled()) {
				logger.debug(lDebugStrb);
			}
			pSLC.addAllSupportedListItemsFromCdsListSpecificationFile(pCdsListSpecification);
		}
	}

	
	public static void main(String[] args) {
		
		String[] lSupportingDataLocations = { 
				"/usr/local/projects/ice/opencds-v2-ice/opencds-ice-service-data/src/main/resources/knowledgeModules/org.cdsframework^ICE^1.0.0/ice-supporting-data/",
				"/usr/local/projects/ice/opencds-v2-ice/opencds-ice-service-data/src/main/resources/knowledgeModules/org.nyc.cir^ICE^1.1.0/ice-supporting-data/"
		};
		/*
		if (args.length == 1 && args[0] != null) {
			lSupportingDataLocation = args[0]; 
		}

		System.out.println("Supporting data location set to " + lSupportingDataLocation);
		*/
		
		List<String> lCdsVersions = new ArrayList<String>();		
		lCdsVersions.add("org.nyc.cir^ICE^1.1.0");
		List<File> lSupportingDirectoryFileLoc = new ArrayList<File>();
		for (int i=0; i < lSupportingDataLocations.length; i++) {
			lSupportingDirectoryFileLoc.add(new File(lSupportingDataLocations[i]));
		}
		try {
			ICESupportingDataHelper icdh = new ICESupportingDataHelper(lCdsVersions, lSupportingDirectoryFileLoc);
			icdh.initializeCodeConcepts("OtherLists");
		}
		catch (Exception e) {
			System.out.print(e.toString());
		}
		
		/*
		String testReplace = "IMM_GENDER\tFEMALE";
		System.out.println(testReplace);
		System.out.println(testReplace.replaceAll("[ \t\n\f\r]", "_"));
		*/
	}
	
}

