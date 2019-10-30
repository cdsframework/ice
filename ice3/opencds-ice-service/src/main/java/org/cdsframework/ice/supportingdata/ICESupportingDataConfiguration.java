/**
 * Copyright (C) 2019 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.DoseStatus;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.RecommendationStatus;
import org.cdsframework.ice.util.XMLSupportingDataFilenameFilterImpl;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListItemConceptMapping;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.cdsframework.util.support.data.ice.season.IceSeasonSpecificationFile;
import org.cdsframework.util.support.data.ice.series.IceDoseIntervalSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesDoseSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesSpecificationFile;
import org.cdsframework.util.support.data.ice.vaccine.IceVaccineSpecificationFile;
import org.cdsframework.util.support.data.ice.vaccinegroup.IceVaccineGroupSpecificationFile;
import org.opencds.common.exceptions.ImproperUsageException;
import org.opencds.vmr.v1_0.schema.CD;


public class ICESupportingDataConfiguration {

	/**
	 * e.g.
	 * 	_RECOMMENDED_IN_FUTURE_REASON("ICE225", "Immunization Recommended in Future (Due In Future)", "2.16.840.1.113883.3.795.12.100.6", "ICE3 Immunization Recommendation Reason", 
	 *		"DUE_IN_FUTURE", "Immunization Recommended in Future (Due In Future)");
	 */
	private List<String> supportedCdsVersions;
	private List<File> supportingDataDirectoryLocations;
	
	private SupportedCdsLists supportedCdsLists;
	private SupportedVaccineGroups supportedVaccineGroups;
	private SupportedVaccines supportedVaccines;
	private SupportedSeasons supportedSeasons;
	private SupportedSeries supportedSeries;
	
	//supporting data subdirectories is a fixed directory structure
	private static String supportingDataDirectory = "ice-supporting-data";
	private static String supportingDataConceptsAndCodesSubdirectory = "OtherLists";	
	private static String supportingDataVaccineGroupsSubdirectory = "VaccineGroups";
	private static String supportingDataVaccinesSubdirectory = "Vaccines";
	private static String supportingDataSeasonsSubdirectory = "Seasons";
	private static String supportingDataSeriesSubdirectory = "Series";
	
	private static Log logger = LogFactory.getLog(ICESupportingDataConfiguration.class);
	
	
	/**
	 * Initialize all of the supporting data. Note that order matters: first CdsLists (i.e. - code systems and value sets) must be initialized; then vaccine groups;
	 * vaccines; seasons; finally, series 
	 * @param pSupportedKnowledgeModules
	 * @param pKnowledgeModuleRepositoryLocation
	 * @throws ImproperUsageException
	 */
	public ICESupportingDataConfiguration(String pCommonLogicModule, File pCommonLogicModuleLocation, List<String> pSupportedKnowledgeModules, File pKnowledgeModuleRepositoryLocation) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "ICESupportingDataConfiguration(): ";
		
		if (pCommonLogicModule == null || pCommonLogicModule.isEmpty() || pSupportedKnowledgeModules == null || pSupportedKnowledgeModules.isEmpty()) {
			String lErrStr = "Applicable CDS versions for this set of supporting data not specified; cannot continue";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		else if (logger.isDebugEnabled()) {
			logger.debug(_METHODNAME + "Knowledge Common Location: " + pCommonLogicModuleLocation.getAbsolutePath() + "; Knowledge Module Repository Location: " + pKnowledgeModuleRepositoryLocation.getAbsolutePath());
			int i=1;
			logger.debug(_METHODNAME + "Common Knowledge: " + pCommonLogicModule);
			for (String lCdsVersion : pSupportedKnowledgeModules) {
				logger.debug(_METHODNAME + "CDS version #" + i + ": " + lCdsVersion);
				i++;
			}
		}

		this.supportingDataDirectoryLocations = new ArrayList<File>();
		this.supportedCdsVersions = new ArrayList<String>();
		
		StringBuffer lSbSDlocation = new StringBuffer(720);
		StringBuffer lSbCdsVersion = new StringBuffer(160);
		lSbSDlocation.append("Supporting Data Directories: ");
		lSbCdsVersion.append("CDS versions: ");

		// First the common logic
		File lKnowledgeCommonDirectory = new File(pCommonLogicModuleLocation, pCommonLogicModule); 
		File lCommonSupportingDataDirectory = new File(lKnowledgeCommonDirectory, supportingDataDirectory);
		if (lCommonSupportingDataDirectory.isDirectory() == false) {
			String lErrStr = "Supporting data directory location \"" + lCommonSupportingDataDirectory + "\" does not exist";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		else {
			this.supportingDataDirectoryLocations.add(lCommonSupportingDataDirectory);
			lSbCdsVersion.append(pCommonLogicModule); 
			lSbSDlocation.append(lCommonSupportingDataDirectory.getAbsolutePath());
		}
		this.supportedCdsVersions.add(pCommonLogicModule);
		if (logger.isDebugEnabled()) {
			logger.info(_METHODNAME + "Added supporting data directory: " + lCommonSupportingDataDirectory.getAbsolutePath());
		}
		
		// Then the knowledge module logic
		for (String lCdsVersion : pSupportedKnowledgeModules) {
			File lKnowledgeModuleDirectory = new File(pKnowledgeModuleRepositoryLocation, lCdsVersion); 
			File lSupportingDataDirectory = new File(lKnowledgeModuleDirectory, supportingDataDirectory);
			if (lSupportingDataDirectory.isDirectory() == false) {
				String lErrStr = "Supporting data directory location \"" + lSupportingDataDirectory + "\" does not exist";
				logger.error(_METHODNAME + lErrStr);
				throw new ImproperUsageException(lErrStr);
			}
			else {
				this.supportingDataDirectoryLocations.add(lSupportingDataDirectory);
				lSbSDlocation.append("; ");
				lSbCdsVersion.append("; ");
				lSbCdsVersion.append(lCdsVersion); 
				lSbSDlocation.append(lSupportingDataDirectory.getAbsolutePath());
			}
			this.supportedCdsVersions.add(lCdsVersion);
			if (logger.isDebugEnabled()) {
				logger.info(_METHODNAME + "Added supporting data directory: " + lSupportingDataDirectory.getAbsolutePath());
			}
		}
		
		///////
		// Initialize Code Systems/Value Sets supporting data
		///////
		this.supportedCdsLists = new SupportedCdsLists(supportedCdsVersions);
		try {
			Method sdMethodToInvoke = this.getClass().getMethod("addSupportedCdsListsAndConceptsFromCdsListSpecificationFile", new Class[] { CdsListSpecificationFile.class } );
			initializeSupportingData(supportingDataConceptsAndCodesSubdirectory, this.supportedCdsLists, CdsListSpecificationFile.class, sdMethodToInvoke);
		} 
		catch (NoSuchMethodException | SecurityException e) {
			String lErrStr = "Failed to obtain method to invoke for initializing supporting *CdsLists* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);
		}
		catch (Exception e) {
			String lErrStr = "An error occurred processing supporting *CdsLists* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);			
		}		
		if (logger.isDebugEnabled()) {
			String lDebugStr = "The following CdsLists have been initialized into the " + this.getClass().getName() + ": \n";
			lDebugStr += this.supportedCdsLists.toString();
			logger.debug(_METHODNAME + lDebugStr);
		}

		// Check to make sure that the required base data codes have been supplied
		if (! allBaseSupportingDataCdsListItemInitialized()) {
			String lErrStr = "Some base supporting data cdsListItems not supplied; a minimum set of cdsListItem codes must be specified. See ICE documentation";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}
		///////
		// Initialize the Vaccine Group supporting data
		///////
		this.supportedVaccineGroups = new SupportedVaccineGroups(this);
		try {
			Method sdMethodToInvoke = this.getClass().getMethod("addSupportedVaccineGroupFromIceVaccineGroupSpecificationFile", new Class[] { IceVaccineGroupSpecificationFile.class } );
			initializeSupportingData(supportingDataVaccineGroupsSubdirectory, this.supportedVaccineGroups, IceVaccineGroupSpecificationFile.class, sdMethodToInvoke);
		} 
		catch (NoSuchMethodException | SecurityException e) {
			String lErrStr = "Failed to obtain method to invoke for initializing supporting *Vaccine Groups* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);
		}
		catch (Exception e) {
			String lErrStr = "An error occurred processing supporting *Vaccine Groups* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);			
		}		
		if (logger.isDebugEnabled()) {
			String lDebugStr = "The following Vaccine Groups have been initialized into the " + this.getClass().getName() + ": \n";
			lDebugStr += this.supportedVaccineGroups.toString();
			logger.debug(_METHODNAME + lDebugStr);
		}

		///////
		// Initialize the Vaccine supporting data
		///////
		this.supportedVaccines = new SupportedVaccines(this);
		try {
			Method sdMethodToInvoke = this.getClass().getMethod("addSupportedVaccineFromIceVaccineSpecificationFile", new Class[] { IceVaccineSpecificationFile.class } );
			initializeSupportingData(supportingDataVaccinesSubdirectory, this.supportedVaccines, IceVaccineSpecificationFile.class, sdMethodToInvoke);
		} 
		catch (NoSuchMethodException | SecurityException e) {
			String lErrStr = "Failed to obtain method to invoke for initializing supporting *Vaccines* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);
		}
		catch (Exception e) {
			String lErrStr = "An error occurred processing supporting *Vaccines* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);			
		}		
		if (logger.isDebugEnabled()) {
			String lDebugStr = "The following Vaccines have been initialized into the " + this.getClass().getName() + ": \n";
			lDebugStr += this.supportedVaccines.toString();
			logger.debug(_METHODNAME + lDebugStr);
		}
		if (! this.supportedVaccines.isSupportingDataConsistent()) {
			String lErrStr = "The vaccine data supplied is inconsistent. Please ensure that all vaccine components for all vaccines have been defined in the supporting data";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}		
		
		///////
		// Initialize Seasons supporting data
		///////
		this.supportedSeasons = new SupportedSeasons(this);
		try {
			Method sdMethodToInvoke = this.getClass().getMethod("addSupportedSeasonFromIceSeasonSpecificationFile", new Class[] { IceSeasonSpecificationFile.class } );
			initializeSupportingData(supportingDataSeasonsSubdirectory, this.supportedSeasons, IceSeasonSpecificationFile.class, sdMethodToInvoke);
		} 
		catch (NoSuchMethodException | SecurityException e) {
			String lErrStr = "Failed to obtain method to invoke for initializing supporting *Seasons* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);
		}
		catch (Exception e) {
			String lErrStr = "An error occurred processing supporting *Seasons* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);			
		}		
		if (logger.isDebugEnabled()) {
			String lDebugStr = "The following Seasons have been initialized into the " + this.getClass().getName() + ":\n";
			lDebugStr += this.supportedSeasons.toString();
			logger.debug(_METHODNAME + lDebugStr);
		}
		
		///////
		// Initialize Series supporting data
		///////
		this.supportedSeries = new SupportedSeries(this);
		try {
			Method sdMethodToInvoke = this.getClass().getMethod("addSupportedSeriesFromIceSeriesSpecificationFile", new Class[] { IceSeriesSpecificationFile.class } );
			initializeSupportingData(supportingDataSeriesSubdirectory, this.supportedSeries, IceSeriesSpecificationFile.class, sdMethodToInvoke);
		} 
		catch (NoSuchMethodException | SecurityException e) {
			String lErrStr = "Failed to obtain method to invoke for initializing supporting *Series* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);
		}
		catch (Exception e) {
			String lErrStr = "An error occurred processing supporting *Series* data";
			logger.error(_METHODNAME + lErrStr, e);
			throw new ICECoreError(lErrStr);			
		}
		if (logger.isDebugEnabled()) {
			String lDebugStr = "The following Series have been initialized into the " + this.getClass().getName() + ":\n";
			lDebugStr += this.supportedSeries.toString();
			logger.debug(_METHODNAME + lDebugStr);
		}
		
		// Log configuration data parameters of data initialized
		lSbCdsVersion.append("; ");
		lSbSDlocation.insert(0, lSbCdsVersion);
		lSbSDlocation.insert(0, _METHODNAME);		
		logger.info(lSbSDlocation.toString());	
	}


	/**
	 * Get the ICE SupportedCdsLists data for this supporting data configuration 
	 */
	public SupportedCdsLists getSupportedCdsLists() {
		
		return this.supportedCdsLists;
	}
	
	
	/**
	 * Get the ICE SupportedVaccineGroups data for this supporting data configuration
	 */
	public SupportedVaccineGroups getSupportedVaccineGroups() {
		
		return this.supportedVaccineGroups;
	}

	
	/**
	 * Get the ICE SupportedVaccineGroups data for this supporting data configuration
	 */
	public SupportedVaccines getSupportedVaccines() {
		
		return this.supportedVaccines;
	}
	
	
	/**
	 * Get the ICE SupportedSeasons data for this supporting data configuration
	 */
	public SupportedSeasons getSupportedSeasons() {
		
		return this.supportedSeasons;
	}
	
	/**
	 * Get the ICE SupportedVaccineGroups data for this supporting data configuration
	 */
	public SupportedCdsConcepts getSupportedCdsConcepts() {
		
		return getSupportedCdsLists().getSupportedCdsConcepts();
	}
	
	
	/**
	 * Get the ICE SupportedSeries data for this supporting data configuration
	 */
	public SupportedSeries getSupportedSeries() {
		
		return this.supportedSeries;
	}
	
	
	private boolean allBaseSupportingDataCdsListItemInitialized() {
		
		// Verify that all DoseStatus enumeration items been provided
		if (! verifyCdsListItemExistsForAllEnumConstants(DoseStatus.class)) {
			return false;
		}
		// Verify that all EvaluationReason items been provided
		if (! verifyCdsListItemExistsForAllEnumConstants(BaseDataEvaluationReason.class)) {
			return false;
		}
		// Verify that all RecommendationStatus items have been provided
		if (! verifyCdsListItemExistsForAllEnumConstants(RecommendationStatus.class)) {
			return false;
		}
		// Verify that all RecommendationReason items have been provided
		if (! verifyCdsListItemExistsForAllEnumConstants(BaseDataRecommendationReason.class)) {
			return false;
		}
		
		return true;
	}
	
	
	private <E extends Enum<E>> boolean verifyCdsListItemExistsForAllEnumConstants(Class<E> pEnum) {
		
		String _METHODNAME = "verifyCdsListItemForAllSpecifiedEnumConstants(): ";
		
		if (pEnum == null) {
			logger.warn(_METHODNAME + "enumeration supplied is not of type BaseData");
			return false;
		}
		
		for (Enum<E> enumVal : pEnum.getEnumConstants()) {
			if (enumVal instanceof BaseData) {
				String lCdsListItemName = ((BaseData) enumVal).getCdsListItemName();
				if (logger.isDebugEnabled()) {
					logger.debug(_METHODNAME + "BaseData cdsListItemName" + pEnum.getSimpleName() + "." + lCdsListItemName);
				}
				if (lCdsListItemName != null && ! this.supportedCdsLists.cdsListItemExists(lCdsListItemName)) {
					return false;
				}
			}
			else {
				logger.warn(_METHODNAME + "enumeration supplied not of type BaseData");
				return false;
			}
		}

		return true;
	}
	
	
	/**
	 * Initialize supporting data from specified ICE XML data file
	 * @param pSDSubDirectory Subdirectory where all of the XML files for this supporting data type are held
	 * @param pSDObjectToInitialize The supporting data object to initialize
	 * @param pSupportingDataXMLClass The class of the supporting data XML to be read in
	 * @param pSupportingDataMethodToInvoke The initialization method to invoke for the supporting data type
	 * @throws ICECoreError if any of the data is invalid or not provided
	 */
	private <T> void initializeSupportingData(String pSDSubDirectory, SupportingData pSDObjectToInitialize, Class<T> pSupportingDataXMLClass, Method pSupportingDataMethodToInvoke) 
		throws ImproperUsageException, InconsistentConfigurationException {
			
		String _METHODNAME = "initializeSupportingData(): ";

		if (pSDObjectToInitialize == null) {
			String lErrStr = "supporting data object to initialize not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		if (pSDSubDirectory == null || pSDSubDirectory.isEmpty()) {
			String lErrStr = "supporting data subdirectory not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		if (pSupportingDataXMLClass == null) {
			String lErrStr = "Supporting data JAXB context path not specified";
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		if (pSupportingDataXMLClass.getPackage() == null) {
			String lErrStr = "Unable to obtain Package for supplied JAXB context";
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}		
		if (pSDObjectToInitialize.isEmpty() == false) {
			String lErrStr = "supporting data object has already been initialized";
			logger.error(_METHODNAME + lErrStr);
			throw new ICECoreError(lErrStr);
		}
		for (File lSupportingDataDirectory : this.supportingDataDirectoryLocations) {
			File lSDDirectory = new File(lSupportingDataDirectory, pSDSubDirectory);
			if (! lSDDirectory.exists()) {
				// No coded concepts defined for this CDS version - go to next supported CDS version
				if (logger.isDebugEnabled()) {
					String lInfo = "No supporting data defined at: " + lSDDirectory.getAbsolutePath();
					logger.debug(_METHODNAME + lInfo);
				}
				continue;
			}
			try {
				JAXBContext jc = JAXBContext.newInstance(pSupportingDataXMLClass.getPackage().getName());
				Unmarshaller lUnmarshaller = jc.createUnmarshaller();
				FilenameFilter lFF = new XMLSupportingDataFilenameFilterImpl();
				String[] lSDFiles = lSDDirectory.list(lFF);
				if (lSDFiles == null) {
					String lErrStr = "An error occurred obtaining list of supporting data files";
					logger.error(_METHODNAME + lErrStr);
					throw new RuntimeException(lErrStr);
				}

				for (String lSDFile : lSDFiles) {
					if (logger.isDebugEnabled()) {
						logger.debug("Parsing supporting data file: \"" + lSDFile + "\"");
					}
					File lIceVFile = new File(lSDDirectory, lSDFile);
					JAXBElement<T> lJAXBDocument = (JAXBElement<T>) lUnmarshaller.unmarshal(new StreamSource(lIceVFile), pSupportingDataXMLClass);
					T lIceDataSpecification = lJAXBDocument.getValue();
					// Invoke the specified supporting data initialization routine
					// pSupportingDataMethodToInvoke.invoke(this, lIceDataSpecification, new Object[] { lIceVFile } );
					pSupportingDataMethodToInvoke.invoke(this, lIceDataSpecification);
				}
			}
			catch (IllegalArgumentException ia) {
				String lErrStr = "Supporting data did not pass validation checks: " + ia.getMessage();
				throw new InconsistentConfigurationException(lErrStr);
			}
			catch (InconsistentConfigurationException ice) {
				throw ice;
			}
			catch (SecurityException se) {
				String lErrStr = "encountered an exception processing supporting data file";
				logger.error(_METHODNAME + lErrStr, se);
				throw new RuntimeException(lErrStr);				
			}			
			catch (JAXBException jaxbe) {
				String lErrStr = "encountered an exception processing supporting data file; likely an invalid formatted file";
				logger.error(_METHODNAME + lErrStr, jaxbe);
				throw new RuntimeException(lErrStr);
			}
			catch (IllegalAccessException iae) {
				String lErrStr = "encountered an IllegalAccessException invoking the specified supporting data initialization routine";
				logger.error(_METHODNAME + lErrStr, iae);
				throw new ICECoreError(lErrStr);
			}
			catch(InvocationTargetException ite) {
				String lErrStr = "encountered an InvocationTargetException invoking the specified supporting data initialization routine";
				logger.error(_METHODNAME + lErrStr, ite);
				throw new ICECoreError(lErrStr);
			}
		}
		
		
	}
	
	
	public void addSupportedSeriesFromIceSeriesSpecificationFile(IceSeriesSpecificationFile pIceSeriesSpecificationFile) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportedSeriesFromIceSeriesSpecificationFile(): ";

		if (pIceSeriesSpecificationFile == null) {
			logger.warn(_METHODNAME + "IceSeriesSpecificationFile object not specified; read of supporting data file skipped");
		}
		
		String lDebugStrb = "";
		if (logger.isDebugEnabled()) {
			lDebugStrb += _METHODNAME + pIceSeriesSpecificationFile.getClass().getName();
			// ID
			lDebugStrb += "\ngetSeriesId(): " + pIceSeriesSpecificationFile.getSeriesId();
			// Code
			lDebugStrb += "\ngetCode(): " + pIceSeriesSpecificationFile.getCode();
			// Name
			lDebugStrb += "\ngetName(): " + pIceSeriesSpecificationFile.getName();
			// Number of Doses in Series
			lDebugStrb += "\ngetNumberOfDosesInSeries(): " + pIceSeriesSpecificationFile.getNumberOfDosesInSeries();
			// Vaccine groups
			List<CD> lVaccineGroups = pIceSeriesSpecificationFile.getVaccineGroups();
			lDebugStrb += "\ngetVaccineGroups(): ";
			int i=1;
			if (lVaccineGroups != null) {
				i=1;
				for (CD lVaccineGroup : lVaccineGroups) {
					lDebugStrb += "\n\t(" + i + "): " + ConceptUtils.toStringCD(lVaccineGroup);
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo Vaccine Group information supplied";
			}
			// Seasons
			List<String> lSeasonCodes = pIceSeriesSpecificationFile.getSeasonCodes();
			lDebugStrb += "\ngetSeasonCodes(): ";
			i=1;
			if (lSeasonCodes != null) {
				for (String lSeasonCode : lSeasonCodes) {
					lDebugStrb += "\n\t(" + i + "): " + lSeasonCode;
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo Seasons information supplied";
			}			
			// CdsVersions
			List<String> lCdsVersions = pIceSeriesSpecificationFile.getCdsVersions();
			lDebugStrb += "\ngetCdsVersions(): ";
			i=1;
			if (lCdsVersions != null) {
				for (String lCdsVersion : lCdsVersions) {
					lDebugStrb += "\n\t(" + i + "): " + lCdsVersion;
					i++;
				}
			}
			else {
				lDebugStrb += "\n\tNo CdsVersion information supplied";
			}
			///////
			// Series Dose Specifications
			///////
			List<IceSeriesDoseSpecification> lIceSeriesDoses = pIceSeriesSpecificationFile.getIceSeriesDoses();
			lDebugStrb += "\ngetIceSeriesDoses(): ";
			if (lIceSeriesDoses != null) {
				i=1;
				for (IceSeriesDoseSpecification isds : lIceSeriesDoses) {
					lDebugStrb += "\n\t(" + i + "): absolute minimum age: " + isds.getAbsoluteMinimumAge() + "; earliest recommended age: " + isds.getEarliestRecommendedAge() + 
						"; latest recommended age: " + isds.getLatestRecommendedAge() + "; minimum age: " + isds.getMinimumAge() + "; maximum age: " + isds.getMaximumAge();
					i++;
				}
			}
			else {
				lDebugStrb += "\t\nNo IceSeriesDoses specified";
			}
			///////
			// Series Dose Intervals
			///////
			List<IceDoseIntervalSpecification> lIceDoseIntervals = pIceSeriesSpecificationFile.getDoseIntervals();
			lDebugStrb += "\ngetDoseIntervals(): ";
			if (lIceDoseIntervals != null) {
				i=1;
				for (IceDoseIntervalSpecification idis : lIceDoseIntervals) {
					lDebugStrb += "\n\t("+i+"): from dose number: " + idis.getFromDoseNumber() + "; to dose number: " + idis.getToDoseNumber() + 
						"; absolute minimum interval: " + idis.getAbsoluteMinimumInterval() + "; minimum interval: " + idis.getMinimumInterval() + 
						"; earliest recommended interval: " + idis.getEarliestRecommendedInterval() + "; latest recommended interval: " + idis.getLatestRecommendedInterval();
					i++;
				}
			}
			else {
				lDebugStrb += "\t\nNo Dose Interval information provided";
			}
			
			logger.debug(_METHODNAME + lDebugStrb);
		}
		
		this.supportedSeries.addSupportedSeriesItemFromIceSeriesSpecificationFile(pIceSeriesSpecificationFile);
	}
	
	
	public void addSupportedSeasonFromIceSeasonSpecificationFile(IceSeasonSpecificationFile pIceSeasonSpecificationFile) 
		throws ImproperUsageException, InconsistentConfigurationException {
		
		String _METHODNAME = "addSupportingSeasonFromIceSeasonSpecificationFile(): ";

		if (pIceSeasonSpecificationFile == null) {
			logger.warn(_METHODNAME + "IceSeasonSpecificationFile object not specified; read of supporting data file skipped");
			return;
		}
		
		String lSeasonCode = pIceSeasonSpecificationFile.getCode();
		if (lSeasonCode == null) {
			String lErrStr = "Required supporting data item code element not provided in IceSeasonSpecificationFile";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
		}		
		CD lVaccineGroupCD = pIceSeasonSpecificationFile.getVaccineGroup();
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD)) {
			String lErrStr = "Required supporting data item vaccineGroup element not provided in IceSeasonSpecificationFile";
			logger.error(_METHODNAME + lErrStr);
			throw new ImproperUsageException(lErrStr);
		}
		
		String lDebugStrb = "";
		if (logger.isDebugEnabled()) {
			lDebugStrb += _METHODNAME + pIceSeasonSpecificationFile.getClass().getName();
			// ID
			lDebugStrb += "\ngetSeasonId(): " + pIceSeasonSpecificationFile.getSeasonId(); 
			// Vaccine Group
			lDebugStrb += "\ngetVaccineGroup():";
			if (lVaccineGroupCD != null) {
				lDebugStrb += "\n" + ConceptUtils.toStringCD(lVaccineGroupCD);
			}
			else {
				lDebugStrb += "\nNo vaccine group CD information supplied";
			}
			// Code
			lDebugStrb += "\ngetCode(): " + pIceSeasonSpecificationFile.getCode();
			// Name
			lDebugStrb += "\ngetName(): " + pIceSeasonSpecificationFile.getName();
			// Start Date
			lDebugStrb += "\ngetStartDate(): " + pIceSeasonSpecificationFile.getStartDate();
			// End Date
			lDebugStrb += "\ngetEndDate(): " + pIceSeasonSpecificationFile.getEndDate();
			// Is Default Season?
			lDebugStrb += "\nisDefaultSeason(): " + pIceSeasonSpecificationFile.isDefaultSeason();
			// Default Start Month and Day
			lDebugStrb += "\ngetDefaultStartMonthAndDay(): " + pIceSeasonSpecificationFile.getDefaultStartMonthAndDay();
			// Default Stop Month and Day
			lDebugStrb += "\ngetDefaultStopMonthAndDay(): " + pIceSeasonSpecificationFile.getDefaultStopMonthAndDay();		
			// CdsVersions
			List<String> lCdsVersions = pIceSeasonSpecificationFile.getCdsVersions();
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
			
			logger.debug(_METHODNAME + lDebugStrb);
		}
		
		this.supportedSeasons.addSupportedSeasonItemFromIceSeasonSpecificationFile(pIceSeasonSpecificationFile);
	}
	
	
	public void addSupportedVaccineFromIceVaccineSpecificationFile(IceVaccineSpecificationFile pIceVaccineSpecification) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportingVaccineConceptsFromIceVaccineSpecificationFile(): ";
		
		if (pIceVaccineSpecification == null) {
			logger.warn(_METHODNAME + "IceVaccineSpecificationFile object not specified; read of supporting data file skipped");
			return;
		}
		
		CD lVaccineCD = pIceVaccineSpecification.getVaccine();
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineCD)) {
			String lErrStr = "Required supporting data item vaccine element not provided in IceVaccineSpecificationFile";
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
			lDebugStrb += "\nUnspecified Formulation: " + pIceVaccineSpecification.isUnspecifiedFormulation();
			
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
			
			// Log it
			logger.debug(lDebugStrb);
		}
		
		this.supportedVaccines.addSupportedVaccineItemFromIceVaccineSpecificationFile(pIceVaccineSpecification);
	}
	
	
	/**
	 * 
	 * @param pIceVaccineGroupSpecification
	 * @param pVaccineGroupFile
	 * @param pSVGs
	 * @param pSupportedCdsLists
	 * @throws ImproperUsageException
	 */	
	public void addSupportedVaccineGroupFromIceVaccineGroupSpecificationFile(IceVaccineGroupSpecificationFile pIceVaccineGroupSpecification)
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportingVaccineGroupConceptsFromIceVaccineGroupSpecificationFile(): ";
		
		if (pIceVaccineGroupSpecification == null) {
			logger.warn(_METHODNAME + "IceVaccineGroupSpecificationFile; read of supporting data file skipped");
			return;
		}
		CD lVaccineGroupCD = pIceVaccineGroupSpecification.getVaccineGroup();
		if (! ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD)) {
			String lErrStr = "Required supporting data item vaccineGroup element not provided in IceVaccineGroupSpecificationFile";
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
			lDebugStrb += "\ngetPriority(): " + pIceVaccineGroupSpecification.getPriority();

			
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
		
		this.supportedVaccineGroups.addVaccineGroupItemFromIceVaccineGroupSpecificationFile(pIceVaccineGroupSpecification);
	}

	
	public void addSupportedCdsListsAndConceptsFromCdsListSpecificationFile(CdsListSpecificationFile pCdsListSpecification) 
		throws ImproperUsageException {
		
		String _METHODNAME = "addSupportingListConceptsForCdsListSpecificationFile(): ";
		
		if (pCdsListSpecification == null) {
			logger.warn(_METHODNAME + "CdsListSpecification object not specified");
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
			String lErrStr = "Required supporting data item code element not provided in cdsListSpecificationFile";
			logger.error(_METHODNAME + lErrStr);
			throw new InconsistentConfigurationException(lErrStr);
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
					String lErrStr = "Required supporting data item cdsListItem.code element not provided in cdsListSpecificationFile";
					logger.error(_METHODNAME + lErrStr);
					throw new InconsistentConfigurationException(lErrStr);
				}
			}
		
			if (logger.isDebugEnabled()) {
				logger.debug(lDebugStrb);
			}
			
			this.supportedCdsLists.addSupportedCdsListItemsAndConceptsFromCdsListSpecificationFile(pCdsListSpecification);
		}
	}

	/*
	public static void main(String[] args) {
			
		List<String> lCdsVersions = new ArrayList<String>();
		lCdsVersions.add("org.cdsframework^ICE^1.0.0");
		lCdsVersions.add("org.nyc.cir^ICE^1.0.0");
		try {
			ICESupportingDataConfiguration icdh = new ICESupportingDataConfiguration(lCdsVersions, new File("/usr/local/projects/ice/ice3/opencds-ice-service-data/src/main/resources/knowledgeModules"));
		}
		catch (Exception e) {
			System.out.print("An unexpected error occurred :" + e.toString());
		}

		System.out.println("\n\nmain(): END.\n\n");
	}
	*/
	
}

