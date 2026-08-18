/**
 * Copyright (C) 2026 New York City Department of Health and Mental Hygiene, Bureau of Immunization
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
 * ANY CLAIM, DAMAGES, OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p>
 * For more information about this software, see http://www.hln.com/ice or send
 * correspondence to ice@hln.com.
 */

package org.cdsframework.ice.supportingdata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.cdsframework.cds.CdsConcept;
import org.cdsframework.cds.supportingdata.LocallyCodedCdsListItem;
import org.cdsframework.cds.supportingdata.SupportingData;
import org.cdsframework.ice.service.ICECoreError;
import org.cdsframework.ice.service.ScheduleAuthority;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SupportedScheduleAuthorities
 */
@Slf4j
@Getter
public class SupportedScheduleAuthorities implements SupportingData
{
    private final ICESupportingDataConfiguration iceSupportingDataConfiguration;
    private final Map<String, LocallyCodedScheduleAuthorityItem> cdsListItemNameToScheduleAuthorityItem;
    private final boolean isSupportingDataConsistent;

    /**
     * SupportedScheduleAuthorities constructor
     */
    public SupportedScheduleAuthorities(final ICESupportingDataConfiguration isdc)
    {
        final String _METHODNAME = "SupportedScheduleAuthorities(): ";

        if (isdc == null)
        {
            final String lErrStr = "ICESupportingDataConfiguration parameter not specified; cannot continue";
            log.error(_METHODNAME + lErrStr);
            throw new IllegalArgumentException(lErrStr);
        }

        this.iceSupportingDataConfiguration = isdc;
        this.cdsListItemNameToScheduleAuthorityItem = new HashMap<>();
        this.isSupportingDataConsistent = true;
    }

    @Override
    public boolean isEmpty()
    {
        return this.cdsListItemNameToScheduleAuthorityItem.isEmpty();
    }

    @Override
    public boolean isSupportingDataConsistent()
    {
        return true;
    }

    /**
     * Get the schedule authority item for the supplied name
     */
    public LocallyCodedScheduleAuthorityItem getScheduleAuthorityItem(final String pScheduleAuthorityItemName)
    {
        return this.cdsListItemNameToScheduleAuthorityItem.get(pScheduleAuthorityItemName);
    }

    public void initializeFromCdsLists()
    {
        final String _METHODNAME = "initializeFromCdsLists(): ";

        final Collection<LocallyCodedCdsListItem> lCdsListItems = this.iceSupportingDataConfiguration.getSupportedCdsLists()
                .getCdsListItemsAssociatedWithCdsListCode(ICEConceptType.SCHEDULE_AUTHORITY.getIceConceptTypeValue());

        if (lCdsListItems == null)
        {
            log.info(_METHODNAME + "No schedule authority items found in supporting data");
            return;
        }

        for (final LocallyCodedCdsListItem lCdsListItem : lCdsListItems)
        {
            final String lScheduleAuthorityCode = lCdsListItem.getCdsListItemName();

            final CdsConcept lCdsConcept = CdsConcept.builder()
                    .openCdsConceptCode(lScheduleAuthorityCode)
                    .displayName(lCdsListItem.getCdsListItemValue())
                    .build();

            final ScheduleAuthority lScheduleAuthority =
                    ScheduleAuthority.builder().code(lScheduleAuthorityCode).displayName(lCdsConcept.getDisplayName()).build();

            try
            {
                this.cdsListItemNameToScheduleAuthorityItem.put(lScheduleAuthorityCode,
                        new LocallyCodedScheduleAuthorityItem(lScheduleAuthorityCode, lCdsConcept, lScheduleAuthority));
            }
            catch (final Exception e)
            {
                final String lErrStr = "An error occurred processing schedule authority item: " + lScheduleAuthorityCode;
                log.error(_METHODNAME + lErrStr, e);
                throw new ICECoreError(lErrStr);
            }
        }
    }

    /**
     * Get the schedule authority for the supplied code
     */
    public Optional<ScheduleAuthority> getScheduleAuthority(final String pCode)
    {
        return Optional.ofNullable(this.cdsListItemNameToScheduleAuthorityItem.get(pCode))
                .map(LocallyCodedScheduleAuthorityItem::getScheduleAuthority);
    }

    @Override
    public String toString()
    {
        final StringBuilder lStr = new StringBuilder("SupportedScheduleAuthorities [\n");
        for (final LocallyCodedScheduleAuthorityItem lItem : this.cdsListItemNameToScheduleAuthorityItem.values())
        {
            lStr.append("\t").append(lItem).append("\n");
        }
        lStr.append("]");
        return lStr.toString();
    }
}
