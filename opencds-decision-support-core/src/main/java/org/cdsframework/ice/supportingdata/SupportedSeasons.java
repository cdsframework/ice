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

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportedCdsLists;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.InconsistentConfigurationException;
import org.cdsframework.ice.service.Season;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SupportedSeasons implements SupportingData
{
    private final SupportedCdsLists supportedCdsLists;
    private final Map<String, LocallyCodedSeasonItem> cdsListItemNameToSeasonItem;
    // cdsListItemName (cdsListCode.cdsListItemKey) to LocallyCodedSeasonItem
    private final Map<LocallyCodedVaccineGroupItem, List<Season>> vaccineGroupItemToSeasons;
    // Internal tracking structure: List of Seasons supported for each vaccine group
    private final Map<String, LocallyCodedSeriesItem> cdsListItemNameToSeriesItem;

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

        this.supportedCdsLists = isdc.getSupportedCdsLists();
        if (this.supportedCdsLists == null)
        {
            final String lErrStr = "Supporting CdsList data not set in ICESupportingDataConfiguration; cannot continue";
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

        this.cdsListItemNameToSeriesItem = new HashMap<>();
        this.cdsListItemNameToSeasonItem = new HashMap<>();
        this.vaccineGroupItemToSeasons = new HashMap<>();
        this.isSupportingDataConsistent = true;
    }

    @Override
    public boolean isEmpty()
    {
        return this.cdsListItemNameToSeasonItem.isEmpty();
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

    public void initializeFromCdsLists() throws InconsistentConfigurationException
    {
        final String _METHODNAME = "initializeFromCdsLists(): ";

        final Map<CdsConcept, LocallyCodedCdsListItem> lIceConceptEntry =
                this.supportedCdsLists.getSupportedCdsConcepts().getCdsConceptsAssociatedWithICEConceptType(ICEConceptType.SEASON);
        if (lIceConceptEntry == null)
        {
            log.info(_METHODNAME + "No seasons found in supportedCdsLists");
            return;
        }

        for (final Map.Entry<CdsConcept, LocallyCodedCdsListItem> entry : lIceConceptEntry.entrySet())
        {
            final CdsConcept lPrimaryOpenCdsConcept = entry.getKey();
            final LocallyCodedCdsListItem locallyCodedCdsSeasonListItem = entry.getValue();
            final String lSeasonCode = locallyCodedCdsSeasonListItem.getCdsListItemName();

            // Extract properties from the CdsListItem
            boolean lDefaultSeason = false;
            String lStartDateStr = null;
            String lEndDateStr = null;
            String lDefaultStartMonthAndDayStr = null;
            String lDefaultStopMonthAndDayStr = null;
            String lVgCode = null;
            String lVgSystem = null;
            String lVgDisplayName = null;

            for (final org.hl7.fhir.CodeSystemConceptProperty cp : locallyCodedCdsSeasonListItem.getProperties())
            {
                final String lPropCode = cp.getCode().getValue();
                switch (lPropCode)
                {
                    case "defaultSeason" -> lDefaultSeason = cp.getValueBoolean().isValue();
                    case "startDate" -> lStartDateStr = cp.getValueString().getValue();
                    case "endDate" -> lEndDateStr = cp.getValueString().getValue();
                    case "defaultStartMonthAndDay" -> lDefaultStartMonthAndDayStr = cp.getValueString().getValue();
                    case "defaultStopMonthAndDay" -> lDefaultStopMonthAndDayStr = cp.getValueString().getValue();
                    case "vaccineGroup" ->
                    {
                        final org.hl7.fhir.Coding vgCoding = cp.getValueCoding();
                        lVgCode = vgCoding.getCode().getValue();
                        lVgSystem = vgCoding.getSystem().getValue();
                        lVgDisplayName = vgCoding.getDisplay().getValue();
                    }
                    // already processed
                    case "conceptMapping", "supported", "outboundCode" ->
                    {
                    }
                    default ->
                        log.warn(_METHODNAME + "Unsupported property found for season: {} - {}", lSeasonCode, lPropCode);
                }
            }

            if (lVgCode == null)
            {
                final String lErrStr = "Required property vaccineGroup not found for season: " + lSeasonCode;
                log.warn(_METHODNAME + lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            // Check to make sure that the specified vaccine group is a supported vaccine group
            final CD lInternalVaccineGroupCD = new CD();
            lInternalVaccineGroupCD.setCode(lVgCode);
            lInternalVaccineGroupCD.setCodeSystem(lVgSystem);
            lInternalVaccineGroupCD.setDisplayName(lVgDisplayName);

            final LocallyCodedCdsListItem locallyCodedCdsVgListItem =
                    this.supportedVaccineGroups.getAssociatedSupportedCdsLists().getCdsListItem(lInternalVaccineGroupCD);
            if (locallyCodedCdsVgListItem == null)
            {
                final String lErrStr =
                        "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsLists: %s".formatted(
                                lInternalVaccineGroupCD);
                log.warn(_METHODNAME + "{}", lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }
            final LocallyCodedVaccineGroupItem lcvgi =
                    this.supportedVaccineGroups.getVaccineGroupItem(locallyCodedCdsVgListItem.getCdsListItemName());
            if (lcvgi == null)
            {
                final String lErrStr =
                        "Attempt to add a season which specifies a vaccine group that is not in the list of SupportedCdsVaccineGroups: "
                                + lInternalVaccineGroupCD;
                log.warn(_METHODNAME + "{}", lErrStr);
                this.isSupportingDataConsistent = false;
                throw new InconsistentConfigurationException(lErrStr);
            }

            final List<Season> lSeasonsListForVG = this.vaccineGroupItemToSeasons.computeIfAbsent(lcvgi, _ -> new ArrayList<>());
            final Season lS;
            if (lDefaultSeason)
            {
                if (lDefaultStartMonthAndDayStr != null && lDefaultStopMonthAndDayStr != null)
                {
                    final MonthDay lStartMonthDay = getMonthDayObjectForSDMonthDayStr(lDefaultStartMonthAndDayStr);
                    final MonthDay lEndMonthDay = getMonthDayObjectForSDMonthDayStr(lDefaultStopMonthAndDayStr);
                    lS = new Season(lSeasonCode, lcvgi.getCdsItemName(), true, lStartMonthDay.getMonthValue(),
                            lStartMonthDay.getDayOfMonth(), lEndMonthDay.getMonthValue(), lEndMonthDay.getDayOfMonth());
                }
                else
                    if (lStartDateStr != null && lEndDateStr != null)
                    {
                        final LocalDate startDate = LocalDate.parse(lStartDateStr);
                        final LocalDate endDate = LocalDate.parse(lEndDateStr);
                        lS = new Season(lSeasonCode, lcvgi.getCdsItemName(), true, startDate.getMonthValue(),
                                startDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getDayOfMonth());
                    }
                    else
                    {
                        final String lErrStr = "Default season dates not specified for season: " + lSeasonCode;
                        log.warn(_METHODNAME + lErrStr);
                        this.isSupportingDataConsistent = false;
                        throw new InconsistentConfigurationException(lErrStr);
                    }
            }
            else
            {
                if (lStartDateStr == null || lEndDateStr == null)
                {
                    final String lErrStr = "Fully-specified season dates not specified for season: " + lSeasonCode;
                    log.warn(_METHODNAME + lErrStr);
                    this.isSupportingDataConsistent = false;
                    throw new InconsistentConfigurationException(lErrStr);
                }
                final LocalDate startDate = LocalDate.parse(lStartDateStr);
                final LocalDate endDate = LocalDate.parse(lEndDateStr);
                lS = new Season(lSeasonCode, lcvgi.getCdsItemName(), true, startDate.getMonthValue(), startDate.getDayOfMonth(),
                        startDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth(), endDate.getYear());
            }

            lSeasonsListForVG.add(lS);
            this.cdsListItemNameToSeasonItem.put(lSeasonCode, new LocallyCodedSeasonItem(lSeasonCode, lPrimaryOpenCdsConcept,
                    locallyCodedCdsSeasonListItem.getCdsListVersions(), lS));
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
