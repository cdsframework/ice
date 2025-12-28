/**
 * Copyright (C) 2025 New York City Department of Health and Mental Hygiene, Bureau of Immunization
 * Contributions by HLN Consulting, LLC
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. You should have received a copy of the GNU Lesser
 * General Public License along with this program. If not, see <http://www.gnu.org/licenses/> for more
 * details.
 * <p>
 * The above-named contributors (HLN Consulting, LLC) are also licensed by the New York City
 * Department of Health and Mental Hygiene, Bureau of Immunization to have (without restriction,
 * limitation, and warranty) complete irrevocable access and rights to this project.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; THE
 * <p>
 * SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS, IF ANY, OR DEVELOPERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY OF ANY KIND, ARISING FROM, OUT OF, OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.supportingdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.transform.stream.StreamSource;

import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.SupportedCdsConcepts;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.DoseStatus;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.RecommendationStatus;
import org.cdsframework.ice.util.KnowledgeModuleUtils;
import org.cdsframework.util.support.data.cds.list.CdsListItem;
import org.cdsframework.util.support.data.cds.list.CdsListItemConceptMapping;
import org.cdsframework.util.support.data.cds.list.CdsListSpecificationFile;
import org.cdsframework.util.support.data.ice.season.IceSeasonSpecificationFile;
import org.cdsframework.util.support.data.ice.series.IceDoseIntervalSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesDoseSpecification;
import org.cdsframework.util.support.data.ice.series.IceSeriesSpecificationFile;
import org.cdsframework.util.support.data.ice.vaccine.IceVaccineSpecificationFile;
import org.cdsframework.util.support.data.ice.vaccinegroup.IceVaccineGroupSpecificationFile;
import org.opencds.config.api.model.KMId;
import org.opencds.vmr.v1_0.schema.CD;
import org.springframework.util.ObjectUtils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ICESupportingDataConfiguration
{
    private final List<Path> supportingDataDirectoryLocations;
    private final SupportedCdsLists supportedCdsLists;
    private final SupportedVaccineGroups supportedVaccineGroups;
    private final SupportedVaccines supportedVaccines;
    private final SupportedSeasons supportedSeasons;
    private final SupportedSeries supportedSeries;

    /**
     * Initialize all of the supporting data. Note that order matters: first CdsLists (i.e. - code systems and value sets) must be initialized; then vaccine groups;
     * vaccines; seasons; finally, series
     */
    public ICESupportingDataConfiguration(final String pCommonLogicModule, final Path pCommonLogicModuleLocation,
            final List<String> pSupportedKnowledgeModules, final Path pKnowledgeModuleRepositoryLocation)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "ICESupportingDataConfiguration(): ";

        if (ObjectUtils.isEmpty(pCommonLogicModule) || ObjectUtils.isEmpty(pSupportedKnowledgeModules))
        {
            final String lErrStr = "Applicable CDS versions for this set of supporting data not specified; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        if (log.isDebugEnabled())
        {
            log.debug(_METHODNAME + "Knowledge Common Location: {}; Knowledge Module Repository Location: {}",
                    pCommonLogicModuleLocation, pKnowledgeModuleRepositoryLocation);
            int i = 1;
            log.debug(_METHODNAME + "Common Knowledge: {}", pCommonLogicModule);
            for (final String lCdsVersion : pSupportedKnowledgeModules)
                log.debug(_METHODNAME + "CDS version #{}: {}", i++, lCdsVersion);
        }

        this.supportingDataDirectoryLocations = new ArrayList<>();
        final List<String> supportedCdsVersions = new ArrayList<>();
        final StringBuilder lSbSDlocation = new StringBuilder(720);
        final StringBuilder lSbCdsVersion = new StringBuilder(160);
        lSbSDlocation.append("Supporting Data Directories: ");
        lSbCdsVersion.append("CDS versions: ");

        // First the common logic
        final KMId lCommonLogicKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(pCommonLogicModule);
        if (lCommonLogicKMId == null)
        {
            final String lErrStr =
                    "Common Logic Module not specified in proper format: " + pCommonLogicModule + ". Cannot continue";
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final Path lKnowledgeCommonDirectory = pCommonLogicModuleLocation.resolve(
                KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lCommonLogicKMId.getScopingEntityId(),
                        lCommonLogicKMId.getBusinessId(), lCommonLogicKMId.getVersion()));
        //supporting data subdirectories is a fixed directory structure
        final String supportingDataDirectory = "ice-supporting-data";
        final Path lCommonSupportingDataDirectory = lKnowledgeCommonDirectory.resolve(supportingDataDirectory);
        if (!Files.isDirectory(lCommonSupportingDataDirectory))
        {
            final String lErrStr =
                    "Supporting data directory location \"%s\" does not exist".formatted(lCommonSupportingDataDirectory);
            log.error(_METHODNAME + "{}", lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportingDataDirectoryLocations.add(lCommonSupportingDataDirectory);
        lSbCdsVersion.append(pCommonLogicModule);
        lSbSDlocation.append(lCommonSupportingDataDirectory);
        supportedCdsVersions.add(pCommonLogicModule);

        if (log.isDebugEnabled())
            log.debug(_METHODNAME + "Added supporting data directory: {}", lCommonSupportingDataDirectory);

        // Then the knowledge module logic for each specified knowledge module
        for (final String lCdsVersion : pSupportedKnowledgeModules)
        {
            final KMId lKMId = KnowledgeModuleUtils.returnKMIdRepresentationOfKnowledgeModule(lCdsVersion);
            if (lKMId == null)
            {
                final String lErrStr = "Knowledge Module not specified in proper format: " + lCdsVersion + ". Cannot continue";
                log.error(_METHODNAME + "{}", lErrStr);
                throw new IllegalArgumentException(lErrStr);
            }

            final Path lKnowledgeModuleDirectory = pKnowledgeModuleRepositoryLocation.resolve(
                    KnowledgeModuleUtils.returnPackageNameForKnowledgeModule(lKMId.getScopingEntityId(), lKMId.getBusinessId(),
                            lKMId.getVersion()));
            final Path lSupportingDataDirectory = lKnowledgeModuleDirectory.resolve(supportingDataDirectory);
            if (!Files.isDirectory(lSupportingDataDirectory))
            {
                final String lErrStr = "Supporting data directory location \"" + lSupportingDataDirectory + "\" does not exist";
                log.error(_METHODNAME + "{}", lErrStr);
                throw new IllegalArgumentException(lErrStr);
            }

            this.supportingDataDirectoryLocations.add(lSupportingDataDirectory);
            lSbSDlocation.append("; ");
            lSbCdsVersion.append("; ");
            lSbCdsVersion.append(lCdsVersion);
            lSbSDlocation.append(lSupportingDataDirectory);
            supportedCdsVersions.add(lCdsVersion);

            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "Added supporting data directory: {}", lSupportingDataDirectory);
        }

        // Initialize Code Systems/Value Sets supporting data
        this.supportedCdsLists = new SupportedCdsLists(supportedCdsVersions);
        try
        {
            initializeSupportingData("OtherLists", this.supportedCdsLists, CdsListSpecificationFile.class,
                    this::addSupportedCdsListsAndConceptsFromCdsListSpecificationFile);
        }
        catch (final SecurityException e)
        {
            final String lErrStr = "Failed to obtain method to invoke for initializing supporting *CdsLists* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *CdsLists* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following CdsLists have been initialized into the " + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedCdsLists.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Check to make sure that the required base data codes have been supplied
        if (!allBaseSupportingDataCdsListItemInitialized())
        {
            final String lErrStr =
                    "Some base supporting data cdsListItems not supplied; a minimum set of cdsListItem codes must be specified. See ICE documentation";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Initialize the Vaccine Group supporting data
        this.supportedVaccineGroups = new SupportedVaccineGroups(this);
        try
        {
            initializeSupportingData("VaccineGroups", this.supportedVaccineGroups, IceVaccineGroupSpecificationFile.class,
                    this::addSupportedVaccineGroupFromIceVaccineGroupSpecificationFile);
        }
        catch (final SecurityException e)
        {
            final String lErrStr = "Failed to obtain method to invoke for initializing supporting *Vaccine Groups* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Vaccine Groups* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Vaccine Groups have been initialized into the " + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedVaccineGroups.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Initialize the Vaccine supporting data
        this.supportedVaccines = new SupportedVaccines(this);
        try
        {
            initializeSupportingData("Vaccines", this.supportedVaccines, IceVaccineSpecificationFile.class,
                    this::addSupportedVaccineFromIceVaccineSpecificationFile);
        }
        catch (final SecurityException e)
        {
            final String lErrStr = "Failed to obtain method to invoke for initializing supporting *Vaccines* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Vaccines* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Vaccines have been initialized into the " + this.getClass().getName() + ": \n";
            lDebugStr += this.supportedVaccines.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        if (!this.supportedVaccines.isSupportingDataConsistent())
        {
            final String lErrStr =
                    "The vaccine data supplied is inconsistent. Please ensure that all vaccine components for all vaccines have been defined in the supporting data";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        // Initialize Seasons supporting data
        this.supportedSeasons = new SupportedSeasons(this);
        try
        {
            initializeSupportingData("Seasons", this.supportedSeasons, IceSeasonSpecificationFile.class,
                    this::addSupportedSeasonFromIceSeasonSpecificationFile);
        }
        catch (final SecurityException e)
        {
            final String lErrStr = "Failed to obtain method to invoke for initializing supporting *Seasons* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Seasons* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }

        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Seasons have been initialized into the " + this.getClass().getName() + ":\n";
            lDebugStr += this.supportedSeasons.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Initialize Series supporting data
        this.supportedSeries = new SupportedSeries(this);
        try
        {
            initializeSupportingData("Series", this.supportedSeries, IceSeriesSpecificationFile.class,
                    this::addSupportedSeriesFromIceSeriesSpecificationFile);
        }
        catch (final SecurityException e)
        {
            final String lErrStr = "Failed to obtain method to invoke for initializing supporting *Series* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        catch (final Exception e)
        {
            final String lErrStr = "An error occurred processing supporting *Series* data";
            log.error(_METHODNAME + lErrStr, e);
            throw new ICECoreError(lErrStr);
        }
        if (log.isDebugEnabled())
        {
            String lDebugStr = "The following Series have been initialized into the " + this.getClass().getName() + ":\n";
            lDebugStr += this.supportedSeries.toString();
            log.debug(_METHODNAME + "{}", lDebugStr);
        }

        // Log configuration data parameters of data initialized
        lSbCdsVersion.append("; ");
        lSbSDlocation.insert(0, lSbCdsVersion);
        lSbSDlocation.insert(0, _METHODNAME);
        log.debug(lSbSDlocation.toString());
    }

    /**
     * Get the ICE SupportedVaccineGroups data for this supporting data configuration
     */
    public SupportedCdsConcepts getSupportedCdsConcepts()
    {
        return getSupportedCdsLists().getSupportedCdsConcepts();
    }

    private boolean allBaseSupportingDataCdsListItemInitialized()
    {
        // Verify that all DoseStatus enumeration items been provided
        if (!verifyCdsListItemExistsForAllEnumConstants(DoseStatus.class))
            return false;

        // Verify that all EvaluationReason items been provided
        if (!verifyCdsListItemExistsForAllEnumConstants(BaseDataEvaluationReason.class))
            return false;

        // Verify that all RecommendationStatus items have been provided
        if (!verifyCdsListItemExistsForAllEnumConstants(RecommendationStatus.class))
            return false;

        // Verify that all RecommendationReason items have been provided
        return verifyCdsListItemExistsForAllEnumConstants(BaseDataRecommendationReason.class);
    }

    private <E extends Enum<E>> boolean verifyCdsListItemExistsForAllEnumConstants(final Class<E> pEnum)
    {
        final String _METHODNAME = "verifyCdsListItemForAllSpecifiedEnumConstants(): ";

        if (pEnum == null)
        {
            log.warn(_METHODNAME + "enumeration supplied is not of type BaseData");
            return false;
        }

        for (final Enum<E> enumVal : pEnum.getEnumConstants())
        {
            if (!(enumVal instanceof final BaseData baseData))
            {
                log.warn(_METHODNAME + "enumeration supplied not of type BaseData");
                return false;
            }

            final String lCdsListItemName = baseData.getCdsListItemName();
            if (log.isDebugEnabled())
                log.debug(_METHODNAME + "BaseData cdsListItemName{}.{}", pEnum.getSimpleName(), lCdsListItemName);
            if (lCdsListItemName != null && !this.supportedCdsLists.cdsListItemExists(lCdsListItemName))
                return false;
        }

        return true;
    }

    /**
     * Initialize supporting data from specified ICE XML data file
     *
     * @param pSDSubDirectory               Subdirectory where all of the XML files for this supporting data type are held
     * @param pSDObjectToInitialize         The supporting data object to initialize
     * @param pSupportingDataXMLClass       The class of the supporting data XML to be read in
     * @param pSupportingDataMethodToInvoke The initialization method to invoke for the supporting data type
     * @throws ICECoreError if any of the data is invalid or not provided
     */
    private <T> void initializeSupportingData(final String pSDSubDirectory, final SupportingData pSDObjectToInitialize,
            final Class<T> pSupportingDataXMLClass, final Consumer<T> pSupportingDataMethodToInvoke)
            throws InconsistentConfigurationException
    {
        final String _METHODNAME = "initializeSupportingData(): ";

        if (pSDObjectToInitialize == null)
        {
            final String lErrStr = "supporting data object to initialize not specified";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }

        if (ObjectUtils.isEmpty(pSDSubDirectory))
        {
            final String lErrStr = "supporting data subdirectory not specified";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }

        if (pSupportingDataXMLClass == null)
        {
            final String lErrStr = "Supporting data JAXB context path not specified";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }

        if (pSupportingDataXMLClass.getPackage() == null)
        {
            final String lErrStr = "Unable to obtain Package for supplied JAXB context";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }

        if (!pSDObjectToInitialize.isEmpty())
        {
            final String lErrStr = "supporting data object has already been initialized";
            log.error(_METHODNAME + lErrStr);
            throw new ICECoreError(lErrStr);
        }

        for (final Path lSupportingDataDirectory : this.supportingDataDirectoryLocations)
        {
            final Path lSDDirectory = lSupportingDataDirectory.resolve(pSDSubDirectory);
            if (!Files.exists(lSDDirectory))
            {
                // No coded concepts defined for this CDS version - go to next supported CDS version
                if (log.isDebugEnabled())
                    log.debug(_METHODNAME + "No supporting data defined at: {}", lSDDirectory);

                continue;
            }

            try
            {
                final JAXBContext jc =
                        JAXBContext.newInstance(pSupportingDataXMLClass.getPackage().getName(), getClass().getClassLoader());
                final Unmarshaller lUnmarshaller = jc.createUnmarshaller();

                final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_\\- ]+\\.xml?");

                final List<Path> lSDFiles;
                try (final Stream<Path> stream = Files.find(lSDDirectory, Integer.MAX_VALUE,
                        (p, a) -> a.isRegularFile() && pattern.matcher(p.getFileName().toString()).matches()))
                {
                    lSDFiles = stream.toList();
                }
                catch (final IOException e)
                {
                    throw new RuntimeException(e);
                }

                if (ObjectUtils.isEmpty(lSDFiles))
                {
                    final String lErrStr = "An error occurred obtaining list of supporting data files";
                    log.error(_METHODNAME + lErrStr);
                    throw new RuntimeException(lErrStr);
                }

                for (final Path lIceVFile : lSDFiles)
                {
                    if (log.isDebugEnabled())
                        log.debug("Parsing supporting data file: \"{}\"", lIceVFile);
                    final JAXBElement<T> lJAXBDocument =
                            lUnmarshaller.unmarshal(new StreamSource(lIceVFile.toUri().toASCIIString()), pSupportingDataXMLClass);
                    final T lIceDataSpecification = lJAXBDocument.getValue();
                    // Invoke the specified supporting data initialization routine
                    // pSupportingDataMethodToInvoke.invoke(this, lIceDataSpecification, new Object[] { lIceVFile } );
                    pSupportingDataMethodToInvoke.accept(lIceDataSpecification);
                }
            }
            catch (final IllegalArgumentException ia)
            {
                throw new InconsistentConfigurationException("Supporting data did not pass validation checks: " + ia.getMessage());
            }
            catch (final SecurityException se)
            {
                final String lErrStr = "encountered an exception processing supporting data file";
                log.error(_METHODNAME + lErrStr, se);
                throw new RuntimeException(lErrStr);
            }
            catch (final JAXBException jaxbe)
            {
                final String lErrStr = "encountered an exception processing supporting data file; likely an invalid formatted file";
                log.error(_METHODNAME + lErrStr, jaxbe);
                throw new RuntimeException(lErrStr);
            }
        }
    }

    public void addSupportedSeriesFromIceSeriesSpecificationFile(final IceSeriesSpecificationFile pIceSeriesSpecificationFile)
            throws InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedSeriesFromIceSeriesSpecificationFile(): ";

        if (pIceSeriesSpecificationFile == null)
            log.warn(_METHODNAME + "IceSeriesSpecificationFile object not specified; read of supporting data file skipped");

        final StringBuilder lDebugStrb = new StringBuilder();
        if (log.isDebugEnabled())
        {
            lDebugStrb.append(_METHODNAME).append(Objects.requireNonNull(pIceSeriesSpecificationFile).getClass().getName());
            // ID
            lDebugStrb.append("\ngetSeriesId(): ").append(pIceSeriesSpecificationFile.getSeriesId());
            // Code
            lDebugStrb.append("\ngetCode(): ").append(pIceSeriesSpecificationFile.getCode());
            // Name
            lDebugStrb.append("\ngetName(): ").append(pIceSeriesSpecificationFile.getName());
            // Number of Doses in Series
            lDebugStrb.append("\ngetNumberOfDosesInSeries(): ").append(pIceSeriesSpecificationFile.getNumberOfDosesInSeries());
            // Vaccine groups
            final List<CD> lVaccineGroups = pIceSeriesSpecificationFile.getVaccineGroups();
            lDebugStrb.append("\ngetVaccineGroups(): ");
            int i = 1;
            if (lVaccineGroups != null)
            {
                for (final CD lVaccineGroup : lVaccineGroups)
                    lDebugStrb.append("\n\t(").append(i++).append("): ").append(ConceptUtils.toStringCD(lVaccineGroup));
            }
            else
                lDebugStrb.append("\n\tNo Vaccine Group information supplied");
            // Seasons
            final List<String> lSeasonCodes = pIceSeriesSpecificationFile.getSeasonCodes();
            lDebugStrb.append("\ngetSeasonCodes(): ");
            i = 1;
            if (lSeasonCodes != null)
            {
                for (final String lSeasonCode : lSeasonCodes)
                    lDebugStrb.append("\n\t(").append(i++).append("): ").append(lSeasonCode);
            }
            else
                lDebugStrb.append("\n\tNo Seasons information supplied");
            // CdsVersions
            final List<String> lCdsVersions = pIceSeriesSpecificationFile.getCdsVersions();
            lDebugStrb.append("\ngetCdsVersions(): ");
            i = 1;
            if (lCdsVersions != null)
            {
                for (final String lCdsVersion : lCdsVersions)
                    lDebugStrb.append("\n\t(").append(i++).append("): ").append(lCdsVersion);
            }
            else
                lDebugStrb.append("\n\tNo CdsVersion information supplied");

            // Series Dose Specifications
            final List<IceSeriesDoseSpecification> lIceSeriesDoses = pIceSeriesSpecificationFile.getIceSeriesDoses();
            lDebugStrb.append("\ngetIceSeriesDoses(): ");
            if (lIceSeriesDoses != null)
            {
                i = 1;
                for (final IceSeriesDoseSpecification isds : lIceSeriesDoses)
                    lDebugStrb.append("\n\t(")
                            .append(i++)
                            .append("): absolute minimum age: ")
                            .append(isds.getAbsoluteMinimumAge())
                            .append("; earliest recommended age: ")
                            .append(isds.getEarliestRecommendedAge())
                            .append("; latest recommended age: ")
                            .append(isds.getLatestRecommendedAge())
                            .append("; minimum age: ")
                            .append(isds.getMinimumAge())
                            .append("; maximum age: ")
                            .append(isds.getAbsoluteMaximumAge());
            }
            else
                lDebugStrb.append("\t\nNo IceSeriesDoses specified");

            // Series Dose Intervals
            final List<IceDoseIntervalSpecification> lIceDoseIntervals = pIceSeriesSpecificationFile.getDoseIntervals();
            lDebugStrb.append("\ngetDoseIntervals(): ");
            if (lIceDoseIntervals != null)
            {
                i = 1;
                for (final IceDoseIntervalSpecification idis : lIceDoseIntervals)
                    lDebugStrb.append("\n\t(")
                            .append(i++)
                            .append("): from dose number: ")
                            .append(idis.getFromDoseNumber())
                            .append("; to dose number: ")
                            .append(idis.getToDoseNumber())
                            .append("; absolute minimum interval: ")
                            .append(idis.getAbsoluteMinimumInterval())
                            .append("; minimum interval: ")
                            .append(idis.getMinimumInterval())
                            .append("; earliest recommended interval: ")
                            .append(idis.getEarliestRecommendedInterval())
                            .append("; latest recommended interval: ")
                            .append(idis.getLatestRecommendedInterval());
            }
            else
                lDebugStrb.append("\t\nNo Dose Interval information provided");

            log.debug(_METHODNAME + "{}", lDebugStrb);
        }

        this.supportedSeries.addSupportedSeriesItemFromIceSeriesSpecificationFile(pIceSeriesSpecificationFile);
    }

    public void addSupportedSeasonFromIceSeasonSpecificationFile(final IceSeasonSpecificationFile pIceSeasonSpecificationFile)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportingSeasonFromIceSeasonSpecificationFile(): ";

        if (pIceSeasonSpecificationFile == null)
        {
            log.warn(_METHODNAME + "IceSeasonSpecificationFile object not specified; read of supporting data file skipped");
            return;
        }

        final String lSeasonCode = pIceSeasonSpecificationFile.getCode();
        if (lSeasonCode == null)
        {
            final String lErrStr = "Required supporting data item code element not provided in IceSeasonSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }

        final CD lVaccineGroupCD = pIceSeasonSpecificationFile.getVaccineGroup();
        if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD))
        {
            final String lErrStr = "Required supporting data item vaccineGroup element not provided in IceSeasonSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final StringBuilder lDebugStrb = new StringBuilder();
        if (log.isDebugEnabled())
        {
            lDebugStrb.append(_METHODNAME).append(pIceSeasonSpecificationFile.getClass().getName());
            // ID
            lDebugStrb.append("\ngetSeasonId(): ").append(pIceSeasonSpecificationFile.getSeasonId());
            // Vaccine Group
            lDebugStrb.append("\ngetVaccineGroup():");
            lDebugStrb.append("\n").append(ConceptUtils.toStringCD(lVaccineGroupCD));
            // Code
            lDebugStrb.append("\ngetCode(): ").append(pIceSeasonSpecificationFile.getCode());
            // Name
            lDebugStrb.append("\ngetName(): ").append(pIceSeasonSpecificationFile.getName());
            // Start Date
            lDebugStrb.append("\ngetStartDate(): ").append(pIceSeasonSpecificationFile.getStartDate());
            // End Date
            lDebugStrb.append("\ngetEndDate(): ").append(pIceSeasonSpecificationFile.getEndDate());
            // Is Default Season?
            lDebugStrb.append("\nisDefaultSeason(): ").append(pIceSeasonSpecificationFile.isDefaultSeason());
            // Default Start Month and Day
            lDebugStrb.append("\ngetDefaultStartMonthAndDay(): ").append(pIceSeasonSpecificationFile.getDefaultStartMonthAndDay());
            // Default Stop Month and Day
            lDebugStrb.append("\ngetDefaultStopMonthAndDay(): ").append(pIceSeasonSpecificationFile.getDefaultStopMonthAndDay());
            // CdsVersions
            final List<String> lCdsVersions = pIceSeasonSpecificationFile.getCdsVersions();
            lDebugStrb.append("\ngetCdsVersions(): ");
            int i = 1;
            if (lCdsVersions != null)
            {
                for (final String lCdsVersion : lCdsVersions)
                    lDebugStrb.append("\n\t(").append(i++).append("): ").append(lCdsVersion);
            }
            else
                lDebugStrb.append("\n\tNo CdsVersion information supplied");

            log.debug(_METHODNAME + "{}", lDebugStrb);
        }

        this.supportedSeasons.addSupportedSeasonItemFromIceSeasonSpecificationFile(pIceSeasonSpecificationFile);
    }

    public void addSupportedVaccineFromIceVaccineSpecificationFile(final IceVaccineSpecificationFile pIceVaccineSpecification)
            throws IllegalArgumentException
    {
        final String _METHODNAME = "addSupportingVaccineConceptsFromIceVaccineSpecificationFile(): ";

        if (pIceVaccineSpecification == null)
        {
            log.warn(_METHODNAME + "IceVaccineSpecificationFile object not specified; read of supporting data file skipped");
            return;
        }

        final CD lVaccineCD = pIceVaccineSpecification.getVaccine();
        if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineCD))
        {
            final String lErrStr = "Required supporting data item vaccine element not provided in IceVaccineSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final StringBuilder lDebugStrb = new StringBuilder();
        if (log.isDebugEnabled())
        {
            lDebugStrb.append(_METHODNAME).append(pIceVaccineSpecification.getClass().getName());

            // Vaccine
            lDebugStrb.append("\ngetVaccine(): ").append(ConceptUtils.toStringCD(lVaccineCD));

            // CdsVersions
            final List<String> lCdsVersions = pIceVaccineSpecification.getCdsVersions();
            lDebugStrb.append("\ngetCdsVersions(): ");
            int i = 1;
            if (lCdsVersions != null)
            {
                for (final String lCdsVersion : lCdsVersions)
                    lDebugStrb.append("\n\t(").append(i++).append("): ").append(lCdsVersion);
            }
            else
                lDebugStrb.append("\n\tNo CdsVersion information supplied");

            // Tradename
            lDebugStrb.append("\nVaccine Trade Name: ").append(pIceVaccineSpecification.getTradeName());

            // Manufacturer
            lDebugStrb.append("\nManufacturer: ").append(ConceptUtils.toStringCD(pIceVaccineSpecification.getManufacturerCode()));

            // Live virus vaccine
            lDebugStrb.append("\nLive Virus: ").append(pIceVaccineSpecification.isLiveVirusVaccine().toString());
            lDebugStrb.append("\nUnspecified Formulation: ").append(pIceVaccineSpecification.isUnspecifiedFormulation());

            // age restrictions
            lDebugStrb.append("\nValid Minimum Age: ").append(pIceVaccineSpecification.getValidMinimumAgeForUse());
            lDebugStrb.append("\nValid Maximum Age: ").append(pIceVaccineSpecification.getValidMaximumAgeForUse());
            lDebugStrb.append("\nLicensed Minimum Age: ").append(pIceVaccineSpecification.getRecommendedMinimumAgeForUse());
            lDebugStrb.append("\nLicensed Maximum Age: ").append(pIceVaccineSpecification.getRecommendedMaximumAgeForUse());

            // Targeted diseases
            lDebugStrb.append("\ngetDiseaseImmunities(): ");
            final List<CD> lRelatedDiseases = pIceVaccineSpecification.getDiseaseImmunities();
            i = 1;
            if (lRelatedDiseases != null)
            {
                for (final CD lRelatedDisease : lRelatedDiseases)
                    lDebugStrb.append("\n\t(").append(i).append("): ").append(ConceptUtils.toStringCD(lRelatedDisease));
            }
            else
                lDebugStrb.append("\n\tNo target disease information supplied");

            // Vaccine components
            lDebugStrb.append("\ngetVaccineComponents(): ");
            final List<CD> lVaccineComponents = pIceVaccineSpecification.getVaccineComponents();
            if (lVaccineComponents != null)
            {
                for (final CD lVaccineComponent : lVaccineComponents)
                    lDebugStrb.append("\n\t(").append(i).append("): ").append(ConceptUtils.toStringCD(lVaccineComponent));
            }
            else
                lDebugStrb.append("\n\tNo vaccine component information supplied");

            // Conflicting vaccines
            lDebugStrb.append("\ngetConflictingVaccines(): ");
            final List<CD> lConflictingVaccines = pIceVaccineSpecification.getConflictingVaccines();
            if (lConflictingVaccines != null)
            {
                for (final CD lConflictingVaccine : lConflictingVaccines)
                    lDebugStrb.append("\n\t(").append(i).append("): ").append(ConceptUtils.toStringCD(lConflictingVaccine));
            }
            else
                lDebugStrb.append("\n\tNo conflict vaccines supplied");

            // OpenCDS memberships
            lDebugStrb.append("\ngetOpenCdsMemberships(): ");
            final List<CD> lOpenCdsMemberships = pIceVaccineSpecification.getOpenCdsMemberships();
            if (lOpenCdsMemberships != null)
            {
                for (final CD lOpenCdsMembership : lOpenCdsMemberships)
                    lDebugStrb.append("\n\t(").append(i).append("): ").append(ConceptUtils.toStringCD(lOpenCdsMembership));
            }
            else
                lDebugStrb.append("\n\tNo OpenCDS memberships supplied");

            // Primary OpenCDS membership
            final CD lPrimaryOpenCdsMembership = pIceVaccineSpecification.getPrimaryOpenCdsConcept();
            lDebugStrb.append("\ngetPrimaryOpenCdsConcept(): ").append(ConceptUtils.toStringCD(lPrimaryOpenCdsMembership));

            // Log it
            log.debug(lDebugStrb.toString());
        }

        this.supportedVaccines.addSupportedVaccineItemFromIceVaccineSpecificationFile(pIceVaccineSpecification);
    }

    /**
     *
     */
    public void addSupportedVaccineGroupFromIceVaccineGroupSpecificationFile(
            final IceVaccineGroupSpecificationFile pIceVaccineGroupSpecification) throws IllegalArgumentException
    {
        final String _METHODNAME = "addSupportingVaccineGroupConceptsFromIceVaccineGroupSpecificationFile(): ";

        if (pIceVaccineGroupSpecification == null)
        {
            log.warn(_METHODNAME + "IceVaccineGroupSpecificationFile; read of supporting data file skipped");
            return;
        }

        final CD lVaccineGroupCD = pIceVaccineGroupSpecification.getVaccineGroup();
        if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD))
        {
            final String lErrStr =
                    "Required supporting data item vaccineGroup element not provided in IceVaccineGroupSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        final StringBuilder lDebugStrb = new StringBuilder();
        if (log.isDebugEnabled())
        {
            lDebugStrb.append(_METHODNAME).append(pIceVaccineGroupSpecification.getClass().getName());
            // VaccineGroup Code
            // CD lVaccineGroupCD = pIceVaccineGroupSpecification.getVaccineGroup();
            lDebugStrb.append("\ngetVaccineGroup():");
            lDebugStrb.append("\n").append(ConceptUtils.toStringCD(lVaccineGroupCD));

            // CdsVersions
            final List<String> lCdsVersions = pIceVaccineGroupSpecification.getCdsVersions();
            lDebugStrb.append("\ngetCdsVersions(): ");
            int i = 1;
            if (lCdsVersions != null)
            {
                for (final String lCdsVersion : lCdsVersions)
                    lDebugStrb.append("\n\t(").append(i++).append("): ").append(lCdsVersion);
            }
            else
                lDebugStrb.append("\n\tNo Cds Versions information supplied");

            // Priority
            lDebugStrb.append("\ngetPriority(): ").append(pIceVaccineGroupSpecification.getPriority());

            // Disease Immunities
            final List<CD> lDiseaseImmunities = pIceVaccineGroupSpecification.getDiseaseImmunities();
            lDebugStrb.append("\ngetDiseaseImmunities(): ");
            if (lDiseaseImmunities != null)
            {
                i = 1;
                for (final CD lDiseaseImmunity : lDiseaseImmunities)
                    lDebugStrb.append("\n\t").append(i++).append(") ").append(ConceptUtils.toStringCD(lDiseaseImmunity));
            }
            else
                lDebugStrb.append("\n\tNo disease immunities specified");

            // OpenCDS memberships
            final List<CD> lOpenCdsMemberships = pIceVaccineGroupSpecification.getOpenCdsMemberships();
            lDebugStrb.append("\ngetOpenCdsMemberships(): ");
            if (lOpenCdsMemberships != null)
            {
                i = 1;
                for (final CD lOpenCdsMembership : lOpenCdsMemberships)
                    lDebugStrb.append("\n\t").append(i++).append("): ").append(ConceptUtils.toStringCD(lOpenCdsMembership));
            }
            else
                lDebugStrb.append("\n\tNo OpenCDS memberships specified");

            // Primary OpenCDS membership
            final CD lPrimaryOpenCdsConcept = pIceVaccineGroupSpecification.getPrimaryOpenCdsConcept();
            lDebugStrb.append("\ngetPrimaryOpenCdsConcept(): ");
            if (lPrimaryOpenCdsConcept != null)
                lDebugStrb.append("\n").append(ConceptUtils.toStringCD(lPrimaryOpenCdsConcept));
            else
                lDebugStrb.append("\tNo Primary OpenCDS concept CD information supplied");

            log.debug(lDebugStrb.toString());
        }

        this.supportedVaccineGroups.addVaccineGroupItemFromIceVaccineGroupSpecificationFile(pIceVaccineGroupSpecification);
    }

    public void addSupportedCdsListsAndConceptsFromCdsListSpecificationFile(final CdsListSpecificationFile pCdsListSpecification)
    {
        final String _METHODNAME = "addSupportingListConceptsForCdsListSpecificationFile(): ";

        if (pCdsListSpecification == null)
        {
            log.warn(_METHODNAME + "CdsListSpecification object not specified");
            return;
        }

        StringBuilder lDebugStrb = null;
        if (log.isDebugEnabled())
        {
            lDebugStrb = new StringBuilder(_METHODNAME + pCdsListSpecification.getClass().getName());
            lDebugStrb.append("\ngetListId(): ").append(pCdsListSpecification.getListId());
            lDebugStrb.append("\ngetCode(): ").append(pCdsListSpecification.getCode());
            lDebugStrb.append("\ngetName(): ").append(pCdsListSpecification.getName());
            lDebugStrb.append("\ngetListType(): ").append(pCdsListSpecification.getListType());
            lDebugStrb.append("\ngetDescription(): ").append(pCdsListSpecification.getDescription());
            lDebugStrb.append("\ngetEnumClass(): ").append(pCdsListSpecification.getEnumClass());
            lDebugStrb.append("\ngetCodeSystem(): ").append(pCdsListSpecification.getCodeSystem());
            lDebugStrb.append("\ngetCodeSystemName(): ").append(pCdsListSpecification.getCodeSystemName());
            lDebugStrb.append("\ngetValueSet(): ").append(pCdsListSpecification.getValueSet());
            lDebugStrb.append("\ngetOpenCdsConceptType(): ").append(pCdsListSpecification.getOpenCdsConceptType());
        }

        final String lCdsListCode = pCdsListSpecification.getCode();
        if (lCdsListCode == null)
        {
            final String lErrStr = "Required supporting data item code element not provided in cdsListSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            throw new InconsistentConfigurationException(lErrStr);
        }
        final List<CdsListItem> lcli = pCdsListSpecification.getCdsListItems();
        if (lcli != null)
        {
            int i = 1;
            for (final CdsListItem cli : lcli)
            {
                if (log.isDebugEnabled())
                {
                    final int j = 1;
                    Objects.requireNonNull(lDebugStrb).append("\n\tCdsListItem ").append(i);
                    lDebugStrb.append("\n\tgetCdsListItemKey(): ").append(cli.getCdsListItemKey());
                    lDebugStrb.append("\n\tgetCdsListItemValue(): ").append(cli.getCdsListItemValue());
                    final List<CdsListItemConceptMapping> clicm = cli.getCdsListItemConceptMappings();
                    lDebugStrb.append("\n\tCdsListItemConceptMapping " + j);
                    for (final CdsListItemConceptMapping clic : clicm)
                    {
                        lDebugStrb.append("\n\t\tgetCode(): ").append(clic.getCode());
                        lDebugStrb.append("\n\t\tgetCodeSystem(): ").append(clic.getCodeSystem());
                        lDebugStrb.append("\n\t\tgetCodeSystemName(): ").append(clic.getCodeSystemName());
                        lDebugStrb.append("\n\t\tgetConceptDeterminationMethod(): ").append(clic.getConceptDeterminationMethod());
                        lDebugStrb.append("\n\t\tgetDisplayName(): ").append(clic.getDisplayName());
                    }
                }
                i++;
                final String lCdsListItemCode = cli.getCdsListItemKey();
                if (lCdsListItemCode == null)
                {
                    final String lErrStr =
                            "Required supporting data item cdsListItem.code element not provided in cdsListSpecificationFile";
                    log.error(_METHODNAME + lErrStr);
                    throw new InconsistentConfigurationException(lErrStr);
                }
            }

            if (log.isDebugEnabled())
                log.debug(Objects.requireNonNull(lDebugStrb).toString());

            this.supportedCdsLists.addSupportedCdsListItemsAndConceptsFromCdsListSpecificationFile(pCdsListSpecification);
        }
    }
}
