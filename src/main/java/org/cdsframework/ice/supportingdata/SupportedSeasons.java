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

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cdsframework.cds.ConceptUtils;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.ICELogicHelper;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.cdsframework.ice.util.CollectionUtils;
import org.cdsframework.util.support.data.ice.season.IceSeasonSpecificationFile;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SupportedSeasons implements SupportingData
{
    private final Map<String, LocallyCodedSeasonItem> cdsListItemNameToSeasonItem;
    // cdsListItemName (cdsListCode.cdsListItemKey) to LocallyCodedSeasonItem
    private final Map<LocallyCodedVaccineGroupItem, List<Season>> vaccineGroupItemToSeasons;
    // Internal tracking structure: List of Seasons supported for each vaccine group
    private final SupportedVaccineGroups supportedVaccineGroups;
    // Supporting vaccine groups from which this season data is built
    private boolean isSupportingDataConsistent;

    protected SupportedSeasons(final ICESupportingDataConfiguration isdc) throws IllegalArgumentException
    {
        final String _METHODNAME = "SupportedCdsSeasons(): ";
        if (isdc == null)
        {
            final String lErrStr = "ICESupportingDataConfiguration argument is null; a valid argument must be provided.";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.supportedVaccineGroups = isdc.getSupportedVaccineGroups();
        if (this.supportedVaccineGroups == null)
        {
            final String lErrStr = "Supporting vaccine group data not set in ICESupportingDataConfiguration; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }
        this.cdsListItemNameToSeasonItem = new HashMap<>();
        this.vaccineGroupItemToSeasons = new HashMap<>();
        this.isSupportingDataConsistent = true;
    }

    public boolean isEmpty()
    {
        return this.cdsListItemNameToSeasonItem.isEmpty();
    }

    public boolean seasonItemExists(final String pSeasonItemName)
    {
        if (pSeasonItemName == null)
            return false;

        return this.cdsListItemNameToSeasonItem.get(pSeasonItemName) != null;
    }

    public LocallyCodedSeasonItem getSeasonItem(final String pSeasonItemName)
    {
        if (pSeasonItemName == null)
            return null;

        return this.cdsListItemNameToSeasonItem.get(pSeasonItemName);
    }

    protected SupportedVaccineGroups getAssociatedSupportedCdsVaccineGroups()
    {
        return this.supportedVaccineGroups;
    }

    protected void addSupportedSeasonItemFromIceSeasonSpecificationFile(
            final IceSeasonSpecificationFile pIceSeasonSpecificationFile)
            throws IllegalArgumentException, InconsistentConfigurationException
    {
        final String _METHODNAME = "addSupportedSeasonItemFromIceSeasonSpecificationFile(): ";

        if (pIceSeasonSpecificationFile == null || this.supportedVaccineGroups == null)
            return;

        String lSeasonCode = pIceSeasonSpecificationFile.getCode();
        if (ObjectUtils.isEmpty(lSeasonCode))
        {
            final String lErrStr = "Required supporting data seasonCode element not provided in IceSeasonSpecificationFile";
            log.error(_METHODNAME + lErrStr);
            this.isSupportingDataConsistent = false;
            throw new IllegalArgumentException(lErrStr);
        }

        lSeasonCode = ConceptUtils.modifyAttributeNameToConformToRequiredNamingConvention(lSeasonCode);
        // If adding a code that is not one of the supported cdsVersions, then return
        final Collection<String> lIntersectionOfSupportedCdsVersions =
                CollectionUtils.intersectionOfStringCollections(pIceSeasonSpecificationFile.getCdsVersions(),
                        this.supportedVaccineGroups.getAssociatedSupportedCdsLists().getCdsVersions());
        if (ObjectUtils.isEmpty(lIntersectionOfSupportedCdsVersions))
        {
            log.warn(_METHODNAME
                            + "Skipping attempt to add a Season \"{}\" which does not have a cdsVersion that is supported by SupportedCdsLists",
                    lSeasonCode);
            return;
        }

        ///////
        // Check to make sure that this season code has not already been defined
        ///////
        if (this.cdsListItemNameToSeasonItem.containsKey(lSeasonCode))
        {
            final String lErrStr = "Attempt to add a Season that was already specified previously: " + lSeasonCode;
            log.warn(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        ///////
        // Check to make sure that the vaccine group specified is a valid vaccine group; one that has been previously specified
        final CD lVaccineGroupCD = ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup());
        if (!ConceptUtils.requiredAttributesForCDSpecified(lVaccineGroupCD))
        {
            final String lErrStr =
                    "Required supporting data item vaccineGroup element not provided in IceVaccineGroupSpecificationFile:";
            log.error(_METHODNAME + lErrStr);
            this.isSupportingDataConsistent = false;
            throw new IllegalArgumentException(lErrStr);
        }

        // Check to make sure that the specified vaccine group is a supported vaccine group
        final LocallyCodedCdsListItem lccli =
                this.supportedVaccineGroups.getAssociatedSupportedCdsLists().getCdsListItem(lVaccineGroupCD);
        if (lccli == null)
        {
            final String lErrStr =
                    "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsLists: %s".formatted(
                            pIceSeasonSpecificationFile.getVaccineGroup() == null
                            ? "null"
                            : ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup()));
            log.warn(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }
        final LocallyCodedVaccineGroupItem lcvgi = this.supportedVaccineGroups.getVaccineGroupItem(lccli.getCdsListItemName());
        if (lcvgi == null)
        {
            final String lErrStr =
                    "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsVaccineGroups: "
                            + (pIceSeasonSpecificationFile.getVaccineGroup() == null
                               ? "null"
                               : ConceptUtils.toInternalCD(pIceSeasonSpecificationFile.getVaccineGroup()));
            log.warn(_METHODNAME + "{}", lErrStr);
            this.isSupportingDataConsistent = false;
            throw new InconsistentConfigurationException(lErrStr);
        }

        ////////////// Create new season and add it to the list of seasons being tracked for each vaccine group START //////////////
        List<Season> lSeasonsListForVG = this.vaccineGroupItemToSeasons.get(lcvgi);
        if (lSeasonsListForVG == null)
            lSeasonsListForVG = new ArrayList<>();
        if (!pIceSeasonSpecificationFile.isDefaultSeason())
        {
            ///////
            // Add fully-specified season
            ///////
            if (pIceSeasonSpecificationFile.getStartDate() == null || pIceSeasonSpecificationFile.getEndDate() == null)
            {
                final String lErrStr =
                        "Fully-specified season start and/or fully-specified season end date not specified for non-default season; start date: "
                                + pIceSeasonSpecificationFile.getStartDate() + "; end date: "
                                + pIceSeasonSpecificationFile.getEndDate();
                log.warn(_METHODNAME + "{}", lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            final LocalDate lJodaFullySpecifiedSeasonStartDate =
                    ICELogicHelper.toLocalDate(pIceSeasonSpecificationFile.getStartDate());
            final LocalDate lJodaFullySpecifiedSeasonEndDate = ICELogicHelper.toLocalDate(pIceSeasonSpecificationFile.getEndDate());
            final Season lS =
                    new Season(lSeasonCode, lcvgi.getCdsItemName(), true, lJodaFullySpecifiedSeasonStartDate.getMonthValue(),
                            lJodaFullySpecifiedSeasonStartDate.getDayOfMonth(), lJodaFullySpecifiedSeasonStartDate.getYear(),
                            lJodaFullySpecifiedSeasonEndDate.getMonthValue(), lJodaFullySpecifiedSeasonEndDate.getDayOfMonth(),
                            lJodaFullySpecifiedSeasonEndDate.getYear());
            // If the off-season is set in the XML, set it here too
            if (pIceSeasonSpecificationFile.getOffSeasonEndDate() != null)
                lS.setOffSeasonEndDateForFullySpecifiedSeason(
                        ICELogicHelper.toLocalDate(pIceSeasonSpecificationFile.getOffSeasonEndDate()));
            lSeasonsListForVG.add(lS);
            this.cdsListItemNameToSeasonItem.put(lSeasonCode,
                    new LocallyCodedSeasonItem(lSeasonCode, pIceSeasonSpecificationFile.getCdsVersions(), lS));
            this.vaccineGroupItemToSeasons.put(lcvgi, lSeasonsListForVG);
        }
        else
        {
            ///////
            // Add default season
            ///////
            final String lDefaultSeasonStartDate = pIceSeasonSpecificationFile.getDefaultStartMonthAndDay();
            final String lDefaultSeasonEndDate = pIceSeasonSpecificationFile.getDefaultStopMonthAndDay();
            if (lDefaultSeasonStartDate == null || lDefaultSeasonEndDate == null)
            {
                final String lErrStr =
                        "Default season start and/or default season end date not specified for default season; start date: "
                                + lDefaultSeasonStartDate + "; end date: " + lDefaultSeasonEndDate;
                log.warn(_METHODNAME + "{}", lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            // Check validity of specified default start and stop dates
            final MonthDay lStartMonthDay;
            final MonthDay lEndMonthDay;
            try
            {
                lStartMonthDay = getMonthDayObjectForSDMonthDayStr(lDefaultSeasonStartDate);
                lEndMonthDay = getMonthDayObjectForSDMonthDayStr(lDefaultSeasonEndDate);
            }
            catch (final IllegalArgumentException e)
            {
                final String lErrStr =
                        "Default season start and/or default season end date invalid format; start date: " + lDefaultSeasonStartDate
                                + "; end date: " + lDefaultSeasonEndDate;
                log.warn(_METHODNAME + "{}", lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }
            final Season lS = new Season(lSeasonCode, lcvgi.getCdsItemName(), true, lStartMonthDay.getMonthValue(),
                    lStartMonthDay.getDayOfMonth(), lEndMonthDay.getMonthValue(), lEndMonthDay.getDayOfMonth());
            lSeasonsListForVG.add(lS);
            this.cdsListItemNameToSeasonItem.put(lSeasonCode,
                    new LocallyCodedSeasonItem(lSeasonCode, pIceSeasonSpecificationFile.getCdsVersions(), lS));
            this.vaccineGroupItemToSeasons.put(lcvgi, lSeasonsListForVG);
        }

    }

    /**
     * Return a *copy* of the list of Season associated with the specified vaccine group. If the vaccine group is not supported, null is returned.
     * If the vaccine group is supported but not Season have been specified for the vaccine group, an empty list is returned.
     */
    public List<Season> getCopyOfSeasonForVaccineGroup(final LocallyCodedVaccineGroupItem plcvg)
    {
        return getSeasonsForVaccineGroup(plcvg, true);
    }

    /**
     * Return a reference to the list of Season associated with the specified vaccine group. If the vaccine group is not supported, null is returned.
     * If the vaccine group is supported but not Season have been specified for the vaccine group, an empty list is returned.
     */
    protected List<Season> getSeasonsForVaccineGroup(final LocallyCodedVaccineGroupItem plcvg)
    {
        return getSeasonsForVaccineGroup(plcvg, false);
    }

    private List<Season> getSeasonsForVaccineGroup(final LocallyCodedVaccineGroupItem plcvg, final boolean copyOf)
    {
        final List<Season> lSs = this.vaccineGroupItemToSeasons.get(plcvg);
        if (lSs == null)
            return null;

        return lSs.stream().map(lS -> copyOf ? Season.constructDeepCopyOfSeasonObject(lS) : lS).toList();
    }

    /**
     * Return a copy of all Season supported by this installation. If none, an empty list is returned.
     */
    public List<Season> getCopyOfAllSeasons()
    {
        return getAllSeasons(true);
    }

    /**
     * Return a copy of all Season supported by this installation. If none, an empty list is returned.
     */
    protected List<Season> getAllSeasons()
    {
        return getAllSeasons(false);
    }

    private List<Season> getAllSeasons(final boolean copyOf)
    {
        return vaccineGroupItemToSeasons.values()
                .stream()
                .flatMap(Collection::stream)
                .map(lSR -> copyOf ? Season.constructDeepCopyOfSeasonObject(lSR) : lSR)
                .toList();
    }

    /**
     * Get MonthDay object for month and day string represented by MM-DD
     */
    private MonthDay getMonthDayObjectForSDMonthDayStr(final String pMonthAndDay) throws IllegalArgumentException
    {
        final String _METHODNAME = "getMonthDayObjectForSDMonthDayStr(): ";

        if (ObjectUtils.isEmpty(pMonthAndDay))
            throw new IllegalArgumentException(_METHODNAME + "MonthDay argument not specified");

        final String regex = "(\\d+)(-)(\\d+)";
        final Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        final Matcher m = p.matcher(pMonthAndDay);
        if (!m.find())
            throw new IllegalArgumentException(
                    _METHODNAME + "MonthDay argument is in invalid format. Format is: (\\d)?(\\d)(-)(\\d)?(\\d)");

        try
        {
            return MonthDay.of(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(3)));
        }
        catch (final IllegalStateException ise)
        {
            final String lErrStr = "An IllegalStateException was encountered for month and day: " + pMonthAndDay;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(lErrStr);
        }
        catch (final IndexOutOfBoundsException iobe)
        {
            final String lErrStr = "An IllegalStateException was encountered for month and day: " + pMonthAndDay;
            log.error(_METHODNAME + "{}", lErrStr);
            throw new ICECoreError(_METHODNAME + lErrStr);
        }
    }

    @Override
    public String toString()
    {
        // First, print out all of the season name->season value map entries
        final Set<String> cdsListItemNames = this.cdsListItemNameToSeasonItem.keySet();
        int i = 1;
        final StringBuilder ltoStringStr = new StringBuilder();
        for (final String s : cdsListItemNames)
            ltoStringStr.append("{")
                    .append(i++)
                    .append("} ")
                    .append(s)
                    .append(" = [ ")
                    .append(this.cdsListItemNameToSeasonItem.get(s).toString())
                    .append(" ]\n");

        // Second, print out which seasons are associated with which vaccine groups
        final Set<LocallyCodedVaccineGroupItem> lcvc = this.vaccineGroupItemToSeasons.keySet();
        i = 1;
        ltoStringStr.append("Vaccine Group -> Seasons list:");
        for (final LocallyCodedVaccineGroupItem lcvg : lcvc)
        {
            ltoStringStr.append("\n\t(").append(i++).append(") Vaccine Group ").append(lcvg.getCdsItemName()).append(" ==> ");
            for (final Season s : vaccineGroupItemToSeasons.get(lcvg))
                ltoStringStr.append(s.getSeasonName()).append("; ");
        }

        return ltoStringStr.toString();
    }
}
